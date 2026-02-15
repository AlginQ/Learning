<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useUserStore } from '@/store/user'
import { User, Edit, Camera, Lock } from '@element-plus/icons-vue'

const userStore = useUserStore()
const activeTab = ref('profile')
const loading = ref(false)

const form = reactive({
  nickname: userStore.currentUser?.nickname || '',
  email: userStore.currentUser?.email || '',
  phone: '',
  gender: 0,
  birthday: ''
})

const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { max: 50, message: '昵称长度不能超过50位', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const handleUpdateProfile = () => {
  loading.value = true
  // 模拟更新个人信息
  setTimeout(() => {
    loading.value = false
    ElMessage.success('个人信息更新成功')
  }, 1000)
}

const handleChangePassword = () => {
  // 密码修改逻辑
  ElMessage.info('密码修改功能待实现')
}
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
              <div class="avatar-wrapper">
                <el-avatar :size="100" :src="userStore.currentUser?.avatar" :icon="User" />
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
          </div>
        </el-col>
        
        <!-- 右侧表单区域 -->
        <el-col :span="16">
          <div class="form-section">
            <el-tabs v-model="activeTab">
              <el-tab-pane label="个人信息" name="profile">
                <el-form
                  :model="form"
                  :rules="rules"
                  label-width="80px"
                  class="profile-form"
                >
                  <el-form-item label="用户名">
                    <el-input v-model="userStore.currentUser?.username" disabled />
                  </el-form-item>
                  
                  <el-form-item label="昵称" prop="nickname">
                    <el-input v-model="form.nickname" placeholder="请输入昵称" />
                  </el-form-item>
                  
                  <el-form-item label="邮箱" prop="email">
                    <el-input v-model="form.email" placeholder="请输入邮箱" />
                  </el-form-item>
                  
                  <el-form-item label="手机号">
                    <el-input v-model="form.phone" placeholder="请输入手机号" />
                  </el-form-item>
                  
                  <el-form-item label="性别">
                    <el-radio-group v-model="form.gender">
                      <el-radio :label="0">保密</el-radio>
                      <el-radio :label="1">男</el-radio>
                      <el-radio :label="2">女</el-radio>
                    </el-radio-group>
                  </el-form-item>
                  
                  <el-form-item label="生日">
                    <el-date-picker
                      v-model="form.birthday"
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
                  <el-form label-width="100px">
                    <el-form-item label="登录密码">
                      <div class="password-item">
                        <span>已设置</span>
                        <el-button type="primary" link @click="handleChangePassword">
                          修改密码
                        </el-button>
                      </div>
                    </el-form-item>
                    
                    <el-form-item label="绑定手机">
                      <div class="bind-item">
                        <span v-if="form.phone">已绑定：{{ form.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') }}</span>
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
</style>