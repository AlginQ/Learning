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
                <div class="player-controls" v-if="currentLesson">
                  <!-- 倍速播放 -->
                  <el-dropdown @command="setPlaybackRate">
                    <el-button size="small" type="primary" plain>
                      {{ playbackRate }}x <el-icon><ArrowDown /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="0.5">0.5x</el-dropdown-item>
                        <el-dropdown-item command="1.0">1.0x</el-dropdown-item>
                        <el-dropdown-item command="1.25">1.25x</el-dropdown-item>
                        <el-dropdown-item command="1.5">1.5x</el-dropdown-item>
                        <el-dropdown-item command="2.0">2.0x</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                  
                  <!-- 清晰度切换 -->
                  <el-dropdown @command="setQuality">
                    <el-button size="small" type="primary" plain>
                      {{ quality }} <el-icon><ArrowDown /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="标清">标清</el-dropdown-item>
                        <el-dropdown-item command="高清">高清</el-dropdown-item>
                        <el-dropdown-item command="超清">超清</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                  
                  <!-- 全屏/画中画 -->
                  <el-button-group>
                    <el-button size="small" @click="toggleFullscreen">
                      <el-icon><FullScreen /></el-icon>
                    </el-button>
                    <el-button size="small" @click="togglePictureInPicture">
                      <el-icon><Picture /></el-icon>
                    </el-button>
                  </el-button-group>
                  
                  <!-- 收藏/分享 -->
                  <el-button-group>
                    <el-button size="small" @click="toggleFavorite" :type="isFavorite ? 'danger' : 'default'">
                      <el-icon><Star /></el-icon>
                      {{ isFavorite ? '已收藏' : '收藏' }}
                    </el-button>
                    <el-button size="small" @click="shareCourse">
                      <el-icon><Share /></el-icon>
                      分享
                    </el-button>
                  </el-button-group>
                </div>
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
                  <p>时长: {{ formatDuration(currentLesson.duration) }} | 进度: {{ currentProgress }}%</p>
                </div>
                <div class="video-container">
                  <!-- 视频播放器占位符 -->
                  <div 
                    class="video-player-placeholder" 
                    ref="videoPlayer"
                    tabindex="0"
                    @keydown="handleKeyDown"
                  >
                    <div class="playback-overlay" v-if="showOverlay">
                      <el-icon size="60" color="#fff">{{ isPlaying ? VideoPause : VideoPlay }}</el-icon>
                    </div>
                    <el-icon size="80" color="#409eff"><VideoPlay /></el-icon>
                    <p>视频播放区域</p>
                    <div class="video-controls">
                      <el-slider 
                        v-model="currentTime" 
                        :max="currentLesson.duration" 
                        :format-tooltip="formatSliderTime"
                        @change="seekVideo"
                      />
                      <div class="control-buttons">
                        <el-button circle @click="togglePlay">
                          <el-icon>{{ isPlaying ? VideoPause : VideoPlay }}</el-icon>
                        </el-button>
                        <el-button circle @click="seek(-10)">
                          <el-icon><CaretLeft /></el-icon>
                        </el-button>
                        <el-button circle @click="seek(10)">
                          <el-icon><CaretRight /></el-icon>
                        </el-button>
                        <span class="time-display">
                          {{ formatDuration(currentTime) }} / {{ formatDuration(currentLesson.duration) }}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- 记笔记按钮 -->
                <div class="note-section">
                  <el-button type="primary" @click="openNoteDialog" icon="Edit">
                    记笔记 ({{ currentTime | formatTime }})
                  </el-button>
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
          
          <!-- 讲师信息卡片 -->
          <el-card class="teacher-card" v-if="course.teacher">
            <template #header>
              <h3>讲师信息</h3>
            </template>
            <div class="teacher-profile">
              <el-avatar :src="course.teacher.avatar" :size="60" />
              <div class="teacher-details">
                <h4>{{ course.teacher.name }}</h4>
                <p class="teacher-title">{{ course.teacher.title }}</p>
                <p class="teacher-intro">{{ course.teacher.introduction }}</p>
              </div>
            </div>
          </el-card>
          
          <!-- 学习统计 -->
          <el-card class="stats-card">
            <template #header>
              <h3>学习统计</h3>
            </template>
            <div class="stats-grid">
              <div class="stat-item">
                <div class="stat-value">{{ formatDuration(totalStudyTime) }}</div>
                <div class="stat-label">总学习时长</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ completedLessons }}</div>
                <div class="stat-label">已完成课时</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ Math.round((completedLessons / totalLessons) * 100) }}%</div>
                <div class="stat-label">完成进度</div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
      
      <!-- 评论区 -->
      <el-card class="comments-section">
        <template #header>
          <h3>课程讨论区</h3>
        </template>
        <div class="comments-container">
          <div class="comment-form">
            <el-input
              v-model="newComment"
              type="textarea"
              placeholder="请输入您的问题或想法..."
              :rows="3"
            />
            <div class="comment-actions">
              <el-button type="primary" @click="submitComment" :disabled="!newComment.trim()">
                发布评论
              </el-button>
            </div>
          </div>
          
          <div class="comments-list">
            <div 
              v-for="comment in comments" 
              :key="comment.id"
              class="comment-item"
            >
              <div class="comment-header">
                <el-avatar :src="comment.avatar" :size="32" />
                <div class="comment-user-info">
                  <span class="comment-author">{{ comment.author }}</span>
                  <span class="comment-time">{{ comment.time }}</span>
                </div>
              </div>
              <div class="comment-content">
                {{ comment.content }}
              </div>
              <div class="comment-actions">
                <el-button link @click="likeComment(comment.id)">
                  <el-icon><CircleCheck /></el-icon>
                  {{ comment.likes }}
                </el-button>
                <el-button link @click="replyComment(comment.id)">
                  回复
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </el-card>
      
      <!-- 相关推荐 -->
      <el-card class="recommend-section">
        <template #header>
          <h3>相关推荐</h3>
        </template>
        <div class="recommend-grid">
          <div 
            v-for="relatedCourse in relatedCourses" 
            :key="relatedCourse.id"
            class="recommend-item"
            @click="goToCourse(relatedCourse.id)"
          >
            <img :src="relatedCourse.coverImage" :alt="relatedCourse.title" />
            <div class="recommend-info">
              <h4>{{ relatedCourse.title }}</h4>
              <div class="recommend-meta">
                <span class="rating">{{ relatedCourse.rating }}分</span>
                <span class="students">{{ relatedCourse.studentCount }}人学习</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>
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
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  VideoPlay, 
  VideoPause, 
  ArrowLeft, 
  ArrowDown,
  FullScreen,
  Picture,
  Star,
  Share,
  CaretLeft,
  CaretRight,
  Edit,
  CircleCheck,
  CircleClose,
  Message,
  ChatDotRound
} from '@element-plus/icons-vue'
import type { Course, Chapter, Lesson } from '@/types/course'

