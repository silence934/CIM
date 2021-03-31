import request from '@/utils/request'

export function getCrowd() {
  return request({
    url: '/api-v1/crowd/getCrowdList',
    method: 'get'
  })
}

export function getCrowdByName(params) {
  return request({
    url: '/api-v1/crowd/getCrowd',
    method: 'get',
    params
  })
}

export function addCrowd(data) {
  return request({
    url: '/api-v1/crowd/addCrowd',
    method: 'post',
    data
  })
}

export function joinCrowd(data) {
  return request({
    url: '/api-v1/crowd/jsonCrowd',
    method: 'post',
    data
  })
}

export function updateCrowd(data) {
  return request({
    url: '/api-v1/crowd/updateCrowd',
    method: 'post',
    data
  })
}

export function quitCrowd(data) {
  return request({
    url: '/api-v1/crowd/quitCrowd',
    method: 'post',
    data
  })
}

export function getCrowdInfo(params) {
  return request({
    url: '/api-v1/crowd/getCrowdInfo',
    method: 'get',
    params
  })
}
