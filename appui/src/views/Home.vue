<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { User, Lock, Search, Menu } from '@element-plus/icons-vue'
import { isAdmin as checkIsAdmin } from '@/utils/permission'

const router = useRouter()
const userStore = useUserStore()

const activeIndex = ref('1')
const searchKeyword = ref('')

// 权限判断
const isAdmin = computed(() => userStore.isLogin)
const isCurrentUserAdmin = computed(() => checkIsAdmin())

const handleSelect = (key: string) => {
  switch (key) {
    case '1':
      router.push('/')
      break
    case '2':
      router.push('/courses')
      break
    case '3':
      if (userStore.isLogin) {
        router.push('/study')
      } else {
        router.push('/login')
      }
      break
    case '4':
      if (userStore.isLogin) {
        router.push('/my-courses')
      } else {
        router.push('/login')
      }
      break
    case '5':
      if (isCurrentUserAdmin.value) {
        router.push('/admin/users')
      } else {
        router.push('/')
      }
      break
  }
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push(`/courses?keyword=${encodeURIComponent(searchKeyword.value)}`)
  }
}

const handleLogout = () => {
  userStore.logout()
  router.push('/')
}
</script>

<template>
  <div class="home-layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo">
          <h2>在线学习平台</h2>
        </div>
        
        <div class="nav-menu">
          <el-menu
            :default-active="activeIndex"
            mode="horizontal"
            @select="handleSelect"
            background-color="#545c64"
            text-color="#fff"
            active-text-color="#ffd04b"
          >
            <el-menu-item index="1">首页</el-menu-item>
            <el-menu-item index="2">课程</el-menu-item>
            <el-menu-item v-if="isAdmin" index="3">学习中心</el-menu-item>
            <el-menu-item v-if="isAdmin" index="4">我的课程</el-menu-item>
            <el-menu-item v-if="isAdmin && isCurrentUserAdmin" index="5">管理中心</el-menu-item>
          </el-menu>
        </div>
        
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索课程..."
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="handleSearch" />
            </template>
          </el-input>
        </div>
        
        <div class="user-actions">
          <template v-if="userStore.isLogin">
            <el-dropdown>
              <span class="el-dropdown-link">
                <el-avatar :icon="User" size="small" />
                {{ userStore.currentUser?.nickname || userStore.currentUser?.username }}
                <el-icon class="el-icon--right">
                  <Menu />
                </el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" @click="$router.push('/login')">
              <el-icon><Lock /></el-icon>
              登录
            </el-button>
            <el-button @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>
    
    <!-- 主要内容区域 -->
    <el-main class="main-content">
      <div class="hero-section">
        <h1>开启您的学习之旅</h1>
        <p>海量优质课程，助您提升技能，成就未来</p>
        <el-button type="primary" size="large" @click="$router.push('/courses')">
          开始学习
        </el-button>
      </div>
      
      <div class="features-section">
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="feature-card">
              <el-icon size="40" color="#409EFF"><User /></el-icon>
              <h3>专业讲师</h3>
              <p>行业专家授课，理论与实践相结合</p>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="feature-card">
              <el-icon size="40" color="#67C23A"><Lock /></el-icon>
              <h3>灵活学习</h3>
              <p>随时随地学习，进度自由掌控</p>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="feature-card">
              <el-icon size="40" color="#E6A23C"><Search /></el-icon>
              <h3>丰富资源</h3>
              <p>涵盖多个领域，满足不同学习需求</p>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-main>
    
    <!-- 底部 -->
    <el-footer class="footer">
      <p>© 2026 在线学习平台. All rights reserved.</p>
    </el-footer>
  </div>
</template>

<style scoped>
.home-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #545c64;
  padding: 0;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
}

.logo h2 {
  color: white;
  margin: 0;
}

.nav-menu {
  flex: 1;
  margin: 0 20px;
}

.search-box {
  width: 300px;
  margin-right: 20px;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  color: white;
}

.main-content {
  flex: 1;
  padding: 0;
}

.hero-section {
  text-align: center;
  padding: 100px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.hero-section h1 {
  font-size: 3rem;
  margin-bottom: 20px;
}

.hero-section p {
  font-size: 1.2rem;
  margin-bottom: 30px;
}

.features-section {
  padding: 60px 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.feature-card {
  text-align: center;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-card h3 {
  margin: 20px 0 10px;
}

.footer {
  background-color: #f5f5f5;
  text-align: center;
  padding: 20px;
  color: #666;
}
</style>