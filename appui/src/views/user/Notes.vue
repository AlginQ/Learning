<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useUserStore } from '@/store/user'
import { Edit, Delete, Search } from '@element-plus/icons-vue'

const userStore = useUserStore()
const activeTab = ref('notes')
const loading = ref(false)
const dialogVisible = ref(false)

// 模拟笔记数据
const notes = ref([
  {
    id: 1,
    courseId: 1,
    courseTitle: 'Java基础教程',
    lessonId: 1,
    lessonTitle: 'Java环境搭建',
    title: 'Java环境配置要点',
    content: 'JDK安装需要注意版本兼容性，建议使用LTS版本。配置环境变量时要注意PATH的顺序。',
    createdAt: '2024-01-15 14:30:00',
    updatedAt: '2024-01-15 14:30:00'
  },
  {
    id: 2,
    courseId: 1,
    courseTitle: 'Java基础教程',
    lessonId: 2,
    lessonTitle: '变量和数据类型',
    title: '数据类型转换注意事项',
    content: '自动类型转换遵循从小到大的原则，强制类型转换可能会丢失精度，需要特别注意。',
    createdAt: '2024-01-15 10:15:00',
    updatedAt: '2024-01-15 10:15:00'
  },
  {
    id: 3,
    courseId: 2,
    courseTitle: 'Vue 3从入门到实战',
    lessonId: 1,
    lessonTitle: 'Vue 3简介',
    title: 'Vue 3新特性总结',
    content: 'Composition API提供了更好的逻辑复用能力，性能优化显著，Teleport组件很实用。',
    createdAt: '2024-01-14 16:20:00',
    updatedAt: '2024-01-14 16:20:00'
  }
])

const noteForm = reactive({
  title: '',
  content: '',
  courseId: null,
  lessonId: null
})

const formRules = {
  title: [
    { required: true, message: '请输入笔记标题', trigger: 'blur' },
    { max: 100, message: '标题长度不能超过100字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入笔记内容', trigger: 'blur' }
  ]
}

const formRef = ref()

const handleAddNote = () => {
  dialogVisible.value = true
  // 重置表单
  Object.assign(noteForm, {
    title: '',
    content: '',
    courseId: null,
    lessonId: null
  })
}

const handleEditNote = (note: any) => {
  dialogVisible.value = true
  Object.assign(noteForm, {
    ...note
  })
}

const handleDeleteNote = (id: number) => {
  ElMessageBox.confirm('确定要删除这条笔记吗？', '提示', {
    type: 'warning'
  }).then(() => {
    notes.value = notes.value.filter(note => note.id !== id)
    ElMessage.success('删除成功')
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        if (noteForm.id) {
          // 编辑笔记
          const index = notes.value.findIndex(n => n.id === noteForm.id)
          if (index !== -1) {
            notes.value[index] = {
              ...noteForm,
              updatedAt: new Date().toLocaleString()
            }
          }
          ElMessage.success('笔记更新成功')
        } else {
          // 新增笔记
          notes.value.push({
            ...noteForm,
            id: Date.now(),
            createdAt: new Date().toLocaleString(),
            updatedAt: new Date().toLocaleString()
          })
          ElMessage.success('笔记添加成功')
        }
        dialogVisible.value = false
      } catch (error: any) {
        ElMessage.error(error.message || '操作失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleCancel = () => {
  dialogVisible.value = false
  formRef.value?.resetFields()
}
</script>

<template>
  <div class="notes-container">
    <div class="page-header">
      <h1>我的笔记</h1>
      <el-button type="primary" @click="handleAddNote">
        <el-icon><Edit /></el-icon>
        新建笔记
      </el-button>
    </div>
    
    <div class="content-wrapper">
      <div class="notes-filter">
        <el-input
          placeholder="搜索笔记..."
          style="width: 300px; margin-right: 20px;"
          :prefix-icon="Search"
        />
        <el-select placeholder="选择课程" style="width: 200px;">
          <el-option label="全部课程" value=""></el-option>
          <el-option label="Java基础教程" value="1"></el-option>
          <el-option label="Vue 3从入门到实战" value="2"></el-option>
        </el-select>
      </div>
      
      <div class="notes-list">
        <el-card 
          v-for="note in notes" 
          :key="note.id" 
          class="note-card"
          shadow="hover"
        >
          <div class="note-header">
            <h3 class="note-title">{{ note.title }}</h3>
            <div class="note-actions">
              <el-button type="primary" link @click="handleEditNote(note)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="danger" link @click="handleDeleteNote(note.id)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
          
          <div class="note-content">
            {{ note.content }}
          </div>
          
          <div class="note-meta">
            <div class="course-info">
              课程：<el-tag type="primary">{{ note.courseTitle }}</el-tag>
              课时：<el-tag>{{ note.lessonTitle }}</el-tag>
            </div>
            <div class="time-info">
              创建时间：{{ note.createdAt }}
              <span v-if="note.createdAt !== note.updatedAt">
                | 更新时间：{{ note.updatedAt }}
              </span>
            </div>
          </div>
        </el-card>
        
        <el-empty v-if="notes.length === 0" description="暂无笔记，点击右上角新建按钮开始记录吧！" />
      </div>
    </div>
    
    <!-- 笔记编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="noteForm.id ? '编辑笔记' : '新建笔记'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="noteForm"
        :rules="formRules"
        label-width="80px"
      >
        <el-form-item label="笔记标题" prop="title">
          <el-input v-model="noteForm.title" placeholder="请输入笔记标题" />
        </el-form-item>
        
        <el-form-item label="笔记内容" prop="content">
          <el-input
            v-model="noteForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入笔记内容"
          />
        </el-form-item>
        
        <el-form-item label="所属课程">
          <el-select v-model="noteForm.courseId" placeholder="选择课程">
            <el-option label="Java基础教程" value="1"></el-option>
            <el-option label="Vue 3从入门到实战" value="2"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="所属课时">
          <el-select v-model="noteForm.lessonId" placeholder="选择课时">
            <el-option label="Java环境搭建" value="1"></el-option>
            <el-option label="变量和数据类型" value="2"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          :loading="loading"
          @click="handleSubmit"
        >
          {{ loading ? '提交中...' : '确定' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.notes-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  color: #333;
  margin: 0;
}

.content-wrapper {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.notes-filter {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.notes-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.note-card {
  border-radius: 8px;
  transition: all 0.3s;
}

.note-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.note-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.note-actions {
  display: flex;
  gap: 10px;
}

.note-content {
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
  white-space: pre-wrap;
  word-break: break-all;
}

.note-meta {
  border-top: 1px solid #eee;
  padding-top: 15px;
}

.course-info {
  margin-bottom: 10px;
  display: flex;
  gap: 10px;
  align-items: center;
}

.time-info {
  font-size: 12px;
  color: #999;
}
</style>