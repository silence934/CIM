const getters = {
  visitedViews: state => state.tagsView.visitedViews,
  cachedViews: state => state.tagsView.cachedViews,
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  name: state => state.user.name,
  userId: state => state.user.user_id,
  chat: state => state.chat.list,
  departmentId: state => state.user.departmentId,
  user: state => state.user.user,
  roles: state => state.user.roles,
  onlineUser: state => state.chat.onlineUser
}
export default getters
