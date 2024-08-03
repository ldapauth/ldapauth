import request from '@/utils/request';

//分页查询
export function list(query) {
    return request({
      url: '/audit/login/list',
      method: 'get',
      params: query
    })
  }