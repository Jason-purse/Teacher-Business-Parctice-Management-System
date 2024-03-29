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
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },

  {
    path: '/',
    component: Layout,
    meta: { title: '教师企业实践管理', icon: 'el-icon-menu' },
    redirect: '/project',
    children: [
      {
        path: 'project',
        name: 'project',
        component: () => import('@/views/project/index'),
        meta: { title: '项目管理', icon: 'el-icon-folder-opened', roles: ['admin', 'teacher', 'auditor', 'leader', 'enterprise_principal'] }
      },
      {
        path: 'attachment',
        name: 'attachment',
        component: () => import('@/views/attachment/index'),
        meta: { title: '附件管理', icon: 'el-icon-document', roles: ['admin'] }
      },
      {
        path: 'attendance',
        name: 'attendance',
        component: () => import('@/views/attendance/index'),
        meta: { title: '考勤管理', icon: 'el-icon-tickets', roles: ['admin', 'teacher', 'auditor', 'leader', 'enterprise_principal'] }
      },
      {
        path: 'audit',
        name: 'audit',
        component: () => import('@/views/audit/index'),
        meta: { title: '审核管理', icon: 'el-icon-edit-outline', roles: ['admin', 'teacher', 'auditor', 'leader', 'enterprise_principal'] }
      },
      {
        path: 'dict',
        name: 'dict',
        component: () => import('@/views/dict/index'),
        meta: { title: '字典管理', icon: 'el-icon-takeaway-box\n', roles: ['admin'] }
      },
      {
        path: 'user',
        name: 'user',
        component: () => import('@/views/user/index'),
        meta: { title: '用户管理', icon: 'el-icon-user', roles: ['admin'] }
      },
      {
        path: 'nothing',
        name: 'nothing',
        component: () => import('@/views/nothing/index'),
        meta: { title: 'nothing', icon: 'el-icon-user', roles: [], hidden: true }
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
