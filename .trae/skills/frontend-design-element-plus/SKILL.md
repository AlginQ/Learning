--- 
name: frontend-design-element-plus 
description: 专为 Vue3 + Element Plus 打造的生产级前端界面美化技能。仅做布局、配色、排版、动效、质感升级，不修改 Element Plus 组件结构与功能，完美适配 SpringBoot 后端接口项目。 
license: Complete terms in LICENSE.txt 
--- 


# 核心规则（必须遵守） 
1. 仅基于 **Vue3 + Element Plus**  开发，不替换、不重写、不覆盖 el-* 原生组件 
2. 只做外层美化：布局、配色、字体、间距、阴影、圆角、过渡动效、背景质感 
3. 样式必须加 **scoped** ，禁止全局污染 
4. 接口调用遵循你的项目规范：RESTful API、JSON、CORS、JWT 
5. 禁止生成无关技术栈（React、Tailwind 等） 
6. 禁止使用通用AI丑样式：Inter 字体、烂大街紫白渐变、无脑居中卡片 


--- 


# 设计方向（二选一，精准执行） 
## 风格A：极简高级（后台管理首选） 
- 色调：低饱和、干净、高对比度 
- 布局：宽松留白、卡片悬浮感、层级清晰 
- 动效：轻量、克制、hover 微动反馈 


## 风格B：质感商务（企业系统首选） 
- 色调：深蓝/墨绿主色、中性灰背景 
- 布局：规整、模块化、信息层级强 
- 动效：流畅不浮夸，聚焦体验 


--- 


# 具体美化要求 
## 1. 排版（拒绝通用字体） 
- 标题：粗体、字重 600–700，有视觉层级 
- 正文：清晰易读，行高 1.5–1.6 
- 禁止默认无设计感字体 


## 2. 配色（统一规范） 
- 主色与 Element Plus 主题色一致 
- 使用 CSS 变量统一管理 
- 背景用轻渐变/磨砂/弱纹理，拒绝纯白死板 


## 3. 布局（高级不撞脸） 
- 不对称、有呼吸感、卡片带悬浮效果 
- 适配 PC 端，支持响应式 
- el-card、el-table、el-form 只加外层包装样式，不动内部 


## 4. 动效（轻量流畅） 
- 页面入场：渐入 + 轻微上移 
- 卡片：hover 上浮 + 阴影加深 
- 按钮/输入框：柔和过渡 
- 不搞花里胡哨，保证 60fps 流畅 


## 5. 禁止行为 
- 不修改 el-button、el-table、el-form、el-dialog 等内部结构 
- 不覆盖 Element Plus 原有样式 
- 不生成无法对接后端接口的纯静态页面 
- 不使用复杂图片资源，优先 CSS 实现质感 


--- 


# 输出格式（固定结构） 
```vue 
<template> 
  <div class="page-container"> 
    <!-- 直接使用 ElPlus 组件 --> 
    <el-card class="card-box"> 
      ... 
    </el-card> 
  </div> 
</template> 


<script setup> 
// 接口请求、逻辑、JWT 等按项目规范编写 
</script> 


<style scoped> 
/* 所有美化样式 scoped 隔离 */ 
.page-container {} 
.card-box {} 
</style>