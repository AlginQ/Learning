# 在线学习系统 API 接口文档

## 一、通用响应格式

所有接口返回统一格式：
```json
{
  "code": 200,
  "data": {},
  "msg": "success"
}
```

**状态码说明：**
- 200: 请求成功
- 400: 请求参数错误
- 401: 未授权/登录失效
- 403: 权限不足
- 404: 资源不存在
- 500: 服务器内部错误

## 二、认证模块

### 1. 用户注册
**POST** `/api/auth/register`

**请求参数：**
```json
{
  "username": "string",     // 用户名，3-20位
  "password": "string",     // 密码，6-20位
  "email": "string",        // 邮箱
  "nickname": "string"      // 昵称（可选）
}
```

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "nickname": "测试用户",
    "createTime": "2024-01-01T12:00:00"
  },
  "msg": "注册成功"
}
```

### 2. 用户登录
**POST** `/api/auth/login`

**请求参数：**
```json
{
  "username": "string",     // 用户名或邮箱
  "password": "string"      // 密码
}
```

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "nickname": "测试用户",
      "avatar": "https://example.com/avatar.jpg"
    }
  },
  "msg": "登录成功"
}
```

### 3. 获取当前用户信息
**GET** `/api/auth/info`

**Headers:**
```
Authorization: Bearer <token>
```

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "nickname": "测试用户",
    "avatar": "https://example.com/avatar.jpg",
    "role": "USER",           // USER 或 ADMIN
    "createTime": "2024-01-01T12:00:00"
  },
  "msg": "success"
}
```

### 4. 退出登录
**POST** `/api/auth/logout`

**Headers:**
```
Authorization: Bearer <token>
```

**响应示例：**
```json
{
  "code": 200,
  "data": null,
  "msg": "退出成功"
}
```

## 三、课程模块

### 1. 获取课程列表
**GET** `/api/courses`

**查询参数：**
```
page: 页码（默认1）
size: 每页数量（默认10）
keyword: 搜索关键词（可选）
category: 分类ID（可选）
sortBy: 排序字段（可选：latest/popular）
```

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "title": "Java基础教程",
        "description": "从零开始学习Java编程",
        "coverImage": "https://example.com/course1.jpg",
        "price": 99.00,
        "discountPrice": 49.00,
        "category": {
          "id": 1,
          "name": "编程语言"
        },
        "teacher": {
          "id": 1,
          "name": "张老师",
          "avatar": "https://example.com/teacher1.jpg"
        },
        "lessonCount": 50,
        "studentCount": 1234,
        "rating": 4.8,
        "createTime": "2024-01-01T12:00:00"
      }
    ],
    "total": 100,
    "current": 1,
    "pages": 10
  },
  "msg": "success"
}
```

### 2. 获取课程详情
**GET** `/api/courses/{id}`

**路径参数：**
- id: 课程ID

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "title": "Java基础教程",
    "description": "从零开始学习Java编程",
    "coverImage": "https://example.com/course1.jpg",
    "price": 99.00,
    "discountPrice": 49.00,
    "category": {
      "id": 1,
      "name": "编程语言"
    },
    "teacher": {
      "id": 1,
      "name": "张老师",
      "avatar": "https://example.com/teacher1.jpg",
      "introduction": "资深Java开发工程师"
    },
    "lessons": [
      {
        "id": 1,
        "title": "第1章 Java简介",
        "chapter": 1,
        "videoUrl": "https://example.com/video1.mp4",
        "duration": 1800,       // 秒
        "isFree": true
      }
    ],
    "lessonCount": 50,
    "studentCount": 1234,
    "rating": 4.8,
    "introduction": "本课程适合零基础学员...",
    "requirements": ["无编程基础", "有学习热情"],
    "targetAudience": ["初学者", "转行人员"],
    "createTime": "2024-01-01T12:00:00"
  },
  "msg": "success"
}
```

### 3. 搜索课程
**GET** `/api/courses/search`

**查询参数：**
```
keyword: 搜索关键词
page: 页码（默认1）
size: 每页数量（默认10）
```

## 四、学习模块

### 1. 获取课程章节列表
**GET** `/api/courses/{courseId}/lessons`

**路径参数：**
- courseId: 课程ID

**响应示例：**
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "title": "第1章 Java简介",
      "chapter": 1,
      "lessons": [
        {
          "id": 1,
          "title": "1.1 什么是Java",
          "videoUrl": "https://example.com/video1.mp4",
          "duration": 1800,
          "isFree": true,
          "isCompleted": false,
          "progress": 0        // 学习进度百分比 0-100
        }
      ]
    }
  ],
  "msg": "success"
}
```

### 2. 记录学习进度
**POST** `/api/study/progress`

**Headers:**
```
Authorization: Bearer <token>
```

**请求参数：**
```json
{
  "lessonId": 1,            // 课时ID
  "progress": 75,           // 学习进度百分比
  "watchDuration": 1350     // 已观看时长（秒）
}
```

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "userId": 1,
    "lessonId": 1,
    "progress": 75,
    "watchDuration": 1350,
    "isCompleted": false,
    "lastStudyTime": "2024-01-01T12:00:00"
  },
  "msg": "进度保存成功"
}
```

### 3. 获取我的学习记录
**GET** `/api/study/records`

**Headers:**
```
Authorization: Bearer <token>
```

**查询参数：**
```
page: 页码（默认1）
size: 每页数量（默认10）
```

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "courseId": 1,
        "courseTitle": "Java基础教程",
        "courseCover": "https://example.com/course1.jpg",
        "lastLesson": {
          "id": 5,
          "title": "1.5 Java环境搭建"
        },
        "totalProgress": 25,      // 总体进度百分比
        "studyTime": 7200,        // 总学习时长（秒）
        "lastStudyTime": "2024-01-01T12:00:00"
      }
    ],
    "total": 5,
    "current": 1,
    "pages": 1
  },
  "msg": "success"
}
```

## 五、分类模块

### 1. 获取所有分类
**GET** `/api/categories`

**响应示例：**
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "编程语言",
      "icon": "code",
      "courseCount": 25
    },
    {
      "id": 2,
      "name": "前端开发",
      "icon": "desktop",
      "courseCount": 18
    }
  ],
  "msg": "success"
}
```

## 六、管理模块（管理员接口）

### 1. 获取用户列表
**GET** `/api/admin/users`

**Headers:**
```
Authorization: Bearer <admin_token>
```

**查询参数：**
```
page: 页码
size: 每页数量
keyword: 搜索关键词
```

### 2. 获取课程列表（管理）
**GET** `/api/admin/courses`

**Headers:**
```
Authorization: Bearer <admin_token>
```

### 3. 创建课程
**POST** `/api/admin/courses`

**Headers:**
```
Authorization: Bearer <admin_token>
```

**请求参数：**
```json
{
  "title": "string",
  "description": "string",
  "coverImage": "string",
  "price": 0,
  "categoryId": 1,
  "teacherId": 1,
  "introduction": "string",
  "requirements": ["string"],
  "targetAudience": ["string"]
}
```

### 4. 更新课程
**PUT** `/api/admin/courses/{id}`

### 5. 删除课程
**DELETE** `/api/admin/courses/{id}`

## 七、错误响应示例

```json
{
  "code": 400,
  "data": null,
  "msg": "用户名已存在"
}
```

```json
{
  "code": 401,
  "data": null,
  "msg": "登录已过期，请重新登录"
}
```

```json
{
  "code": 403,
  "data": null,
  "msg": "权限不足"
}
```

```json
{
  "code": 500,
  "data": null,
  "msg": "服务器内部错误"
}
```