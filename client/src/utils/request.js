import axios from 'axios'
import {Message, MessageBox} from 'element-ui'
import store from '@/store'
import {getToken} from '@/utils/auth'

const service = axios.create({
  baseURL: '/proxy',
  timeout: 5000
})

let refreshing = false,// 正在刷新标识，避免重复刷新
  waitQueue = [] // 请求等待队列

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

service.interceptors.response.use(response => {
    console.log(response)
    const data = response.data
    if (response.headers['content-type'] === 'application/octet-stream') {
      return response
    }
    if (response.status !== 200||data.code !== '00000') {
      if (data.code === 'A0230') {
        const config = response.config
        if (!refreshing) {
          console.log("token过期 尝试刷新token")
          refreshing = true
          return store.dispatch('user/refreshToken').then((token) => {
            console.log("刷新token成功")
            config.headers['Authorization'] = 'Bearer ' + token
            config.baseURL = ''
            waitQueue.forEach(callback => callback(token))
            waitQueue = []
            return service(config)
          }).catch(() => { // refresh_token也过期，直接跳转登录页面重新登录
            MessageBox.confirm('当前页面已失效，请重新登录', '确认退出', {
              confirmButtonText: '重新登录',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              store.dispatch('user/resetToken').then(() => {
                location.reload()
              })
            })
          }).finally(() => {
            refreshing = false
          })
        } else {
          // 正在刷新token，返回未执行resolve的Promise,刷新token执行回调
          return new Promise((resolve => {
            waitQueue.push((token) => {
              config.headers['Authorization'] = 'Bearer ' + token
              config.baseURL = '' // 请求重试时，url已包含baseURL
              resolve(service(config))
            })
          }))
        }

      }
      return Promise.reject(new Error(data.message || 'Error'))
    } else {
      return data
    }
  }, error => {
    return Promise.reject(error)
  }
)

export default service
