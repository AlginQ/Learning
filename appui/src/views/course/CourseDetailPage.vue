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
            <el-tag>{{ course.category?.name || '未分类' }}</el-tag>
            <el-tag type="success">{{ course.rating }}分</el-tag>
            <el-tag type="warning">{{ course.lessonCount }}课时</el-tag>
            <el-tag type="info">{{ course.studentCount }}人学习</el-tag>
          </div>
          
          <div class="course-description">
            <p>{{ course.description }}</p>
          </div>
          
          <div class="teacher-info" v-if="course.teacher">
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
                <!-- 移除顶部控制栏，保留空div -->
                <div></div>
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
                  <!-- 视频播放器 -->
                  <div class="bilibili-player" ref="videoPlayer" tabindex="0" @keydown="handleKeyDown">
                    <!-- 视频播放区域 -->
                    <div class="video-area">
                      <div class="video-placeholder" v-if="!currentLesson">
                        <el-icon size="60" color="#999"><VideoPlay /></el-icon>
                        <p>请选择章节开始学习</p>
                      </div>
                      <div class="video-content" v-else>
                        <!-- 视频播放器主体 -->
                        <div class="player-main">
                          <div class="player-video">
                            <!-- 实际视频元素 -->
                            <video 
                              ref="videoElement" 
                              :src="currentLesson.videoUrl" 
                              class="video-element"
                              @play="isPlaying = true"
                              @pause="isPlaying = false"
                              @timeupdate="updateTime"
                              @ended="handleEnded"
                            ></video>
                            
                            <!-- 播放按钮覆盖层 -->
                            <div class="play-overlay" v-if="!isPlaying">
                              <el-button circle size="large" type="primary" @click="togglePlay">
                                <el-icon size="32">
                                  <component :is="isPlaying ? VideoPause : VideoPlay" />
                                </el-icon>
                              </el-button>
                            </div>
                            
                            <!-- 视频标题覆盖层 -->
                            <div class="video-title-overlay">
                              <h4>{{ currentLesson.title }}</h4>
                            </div>
                            
                            <!-- 底部控制栏 -->
                            <div class="video-controls-bottom">
                              <div class="progress-bar">
                                <el-slider 
                                  v-model="currentTime" 
                                  :max="currentLesson.duration" 
                                  :format-tooltip="formatSliderTime"
                                  @change="seekVideo"
                                  style="width: 100%"
                                />
                              </div>
                              <div class="control-row">
                                <div class="left-controls">
                                  <el-button circle size="small" @click="togglePlay">
                                    <el-icon>
                                      <component :is="isPlaying ? VideoPause : VideoPlay" />
                                    </el-icon>
                                  </el-button>
                                  <span class="time-display">
                                    {{ formatDuration(currentTime) }} / {{ formatDuration(currentLesson.duration) }}
                                  </span>
                                </div>
                                              
                                <div class="center-controls">
                                  <el-dropdown trigger="click" @command="setQuality">
                                    <el-button size="small" plain>
                                      {{ quality }}
                                    </el-button>
                                    <template #dropdown>
                                      <el-dropdown-menu>
                                        <el-dropdown-item command="超清">超清</el-dropdown-item>
                                        <el-dropdown-item command="高清">高清</el-dropdown-item>
                                        <el-dropdown-item command="标清">标清</el-dropdown-item>
                                      </el-dropdown-menu>
                                    </template>
                                  </el-dropdown>
                                  
                                  <el-dropdown trigger="click" @command="setPlaybackRate">
                                    <el-button size="small" plain>
                                      倍速
                                    </el-button>
                                    <template #dropdown>
                                      <el-dropdown-menu>
                                        <el-dropdown-item command="0.5">0.5x</el-dropdown-item>
                                        <el-dropdown-item command="0.75">0.75x</el-dropdown-item>
                                        <el-dropdown-item command="1.0">1.0x</el-dropdown-item>
                                        <el-dropdown-item command="1.25">1.25x</el-dropdown-item>
                                        <el-dropdown-item command="1.5">1.5x</el-dropdown-item>
                                        <el-dropdown-item command="2.0">2.0x</el-dropdown-item>
                                      </el-dropdown-menu>
                                    </template>
                                  </el-dropdown>
                                  

                                  

                                </div>
                                              
                                <div class="right-controls">
                                  <el-button size="small" plain @click="toggleFavorite">
                                    <el-icon><Star :class="{ filled: isFavorite }" /></el-icon>
                                  </el-button>
                                  <el-button size="small" plain>
                                    <el-icon><Setting /></el-icon>
                                  </el-button>
                                  <el-button size="small" plain @click="toggleFullscreen">
                                    <el-icon><FullScreen /></el-icon>
                                  </el-button>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
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
                <el-avatar :size="32">{{ comment.username?.charAt(0) }}</el-avatar>
                <div class="comment-user-info">
                  <span class="comment-author">{{ comment.username }}</span>
                  <el-rate v-if="comment.rating" :value="comment.rating" disabled :show-score="false" />
                  <span class="comment-time">{{ comment.createTime }}</span>
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
  ChatDotRound,
  Setting
} from '@element-plus/icons-vue'
import type { Course, Chapter, Lesson } from '@/types/course'
import { addStudyRecord } from '@/api/study'
import { getCourseDetailApi, getCourseChaptersApi } from '@/api/course'
import { addFavoriteApi, removeFavoriteApi, getFavoriteStatusApi } from '@/api/favorite'
import { addCommentApi, getCommentsByCourseIdApi, likeCommentApi, type CommentItem } from '@/api/comment'
import { getProgressApi, saveProgressApi, type StudyProgress } from '@/api/progress'

const route = useRoute()
const router = useRouter()

const courseId = ref(parseInt(route.params.id as string) || 0)
const course = ref<Course | null>(null)
const chapters = ref<Chapter[]>([])
const activeChapter = ref<number | null>(null)
const currentLesson = ref<Lesson | null>(null)
const dataSource = ref<'backend' | 'mock'>('mock') // 追踪数据源

// 播放器相关状态
const isPlaying = ref(false)
const currentTime = ref(0)
const playbackRate = ref('1.0')
const quality = ref('高清')
const showOverlay = ref(false)
const videoPlayer = ref<HTMLDivElement | null>(null)
const videoElement = ref<HTMLVideoElement | null>(null)
const recordTimer = ref<number | null>(null)

// 学习进度
const currentProgress = ref(0)
const completedLessons = ref(0)
const totalLessons = computed(() => {
  return chapters.value.reduce((total, chapter) => total + chapter.lessons.length, 0)
})
const totalStudyTime = ref(0) // 秒

