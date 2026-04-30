<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Document, ArrowLeft } from '@element-plus/icons-vue'
import { addCourseApi } from '@/api/course'

const router = useRouter()
const userStore = useUserStore()

// 课程表单
const courseForm = reactive({
  title: '',
  description: '',
  category: '',
  coverImage: '',
  price: 0,
  isFree: false,
  teacherId: 0
})

// 章节列表
const chapters = ref([
  {
    id: 1,
    title: '',
    videoUrl: '',
    duration: ''
  }
])

// 表单验证规则
const courseRules = {
  title: [
    { required: true, message: '请输入课程标题', trigger: 'blur' },
    { min: 5, max: 100, message: '标题长度为5-100位', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入课程描述', trigger: 'blur' },
    { min: 10, max: 500, message: '描述长度为10-500位', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择课程分类', trigger: 'blur' }
  ],
  coverImage: [
    { required: true, message: '请上传课程封面', trigger: 'blur' }
  ]
}

// 添加章节
const addChapter = () => {
  const newId = chapters.value.length + 1
  chapters.value.push({
    id: newId,
    title: '',
    videoUrl: '',
    duration: ''
  })
}

// 删除章节
const deleteChapter = (index: number) => {
  if (chapters.value.length > 1) {
    chapters.value.splice(index, 1)
  } else {
    ElMessage.warning('至少需要一个章节')
  }
}

// 保存课程
const saveCourse = async () => {
  try {
    // 获取当前用户ID作为教师ID
    const userId = userStore.currentUser?.id || 0
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }
    
    // 验证章节数据
    for (let i = 0; i < chapters.value.length; i++) {
      const chapter = chapters.value[i]
      if (!chapter.title || chapter.title.trim() === '') {
        ElMessage.error(`第 ${i + 1} 个章节标题不能为空`)
        return
      }
      if (!chapter.videoUrl || chapter.videoUrl.trim() === '') {
        ElMessage.error(`第 ${i + 1} 个章节视频链接不能为空`)
        return
      }
      if (!chapter.duration || chapter.duration.trim() === '') {
        ElMessage.error(`第 ${i + 1} 个章节视频时长不能为空`)
        return
      }
    }
    
    // 获取用户角色
    const userRole = userStore.userInfo?.role || 'USER'
    const isAdmin = userRole === 'ADMIN'
    
    // 构建课程数据（包含章节）
    const courseData = {
      title: courseForm.title,
      description: courseForm.description,
      coverImage: courseForm.coverImage || `https://picsum.photos/400/225?random=${Math.random().toString(36).substr(2, 9)}`,
      price: courseForm.isFree ? 0 : courseForm.price,
      discountPrice: courseForm.isFree ? 0 : courseForm.price,
      categoryId: getCategoryId(courseForm.category),
      teacherId: userId,
      lessonCount: chapters.value.length,
      introduction: courseForm.description,
      // 管理员创建的课程直接上架并审核通过
      status: isAdmin ? 1 : 0,
      auditStatus: isAdmin ? 1 : 0,
      // 添加章节数据
      lessons: chapters.value.map((chapter, index) => ({
        title: chapter.title,
        videoUrl: chapter.videoUrl,
        duration: convertDurationToSeconds(chapter.duration),
        lessonNumber: index + 1
      }))
    }
    
    // 调用API保存课程
    const response = await addCourseApi(courseData)
    if (response.code === 200) {
      if (isAdmin) {
        ElMessage.success('课程创建成功')
        router.push('/admin/courses')
      } else {
        ElMessage.success('课程创建成功，等待管理员审核')
        router.push('/teacher/courses')
      }
    } else {
      ElMessage.error(response.msg || '课程创建失败')
    }
  } catch (error: any) {
    ElMessage.error('课程创建失败: ' + (error.message || '未知错误'))
  }
}

// 将时长格式转换为秒数
// 支持两种格式：
// 1. 纯数字（直接秒数）：如 "300" -> 300秒
// 2. 时间格式：如 "05:30" -> 330秒，"01:05:30" -> 3930秒
const convertDurationToSeconds = (duration: string): number => {
  // 去除首尾空格
  const trimmedDuration = duration.trim()
  
  // 检查是否为纯数字（直接秒数）
  if (/^\d+$/.test(trimmedDuration)) {
    return parseInt(trimmedDuration, 10)
  }
  
  // 检查是否为时间格式
  const parts = trimmedDuration.split(':')
  if (parts.length === 2) {
    const minutes = parseInt(parts[0], 10)
    const seconds = parseInt(parts[1], 10)
    if (!isNaN(minutes) && !isNaN(seconds)) {
      return minutes * 60 + seconds
    }
  } else if (parts.length === 3) {
    const hours = parseInt(parts[0], 10)
    const minutes = parseInt(parts[1], 10)
    const seconds = parseInt(parts[2], 10)
    if (!isNaN(hours) && !isNaN(minutes) && !isNaN(seconds)) {
      return hours * 3600 + minutes * 60 + seconds
    }
  }
  
  return 0
}

// 将分类名称转换为分类ID（与数据库中的category表一致）
const getCategoryId = (categoryName: string): number => {
  const categoryMap: Record<string, number> = {
    'frontend': 2,      // 前端开发
    'backend': 3,       // 后端开发
    'mobile': 4,        // 移动开发
    'language': 5,      // 编程语言
    'database': 6,      // 数据库
    'ai': 7,            // 人工智能
    'devops': 8         // 运维技术
  }
  return categoryMap[categoryName] || 2
}

// 取消
const cancel = () => {
  const userRole = userStore.userInfo?.role || 'USER'
  if (userRole === 'ADMIN') {
    router.push('/admin/courses')
  } else {
    router.push('/teacher/courses')
  }
}
</script>

<template>
  <div class="create-course">
    <div class="page-header">
      <div class="header-left">
        <el-button type="primary" link @click="cancel">
          <el-icon><ArrowLeft /></el-icon> 返回课程管理
        </el-button>
        <h1>发布新课程</h1>
      </div>
    </div>
    
    <el-card class="form-card">
      <el-form
        :model="courseForm"
        :rules="courseRules"
        label-width="100px"
        class="course-form"
      >
        <el-form-item label="课程标题" prop="title">
          <el-input v-model="courseForm.title" placeholder="请输入课程标题" />
        </el-form-item>
        
        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入课程描述"
          />
        </el-form-item>
        
        <el-form-item label="课程分类" prop="category">
          <el-select v-model="courseForm.category" placeholder="请选择课程分类">
            <el-option label="前端开发" value="frontend" />
            <el-option label="后端开发" value="backend" />
            <el-option label="移动开发" value="mobile" />
            <el-option label="编程语言" value="language" />
            <el-option label="数据库" value="database" />
            <el-option label="人工智能" value="ai" />
            <el-option label="运维技术" value="devops" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="课程封面" prop="coverImage">
          <el-input v-model="courseForm.coverImage" placeholder="请输入封面图片URL" />
          <el-button type="primary" link>上传图片</el-button>
        </el-form-item>
        
        <el-form-item label="课程价格">
          <el-row :gutter="10">
            <el-col :span="12">
              <el-input-number v-model="courseForm.price" :min="0" :step="0.01" placeholder="请输入价格" />
            </el-col>
            <el-col :span="12">
              <el-checkbox v-model="courseForm.isFree">免费课程</el-checkbox>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="chapters-card">
      <template #header>
        <div class="card-header">
          <h3>课程章节</h3>
          <el-button type="success" :icon="Plus" @click="addChapter">
            添加章节
          </el-button>
        </div>
      </template>
      
      <div class="chapters-list">
        <div
          v-for="(chapter, index) in chapters"
          :key="chapter.id"
          class="chapter-item"
        >
          <el-card class="chapter-card">
            <div class="chapter-header">
              <h4>章节 {{ index + 1 }}</h4>
              <el-button
                type="danger"
                size="small"
                :icon="Delete"
                @click="deleteChapter(index)"
              >
                删除
              </el-button>
            </div>
            
            <el-form class="chapter-form">
              <el-form-item label="章节标题">
                <el-input v-model="chapter.title" placeholder="请输入章节标题" />
              </el-form-item>
              
              <el-form-item label="视频链接">
                <el-input v-model="chapter.videoUrl" placeholder="请输入视频外链URL" />
              </el-form-item>
              
              <el-form-item label="视频时长">
                <el-input v-model="chapter.duration" placeholder="支持格式：300（秒）或 05:30（分:秒）或 01:05:30（时:分:秒）" />
              </el-form-item>
            </el-form>
          </el-card>
        </div>
      </div>
    </el-card>
    
    <div class="form-actions">
      <el-button @click="cancel">取消</el-button>
      <el-button type="primary" :icon="Document" @click="saveCourse">
        保存课程
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.create-course {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 30px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-left h1 {
  font-size: 24px;
  color: #333;
  margin: 0;
}

.form-card {
  margin-bottom: 30px;
}

.course-form {
  max-width: 600px;
}

.chapters-card {
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

.chapters-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chapter-item {
  width: 100%;
}

.chapter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chapter-header h4 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.chapter-form {
  max-width: 500px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
}
</style>