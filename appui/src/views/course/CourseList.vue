<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCourseListApi, getCategoryApi, getTeacherListApi } from '@/api/course'
import type { Course, Category } from '@/types/course'
import { Search, ArrowDown, House } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const courses = ref<Course[]>([])
const categories = ref<Category[]>([])
const teachers = ref<any[]>([])
const loading = ref(false)
const total = ref(0)
const activeCategory = ref('all')
const showMoreFilters = ref(false)

const pagination = reactive({
  page: 1,
  size: 12
})

const searchForm = reactive({
  keyword: '',
  categoryId: undefined as number | undefined,
  priceRange: undefined as string | undefined,
  rating: undefined as number | undefined,
  sortBy: 'latest' as string
})

// 模拟分类数据
const mockCategories = [
  { id: 1, name: '前端开发' },
  { id: 2, name: '后端开发' },
  { id: 3, name: '数据科学' },
  { id: 4, name: '人工智能' },
  { id: 5, name: '移动开发' }
]

// 模拟教师数据
const mockTeachers = [
  { id: 1, name: '张老师', avatar: 'https://a0ai.marscode.cn/api/ide/v1/text_to_image?prompt=teacher%20avatar%20male&image_size=square' },
  { id: 2, name: '李老师', avatar: 'https://a0ai.marscode.cn/api/ide/v1/text_to_image?prompt=teacher%20avatar%20female&image_size=square' },
  { id: 3, name: '王老师', avatar: 'https://a0ai.marscode.cn/api/ide/v1/text_to_image?prompt=teacher%20avatar%20male%20glasses&image_size=square' }
]

// 获取课程分类
const loadCategories = async () => {
  try {
    const response = await getCategoryApi()
    categories.value = response.data
    
    // 如果分类数据为空，使用模拟数据
    if (categories.value.length === 0) {
      categories.value = mockCategories
    }
  } catch (error) {
    console.error('获取分类失败:', error)
    // API调用失败时使用模拟数据
    categories.value = mockCategories
  }
}

// 获取教师列表
const loadTeachers = async () => {
  try {
    const response = await getTeacherListApi()
    teachers.value = response.data
    
    // 如果教师数据为空，使用模拟数据
    if (teachers.value.length === 0) {
      teachers.value = mockTeachers
    }
  } catch (error) {
    console.error('获取教师列表失败:', error)
    // API调用失败时使用模拟数据
    teachers.value = mockTeachers
  }
}

// 根据ID获取教师名称
const getTeacherName = (teacherId: number) => {
  const teacher = teachers.value.find(t => t.id === teacherId)
  return teacher ? teacher.name : '未知讲师'
}

// 根据ID获取分类名称
const getCategoryName = (categoryId: number) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category ? category.name : '未分类'
}

// 格式化时长
const formatDuration = (seconds: number) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = Math.round(seconds % 60)
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
}

// 格式化数字
const formatNumber = (num: number) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toString()
}

