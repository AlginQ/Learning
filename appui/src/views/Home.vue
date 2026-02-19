<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { User, Lock, Search, Menu, Star, Clock } from '@element-plus/icons-vue'
import { isAdmin as checkIsAdmin } from '@/utils/permission'
import type { Course } from '@/types/course'

const router = useRouter()
const userStore = useUserStore()

const activeIndex = ref('1')
const searchKeyword = ref('')
const activeCarouselIndex = ref(0)

// 课程数据
const hotCourses = ref<Course[]>([])
const newCourses = ref<Course[]>([])
const featuredCourses = ref<Course[]>([])

// 轮播图数据
const carouselItems = ref([
  {
    id: 1,
    title: '全新课程上线',
    subtitle: '掌握前沿技术，提升职场竞争力',
    image: 'https://images.unsplash.com/photo-1553877522-43269d4ea984?w=1200&h=400&fit=crop',
    buttonText: '立即学习'
  },
  {
    id: 2,
    title: '限时优惠活动',
    subtitle: '精选课程低至5折，机会不容错过',
    image: 'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=1200&h=400&fit=crop',
    buttonText: '查看详情'
  },
  {
    id: 3,
    title: '名师精品课',
    subtitle: '行业大咖亲授，系统化学习路径',
    image: 'https://images.unsplash.com/photo-1543269865-cbf427effbad?w=1200&h=400&fit=crop',
    buttonText: '马上报名'
  }
])

// 权限判断
const isAdmin = computed(() => userStore.isLogin)
const isCurrentUserAdmin = computed(() => checkIsAdmin())

const handleSelect = (key: string) => {
  switch (key) {
    case '1':
      router.push('/')
      break
    case '2':
      router.push('/courses')
      break
    case '3':
      if (userStore.isLogin) {
        router.push('/study')
      } else {
        router.push('/login')
      }
      break
    case '4':
      if (userStore.isLogin) {
        router.push('/my-courses')
      } else {
        router.push('/login')
      }
      break
    case '5':
      if (isCurrentUserAdmin.value) {
        router.push('/admin/users')
      } else {
        router.push('/')
      }
      break
  }
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push(`/courses?keyword=${encodeURIComponent(searchKeyword.value)}`)
  }
}

const handleLogout = () => {
  userStore.logout()
  router.push('/')
}