const route = useRoute()
const router = useRouter()

const courseId = route.params.id as string
const course = ref<Course | null>(null)
const chapters = ref<Chapter[]>([])
const activeChapter = ref<number | null>(null)
const currentLesson = ref<Lesson | null>(null)

// 播放器相关状态
const isPlaying = ref(false)
const currentTime = ref(0)
const playbackRate = ref('1.0')
const quality = ref('高清')
const showOverlay = ref(false)
const videoPlayer = ref<HTMLDivElement | null>(null)

// 学习进度
const currentProgress = ref(0)
const completedLessons = ref(3) // 模拟已完成3个课时
const totalLessons = computed(() => {
  return chapters.value.reduce((total, chapter) => total + chapter.lessons.length, 0)
})
const totalStudyTime = ref(7200) // 秒

// 交互状态
const isFavorite = ref(false)
const noteDialogVisible = ref(false)
const replyDialogVisible = ref(false)
const noteContent = ref('')
const newComment = ref('')
const replyContent = ref('')

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
    avatar: '',
    title: '高级前端工程师',
    introduction: '拥有8年前端开发经验，专注Vue生态技术栈，曾参与多个大型企业级项目开发。'
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
  }
]

const comments = ref([
  {
    id: 1,
    author: '学习者A',
    avatar: '',
    content: '这个课程讲得很清楚，特别是响应式系统的部分，让我对Vue的理解更深了！',
    time: '2小时前',
    likes: 15
  },
  {
    id: 2,
    author: '前端小白',
    avatar: '',
    content: '请问老师，Vue 3和Vue 2的主要区别是什么？',
    time: '1天前',
    likes: 8
  }
])

