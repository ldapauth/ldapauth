import {login, logout, currentUser} from '@/api/login'
import {
    getToken,
    setToken,
    removeToken,
    setRefreshToken,
    setUserInfo,
    setTokenInfo,
    removeUserInfo,
    removeRefreshToken,
    removeTokenInfo
} from '@/utils/auth'
import defAva from '@/assets/images/profile.jpg'

const useUserStore = defineStore(
    'user',
    {
        state: () => ({
            token: getToken(),
            id: '',
            name: '',
            avatar: '',
            roles: [],
            permissions: []
        }),
        actions: {
            // 登录
            login(loginCredential) {
                const username = loginCredential.username.trim()
                const password = loginCredential.password
                const captcha = loginCredential.captcha
                const uuid = loginCredential.uuid
                const state = loginCredential.state
                const sTempCode = loginCredential.sTempCode
                return new Promise((resolve, reject) => {
                    login(username, password, captcha, uuid, state,sTempCode).then(res => {
                        setToken(res.data.token)
                        setRefreshToken(res.data.refresh_token)
                        setTokenInfo(res.data)
                        // this.roles = res.data.authorities
                        this.token = res.data.token
                        resolve()
                    }).catch(error => {
                        reject(error)
                    })
                })
            },
            setCredential(loginCredential) {
                setToken(loginCredential.token)
                setRefreshToken(loginCredential.refresh_token)
                setTokenInfo(loginCredential.data)
                // this.roles = res.data.authorities
                this.token = loginCredential.token
            },
            // 获取用户信息
            currentUser() {
                return new Promise((resolve, reject) => {
                    currentUser().then(res => {
                        const user = res.data
                        const avatar = (user.avatar == "" || user.avatar == null) ? defAva : import.meta.env.VITE_APP_BASE_API + user.avatar;
                        // const avatar = (user.avatar == "" || user.avatar == null) ? defAva : import.meta.env.VITE_APP_BASE_API + user.avatar;
                        this.roles = ['ROLE_DEFAULT']
                        this.id = user.id
                        this.name = user.displayName
                        this.avatar = avatar
                        setUserInfo(user)
                        resolve(res)
                    }).catch(error => {
                        reject(error)
                    })
                })
            },
            // 退出系统
            logOut() {
                return new Promise((resolve, reject) => {
                    this.token = ''
                    this.roles = []
                    this.permissions = []
                    removeToken()
                    removeUserInfo()
                    removeRefreshToken()
                    removeTokenInfo()
                    resolve()
                })
            }
        }
    })

export default useUserStore
