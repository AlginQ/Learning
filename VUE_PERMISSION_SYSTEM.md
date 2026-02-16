# Vue3 权限控制系统文档

## 概述

本项目实现了基于Vue3 + Element Plus的完整权限控制系统，包含菜单权限、路由权限和组件权限控制。

## 核心功能

### 1. 菜单权限控制
- **普通用户**可见菜单：首页、我的课程、学习进度
- **管理员**额外可见：用户管理、课程管理
- 通过 `v-if` 和 localStorage 中的 role 字段动态控制

### 2. 路由权限控制
- 路由守卫验证用户登录状态
- 管理员路由需要 `requiresAdmin: true` 元信息
- 未授权访问会重定向到首页并提示错误

### 3. 组件权限控制
- 提供 `v-permission` 自定义指令
- 支持字符串和数组两种权限格式
- 实时响应权限变化

### 4. 二次权限校验
- 防止手动修改localStorage绕过权限控制
- 通过后端API验证用户真实权限
- 异步权限验证机制

## 文件结构

```
src/
├── components/
│   └── MainLayout.vue          # 主布局组件，包含侧边栏菜单
├── views/
│   ├── admin/
│   │   ├── UserManagement.vue   # 用户管理页面
│   │   └── CourseManagement.vue # 课程管理页面
│   └── PermissionDemo.vue       # 权限控制演示页面
├── utils/
│   └── permission.ts           # 权限验证工具函数
├── store/
│   └── user.ts                 # 用户状态管理
└── router/
    └── index.ts                # 路由配置和守卫
```

## 核心实现

### 1. 权限工具函数 (permission.ts)

```typescript
// 角色检查
export function hasRole(requiredRole: string): boolean

// 管理员检查
export function isAdmin(): boolean

// 二次权限校验
export async function verifyPermission(requiredRole: string): Promise<boolean>

// 自定义指令
export const permissionDirective = {
  mounted(el: HTMLElement, binding: any) { ... }
}
```

### 2. 路由守卫配置

```typescript
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('role') || 'USER'
  
  // 检查认证状态
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }
  
  // 检查管理员权限
  if (to.meta.requiresAdmin && userRole !== 'ADMIN') {
    ElMessage.error('权限不足，无法访问该页面')
    next('/')
    return
  }
  
  next()
})
```

### 3. 主布局组件 (MainLayout.vue)

```vue
<!-- 动态菜单项 -->
<el-menu-item 
  v-for="item in menuItems" 
  :key="item.index"
  :index="item.index"
>
  <el-icon><component :is="item.icon" /></el-icon>
  <template #title>{{ item.title }}</template>
</el-menu-item>

<!-- 计算属性过滤菜单 -->
const menuItems = computed(() => {
  const userRole = userStore.currentUser?.role || localStorage.getItem('role') || 'USER'
  return baseMenus.filter(menu => menu.roles.includes(userRole))
})
```

### 4. 权限指令使用

```vue
<!-- 单个角色 -->
<div v-permission="'ADMIN'">仅管理员可见</div>

<!-- 多个角色 -->
<div v-permission="['ADMIN', 'USER']">管理员和用户都可见</div>
```

## 安全机制

### 1. 前端权限控制
- localStorage存储用户角色信息
- 菜单和路由级别的权限过滤
- 自定义指令实现组件级别控制

### 2. 后端权限保护
- Spring Security 方法级权限控制
- `@PreAuthorize("hasRole('ADMIN')")` 注解保护
- JWT Token 认证机制

### 3. 二次校验机制
```typescript
// 防止localStorage篡改
export async function verifyPermission(requiredRole: string): Promise<boolean> {
  try {
    // 调用后端API验证真实权限
    const response = await api.verifyUserRole()
    return response.data.role === requiredRole
  } catch (error) {
    return false
  }
}
```

## 使用示例

### 1. 添加新的管理员页面

```typescript
// router/index.ts
{
  path: '/admin/new-feature',
  name: 'NewFeature',
  component: () => import('@/views/admin/NewFeature.vue'),
  meta: { requiresAuth: true, requiresAdmin: true }
}
```

### 2. 在组件中使用权限控制

```vue
<script setup>
import { isAdmin } from '@/utils/permission'
</script>

<template>
  <div>
    <!-- 条件渲染 -->
    <button v-if="isAdmin()">管理员功能</button>
    
    <!-- 权限指令 -->
    <div v-permission="'ADMIN'">管理员专属内容</div>
  </div>
</template>
```

### 3. 手动权限验证

```typescript
import { verifyAdminPermission } from '@/utils/permission'

async function handleAdminAction() {
  const hasPermission = await verifyAdminPermission()
  if (!hasPermission) {
    ElMessage.error('权限不足')
    return
  }
  // 执行管理员操作
}
```

## 测试验证

### 1. 正常权限测试
- 普通用户登录后只能看到基础功能
- 管理员登录后可以看到所有功能

### 2. 权限绕过测试
- 手动修改localStorage中的role字段
- 点击"验证权限"按钮触发二次校验
- 系统应能识别并拒绝非法访问

### 3. 路由权限测试
- 未登录用户访问受保护路由应重定向到登录页
- 普通用户访问管理员路由应被拒绝

## 注意事项

1. **永远不要只依赖前端权限控制** - 后端必须有相应的权限验证
2. **定期清理localStorage** - 避免权限信息过期
3. **敏感操作必须二次验证** - 重要功能需要重新验证身份
4. **权限变更后及时刷新** - 用户角色变更后应重新加载权限信息

## 扩展建议

1. 添加更细粒度的权限控制（按钮级别）
2. 实现权限缓存机制减少API调用
3. 添加权限变更通知机制
4. 实现权限继承和组合功能