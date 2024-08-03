import request from '@/utils/request'

// 查询角色列表
export function fetchAuth(query) {
  return request({
    url: '/groups/apps/fetch',
    method: 'get',
    params: query
  })
}


// 新增分组
export function authData(data) {
  return request({
    url: '/groups/apps/auth',
    method: 'post',
    data: data
  })
}
// 删除分组
export function delAuth(ids) {
  return request({
    url: '/groups/apps/delete/' + ids,
    method: 'delete'
  })
}
