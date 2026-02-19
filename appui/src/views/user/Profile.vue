<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Edit, Camera, Lock, VideoPlay, DataAnalysis, Collection, Document, Upload, Refresh } from '@element-plus/icons-vue'
import { updateUserProfile, updateUserAvatar, changePassword } from '@/api/auth'

const userStore = useUserStore()
const router = useRouter()
const activeTab = ref('profile')
const loading = ref(false)
const avatarLoading = ref(false)

// 个人信息表单
const profileForm = reactive({
  nickname: userStore.currentUser?.nickname || '',
  email: userStore.currentUser?.email || '',
  phone: userStore.currentUser?.phone || '',
  gender: userStore.currentUser?.gender || 0,
  birthday: userStore.currentUser?.birthday || ''
})

// 头像相关
const avatarDialogVisible = ref(false)
const avatarPreview = ref('')
const currentAvatar = ref(userStore.currentUser?.avatar || '')

// 密码修改表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const profileRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { max: 50, message: '昵称长度不能超过50位', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { 
      validator: (rule: any, value: string, callback: Function) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
}

// 初始化表单数据
const initFormData = () => {
  profileForm.nickname = userStore.currentUser?.nickname || ''
  profileForm.email = userStore.currentUser?.email || ''
  profileForm.phone = userStore.currentUser?.phone || ''
  profileForm.gender = userStore.currentUser?.gender || 0
  profileForm.birthday = userStore.currentUser?.birthday || ''
  currentAvatar.value = userStore.currentUser?.avatar || ''
}

// 更新个人信息
const handleUpdateProfile = async () => {
  loading.value = true
  try {
    const response = await updateUserProfile(profileForm)
    if (response.code === 200) {
      // 更新本地存储
      const updatedUser = {
        ...userStore.currentUser,
        ...profileForm
      }
      userStore.setCurrentUser(updatedUser)
      ElMessage.success('个人信息更新成功')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '更新失败')
  } finally {
    loading.value = false
  }
}

// 处理头像上传
const handleAvatarUpload = () => {
  // 这里应该集成实际的文件上传逻辑
  // 暂时使用模拟数据
  avatarPreview.value = 'https://via.placeholder.com/200x200/409EFF/FFFFFF?text=Avatar'
  avatarDialogVisible.value = true
}

// 确认更换头像
const confirmAvatarChange = async () => {
  avatarLoading.value = true
  try {
    const response = await updateUserAvatar(avatarPreview.value)
    if (response.code === 200) {
      currentAvatar.value = avatarPreview.value
      userStore.currentUser!.avatar = avatarPreview.value
      ElMessage.success('头像更新成功')
      avatarDialogVisible.value = false
    }
  } catch (error: any) {
    ElMessage.error(error.message || '头像更新失败')
  } finally {
    avatarLoading.value = false
  }
}

// 修改密码
const handleChangePassword = async (formEl: any) => {
  if (!formEl) return
  
  try {
    const valid = await formEl.validate()
    if (valid) {
      const response = await changePassword(passwordForm.oldPassword, passwordForm.newPassword)
      if (response.code === 200) {
        ElMessage.success('密码修改成功')
        // 重置表单
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
        formEl.resetFields()
      }
    }
  } catch (error: any) {
    ElMessage.error(error.message || '密码修改失败')
  }
}

// 快捷导航
const goToMyCourses = () => {
  router.push('/my-courses')
}

const goToStudyRecords = () => {
  router.push('/study-records')
}

const goToNotes = () => {
  router.push('/notes')
}

// 组件挂载时初始化
onMounted(() => {
  initFormData()
})
</script>

