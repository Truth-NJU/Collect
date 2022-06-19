import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'/'el-icon-x' the icon show in the sidebar
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },

  {
    path: '/register',
    component: () => import('@/views/register/index')
  },

  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/table',
    children: [{
      path: 'table',
      name: 'Table',
      component: () => import('@/views/TaskTable/index'),
      meta: { title: '任务广场', icon: 'el-icon-s-home' }
    }]
  },
  {
    path: '/myTask',
    component: Layout,
    children: [{
      path: 'index',
      name: 'myTask',
      component: () => import('@/views/dashboard/index'),
      meta: { title: '我的任务', icon: 'dashboard', '众包工人': true, '发包方': true }
    }]
  },

  {
    path: '/taskForm',
    component: Layout,
    children: [
      {
        path: 'index',
        name: 'TaskForm',
        component: () => import('@/views/TaskForm/index'),
        meta: { title: '发布任务', icon: 'form', '发包方': true }
      }
    ]
  },
  {
    path: '/reports',
    component: Layout,
    children: [
      {
        path: 'index',
        name: 'reports',
        component: () => import('@/views/reports/index'),
        meta: { title: '任务报告', icon: 'form', '发包方': true, '众包工人': true }
      }
    ]
  },
  {
    path: '/reportClassDetail',
    component: Layout,
    children: [
      {
        path: 'index',
        name: 'reportClassDetail',
        component: () => import('@/views/reportClassDetail/index'),
        meta: { title: '报告分类详情', icon: 'form', '发包方': true, '众包工人': false }
      }
    ]
  },
  {
    path: '/reportForm',
    component: Layout,
    children: [
      {
        path: 'index',
        name: 'Form',
        component: () => import('@/views/reportForm/index'),
        meta: { title: '报告编辑', icon: 'form', '众包工人': true }
      }
    ]
  },
  {

    path: '/admin',
    component: Layout,
    children: [{
      path: 'index',
      name: 'admin',
      component: () => import('@/views/admin/index'),
      meta: { title: '管理', icon: 'form', 管理员: true }

    }]
  },

  {
    path: '/reportDetail',
    component: Layout,
    children: [
      {
        path: 'index',
        name: 'reportDetail',
        component: () => import('@/views/reportDetail/index'),
        meta: { title: '报告详情页', icon: 'form', '发包方': true, '众包工人': true }
      }

    ]
  },

  {
    path: 'external-link',
    component: Layout,
    children: [
      {
        path: 'http://172.29.4.49/191250048_smtc',
        meta: { title: 'External Link', icon: 'link' }
      }
    ]
  },

  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
