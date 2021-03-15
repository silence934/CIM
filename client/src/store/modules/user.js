import { getInfo, login } from '@/api/user'
import { getToken, getUser, removeToken, removeUser, setToken, setUser } from '@/utils/auth'

const state = {
  token: getToken(),
  name: '',
  avatar: '',
  user_id: '',
  roles: [],
  departmentId: '',
  user: getUser()
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
    setToken(token)
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_ROLE: (state, roles) => {
    if (roles) {
      state.roles = roles.map(i => i.name)
    }
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  SET_USER_ID: (state, id) => {
    state.user_id = id
  },
  SET_DEPARTMENT_ID: (state, departmentId) => {
    state.departmentId = departmentId
  },
  SET_USER: (state, user) => {
    state.user = user
    setUser(user)
  },
  REMOVE_USER: (state) => {
    state.user = {}
    removeUser()
  }
}

const actions = {
  login({ commit }, userInfo) {
    const { username, password, isAdmin } = userInfo
    return new Promise((resolve, reject) => {
      let data = new FormData
      data.append('username', username.trim())
      data.append('password', password)
      data.append('grant_type', 'password')
      data.append('client_id', 'client')
      data.append('client_secret', '123456')
      login(data).then(() => {
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      getInfo().then(response => {
        const { data } = response
        if (!data) {
          reject('验证失败，请重新登录')
        }
        const { nickname, roles, avatar, id, departmentId } = data
        commit('SET_NAME', nickname)
        commit('SET_ROLE', roles)
        commit('SET_AVATAR', avatar)
        commit('SET_USER_ID', id)
        commit('SET_DEPARTMENT_ID', departmentId)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  logout({ commit }) {
    return new Promise((resolve, reject) => {
      commit('SET_TOKEN', '')
      commit('SET_NAME', '')
      commit('SET_ROLE', '')
      removeToken()
      //  resetRouter()
      resolve()
    })
  },

  resetToken({ commit }) {
    return new Promise(resolve => {
      commit('SET_TOKEN', '')
      removeToken()
      resolve()
    })
  },

  setToken({ commit }, token) {
    commit('SET_TOKEN', token)
  },

  setUser({ commit }, user) {
    commit('SET_USER', user)
  },

  removeUser({ commit }) {
    commit('REMOVE_USER')
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

