<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Edit, Delete, Plus } from '@element-plus/icons-vue'
import { getAllUsersApi } from '@/api/admin'

const route = useRoute()

interface TeacherItem {
  id: number
  username: string
  realName: string
  title: string
  specialty: string
  introduction: string
  avatar: string | null
  createTime: string
}

const loading = ref(false)
const teachers = ref<TeacherItem[]>([])
const searchKeyword = ref('')

// 获取教师列表
const loadTeachers = async () => {
  loading.value = true
  try {
    console.log('开始获取教师列表...')
    const response = await getAllUsersApi()
    console.log('获取用户列表响应:', response)
    if (response.code === 200) {
      // 过滤出角色为TEACHER的用户
      teachers.value = (response.data || []).filter((user: any) => user.role === 'TEACHER')
      console.log('教师数据已加载:', teachers.value)
    } else {
      console.error('获取用户列表失败:', response.message)
      ElMessage.error('获取用户列表失败: ' + response.message)
    }
  } catch (error: any) {
    console.error('获取用户列表错误:', error)
    ElMessage.error('获取用户列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 监听路由变化
watch(
  () => route.fullPath,
  () => {
    loadTeachers()
  }
)

onMounted(() => {
  loadTeachers()
})
</script>

<template>
  <div class="teacher-management">
    <div class="page-header">
      <h1>教师管理</h1>
      <p>管理系统中的所有教师</p>
    </div>
    
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索教师姓名或职称"
            clearable
          >
            <template #prepend>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="16">
          <el-button type="primary">搜索</el-button>
          <el-button @click="loadTeachers">刷新</el-button>
        </el-col>
      </el-row>
    </el-card>
    
    <el-card class="table-card">
      <el-table
        :data="teachers"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="title" label="职称" />
        <el-table-column prop="specialty" label="专业" />
        <el-table-column prop="introduction" label="简介" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ new Date(row.createTime).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="operation-container">
              <el-button size="small" :icon="Edit">编辑</el-button>
              <el-button 
                size="small" 
                type="danger" 
                :icon="Delete"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.teacher-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  color: #333;
  margin: 0 0 10px;
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

/* 优化编辑按钮 */
.el-table .el-button--primary {
  background-color: #409eff;
  border-color: #409eff;
}

.el-table .el-button--primary:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
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