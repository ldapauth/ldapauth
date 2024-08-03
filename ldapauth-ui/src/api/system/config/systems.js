import request from '@/utils/request'

// 获取系统配置
export function systemInfo() {
  return request({
    url: '/systems/get',
    method: 'get',
  })
}

// 保存登录策略
export function saveSystemsInfo(data) {
  return request({
    url: '/systems/save',
    method: 'post',
    data: data
  })
}
