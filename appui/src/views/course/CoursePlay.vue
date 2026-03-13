<template>
  <div class="course-play-container">
    <!-- 视频播放器 -->
    <div class="video-player">
      <video 
        ref="videoRef" 
        :src="videoUrl" 
        controls 
        autoplay
      ></video>
    </div>
    
    <!-- 课程信息 -->
    <div class="course-info">
      <h2>{{ course?.title }}</h2>
      <div class="course-meta">
        <span class="teacher">讲师：{{ course?.teacher }}</span>
        <span class="duration">时长：{{ formatDuration(course?.duration || 0) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const videoRef = ref<HTMLVideoElement>()

// 课程信息
const course = ref({
  title: 'Vue 3从入门到实战',
  teacher: '张老师',
  duration: 3600 // 秒
})

// 视频URL
const videoUrl = ref('https://example.com/video.mp4')

// 格式化时长
const formatDuration = (seconds: number): string => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  
  if (hours > 0) {
    return `${hours}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }
  return `${minutes}:${secs.toString().padStart(2, '0')}`
}

onMounted(() => {
  const courseId = route.params.id
  const playTime = parseInt(route.query.time as string) || 0
  
  // 模拟加载课程数据
  console.log('加载课程:', courseId)
  console.log('播放时间:', playTime)
  
  // 设置播放位置
  if (videoRef.value && playTime > 0) {
    videoRef.value.currentTime = playTime
  }
})
</script>

<style scoped>
.course-play-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.video-player {
  margin-bottom: 20px;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}

.video-player video {
  width: 100%;
  height: auto;
  min-height: 450px;
}

.course-info {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.course-info h2 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.course-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #666;
}

@media (max-width: 768px) {
  .video-player video {
    min-height: 300px;
  }
  
  .course-meta {
    flex-direction: column;
    gap: 8px;
  }
}
</style>