// 从本地存储加载学习进度
const loadLearningProgress = (courseId: number) => {
  const storedProgress = localStorage.getItem(`learning_progress_${courseId}`)
  if (storedProgress) {
    try {
      const progress = JSON.parse(storedProgress)
      currentProgress.value = progress.currentProgress || 0
      completedLessons.value = progress.completedLessons || 0
      totalStudyTime.value = progress.totalStudyTime || 0
      return progress
    } catch (error) {
      console.error('加载学习进度失败:', error)
    }
  }
  return null
}

// 保存学习进度到本地存储
const saveLearningProgress = (courseId: number) => {
  const progress = {
    currentProgress: currentProgress.value,
    completedLessons: completedLessons.value,
    totalStudyTime: totalStudyTime.value,
    lastUpdated: new Date().toISOString()
  }
  localStorage.setItem(`learning_progress_${courseId}`, JSON.stringify(progress))
  
  // 同时保存课程基本信息到本地存储，以便MyCourses页面使用
  if (course.value) {
    const courseInfo = {
      id: course.value.id,
      title: course.value.title,
      coverImage: course.value.coverImage
    }
    localStorage.setItem(`course_info_${courseId}`, JSON.stringify(courseInfo))
  }
}

// 获取当前用户ID（用于localStorage键的唯一性）
const getCurrentUserId = (): string => {
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    try {
      const user = JSON.parse(userInfo)
      return user.id || 'anonymous'
    } catch {
      return 'anonymous'
    }
  }
  return 'anonymous'
}

// 保存当前课时进度（同时保存到数据库和localStorage）
const saveLessonProgress = async (courseId: number, lessonId: number, time: number) => {
  // 使用用户ID+课程ID+课时ID作为键，确保每个用户的进度独立
  const userId = getCurrentUserId()
  const storageKey = `progress_${userId}_${courseId}_${lessonId}`
  localStorage.setItem(storageKey, String(time))
  console.log(`保存进度到localStorage: ${storageKey} = ${time}秒`)
  
  // 如果用户已登录，同步保存到数据库
  const token = localStorage.getItem('token')
  if (token && currentLesson.value) {
    const duration = currentLesson.value.duration || 1
    const progress = Math.round((time / duration) * 100)
    const isCompleted = progress >= 90
    
    try {
      await saveProgressApi(lessonId, progress, Math.round(time), isCompleted)
      console.log('进度已同步到数据库')
    } catch (error) {
      console.error('保存进度到数据库失败:', error)
    }
  }
}

// 加载当前课时进度（优先从数据库获取）
const loadLessonProgress = async (courseId: number, lessonId: number): Promise<number> => {
  // 如果用户已登录，先从数据库获取
  const token = localStorage.getItem('token')
  if (token) {
    try {
      const response = await getProgressApi(lessonId)
      if (response.code === 200 && response.data) {
        const progress = response.data as StudyProgress
        const savedTime = progress.watchDuration || 0
        console.log(`从数据库加载进度: ${savedTime}秒 (课程${courseId}, 课时${lessonId})`)
        return savedTime
      }
    } catch (error) {
      console.error('从数据库加载进度失败，使用localStorage:', error)
    }
  }
  
  // 从localStorage加载（使用用户ID确保唯一性）
  const userId = getCurrentUserId()
  const storageKey = `progress_${userId}_${courseId}_${lessonId}`
  const storedProgress = localStorage.getItem(storageKey)
  if (storedProgress) {
    try {
      const savedTime = parseFloat(storedProgress)
      console.log(`从localStorage加载进度: ${savedTime}秒 (${storageKey})`)
      return savedTime
    } catch (error) {
      console.error('加载课时进度失败:', error)
    }
  }
  console.log(`没有找到进度数据，从0开始 (课程${courseId}, 课时${lessonId})`)
  return 0
}

// 交互状态
const isFavorite = ref(false)
const replyDialogVisible = ref(false)
const newComment = ref('')
const replyContent = ref('')

