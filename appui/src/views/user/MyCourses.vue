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
        <div class="stat-number">{{ stats.favoritedCount }}</div>
        <div class="stat-label">已收藏课程</div>
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
      <el-skeleton v-if="loading" :rows="6" animated />
      
      <div v-else-if="courses.length === 0" class="empty-state">
        <el-empty description="暂无课程数据" />
      </div>
      
      <div 
        v-for="course in filteredCourses" 
        :key="course.id"
        class="course-card"
      >
        <div class="course-cover">
          <img 
            :src="course.coverImage || course.cover_image || 'https://via.placeholder.com/400x225?text=' + encodeURIComponent(course.title)"
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
import { useUserStore } from '@/store/user'
import { getUserStudyRecords } from '@/api/study'
import { getUserFavoritesApi, type FavoriteItem } from '@/api/favorite'

const router = useRouter()
const userStore = useUserStore()

// 筛选标签
const filters = [
  { label: '正在学', value: 'learning' },
  { label: '已学完', value: 'completed' },
  { label: '已收藏', value: 'favorited' }
]

// 收藏数据
const favorites = ref<FavoriteItem[]>([])

// 当前选中的筛选标签
const activeFilter = ref('learning')

// 课程数据
const courses = ref<any[]>([])

// 统计数据
const stats = reactive({
  learningCount: 0,
  completedCount: 0,
  favoritedCount: 0,
  totalDuration: 0 // 分钟
})

// 加载状态
const loading = ref(false)

// 从后端API加载课程数据
const loadCoursesFromApi = async () => {
  if (!userStore.isLogin) {
    return
  }
  
  loading.value = true
  try {
    // 同时获取学习记录和收藏数据
    const [studyResponse, favoriteResponse] = await Promise.all([
      getUserStudyRecords(),
      getUserFavoritesApi()
    ])
    
    // 按课程ID分组
    const courseMap: Record<number, any> = {}
    
    // 处理学习记录
    if (studyResponse.code === 200 && studyResponse.data) {
      const records = studyResponse.data
      records.forEach((record: any) => {
        if (!courseMap[record.courseId]) {
          const randomSeed = Math.floor(Math.random() * 1000)
          courseMap[record.courseId] = {
            id: record.courseId,
            title: record.courseTitle || '未知课程',
            coverImage: record.courseCover || `https://picsum.photos/400/225?random=${randomSeed}`,
            progress: 0,
            totalDuration: 0,
            lessons: []
          }
        }
        
        courseMap[record.courseId].lessons.push(record)
        courseMap[record.courseId].totalDuration += record.duration || 0
      })
      
      // 计算每个学习课程的进度
      Object.values(courseMap).forEach((course: any) => {
        if (course.lessons.length > 0) {
          const completedLessons = course.lessons.filter((lesson: any) => lesson.progress === 100).length
          course.progress = Math.round((completedLessons / course.lessons.length) * 100)
          
          const lastLesson = course.lessons.sort((a: any, b: any) => 
            new Date(b.studyTime).getTime() - new Date(a.studyTime).getTime()
          )[0]
          course.lastStudyTime = lastLesson ? new Date(lastLesson.studyTime).toLocaleString() : '从未学习'
          
          course.status = course.progress >= 100 ? 'completed' : 'learning'
        } else {
          course.progress = 0
          course.lastStudyTime = '从未学习'
          course.status = 'learning'
        }
      })
    }
    
    // 处理收藏数据
    if (favoriteResponse.code === 200 && favoriteResponse.data) {
      favorites.value = favoriteResponse.data
      
      favoriteResponse.data.forEach((item: any) => {
        const courseId = item.courseId || item.course_id
        if (!courseMap[courseId]) {
          // 如果收藏的课程不在学习记录中，添加到课程列表
          const randomSeed = Math.floor(Math.random() * 1000)
          courseMap[courseId] = {
            id: courseId,
            title: item.courseTitle || item.course_title || '未知课程',
            coverImage: item.courseCover || item.course_cover || `https://picsum.photos/400/225?random=${randomSeed}`,
            progress: 0,
            totalDuration: 0,
            lessons: [],
            lastStudyTime: '从未学习',
            status: 'favorited',
            isFavorite: true
          }
        } else {
          // 如果课程已在学习记录中，标记为收藏
          courseMap[courseId].isFavorite = true
        }
      })
    }
    
    // 为所有课程设置 isFavorite 属性（未收藏的设为 false）
    Object.values(courseMap).forEach((course: any) => {
      if (course.isFavorite === undefined) {
        course.isFavorite = false
      }
    })
    
    // 转换为数组
    courses.value = Object.values(courseMap)
    
    // 更新统计数据
    updateStats()
  } catch (error) {
    console.error('获取课程数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = () => {
  const learningCount = courses.value.filter(course => course.status === 'learning').length
  const completedCount = courses.value.filter(course => course.status === 'completed').length
  const favoritedCount = favorites.value.length
  const totalDuration = Math.round(courses.value.reduce((total, course) => total + (course.totalDuration || 0), 0) / 60) // 转换为分钟
  
  stats.learningCount = learningCount
  stats.completedCount = completedCount
  stats.favoritedCount = favoritedCount
  stats.totalDuration = totalDuration
}

// 页面加载时从API加载数据
onMounted(() => {
  loadCoursesFromApi()
})

// 根据筛选标签过滤课程
const filteredCourses = computed(() => {
  return courses.value.filter(course => {
    if (activeFilter.value === 'learning') {
      return course.status === 'learning'
    } else if (activeFilter.value === 'completed') {
      return course.status === 'completed'
    } else if (activeFilter.value === 'favorited') {
      return course.isFavorite === true
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