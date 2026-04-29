<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Edit, Delete, Plus } from '@element-plus/icons-vue'
import {
  getCourseListApi,
  addCourseApi,
  updateCourseApi,
  deleteCourseApi,
  toggleCourseStatusApi,
  toggleCourseRecommendApi,
  getPendingCoursesApi,
  auditCourseApi
} from '@/api/course'

const route = useRoute()

interface CourseItem {
  id: number
  title: string
  description: string
  price: number
  discountPrice?: number
  status: number
  recommend: number
  auditStatus: number
  studentCount: number
  rating: number
  createTime: string
  categoryName: string
  teacherName: string
}

const loading = ref(false)
const courses = ref<CourseItem[]>([])
const searchKeyword = ref('')
const dialogVisible = ref(false)
const editMode = ref(false)
const currentCourse = ref<Partial<CourseItem>>({})
const selectedCourses = ref<CourseItem[]>([])
const activeTab = ref<'all' | 'pending'>('all')

const pagination = ref({
  currentPage: 1,
  pageSize: 8,
  total: 13
})

// 模拟课程数据
const mockCourses: CourseItem[] = [
  {
    id: 1,
    title: 'Java基础教程',
    description: '从零开始学习Java编程语言',
    price: 99.00,
    discountPrice: 49.00,
    status: 1,
    recommend: 1,
    studentCount: 1234,
    rating: 4.8,
    createTime: '2024-01-01T00:00:00',
    categoryName: '编程语言',
    teacherName: '张老师'
  },
  {
    id: 2,
    title: 'Vue 3从入门到实战',
    description: '掌握现代前端开发框架Vue 3',
    price: 129.00,
    status: 1,
    recommend: 0,
    studentCount: 856,
    rating: 4.6,
    createTime: '2024-01-02T00:00:00',
    categoryName: '前端开发',
    teacherName: '李老师'
  },
  {
    id: 3,
    title: 'Python数据分析入门',
    description: '学习Python数据分析和可视化',
    price: 159.00,
    discountPrice: 79.00,
    status: 0,
    recommend: 1,
    studentCount: 634,
    rating: 4.7,
    createTime: '2024-01-03T00:00:00',
    categoryName: '数据科学',
    teacherName: '王老师'
  }
]

// 获取课程列表
const loadCourses = async () => {
  loading.value = true
  try {
    // 确保参数类型正确
    const params = {
      page: Number(pagination.value.currentPage),
      size: Number(pagination.value.pageSize),
      keyword: searchKeyword.value
    }
    console.log('Request params:', params)
    const response = await getCourseListApi(params)
    console.log('Response data:', response)
    
    // 处理后端返回的数据
    if (response && response.data) {
      courses.value = response.data.records || []
      // 确保total是数字类型
      pagination.value.total = Number(response.data.total) || courses.value.length
      console.log('Total courses:', courses.value.length)
      console.log('Pagination total:', pagination.value.total)
    } else {
      courses.value = []
      pagination.value.total = 0
    }
  } catch (error: any) {
    console.error('Error:', error)
    ElMessage.error('获取课程列表失败: ' + (error.response?.data?.message || error.message))
    // 即使出错也要设置一个默认的total值，确保分页组件正常显示
    pagination.value.total = 13
  } finally {
    loading.value = false
  }
}

// 页面加载时获取数据
loadCourses()

// 搜索课程
const handleSearch = () => {
  pagination.value.currentPage = 1
  loadCourses()
}

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = ''
  pagination.value.currentPage = 1
  loadCourses()
}

// 过滤课程列表
const filteredCourses = computed(() => {
  return courses.value
})

// 添加课程
const handleAddCourse = () => {
  editMode.value = false
  currentCourse.value = {}
  dialogVisible.value = true
}

// 编辑课程
const handleEditCourse = (course: CourseItem) => {
  editMode.value = true
  currentCourse.value = { ...course }
  dialogVisible.value = true
}

// 删除课程
const handleDeleteCourse = async (course: CourseItem) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除课程 "${course.title}" 吗？此操作不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    
    await deleteCourseApi(course.id)
    ElMessage.success('课程删除成功')
    loadCourses()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除课程失败: ' + (error.response?.data?.message || error.message))
    }
  }
}

// 上架/下架课程
const toggleCourseStatus = async (course: CourseItem) => {
  try {
    const action = course.status === 1 ? '下架' : '上架'
    await ElMessageBox.confirm(
      `确定要${action}课程 "${course.title}" 吗？`,
      `${action}确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await toggleCourseStatusApi(course.id)
    ElMessage.success(`${action}课程成功`)
    loadCourses()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}课程失败: ` + (error.response?.data?.message || error.message))
    }
  }
}

