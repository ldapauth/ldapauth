import request from '@/utils/request'

// 获取登录策略配置
export function policyLogin() {
  return request({
    url: '/policy/login',
    method: 'get',
  })
}

// 获取密码策略配置
export function policyPassword() {
  return request({
    url: '/policy/password',
    method: 'get'
  })
}

// 保存登录策略
export function savePolicyLogin(data) {
  return request({
    url: '/policy/save/login',
    method: 'post',
    data: data
  })
}

// 保存密码策略
export function savePolicyPassword(data) {
  return request({
    url: '/policy/save/password',
    method: 'post',
    data: data
  })
}
