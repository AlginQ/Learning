<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Edit, Delete, Plus, Refresh } from '@element-plus/icons-vue'
import {
  getAllUsersApi,
  deleteUserApi,
  disableUserApi,
  enableUserApi,
  updateUserRoleApi,
  addUserApi,
  updateUserApi
} from '@/api/admin'

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
const currentUser = ref<Partial<UserItem>>({
})

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 获取用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    console.log('开始获取用户列表...')
    const response = await getAllUsersApi()
    console.log('获取用户列表响应:', response)
    if (response.code === 200) {
      users.value = response.data || []
      pagination.value.total = users.value.length
      console.log('用户数据已加载:', users.value)
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

// 修改用户角色
const changeUserRole = async (user: UserItem, newRole: string) => {
  try {
    await ElMessageBox.confirm(`确定要将用户 ${user.username} 的角色修改为 ${newRole === 'TEACHER' ? '教师' : '普通用户'} 吗？`, '角色修改确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用修改角色的API
    const response = await updateUserRoleApi(user.id, newRole)
    
    if (response.code === 200) {
      // 更新本地数据
      user.role = newRole
      ElMessage.success('角色修改成功')
    } else {
      ElMessage.error('角色修改失败: ' + response.message)
    }
  } catch (error: any) {
    if (error.message !== 'cancel') {
      ElMessage.error('操作失败: ' + error.message)
    }
  }
}

// 禁用/启用用户
const toggleUserStatus = async (user: UserItem) => {
  try {
    await ElMessageBox.confirm(`确定要${user.status === 1 ? '禁用' : '启用'}用户 ${user.username} 吗？`, '状态修改确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const apiCall = user.status === 1 ? disableUserApi(user.id) : enableUserApi(user.id)
    const response = await apiCall
    
    if (response.code === 200) {
      user.status = user.status === 1 ? 0 : 1
      ElMessage.success(`${user.status === 1 ? '启用' : '禁用'}成功`)
    } else {
      ElMessage.error(`${user.status === 1 ? '禁用' : '启用'}失败: ` + response.message)
    }
  } catch (error: any) {
    if (error.message !== 'cancel') {
      ElMessage.error('操作失败: ' + error.message)
    }
  }
}

// 删除用户
const deleteUser = async (user: UserItem) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户 ${user.username} 吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'danger'
    })
    
    const response = await deleteUserApi(user.id)
    
    if (response.code === 200) {
      // 从列表中移除用户
      users.value = users.value.filter(u => u.id !== user.id)
      pagination.value.total = users.value.length
      ElMessage.success('删除成功')
    } else {
      ElMessage.error('删除失败: ' + response.message)
    }
  } catch (error: any) {
    if (error.message !== 'cancel') {
      ElMessage.error('操作失败: ' + error.message)
    }
  }
}

// 搜索用户
const handleSearch = () => {
  loadUsers()
}

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = ''
  loadUsers()
}

// 过滤用户列表
const filteredUsers = computed(() => {
  if (!searchKeyword.value) {
    return users.value
  }
  const keyword = searchKeyword.value.toLowerCase()
  return users.value.filter(user => 
    user.username.toLowerCase().includes(keyword) ||
    user.email.toLowerCase().includes(keyword) ||
    user.nickname.toLowerCase().includes(keyword)
  )
})

// 打开添加用户对话框
const openAddUserDialog = () => {
  editMode.value = false
  currentUser.value = {
    status: 1,
    role: 'USER'
  }
  dialogVisible.value = true
}

// 打开编辑用户对话框
const openEditUserDialog = (user: UserItem) => {
  editMode.value = true
  currentUser.value = { ...user }
  dialogVisible.value = true
}

// 保存用户
const saveUser = async () => {
  try {
    if (editMode.value) {
      // 编辑用户
      await updateUserApi(currentUser.value.id, currentUser.value)
      ElMessage.success('用户更新成功')
    } else {
      // 添加用户
      await addUserApi(currentUser.value)
      ElMessage.success('用户创建成功')
    }
    
    dialogVisible.value = false
    loadUsers()
  } catch (error: any) {
    ElMessage.error('保存用户失败: ' + (error.response?.data?.message || error.message))
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
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" :icon="Plus" @click="openAddUserDialog">
            添加用户
          </el-button>
        </el-col>
      </el-row>
    </el-card>
    
    <el-card class="table-card">
      <el-table
        :data="filteredUsers"
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
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : row.role === 'TEACHER' ? 'warning' : 'primary'">
              {{ row.role === 'ADMIN' ? '管理员' : row.role === 'TEACHER' ? '教师' : '普通用户' }}
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
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <div class="operation-container">
              <el-button size="small" :icon="Edit" @click="openEditUserDialog(row)">编辑</el-button>
              <el-button 
                size="small" 
                :type="row.status === 1 ? 'warning' : 'success'"
                @click="toggleUserStatus(row)"
              >
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button 
                v-if="row.role !== 'ADMIN'" 
                size="small" 
                :type="row.role === 'TEACHER' ? 'info' : 'primary'"
                @click="changeUserRole(row, row.role === 'TEACHER' ? 'USER' : 'TEACHER')"
              >
                {{ row.role === 'TEACHER' ? '取消教师' : '设为教师' }}
              </el-button>
              <el-button 
                size="small" 
                type="danger" 
                :icon="Delete"
                @click="deleteUser(row)"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 用户编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editMode ? '编辑用户' : '添加用户'"
      width="600px"
    >
      <el-form :model="currentUser" label-width="100px">
        <el-form-item label="用户名" required :rules="[
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 4, max: 20, message: '用户名长度为4-20位', trigger: 'blur' },
          {
            pattern: /^[a-zA-Z0-9_]+$/,
            message: '用户名只能包含字母、数字和下划线',
            trigger: 'blur'
          }
        ]">
          <el-input v-model="currentUser.username" placeholder="请输入用户名" :disabled="editMode" />
        </el-form-item>
        <el-form-item label="邮箱" required>
          <el-input v-model="currentUser.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="昵称" required>
          <el-input v-model="currentUser.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="角色" required>
          <el-select v-model="currentUser.role" placeholder="请选择角色">
            <el-option label="普通用户" value="USER" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="currentUser.status"
            :active-value="1"
            :inactive-value="0"
            active-text="正常"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveUser">保存</el-button>
        </span>
      </template>
    </el-dialog>
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

/* 优化禁用/启用按钮 */
.el-table .el-button--warning {
  background-color: #e6a23c;
  border-color: #e6a23c;
}

.el-table .el-button--warning:hover {
  background-color: #ebb563;
  border-color: #ebb563;
}

.el-table .el-button--success {
  background-color: #67c23a;
  border-color: #67c23a;
}

.el-table .el-button--success:hover {
  background-color: #85ce61;
  border-color: #85ce61;
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

/* 优化角色标签 */
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