// 推荐/取消推荐课程
const toggleRecommend = async (course: CourseItem) => {
  try {
    const action = course.recommend === 1 ? '取消推荐' : '设为推荐'
    await ElMessageBox.confirm(
      `确定要${action}课程 "${course.title}" 吗？`,
      `${action}确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await toggleCourseRecommendApi(course.id)
    ElMessage.success(`${action}课程成功`)
    loadCourses()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}课程失败: ` + (error.response?.data?.message || error.message))
    }
  }
}

// 获取待审核课程
const loadPendingCourses = async () => {
  loading.value = true
  try {
    const response = await getPendingCoursesApi()
    if (response.code === 200) {
      courses.value = response.data || []
      pagination.value.total = courses.value.length
    } else {
      courses.value = []
      pagination.value.total = 0
    }
  } catch (error: any) {
    ElMessage.error('获取待审核课程失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 审核课程
const handleAuditCourse = async (course: CourseItem, status: number) => {
  try {
    const action = status === 1 ? '通过' : '拒绝'
    await ElMessageBox.confirm(
      `确定要${action}课程 "${course.title}" 的审核吗？`,
      `${action}审核确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await auditCourseApi(course.id, status)
    ElMessage.success(`课程审核${action}成功`)
    if (activeTab.value === 'pending') {
      loadPendingCourses()
    } else {
      loadCourses()
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`审核课程失败: ` + (error.response?.data?.message || error.message))
    }
  }
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

// 保存课程
const saveCourse = async () => {
  try {
    if (editMode.value) {
      // 编辑课程
      await updateCourseApi(currentCourse.value.id as number, currentCourse.value)
      ElMessage.success('课程更新成功')
    } else {
      // 添加课程
      await addCourseApi(currentCourse.value)
      ElMessage.success('课程创建成功')
    }
    
    dialogVisible.value = false
    loadCourses()
  } catch (error: any) {
    ElMessage.error('保存课程失败: ' + (error.response?.data?.message || error.message))
  }
}

// 处理多选框选择
const handleSelectionChange = (val: CourseItem[]) => {
  selectedCourses.value = val
}

// 批量删除课程
const batchDeleteCourses = async () => {
  if (selectedCourses.value.length === 0) {
    ElMessage.warning('请选择要删除的课程')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedCourses.value.length} 门课程吗？此操作不可恢复！`,
      '批量删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    
    // 批量删除课程
    for (const course of selectedCourses.value) {
      await deleteCourseApi(course.id)
    }
    
    ElMessage.success(`成功删除 ${selectedCourses.value.length} 门课程`)
    loadCourses()
    selectedCourses.value = []
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除课程失败: ' + (error.response?.data?.message || error.message))
    }
  }
}

// 分页处理
const handlePageChange = (page: number) => {
  pagination.value.currentPage = page
  loadCourses()
}

const handleSizeChange = (size: number) => {
  pagination.value.pageSize = size
  pagination.value.currentPage = 1
  loadCourses()
}

// 方案b: 监听路由变化，手动刷新数据
watch(
  () => route.fullPath,
  () => {
    // 路由变化时重新加载数据
    loadCourses()
  }
)

onMounted(() => {
  loadCourses()
})
</script>

<template>
  <div class="course-management">

    <!-- 搜索区域 -->
    <el-card class="search-card">
      <!-- 标签页切换 -->
      <div class="tab-switch">
        <el-button 
          :type="activeTab === 'all' ? 'primary' : 'default'" 
          @click="activeTab = 'all'; loadCourses()"
        >
          全部课程
        </el-button>
        <el-button 
          :type="activeTab === 'pending' ? 'primary' : 'default'" 
          @click="activeTab = 'pending'; loadPendingCourses()"
        >
          待审核课程
        </el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索课程标题"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prepend>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="16">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button :icon="Refresh" @click="activeTab === 'pending' ? loadPendingCourses() : loadCourses()">刷新</el-button>
          <el-button type="success" :icon="Plus" @click="handleAddCourse">
            添加课程
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 课程列表 -->
    <el-card class="table-card">
      <div class="batch-actions" v-if="selectedCourses.length > 0">
        <el-button type="danger" @click="batchDeleteCourses">
          批量删除 ({{ selectedCourses.length }})
        </el-button>
      </div>
      <el-table
        :data="filteredCourses"
        v-loading="loading"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="课程标题" min-width="200" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="teacherName" label="讲师" width="100" />
        <el-table-column label="价格" width="120">
          <template #default="{ row }">
            <div>
              <div v-if="row.discountPrice && row.discountPrice < row.price">
                <span class="discount-price">¥{{ row.discountPrice }}</span>
                <div class="original-price">¥{{ row.price }}</div>
              </div>
              <div v-else>
                <span>¥{{ row.price }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="studentCount" label="学习人数" width="100" />
        <el-table-column prop="rating" label="评分" width="80" />
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
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="推荐" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.recommend === 1" type="success">
              推荐
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ new Date(row.createTime).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="350" fixed="right">
          <template #default="{ row }">
            <div class="operation-container">
              <!-- 审核操作 -->
              <div v-if="row.auditStatus === 0" class="audit-group">
                <el-button 
                  size="small" 
                  type="success"
                  @click="handleAuditCourse(row, 1)"
                >
                  通过
                </el-button>
                <el-button 
                  size="small" 
                  type="danger"
                  @click="handleAuditCourse(row, 2)"
                >
                  拒绝
                </el-button>
              </div>
              <div class="button-group">
                <el-button 
                  size="small" 
                  @click="handleEditCourse(row)"
                  :icon="Edit"
                >
                  编辑
                </el-button>
              </div>
              <div class="button-group">
                <el-button 
                  size="small" 
                  :type="row.status === 1 ? 'warning' : 'success'"
                  @click="toggleCourseStatus(row)"
                >
                  {{ row.status === 1 ? '下架' : '上架' }}
                </el-button>
                <el-button 
                  size="small" 
                  :type="row.recommend === 1 ? 'info' : 'primary'"
                  @click="toggleRecommend(row)"
                >
                  {{ row.recommend === 1 ? '取消推荐' : '推荐' }}
                </el-button>
              </div>
              <div class="danger-group">
                <el-button 
                  size="small" 
                  type="danger" 
                  :icon="Delete"
                  @click="handleDeleteCourse(row)"
                >
                  删除
                </el-button>
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          layout="total, prev, pager, next, jumper"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 课程编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editMode ? '编辑课程' : '添加课程'"
      width="600px"
    >
      <el-form :model="currentCourse" label-width="100px">
        <el-form-item label="课程标题" required>
          <el-input v-model="currentCourse.title" placeholder="请输入课程标题" />
        </el-form-item>
        <el-form-item label="课程描述" required>
          <el-input 
            v-model="currentCourse.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入课程描述"
          />
        </el-form-item>
        <el-form-item label="原价" required>
          <el-input-number 
            v-model="currentCourse.price" 
            :precision="2" 
            :step="10"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="折扣价">
          <el-input-number 
            v-model="currentCourse.discountPrice" 
            :precision="2" 
            :step="10"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="currentCourse.categoryName" placeholder="请选择分类">
            <el-option label="编程语言" value="编程语言" />
            <el-option label="前端开发" value="前端开发" />
            <el-option label="后端开发" value="后端开发" />
            <el-option label="数据科学" value="数据科学" />
          </el-select>
        </el-form-item>
        <el-form-item label="讲师">
          <el-input v-model="currentCourse.teacherName" placeholder="请输入讲师姓名" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="currentCourse.status"
            :active-value="1"
            :inactive-value="0"
            active-text="上架"
            inactive-text="下架"
          />
        </el-form-item>
        <el-form-item label="推荐">
          <el-switch
            v-model="currentCourse.recommend"
            :active-value="1"
            :inactive-value="0"
            active-text="推荐"
            inactive-text="不推荐"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveCourse">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.course-management {
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.discount-price {
  color: #ff4d4f;
  font-weight: bold;
  font-size: 16px;
}

.original-price {
  color: #999;
  text-decoration: line-through;
  font-size: 12px;
}

:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-table__header th) {
  background-color: #fafafa;
  color: #666;
  font-weight: 500;
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
  justify-content: space-between;
}

/* 批量操作按钮 */
.batch-actions {
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  border-left: 4px solid #f56c6c;
}

/* 按钮分组样式 */
.button-group {
  display: flex;
  gap: 4px;
}

/* 危险操作按钮组 */
.danger-group {
  margin-left: auto;
}

/* 标签页切换 */
.tab-switch {
  margin-bottom: 15px;
}

.tab-switch .el-button {
  margin-right: 10px;
  border-radius: 20px;
  padding: 6px 20px;
}

/* 审核按钮组 */
.audit-group {
  display: flex;
  gap: 4px;
}
</style>