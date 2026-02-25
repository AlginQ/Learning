<script setup lang="ts">
import { RouterView } from 'vue-router'
import MainLayout from '@/components/MainLayout.vue'
import { useRoute } from 'vue-router'
import { computed } from 'vue'

const route = useRoute()

// 定义独立页面路由列表
const noLayoutRoutes = ['Login', 'Register', 'Home', 'CourseDetailPage', 'Profile', 'MyCourses', 'Courses', 'StudyCenter']

// 判断当前路由是否为独立页面
const isIndependentPage = computed(() => {
  // 管理员子路由需要使用主布局
  if (route.path.startsWith('/admin/')) {
    return false
  }
  
  // 安全检查：确保 route.name 不为空
  const currentRouteName = route.name
  return currentRouteName && noLayoutRoutes.includes(currentRouteName as string)
})

// 判断是否需要主布局
const needLayout = computed(() => {
  return !isIndependentPage.value
})
</script>

<template>
  <div id="app">
    <RouterView v-if="isIndependentPage" />
    <MainLayout v-else>
      <RouterView />
    </MainLayout>
  </div>
</template>

<style scoped>
#app {
  height: 100vh;
  width: 100vw;
}
</style>