// 模拟数据 - 多个课程
const mockCourses: Course[] = [
  {
    id: 1,
    title: 'Vue 3 入门教程',
    description: '从零开始学习 Vue 3，掌握现代前端开发核心技术。本课程涵盖Vue 3的基础概念、响应式系统、组件开发、状态管理等核心内容，适合前端开发初学者。',
    coverImage: 'https://picsum.photos/400/225?random=1',
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
  },
  {
    id: 2,
    title: 'Spring Boot 企业级开发',
    description: '深入学习Spring Boot企业级开发技术，掌握微服务架构设计与实现，适合后端开发工程师。',
    coverImage: 'https://picsum.photos/400/225?random=2',
    price: 199,
    category: {
      id: 2,
      name: '后端开发'
    },
    teacher: {
      id: 2,
      name: '李老师',
      avatar: '',
      title: '高级后端工程师',
      introduction: '拥有10年Java开发经验，精通Spring生态系统，曾主导多个大型企业级项目架构设计。'
    },
    lessonCount: 25,
    studentCount: 2345,
    rating: 4.7,
    createTime: new Date().toISOString()
  },
  {
    id: 3,
    title: 'React Hooks 完全指南',
    description: '全面学习React Hooks，掌握函数式组件开发，提升前端开发效率。',
    coverImage: 'https://picsum.photos/400/225?random=3',
    price: 149,
    category: {
      id: 1,
      name: '前端开发'
    },
    teacher: {
      id: 3,
      name: '王老师',
      avatar: '',
      title: '前端架构师',
      introduction: '拥有7年前端开发经验，专注React技术栈，曾参与多个大型单页应用开发。'
    },
    lessonCount: 18,
    studentCount: 1876,
    rating: 4.9,
    createTime: new Date().toISOString()
  },
  {
    id: 4,
    title: 'JavaScript 高级编程',
    description: '深入学习JavaScript高级特性，包括闭包、原型链、异步编程等核心概念。',
    coverImage: 'https://picsum.photos/400/225?random=4',
    price: 129,
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
    lessonCount: 22,
    studentCount: 4567,
    rating: 4.7,
    createTime: new Date().toISOString()
  },
  {
    id: 5,
    title: 'TypeScript 入门到精通',
    description: '学习TypeScript类型系统和最佳实践，提升代码质量和开发效率。',
    coverImage: 'https://picsum.photos/400/225?random=5',
    price: 149,
    category: {
      id: 1,
      name: '前端开发'
    },
    teacher: {
      id: 2,
      name: '李老师',
      avatar: '',
      title: '高级后端工程师',
      introduction: '拥有10年Java开发经验，精通Spring生态系统，曾主导多个大型企业级项目架构设计。'
    },
    lessonCount: 20,
    studentCount: 2345,
    rating: 4.8,
    createTime: new Date().toISOString()
  },
  {
    id: 6,
    title: 'HTML5与CSS3实战',
    description: '学习HTML5与CSS3高级特性，构建现代化网页。',
    coverImage: 'https://picsum.photos/400/225?random=6',
    price: 99,
    category: {
      id: 1,
      name: '前端开发'
    },
    teacher: {
      id: 3,
      name: '王老师',
      avatar: '',
      title: '前端架构师',
      introduction: '拥有7年前端开发经验，专注React技术栈，曾参与多个大型单页应用开发。'
    },
    lessonCount: 15,
    studentCount: 4567,
    rating: 4.6,
    createTime: new Date().toISOString()
  },
  {
    id: 7,
    title: '前端性能优化',
    description: '学习前端性能优化技巧，提升网站加载速度和用户体验。',
    coverImage: 'https://picsum.photos/400/225?random=7',
    price: 179,
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
    lessonCount: 18,
    studentCount: 1234,
    rating: 4.7,
    createTime: new Date().toISOString()
  },
  {
    id: 8,
    title: 'Vue 3 组件库开发',
    description: '学习如何开发自己的Vue 3组件库，提升组件化开发能力。',
    coverImage: 'https://picsum.photos/400/225?random=8',
    price: 199,
    category: {
      id: 1,
      name: '前端开发'
    },
    teacher: {
      id: 2,
      name: '李老师',
      avatar: '',
      title: '高级后端工程师',
      introduction: '拥有10年Java开发经验，精通Spring生态系统，曾主导多个大型企业级项目架构设计。'
    },
    lessonCount: 25,
    studentCount: 1890,
    rating: 4.8,
    createTime: new Date().toISOString()
  },
  {
    id: 9,
    title: 'Spring Cloud 微服务架构',
    description: '学习Spring Cloud微服务架构设计与实现，掌握分布式系统开发技术。',
    coverImage: 'https://picsum.photos/400/225?random=9',
    price: 299,
    category: {
      id: 2,
      name: '后端开发'
    },
    teacher: {
      id: 3,
      name: '王老师',
      avatar: '',
      title: '前端架构师',
      introduction: '拥有7年前端开发经验，专注React技术栈，曾参与多个大型单页应用开发。'
    },
    lessonCount: 35,
    studentCount: 2345,
    rating: 4.8,
    createTime: new Date().toISOString()
  },
  {
    id: 10,
    title: 'Node.js 后端开发',
    description: '学习Node.js后端开发技术，构建高性能服务。',
    coverImage: 'https://picsum.photos/400/225?random=10',
    price: 169,
    category: {
      id: 2,
      name: '后端开发'
    },
    teacher: {
      id: 1,
      name: '张老师',
      avatar: '',
      title: '高级前端工程师',
      introduction: '拥有8年前端开发经验，专注Vue生态技术栈，曾参与多个大型企业级项目开发。'
    },
    lessonCount: 24,
    studentCount: 2789,
    rating: 4.7,
    createTime: new Date().toISOString()
  },
  {
    id: 11,
    title: 'Python 后端开发',
    description: '学习Python后端开发框架，如Django和Flask。',
    coverImage: 'https://picsum.photos/400/225?random=11',
    price: 159,
    category: {
      id: 2,
      name: '后端开发'
    },
    teacher: {
      id: 2,
      name: '李老师',
      avatar: '',
      title: '高级后端工程师',
      introduction: '拥有10年Java开发经验，精通Spring生态系统，曾主导多个大型企业级项目架构设计。'
    },
    lessonCount: 22,
    studentCount: 3456,
    rating: 4.6,
    createTime: new Date().toISOString()
  },
  {
    id: 12,
    title: 'Go 语言实战',
    description: '学习Go语言的核心特性和实战应用。',
    coverImage: 'https://picsum.photos/400/225?random=12',
    price: 189,
    category: {
      id: 2,
      name: '后端开发'
    },
    teacher: {
      id: 3,
      name: '王老师',
      avatar: '',
      title: '前端架构师',
      introduction: '拥有7年前端开发经验，专注React技术栈，曾参与多个大型单页应用开发。'
    },
    lessonCount: 20,
    studentCount: 1987,
    rating: 4.7,
    createTime: new Date().toISOString()
  },
  {
    id: 13,
    title: '微服务架构设计',
    description: '学习微服务架构的设计原则和最佳实践。',
    coverImage: 'https://picsum.photos/400/225?random=13',
    price: 249,
    category: {
      id: 2,
      name: '后端开发'
    },
    teacher: {
      id: 1,
      name: '张老师',
      avatar: '',
      title: '高级前端工程师',
      introduction: '拥有8年前端开发经验，专注Vue生态技术栈，曾参与多个大型企业级项目架构设计。'
    },
    lessonCount: 28,
    studentCount: 1654,
    rating: 4.8,
    createTime: new Date().toISOString()
  },
  {
    id: 14,
    title: 'Flutter 跨平台开发',
    description: '学习Flutter跨平台应用开发，一次编写多端运行。',
    coverImage: 'https://picsum.photos/400/225?random=14',
    price: 249,
    category: {
      id: 3,
      name: '移动开发'
    },
    teacher: {
      id: 2,
      name: '李老师',
      avatar: '',
      title: '高级后端工程师',
      introduction: '拥有10年Java开发经验，精通Spring生态系统，曾主导多个大型企业级项目架构设计。'
    },
    lessonCount: 30,
    studentCount: 2789,
    rating: 4.7,
    createTime: new Date().toISOString()
  },
  {
    id: 15,
    title: 'React Native 开发',
    description: '学习React Native移动应用开发。',
    coverImage: 'https://picsum.photos/400/225?random=15',
    price: 229,
    category: {
      id: 3,
      name: '移动开发'
    },
    teacher: {
      id: 3,
      name: '王老师',
      avatar: '',
      title: '前端架构师',
      introduction: '拥有7年前端开发经验，专注React技术栈，曾参与多个大型单页应用开发。'
    },
    lessonCount: 28,
    studentCount: 2345,
    rating: 4.6,
    createTime: new Date().toISOString()
  },
  {
    id: 16,
    title: 'iOS 开发基础',
    description: '学习iOS应用开发的基础知识和技能。',
    coverImage: 'https://picsum.photos/400/225?random=16',
    price: 199,
    category: {
      id: 3,
      name: '移动开发'
    },
    teacher: {
      id: 1,
      name: '张老师',
      avatar: '',
      title: '高级前端工程师',
      introduction: '拥有8年前端开发经验，专注Vue生态技术栈，曾参与多个大型企业级项目开发。'
    },
    lessonCount: 25,
    studentCount: 1890,
    rating: 4.7,
    createTime: new Date().toISOString()
  },
  {
    id: 17,
    title: 'Android 开发入门',
    description: '学习Android应用开发的入门知识。',
    coverImage: 'https://picsum.photos/400/225?random=17',
    price: 189,
    category: {
      id: 3,
      name: '移动开发'
    },
    teacher: {
      id: 2,
      name: '李老师',
      avatar: '',
      title: '高级后端工程师',
      introduction: '拥有10年Java开发经验，精通Spring生态系统，曾主导多个大型企业级项目架构设计。'
    },
    lessonCount: 22,
    studentCount: 2134,
    rating: 4.6,
    createTime: new Date().toISOString()
  },
  {
    id: 18,
    title: '移动应用UI设计',
    description: '学习移动应用的UI设计原则和实践。',
    coverImage: 'https://picsum.photos/400/225?random=18',
    price: 159,
    category: {
      id: 3,
      name: '移动开发'
    },
    teacher: {
      id: 3,
      name: '王老师',
      avatar: '',
      title: '前端架构师',
      introduction: '拥有7年前端开发经验，专注React技术栈，曾参与多个大型单页应用开发。'
    },
    lessonCount: 18,
    studentCount: 1654,
    rating: 4.8,
    createTime: new Date().toISOString()
  },
  {
    id: 19,
    title: 'MySQL 数据库优化',
    description: '学习MySQL数据库性能优化技巧。',
    coverImage: 'https://picsum.photos/400/225?random=19',
    price: 149,
    category: {
      id: 4,
      name: '数据库'
    },
    teacher: {
      id: 1,
      name: '张老师',
      avatar: '',
      title: '高级前端工程师',
      introduction: '拥有8年前端开发经验，专注Vue生态技术栈，曾参与多个大型企业级项目开发。'
    },
    lessonCount: 18,
    studentCount: 1987,
    rating: 4.9,
    createTime: new Date().toISOString()
  },
  {
    id: 20,
    title: 'PostgreSQL 高级特性',
    description: '学习PostgreSQL数据库的高级特性和应用。',
    coverImage: 'https://picsum.photos/400/225?random=20',
    price: 169,
    category: {
      id: 4,
      name: '数据库'
    },
    teacher: {
      id: 2,
      name: '李老师',
      avatar: '',
      title: '高级后端工程师',
      introduction: '拥有10年Java开发经验，精通Spring生态系统，曾主导多个大型企业级项目架构设计。'
    },
    lessonCount: 20,
    studentCount: 1234,
    rating: 4.7,
    createTime: new Date().toISOString()
  },
  {
    id: 21,
    title: 'MongoDB 实战',
    description: '学习MongoDB非关系型数据库的应用。',
    coverImage: 'https://picsum.photos/400/225?random=21',
    price: 159,
    category: {
      id: 4,
      name: '数据库'
    },
    teacher: {
      id: 3,
      name: '王老师',
      avatar: '',
      title: '前端架构师',
      introduction: '拥有7年前端开发经验，专注React技术栈，曾参与多个大型单页应用开发。'
    },
    lessonCount: 19,
    studentCount: 1567,
    rating: 4.6,
    createTime: new Date().toISOString()
  },
  {
    id: 22,
    title: '数据库设计与建模',
    description: '学习数据库设计的原则和建模技巧。',
    coverImage: 'https://picsum.photos/400/225?random=22',
    price: 179,
    category: {
      id: 4,
      name: '数据库'
    },
    teacher: {
      id: 1,
      name: '张老师',
      avatar: '',
      title: '高级前端工程师',
      introduction: '拥有8年前端开发经验，专注Vue生态技术栈，曾参与多个大型企业级项目开发。'
    },
    lessonCount: 22,
    studentCount: 1345,
    rating: 4.8,
    createTime: new Date().toISOString()
  },
  {
    id: 23,
    title: 'Redis 缓存技术',
    description: '学习Redis缓存技术的应用和最佳实践。',
    coverImage: 'https://picsum.photos/400/225?random=23',
    price: 149,
    category: {
      id: 4,
      name: '数据库'
    },
    teacher: {
      id: 2,
      name: '李老师',
      avatar: '',
      title: '高级后端工程师',
      introduction: '拥有10年Java开发经验，精通Spring生态系统，曾主导多个大型企业级项目架构设计。'
    },
    lessonCount: 16,
    studentCount: 1789,
    rating: 4.7,
    createTime: new Date().toISOString()
  }
]

