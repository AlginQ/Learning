<template>
  <div class="course-detail-page">
    <!-- 返回按钮 -->
    <div class="back-button">
      <el-button @click="goBack" type="primary" plain>
        <el-icon><ArrowLeft /></el-icon>
        返回课程列表
      </el-button>
    </div>

    <!-- 课程详情内容 -->
    <div class="course-detail-content" v-if="course">
      <!-- 课程基本信息 -->
      <el-card class="course-header">
        <template #header>
          <div class="card-header">
            <h1>{{ course.title }}</h1>
          </div>
        </template>
        
        <div class="course-basic-info">
          <div class="info-row">
            <el-tag>{{ course.category.name }}</el-tag>
            <el-tag type="success">{{ course.rating }}分</el-tag>
            <el-tag type="warning">{{ course.lessonCount }}课时</el-tag>
            <el-tag type="info">{{ course.studentCount }}人学习</el-tag>
          </div>
          
          <div class="course-description">
            <p>{{ course.description }}</p>
          </div>
          
          <div class="teacher-info">
            <el-avatar :src="course.teacher.avatar" :size="40" />
            <span class="teacher-name">{{ course.teacher.name }}</span>
          </div>
        </div>
      </el-card>

      <!-- 课程内容区域 -->
      <div class="course-content-area">
        <!-- 左侧视频播放区域 -->
        <div class="video-section">
          <el-card>
            <template #header>
              <div class="section-header">
                <h3>课程播放</h3>
              </div>
            </template>
            
            <div class="video-player">
              <div class="video-placeholder" v-if="!currentLesson">
                <el-icon size="60" color="#999"><VideoPlay /></el-icon>
                <p>请选择章节开始学习</p>
              </div>
              <div class="video-content" v-else>
                <div class="current-lesson-info">
                  <h4>{{ currentLesson.title }}</h4>
                  <p>时长: {{ formatDuration(currentLesson.duration) }}</p>
                </div>
                <div class="video-player-placeholder">
                  <el-icon size="80" color="#409eff"><VideoPlay /></el-icon>
                  <p>视频播放区域</p>
                </div>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 右侧章节列表 -->
        <div class="chapters-section">
          <el-card>
            <template #header>
              <div class="section-header">
                <h3>课程章节</h3>
              </div>
            </template>
            
            <div class="chapters-list">
              <el-collapse v-model="activeChapter" accordion>
                <el-collapse-item 
                  v-for="chapter in chapters" 
                  :key="chapter.id"
                  :title="`${chapter.chapterNumber}. ${chapter.title}`"
                  :name="chapter.id"
                >
                  <div 
                    v-for="lesson in chapter.lessons" 
                    :key="lesson.id"
                    class="lesson-item"
                    :class="{ active: currentLesson?.id === lesson.id }"
                    @click="selectLesson(lesson, chapter)"
                  >
                    <div class="lesson-info">
                      <el-icon :color="currentLesson?.id === lesson.id ? '#409eff' : '#999'">
                        <VideoPlay />
                      </el-icon>
                      <span class="lesson-title">{{ lesson.title }}</span>
                    </div>
                    <div class="lesson-meta">
                      <span class="lesson-duration">{{ formatDuration(lesson.duration) }}</span>
                      <el-tag 
                        v-if="lesson.isFree" 
                        type="success" 
                        size="small"
                        effect="plain"
                      >
                        免费
                      </el-tag>
                    </div>
                  </div>
                </el-collapse-item>
              </el-collapse>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-else class="loading-container">
      <el-skeleton animated>
        <template #template>
          <el-skeleton-item variant="h1" style="width: 60%" />
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
import { VideoPlay, ArrowLeft } from '@element-plus/icons-vue'
import type { Course, Chapter, Lesson } from '@/types/course'

const route = useRoute()
const router = useRouter()

const courseId = route.params.id as string
const course = ref<Course | null>(null)
const chapters = ref<Chapter[]>([])
const activeChapter = ref<number | null>(null)
const currentLesson = ref<Lesson | null>(null)

// 模拟数据
const mockCourse: Course = {
  id: 1,
  title: 'Vue 3 入门教程',
  description: '从零开始学习 Vue 3，掌握现代前端开发核心技术。本课程涵盖Vue 3的基础概念、响应式系统、组件开发、状态管理等核心内容，适合前端开发初学者。',
  coverImage: '',
  price: 0,
  category: {
    id: 1,
    name: '前端开发'
  },
  teacher: {
    id: 1,
    name: '张老师',
    avatar: ''
  },
  lessonCount: 20,
  studentCount: 1234,
  rating: 4.8,
  createTime: new Date().toISOString()
}

