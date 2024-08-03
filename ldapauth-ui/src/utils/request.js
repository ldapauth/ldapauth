import axios from 'axios'
import {ElLoading, ElMessage, ElMessageBox, ElNotification} from 'element-plus'
import {
    getRefreshToken,
    getToken,
    setToken,
    setRefreshToken,
    setUserInfo,
    setTokenInfo,
} from '@/utils/auth'
import errorCode from '@/utils/errorCode'
import {blobValidate, tansParams} from '@/utils/ruoyi'
import cache from '@/plugins/cache'
import {saveAs} from 'file-saver'
import useUserStore from '@/store/modules/user'

let downloadLoadingInstance;
// 是否显示重新登录
export let isRelogin = {show: false};
let isRefreshing = false; // 标志是否正在刷新token
let requests = []; // 存储待处理请求

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 创建axios实例
const service = axios.create({
    // axios中请求配置有baseURL选项，表示请求URL公共部分
    baseURL: import.meta.env.VITE_APP_BASE_API,
    // 超时
    timeout: 15000
})

const serviceRefresh = axios.create({
    // axios中请求配置有baseURL选项，表示请求URL公共部分
    baseURL: import.meta.env.VITE_APP_BASE_API,
    // 超时
    timeout: 15000
})

function showReLoginToast(){
    if (!isRelogin.show) {
        console.info('isRelogin', isRelogin)
        isRefreshing = false;
        isRelogin.show = true;
        ElMessageBox.confirm(
            '登录状态已过期，您可以继续留在该页面，或者重新登录',
            '系统提示',
            {confirmButtonText: '重新登录', cancelButtonText: '取消', type: 'warning'}
        )
            .then(() => {
                isRelogin.show = false;
                useUserStore()
                    .logOut()
                    .then(() => {
                        location.href = '/index';
                    });
            })
            .catch(() => {
                isRelogin.show = false;
            });
    }else {
        console.error('错误', isRelogin.show)
    }
}

// 刷新token
function refreshToken(token) {
    return new Promise((resolve, reject) => {
        serviceRefresh({
            url: '/token/refresh?refresh_token=' + token,
            headers: {
                isToken: false
            },
            method: 'post'
        }).then(res => {
            console.log("refresh", res)
            if (res.data.code === 200) {
                resolve(res.data)
            } else {
                showReLoginToast()
            }
        }).catch(err => {
            showReLoginToast()
        })
    })

}

// request拦截器
service.interceptors.request.use(config => {
    // 是否需要设置 token
    const isToken = (config.headers || {}).isToken === false
    // 是否需要防止数据重复提交
    const isRepeatSubmit = (config.headers || {}).repeatSubmit === false
    if (getToken() && !isToken) {
        config.headers['Authorization'] = 'Bearer ' + getToken() // 让每个请求携带自定义token 请根据实际情况自行修改
    }
    // get请求映射params参数
    if (config.method === 'get' && config.params) {
        let url = config.url + '?' + tansParams(config.params);
        url = url.slice(0, -1);
        config.params = {};
        config.url = url;
    }
    if (!isRepeatSubmit && (config.method === 'post' || config.method === 'put')) {
        const requestObj = {
            url: config.url,
            data: typeof config.data === 'object' ? JSON.stringify(config.data) : config.data,
            time: new Date().getTime()
        }
        const requestSize = Object.keys(JSON.stringify(requestObj)).length; // 请求数据大小
        const limitSize = 5 * 1024 * 1024; // 限制存放数据5M
        if (requestSize >= limitSize) {
            console.warn(`[${config.url}]: ` + '请求数据大小超出允许的5M限制，无法进行防重复提交验证。')
            return config;
        }
        const sessionObj = cache.session.getJSON('sessionObj')
        if (sessionObj === undefined || sessionObj === null || sessionObj === '') {
            cache.session.setJSON('sessionObj', requestObj)
        } else {
            const s_url = sessionObj.url;                // 请求地址
            const s_data = sessionObj.data;              // 请求数据
            const s_time = sessionObj.time;              // 请求时间
            const interval = 1000;                       // 间隔时间(ms)，小于此时间视为重复提交
            if (s_data === requestObj.data && requestObj.time - s_time < interval && s_url === requestObj.url) {
                const message = '数据正在处理，请勿重复提交';
                console.warn(`[${s_url}]: ` + message)
                return Promise.reject(new Error(message))
            } else {
                cache.session.setJSON('sessionObj', requestObj)
            }
        }
    }
    return config
}, error => {
    console.error(error)
    Promise.reject(error)
})