// 获取课程数据
const fetchCourses = async () => {
  try {
    // 模拟获取热门课程
    hotCourses.value = [
      {
        id: 1,
        title: 'Vue 3从入门到实战',
        description: '全面掌握Vue 3核心技术，打造企业级应用',
        coverImage: 'https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=300&h=200&fit=crop',
        price: 299,
        isFree: false,
        teacher: { id: 1, name: '张老师' },
        category: { id: 1, name: '前端开发' },
        lessonCount: 20,
        studentCount: 1234,
        rating: 4.8,
        createTime: '2024-01-01'
      },
      {
        id: 2,
        title: 'React全栈开发',
        description: '从前端到后端，构建完整的React应用',
        coverImage: 'https://images.unsplash.com/photo-1633356122102-3fe601e05bd2?w=300&h=200&fit=crop',
        price: 399,
        isFree: false,
        teacher: { id: 2, name: '李老师' },
        category: { id: 2, name: '全栈开发' },
        lessonCount: 25,
        studentCount: 987,
        rating: 4.6,
        createTime: '2024-01-02'
      },
      {
        id: 3,
        title: 'Python数据分析',
        description: '掌握Python数据分析核心技能',
        coverImage: 'https://images.unsplash.com/photo-1555949963-aa79dcee981c?w=300&h=200&fit=crop',
        price: 0,
        isFree: true,
        teacher: { id: 3, name: '王老师' },
        category: { id: 3, name: '数据分析' },
        lessonCount: 18,
        studentCount: 2156,
        rating: 4.9,
        createTime: '2024-01-03'
      },
      {
        id: 4,
        title: 'UI/UX设计基础',
        description: '从零开始学习用户体验设计',
        coverImage: 'https://images.unsplash.com/photo-1558655146-9f40138edfeb?w=300&h=200&fit=crop',
        price: 199,
        isFree: false,
        teacher: { id: 4, name: '陈老师' },
        category: { id: 4, name: '设计' },
        lessonCount: 15,
        studentCount: 843,
        rating: 4.7,
        createTime: '2024-01-04'
      }
    ]
    
    // 模拟获取最新课程
    newCourses.value = [
      {
        id: 5,
        title: 'Node.js后端开发',
        description: '深入学习Node.js后端开发技术',
        coverImage: 'https://images.unsplash.com/photo-1620714223084-8fcacc6dfd8d?w=300&h=200&fit=crop',
        price: 349,
        isFree: false,
        teacher: { id: 5, name: '赵老师' },
        category: { id: 5, name: '后端开发' },
        lessonCount: 22,
        studentCount: 654,
        rating: 4.5,
        createTime: '2024-01-05'
      },
      {
        id: 6,
        title: '移动端开发实战',
        description: '跨平台移动应用开发技术',
        coverImage: 'https://images.unsplash.com/photo-1551650975-87deedd944c3?w=300&h=200&fit=crop',
        price: 0,
        isFree: true,
        teacher: { id: 6, name: '孙老师' },
        category: { id: 6, name: '移动开发' },
        lessonCount: 16,
        studentCount: 1321,
        rating: 4.8,
        createTime: '2024-01-06'
      }
    ]
    
    // 模拟获取精选推荐
    featuredCourses.value = [
      {
        id: 7,
        title: '人工智能入门',
        description: 'AI时代必备技能，从基础到应用',
        coverImage: 'https://images.unsplash.com/photo-1677442135432-4248b5d92a50?w=300&h=200&fit=crop',
        price: 499,
        isFree: false,
        teacher: { id: 7, name: '刘老师' },
        category: { id: 7, name: '人工智能' },
        lessonCount: 30,
        studentCount: 2876,
        rating: 4.9,
        createTime: '2024-01-07'
      },
      {
        id: 8,
        title: '云计算架构师',
        description: '企业级云架构设计与实施',
        coverImage: 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=300&h=200&fit=crop',
        price: 599,
        isFree: false,
        teacher: { id: 8, name: '周老师' },
        category: { id: 8, name: '云计算' },
        lessonCount: 28,
        studentCount: 1543,
        rating: 4.7,
        createTime: '2024-01-08'
      }
    ]
  } catch (error) {
    console.error('获取课程数据失败:', error)
  }
}

// 处理课程点击
const handleCourseClick = (courseId: number) => {
  router.push(`/course/${courseId}`)
}

// 组件挂载时获取数据
onMounted(() => {
  fetchCourses()
})
</script>

