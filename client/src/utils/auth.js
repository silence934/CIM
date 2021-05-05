const TokenKey = 'cim_token'
const refreshTokenKey = 'cim_refresh_token'
const ChatKey = 'cim_chat_'
const User = 'cim_user'

export function getToken() {
  return JSON.parse(localStorage.getItem(TokenKey))
}

export function setToken(token) {
  return localStorage.setItem(TokenKey, JSON.stringify(token))
}

export function removeToken() {
  return localStorage.removeItem(TokenKey)
}

export function getRefreshToken() {
  return JSON.parse(localStorage.getItem(refreshTokenKey))
}

export function setRefreshToken(refreshToken) {
  return localStorage.setItem(refreshTokenKey, JSON.stringify(refreshToken))
}

export function removeRefreshToken() {
  return localStorage.removeItem(refreshTokenKey)
}

export function getChat(id) {
  return JSON.parse(localStorage.getItem(ChatKey + id))
}

export function setChat(list, id) {
  return localStorage.setItem(ChatKey + id, JSON.stringify(list))
}

export function removeChat(id) {
  return localStorage.removeItem(ChatKey + id)
}

export function getUser() {
  return JSON.parse(localStorage.getItem(User))
}

export function setUser(user) {
  return localStorage.setItem(User, JSON.stringify(user))
}

export function removeUser() {
  return localStorage.removeItem(User)
}
