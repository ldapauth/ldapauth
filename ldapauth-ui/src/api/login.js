import request from '@/utils/request'

// 登录方法
export function login(username, password, captcha, uuid, state, sTempCode) {
    const data = {
        username,
        password,
        captcha,
        uuid,
        state,
        sTempCode
    }
    data.authType = "normal";
    return request({
        url: '/login/signin',
        headers: {
            isToken: false,
            repeatSubmit: false
        },
        method: 'post',
        data: data
    })
}

export function getThirdById(id) {
    return request({
        url: '/login/authorize/' + id,
        headers: {
            isToken: false,
            repeatSubmit: false
        },
        method: 'get'
    })
}

// 注册方法
export function register(data) {
    return request({
        url: '/register',
        headers: {
            isToken: false
        },
        method: 'post',
        data: data
    })
}

//忘记密码-发送短信验证码
export function sendSmsCode(query) {
    return request({
        url: '/users/sendCode',
        headers: {
            isToken: false
        },
        method: 'get',
        params: query
    })
}

export function validateCaptcha(query) {
    return request({
        url: `/password/validate`,
        method: 'get',
        params: query,
        headers: {
            isToken: false,
        }
    })
}

//获取所有的策略
export function getPwdPolicy() {
    return request({
        url: `/policy/password`,
        method: 'get',
        headers: {
            isToken: false,
        },
    })
}

//设置新密码
export function setNewPassword(data) {
    return request({
        url: `/users/setPwd`,
        method: 'put',
        data: data,
        headers: {
            isToken: false,
        }
    })
}

// 获取用户详细信息
export function currentUser() {
    return request({
        url: '/users/currentUser',
        method: 'get'
    })
}

// 退出方法
export function logout() {
    return request({
        url: '/logout',
        method: 'post'
    })
}

// 获取验证码
export function getCodeImg(state) {
    return request({
        url: '/captcha?state=' + state,
        headers: {
            isToken: false
        },
        method: 'get',
        timeout: 20000
    })
}

//
export function loginPreGet() {
    return request({
        url: '/login/get',
        method: 'get'
    })
}

// 方法
export function callbackLogin(data) {
    return request({
        url: '/login/callback',
        headers: {
            isToken: false,
            repeatSubmit: false
        },
        method: 'post',
        data: data
    })
}

// 退出方法
export function logoutApi() {
    return request({
        url: '/logout',
        method: 'post'
    })
}