// 响应拦截器
service.interceptors.response.use(res => {
        // 未设置状态码则默认成功状态
        const code = res.data.code || 200;
        // 获取错误信息
        const msg = errorCode[code] || res.data.message || errorCode['default']
        // 二进制数据则直接返回
        if (res.request.responseType === 'blob' || res.request.responseType === 'arraybuffer') {
            return res.data
        }
        if (code === 401) {
            return err401(res)
        } else if (code === 500) {
            ElMessage({message: msg, type: 'error'})
            return Promise.reject(new Error(msg))
        } else if (code === 601) {
            ElMessage({message: msg, type: 'warning'})
            return Promise.reject(new Error(msg))
        } else if (code === 400) {
            ElNotification.error({title: msg})
            return Promise.reject('error')
        } else {
            return Promise.resolve(res.data)
        }
    },
    (error) => {
        let {message, response} = error;
        console.error(error)
        if (response.status === 401) {
            return err401(response)
        }

        if (message === "Network Error") {
            message = "后端接口连接异常";
        } else if (message.includes("timeout")) {
            message = "系统接口请求超时";
        } else if (message.includes("Request failed with status code")) {
            message = "系统接口" + message.substring(message.length - 3) + "异常";
        }

        ElMessage({message: message, type: 'error', duration: 5 * 1000})
        return Promise.reject(error)
    }
)

function err401(res) {
    if (!isRefreshing) {
        isRefreshing = true;
        try {
            const refreshTokenStr = getRefreshToken();
            if (!refreshTokenStr) {
                const r = 1 / 0
            }
            refreshToken(refreshTokenStr).then(refreshRes => {
                setToken(refreshRes.data.token)
                setRefreshToken(refreshRes.data.refresh_token)
                setTokenInfo(refreshRes.data)
                useUserStore().token = refreshRes.data.token
                isRefreshing = false;
                requests.forEach(cb => cb(refreshRes.data.token));
                requests = [];
            })
        } catch (err) {
            console.error(err)

            if (!isRelogin.show) {
                showReLoginToast()
            } else {
                Promise.reject('登录失效，请重新登录')
            }
        }
    }
    return new Promise(resolve => {
        requests.push(token => {
            res.config.headers['Authorization'] = 'Bearer ' + token;
            resolve(service(res.config));
        });
    });
}

// 通用下载方法
export function download(url, params, filename, config) {
    downloadLoadingInstance = ElLoading.service({text: "正在下载数据，请稍候", background: "rgba(0, 0, 0, 0.7)",})
    return service.post(url, params, {
        transformRequest: [(params) => {
            return tansParams(params)
        }],
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        responseType: 'blob',
        ...config
    }).then(async (data) => {
        const isBlob = blobValidate(data);
        if (isBlob) {
            const blob = new Blob([data])
            saveAs(blob, filename)
        } else {
            const resText = await data.text();
            const rspObj = JSON.parse(resText);
            const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode['default']
            ElMessage.error(errMsg);
        }
        downloadLoadingInstance.close();
    }).catch((r) => {
        console.error(r)
        ElMessage.error('下载文件出现错误，请联系管理员！')
        downloadLoadingInstance.close();
    })
}

export default service