const mockChapters: Chapter[] = [
  {
    id: 1,
    title: 'Vue 3 基础入门',
    chapterNumber: 1,
    lessons: [
      {
        id: 1,
        title: 'Vue 3 简介与环境搭建',
        videoUrl: '',
        duration: 930,
        lessonNumber: 1,
        isFree: true
      },
      {
        id: 2,
        title: 'Vue 3 响应式系统原理',
        videoUrl: '',
        duration: 1335,
        lessonNumber: 2,
        isFree: true
      },
      {
        id: 3,
        title: '模板语法与指令',
        videoUrl: '',
        duration: 1520,
        lessonNumber: 3,
        isFree: false
      }
    ]
  },
  {
    id: 2,
    title: '组件开发进阶',
    chapterNumber: 2,
    lessons: [
      {
        id: 4,
        title: '组件基础概念',
        videoUrl: '',
        duration: 1125,
        lessonNumber: 1,
        isFree: false
      },
      {
        id: 5,
        title: '组件通信方式',
        videoUrl: '',
        duration: 1800,
        lessonNumber: 2,
        isFree: false
      }
    ]
  },
  {
    id: 3,
    title: '状态管理与路由',
    chapterNumber: 3,
    lessons: [
      {
        id: 6,
        title: 'Pinia状态管理',
        videoUrl: '',
        duration: 2200,
        lessonNumber: 1,
        isFree: false
      },
      {
        id: 7,
        title: 'Vue Router路由管理',
        videoUrl: '',
        duration: 1950,
        lessonNumber: 2,
        isFree: false
      }
    ]
  }
]

// 格式化时长显示
const formatDuration = (seconds: number) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
}

// 返回上一页
const goBack = () => {
  router.push('/courses')
}

// 选择课程
const selectLesson = (lesson: Lesson, chapter: Chapter) => {
  currentLesson.value = lesson
  activeChapter.value = chapter.id
  ElMessage.success(`开始学习: ${lesson.title}`)
}

// 加载课程数据
onMounted(() => {
  // 模拟 API 调用
  setTimeout(() => {
    course.value = mockCourse
    chapters.value = mockChapters
    if (mockChapters.length > 0) {
      activeChapter.value = mockChapters[0].id
      // 默认选择第一个免费课程
      const firstFreeLesson = mockChapters[0].lessons.find(lesson => lesson.isFree)
      if (firstFreeLesson) {
        currentLesson.value = firstFreeLesson
      }
    }
  }, 1000)
})
</script>

<style scoped>
.course-detail-page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.back-button {
  margin-bottom: 20px;
}

.course-detail-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.course-header {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header h1 {
  margin: 0;
  color: #333;
  font-size: 28px;
  font-weight: 600;
}

.course-basic-info {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-row {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.course-description p {
  color: #666;
  line-height: 1.6;
  font-size: 16px;
  margin: 0;
}

.teacher-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.teacher-name {
  font-weight: 500;
  color: #333;
}

.course-content-area {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  height: 600px;
}

.video-section, .chapters-section {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.section-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.video-player {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.video-placeholder {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  background: #fafafa;
  border-radius: 4px;
}

.video-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.current-lesson-info {
  padding: 15px;
  background: #f0f8ff;
  border-radius: 4px;
}

.current-lesson-info h4 {
  margin: 0 0 5px 0;
  color: #333;
}

.current-lesson-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.video-player-placeholder {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #000;
  color: white;
  border-radius: 4px;
}

.chapters-list {
  max-height: 500px;
  overflow-y: auto;
}

.lesson-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  margin-bottom: 8px;
  background: #fafafa;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.lesson-item:hover {
  background: #f0f8ff;
  border-color: #d1e7ff;
}

.lesson-item.active {
  background: #e3f2fd;
  border-color: #409eff;
}

.lesson-info {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
}

.lesson-title {
  font-size: 14px;
  color: #333;
}

.lesson-item.active .lesson-title {
  color: #409eff;
  font-weight: 500;
}

.lesson-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.lesson-duration {
  font-size: 12px;
  color: #999;
}

.loading-container {
  padding: 50px;
  text-align: center;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 滚动条样式 */
.chapters-list::-webkit-scrollbar {
  width: 6px;
}

.chapters-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.chapters-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.chapters-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .course-content-area {
    grid-template-columns: 1fr;
    height: auto;
  }
  
  .video-section {
    order: 1;
  }
  
  .chapters-section {
    order: 2;
  }
}

@media (max-width: 768px) {
  .course-detail-page {
    padding: 10px;
  }
  
  .card-header h1 {
    font-size: 24px;
  }
  
  .info-row {
    justify-content: center;
  }
}
</style>