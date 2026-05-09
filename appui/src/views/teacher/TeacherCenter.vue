<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'
import { VideoPlay, DataAnalysis, User, Management, Document, Plus } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()

const teacherStats = ref({
  courses: 5,
  students: 128,
  totalViews: 3500,
  totalHours: 420
})

const recentCourses = ref([
  { id: 1, title: 'Vue3 从入门到精通', students: 45, views: 1200, status: 'published' },
  { id: 2, title: 'TypeScript 高级编程', students: 38, views: 950, status: 'published' },
  { id: 3, title: 'React 组件设计模式', students: 45, views: 1350, status: 'published' }
])

const goToCourseManagement = () => {
  router.push('/teacher/courses')
}

const goToCreateCourse = () => {
  router.push('/teacher/courses/create')
}

const handleEdit = (id: number) => {
  router.push(`/teacher/courses/${id}/edit`)
}

const handleView = (id: number) => {
  router.push(`/course/${id}`)
}
</script>

<template>
  <div class="teacher-center">
    <div class="page-header">
      <h1>教师中心</h1>
      <p>欢迎回来，{{ userStore.currentUser?.nickname || userStore.currentUser?.username }}</p>
    </div>

    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon bg-blue">
              <el-icon><VideoPlay /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ teacherStats.courses }}</div>
              <div class="stat-label">发布课程</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon bg-green">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ teacherStats.students }}</div>
              <div class="stat-label">学习人数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon bg-orange">
              <el-icon><DataAnalysis /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ teacherStats.totalViews }}</div>
              <div class="stat-label">总播放量</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon bg-purple">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ teacherStats.totalHours }}</div>
              <div class="stat-label">总学习时长(小时)</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="actions-section">
      <el-card class="action-card">
        <div class="action-buttons">
          <el-button type="primary" size="large" @click="goToCreateCourse">
            <el-icon><Plus /></el-icon>
            发布新课程
          </el-button>
          <el-button type="success" size="large" @click="goToCourseManagement">
            <el-icon><Management /></el-icon>
            管理课程
          </el-button>
        </div>
      </el-card>
    </div>

    <div class="recent-courses-section">
      <el-card class="courses-card">
        <template #header>
          <div class="card-header">
            <h3>最近课程</h3>
            <el-button type="primary" link @click="goToCourseManagement">查看全部</el-button>
          </div>
        </template>
        <el-table :data="recentCourses" stripe style="width: 100%">
          <el-table-column prop="title" label="课程名称" min-width="200" />
          <el-table-column prop="students" label="学习人数" width="100" />
          <el-table-column prop="views" label="播放量" width="100" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag type="success" v-if="row.status === 'published'">已发布</el-tag>
              <el-tag type="warning" v-else-if="row.status === 'draft'">草稿</el-tag>
              <el-tag type="info" v-else>审核中</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="handleEdit(row.id)">编辑</el-button>
              <el-button size="small" type="primary" @click="handleView(row.id)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.teacher-center {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  color: #333;
  margin: 0 0 10px;
}

.page-header p {
  color: #666;
  margin: 0;
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
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 60px;
  height: 60px;
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

.bg-blue { background-color: #409eff; }
.bg-green { background-color: #67c23a; }
.bg-orange { background-color: #e6a23c; }
.bg-purple { background-color: #909399; }

.stat-content {
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

.actions-section {
  margin-bottom: 30px;
}

.action-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  padding: 30px;
}

.action-buttons .el-button {
  min-width: 180px;
}

.recent-courses-section {
  margin-bottom: 30px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}
</style>