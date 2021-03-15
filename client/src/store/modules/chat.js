import {getChat, setChat} from '@/utils/auth'

const state = {
  user_id: '',
  list: []
}

const mutations = {
  INIT: (state, id) => {
    state.user_id = id
    state.list = getChat(id) === null ? [] : getChat(id)
  },
  SET_LIST: (state, list) => {
    state.list = list
    setChat(list, state.user_id)
  },
  ADD_LIST: (state, item) => {
    state.list = state.list.filter(e => e.id !== item.id)
    state.list.push(item)
    setChat(state.list, state.user_id)
  },
  UPDATE_LIST: (state, item) => {
    state.list.map(i => i.id === item.id ? compare(item, i) : i)
    setChat(state.list, state.user_id)
  },
}

function compare(item, i) {
  i.avatar = item.avatar ? item.avatar : i.avatar
  i.nickname = item.nickname ? item.nickname : i.nickname
  i.time = item.time ? item.time : i.time
  i.msg = item.msg ? item.msg : i.msg
  i.badge = item.badge ? item.badge : i.badge
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
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