// 模拟课程数据
const mockCourses = [
  {
    id: 1,
    title: 'Vue3极简2025版教程',
    description: '从零开始学习Vue3，掌握现代前端开发技术',
    coverImage: 'https://a0ai.marscode.cn/api/ide/v1/text_to_image?prompt=Vue%203%20tutorial%20course%20cover%20with%20modern%20design&image_size=landscape_16_9',
    price: 0,
    discountPrice: 0,
    categoryId: 1,
    teacherId: 1,
    lessonCount: 20,
    studentCount: 12345,
    rating: 4.8,
    duration: 3600,
    commentCount: 567
  },
  {
    id: 2,
    title: 'Java基础教程',
    description: '从零开始学习Java编程语言',
    coverImage: 'https://a0ai.marscode.cn/api/ide/v1/text_to_image?prompt=Java%20programming%20course%20cover%20with%20modern%20design&image_size=landscape_16_9',
    price: 99,
    discountPrice: 49,
    categoryId: 2,
    teacherId: 2,
    lessonCount: 40,
    studentCount: 8923,
    rating: 4.7,
    duration: 7200,
    commentCount: 345
  },
  {
    id: 3,
    title: 'Python数据分析入门',
    description: '学习Python数据分析和可视化',
    coverImage: 'https://a0ai.marscode.cn/api/ide/v1/text_to_image?prompt=Python%20data%20analysis%20course%20cover%20with%20modern%20design&image_size=landscape_16_9',
    price: 159,
    discountPrice: 79,
    categoryId: 3,
    teacherId: 3,
    lessonCount: 30,
    studentCount: 5678,
    rating: 4.9,
    duration: 5400,
    commentCount: 234
  },
  {
    id: 4,
    title: 'React实战项目开发',
    description: '通过实战项目学习React开发',
    coverImage: 'https://a0ai.marscode.cn/api/ide/v1/text_to_image?prompt=React%20project%20development%20course%20cover%20with%20modern%20design&image_size=landscape_16_9',
    price: 199,
    discountPrice: 99,
    categoryId: 1,
    teacherId: 1,
    lessonCount: 25,
    studentCount: 3456,
    rating: 4.6,
    duration: 4500,
    commentCount: 123
  },
  {
    id: 5,
    title: 'Spring Boot微服务开发',
    description: '学习Spring Boot微服务架构',
    coverImage: 'https://a0ai.marscode.cn/api/ide/v1/text_to_image?prompt=Spring%20Boot%20microservices%20course%20cover%20with%20modern%20design&image_size=landscape_16_9',
    price: 299,
    discountPrice: 149,
    categoryId: 2,
    teacherId: 2,
    lessonCount: 35,
    studentCount: 2345,
    rating: 4.8,
    duration: 6300,
    commentCount: 89
  },
  {
    id: 6,
    title: 'JavaScript高级编程',
    description: '深入学习JavaScript高级特性',
    coverImage: 'https://a0ai.marscode.cn/api/ide/v1/text_to_image?prompt=JavaScript%20advanced%20programming%20course%20cover%20with%20modern%20design&image_size=landscape_16_9',
    price: 129,
    discountPrice: 69,
    categoryId: 1,
    teacherId: 1,
    lessonCount: 22,
    studentCount: 4567,
    rating: 4.7,
    duration: 3960,
    commentCount: 156
  },
  {
    id: 7,
    title: 'MySQL数据库优化',
    description: '学习MySQL数据库性能优化',
    coverImage: 'https://a0ai.marscode.cn/api/ide/v1/text_to_image?prompt=MySQL%20database%20optimization%20course%20cover%20with%20modern%20design&image_size=landscape_16_9',
    price: 149,
    discountPrice: 79,
    categoryId: 2,
    teacherId: 2,
    lessonCount: 18,
    studentCount: 1987,
    rating: 4.9,
    duration: 3240,
    commentCount: 67
  },
  {
    id: 8,
    title: 'Docker容器技术',
    description: '学习Docker容器化技术',
    coverImage: 'https://a0ai.marscode.cn/api/ide/v1/text_to_image?prompt=Docker%20container%20technology%20course%20cover%20with%20modern%20design&image_size=landscape_16_9',
    price: 179,
    discountPrice: 89,
    categoryId: 2,
    teacherId: 3,
    lessonCount: 20,
    studentCount: 1654,
    rating: 4.6,
    duration: 3600,
    commentCount: 45
  },
  {
    id: 9,
    title: 'Node.js后端开发',
    description: '学习Node.js后端开发技术',
    coverImage: 'https://a0ai.marscode.cn/api/ide/v1/text_to_image?prompt=Node.js%20backend%20development%20course%20cover%20with%20modern%20design&image_size=landscape_16_9',
    price: 169,
    discountPrice: 89,
    categoryId: 1,
    teacherId: 1,
    lessonCount: 24,
    studentCount: 2789,
    rating: 4.7,
    duration: 4320,
    commentCount: 98
  },
  {
    id: 10,
    title: '数据结构与算法',
    description: '学习数据结构与算法基础',
    coverImage: 'https://a0ai.marscode.cn/api/ide/v1/text_to_image?prompt=Data%20structures%20and%20algorithms%20course%20cover%20with%20modern%20design&image_size=landscape_16_9',
    price: 189,
    discountPrice: 99,
    categoryId: 2,
    teacherId: 2,
    lessonCount: 30,
    studentCount: 3456,
    rating: 4.8,
    duration: 5400,
    commentCount: 123
  },
  {
    id: 11,
    title: 'HTML5与CSS3实战',
    description: '学习HTML5与CSS3高级特性',
    coverImage: 'https://a0ai.marscode.cn/api/ide/v1/text_to_image?prompt=HTML5%20and%20CSS3%20course%20cover%20with%20modern%20design&image_size=landscape_16_9',
    price: 99,
    discountPrice: 49,
    categoryId: 1,
    teacherId: 1,
    lessonCount: 15,
    studentCount: 4567,
    rating: 4.6,
    duration: 2700,
    commentCount: 156
  },
  {
    id: 12,
    title: '人工智能入门',
    description: '学习人工智能基础概念和应用',
    coverImage: 'https://a0ai.marscode.cn/api/ide/v1/text_to_image?prompt=Artificial%20intelligence%20introduction%20course%20cover%20with%20modern%20design&image_size=landscape_16_9',
    price: 299,
    discountPrice: 149,
    categoryId: 3,
    teacherId: 3,
    lessonCount: 35,
    studentCount: 2345,
    rating: 4.9,
    duration: 6300,
    commentCount: 89
  }
]

