import axios from 'axios'
import {Message, MessageBox} from 'element-ui'
import store from '@/store'
import {getToken} from '@/utils/auth'

const service = axios.create({
  baseURL: '/proxy',
  timeout: 5000
})

service.interceptors.request.use(
  config => {
    if (store.getters.token) {
      config.headers['Authorization'] = 'Bearer ' + getToken()
    }
    return config
  },
  error => {
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const data = response.data
    if (response.headers['content-type'] === 'application/octet-stream') {
      return response
    }
    if (data.code !== '00000') {
      Message({
        message: data.msg || '网络异常',
        type: 'error',
        duration: 5 * 1000
      })

      if (data.code === 50012 || data.code === 50014) {
        MessageBox.confirm('您已经登出，您可以取消以停留在此页面，或再次登录', 'Confirm logout', {
          confirmButtonText: 'Re-Login',
          cancelButtonText: 'Cancel',
          type: 'warning'
        }).then(() => {
          store.dispatch('user/resetToken').then(() => {
            location.reload()
          })
        })
      }
      return Promise.reject(new Error(data.message || 'Error'))
    } else {
      if (data.data && data.data.token) {
        store.dispatch('user/setToken', data.data.token)
      }
      return data
    }
  },
  error => {
    console.log('err' + error)
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
