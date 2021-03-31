import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/api-v1/oauth/token',
    method: 'post',
    data
  })
}

export function getInfo() {
  return request({
    url: '/api-v1/user/info',
    method: 'get'
  })
}

export function logout() {
  return request({
    url: '/api-v1/vue-admin-template/user/logout',
    method: 'post'
  })
}

export function registerUser(data) {
  return request({
    url: '/api-v1/user/register',
    method: 'post',
    data
  })
}

export function getAvatar(params) {
  return request({
    url: '/api-v1/user/getAvatar',
    method: 'get',
    params
  })
}

export function getUsersInfo(data) {
  return request({
    url: '/api-v1/user/select/details/list',
    method: 'post',
    data
  })
}

export function getDetails(params) {
  return request({
    url: '/api-v1/user/select/details',
    method: 'get',
    params
  })
}

export function getGroup() {
  return request({
    url: '/api-v1/friendGroup/getGroupList',
    method: 'get'
  })
}

export function getUser(params) {
  return request({
    url: '/api-v1/user/findUser',
    method: 'get',
    params
  })
}

export function updateFriend(data) {
  return request({
    url: '/api-v1/friendGroup/updateFriend',
    method: 'post',
    data
  })
}


export function addGroup(data) {
  return request({
    url: '/api-v1/friendGroup/addGroup',
    method: 'post',
    data
  })
}

export function addFriend(data) {
  return request({
    url: '/api-v1/friendGroup/addFriend',
    method: 'post',
    data
  })
}

export function deleteFriend(data) {
  return request({
    url: '/api-v1/friendGroup/deleteFriend',
    method: 'post',
    data
  })
}


export function getVerificationCode(data) {
  return request({
    url: '/api-v1/user/getCode',
    method: 'post',
    data
  })
}

export function retrievePassword(data) {
  return request({
    url: '/api-v1/user/retrievePassword',
    method: 'post',
    data
  })
}
