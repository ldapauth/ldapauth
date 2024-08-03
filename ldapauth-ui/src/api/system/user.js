import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 查询用户列表
export function listUser(query) {
  return request({
    url: '/users/fetch',
    method: 'get',
    params: query
  })
}

// 查询用户详细
export function getUser(userId) {
  return request({
    url: `/users/get/${userId}`,
    method: 'get'
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/users/add',
    method: 'post',
    data: data
  })
}

//上传头像
export function uploadImage(data) {
  return request({
    url: '/file/upload',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data',
    }
  })
}

// 个人资料头像修改
export function uploadAvatar(data) {
  return request({
    url: '/users/update/avatar',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

// 修改用户
export function updateUser(data) {
  return request({
    url: '/users/edit',
    method: 'put',
    data: data
  })
}

// 删除用户
export function delUser(data) {
  return request({
    url: '/users/delete',
    method: 'delete',
    data: data
  })
}

// 禁用用户
export function disableUser(data) {
  return request({
    url: '/users/disable',
    method: 'put',
    data: data
  })
}

// 启用用户
export function activeUser(data) {
  return request({
    url: '/users/active',
    method: 'put',
    data: data
  })
}



// 修改用户个人信息
export function updateUserProfile(data) {
  return request({
    url: '/users/update/profile',
    method: 'put',
    data: data
  })
}

// 修改用户密码
export function updateUserPwd(data) {
  return request({
    url: '/users/update/password',
    method: 'put',
    data: data
  })
}

//重置用户密码
export function resetSubUserPassword(id) {
  return request({
    url: `/users/resetPwd/${id}`,
    method: 'put'
  })
}
