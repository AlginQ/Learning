<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { VideoPlay, Clock, Star, Filter, Sort, Refresh } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()
const activeTab = ref('purchased')
const loading = ref(false)

// 筛选和排序
const filters = reactive({
  category: '',
  sortBy: 'lastStudy',
  sortOrder: 'desc'
})

// 模拟数据 - 已购课程
const purchasedCourses = ref([
  {
    id: 1,
    title: 'Java基础教程',
    cover: 'https://images.unsplash.com/photo-1633356122102-3fe601e05bd2?w=400',
    progress: 75,
    lastStudyTime: '2024-01-15 14:30:00',
    lastStudyChapter: '第8章 面向对象编程',
    teacher: '张老师',
    category: '编程语言',
    price: 99.00,
    discountPrice: 49.00,
    totalLessons: 45,
    completedLessons: 34,
    estimatedTime: '12小时30分钟'
  },
  {
    id: 2,
    title: 'Vue 3从入门到实战',
    cover: 'https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=400',
    progress: 30,
    lastStudyTime: '2024-01-14 10:15:00',
    lastStudyChapter: '第3章 组件基础',
    teacher: '李老师',
    category: '前端开发',
    price: 129.00,
    discountPrice: 69.00,
    totalLessons: 32,
    completedLessons: 10,
    estimatedTime: '8小时15分钟'
  },
  {
    id: 5,
    title: 'Python机器学习实战',
    cover: 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400',
    progress: 15,
    lastStudyTime: '2024-01-12 16:45:00',
    lastStudyChapter: '第2章 数据预处理',
    teacher: '王老师',
    category: '数据科学',
    price: 159.00,
    discountPrice: 79.00,
    totalLessons: 28,
    completedLessons: 4,
    estimatedTime: '15小时20分钟'
  }
])

// 已完成课程
const completedCourses = ref([
  {
    id: 3,
    title: 'Python数据分析入门',
    cover: 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400',
    completedTime: '2024-01-10',
    totalTime: 25.5,
    rating: 4.8,
    teacher: '王老师',
    category: '数据科学',
    certificate: true,
    review: '课程内容丰富实用，老师讲解清晰易懂',
    difficulty: '中级'
  },
  {
    id: 6,
    title: 'HTML/CSS网页设计基础',
    cover: 'https://images.unsplash.com/photo-1547658719-da2b51169166?w=400',
    completedTime: '2024-01-05',
    totalTime: 18.2,
    rating: 4.6,
    teacher: '陈老师',
    category: '前端开发',
    certificate: true,
    review: '非常适合初学者，循序渐进',
    difficulty: '初级'
  }
])

// 收藏课程
const favoriteCourses = ref([
  {
    id: 4,
    title: 'Spring Boot企业级开发',
    cover: 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400',
    price: 199.00,
    discountPrice: 99.00,
    rating: 4.7,
    studentCount: 567,
    teacher: '张老师',
    category: '后端开发',
    difficulty: '高级',
    duration: '20小时',
    isHot: true
  },
  {
    id: 7,
    title: 'React全栈开发实战',
    cover: 'https://images.unsplash.com/photo-1633356122210-3fe601e05bd2?w=400',
    price: 179.00,
    discountPrice: 89.00,
    rating: 4.9,
    studentCount: 892,
    teacher: '刘老师',
    category: '前端开发',
    difficulty: '中级',
    duration: '25小时',
    isNew: true
  }
])

// 处理继续学习
const handleContinueStudy = (course: any) => {
  ElMessage.success(`正在跳转到《${course.title}》的 ${course.lastStudyChapter}`)
  // 这里应该跳转到具体的课程学习页面
  router.push(`/course/${course.id}/lesson`) // 示例路由
}

// 处理查看详情
const handleViewDetails = (course: any) => {
  router.push(`/course-detail/${course.id}`)
}

// 重新学习
const handleRelearn = (course: any) => {
  ElMessage.success(`开始重新学习《${course.title}》`)
  // 重置学习进度
  course.progress = 0
  course.completedLessons = 0
  course.lastStudyTime = new Date().toLocaleString()
}

// 立即购买
const handlePurchase = (course: any) => {
  ElMessage.success(`即将购买《${course.title}》`)
  // 这里应该跳转到购买页面
}

// 取消收藏
const handleRemoveFavorite = (course: any) => {
  ElMessage.success(`已取消收藏《${course.title}》`)
  const index = favoriteCourses.value.findIndex(c => c.id === course.id)
  if (index > -1) {
    favoriteCourses.value.splice(index, 1)
  }
}

// 查看评价
const handleViewReview = (course: any) => {
  ElMessage.info(`查看《${course.title}》的评价`) 
}

// 筛选课程
const filteredCourses = computed(() => {
  let courses = []
  
  switch(activeTab.value) {
    case 'purchased':
      courses = purchasedCourses.value
      break
    case 'completed':
      courses = completedCourses.value
      break
    case 'favorites':
      courses = favoriteCourses.value
      break
  }
  
  // 按分类筛选
  if (filters.category) {
    courses = courses.filter(course => course.category === filters.category)
  }
  
  // 排序
  courses.sort((a, b) => {
    let aValue, bValue
    
    switch(filters.sortBy) {
      case 'lastStudy':
        aValue = new Date(a.lastStudyTime || a.completedTime || 0).getTime()
        bValue = new Date(b.lastStudyTime || b.completedTime || 0).getTime()
        break
      case 'progress':
        aValue = a.progress || 0
        bValue = b.progress || 0
        break
      case 'rating':
        aValue = a.rating || 0
        bValue = b.rating || 0
        break
      default:
        return 0
    }
    
    return filters.sortOrder === 'desc' ? bValue - aValue : aValue - bValue
  })
  
  return courses
})

