<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Star, Delete, Search } from '@element-plus/icons-vue'

const userStore = useUserStore()
const activeTab = ref('collections')
const loading = ref(false)

// 模拟收藏数据
const collections = ref([
  {
    id: 1,
    courseId: 1,
    courseTitle: 'Java基础教程',
    coverImage: 'https://picsum.photos/400/225?random=1',
    category: '编程语言',
    teacher: '张老师',
    rating: 4.8,
    studentCount: 12500,
    createdAt: '2024-01-15 14:30:00'
  },
  {
    id: 2,
    courseId: 2,
    courseTitle: 'Vue 3从入门到实战',
    coverImage: 'https://picsum.photos/400/225?random=2',
    category: '前端开发',
    teacher: '李老师',
    rating: 4.9,
    studentCount: 8900,
    createdAt: '2024-01-14 10:15:00'
  },
  {
    id: 3,
    courseId: 3,
    courseTitle: 'Python数据分析入门',
    coverImage: 'https://picsum.photos/400/225?random=3',
    category: '数据科学',
    teacher: '王老师',
    rating: 4.7,
    studentCount: 6700,
    createdAt: '2024-01-13 16:20:00'
  },
  {
    id: 4,
    courseId: 4,
    courseTitle: 'Spring Boot企业级开发',
    coverImage: 'https://picsum.photos/400/225?random=4',
    category: '后端开发',
    teacher: '赵老师',
    rating: 4.9,
    studentCount: 10200,
    createdAt: '2024-01-12 09:45:00'
  }
])

const handleDeleteCollection = (id: number) => {
  ElMessageBox.confirm('确定要取消收藏这门课程吗？', '提示', {
    type: 'warning'
  }).then(() => {
    collections.value = collections.value.filter(item => item.id !== id)
    ElMessage.success('取消收藏成功')
  })
}

const handleGoToCourse = (courseId: number) => {
  // 跳转到课程详情页面
  window.location.href = `/course/${courseId}`
}
</script>

<template>
  <div class="collections-container">
    <div class="page-header">
      <h1>我的收藏</h1>
    </div>
    
    <div class="content-wrapper">
      <div class="collections-filter">
        <el-input
          placeholder="搜索收藏课程..."
          style="width: 300px; margin-right: 20px;"
          :prefix-icon="Search"
        />
        <el-select placeholder="选择分类" style="width: 200px;">
          <el-option label="全部分类" value=""></el-option>
          <el-option label="编程语言" value="1"></el-option>
          <el-option label="前端开发" value="2"></el-option>
          <el-option label="后端开发" value="3"></el-option>
          <el-option label="数据科学" value="4"></el-option>
        </el-select>
      </div>
      
      <div class="collections-list">
        <div class="courses-grid">
          <el-card 
            v-for="item in collections" 
            :key="item.id"
            class="course-card"
            shadow="hover"
          >
            <div class="course-cover" @click="handleGoToCourse(item.courseId)">
              <img :src="item.coverImage" alt="课程封面" />
              <div class="course-rating">
                <el-icon><Star /></el-icon>
                <span>{{ item.rating }}</span>
              </div>
            </div>
            
            <div class="course-info">
              <h3 class="course-title" @click="handleGoToCourse(item.courseId)">{{ item.courseTitle }}</h3>
              <div class="course-meta">
                <span class="category-tag">{{ item.category }}</span>
                <span class="author">{{ item.teacher }}</span>
                <span class="views">{{ item.studentCount }}人学习</span>
              </div>
              <div class="course-actions">
                <el-button type="primary" size="small" @click="handleGoToCourse(item.courseId)">继续学习</el-button>
                <el-button type="danger" size="small" @click="handleDeleteCollection(item.id)">
                  <el-icon><Delete /></el-icon>
                  取消收藏
                </el-button>
              </div>
              <div class="collection-time">
                收藏时间：{{ item.createdAt }}
              </div>
            </div>
          </el-card>
        </div>
        
        <el-empty v-if="collections.length === 0" description="暂无收藏课程，去发现更多精彩课程吧！" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.collections-container {
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

.collections-filter {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.collections-list {
  margin-top: 20px;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.course-card {
  border-radius: 8px;
  transition: all 0.3s;
  overflow: hidden;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.course-cover {
  position: relative;
  height: 180px;
  overflow: hidden;
  cursor: pointer;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.course-card:hover .course-cover img {
  transform: scale(1.05);
}

.course-rating {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.6);
  color: #ffd700;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.course-info {
  padding: 16px;
}

.course-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin: 0 0 12px 0;
  cursor: pointer;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.course-title:hover {
  color: #409eff;
}

.course-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
  font-size: 14px;
  color: #666;
}

.category-tag {
  background: #ecf5ff;
  color: #409eff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.course-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}

.collection-time {
  font-size: 12px;
  color: #999;
  text-align: right;
}
</style>