// 获取课程列表
const loadCourses = async () => {
  loading.value = true
  try {
    const params = {
      ...pagination,
      ...searchForm
    }
    const response = await getCourseListApi(params)
    let courseList = response.data.records
    total.value = response.data.total
    
    // 如果课程数据少于12个，使用模拟数据
    if (courseList.length < 12) {
      courseList = mockCourses
      total.value = mockCourses.length
    }
    
    // 前端过滤：按关键词搜索
    if (searchForm.keyword) {
      const keyword = searchForm.keyword.toLowerCase()
      courseList = courseList.filter(course => {
        // 确保课程对象有title字段，并且title或description包含关键词
        return course.title && (course.title.toLowerCase().includes(keyword) ||
               (course.description && course.description.toLowerCase().includes(keyword)))
      })
      total.value = courseList.length
    }
    
    // 前端过滤：按分类筛选
    if (searchForm.categoryId) {
      courseList = courseList.filter(course => course.categoryId === searchForm.categoryId)
      total.value = courseList.length
    }
    
    // 前端过滤：按最低评分筛选
    if (searchForm.rating) {
      courseList = courseList.filter(course => course.rating >= searchForm.rating)
      total.value = courseList.length
    }
    
    // 排序
    switch (searchForm.sortBy) {
      case 'rating-asc':
        courseList.sort((a, b) => (a.rating || 0) - (b.rating || 0))
        break
      case 'rating-desc':
        courseList.sort((a, b) => (b.rating || 0) - (a.rating || 0))
        break
      case 'latest':
      default:
        // 综合排序：先按评分降序，再按学习人数降序
        courseList.sort((a, b) => {
          const ratingDiff = (b.rating || 0) - (a.rating || 0)
          if (ratingDiff !== 0) return ratingDiff
          return (b.studentCount || 0) - (a.studentCount || 0)
        })
        break
    }
    
    courses.value = courseList
  } catch (error) {
    console.error('获取课程列表失败:', error)
    // API调用失败时使用模拟数据
    let courseList = mockCourses
    
    // 前端过滤：按关键词搜索
    if (searchForm.keyword) {
      const keyword = searchForm.keyword.toLowerCase()
      courseList = courseList.filter(course => {
        // 确保课程对象有title字段，并且title或description包含关键词
        return course.title && (course.title.toLowerCase().includes(keyword) ||
               (course.description && course.description.toLowerCase().includes(keyword)))
      })
      total.value = courseList.length
    }
    
    // 前端过滤：按分类筛选
    if (searchForm.categoryId) {
      courseList = courseList.filter(course => course.categoryId === searchForm.categoryId)
      total.value = courseList.length
    }
    
    // 前端过滤：按最低评分筛选
    if (searchForm.rating) {
      courseList = courseList.filter(course => course.rating >= searchForm.rating)
      total.value = courseList.length
    }
    
    // 排序
    switch (searchForm.sortBy) {
      case 'rating-asc':
        courseList.sort((a, b) => (a.rating || 0) - (b.rating || 0))
        break
      case 'rating-desc':
        courseList.sort((a, b) => (b.rating || 0) - (a.rating || 0))
        break
      case 'latest':
      default:
        // 综合排序：先按评分降序，再按学习人数降序
        courseList.sort((a, b) => {
          const ratingDiff = (b.rating || 0) - (a.rating || 0)
          if (ratingDiff !== 0) return ratingDiff
          return (b.studentCount || 0) - (a.studentCount || 0)
        })
        break
    }
    
    courses.value = courseList
    total.value = courseList.length
  } finally {
    loading.value = false
  }
}

