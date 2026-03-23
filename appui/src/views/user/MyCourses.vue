<template>
  <div class="my-courses-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>我的课程</h1>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-section">
      <div class="stat-card">
        <div class="stat-number">{{ stats.learningCount }}</div>
        <div class="stat-label">在学课程</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ stats.completedCount }}</div>
        <div class="stat-label">已完成课程</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ formatDuration(stats.totalDuration) }}</div>
        <div class="stat-label">总学习时长</div>
      </div>
    </div>

    <!-- 筛选标签栏 -->
    <div class="filter-section">
      <div 
        v-for="filter in filters" 
        :key="filter.value"
        class="filter-tag"
        :class="{ active: activeFilter === filter.value }"
        @click="activeFilter = filter.value"
      >
        {{ filter.label }}
      </div>
    </div>

    <!-- 课程卡片列表 -->
    <div class="courses-section">
      <div 
        v-for="course in filteredCourses" 
        :key="course.id"
        class="course-card"
      >
        <div class="course-cover">
          <img 
            :src="course.cover_image || course.coverImage || 'https://via.placeholder.com/400x225?text=' + encodeURIComponent(course.title)"
            :alt="course.title"
          />
        </div>
        <div class="course-info">
          <h3 class="course-title">{{ course.title }}</h3>
          
          <!-- 学习进度条 -->
          <div class="progress-section">
            <el-progress 
              :percentage="course.progress" 
              :format="formatProgress" 
              :stroke-width="6"
            />
          </div>
          
          <div class="course-meta">
            <span class="last-study-time">最后学习：{{ course.lastStudyTime }}</span>
          </div>
          
          <button 
            class="continue-btn"
            @click="continueLearning(course)"
          >
            继续学习
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 筛选标签
const filters = [
  { label: '正在学', value: 'learning' },
  { label: '已学完', value: 'completed' },
  { label: '已收藏', value: 'favorited' }
]

// 当前选中的筛选标签
const activeFilter = ref('learning')

// 课程数据
const courses = ref<any[]>([])

// 统计数据
const stats = reactive({
  learningCount: 0,
  completedCount: 0,
  totalDuration: 0 // 分钟
})

// 从本地存储加载课程数据
const loadCoursesFromStorage = () => {
  // 获取所有课程的学习进度
  const courseProgressMap: Record<number, any> = {}
  
  // 首先处理学习进度数据
  for (let i = 0; i < localStorage.length; i++) {
    const key = localStorage.key(i)
    if (key && key.startsWith('learning_progress_')) {
      const courseId = parseInt(key.replace('learning_progress_', ''))
      try {
        const progressData = JSON.parse(localStorage.getItem(key) || '{}')
        
        // 获取课程基本信息
        const courseInfo = getCourseInfo(courseId)
        if (courseInfo) {
          // 获取最后学习时间
          const lastStudyTime = progressData.lastUpdated ? new Date(progressData.lastUpdated).toLocaleString() : '从未学习'
          
          // 计算学习状态
          let status = 'learning'
          if (progressData.currentProgress >= 100) {
            status = 'completed'
          }
          
          // 获取最后播放时间
          const lessonProgressKey = `lesson_progress_${courseId}`
          const lessonProgressData = localStorage.getItem(lessonProgressKey)
          let lastPlayTime = 0
          if (lessonProgressData) {
            try {
              const lessonProgress = JSON.parse(lessonProgressData)
              // 获取最大的播放时间作为最后播放时间
              const playTimes = Object.values(lessonProgress).map(Number)
              if (playTimes.length > 0) {
                lastPlayTime = Math.max(...playTimes)
              }
            } catch (error) {
              console.error('解析课时进度失败:', error)
            }
          }
          
          courseProgressMap[courseId] = {
            id: courseId,
            title: courseInfo.title,
            cover_image: courseInfo.coverImage,
            progress: progressData.currentProgress || 0,
            lastStudyTime,
            status,
            lastPlayTime,
            totalDuration: progressData.totalStudyTime || 0
          }
        }
      } catch (error) {
        console.error('解析学习进度失败:', error)
      }
    }
  }
  
  // 然后处理课程信息数据（确保即使没有学习进度也能显示课程）
  for (let i = 0; i < localStorage.length; i++) {
    const key = localStorage.key(i)
    if (key && key.startsWith('course_info_')) {
      const courseId = parseInt(key.replace('course_info_', ''))
      // 只有当该课程还没有在courseProgressMap中时才添加
      if (!courseProgressMap[courseId]) {
        try {
          const courseInfo = JSON.parse(localStorage.getItem(key) || '{}')
          if (courseInfo.id) {
            courseProgressMap[courseId] = {
              id: courseInfo.id,
              title: courseInfo.title,
              cover_image: courseInfo.coverImage,
              progress: 0,
              lastStudyTime: '从未学习',
              status: 'learning',
              lastPlayTime: 0,
              totalDuration: 0
            }
          }
        } catch (error) {
          console.error('解析课程信息失败:', error)
        }
      }
    }
  }
  
  // 转换为数组
  courses.value = Object.values(courseProgressMap)
  
  // 更新统计数据
  updateStats()
}

