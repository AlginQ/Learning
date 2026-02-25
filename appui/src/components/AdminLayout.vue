<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'
import {
  HomeFilled,
  User,
  Management,
  Setting,
  SwitchButton
} from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()
const isCollapse = ref(false)

// 菜单项配置
const menuItems = [
  {
    index: 'admin-users',
    title: '用户管理',
    icon: User,
    path: '/admin/users',
    roles: ['ADMIN']
  },
  {
    index: 'admin-courses',
    title: '课程管理',
    icon: Management,
    path: '/admin/courses',
    roles: ['ADMIN']
  }
]

// 菜单点击处理
const handleMenuSelect = (index: string) => {
  const menuItem = menuItems.find(item => item.index === index)
  if (menuItem) {
    router.push(menuItem.path)
  }
}
</script>

<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <el-aside 
      :width="isCollapse ? '64px' : '200px'" 
      class="sidebar"
    >
      <div class="sidebar-header">
        <div class="logo-full">
          <h2>管理后台</h2>
        </div>
      </div>
      
      <el-menu
        :default-active="$route.path"
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
          :index="item.path"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.title }}</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区域 -->
    <div class="main-container">
      <router-view />
    </div>
  </div>
</template>

<style scoped>
.admin-layout {
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

.sidebar-menu {
  flex: 1;
  border-right: none;
}

.main-container {
  flex: 1;
  overflow: auto;
}
</style>