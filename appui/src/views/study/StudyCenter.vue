<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getStudyRecordsApi } from '@/api/study'
import { getCourseDetailApi } from '@/api/course'
import type { Course } from '@/types/course'
import { VideoPlay, Document, Clock, User } from '@element-plus/icons-vue'

const userStore = useUserStore()
const loading = ref(false)
const studyRecords = ref<any[]>([])

const stats = reactive({
  totalCourses: 0,
  totalHours: 0,
  completedLessons: 0,
  avgProgress: 0
})

// 获取学习记录
const loadStudyRecords = async () => {
  if (!userStore.isLogin) return
  
  loading.value = true
  try {
    const response = await getStudyRecordsApi({ page: 1, size: 10 })
    studyRecords.value = response.data.records
    
    // 计算统计信息
    stats.totalCourses = response.data.total
    stats.totalHours = studyRecords.value.reduce((sum, record) => 
      sum + (record.studyTime || 0), 0) / 3600
    stats.completedLessons = studyRecords.value.reduce((sum, record) => 
      sum + (record.completedLessons || 0), 0)
    stats.avgProgress = studyRecords.value.length > 0 
      ? studyRecords.value.reduce((sum, record) => sum + record.totalProgress, 0) / studyRecords.value.length
      : 0
  } catch (error) {
    console.error('获取学习记录失败:', error)
  } finally {
    loading.value = false
  }
}

// 继续学习
const continueStudy = (courseId: number) => {
  // 跳转到课程学习页面
  window.open(`/course/${courseId}`, '_blank')
}

onMounted(() => {
  loadStudyRecords()
})
</script>

<template>
  <div class="study-center-container">
    <div class="page-header">
      <h1>学习中心</h1>
      <p>欢迎回来，{{ userStore.currentUser?.nickname || userStore.currentUser?.username }}！</p>
    </div>
    
    <!-- 学习统计 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon" style="background-color: #409eff;">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.totalCourses }}</div>
              <div class="stat-label">学习课程</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon" style="background-color: #67c23a;">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.totalHours.toFixed(1) }}</div>
              <div class="stat-label">学习时长(小时)</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon" style="background-color: #e6a23c;">
              <el-icon><VideoPlay /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.completedLessons }}</div>
              <div class="stat-label">完成课时</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon" style="background-color: #f56c6c;">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.avgProgress.toFixed(1) }}%</div>
              <div class="stat-label">平均进度</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    
    <!-- 学习记录 -->
    <div class="records-section">
      <div class="section-header">
        <h2>我的学习记录</h2>
      </div>
      
      <el-skeleton v-if="loading" :rows="5" animated />
      
      <div v-else-if="studyRecords.length === 0" class="empty-state">
        <el-empty description="暂无学习记录，快去选择感兴趣的课程开始学习吧！">
          <el-button type="primary" @click="$router.push('/courses')">浏览课程</el-button>
        </el-empty>
      </div>
      
      <div v-else class="records-list">
        <div
          v-for="record in studyRecords"
          :key="record.courseId"
          class="record-item"
        >
          <div class="course-cover">
            <img :src="record.courseCover || '/placeholder-course.jpg'" :alt="record.courseTitle" />
          </div>
          
          <div class="record-info">
            <h3 class="course-title">{{ record.courseTitle }}</h3>
            <p class="last-lesson">最近学习：{{ record.lastLesson?.title || '暂无' }}</p>
            
            <div class="progress-info">
              <div class="progress-bar">
                <el-progress :percentage="record.totalProgress || 0" :show-text="false" />
              </div>
              <span class="progress-text">{{ record.totalProgress || 0 }}%</span>
            </div>
            
            <div class="record-meta">
              <span>学习时长：{{ Math.floor((record.studyTime || 0) / 3600) }}小时</span>
              <span>最后学习：{{ record.lastStudyTime ? new Date(record.lastStudyTime).toLocaleDateString() : '从未学习' }}</span>
            </div>
          </div>
          
          <div class="record-actions">
            <el-button type="primary" @click="continueStudy(record.courseId)">
              继续学习
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.study-center-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  margin-bottom: 10px;
  color: #333;
}

.page-header p {
  color: #666;
  font-size: 16px;
}

.stats-section {
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-icon .el-icon {
  font-size: 24px;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.records-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.section-header h2 {
  margin: 0 0 20px;
  color: #333;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

.records-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.record-item {
  display: flex;
  gap: 20px;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  transition: box-shadow 0.3s;
}

.record-item:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
}

.course-cover {
  flex-shrink: 0;
}

.course-cover img {
  width: 120px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.record-info {
  flex: 1;
}

.course-title {
  font-size: 18px;
  font-weight: bold;
  margin: 0 0 10px;
  color: #333;
}

.last-lesson {
  color: #666;
  margin: 0 0 15px;
  font-size: 14px;
}

.progress-info {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.progress-bar {
  flex: 1;
}

.progress-text {
  font-weight: bold;
  color: #409eff;
  min-width: 50px;
}

.record-meta {
  display: flex;
  gap: 20px;
  color: #999;
  font-size: 12px;
}

.record-actions {
  display: flex;
  align-items: center;
}
</style>