// 获取分类列表
const categories = computed(() => {
  const allCourses = [...purchasedCourses.value, ...completedCourses.value, ...favoriteCourses.value]
  const uniqueCategories = [...new Set(allCourses.map(course => course.category))]
  return uniqueCategories
})

// 刷新数据
const refreshData = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    ElMessage.success('数据刷新成功')
  }, 1000)
}

// 组件挂载
onMounted(() => {
  // 可以在这里加载真实数据
})
</script>

<template>
  <div class="my-courses-container">
    <div class="page-header">
      <h1>我的课程</h1>
    </div>
    
    <div class="content-wrapper">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="已购课程" name="purchased">
          <div class="courses-grid">
            <el-card 
              v-for="course in purchasedCourses" 
              :key="course.id" 
              class="course-card"
              shadow="hover"
            >
              <div class="course-content">
                <div class="course-cover">
                  <img :src="course.cover" :alt="course.title">
                </div>
                <div class="course-info">
                  <h3 class="course-title">{{ course.title }}</h3>
                  <p class="course-teacher">讲师：{{ course.teacher }}</p>
                  <div class="progress-section">
                    <div class="progress-label">
                      <el-icon><VideoPlay /></el-icon>
                      学习进度
                    </div>
                    <el-progress :percentage="course.progress" :show-text="false" />
                    <div class="progress-text">{{ course.progress }}%</div>
                  </div>
                  <div class="course-meta">
                    <span class="last-study">
                      <el-icon><Clock /></el-icon>
                      最后学习：{{ course.lastStudyTime }}
                    </span>
                  </div>
                  <div class="course-actions">
                    <el-button type="primary" size="small">继续学习</el-button>
                    <el-button size="small">查看详情</el-button>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="已完成课程" name="completed">
          <div class="courses-grid">
            <el-card 
              v-for="course in completedCourses" 
              :key="course.id" 
              class="course-card completed"
              shadow="hover"
            >
              <div class="course-content">
                <div class="course-cover">
                  <img :src="course.cover" :alt="course.title">
                  <div class="completed-badge">
                    <el-icon><Star /></el-icon>
                    已完成
                  </div>
                </div>
                <div class="course-info">
                  <h3 class="course-title">{{ course.title }}</h3>
                  <p class="course-teacher">讲师：{{ course.teacher }}</p>
                  <div class="completion-info">
                    <div class="info-item">
                      <span class="label">完成时间：</span>
                      <span class="value">{{ course.completedTime }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">学习时长：</span>
                      <span class="value">{{ course.totalTime }}小时</span>
                    </div>
                    <div class="info-item">
                      <span class="label">课程评分：</span>
                      <span class="value">{{ course.rating }}分</span>
                    </div>
                  </div>
                  <div class="course-actions">
                    <el-button type="success" size="small">重新学习</el-button>
                    <el-button size="small">查看评价</el-button>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="我的收藏" name="favorites">
          <div class="courses-grid">
            <el-card 
              v-for="course in favoriteCourses" 
              :key="course.id" 
              class="course-card"
              shadow="hover"
            >
              <div class="course-content">
                <div class="course-cover">
                  <img :src="course.cover" :alt="course.title">
                  <div class="favorite-badge">
                    <el-icon color="#ff4d4f"><Star /></el-icon>
                  </div>
                </div>
                <div class="course-info">
                  <h3 class="course-title">{{ course.title }}</h3>
                  <p class="course-teacher">讲师：{{ course.teacher }}</p>
                  <div class="course-price">
                    <span class="current-price">¥{{ course.discountPrice }}</span>
                    <span class="original-price">¥{{ course.price }}</span>
                  </div>
                  <div class="course-stats">
                    <span class="rating">评分：{{ course.rating }}</span>
                    <span class="students">{{ course.studentCount }}人学习</span>
                  </div>
                  <div class="course-actions">
                    <el-button type="primary" size="small">立即购买</el-button>
                    <el-button size="small">取消收藏</el-button>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

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
  font-size: 28px;
  color: #333;
  margin: 0;
}

.content-wrapper {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.course-card {
  border-radius: 8px;
  overflow: hidden;
}

.course-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.course-cover {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.completed-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #67c23a;
  color: white;
  padding: 5px 10px;
  border-radius: 15px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.favorite-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: white;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.course-info {
  padding: 15px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.course-title {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 10px;
  color: #333;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-teacher {
  color: #666;
  font-size: 14px;
  margin: 0 0 15px;
}

.progress-section {
  margin-bottom: 15px;
}

.progress-label {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.progress-text {
  text-align: right;
  font-size: 12px;
  color: #409eff;
  margin-top: 5px;
}

.course-meta {
  margin-bottom: 15px;
}

.last-study {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #999;
}

.course-price {
  margin: 10px 0;
}

.current-price {
  color: #ff4d4f;
  font-size: 18px;
  font-weight: bold;
  margin-right: 10px;
}

.original-price {
  color: #999;
  text-decoration: line-through;
  font-size: 14px;
}

.course-stats {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
  margin: 10px 0;
}

.course-actions {
  display: flex;
  gap: 10px;
  margin-top: auto;
}

.completed .course-actions .el-button:first-child {
  background: #67c23a;
  border-color: #67c23a;
}

.completion-info {
  margin: 15px 0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.info-item .label {
  color: #666;
}

.info-item .value {
  color: #333;
  font-weight: 500;
}
</style>