const relatedCourses = ref([
  {
    id: 2,
    title: 'React从入门到精通',
    coverImage: '',
    rating: 4.7,
    studentCount: 2345
  },
  {
    id: 3,
    title: 'TypeScript实战教程',
    coverImage: '',
    rating: 4.9,
    studentCount: 1876
  }
])

// 播放器控制函数
const togglePlay = () => {
  isPlaying.value = !isPlaying.value
  ElMessage.info(isPlaying.value ? '开始播放' : '暂停播放')
}

const seek = (seconds: number) => {
  currentTime.value = Math.max(0, Math.min(currentLesson.value?.duration || 0, currentTime.value + seconds))
  ElMessage.info(`快${seconds > 0 ? '进' : '退'}${Math.abs(seconds)}秒`)
}

const seekVideo = (value: number) => {
  currentTime.value = value
  ElMessage.info(`跳转到 ${formatDuration(value)}`)
}

const setPlaybackRate = (rate: string) => {
  playbackRate.value = rate
  ElMessage.info(`已设置播放速度为 ${rate}x`)
}

const setQuality = (qualityValue: string) => {
  quality.value = qualityValue
  ElMessage.info(`已切换到${qualityValue}画质`)
}

const toggleFullscreen = () => {
  if (document.fullscreenElement) {
    document.exitFullscreen()
  } else {
    videoPlayer.value?.requestFullscreen()
  }
  ElMessage.info('切换全屏模式')
}

const togglePictureInPicture = () => {
  ElMessage.info('切换画中画模式')
}

// 学习互动功能
const toggleFavorite = () => {
  isFavorite.value = !isFavorite.value
  ElMessage.success(isFavorite.value ? '收藏成功' : '取消收藏')
}

const shareCourse = () => {
  ElMessage.success('分享链接已复制到剪贴板')
}

const openNoteDialog = () => {
  noteDialogVisible.value = true
}

const saveNote = () => {
  if (noteContent.value.trim()) {
    ElMessage.success('笔记保存成功')
    noteDialogVisible.value = false
    noteContent.value = ''
  } else {
    ElMessage.warning('请输入笔记内容')
  }
}

// 章节状态相关函数
const isLessonCompleted = (lesson: Lesson): boolean => {
  return lesson.id <= 3  // 模拟前3个课时已完成
}

const getLessonStatusColor = (lesson: Lesson): string => {
  if (isLessonCompleted(lesson)) {
    return '#67C23A' // 绿色
  } else if (currentLesson?.value?.id === lesson.id) {
    return '#409eff' // 蓝色（当前学习）
  } else {
    return '#999' // 灰色（未完成）
  }
}

const getLessonStatusIcon = (lesson: Lesson) => {
  if (isLessonCompleted(lesson)) {
    return 'CircleCheck'
  } else if (currentLesson?.value?.id === lesson.id) {
    return 'VideoPlay'
  } else {
    return 'CircleClose'
  }
}

// 评论功能
const submitComment = () => {
  if (newComment.value.trim()) {
    comments.value.unshift({
      id: comments.value.length + 1,
      author: '我',
      avatar: '',
      content: newComment.value,
      time: '刚刚',
      likes: 0
    })
    newComment.value = ''
    ElMessage.success('评论发布成功')
  }
}

const likeComment = (commentId: number) => {
  const comment = comments.value.find(c => c.id === commentId)
  if (comment) {
    comment.likes += 1
    ElMessage.success('点赞成功')
  }
}

const replyComment = (commentId: number) => {
  replyDialogVisible.value = true
}

const submitReply = () => {
  if (replyContent.value.trim()) {
    ElMessage.success('回复发送成功')
    replyDialogVisible.value = false
    replyContent.value = ''
  }
}

// 导航功能
const goToCourse = (courseId: number) => {
  router.push(`/course/${courseId}`)
}

// 格式化函数
const formatDuration = (seconds: number) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
}

const formatSliderTime = (value: number) => {
  return formatDuration(value)
}

