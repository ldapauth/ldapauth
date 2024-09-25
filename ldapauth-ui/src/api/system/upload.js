import request from '@/utils/request'

//上传头像
export function upload(data) {
  return request({
    url: '/file/upload',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data',
    }
  })
}

//上传文件
export function uploadFile(data) {
  return request({
    url: '/file/uploadFile',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data',
    }
  })
}
