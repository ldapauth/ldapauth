import request from '@/utils/request';
/* import apiConfig from '../../apiConfig'; */


export function myApps() {
  return request({
    url: '/apps/myApps',
    method: 'get'
  })
}

//分页查询
export function list(query) {
  return request({
    url: '/apps/list',
    method: 'get',
    params: query
  })
}

// 查询全部应用
export function allApps() {
  return request({
    url: '/apps/all',
    method: 'get'
  })
}


//新增oidc
export function oidcCreate(data) {
  return request({
    url: `/apps/oidc/create`,
    method: 'post',
    data: data
  })
}

//修改oidc
export function oidcUpdate(data) {
  return request({
    url: `/apps/oidc/update`,
    method: 'put',
    data: data
  })
}

//新增SAML
export function samlCreate(data) {
  return request({
    url: `/apps/saml/create`,
    method: 'post',
    data: data
  })
}

//修改SAML
export function samlUpdate(data) {
  return request({
    url: `/apps/saml/update`,
    method: 'put',
    data: data
  })
}

//新增jwt
export function jwtCreate(data) {
  return request({
    url: `/apps/jwt/create`,
    method: 'post',
    data: data
  })
}

//修改jwt
export function jwtUpdate(data) {
  return request({
    url: `/apps/jwt/update`,
    method: 'put',
    data: data
  })
}

//新增cas
export function casCreate(data) {
  return request({
    url: `/apps/cas/create`,
    method: 'post',
    data: data
  })
}

//修改cas
export function casUpdate(data) {
  return request({
    url: `/apps/cas/update`,
    method: 'put',
    data: data
  })
}

//ID查询IDP扩展信息
export function getAppdetails(id) {
  return request({
    url: `/apps/appdetails/${id}`,
    method: 'get'
  })
}

//启用
export function active(ids) {
  return request({
    url: `/apps/active/${ids}`,
    method: 'put'
  })
}

//禁用
export function disable(ids) {
  return request({
    url: `/apps/disable/${ids}`,
    method: 'put'
  })
}

//删除
export function deleteBatch(ids) {
  return request({
    url: `/apps/delete/${ids}`,
    method: 'delete'
  })
}

//文件解析元数据
export function fileTransform(data) {
  return request({
    url: `/idp/saml20/metadata/fileTransform`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data',
    }
  })
}
