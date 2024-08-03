import request from '@/utils/request'

// 查询角色列表
export function listProviders(query) {
    return request({
        url: '/provider/list',
        method: 'get',
        params: query
    })
}

export function getProvider(id) {
    return request({
        url: `/provider/get/${id}`,
        method: 'get'
    })
}

export function addProvider(data) {
    return request({
        url: `/provider/add`,
        method: 'post',
        data: data
    })
}

export function editProvider(data) {
    return request({
        url: `/provider/edit`,
        method: 'put',
        data: data
    })
}

export function deleteBatch(data) {
    return request({
        url: `/provider/delete`,
        method: 'delete',
        data: data
    })
}

export function disableProvider(id) {
    return request({
        url: `/provider/disable/${id}`,
        method: 'put',
    })
}

export function activeProvider(id) {
    return request({
        url: `/provider/active/${id}`,
        method: 'put',
    })
}
