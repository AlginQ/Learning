<script setup lang="ts">
import { RouterView } from 'vue-router'
import MainLayout from '@/components/MainLayout.vue'
import { useRoute } from 'vue-router'
import { computed } from 'vue'

const route = useRoute()

// 判断当前路由是否需要使用主布局
const needLayout = computed(() => {
  const noLayoutRoutes = ['Login', 'Register', 'Home', 'CourseDetailPage', 'Profile']
    
  // 管理员子路由需要使用主布局
  if (route.path.startsWith('/admin/')) {
    return true
  }
    
  // 个人中心相关页面需要使用主布局（但在新窗口中会单独处理）
  const profileRoutes = ['/profile', '/my-courses', '/study-records', '/change-password', '/notes']
  if (profileRoutes.includes(route.path)) {
    return true
  }
    
  return !noLayoutRoutes.includes(route.name as string)
})
</script>

<template>
  <div id="app">
    <MainLayout v-if="needLayout">
      <RouterView />
    </MainLayout>
    <RouterView v-else />
  </div>
</template>

<style scoped>
#app {
  height: 100vh;
  width: 100vw;
}
</style>