// 模拟章节数据
const mockChapters: Record<number, Chapter[]> = {
  1: [
    {
      id: 1,
      title: 'Vue 3 基础入门',
      chapterNumber: 1,
      lessons: [
        {
          id: 1,
          title: 'Vue 3 简介与环境搭建',
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
          duration: 930,
          lessonNumber: 1,
          isFree: true
        },
        {
          id: 2,
          title: 'Vue 3 响应式系统原理',
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
          duration: 1335,
          lessonNumber: 2,
          isFree: true
        },
        {
          id: 3,
          title: '模板语法与指令',
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
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
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
          duration: 1125,
          lessonNumber: 1,
          isFree: false
        },
        {
          id: 5,
          title: '组件通信方式',
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
          duration: 1800,
          lessonNumber: 2,
          isFree: false
        }
      ]
    }
  ],
  2: [
    {
      id: 3,
      title: 'Spring Boot 核心概念',
      chapterNumber: 1,
      lessons: [
        {
          id: 6,
          title: 'Spring Boot 简介与快速入门',
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
          duration: 1200,
          lessonNumber: 1,
          isFree: true
        },
        {
          id: 7,
          title: 'Spring Boot 自动配置原理',
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
          duration: 1500,
          lessonNumber: 2,
          isFree: true
        }
      ]
    }
  ],
  3: [
    {
      id: 4,
      title: 'React Hooks 基础',
      chapterNumber: 1,
      lessons: [
        {
          id: 8,
          title: 'React Hooks 简介',
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
          duration: 900,
          lessonNumber: 1,
          isFree: true
        },
        {
          id: 9,
          title: 'useState 与 useEffect',
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
          duration: 1200,
          lessonNumber: 2,
          isFree: true
        }
      ]
    }
  ],
  13: [
    {
      id: 5,
      title: '微服务架构基础',
      chapterNumber: 1,
      lessons: [
        {
          id: 10,
          title: '微服务架构概述',
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
          duration: 1800,
          lessonNumber: 1,
          isFree: true
        },
        {
          id: 11,
          title: '微服务设计原则',
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
          duration: 2400,
          lessonNumber: 2,
          isFree: true
        },
        {
          id: 12,
          title: '服务注册与发现',
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
          duration: 3000,
          lessonNumber: 3,
          isFree: false
        },
        {
          id: 13,
          title: '配置中心',
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
          duration: 2700,
          lessonNumber: 4,
          isFree: false
        },
        {
          id: 14,
          title: '服务熔断与限流',
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
          duration: 3600,
          lessonNumber: 5,
          isFree: false
        }
      ]
    },
    {
      id: 6,
      title: 'Spring Cloud 实战',
      chapterNumber: 2,
      lessons: [
        {
          id: 15,
          title: 'Spring Cloud 简介',
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
          duration: 2100,
          lessonNumber: 1,
          isFree: false
        },
        {
          id: 16,
          title: 'Eureka 服务注册中心',
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
          duration: 3300,
          lessonNumber: 2,
          isFree: false
        },
        {
          id: 17,
          title: 'Ribbon 负载均衡',
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
          duration: 2700,
          lessonNumber: 3,
          isFree: false
        },
        {
          id: 18,
          title: 'Feign 服务调用',
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
          duration: 3000,
          lessonNumber: 4,
          isFree: false
        },
        {
          id: 19,
          title: 'Hystrix 熔断器',
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
          duration: 3600,
          lessonNumber: 5,
          isFree: false
        }
      ]
    }
  ]
}

