import {getChat, setChat} from '@/utils/auth'

const state = {
  user_id: '',
  list: [],
  onlineUser: []
}

const mutations = {
    INIT: (state, id) => {
      state.user_id = id
      state.list = getChat(id) || []
    },
    SET_LIST: (state, list) => {
      state.list = list
      setChat(list, state.user_id)
    },
    DELETE_LIST:(state, item)=>{
        state.list = state.list.filter(e => !(e.id === item.id))
        setChat(state.list, state.user_id)
    },
    ADD_LIST: (state, item) => {
      //去除旧消息
      state.list = state.list.filter(e => !(e.id === item.id))
      //添加新消息
      state.list.unshift(item)
      setChat(state.list, state.user_id)
    },
    UPDATE_LIST: (state, item) => {
      state.list.map(i => i.id === item.id ? compare(item, i) : i)
      setChat(state.list, state.user_id)
    },
    UPDATE_ONLINE_USER: (state, data) => {
      state.onlineUser = data
    },
}

function compare(item, i) {

  i.avatar = item.avatar || i.avatar
  i.nickname = item.nickname || i.nickname
  i.time = item.time || i.time
  i.msg = item.msg || i.msg
  i.badge = item.badge || i.badge
  return i
}

const actions = {

  init({commit}, userId) {
    commit('INIT', userId)
  },

  addList({commit}, item) {
    commit('ADD_LIST', item)
  },

  updateList({commit}, item) {
    commit('UPDATE_LIST', item)
  },

  deleteList({commit}, item) {
    commit('DELETE_LIST', item)
  },

  updateOnlineUser({commit}, data) {
    commit('UPDATE_ONLINE_USER', data)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