<template>
  <div class="home-layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo">
          <h2>在线学习平台</h2>
        </div>
        
        <div class="nav-menu">
          <el-menu
            :default-active="activeIndex"
            mode="horizontal"
            @select="handleSelect"
            background-color="#545c64"
            text-color="#fff"
            active-text-color="#ffd04b"
          >
            <el-menu-item index="1">首页</el-menu-item>
            <el-menu-item index="2">课程</el-menu-item>
            <el-menu-item v-if="isAdmin" index="3">学习中心</el-menu-item>
            <el-menu-item v-if="isAdmin" index="4">我的课程</el-menu-item>
            <el-menu-item v-if="isAdmin && isCurrentUserAdmin" index="5">管理中心</el-menu-item>
          </el-menu>
        </div>
        
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索课程..."
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="handleSearch" />
            </template>
          </el-input>
        </div>
        
        <div class="user-actions">
          <template v-if="userStore.isLogin">
            <el-dropdown>
              <span class="el-dropdown-link">
                <el-avatar :icon="User" size="small" />
                {{ userStore.currentUser?.nickname || userStore.currentUser?.username }}
                <el-icon class="el-icon--right">
                  <Menu />
                </el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" @click="$router.push('/login')">
              <el-icon><Lock /></el-icon>
              登录
            </el-button>
            <el-button @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>
    
    <!-- 主要内容区域 -->
    <el-main class="main-content">
      <!-- 轮播图区域 -->
      <div class="carousel-section">
        <el-carousel 
          height="400px" 
          :autoplay="true" 
          :interval="5000"
          arrow="hover"
          indicator-position="outside"
          v-model:active-index="activeCarouselIndex"
        >
          <el-carousel-item v-for="item in carouselItems" :key="item.id">
            <div class="carousel-item" :style="{ backgroundImage: `url(${item.image})` }">
              <div class="carousel-content">
                <h2>{{ item.title }}</h2>
                <p>{{ item.subtitle }}</p>
                <el-button type="primary" size="large">{{ item.buttonText }}</el-button>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
      
      <!-- 热门课程 -->
      <div class="course-section">
        <div class="section-header">
          <el-icon size="24" color="#F56C6C"><Fire /></el-icon>
          <h2>热门课程</h2>
          <el-button link type="primary" @click="$router.push('/courses')">更多 ></el-button>
        </div>
        <div class="course-grid">
          <el-card 
            v-for="course in hotCourses" 
            :key="course.id" 
            class="course-card"
            shadow="hover"
            @click="handleCourseClick(course.id)"
          >
            <template #header>
              <div class="card-header">
                <img :src="course.coverImage" :alt="course.title" class="course-cover" />
                <el-tag 
                  :type="course.isFree ? 'success' : 'danger'" 
                  size="small" 
                  class="price-tag"
                >
                  {{ course.isFree ? '免费' : `¥${course.price}` }}
                </el-tag>
              </div>
            </template>
            <div class="card-body">
              <h3 class="course-title">{{ course.title }}</h3>
              <p class="course-description">{{ course.description }}</p>
              <div class="course-info">
                <span class="teacher">{{ course.teacher?.name }}</span>
                <span class="category">{{ course.category?.name }}</span>
              </div>
            </div>
          </el-card>
        </div>
      </div>
      
      <!-- 最新上线 -->
      <div class="course-section">
        <div class="section-header">
          <el-icon size="24" color="#409EFF"><Clock /></el-icon>
          <h2>最新上线</h2>
          <el-button link type="primary" @click="$router.push('/courses')">更多 ></el-button>
        </div>
        <div class="course-grid">
          <el-card 
            v-for="course in newCourses" 
            :key="course.id" 
            class="course-card"
            shadow="hover"
            @click="handleCourseClick(course.id)"
          >
            <template #header>
              <div class="card-header">
                <img :src="course.coverImage" :alt="course.title" class="course-cover" />
                <el-tag 
                  :type="course.isFree ? 'success' : 'danger'" 
                  size="small" 
                  class="price-tag"
                >
                  {{ course.isFree ? '免费' : `¥${course.price}` }}
                </el-tag>
              </div>
            </template>
            <div class="card-body">
              <h3 class="course-title">{{ course.title }}</h3>
              <p class="course-description">{{ course.description }}</p>
              <div class="course-info">
                <span class="teacher">{{ course.teacher?.name }}</span>
                <span class="category">{{ course.category?.name }}</span>
              </div>
            </div>
          </el-card>
        </div>
      </div>
      
      <!-- 精选推荐 -->
      <div class="course-section">
        <div class="section-header">
          <el-icon size="24" color="#E6A23C"><Star /></el-icon>
          <h2>精选推荐</h2>
          <el-button link type="primary" @click="$router.push('/courses')">更多 ></el-button>
        </div>
        <div class="course-grid">
          <el-card 
            v-for="course in featuredCourses" 
            :key="course.id" 
            class="course-card"
            shadow="hover"
            @click="handleCourseClick(course.id)"
          >
            <template #header>
              <div class="card-header">
                <img :src="course.coverImage" :alt="course.title" class="course-cover" />
                <el-tag 
                  :type="course.isFree ? 'success' : 'danger'" 
                  size="small" 
                  class="price-tag"
                >
                  {{ course.isFree ? '免费' : `¥${course.price}` }}
                </el-tag>
              </div>
            </template>
            <div class="card-body">
              <h3 class="course-title">{{ course.title }}</h3>
              <p class="course-description">{{ course.description }}</p>
              <div class="course-info">
                <span class="teacher">{{ course.teacher?.name }}</span>
                <span class="category">{{ course.category?.name }}</span>
              </div>
            </div>
          </el-card>
        </div>
      </div>
      

    </el-main>
    
    <!-- 底部 -->
    <el-footer class="custom-footer">

      <div class="footer-wrapper">

        <div class="footer-bottom">
          <p>© 2026 在线学习平台. All rights reserved. | 京ICP备xxxxxx号</p>
        </div>
      </div>
    </el-footer>
  </div>