// 获取课程数据
const getCourseById = (id: number): Course | undefined => {
  const course = mockCourses.find(course => course.id === id)
  // 如果找不到对应ID的课程，返回第一个课程数据
  return course || mockCourses[0]
}

// 获取章节数据
const getChaptersByCourseId = (courseId: number): Chapter[] => {
  return mockChapters[courseId] || [
    {
      id: 1,
      title: '课程介绍',
      chapterNumber: 1,
      lessons: [
        {
          id: 1,
          title: '课程概述',
          videoUrl: '/videos/Vue3极简2025版教程.mp4',
          duration: 600,
          lessonNumber: 1,
          isFree: true
        }
      ]
    }
  ]
}

const comments = ref<CommentItem[]>([])

// 加载评论列表
const loadComments = async () => {
  try {
    const response = await getCommentsByCourseIdApi(courseId.value)
    if (response.code === 200 && response.data) {
      comments.value = response.data
    }
  } catch (error) {
    console.error('获取评论失败:', error)
  }
}

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
  if (videoElement.value) {
    if (isPlaying.value) {
      videoElement.value.pause()
      stopRecordTimer()
    } else {
      videoElement.value.play()
      startRecordTimer()
    }
    isPlaying.value = !isPlaying.value
    ElMessage.info(isPlaying.value ? '开始播放' : '暂停播放')
  }
}

// 上报学习记录
const reportStudyRecord = async () => {
  // 检查用户是否登录
  const token = localStorage.getItem('token')
  console.log('用户登录状态:', !!token)
  if (!token) {
    console.log('用户未登录，跳过上报')
    return
  }
  
  if (!currentLesson.value) {
    console.error('上报学习记录失败: currentLesson 为 null')
    return
  }
  if (!course.value) {
    console.error('上报学习记录失败: course 为 null')
    return
  }
  if (currentTime.value < 10) {
    console.log('学习时间过短，跳过上报')
    return
  }
  try {
    // 将秒数转换为分钟数
    const durationInMinutes = Math.max(1, Math.round(currentTime.value / 60))
    const progress = Math.max(1, currentProgress.value)
    const courseId = Number(course.value.id)
    const lessonId = Number(currentLesson.value.id)
    
    // 确保参数类型正确
    const payload = {
      courseId: courseId,
      lessonId: lessonId,
      duration: durationInMinutes,
      progress: progress
    }
    
    console.log('上报学习记录:', payload)
    console.log('参数类型:', {
      courseId: typeof payload.courseId,
      lessonId: typeof payload.lessonId,
      duration: typeof payload.duration,
      progress: typeof payload.progress
    })
    
    // 确保所有参数都有值（注意：0 是有效的ID值）
    if (payload.courseId === undefined || payload.courseId === null || 
        payload.lessonId === undefined || payload.lessonId === null || 
        payload.duration === undefined || payload.duration === null || 
        payload.progress === undefined || payload.progress === null) {
      console.error('上报学习记录失败: 参数不完整')
      return
    }
    
    console.log('准备发送学习记录请求')
    const response = await addStudyRecord(payload)
    console.log('上报学习记录成功:', response)
  } catch (error: any) {
    console.error('上报学习记录失败:', error)
    console.error('错误详情:', error.response?.data)
    console.error('错误状态:', error.response?.status)
    console.error('错误头信息:', error.response?.headers)
  }
}

// 开始记录定时器
const startRecordTimer = () => {
  // 每30秒上报一次学习记录
  recordTimer.value = window.setInterval(() => {
    reportStudyRecord()
  }, 30000)
}

// 停止记录定时器
const stopRecordTimer = () => {
  if (recordTimer.value) {
    clearInterval(recordTimer.value)
    recordTimer.value = null
  }
}

const updateTime = () => {
  if (videoElement.value && currentLesson.value) {
    currentTime.value = videoElement.value.currentTime
    // 防止除以零
    const duration = currentLesson.value.duration || 1
    currentProgress.value = Math.round((currentTime.value / duration) * 100)
  }
}

const handleEnded = async () => {
  isPlaying.value = false
  stopRecordTimer()
  // 视频结束时上报学习记录
  await reportStudyRecord()
  ElMessage.info('视频播放结束')
}

const seek = (seconds: number) => {
  currentTime.value = Math.max(0, Math.min(currentLesson.value?.duration || 0, currentTime.value + seconds))
  ElMessage.info(`快${seconds > 0 ? '进' : '退'}${Math.abs(seconds)}秒`)
}

const seekVideo = (value: number) => {
  const roundedValue = Math.round(value) // 不保留小数
  if (videoElement.value) {
    videoElement.value.currentTime = roundedValue
  }
  currentTime.value = roundedValue
  ElMessage.info(`跳转到 ${formatDuration(roundedValue)}`)
}

