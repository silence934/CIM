import Vue from 'vue'
import Router from 'vue-router'
/* Layout */
import Layout from '@/layout'

Vue.use(Router)

export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },

  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },

  {
    path: '/',
    component: Layout,
    redirect: '/message',
    children: [{
      path: 'message',
      name: 'message',
      component: () => import('@/views/message/index'),
      meta: {title: 'message', icon: 'chat'}
    }]
  },

  {
    path: '/friend',
    component: Layout,
    redirect: '/friend/table',
    name: '/friend',
    meta: {title: 'Example'},
    children: [
      {
        path: '/friend',
        component: () => import('@/views/friend/index'),
        meta: {title: 'friend', icon: 'friend'}
      }
    ]
  },

  {
    path: '/group',
    component: Layout,
    children: [
      {
        path: 'group',
        name: 'group',
        component: () => import('@/views/crowd/index'),
        meta: {title: 'group', icon: 'group'}
      }
    ]
  },
  {
    path: '/other',
    component: Layout,
    children: [
      {
        path: 'other',
        name: 'other',
        component: () => import('@/views/other/index'),
        meta: {title: 'other', icon: 'box'}
      }
    ]
  },
  {
    path: '/logout',
    component: () => import('@/views/nested/menu2/index'),
    name: 'Nested',
    meta: {
      title: 'logout',
      icon: 'logout'
    },
  },

  // 404 page must be placed at the end !!!
  {path: '*', redirect: '/404', hidden: true}
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({y: 0}),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
