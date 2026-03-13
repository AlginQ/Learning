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
import { ref, computed } from 'vue'
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

// 统计数据
const stats = ref({
  learningCount: 3,
  completedCount: 2,
  totalDuration: 1230 // 分钟
})

// 模拟课程数据
const courses = ref([
  {
    id: 1,
    title: 'Vue 3从入门到实战',
    cover_image: 'https://picsum.photos/400/225?random=1',
    progress: 65,
    lastStudyTime: '2024-03-10 15:30',
    status: 'learning',
    lastPlayTime: 1200 // 秒
  },
  {
    id: 2,
    title: 'Spring Boot企业级开发',
    cover_image: 'https://picsum.photos/400/225?random=2',
    progress: 40,
    lastStudyTime: '2024-03-09 10:15',
    status: 'learning',
    lastPlayTime: 800 // 秒
  },
  {
    id: 3,
    title: 'React Hooks完全指南',
    cover_image: 'https://picsum.photos/400/225?random=3',
    progress: 85,
    lastStudyTime: '2024-03-08 14:20',
    status: 'learning',
    lastPlayTime: 1500 // 秒
  },
  {
    id: 4,
    title: 'JavaScript高级编程',
    cover_image: 'https://picsum.photos/400/225?random=4',
    progress: 100,
    lastStudyTime: '2024-03-05 09:45',
    status: 'completed',
    lastPlayTime: 3600 // 秒
  },
  {
    id: 5,
    title: 'TypeScript入门到精通',
    cover_image: 'https://picsum.photos/400/225?random=5',
    progress: 100,
    lastStudyTime: '2024-03-01 16:50',
    status: 'completed',
    lastPlayTime: 2800 // 秒
  },
  {
    id: 6,
    title: 'HTML5与CSS3实战',
    cover_image: 'https://picsum.photos/400/225?random=6',
    progress: 0,
    lastStudyTime: '2024-02-28 11:30',
    status: 'favorited',
    lastPlayTime: 0 // 秒
  }
])

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