// 快捷键支持
const handleKeyDown = (event: KeyboardEvent) => {
  if (!currentLesson.value) return
  
  switch (event.code) {
    case 'Space':
      event.preventDefault()
      togglePlay()
      break
    case 'ArrowLeft':
      event.preventDefault()
      seek(-10)
      break
    case 'ArrowRight':
      event.preventDefault()
      seek(10)
      break
  }
}

// 返回上一页
const goBack = () => {
  router.push('/courses')
}

// 选择课程
const selectLesson = (lesson: Lesson, chapter: Chapter) => {
  currentLesson.value = lesson
  activeChapter.value = chapter.id
  currentTime.value = 0 // 重置播放时间
  ElMessage.success(`开始学习: ${lesson.title}`)
}

// 自动播放进度更新
let progressInterval: number | null = null

const startProgressTimer = () => {
  if (progressInterval) clearInterval(progressInterval)
  
  progressInterval = window.setInterval(() => {
    if (isPlaying.value && currentLesson.value) {
      currentTime.value = Math.min(currentLesson.value.duration, currentTime.value + 1)
      currentProgress.value = Math.round((currentTime.value / currentLesson.value.duration) * 100)
      
      // 模拟学习时长增加
      if (currentTime.value % 60 === 0) {
        totalStudyTime.value += 60
      }
    }
  }, 1000)
}

const stopProgressTimer = () => {
  if (progressInterval) {
    clearInterval(progressInterval)
    progressInterval = null
  }
}

// 组件生命周期
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
    startProgressTimer()
  }, 1000)
  
  // 添加全局键盘监听
  window.addEventListener('keydown', (e) => {
    if (e.target instanceof HTMLInputElement || e.target instanceof HTMLTextAreaElement) {
      return // 如果焦点在输入框内，不处理快捷键
    }
    handleKeyDown(e)
  })
})

onUnmounted(() => {
  stopProgressTimer()
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
  min-height: 750px;
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

.player-controls {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: auto;
}

.progress-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-left: auto;
  min-width: 150px;
}

.video-player {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.video-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.video-container {
  position: relative;
  height: 60vh;
  max-height: 600px;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}

.video-player-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #000;
  color: white;
  border-radius: 8px;
}

.playback-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.video-controls {
  width: 100%;
  padding: 15px;
  background: rgba(0, 0, 0, 0.8);
}

.control-buttons {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-top: 10px;
}

.time-display {
  margin-left: auto;
  color: white;
  font-size: 14px;
}

.note-section {
  padding: 15px;
  text-align: center;
  border-top: 1px solid #eee;
}

.lesson-item.completed {
  background: #f0fdf4;
  border-color: #bbf7d0;
}

.lesson-item.current {
  background: #e3f2fd;
  border-color: #409eff;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(64, 158, 255, 0.4); }
  70% { box-shadow: 0 0 0 10px rgba(64, 158, 255, 0); }
  100% { box-shadow: 0 0 0 0 rgba(64, 158, 255, 0); }
}

.teacher-card, .stats-card {
  margin-top: 20px;
}

.teacher-profile {
  display: flex;
  gap: 15px;
  align-items: center;
}

.teacher-details h4 {
  margin: 0 0 5px 0;
  color: #333;
}

.teacher-title {
  margin: 0 0 8px 0;
  color: #666;
  font-size: 14px;
}

.teacher-intro {
  margin: 0;
  color: #999;
  font-size: 13px;
  line-height: 1.4;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  text-align: center;
}

.stat-item {
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.comments-section, .recommend-section {
  margin-top: 30px;
}

.comment-form {
  margin-bottom: 30px;
}

.comment-actions {
  margin-top: 15px;
  text-align: right;
}

.comment-item {
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.comment-user-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.comment-author {
  font-weight: 500;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-content {
  margin-bottom: 10px;
  color: #666;
  line-height: 1.5;
}

.recommend-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.recommend-item {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.recommend-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.recommend-item img {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.recommend-info {
  padding: 15px;
}

.recommend-info h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 16px;
}

.recommend-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.rating {
  color: #f59e0b;
  font-weight: 500;
}

.students {
  color: #999;
  font-size: 14px;
}

.note-dialog-content, .reply-dialog-content {
  padding: 20px 0;
}

.note-time {
  margin-bottom: 15px;
  padding: 10px;
  background: #f0f8ff;
  border-radius: 4px;
  color: #409eff;
  font-weight: 500;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
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