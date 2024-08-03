import request from '@/utils/request';

//分页查询
export function list(query) {
    return request({
      url: '/audit/applogin/list',
      method: 'get',
      params: query
    })
  }