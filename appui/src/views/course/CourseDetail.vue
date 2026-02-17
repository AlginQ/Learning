<template>
  <div class="course-detail">
    <el-page-header @back="goBack">
      <template #content>
        <span class="text-large font-600 mr-3"> 课程详情 </span>
      </template>
    </el-page-header>
    
    <div class="course-content" v-if="course">
      <el-card class="course-info">
        <template #header>
          <div class="card-header">
            <span>{{ course.title }}</span>
          </div>
        </template>
        <div class="course-description">
          <p>{{ course.description }}</p>
        </div>
        <div class="course-meta">
          <el-tag>{{ course.category.name }}</el-tag>
          <el-tag type="success">{{ course.rating }}分</el-tag>
          <el-tag type="warning">{{ course.lessonCount }}课时</el-tag>
        </div>
      </el-card>
      
      <el-card class="chapters">
        <template #header>
          <div class="card-header">
            <span>课程章节</span>
          </div>
        </template>
        <div class="chapter-list">
          <el-collapse v-model="activeChapter">
            <el-collapse-item 
              v-for="chapter in chapters" 
              :key="chapter.id"
              :title="chapter.title"
              :name="chapter.id"
            >
              <div class="lesson-list">
                <div 
                  v-for="lesson in chapter.lessons" 
                  :key="lesson.id"
                  class="lesson-item"
                  @click="startLesson(lesson)"
                >
                  <el-icon><VideoPlay /></el-icon>
                  <span class="lesson-title">{{ lesson.title }}</span>
                  <span class="lesson-duration">{{ formatDuration(lesson.duration) }}</span>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </el-card>
    </div>
    
    <div v-else class="loading">
      <el-skeleton animated>
        <template #template>
          <el-skeleton-item variant="h3" style="width: 50%" />
          <div style="margin-top: 20px">
            <el-skeleton-item variant="p" style="width: 100%" />
            <el-skeleton-item variant="p" style="width: 80%; margin-top: 10px" />
          </div>
        </template>
      </el-skeleton>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { VideoPlay } from '@element-plus/icons-vue'
import type { Course, Chapter } from '@/types/course'

const route = useRoute()
const router = useRouter()

const courseId = route.params.id as string
const course = ref<Course | null>(null)
const chapters = ref<Chapter[]>([])
const activeChapter = ref<string>('')

// 模拟数据
const mockCourse: Course = {
  id: 1,
  title: 'Vue 3 入门教程',
  description: '从零开始学习 Vue 3，掌握现代前端开发核心技术',
  coverImage: '',
  price: 0,
  category: {
    id: 1,
    name: '前端开发'
  },
  teacher: {
    id: 1,
    name: '张老师'
  },
  lessonCount: 20,
  studentCount: 1234,
  rating: 4.8,
  createTime: new Date().toISOString()
}

const mockChapters: Chapter[] = [
  {
    id: 1,
    title: '第一章：Vue 3 基础',
    chapterNumber: 1,
    lessons: [
      {
        id: 1,
        title: 'Vue 3 简介',
        videoUrl: '',
        duration: 930,
        lessonNumber: 1,
        isFree: true
      },
      {
        id: 2,
        title: '响应式系统',
        videoUrl: '',
        duration: 1335,
        lessonNumber: 2,
        isFree: true
      }
    ]
  },
  {
    id: 2,
    title: '第二章：组件开发',
    chapterNumber: 2,
    lessons: [
      {
        id: 3,
        title: '组件基础',
        videoUrl: '',
        duration: 1125,
        lessonNumber: 1,
        isFree: false
      }
    ]
  }
]

const goBack = () => {
  router.go(-1)
}

// 格式化时长显示
const formatDuration = (seconds: number) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
}

const startLesson = (lesson: any) => {
  ElMessage.info(`开始学习：${lesson.title}`)
  // 这里可以跳转到视频播放页面
}

onMounted(() => {
  // 模拟 API 调用
  setTimeout(() => {
    course.value = mockCourse
    chapters.value = mockChapters
    if (mockChapters.length > 0) {
      activeChapter.value = mockChapters[0].id
    }
  }, 1000)
})
</script>

<style scoped>
.course-detail {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.course-content {
  margin-top: 20px;
}

.course-info {
  margin-bottom: 20px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.course-description p {
  color: #666;
  line-height: 1.6;
}

.course-meta {
  margin-top: 15px;
  display: flex;
  gap: 10px;
}

.chapters {
  margin-bottom: 20px;
}

.lesson-list {
  padding-left: 20px;
}

.lesson-item {
  display: flex;
  align-items: center;
  padding: 10px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.lesson-item:hover {
  background-color: #f5f7fa;
}

.lesson-item .el-icon {
  margin-right: 10px;
  color: #409eff;
}

.lesson-title {
  flex: 1;
}

.lesson-duration {
  color: #999;
  font-size: 12px;
}

.loading {
  padding: 50px;
  text-align: center;
}
</style>