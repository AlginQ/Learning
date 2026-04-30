<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Edit, Delete, Plus, View } from '@element-plus/icons-vue'
import { getTeacherCoursesApi as getCoursesApi, deleteCourseApi } from '@/api/course'

const router = useRouter()
const userStore = useUserStore()

interface CourseItem {
  id: number
  title: string
  description: string
  categoryId: number
  studentCount: number
  status: number
  auditStatus: number
  createTime: string
}

const loading = ref(false)
const courses = ref<CourseItem[]>([])
const searchKeyword = ref('')

// 获取课程列表
const loadCourses = async () => {
  loading.value = true
  try {
    const userId = userStore.currentUser?.id || 0
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }
    const response = await getCoursesApi(userId)
    if (response.code === 200) {
      courses.value = response.data || []
      console.log('课程数据已加载')
    } else {
      ElMessage.error('获取课程列表失败: ' + (response.msg || '未知错误'))
    }
  } catch (error: any) {
    ElMessage.error('获取课程列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 获取分类名称（与数据库category表一致）
const getCategoryName = (categoryId: number): string => {
  const categoryMap: Record<number, string> = {
    1: '全部',
    2: '前端开发',
    3: '后端开发',
    4: '移动开发',
    5: '编程语言',
    6: '数据库',
    7: '人工智能',
    8: '运维技术'
  }
  return categoryMap[categoryId] || '其他'
}

// 获取审核状态文本
const getAuditStatusText = (status: number): string => {
  const statusMap: Record<number, string> = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝'
  }
  return statusMap[status] || '未知'
}

// 获取审核状态类型
const getAuditStatusType = (status: number): string => {
  const typeMap: Record<number, string> = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return typeMap[status] || 'info'
}

// 导航到创建课程
const goToCreateCourse = () => {
  router.push('/teacher/courses/create')
}

// 导航到编辑课程
const goToEditCourse = (id: number) => {
  router.push(`/teacher/courses/${id}/edit`)
}

// 查看课程详情
const viewCourseDetail = (id: number) => {
  router.push(`/course/${id}`)
}

// 删除课程
const deleteCourse = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这门课程吗？此操作不可恢复。', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await deleteCourseApi(id)
    
    if (response.code === 200) {
      // 从列表中移除
      courses.value = courses.value.filter(course => course.id !== id)
      ElMessage.success('课程删除成功')
    } else {
      ElMessage.error('课程删除失败: ' + response.message)
    }
  } catch (error: any) {
    if (error.message !== 'cancel') {
      ElMessage.error('操作失败: ' + error.message)
    }
  }
}

onMounted(() => {
  loadCourses()
})
</script>

<template>
  <div class="teacher-courses">
    <div class="page-header">
      <h1>课程管理</h1>
      <p>管理您发布的所有课程</p>
    </div>
    
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索课程名称"
            clearable
          >
            <template #prepend>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="16">
          <el-button type="primary" @click="loadCourses"><el-icon><Refresh /></el-icon> 刷新</el-button>
          <el-button type="success" :icon="Plus" @click="goToCreateCourse">
            发布新课程
          </el-button>
        </el-col>
      </el-row>
    </el-card>
    
    <el-card class="table-card">
      <el-table
        :data="courses"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="课程名称" min-width="200" />
        <el-table-column label="分类" width="120">
          <template #default="{ row }">
            {{ getCategoryName(row.categoryId) }}
          </template>
        </el-table-column>
        <el-table-column prop="studentCount" label="学习人数" width="100" />
        <el-table-column label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getAuditStatusType(row.auditStatus)">
              {{ getAuditStatusText(row.auditStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="课程状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已上架' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ new Date(row.createTime).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <div class="operation-container">
              <el-button 
                v-if="row.auditStatus === 1" 
                size="small" 
                :icon="View" 
                @click="viewCourseDetail(row.id)"
              >查看</el-button>
              <el-button size="small" :icon="Edit" @click="goToEditCourse(row.id)">编辑</el-button>
              <el-button size="small" type="danger" :icon="Delete" @click="deleteCourse(row.id)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.teacher-courses {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  color: #333;
  margin: 0 0 10px;
}

.page-header p {
  color: #666;
  margin: 0;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

/* 优化按钮样式 */
.el-table .el-button {
  border-radius: 4px;
  font-size: 12px;
  padding: 4px 12px;
  margin: 0 4px 4px 0;
  min-width: 60px;
}

.el-table .el-button:last-child {
  margin-right: 0;
}

/* 优化查看按钮 */
.el-table .el-button--primary {
  background-color: #409eff;
  border-color: #409eff;
}

.el-table .el-button--primary:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

/* 优化编辑按钮 */
.el-table .el-button--info {
  background-color: #909399;
  border-color: #909399;
}

.el-table .el-button--info:hover {
  background-color: #a6a9ad;
  border-color: #a6a9ad;
}

/* 优化删除按钮 */
.el-table .el-button--danger {
  background-color: #f56c6c;
  border-color: #f56c6c;
}

.el-table .el-button--danger:hover {
  background-color: #f78989;
  border-color: #f78989;
}

/* 优化状态标签 */
.el-table .el-tag {
  border-radius: 10px;
  padding: 2px 10px;
  font-size: 12px;
}

/* 优化表格行间距 */
.el-table__row {
  height: 80px;
}

/* 优化表格内容对齐 */
.el-table td {
  vertical-align: middle;
  padding-top: 0;
}

/* 操作列容器 */
.operation-container {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  min-height: 60px;
  gap: 4px;
}
</style>