</template>

<style scoped>
.home-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏样式 */
.header {
  background-color: #545c64;
  padding: 0;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
}

.logo h2 {
  color: white;
  margin: 0;
  font-size: 1.5rem;
}

.nav-menu {
  flex: 1;
  margin: 0 20px;
}

.search-box {
  width: 300px;
  margin-right: 20px;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  color: white;
}

/* 主要内容区域 */
.main-content {
  flex: 1;
  padding: 0;
  margin-top: 60px; /* 为固定导航栏留出空间 */
}

/* 轮播图样式 */
.carousel-section {
  margin-bottom: 40px;
}

.carousel-item {
  height: 400px;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.carousel-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
}

.carousel-content {
  text-align: center;
  color: white;
  position: relative;
  z-index: 1;
  max-width: 800px;
  padding: 0 20px;
}

.carousel-content h2 {
  font-size: 2.5rem;
  margin-bottom: 20px;
  font-weight: 600;
}

.carousel-content p {
  font-size: 1.2rem;
  margin-bottom: 30px;
  opacity: 0.9;
}

/* 课程板块样式 */
.course-section {
  padding: 40px 20px;
  max-width: 1200px;
  margin: 0 auto 40px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 30px;
}

.section-header h2 {
  margin: 0;
  font-size: 1.8rem;
  color: #333;
  flex: 1;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.course-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 12px;
  overflow: hidden;
}

.course-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.card-header {
  position: relative;
  height: 160px;
  overflow: hidden;
}

.course-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.course-card:hover .course-cover {
  transform: scale(1.05);
}

.price-tag {
  position: absolute;
  top: 12px;
  right: 12px;
  font-weight: bold;
}

.card-body {
  padding: 20px;
}

.course-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0 0 12px 0;
  color: #333;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-description {
  color: #666;
  font-size: 0.9rem;
  margin: 0 0 16px 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.85rem;
  color: #999;
}

.teacher {
  font-weight: 500;
}

.category {
  background: #f0f2f5;
  padding: 2px 8px;
  border-radius: 4px;
  color: #666;
}

/* 底部样式 - 基于 Element Plus el-footer */
.custom-footer {
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%) !important;
  color: #ffffff !important;
  margin-top: 60px;
  padding: 0 !important;
  border-top: none !important;
}

.footer-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 30px 40px;
  box-sizing: border-box;
}

.footer-main {
  margin-bottom: 20px;
}

.about-section h3 {
  color: #ffffff;
  margin-bottom: 25px;
  font-size: 1.5rem;
  font-weight: 600;
  position: relative;
  padding-bottom: 12px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.about-section h3::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #3498db, #2980b9);
  border-radius: 2px;
}

.about-section p {
  color: #e8e8e8;
  line-height: 1.8;
  margin: 0;
  font-size: 1.1rem;
  opacity: 0.95;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #415a77;
  color: #000000;
  font-size: 0.95rem;
  opacity: 0.9;
  text-shadow: 0 1px 1px rgba(255, 255, 255, 0.2);
}

.footer-bottom p {
  margin: 0;
  letter-spacing: 0.8px;
}

.footer-bottom:hover {
  opacity: 1;
}



/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-wrap: wrap;
    padding: 0 15px;
  }
  
  .nav-menu {
    margin: 10px 0;
    width: 100%;
  }
  
  .search-box {
    width: 100%;
    margin: 10px 0;
  }
  
  .user-actions {
    width: 100%;
    justify-content: center;
  }
  
  .carousel-content h2 {
    font-size: 2rem;
  }
  
  .carousel-content p {
    font-size: 1rem;
  }
  
  .course-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .footer-wrapper {
    padding: 40px 20px 30px;
  }
  
  .about-section {
    text-align: center;
  }
  
  .about-section h3::after {
    left: 50%;
    transform: translateX(-50%);
  }
}
</style>