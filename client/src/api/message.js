import request from '@/utils/request'

export function getUnreadMessage() {
  return request({
    url: '/api-v1/message/getUnreadMessage',
    method: 'get'
  })
}
