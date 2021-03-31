import request from '@/utils/request'

export function upload(data) {
  return request({
    url: '/api-v1/artifact/upload',
    method: 'post',
    data
  })
}
