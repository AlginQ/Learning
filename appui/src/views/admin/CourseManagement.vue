<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Edit, Delete, Plus } from '@element-plus/icons-vue'

const route = useRoute()

interface CourseItem {
  id: number
  title: string
  description: string
  price: number
  discountPrice?: number
  status: number
  recommend: number
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

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
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
    // 模拟API延迟
    await new Promise(resolve => setTimeout(resolve, 500))
    courses.value = mockCourses
    pagination.value.total = mockCourses.length
  } catch (error: any) {
    ElMessage.error('获取课程列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

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
    
    // 模拟删除成功
    ElMessage.success('课程删除成功')
    loadCourses()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除课程失败: ' + error.message)
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
    
    // 模拟操作成功
    course.status = course.status === 1 ? 0 : 1
    ElMessage.success(`${action}课程成功`)
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}课程失败: ` + error.message)
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
    
    // 模拟操作成功
    course.recommend = course.recommend === 1 ? 0 : 1
    ElMessage.success(`${action}课程成功`)
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}课程失败: ` + error.message)
    }
  }
}

// 保存课程
const saveCourse = async () => {
  try {
    // 模拟保存操作
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    if (editMode.value) {
      ElMessage.success('课程更新成功')
    } else {
      ElMessage.success('课程创建成功')
    }
    
    dialogVisible.value = false
    loadCourses()
  } catch (error: any) {
    ElMessage.error('保存课程失败: ' + error.message)
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
    <!-- 页面头部 -->
    <div class="page-header">
      <h1>课程管理</h1>
      <p>管理系统中的所有课程</p>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-card">
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
          <el-button :icon="Refresh" @click="loadCourses">刷新</el-button>
          <el-button type="success" :icon="Plus" @click="handleAddCourse">
            添加课程
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 课程列表 -->
    <el-card class="table-card">
      <el-table
        :data="courses"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
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
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="推荐" width="80">
          <template #default="{ row }">
            <el-tag :type="row.recommend === 1 ? 'primary' : 'info'">
              {{ row.recommend === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ new Date(row.createTime).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button 
              size="small" 
              @click="handleEditCourse(row)"
              :icon="Edit"
            >
              编辑
            </el-button>
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
            <el-button 
              size="small" 
              type="danger" 
              :icon="Delete"
              @click="handleDeleteCourse(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
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
</style>