<script setup lang="ts">
import { ref, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'
import {
  HomeFilled,
  VideoPlay,
  DataAnalysis,
  User,
  Management,
  Setting,
  SwitchButton
} from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()

// 侧边栏展开状态
const isCollapse = ref(false)

// 当前激活的菜单项
const activeMenu = computed(() => {
  const routePath = router.currentRoute.value.path
  if (routePath === '/') return 'home'
  if (routePath === '/my-courses') return 'my-courses'
  if (routePath === '/study') return 'study-center'
  if (routePath === '/admin/users') return 'admin-users'
  if (routePath === '/admin/courses') return 'admin-courses'
  return 'home'
})

// 菜单项配置
const menuItems = computed(() => {
  const baseMenus = [
    {
      index: 'home',
      title: '首页',
      icon: HomeFilled,
      path: '/',
      roles: ['USER', 'ADMIN'] // 所有用户可见
    },
    {
      index: 'my-courses',
      title: '我的课程',
      icon: VideoPlay,
      path: '/my-courses',
      roles: ['USER', 'ADMIN'] // 所有用户可见
    },
    {
      index: 'study-center',
      title: '学习进度',
      icon: DataAnalysis,
      path: '/study',
      roles: ['USER', 'ADMIN'] // 所有用户可见
    }
  ]

  // 管理员菜单项
  const adminMenus = [
    {
      index: 'admin-users',
      title: '用户管理',
      icon: User,
      path: '/admin/users',
      roles: ['ADMIN'] // 仅管理员可见
    },
    {
      index: 'admin-courses',
      title: '课程管理',
      icon: Management,
      path: '/admin/courses',
      roles: ['ADMIN'] // 仅管理员可见
    }
  ]

  // 根据用户角色过滤菜单项
  const userRole = userStore.currentUser?.role || localStorage.getItem('role') || 'USER'
  const filteredBaseMenus = baseMenus.filter(menu => menu.roles.includes(userRole))
  
  if (userRole === 'ADMIN') {
    return [...filteredBaseMenus, ...adminMenus]
  }
  
  return filteredBaseMenus
})

// 菜单点击处理
const handleMenuSelect = (index: string) => {
  const menuItem = menuItems.value.find(item => item.index === index)
  if (menuItem) {
    router.push(menuItem.path)
  }
}

// 退出登录
const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

// 切换侧边栏折叠状态
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}
</script>

<template>
  <div class="main-layout">
    <!-- 侧边栏 -->
    <el-aside 
      :width="isCollapse ? '64px' : '200px'" 
      class="sidebar"
    >
      <div class="sidebar-header">
        <transition name="logo" mode="out-in">
          <div v-if="!isCollapse" key="full" class="logo-full">
            <h2>学习平台</h2>
          </div>
          <div v-else key="mini" class="logo-mini">
            <h3>L</h3>
          </div>
        </transition>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        :collapse="isCollapse"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        @select="handleMenuSelect"
      >
        <el-menu-item 
          v-for="item in menuItems" 
          :key="item.index"
          :index="item.index"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.title }}</template>
        </el-menu-item>
      </el-menu>
      
      <div class="sidebar-footer">
        <el-button 
          type="primary" 
          link 
          @click="toggleCollapse"
          class="collapse-btn"
        >
          <el-icon>
            <Setting />
          </el-icon>
        </el-button>
      </div>
    </el-aside>

    <!-- 主内容区域 -->
    <div class="main-container">
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ activeMenu }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown>
            <span class="user-dropdown">
              <el-avatar :icon="User" size="small" />
              <span class="username">{{ userStore.currentUser?.nickname || userStore.currentUser?.username }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/profile')">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/change-password')">
                  <el-icon><Setting /></el-icon>修改密码
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区域 -->
      <el-main class="main-content">
        <!-- 方案a: 强制重新渲染组件 -->
        <router-view :key="$route.fullPath"></router-view>
      </el-main>
    </div>
  </div>
</template>

<style scoped>
.main-layout {
  display: flex;
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
}

.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2c3e50;
  border-bottom: 1px solid #4a5f7a;
}

.logo-full h2 {
  color: white;
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.logo-mini h3 {
  color: white;
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
}

.sidebar-footer {
  padding: 10px;
  border-top: 1px solid #4a5f7a;
  text-align: center;
}

.collapse-btn {
  color: #bfcbd9;
}

.collapse-btn:hover {
  color: #409eff;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  background-color: white;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-dropdown:hover {
  background-color: #f5f7fa;
}

.username {
  margin: 0 8px;
  color: #606266;
  font-size: 14px;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f0f2f5;
}

/* 动画效果 */
.logo-enter-active,
.logo-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.logo-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.logo-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 1000;
  }
  
  .main-container {
    margin-left: 0;
  }
}
</style>