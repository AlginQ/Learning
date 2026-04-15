<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Check, Close, UserFilled } from '@element-plus/icons-vue'
import { getPendingTeacherAppliesApi, reviewTeacherApplyApi } from '@/api/admin'

interface UserItem {
  id: number
  username: string
  email: string
  nickname: string
  role: string
  status: number
  createTime: string
}

interface TeacherApplyItem {
  id: number
  userId: number
  realName: string
  major: string
  qualification: string
  status: number
  createTime: string
  user?: UserItem
}

const loadingApplies = ref(false)
const teacherApplies = ref<TeacherApplyItem[]>([])
const searchKeyword = ref('')

// 获取教师申请列表
const loadTeacherApplies = async () => {
  loadingApplies.value = true
  try {
    const response = await getPendingTeacherAppliesApi()
    if (response.code === 200) {
      teacherApplies.value = response.data || []
      console.log('教师申请数据已加载')
    } else {
      ElMessage.error('获取教师申请列表失败: ' + response.message)
    }
  } catch (error: any) {
    ElMessage.error('获取教师申请列表失败: ' + error.message)
  } finally {
    loadingApplies.value = false
  }
}

// 处理教师申请
const handleTeacherApply = async (apply: TeacherApplyItem, status: number) => {
  try {
    await ElMessageBox.confirm(`确定要${status === 1 ? '通过' : '拒绝'}该教师申请吗？`, '申请处理确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await reviewTeacherApplyApi(apply.id, status)
    
    if (response.code === 200) {
      // 更新本地数据
      apply.status = status
      
      // 如果通过申请，更新用户角色
      if (status === 1 && apply.user) {
        apply.user.role = 'TEACHER'
      }
      
      ElMessage.success(`${status === 1 ? '通过' : '拒绝'}申请成功`)
    } else {
      ElMessage.error(`${status === 1 ? '通过' : '拒绝'}申请失败: ` + response.message)
    }
  } catch (error: any) {
    if (error.message !== 'cancel') {
      ElMessage.error('操作失败: ' + error.message)
    }
  }
}

onMounted(() => {
  loadTeacherApplies()
})
</script>

<template>
  <div class="teacher-apply-management">
    <div class="page-header">
      <h1>教师申请管理</h1>
      <p>管理用户的教师申请</p>
    </div>
    
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索申请人"
            clearable
          >
            <template #prepend>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="16">
          <el-button type="primary">搜索</el-button>
          <el-button @click="loadTeacherApplies">刷新</el-button>
        </el-col>
      </el-row>
    </el-card>
    
    <el-card class="table-card">
      <el-table
        :data="teacherApplies"
        v-loading="loadingApplies"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="申请ID" width="80" />
        <el-table-column label="申请人" width="150">
          <template #default="{ row }">
            {{ row.user?.username }} ({{ row.realName }})
          </template>
        </el-table-column>
        <el-table-column prop="major" label="专业" width="150" />
        <el-table-column prop="qualification" label="资质说明" min-width="200" />
        <el-table-column prop="createTime" label="申请时间" width="180">
          <template #default="{ row }">
            {{ new Date(row.createTime).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'info' : row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 0 ? '待审核' : row.status === 1 ? '已通过' : '已拒绝' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <template v-if="row.status === 0">
                <div class="operation-container">
                  <el-button size="small" type="success" :icon="Check" @click="handleTeacherApply(row, 1)">通过</el-button>
                  <el-button size="small" type="danger" :icon="Close" @click="handleTeacherApply(row, 2)">拒绝</el-button>
                </div>
              </template>
              <template v-else>
                <div class="processed-container">
                  <el-tag type="info">已处理</el-tag>
                </div>
              </template>
            </template>
          </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.teacher-apply-management {
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
  width: 60px;
  margin: 0;
}

/* 优化通过按钮 */
.el-table .el-button--success {
  background-color: #67c23a;
  border-color: #67c23a;
}

.el-table .el-button--success:hover {
  background-color: #85ce61;
  border-color: #85ce61;
}

/* 优化拒绝按钮 */
.el-table .el-button--danger {
  background-color: #f56c6c;
  border-color: #f56c6c;
}

.el-table .el-button--danger:hover {
  background-color: #f78989;
  border-color: #f78989;
}

/* 优化已处理标签 */
.el-table .el-tag--info {
  background-color: #ecf5ff;
  border-color: #d9ecff;
  color: #409eff;
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
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  min-height: 60px;
  justify-content: center;
}

/* 已处理标签容器 */
.processed-container {
  display: flex;
  align-items: center;
  min-height: 60px;
}
</style>