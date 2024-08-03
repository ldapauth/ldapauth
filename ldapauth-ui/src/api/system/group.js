import request from '@/utils/request'

// 查询角色列表
export function listGroup(query) {
  return request({
    url: '/groups/fetch',
    method: 'get',
    params: query
  })
}

// 查询分组详细
export function getGroup(groupId) {
  return request({
    url: '/groups/get/' + groupId,
    method: 'get'
  })
}

// 全部分组
export function allGroup() {
  return request({
    url: '/groups/all',
    method: 'get'
  })
}


// 新增分组
export function addGroup(data) {
  return request({
    url: '/groups/add',
    method: 'post',
    data: data
  })
}

// 修改分组
export function updateGroup(data) {
  return request({
    url: '/groups/edit',
    method: 'put',
    data: data
  })
}

// 删除分组
export function delGroup(ids) {
  return request({
    url: '/groups/delete/' + ids,
    method: 'delete'
  })
}

// 查询已授权的成员列表
export function listGroupMemberAuth(query) {
  return request({
    url: '/groups/member/fetchAuthList',
    method: 'get',
    params: query
  })
}

// 添加成员
export function addMember(data) {
  return request({
    url: '/groups/member/addMember',
    method: 'post',
    data: data
  })
}

// 移除成员
export function removeMember(data) {
  return request({
    url: '/groups/member/removeMember',
    method: 'post',
    data: data
  })
}


// 查询未授权的成员列表
export function listGroupMemberNotAuth(query) {
  return request({
    url: '/groups/member/fetchNotAuthList',
    method: 'get',
    params: query
  })
}



// 授权组资源
export function authResource(data) {
  return request({
    url: '/groups/resource/authResource',
    method: 'post',
    data: data
  })
}
// 授权组资源
export function getAuthResourceIds(groupId) {
  return request({
    url: '/groups/resource/getAuthResourceIds/'+groupId,
    method: 'get'
  })
}

