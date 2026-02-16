<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { verifyAdminPermission } from '@/utils/permission'

const showAdminContent = ref(false)

// 测试二次权限校验
const testAdminPermission = async () => {
  try {
    const hasPermission = await verifyAdminPermission()
    if (hasPermission) {
      ElMessage.success('权限验证通过，您可以访问管理员功能')
      showAdminContent.value = true
    } else {
      ElMessage.error('权限验证失败，您没有管理员权限')
      showAdminContent.value = false
    }
  } catch (error) {
    ElMessage.error('权限验证出现错误')
    showAdminContent.value = false
  }
}

// 模拟篡改localStorage
const tamperLocalStorage = () => {
  localStorage.setItem('role', 'ADMIN')
  ElMessage.warning('已手动修改localStorage中的角色为ADMIN，请点击"验证权限"按钮进行二次校验')
}
</script>

<template>
  <div class="permission-demo">
    <div class="page-header">
      <h1>权限控制演示</h1>
      <p>展示Vue3 + Element Plus的权限控制实现</p>
    </div>

    <el-card class="demo-card">
      <h3>权限状态</h3>
      <div class="status-info">
        <p><strong>当前用户角色:</strong> {{ localStorage.getItem('role') || 'USER' }}</p>
        <p><strong>是否已登录:</strong> {{ !!localStorage.getItem('token') }}</p>
        <p><strong>是否为管理员:</strong> {{ localStorage.getItem('role') === 'ADMIN' }}</p>
      </div>
    </el-card>

    <el-card class="demo-card">
      <h3>v-permission 指令演示</h3>
      <div class="permission-examples">
        <!-- 普通用户可见 -->
        <el-alert 
          v-permission="['USER', 'ADMIN']"
          title="所有用户都可以看到这条消息" 
          type="success" 
          :closable="false"
          style="margin-bottom: 20px;"
        />

        <!-- 仅管理员可见 -->
        <el-alert 
          v-permission="'ADMIN'"
          title="只有管理员才能看到这条消息" 
          type="warning" 
          :closable="false"
          style="margin-bottom: 20px;"
        />

        <!-- 仅普通用户可见 -->
        <el-alert 
          v-permission="'USER'"
          title="只有普通用户才能看到这条消息" 
          type="info" 
          :closable="false"
        />
      </div>
    </el-card>

    <el-card class="demo-card">
      <h3>二次权限校验测试</h3>
      <div class="verification-test">
        <el-button type="primary" @click="testAdminPermission">
          验证管理员权限
        </el-button>
        <el-button type="warning" @click="tamperLocalStorage">
          模拟篡改localStorage
        </el-button>
        
        <div v-if="showAdminContent" class="admin-content">
          <el-alert 
            title="✅ 权限验证成功！这里是管理员专属内容" 
            type="success" 
            :closable="false"
            style="margin-top: 20px;"
          />
          <div style="margin-top: 20px;">
            <el-button type="danger">删除用户</el-button>
            <el-button type="warning">禁用账户</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="demo-card">
      <h3>菜单权限说明</h3>
      <div class="menu-info">
        <ul>
          <li><strong>普通用户</strong>可访问：首页、课程列表、学习中心、我的课程</li>
          <li><strong>管理员</strong>额外可访问：用户管理、课程管理</li>
          <li>菜单显示通过 <code>v-if</code> 和 localStorage 中的 role 字段控制</li>
          <li>路由访问通过路由守卫进行权限验证</li>
        </ul>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.permission-demo {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  color: #333;
  margin: 0 0 10px;
}

.page-header p {
  color: #666;
  margin: 0;
}

.demo-card {
  margin-bottom: 20px;
}

.demo-card h3 {
  margin-top: 0;
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.status-info p {
  margin: 10px 0;
  font-size: 16px;
}

.verification-test {
  text-align: center;
}

.admin-content {
  margin-top: 20px;
  padding: 20px;
  background-color: #f0f9ff;
  border-radius: 4px;
  border: 1px solid #b3e0ff;
}

.menu-info ul {
  padding-left: 20px;
}

.menu-info li {
  margin: 10px 0;
  line-height: 1.6;
}

.menu-info code {
  background-color: #f5f5f5;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: monospace;
}
</style>