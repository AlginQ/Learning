<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Edit, Delete, Plus } from '@element-plus/icons-vue'

const route = useRoute()

interface UserItem {
  id: number
  username: string
  email: string
  nickname: string
  role: string
  status: number
  createTime: string
}

const loading = ref(false)
const users = ref<UserItem[]>([])
const searchKeyword = ref('')
const dialogVisible = ref(false)
const editMode = ref(false)
const currentUser = ref<Partial<UserItem>>({})

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 模拟用户数据
const mockUsers: UserItem[] = [
  {
    id: 1,
    username: 'admin',
    email: 'admin@example.com',
    nickname: '管理员',
    role: 'ADMIN',
    status: 1,
    createTime: '2024-01-01T00:00:00'
  },
  {
    id: 2,
    username: 'user1',
    email: 'user1@example.com',
    nickname: '普通用户1',
    role: 'USER',
    status: 1,
    createTime: '2024-01-02T00:00:00'
  },
  {
    id: 3,
    username: 'user2',
    email: 'user2@example.com',
    nickname: '普通用户2',
    role: 'USER',
    status: 0,
    createTime: '2024-01-03T00:00:00'
  }
]

// 获取用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    // 模拟API延迟
    await new Promise(resolve => setTimeout(resolve, 500))
    users.value = mockUsers
    pagination.value.total = mockUsers.length
    console.log('用户数据已加载')
  } catch (error: any) {
    ElMessage.error('获取用户列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 监听路由变化
watch(
  () => route.fullPath,
  () => {
    loadUsers()
  }
)

onMounted(() => {
  loadUsers()
})
</script>

<template>
  <div class="user-management">
    <div class="page-header">
      <h1>用户管理</h1>
      <p>管理系统中的所有用户</p>
    </div>
    
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索用户名或邮箱"
            clearable
          >
            <template #prepend>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="16">
          <el-button type="primary">搜索</el-button>
          <el-button @click="loadUsers">刷新</el-button>
          <el-button type="success" :icon="Plus">
            添加用户
          </el-button>
        </el-col>
      </el-row>
    </el-card>
    
    <el-card class="table-card">
      <el-table
        :data="users"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'">
              {{ row.role === 'ADMIN' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ new Date(row.createTime).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" :icon="Edit">编辑</el-button>
            <el-button 
              size="small" 
              :type="row.status === 1 ? 'warning' : 'success'"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" :icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.user-management {
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
</style>