const setPlaybackRate = (rate: string) => {
  playbackRate.value = rate
  if (videoElement.value) {
    videoElement.value.playbackRate = parseFloat(rate)
  }
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

// 监听全屏状态变化
const handleFullscreenChange = () => {
  const isFullscreen = !!document.fullscreenElement
  if (isFullscreen) {
    // 全屏时确保控制栏可见
    const controls = videoPlayer.value?.querySelector('.video-controls-bottom')
    if (controls) {
      controls.style.display = 'block'
    }
  }
}

// 添加全屏变化监听器
onMounted(() => {
  const id = courseId.value
  
  // 从后端获取课程详情
  getCourseDetailApi(id).then(response => {
    console.log('=== 课程详情API响应 ===')
    console.log('完整响应:', response)
    console.log('response.data:', response?.data)
    console.log('response.data.id:', response?.data?.id)
    console.log('条件判断结果:', response && response.data && response.data.id)
    
    // response 已经是 ApiResult 对象，response.data 是课程数据
    if (response && response.data && response.data.id) {
      course.value = response.data
      dataSource.value = 'backend' // 设置数据源为后端
      console.log('✅ 使用后端真实数据:', course.value.title)
    } else {
      // 如果后端没有数据，使用模拟数据
      const courseData = getCourseById(id)
      if (courseData) {
        course.value = courseData
        dataSource.value = 'mock' // 设置数据源为模拟数据
        console.log('⚠️ 使用模拟数据:', courseData.title)
      } else {
        console.log('❌ 没有找到任何数据')
      }
    }
  }).catch(error => {
    console.error('获取课程详情失败:', error)
    // 使用模拟数据作为后备
    const courseData = getCourseById(id)
    if (courseData) {
      course.value = courseData
      dataSource.value = 'mock' // 设置数据源为模拟数据
      console.log('⚠️ 请求失败，使用模拟数据:', courseData.title)
    }
  })
  
  // 从后端获取章节和课时
  getCourseChaptersApi(id).then(response => {
    // response 已经是 ApiResult 对象，response.data 是章节数据
    if (response && response.data && response.data.length > 0) {
      chapters.value = response.data
      console.log('获取章节数据成功:', chapters.value)
      
      // 加载课程学习进度
      loadLearningProgress(id)
      
      if (chapters.value.length > 0) {
        activeChapter.value = chapters.value[0].id
        // 默认选择第一个免费课程
        const firstFreeLesson = chapters.value[0].lessons.find(lesson => lesson.isFree)
        if (firstFreeLesson) {
          currentLesson.value = firstFreeLesson
          // 加载课时进度
          loadLessonProgress(id, firstFreeLesson.id).then(savedTime => {
            currentTime.value = savedTime
            console.log(`初始化课时 ${firstFreeLesson.id}: 加载进度 ${savedTime}秒`)
            // 将进度应用到视频播放器上
            nextTick(() => {
              if (videoElement.value) {
                const setCurrentTime = () => {
                  videoElement.value.currentTime = currentTime.value
                  console.log(`视频进度已设置为: ${currentTime.value}秒`)
                }
                if (videoElement.value.readyState >= 2) {
                  setCurrentTime()
                } else {
                  videoElement.value.addEventListener('loadedmetadata', setCurrentTime, { once: true })
                }
              }
            })
          })
        }
      }
    } else {
      // 如果后端没有数据，使用模拟数据
      const chaptersData = getChaptersByCourseId(id)
      chapters.value = chaptersData
      
      if (chaptersData.length > 0) {
        activeChapter.value = chaptersData[0].id
        const firstFreeLesson = chaptersData[0].lessons.find(lesson => lesson.isFree)
        if (firstFreeLesson) {
          currentLesson.value = firstFreeLesson
          loadLessonProgress(id, firstFreeLesson.id).then(savedTime => {
            currentTime.value = savedTime
            console.log(`初始化课时 ${firstFreeLesson.id}: 加载进度 ${savedTime}秒`)
            // 将进度应用到视频播放器上
            nextTick(() => {
              if (videoElement.value) {
                const setCurrentTime = () => {
                  videoElement.value.currentTime = currentTime.value
                  console.log(`视频进度已设置为: ${currentTime.value}秒`)
                }
                if (videoElement.value.readyState >= 2) {
                  setCurrentTime()
                } else {
                  videoElement.value.addEventListener('loadedmetadata', setCurrentTime, { once: true })
                }
              }
            })
          })
        }
      }
    }
    startProgressTimer()
  }).catch(error => {
    console.error('获取章节数据失败:', error)
    // 使用模拟数据作为后备
    const chaptersData = getChaptersByCourseId(id)
    chapters.value = chaptersData
    
    if (chaptersData.length > 0) {
      activeChapter.value = chaptersData[0].id
      const firstFreeLesson = chaptersData[0].lessons.find(lesson => lesson.isFree)
      if (firstFreeLesson) {
        currentLesson.value = firstFreeLesson
        loadLessonProgress(id, firstFreeLesson.id).then(savedTime => {
          currentTime.value = savedTime
          // 将进度应用到视频播放器上
          if (videoElement.value) {
            videoElement.value.currentTime = currentTime.value
          }
        })
      }
    }
    startProgressTimer()
  })
  
  // 加载评论列表
  loadComments()
  
  // 添加全局键盘监听
  window.addEventListener('keydown', (e) => {
    if (e.target instanceof HTMLInputElement || e.target instanceof HTMLTextAreaElement) {
      return // 如果焦点在输入框内，不处理快捷键
    }
    handleKeyDown(e)
  })
  
  // 添加全屏变化监听器
  document.addEventListener('fullscreenchange', handleFullscreenChange)
  
  // 添加页面离开时的保存监听
  window.addEventListener('beforeunload', handleBeforeUnload)
  window.addEventListener('popstate', handleBeforeUnload)
  
  // 加载收藏状态
  loadFavoriteStatus()
})

// 页面离开时保存进度
const handleBeforeUnload = async () => {
  if (course.value && currentLesson.value && videoElement.value) {
    await saveLessonProgress(course.value.id, currentLesson.value.id, videoElement.value.currentTime)
    saveLearningProgress(course.value.id)
  }
}

onUnmounted(() => {
  stopProgressTimer()
  stopRecordTimer()
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
  window.removeEventListener('beforeunload', handleBeforeUnload)
  window.removeEventListener('popstate', handleBeforeUnload)
})

const togglePictureInPicture = () => {
  ElMessage.info('切换画中画模式')
}

// 学习互动功能
const toggleFavorite = async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    if (isFavorite.value) {
      const response = await removeFavoriteApi(courseId.value)
      if (response.code === 200) {
        isFavorite.value = false
        ElMessage.success('取消收藏成功')
      }
    } else {
      const response = await addFavoriteApi(courseId.value)
      if (response.code === 200) {
        isFavorite.value = true
        ElMessage.success('收藏成功')
      }
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 加载收藏状态
const loadFavoriteStatus = async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    isFavorite.value = false
    return
  }
  
  try {
    const response = await getFavoriteStatusApi(courseId.value)
    if (response.code === 200) {
      isFavorite.value = response.data?.isFavorite || false
    }
  } catch (error) {
    console.error('获取收藏状态失败:', error)
    isFavorite.value = false
  }
}

const shareCourse = () => {
  ElMessage.success('分享链接已复制到剪贴板')
}

// 章节状态相关函数
const isLessonCompleted = (lesson: Lesson): boolean => {
  if (!course.value) return false
  
  // 从本地存储获取已完成的课时
  const storedProgress = localStorage.getItem(`completed_lessons_${course.value.id}`)
  if (storedProgress) {
    try {
      const completedLessonIds = JSON.parse(storedProgress)
      return completedLessonIds.includes(lesson.id)
    } catch (error) {
      console.error('加载已完成课时失败:', error)
    }
  }
  
  // 检查当前进度是否达到90%以上
  if (currentLesson.value?.id === lesson.id) {
    return currentProgress.value >= 90
  }
  
  return false
}

