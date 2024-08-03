import request from '@/utils/request'

// 查询详情
export function getSynchronizers(synchronizersId) {
  return request({
    url: '/synchronizers/get/' + synchronizersId,
    method: 'get'
  })
}


// 修改
export function updateSynchronizers(data) {
  return request({
    url: '/synchronizers/edit',
    method: 'put',
    data: data
  })
}

// 修改
export function testSynchronizers(data) {
  return request({
    url: '/synchronizers/test',
    method: 'post',
    data: data
  })
}

// 立即同步
export function syncSynchronizers(id) {
  return request({
    url: '/synchronizers/sync/'+id,
    method: 'post'
  })
}


// 查询日志
export function logsFetch(query) {
  return request({
    url: '/synchronizers/logs/fetch',
    method: 'get',
    params: query
  })
}
