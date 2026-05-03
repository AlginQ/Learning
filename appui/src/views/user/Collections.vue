<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Star, Delete } from '@element-plus/icons-vue'
import { getUserFavoritesApi, removeFavoriteApi, type FavoriteItem } from '@/api/favorite'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const collections = ref<FavoriteItem[]>([])

const loadFavorites = async () => {
  if (!userStore.isLogin) {
    return
  }
  
  loading.value = true
  try {
    const response = await getUserFavoritesApi()
    if (response.code === 200 && response.data) {
      collections.value = response.data
    }
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

const handleDeleteCollection = async (id: number, courseId: number) => {
  ElMessageBox.confirm('确定要取消收藏这门课程吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const response = await removeFavoriteApi(courseId)
      if (response.code === 200) {
        collections.value = collections.value.filter(item => item.id !== id)
        ElMessage.success('取消收藏成功')
      } else {
        ElMessage.error('取消收藏失败')
      }
    } catch (error) {
      console.error('取消收藏失败:', error)
      ElMessage.error('取消收藏失败')
    }
  })
}

const handleGoToCourse = (courseId: number) => {
  router.push(`/course-detail/${courseId}`)
}

onMounted(() => {
  loadFavorites()
})
</script>

<template>
  <div class="collections-container">
    <div class="page-header">
      <h1>我的收藏</h1>
    </div>
    
    <div class="content-wrapper">
      <div class="collections-list">
        <div v-if="loading" class="loading">
          <el-skeleton :rows="6" animated />
        </div>
        
        <div v-else class="courses-grid">
          <el-card 
            v-for="item in collections" 
            :key="item.id"
            class="course-card"
            shadow="hover"
          >
            <div class="course-cover" @click="handleGoToCourse(item.courseId)">
              <img :src="item.courseCover" alt="课程封面" />
              <div class="course-rating">
                <el-icon><Star /></el-icon>
                <span>{{ item.rating }}</span>
              </div>
            </div>
            
            <div class="course-info">
              <h3 class="course-title" @click="handleGoToCourse(item.courseId)">{{ item.courseTitle }}</h3>
              <div class="course-meta">
                <span class="category-tag">{{ item.categoryName }}</span>
                <span class="author">{{ item.teacherName }}</span>
                <span class="views">{{ item.studentCount }}人学习</span>
              </div>
              <div class="course-actions">
                <el-button type="primary" size="small" @click="handleGoToCourse(item.courseId)">继续学习</el-button>
                <el-button type="danger" size="small" @click="handleDeleteCollection(item.id, item.courseId)">
                  <el-icon><Delete /></el-icon>
                  取消收藏
                </el-button>
              </div>
              <div class="collection-time">
                收藏时间：{{ item.createTime }}
              </div>
            </div>
          </el-card>
        </div>
        
        <el-empty v-if="!loading && collections.length === 0" description="暂无收藏课程，去发现更多精彩课程吧！" />
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
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.collections-list {
  min-height: 400px;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.course-card {
  overflow: hidden;
  transition: transform 0.2s;
}

.course-card:hover {
  transform: translateY(-4px);
}

.course-cover {
  position: relative;
  height: 160px;
  overflow: hidden;
  cursor: pointer;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-rating {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.6);
  color: #ffd700;
  padding: 4px 10px;
  border-radius: 15px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.course-info {
  padding: 15px;
}

.course-title {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 10px 0;
  color: #333;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-title:hover {
  color: #409eff;
}

.course-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 15px;
}

.category-tag {
  background: #e8f4fd;
  color: #409eff;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
}

.author, .views {
  color: #999;
  font-size: 12px;
}

.course-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.collection-time {
  color: #999;
  font-size: 12px;
}

.loading {
  padding: 20px;
}
</style>