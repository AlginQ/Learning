<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete, Search } from '@element-plus/icons-vue'
import type { Note } from '@/types/note'
import { getNotesApi, createNoteApi, updateNoteApi, deleteNoteApi } from '@/api/note'

const userStore = useUserStore()
const activeTab = ref('notes')
const loading = ref(false)
const dialogVisible = ref(false)
const searchKeyword = ref('')

// 笔记数据
const notes = ref<Note[]>([])

const noteForm = reactive({
  id: undefined,
  title: '',
  content: '',
  courseId: 25, // 使用实际存在的课程ID
  lessonId: 9, // 使用实际存在的课时ID
  type: 0
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

// 加载笔记列表
const loadNotes = async () => {
  loading.value = true
  try {
    const params: { courseId?: number; keyword?: string } = {}
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    
    const response = await getNotesApi(params)
    notes.value = response.data
  } catch (error: any) {
    ElMessage.error('获取笔记列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 新增笔记
const handleAddNote = () => {
  dialogVisible.value = true
  // 重置表单
  Object.assign(noteForm, {
    id: undefined,
    title: '',
    content: '',
    courseId: null,
    lessonId: null,
    type: 0
  })
}

// 编辑笔记
const handleEditNote = (note: Note) => {
  dialogVisible.value = true
  Object.assign(noteForm, {
    id: note.id,
    title: note.title,
    content: note.content,
    courseId: note.courseId,
    lessonId: note.lessonId,
    type: note.type
  })
}

// 删除笔记
const handleDeleteNote = (id: number) => {
  ElMessageBox.confirm('确定要删除这条笔记吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    loading.value = true
    try {
      await deleteNoteApi(id)
      notes.value = notes.value.filter(note => note.id !== id)
      ElMessage.success('删除成功')
    } catch (error: any) {
      ElMessage.error('删除失败: ' + (error.response?.data?.message || error.message))
    } finally {
      loading.value = false
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        if (noteForm.id) {
          // 编辑笔记
          await updateNoteApi(noteForm.id, noteForm)
          const index = notes.value.findIndex(n => n.id === noteForm.id)
          if (index !== -1) {
            notes.value[index] = { ...noteForm } as Note
          }
          ElMessage.success('笔记更新成功')
        } else {
          // 新增笔记
          console.log('Creating note with:', noteForm)
          const response = await createNoteApi(noteForm)
          notes.value.unshift(response.data)
          ElMessage.success('笔记添加成功')
        }
        dialogVisible.value = false
      } catch (error: any) {
        ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
      } finally {
        loading.value = false
      }
    }
  })
}

// 取消操作
const handleCancel = () => {
  dialogVisible.value = false
  formRef.value?.resetFields()
}

// 搜索笔记
const handleSearch = () => {
  loadNotes()
}



// 页面加载时获取笔记列表
onMounted(() => {
  loadNotes()
})
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
          v-model="searchKeyword"
          placeholder="搜索笔记..."
          style="width: 300px;"
          :prefix-icon="Search"
          @keyup.enter="handleSearch"
        />
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
            <div class="time-info">
              创建时间：{{ note.createTime }}
              <span v-if="note.createTime !== note.updateTime">
                | 更新时间：{{ note.updateTime }}
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