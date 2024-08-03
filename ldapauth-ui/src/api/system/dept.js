import request from '@/utils/request'

// 查询部门列表
export function listDept(query) {
  return request({
    url: '/organizations/fetch',
    method: 'get',
    params: query
  })
}

// 查询部门详细
export function getDept(deptId) {
  return request({
    url: `/organizations/get/${deptId}`,
    method: 'get'
  })
}

// 新增部门
export function addDept(data) {
  return request({
    url: '/organizations/add',
    method: 'post',
    data: data
  })
}

// 修改部门
export function updateDept(data) {
  return request({
    url: '/organizations/edit',
    method: 'put',
    data: data
  })
}


//获取组织部门树
export function getTree(query) {
  return request({
    url: '/organizations/tree',
    method: 'get',
    params: query
  })
}

//禁用组织机构
export function disableBatch(data) {
  return request({
    url: '/organizations/disable',
    method: 'put',
    data: data
  })
}

//启用组织机构
export function activeBatch(data) {
  return request({
    url: '/organizations/active',
    method: 'put',
    data: data
  })
}

//删除组织机构
export function deleteBatch(data) {
  return request({
    url: '/organizations/delete',
    method: 'delete',
    data: data
  })
}
