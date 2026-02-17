import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue')
  },
  {
    path: '/courses',
    name: 'Courses',
    component: () => import('@/views/course/CourseList.vue')
  },
  {
    path: '/course/:id',
    name: 'CourseDetail',
    component: () => import('@/views/course/CourseDetail.vue')
  },
  {
    path: '/course-detail/:id',
    name: 'CourseDetailPage',
    component: () => import('@/views/course/CourseDetailPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/study',
    name: 'StudyCenter',
    component: () => import('@/views/study/StudyCenter.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/user/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/my-courses',
    name: 'MyCourses',
    component: () => import('@/views/user/MyCourses.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/study-records',
    name: 'StudyRecords',
    component: () => import('@/views/user/StudyRecords.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/change-password',
    name: 'ChangePassword',
    component: () => import('@/views/user/ChangePassword.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/notes',
    name: 'Notes',
    component: () => import('@/views/user/Notes.vue'),
    meta: { requiresAuth: true }
  },
  // 管理员路由 - 修复双层侧边栏问题
  {
    path: '/admin',
    name: 'Admin',
    redirect: '/admin/users',
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('@/views/admin/UserManagement.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'courses',
        name: 'CourseManagement',
        component: () => import('@/views/admin/CourseManagement.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
      }
    ]
  },
  // 权限演示页面
  {
    path: '/permission-demo',
    name: 'PermissionDemo',
    component: () => import('@/views/PermissionDemo.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('role') || 'USER'
  
  // 已登录用户访问登录页时，自动跳转到首页
  if (to.path === '/login' && token) {
    next('/')
    return
  }
  
  // 检查是否需要认证
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }
  
  // 对 /admin/** 路径做权限校验
  if (to.path.startsWith('/admin')) {
    // 检查用户角色是否为ADMIN
    if (userRole !== 'ADMIN') {
      ElMessage.error('无管理员权限')
      next('/login')
      return
    }
  }
  
  // 检查路由元信息中的管理员权限要求
  if (to.meta.requiresAdmin && userRole !== 'ADMIN') {
    ElMessage.error('无管理员权限')
    next('/login')
    return
  }
  
  next()
})

export default router