// 搜索课程
const handleSearch = () => {
  pagination.page = 1
  loadCourses()
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.categoryId = undefined
  searchForm.priceRange = undefined
  searchForm.rating = undefined
  searchForm.sortBy = 'latest'
  pagination.page = 1
  loadCourses()
}

// 分页变化
const handlePageChange = (page: number) => {
  pagination.page = page
  loadCourses()
}

// 处理图片加载错误
const handleImageError = (event: Event, course: any) => {
  const img = event.target as HTMLImageElement
  // 如果图片加载失败，使用占位图
  img.src = `https://via.placeholder.com/400x225?text=${encodeURIComponent(course.title)}`
}

// 跳转到课程详情
const goToCourseDetail = (id: number) => {
  router.push(`/course-detail/${id}`)
}

// 返回首页
const goToHome = () => {
  router.push('/')
}

// 处理分类标签点击
const handleCategoryChange = (tab: any) => {
  activeCategory.value = tab.props.name
  
  // 如果点击的是"更多筛选"标签，展开高级筛选选项
  if (tab.props.name === 'more') {
    showMoreFilters.value = true
  } else {
    // 否则，收起高级筛选选项，重置搜索条件并加载课程
    showMoreFilters.value = false
    pagination.page = 1
    
    // 根据标签设置排序方式
    switch (tab.props.name) {
      case 'latest':
        searchForm.sortBy = 'latest'
        break
      case 'most-played':
        searchForm.sortBy = 'sales'
        break
      case 'most-collected':
        searchForm.sortBy = 'rating'
        break
      default:
        searchForm.sortBy = 'latest'
    }
    
    loadCourses()
  }
}

onMounted(() => {
  // 从路由参数获取搜索关键词
  if (route.query.keyword) {
    searchForm.keyword = route.query.keyword as string
  }
  
  loadCategories()
  loadTeachers()
  loadCourses()
})
</script>

<template>
  <div class="course-list-container">
    <!-- 顶部栏：返回首页按钮和搜索框 -->
    <div class="top-bar">
      <div class="back-home">
        <el-button type="default" @click="goToHome" icon="House">返回首页</el-button>
      </div>
      
      <div class="search-box">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索课程..."
          size="large"
          @keyup.enter="handleSearch"
          style="max-width: 600px; width: 100%;"
        >
          <template #append>
            <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>
    </div>
    
    <!-- 分类和排序 -->
    <div class="category-sort">
      <div class="category-tabs">
        <el-tabs v-model="activeCategory" @tab-click="handleCategoryChange">
          <el-tab-pane label="综合" name="all" />
          <el-tab-pane label="最新发布" name="latest" />
          <el-tab-pane label="最多播放" name="most-played" />
          <el-tab-pane label="最多收藏" name="most-collected" />
          <el-tab-pane label="更多筛选" name="more" />
        </el-tabs>
      </div>
      
      <div class="sort-options">
        <el-radio-group v-model="searchForm.sortBy" @change="handleSearch">
          <el-radio-button label="latest">综合排序</el-radio-button>
          <el-radio-button label="rating-asc">评分从低到高</el-radio-button>
          <el-radio-button label="rating-desc">评分从高到低</el-radio-button>
        </el-radio-group>
      </div>
    </div>
    
    <!-- 高级筛选 -->
    <div v-if="showMoreFilters" class="advanced-filter">
      <div class="filter-box">
        <el-select
          v-model="searchForm.categoryId"
          placeholder="选择分类"
          clearable
          @change="handleSearch"
        >
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
        
        <el-select
          v-model="searchForm.rating"
          placeholder="最低评分"
          clearable
          @change="handleSearch"
        >
          <el-option label="4.0分及以上" value="4" />
          <el-option label="4.5分及以上" value="4.5" />
          <el-option label="5.0分" value="5" />
        </el-select>
        
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </div>
    
    <!-- 课程列表 -->
    <div class="course-list">
      <el-skeleton v-if="loading" :rows="6" animated />
      
      <div v-else-if="courses.length === 0" class="empty-state">
        <el-empty description="暂无课程数据" />
      </div>
      
      <el-row v-else :gutter="16">
        <el-col
          v-for="course in courses"
          :key="course.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
          :xl="4"
        >
          <div class="course-card" @click="goToCourseDetail(course.id)">
            <div class="course-cover">
              <img
                :src="course.coverImage || course.cover_image || 'https://via.placeholder.com/400x225?text=' + encodeURIComponent(course.title)"
                :alt="course.title"
                @error="handleImageError($event, course)"
              />
              
              <!-- 热门标签 -->
              <div v-if="course.studentCount > 1000" class="hot-tag">热门</div>
              
              <!-- 免费标签 -->
              <div v-if="course.price === 0" class="free-tag">免费</div>
              
              <!-- 时长标签 -->
              <div class="duration-tag">{{ formatDuration(course.duration || course.lessonCount * 45 * 60 || 0) }}</div>
            </div>
            
            <div class="course-info">
              <h3 class="course-title">{{ course.title }}</h3>
              
              <div class="course-meta">
                <span class="category-tag">{{ getCategoryName(course.categoryId) }}</span>
                <span class="author">{{ getTeacherName(course.teacherId) }}</span>
                <span class="views">{{ formatNumber(course.studentCount) }}播放</span>
                <span class="danmaku">{{ course.commentCount || 0 }}弹幕</span>
                <span class="rating">{{ course.rating || 0 }}分</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > pagination.size">
      <el-pagination
        v-model:current-page="pagination.page"
        :page-size="pagination.size"
        :total="total"
        layout="prev, pager, next, jumper"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<style scoped>
