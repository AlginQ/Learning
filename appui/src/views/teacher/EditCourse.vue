<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Save, ArrowLeft, Delete, Plus } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const courseId = Number(route.params.id)

// 课程表单
const courseForm = reactive({
  title: '',
  description: '',
  category: '',
  coverImage: '',
  price: 0,
  isFree: false
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

// 加载课程数据
const loadCourseData = async () => {
  try {
    // 模拟API请求
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟课程数据
    courseForm.title = 'Vue3 从入门到精通'
    courseForm.description = '全面讲解Vue3核心概念和实战技巧，包括Composition API、响应式系统、组件通信等内容。'
    courseForm.category = 'frontend'
    courseForm.coverImage = 'https://picsum.photos/800/450'
    courseForm.price = 99
    courseForm.isFree = false
    
    // 模拟章节数据
    chapters.value = [
      {
        id: 1,
        title: 'Vue3 简介',
        videoUrl: 'https://example.com/video1.mp4',
        duration: '15:30'
      },
      {
        id: 2,
        title: 'Composition API 基础',
        videoUrl: 'https://example.com/video2.mp4',
        duration: '20:15'
      },
      {
        id: 3,
        title: '响应式系统',
        videoUrl: 'https://example.com/video3.mp4',
        duration: '25:45'
      }
    ]
    
    console.log('课程数据已加载')
  } catch (error: any) {
    ElMessage.error('获取课程数据失败: ' + error.message)
  }
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
    // 模拟API请求
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('课程更新成功')
    router.push('/teacher/courses')
  } catch (error: any) {
    ElMessage.error('课程更新失败: ' + error.message)
  }
}

// 取消
const cancel = () => {
  router.push('/teacher/courses')
}

onMounted(() => {
  loadCourseData()
})
</script>

<template>
  <div class="edit-course">
    <div class="page-header">
      <div class="header-left">
        <el-button type="primary" link @click="cancel">
          <el-icon><ArrowLeft /></el-icon> 返回课程管理
        </el-button>
        <h1>编辑课程</h1>
      </div>
    </div>
    
    <el-card class="form-card">
      <el-form
        :model="courseForm"
        label-width="100px"
        class="course-form"
      >
        <el-form-item label="课程标题">
          <el-input v-model="courseForm.title" placeholder="请输入课程标题" />
        </el-form-item>
        
        <el-form-item label="课程描述">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入课程描述"
          />
        </el-form-item>
        
        <el-form-item label="课程分类">
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
        
        <el-form-item label="课程封面">
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
                <el-input v-model="chapter.duration" placeholder="请输入视频时长（如：05:30）" />
              </el-form-item>
            </el-form>
          </el-card>
        </div>
      </div>
    </el-card>
    
    <div class="form-actions">
      <el-button @click="cancel">取消</el-button>
      <el-button type="primary" :icon="Save" @click="saveCourse">
        保存修改
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.edit-course {
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