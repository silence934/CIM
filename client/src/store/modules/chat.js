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
  ADD_LIST: (state, item) => {
    //去除旧消息
    if (item.type === 'ADD_FRIEND') {
      state.list = state.list.filter(e => !(e.id === item.id && e.type === "ADD_FRIEND"))
    } else {
      state.list = state.list.filter(e => !(e.id === item.id && e.type !== "ADD_FRIEND"))
    }
    //添加新消息
    state.list.push(item)
    setChat(state.list, state.user_id)
  },
  UPDATE_LIST: (state, item) => {
    state.list.map(i => i.id === item.id && (
      item.type === i.type || ((item.type !== 'ADD_FRIEND') && i.type !== 'ADD_FRIEND')) ? compare(item, i) : i)
    setChat(state.list, state.user_id)
  },
  UPDATE_ONLINE_USER: (state, data) => {
    state.onlineUser = data
  }
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