// 获取课程基本信息（优先从本地存储获取，其次使用模拟数据）
const getCourseInfo = (courseId: number) => {
  // 先从本地存储获取课程信息
  const courseInfoKey = `course_info_${courseId}`
  const storedCourseInfo = localStorage.getItem(courseInfoKey)
  if (storedCourseInfo) {
    try {
      return JSON.parse(storedCourseInfo)
    } catch (error) {
      console.error('解析课程信息失败:', error)
    }
  }
  
  // 如果本地存储中没有，则使用模拟数据
  const mockCourses = [
    { id: 1, title: 'Vue 3入门教程', coverImage: 'https://picsum.photos/400/225?random=1' },
    { id: 2, title: 'Spring Boot 企业级开发', coverImage: 'https://picsum.photos/400/225?random=2' },
    { id: 3, title: 'React Hooks 完全指南', coverImage: 'https://picsum.photos/400/225?random=3' },
    { id: 4, title: 'JavaScript 高级编程', coverImage: 'https://picsum.photos/400/225?random=4' },
    { id: 5, title: 'TypeScript 入门到精通', coverImage: 'https://picsum.photos/400/225?random=5' },
    { id: 6, title: 'HTML5与CSS3实战', coverImage: 'https://picsum.photos/400/225?random=6' },
    { id: 7, title: '前端性能优化', coverImage: 'https://picsum.photos/400/225?random=7' },
    { id: 8, title: 'Vue 3 组件库开发', coverImage: 'https://picsum.photos/400/225?random=8' },
    { id: 9, title: 'Spring Cloud 微服务架构', coverImage: 'https://picsum.photos/400/225?random=9' },
    { id: 10, title: 'Node.js 后端开发', coverImage: 'https://picsum.photos/400/225?random=10' },
    { id: 11, title: 'Python 后端开发', coverImage: 'https://picsum.photos/400/225?random=11' },
    { id: 12, title: 'Go 语言实战', coverImage: 'https://picsum.photos/400/225?random=12' },
    { id: 13, title: '微服务架构设计', coverImage: 'https://picsum.photos/400/225?random=13' },
    { id: 14, title: 'Flutter 跨平台开发', coverImage: 'https://picsum.photos/400/225?random=14' },
    { id: 15, title: 'React Native 开发', coverImage: 'https://picsum.photos/400/225?random=15' },
    { id: 16, title: 'iOS 开发基础', coverImage: 'https://picsum.photos/400/225?random=16' },
    { id: 17, title: 'Android 开发入门', coverImage: 'https://picsum.photos/400/225?random=17' },
    { id: 18, title: '移动应用UI设计', coverImage: 'https://picsum.photos/400/225?random=18' },
    { id: 19, title: 'MySQL 数据库优化', coverImage: 'https://picsum.photos/400/225?random=19' },
    { id: 20, title: 'PostgreSQL 高级特性', coverImage: 'https://picsum.photos/400/225?random=20' },
    { id: 21, title: 'MongoDB 实战', coverImage: 'https://picsum.photos/400/225?random=21' },
    { id: 22, title: '数据库设计与建模', coverImage: 'https://picsum.photos/400/225?random=22' },
    { id: 23, title: 'Redis 缓存技术', coverImage: 'https://picsum.photos/400/225?random=23' }
  ]
  
  return mockCourses.find(course => course.id === courseId)
}

// 更新统计数据
const updateStats = () => {
  const learningCount = courses.value.filter(course => course.status === 'learning').length
  const completedCount = courses.value.filter(course => course.status === 'completed').length
  const totalDuration = Math.round(courses.value.reduce((total, course) => total + (course.totalDuration || 0), 0) / 60) // 转换为分钟
  
  stats.learningCount = learningCount
  stats.completedCount = completedCount
  stats.totalDuration = totalDuration
}

// 页面加载时从本地存储加载数据
onMounted(() => {
  loadCoursesFromStorage()
})

// 根据筛选标签过滤课程
const filteredCourses = computed(() => {
  return courses.value.filter(course => {
    if (activeFilter.value === 'learning') {
      return course.status === 'learning'
    } else if (activeFilter.value === 'completed') {
      return course.status === 'completed'
    } else if (activeFilter.value === 'favorited') {
      return course.status === 'favorited'
    }
    return true
  })
})

// 格式化学习时长
const formatDuration = (minutes: number): string => {
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  if (hours > 0) {
    return `${hours}小时${mins}分钟`
  }
  return `${mins}分钟`
}

// 格式化进度显示
const formatProgress = (percentage: number): string => {
  return `${percentage}%`
}

// 继续学习
const continueLearning = (course: any) => {
  router.push({
    path: `/course-detail/${course.id}`,
    query: {
      time: course.lastPlayTime
    }
  })
}
</script>

<style scoped>
.my-courses-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.stats-section {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  text-align: center;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #165DFF;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.filter-section {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 10px;
}

.filter-tag {
  padding: 8px 20px;
  border-radius: 20px;
  background: #f5f7fa;
  color: #606266;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
}

.filter-tag:hover {
  background: #e4e7ed;
}

.filter-tag.active {
  background: #165DFF;
  color: #fff;
}

.courses-section {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.course-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 20px 0 rgba(0, 0, 0, 0.15);
}

.course-cover {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.course-card:hover .course-cover img {
  transform: scale(1.05);
}

.course-info {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.course-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
  line-height: 1.4;
}

.progress-section {
  margin-bottom: 12px;
}

.course-meta {
  font-size: 12px;
  color: #909399;
  margin-bottom: 16px;
}

.continue-btn {
  background: #165DFF;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  align-self: flex-start;
}

.continue-btn:hover {
  background: #409EFF;
  transform: translateY(-1px);
}

@media (max-width: 768px) {
  .stats-section {
    flex-direction: column;
  }
  
  .courses-section {
    grid-template-columns: 1fr;
  }
}
</style>