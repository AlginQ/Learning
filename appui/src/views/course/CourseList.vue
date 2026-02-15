<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCourseListApi, getCategoryApi } from '@/api/course'
import type { Course, Category } from '@/types/course'
import { Search } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const courses = ref<Course[]>([])
const categories = ref<Category[]>([])
const loading = ref(false)
const total = ref(0)

const pagination = reactive({
  page: 1,
  size: 12
})

const searchForm = reactive({
  keyword: '',
  categoryId: undefined as number | undefined
})

// 获取课程分类
const loadCategories = async () => {
  try {
    const response = await getCategoryApi()
    categories.value = response.data
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取课程列表
const loadCourses = async () => {
  loading.value = true
  try {
    const params = {
      ...pagination,
      ...searchForm
    }
    const response = await getCourseListApi(params)
    courses.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('获取课程列表失败:', error)
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
  pagination.page = 1
  loadCourses()
}

// 分页变化
const handlePageChange = (page: number) => {
  pagination.page = page
  loadCourses()
}

// 跳转到课程详情
const goToCourseDetail = (id: number) => {
  router.push(`/course/${id}`)
}

onMounted(() => {
  // 从路由参数获取搜索关键词
  if (route.query.keyword) {
    searchForm.keyword = route.query.keyword as string
  }
  
  loadCategories()
  loadCourses()
})
</script>

<template>
  <div class="course-list-container">
    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-box">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索课程..."
          size="large"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>
      
      <div class="filter-box">
        <el-select
          v-model="searchForm.categoryId"
          placeholder="选择分类"
          clearable
          size="large"
          @change="handleSearch"
        >
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
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
      
      <el-row v-else :gutter="20">
        <el-col
          v-for="course in courses"
          :key="course.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <div class="course-card" @click="goToCourseDetail(course.id)">
            <div class="course-cover">
              <img
                :src="course.coverImage || '/placeholder-course.jpg'"
                :alt="course.title"
              />
              <div class="course-price">
                <span v-if="course.discountPrice && course.discountPrice < course.price" class="discount-price">
                  ¥{{ course.discountPrice }}
                </span>
                <span :class="{ 'original-price': course.discountPrice && course.discountPrice < course.price }">
                  ¥{{ course.price }}
                </span>
              </div>
            </div>
            
            <div class="course-info">
              <h3 class="course-title">{{ course.title }}</h3>
              <p class="course-description">{{ course.description }}</p>
              
              <div class="course-meta">
                <div class="meta-item">
                  <el-icon><User /></el-icon>
                  <span>{{ course.teacher.name }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><Collection /></el-icon>
                  <span>{{ course.studentCount }}人学习</span>
                </div>
                <div class="meta-item">
                  <el-rate
                    v-model="course.rating"
                    disabled
                    show-score
                    score-template="{value}分"
                    size="small"
                  />
                </div>
              </div>
              
              <div class="course-stats">
                <span>{{ course.lessonCount }}课时</span>
                <span class="category-tag">{{ course.category.name }}</span>
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

.search-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.search-box {
  max-width: 500px;
  margin-bottom: 15px;
}

.filter-box {
  display: flex;
  gap: 15px;
  align-items: center;
  flex-wrap: wrap;
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
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
  margin-bottom: 20px;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 20px 0 rgba(0, 0, 0, 0.15);
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

.course-price {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 5px 10px;
  border-radius: 4px;
  font-weight: bold;
}

.discount-price {
  color: #ff6b6b;
  margin-right: 5px;
}

.original-price {
  text-decoration: line-through;
  font-size: 12px;
  opacity: 0.8;
}

.course-info {
  padding: 15px;
}

.course-title {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-description {
  color: #666;
  font-size: 14px;
  margin: 0 0 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 40px;
}

.course-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #666;
}

.course-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #666;
}

.category-tag {
  background: #ecf5ff;
  color: #409eff;
  padding: 2px 8px;
  border-radius: 10px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}
</style>