<template>
  <div class="profile-container">
    <div class="profile-header">
      <h1>个人中心</h1>
    </div>
    
    <div class="profile-content">
      <el-row :gutter="30">
        <!-- 左侧个人信息卡片 -->
        <el-col :span="8">
          <div class="profile-card">
            <div class="avatar-section">
              <div class="avatar-wrapper" @click="handleAvatarUpload">
                <el-avatar :size="100" :src="currentAvatar" :icon="User" />
                <div class="avatar-overlay">
                  <el-icon><Camera /></el-icon>
                </div>
              </div>
              <h3>{{ userStore.currentUser?.nickname || userStore.currentUser?.username }}</h3>
              <p class="user-role">{{ userStore.currentUser?.role === 'ADMIN' ? '管理员' : '普通用户' }}</p>
            </div>
            
            <div class="user-stats">
              <div class="stat-item">
                <div class="stat-value">12</div>
                <div class="stat-label">学习课程</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">48</div>
                <div class="stat-label">完成课时</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">24.5</div>
                <div class="stat-label">学习时长(小时)</div>
              </div>
            </div>
            
            <div class="quick-links">
              <div class="link-item" @click="goToMyCourses">
                <el-icon><VideoPlay /></el-icon>
                <span>我的课程</span>
              </div>
              <div class="link-item" @click="goToStudyRecords">
                <el-icon><DataAnalysis /></el-icon>
                <span>学习记录</span>
              </div>
              <div class="link-item" @click="goToNotes">
                <el-icon><Document /></el-icon>
                <span>我的笔记</span>
              </div>
              <div class="link-item">
                <el-icon><Collection /></el-icon>
                <span>我的收藏</span>
              </div>
            </div>
          </div>
        </el-col>
        
        <!-- 右侧表单区域 -->
        <el-col :span="16">
          <div class="form-section">
            <el-tabs v-model="activeTab">
              <el-tab-pane label="个人信息" name="profile">
                <el-form
                  :model="profileForm"
                  :rules="profileRules"
                  label-width="80px"
                  class="profile-form"
                >
                  <el-form-item label="用户名">
                    <el-input :model-value="userStore.currentUser?.username" disabled />
                  </el-form-item>
                  
                  <el-form-item label="昵称" prop="nickname">
                    <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
                  </el-form-item>
                  
                  <el-form-item label="邮箱" prop="email">
                    <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
                  </el-form-item>
                  
                  <el-form-item label="手机号">
                    <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
                  </el-form-item>
                  
                  <el-form-item label="性别">
                    <el-radio-group v-model="profileForm.gender">
                      <el-radio :label="0">保密</el-radio>
                      <el-radio :label="1">男</el-radio>
                      <el-radio :label="2">女</el-radio>
                    </el-radio-group>
                  </el-form-item>
                  
                  <el-form-item label="生日">
                    <el-date-picker
                      v-model="profileForm.birthday"
                      type="date"
                      placeholder="选择生日"
                      format="YYYY-MM-DD"
                      value-format="YYYY-MM-DD"
                    />
                  </el-form-item>
                  
                  <el-form-item>
                    <el-button
                      type="primary"
                      :loading="loading"
                      @click="handleUpdateProfile"
                    >
                      <el-icon><Edit /></el-icon>
                      更新信息
                    </el-button>
                  </el-form-item>
                </el-form>
              </el-tab-pane>
              
              <el-tab-pane label="安全设置" name="security">
                <div class="security-form">
                  <el-form 
                    ref="passwordFormRef"
                    :model="passwordForm" 
                    :rules="passwordRules" 
                    label-width="100px"
                  >
                    <el-form-item label="登录密码">
                      <div class="password-item">
                        <span>已设置</span>
                        <el-button type="primary" link @click="activeTab = 'password'">
                          修改密码
                        </el-button>
                      </div>
                    </el-form-item>
                    
                    <el-form-item label="绑定手机">
                      <div class="bind-item">
                        <span v-if="profileForm.phone">已绑定：{{ profileForm.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') }}</span>
                        <span v-else style="color: #999;">未绑定</span>
                        <el-button type="primary" link>修改手机</el-button>
                      </div>
                    </el-form-item>
                    
                    <el-form-item label="绑定邮箱">
                      <div class="bind-item">
                        <span>{{ userStore.currentUser?.email }}</span>
                        <el-button type="primary" link>修改邮箱</el-button>
                      </div>
                    </el-form-item>
                  </el-form>
                </div>
              </el-tab-pane>
              
              <el-tab-pane label="修改密码" name="password">
                <div class="password-change-form">
                  <el-form 
                    ref="passwordFormRef"
                    :model="passwordForm" 
                    :rules="passwordRules" 
                    label-width="100px"
                    class="password-form"
                  >
                    <el-form-item label="原密码" prop="oldPassword">
                      <el-input 
                        v-model="passwordForm.oldPassword" 
                        type="password" 
                        placeholder="请输入原密码" 
                        show-password
                      />
                    </el-form-item>
                    
                    <el-form-item label="新密码" prop="newPassword">
                      <el-input 
                        v-model="passwordForm.newPassword" 
                        type="password" 
                        placeholder="请输入新密码" 
                        show-password
                      />
                    </el-form-item>
                    
                    <el-form-item label="确认密码" prop="confirmPassword">
                      <el-input 
                        v-model="passwordForm.confirmPassword" 
                        type="password" 
                        placeholder="请再次输入新密码" 
                        show-password
                      />
                    </el-form-item>
                    
                    <el-form-item>
                      <el-button 
                        type="primary" 
                        @click="handleChangePassword(passwordFormRef)"
                      >
                        <el-icon><Lock /></el-icon>
                        修改密码
                      </el-button>
                      <el-button @click="activeTab = 'security'">取消</el-button>
                    </el-form-item>
                  </el-form>
                </div>
              </el-tab-pane>
              
              <el-tab-pane label="学习统计" name="statistics">
                <div class="statistics-content">
                  <el-row :gutter="20">
                    <el-col :span="8">
                      <div class="stat-box">
                        <div class="stat-icon bg-blue">
                          <el-icon><Document /></el-icon>
                        </div>
                        <div class="stat-text">
                          <div class="stat-number">12</div>
                          <div class="stat-desc">学习课程数</div>
                        </div>
                      </div>
                    </el-col>
                    <el-col :span="8">
                      <div class="stat-box">
                        <div class="stat-icon bg-green">
                          <el-icon><VideoPlay /></el-icon>
                        </div>
                        <div class="stat-text">
                          <div class="stat-number">48</div>
                          <div class="stat-desc">完成课时数</div>
                        </div>
                      </div>
                    </el-col>
                    <el-col :span="8">
                      <div class="stat-box">
                        <div class="stat-icon bg-orange">
                          <el-icon><Clock /></el-icon>
                        </div>
                        <div class="stat-text">
                          <div class="stat-number">24.5</div>
                          <div class="stat-desc">总学习时长(小时)</div>
                        </div>
                      </div>
                    </el-col>
                  </el-row>
                  
                  <div class="chart-section">
                    <h3>学习趋势</h3>
                    <div class="chart-placeholder">
                      <el-empty description="学习统计图表待实现" />
                    </div>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
  
  <!-- 头像预览对话框 -->
  <el-dialog
    v-model="avatarDialogVisible"
    title="更换头像"
    width="400px"
    :close-on-click-modal="false"
  >
    <div class="avatar-preview">
      <div class="preview-image">
        <img :src="avatarPreview" alt="预览头像" />
      </div>
      <p class="preview-tip">确认使用这张图片作为头像吗？</p>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="avatarDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          :loading="avatarLoading"
          @click="confirmAvatarChange"
        >
          <el-icon v-if="!avatarLoading"><Check /></el-icon>
          {{ avatarLoading ? '上传中...' : '确定' }}
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.profile-header {
  margin-bottom: 30px;
}

.profile-header h1 {
  font-size: 28px;
  color: #333;
  margin: 0;
}

.profile-content {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.profile-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 30px 20px;
  text-align: center;
}

.avatar-section {
  margin-bottom: 30px;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 15px;
}

.avatar-overlay {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 30px;
  height: 30px;
  background: #409eff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.avatar-overlay .el-icon {
  color: white;
  font-size: 16px;
}

.user-role {
  color: #409eff;
  font-weight: bold;
  margin: 5px 0;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
}

.quick-links {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.link-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 15px;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #eee;
}

.link-item:hover {
  background: #f0f8ff;
  border-color: #409eff;
  transform: translateX(5px);
}

.link-item .el-icon {
  font-size: 18px;
  color: #409eff;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  color: #666;
  font-size: 12px;
}

.form-section {
  min-height: 400px;
}

.profile-form {
  max-width: 500px;
}

.password-item,
.bind-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.statistics-content {
  padding: 20px 0;
}

.stat-box {
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-icon .el-icon {
  font-size: 24px;
  color: white;
}

.bg-blue {
  background-color: #409eff;
}

.bg-green {
  background-color: #67c23a;
}

.bg-orange {
  background-color: #e6a23c;
}

.stat-text {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-desc {
  color: #666;
  font-size: 14px;
}

.chart-section {
  margin-top: 30px;
}

.chart-section h3 {
  margin-bottom: 20px;
  color: #333;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border-radius: 8px;
}

.avatar-preview {
  text-align: center;
  padding: 20px;
}

.preview-image {
  margin-bottom: 20px;
}

.preview-image img {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #409eff;
}

.preview-tip {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.password-change-form {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px 0;
}

.password-form {
  max-width: 400px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.security-form {
  max-width: 500px;
}</style>