.course-list-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 顶部栏 */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

/* 返回首页按钮 */
.back-home {
  text-align: left;
}

/* 搜索框 */
.search-box {
  display: flex;
  justify-content: flex-end;
  flex: 1;
  min-width: 300px;
}

/* 分类和排序 */
.category-sort {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 15px;
  margin-bottom: 20px;
}

.category-tabs {
  margin-bottom: 15px;
}

.sort-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

/* 高级筛选 */
.advanced-filter {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 15px;
  margin-bottom: 20px;
}

.filter-box {
  display: flex;
  gap: 15px;
  align-items: center;
  flex-wrap: wrap;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .course-list-container {
    padding: 20px 15px;
  }
  
  .category-sort,
  .advanced-filter {
    padding: 10px;
  }
  
  .filter-box {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-box .el-select {
    width: 100%;
  }
  
  .course-cover {
    height: 150px;
  }
  
  .course-info {
    padding: 12px;
  }
  
  .sort-options {
    flex-direction: column;
    align-items: stretch;
  }
}

.course-list {
  margin-bottom: 30px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

.course-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  margin-bottom: 16px;
  position: relative;
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.course-cover {
  position: relative;
  height: 160px;
  overflow: hidden;
  border-radius: 8px 8px 0 0;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.course-card:hover .course-cover img {
  transform: scale(1.05);
}

.course-info {
  padding: 12px;
}

.course-title {
  font-size: 14px;
  font-weight: 500;
  margin: 0 0 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
  color: #333;
  height: 40px;
}

.course-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 12px;
  color: #999;
}

.category-tag {
  background: #ecf5ff;
  color: #409eff;
  padding: 2px 6px;
  border-radius: 8px;
  font-size: 10px;
  font-weight: 500;
  margin-right: 8px;
}

.author {
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 8px;
}

.views,
.danmaku,
.rating {
  margin-right: 8px;
}

.rating {
  color: #ff9500;
  font-weight: 500;
}

/* 热门标签 */
.hot-tag {
  position: absolute;
  top: 8px;
  left: 0;
  background: #ff6b6b;
  color: white;
  padding: 2px 8px;
  border-radius: 0 10px 10px 0;
  font-size: 10px;
  font-weight: 500;
  z-index: 10;
}

/* 免费标签 */
.free-tag {
  position: absolute;
  top: 8px;
  right: 8px;
  background: #67c23a;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 10px;
  font-weight: 500;
  z-index: 10;
}

/* 时长标签 */
.duration-tag {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  z-index: 10;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 30px 0;
  margin-top: 20px;
}
</style>