// 标记课时为已完成
const markLessonAsCompleted = (courseId: number, lessonId: number) => {
  const storedProgress = localStorage.getItem(`completed_lessons_${courseId}`)
  const completedLessonIds = storedProgress ? JSON.parse(storedProgress) : []
  
  if (!completedLessonIds.includes(lessonId)) {
    completedLessonIds.push(lessonId)
    localStorage.setItem(`completed_lessons_${courseId}`, JSON.stringify(completedLessonIds))
  }
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
const submitComment = async () => {
  if (!newComment.value.trim()) return
  
  try {
    const response = await addCommentApi(courseId.value, newComment.value)
    if (response.code === 200) {
      await loadComments()
      newComment.value = ''
      ElMessage.success('评论发布成功')
    } else {
      ElMessage.error('评论失败')
    }
  } catch (error) {
    console.error('提交评论失败:', error)
    ElMessage.error('评论失败')
  }
}

const likeComment = async (commentId: number) => {
  try {
    const response = await likeCommentApi(commentId)
    if (response.code === 200) {
      const comment = comments.value.find(c => c.id === commentId)
      if (comment) {
        comment.likes += 1
      }
    }
  } catch (error) {
    console.error('点赞失败:', error)
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
  const remainingSeconds = Math.round(seconds % 60) // 不保留小数
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
const selectLesson = async (lesson: Lesson, chapter: Chapter) => {
  currentLesson.value = lesson
  activeChapter.value = chapter.id
  
  // 加载该课时的保存进度
  if (course.value) {
    console.log(`=== 选择课时 ===`)
    console.log(`课程ID: ${course.value.id}`)
    console.log(`课程名称: ${course.value.title}`)
    console.log(`课时ID: ${lesson.id}`)
    console.log(`课时序号(lessonNumber): ${lesson.lessonNumber}`)
    console.log(`课时名称: ${lesson.title}`)
    
    // 验证课时ID是否有效
    if (!lesson.id || lesson.id <= 0) {
      console.error('课时ID无效:', lesson.id)
      ElMessage.error('课时数据异常，请刷新页面')
      return
    }
    
    const savedTime = await loadLessonProgress(course.value.id, lesson.id)
    currentTime.value = savedTime
    console.log(`加载进度: ${savedTime}秒`)
    
    // 等待视频加载完成后再设置进度
    nextTick(() => {
      if (videoElement.value) {
        // 等待视频元数据加载完成
        const setCurrentTime = () => {
          videoElement.value.currentTime = currentTime.value
          console.log(`视频进度已设置为: ${currentTime.value}秒`)
        }
        
        if (videoElement.value.readyState >= 2) {
          setCurrentTime()
        } else {
          videoElement.value.addEventListener('loadedmetadata', setCurrentTime, { once: true })
        }
      }
    })
  }
  
  ElMessage.success(`开始学习: ${lesson.title}`)
}

// 自动播放进度更新
let progressInterval: number | null = null

const startProgressTimer = () => {
  if (progressInterval) clearInterval(progressInterval)
  
  progressInterval = window.setInterval(async () => {
    if (isPlaying.value && currentLesson.value && course.value) {
      const duration = currentLesson.value.duration || 1
      currentTime.value = Math.min(duration, currentTime.value + 1)
      currentProgress.value = Math.round((currentTime.value / duration) * 100)
      
      // 模拟学习时长增加
      if (currentTime.value % 60 === 0) {
        totalStudyTime.value += 60
        // 每60秒保存一次学习进度
        saveLearningProgress(course.value.id)
      }
      
      // 每10秒保存一次课时进度
      if (currentTime.value % 10 === 0) {
        await saveLessonProgress(course.value.id, currentLesson.value.id, currentTime.value)
      }
      
      // 课时完成判断
      if (currentTime.value >= currentLesson.value.duration * 0.9) { // 观看90%以上算完成
        // 标记为已完成
        if (!isLessonCompleted(currentLesson.value)) {
          completedLessons.value++
          markLessonAsCompleted(course.value.id, currentLesson.value.id)
          saveLearningProgress(course.value.id)
        }
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
  grid-template-columns: 3fr 1fr;
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

.bilibili-player {
  width: 100%;
  position: relative;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
  aspect-ratio: 16/9;
  min-width: 600px;
  max-width: 100%;
  margin: 0 auto;
  min-height: 450px;
}

.video-area {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.video-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  text-align: center;
  padding: 20px;
}

.video-placeholder p {
  margin-top: 20px;
  font-size: 16px;
}

.player-main {
  width: 100%;
  height: 100%;
  position: relative;
  display: flex;
  flex-direction: column;
}

.player-video {
  flex: 1;
  position: relative;
  background: #000;
  overflow: hidden;
}

.video-element {
  width: 100%;
  height: 100%;
  object-fit: cover;
}



/* 视频标题覆盖层 */
.video-title-overlay {
  position: absolute;
  bottom: 80px;
  left: 15px;
  color: white;
  z-index: 5;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.8);
}

.video-title-overlay h4 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

/* 底部控制栏 */
.video-controls-bottom {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 15px 20px;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(5px);
  transition: all 0.3s ease;
  z-index: 9999;
  min-height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  box-sizing: border-box;
  transform: translateY(100%);
}

.progress-bar {
  width: 100%;
  margin-bottom: 10px;
  position: relative;
  z-index: 1;
}

.player-video:hover .video-controls-bottom {
  transform: translateY(0);
}

/* 全屏模式下的控制栏 */
:-webkit-full-screen .video-controls-bottom,
:-moz-full-screen .video-controls-bottom,
:-ms-fullscreen .video-controls-bottom,
:fullscreen .video-controls-bottom {
  width: 100% !important;
  padding: 15px 20px !important;
  min-height: 130px !important;
  bottom: 0 !important;
  opacity: 1 !important;
  pointer-events: auto !important;
  z-index: 9999 !important;
  transform: translateY(0) !important;
  position: fixed !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  display: flex !important;
  flex-direction: column !important;
  align-items: center !important;
  justify-content: space-between !important;
}

:-webkit-full-screen .progress-bar,
:-moz-full-screen .progress-bar,
:-ms-fullscreen .progress-bar,
:fullscreen .progress-bar {
  width: 100% !important;
  max-width: 100% !important;
  margin-bottom: 10px !important;
  position: relative !important;
  z-index: 1 !important;
}

:-webkit-full-screen .control-row,
:-moz-full-screen .control-row,
:-ms-fullscreen .control-row,
:fullscreen .control-row {
  max-width: 100% !important;
  width: 100% !important;
  margin: 0 !important;
  flex-wrap: wrap !important;
  gap: 15px !important;
  padding: 0 20px !important;
  box-sizing: border-box !important;
  display: flex !important;
  align-items: center !important;
  justify-content: space-between !important;
  flex: 1 !important;
}

/* 全屏模式下的播放器容器 */
:-webkit-full-screen .bilibili-player,
:-moz-full-screen .bilibili-player,
:-ms-fullscreen .bilibili-player,
:fullscreen .bilibili-player {
  width: 100vw !important;
  height: 100vh !important;
  max-width: 100vw !important;
  min-width: 100vw !important;
  min-height: 100vh !important;
  border-radius: 0 !important;
  margin: 0 !important;
  position: relative !important;
  z-index: 1 !important;
}

/* 全屏模式下的视频元素 */
:-webkit-full-screen .video-element,
:-moz-full-screen .video-element,
:-ms-fullscreen .video-element,
:fullscreen .video-element {
  width: 100% !important;
  height: 100% !important;
  object-fit: contain !important;
}

/* 全屏模式下的左右控制栏 */
:-webkit-full-screen .left-controls,
:-moz-full-screen .left-controls,
:-ms-fullscreen .left-controls,
:fullscreen .left-controls {
  flex-shrink: 1 !important;
  min-width: 200px !important;
}

:-webkit-full-screen .right-controls,
:-moz-full-screen .right-controls,
:-ms-fullscreen .right-controls,
:fullscreen .right-controls {
  flex-shrink: 1 !important;
  min-width: 150px !important;
}

/* 全屏模式下的进度条 */
:-webkit-full-screen .progress-bar,
:-moz-full-screen .progress-bar,
:-ms-fullscreen .progress-bar,
:fullscreen .progress-bar {
  width: 100% !important;
  max-width: 100% !important;
  margin-bottom: 15px !important;
}

/* 全屏模式下的播放器容器 */
:-webkit-full-screen .bilibili-player,
:-moz-full-screen .bilibili-player,
:-ms-fullscreen .bilibili-player,
:fullscreen .bilibili-player {
  width: 100vw;
  height: 100vh;
  max-width: 100vw;
  min-width: 100vw;
  min-height: 100vh;
  border-radius: 0;
  margin: 0;
}

/* 全屏模式下的视频元素 */
:-webkit-full-screen .video-element,
:-moz-full-screen .video-element,
:-ms-fullscreen .video-element,
:fullscreen .video-element {
  width: 100%;
  height: 100%;
}

.progress-bar {
  margin-bottom: 8px;
  position: relative;
  z-index: 1;
}

.control-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  flex-wrap: nowrap;
  width: 100%;
  flex: 1;
}

.left-controls {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-shrink: 0;
}

.center-controls {
  display: flex;
  align-items: center;
  gap: 15px;
  flex: 1;
  justify-content: center;
  min-width: 300px;
}

.right-controls {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-shrink: 0;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .control-row {
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .center-controls {
    order: 3;
    width: 100%;
    justify-content: center;
    min-width: unset;
  }
  
  .left-controls,
  .right-controls {
    flex: 1;
  }
  
  .right-controls {
    justify-content: flex-end;
  }
}

.time-display {
  color: white;
  font-size: 14px;
  margin-left: 10px;
}

/* 收藏图标样式 */
.el-icon .filled,
.el-icon.filled {
  color: #ffd700;
}

/* 播放按钮覆盖层 */
.play-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 5;
  transition: all 0.3s ease;
}

.play-overlay .el-button {
  background: rgba(255, 255, 255, 0.9) !important;
  border-color: transparent !important;
  width: 80px !important;
  height: 80px !important;
  min-width: 80px !important;
  min-height: 80px !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.play-overlay .el-button:hover {
  background: rgba(255, 255, 255, 1) !important;
  transform: scale(1.1);
}

/* 控制按钮样式 */
.control-row .el-button {
  color: white !important;
  border-color: rgba(255, 255, 255, 0.3) !important;
  background: rgba(255, 255, 255, 0.1) !important;
  padding: 8px 12px !important;
  font-size: 14px !important;
  border-radius: 4px !important;
  transition: all 0.3s ease !important;
}

.control-row .el-button:hover {
  background: rgba(255, 255, 255, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.5) !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 2px 8px rgba(255, 255, 255, 0.2) !important;
}

/* 播放按钮样式 */
.left-controls .el-button {
  width: 40px !important;
  height: 40px !important;
  min-width: 40px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

/* 进度条样式 */
.progress-bar .el-slider__bar {
  background-color: #409eff !important;
  height: 6px !important;
}

.progress-bar .el-slider__button {
  width: 16px !important;
  height: 16px !important;
  border: 2px solid #409eff !important;
  background-color: white !important;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3) !important;
}

.progress-bar .el-slider__runway {
  height: 6px !important;
  background-color: rgba(255, 255, 255, 0.3) !important;
  border-radius: 3px !important;
}

/* 下拉菜单样式 */
.el-dropdown-menu {
  background: rgba(0, 0, 0, 0.9) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
}

.el-dropdown-item {
  color: white !important;
}

.el-dropdown-item:hover {
  background: rgba(255, 255, 255, 0.1) !important;
}

/* B站风格播放按钮 */
.el-button--circle {
  width: 40px !important;
  height: 40px !important;
  min-width: 40px !important;
  min-height: 40px !important;
}

.el-button--large {
  width: 60px !important;
  height: 60px !important;
  min-width: 60px !important;
  min-height: 60px !important;
}

/* 播放按钮样式 */
.play-overlay .el-button {
  background: rgba(255, 255, 255, 0.9) !important;
  border-color: transparent !important;
  width: 60px !important;
  height: 60px !important;
  min-width: 60px !important;
  min-height: 60px !important;
}

.play-overlay .el-button:hover {
  background: rgba(255, 255, 255, 1) !important;
}

/* 顶部控制栏样式 */
.player-controls-top {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: rgba(0, 0, 0, 0.7);
  border-radius: 4px;
  margin-bottom: 12px;
  position: absolute;
  top: 15px;
  right: 15px;
  z-index: 20;
}

.player-controls-top .el-button {
  padding: 4px 8px;
  font-size: 12px;
  height: 28px;
  min-height: 28px;
  border-radius: 4px;
}

.player-controls-top .el-button--primary {
  background: rgba(255, 255, 255, 0.1) !important;
  border-color: rgba(255, 255, 255, 0.2) !important;
  color: white !important;
}

.player-controls-top .el-button--primary:hover {
  background: rgba(255, 255, 255, 0.2) !important;
}

.player-controls-top .el-button--default {
  color: white !important;
  border-color: rgba(255, 255, 255, 0.2) !important;
}

.player-controls-top .el-button--default:hover {
  background: rgba(255, 255, 255, 0.1) !important;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .control-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .right-controls {
    margin-left: auto;
  }
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