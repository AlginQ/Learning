<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { VideoPlay, Clock, Document, DataAnalysis, Calendar, Trophy, Star } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getUserStudyRecords, getUserStudyStatistics, getRecentStudyRecords } from '@/api/study'

// 学习记录类型定义
interface StudyRecord {
  id: number
  courseId: number
  courseTitle: string
  lessonTitle: string
  studyTime: string
  duration: number
  progress: number
  category?: string
  difficulty?: string
}

const userStore = useUserStore()
const activeTab = ref('statistics')
const loading = ref(false)
const filterDate = ref<string[]>([])
const selectedCourse = ref('')

// 学习统计数据
const studyStats = reactive({
  totalStudyTime: 0, // 总学习时长（小时）
  completedLessons: 0, // 完成课时数
  completedCourses: 0, // 完成课程数
  currentStreak: 0, // 连续学习天数
  totalCourses: 0, // 总学习课程数
  todayStudyTime: 0, // 今日学习时长
  weekStudyTime: 0, // 本周学习时长
  monthStudyTime: 0 // 本月学习时长
})

// 学习记录数据
const studyRecords = ref<StudyRecord[]>([])

// 时间维度统计数据
const timeStats = ref([
  { period: '今日', hours: 0, lessons: 0, courses: 0 },
  { period: '本周', hours: 0, lessons: 0, courses: 0 },
  { period: '本月', hours: 0, lessons: 0, courses: 0 },
  { period: '总计', hours: 0, lessons: 0, courses: 0 }
])

// 本周学习数据（用于图表）
const weeklyData = ref([
  { day: '周一', hours: 0 },
  { day: '周二', hours: 0 },
  { day: '周三', hours: 0 },
  { day: '周四', hours: 0 },
  { day: '周五', hours: 0 },
  { day: '周六', hours: 0 },
  { day: '周日', hours: 0 }
])

// 课程分布数据
const courseDistribution = ref<{ name: string; value: number }[]>([])

// 难度分布数据
const difficultyDistribution = ref([
  { name: '初级', value: 0 },
  { name: '中级', value: 0 },
  { name: '高级', value: 0 }
])

// 最近学习记录
const recentRecords = computed(() => {
  return studyRecords.value
    .slice()
    .sort((a, b) => new Date(b.studyTime).getTime() - new Date(a.studyTime).getTime())
    .slice(0, 10)
})

// 过滤后的学习记录
const filteredRecords = computed(() => {
  return studyRecords.value.filter(record => {
    // 课程筛选
    if (selectedCourse.value && record.courseId.toString() !== selectedCourse.value) {
      return false
    }
    
    // 日期范围筛选
    if (filterDate.value && filterDate.value.length === 2) {
      const recordDate = new Date(record.studyTime)
      const startDate = new Date(filterDate.value[0])
      const endDate = new Date(filterDate.value[1])
      // 设置结束日期为当天的23:59:59
      endDate.setHours(23, 59, 59, 999)
      if (recordDate < startDate || recordDate > endDate) {
        return false
      }
    }
    
    return true
  })
})

// 处理筛选条件变化
const handleFilterChange = async () => {
  if (userStore.isLogin) {
    loading.value = true
    try {
      // 构建筛选参数
      const params: any = {}
      if (filterDate.value && filterDate.value.length === 2) {
        params.startDate = filterDate.value[0]
        params.endDate = filterDate.value[1]
      }
      
      // 获取学习记录
      const recordsResponse = await getUserStudyRecords(params)
      if (recordsResponse.code === 200 && recordsResponse.data) {
        studyRecords.value = recordsResponse.data
      }
    } catch (error) {
      console.error('获取学习记录失败:', error)
    } finally {
      loading.value = false
    }
  }
}

// 已完成 vs 未完成课程
const completionStats = computed(() => {
  const completed = studyRecords.value.filter(record => record.progress === 100).length
  const inProgress = studyRecords.value.filter(record => record.progress < 100).length
  return [
    { name: '已完成', value: completed },
    { name: '学习中', value: inProgress }
  ]
})

const chartRef = ref()
const pieChartRef = ref()
const difficultyChartRef = ref()
const completionChartRef = ref()

// 初始化图表
const initCharts = () => {
  // 学习趋势图
  if (chartRef.value) {
    const chart = echarts.init(chartRef.value)
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: weeklyData.value.map(item => item.day)
      },
      yAxis: {
        type: 'value',
        name: '学习时长(小时)'
      },
      series: [{
        data: weeklyData.value.map(item => item.hours),
        type: 'line',
        smooth: true,
        areaStyle: {
          opacity: 0.3
        },
        itemStyle: {
          color: '#409eff'
        }
      }]
    }
    chart.setOption(option)
  }

  // 课程分布饼图
  if (pieChartRef.value) {
    const pieChart = echarts.init(pieChartRef.value)
    const option = {
      tooltip: {
        trigger: 'item'
      },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: courseDistribution.value,
        color: ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#722ed1'],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }]
    }
    pieChart.setOption(option)
  }

  // 难度分布饼图
  if (difficultyChartRef.value) {
    const difficultyChart = echarts.init(difficultyChartRef.value)
    const option = {
      tooltip: {
        trigger: 'item'
      },
      series: [{
        type: 'pie',
        radius: ['30%', '60%'],
        data: difficultyDistribution.value,
        color: ['#409eff', '#67c23a', '#e6a23c'],
        label: {
          formatter: '{b}: {d}%'
        }
      }]
    }
    difficultyChart.setOption(option)
  }

  // 完成状态饼图
  if (completionChartRef.value) {
    const completionChart = echarts.init(completionChartRef.value)
    const option = {
      tooltip: {
        trigger: 'item'
      },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: completionStats.value,
        color: ['#67c23a', '#409eff']
      }]
    }
    completionChart.setOption(option)
  }
}

// 页面加载完成后初始化图表
onMounted(async () => {
  if (userStore.isLogin) {
    loading.value = true
    try {
      // 获取学习统计数据
      const statsResponse = await getUserStudyStatistics()
      if (statsResponse.code === 200 && statsResponse.data) {
        const stats = statsResponse.data
        studyStats.totalStudyTime = Math.round((stats.totalStudyTime || 0) * 10) / 10
        studyStats.completedLessons = stats.completedLessons || 0
        studyStats.completedCourses = stats.completedCourses || 0
        studyStats.currentStreak = stats.currentStreak || 0
        studyStats.totalCourses = stats.totalCourses || 0
        studyStats.todayStudyTime = Math.round((stats.todayStudyTime || 0) * 10) / 10
        studyStats.weekStudyTime = Math.round((stats.weekStudyTime || 0) * 10) / 10
        studyStats.monthStudyTime = Math.round((stats.monthStudyTime || 0) * 10) / 10
        
        // 更新时间维度统计数据
        timeStats.value = [
          { period: '今日', hours: Math.round((stats.todayStudyTime || 0) * 10) / 10, lessons: stats.todayLessons || 0, courses: stats.todayCourses || 0 },
          { period: '本周', hours: Math.round((stats.weekStudyTime || 0) * 10) / 10, lessons: stats.weekLessons || 0, courses: stats.weekCourses || 0 },
          { period: '本月', hours: Math.round((stats.monthStudyTime || 0) * 10) / 10, lessons: stats.monthLessons || 0, courses: stats.monthCourses || 0 },
          { period: '总计', hours: Math.round((stats.totalStudyTime || 0) * 10) / 10, lessons: stats.completedLessons || 0, courses: stats.completedCourses || 0 }
        ]
        
        // 更新课程分布数据
        if (stats.courseDistribution) {
          courseDistribution.value = stats.courseDistribution
        }
        
        // 更新难度分布数据
        if (stats.difficultyDistribution) {
          difficultyDistribution.value = stats.difficultyDistribution
        }
        
        // 更新本周学习数据
        if (stats.weeklyData) {
          weeklyData.value = stats.weeklyData
        }
      }
      
      // 获取学习记录
      const recordsResponse = await getUserStudyRecords()
      if (recordsResponse.code === 200 && recordsResponse.data) {
        studyRecords.value = recordsResponse.data
      }
    } catch (error) {
      console.error('获取学习数据失败:', error)
    } finally {
      loading.value = false
      setTimeout(() => {
        initCharts()
      }, 100)
    }
  } else {
    setTimeout(() => {
      initCharts()
    }, 100)
  }
})
</script>

<template>
  <div class="study-records-container">
    <div class="page-header">
      <h1>学习记录与统计</h1>
    </div>
    
    <div class="content-wrapper">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="学习统计" name="statistics">
          <!-- 时间维度统计 -->
          <div class="time-stats">
            <h3>学习时长统计</h3>
            <el-row :gutter="20">
              <el-col :span="6" v-for="stat in timeStats" :key="stat.period">
                <div class="time-stat-card">
                  <div class="period">{{ stat.period }}</div>
                  <div class="hours">{{ stat.hours }}<span class="unit">小时</span></div>
                  <div class="details">
                    <span>{{ stat.lessons }}课时</span>
                    <span>{{ stat.courses }}课程</span>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
          <div class="stats-overview">
            <el-row :gutter="20">
              <el-col :span="6">
                <div class="stat-card">
                  <div class="stat-icon bg-blue">
                    <el-icon><Clock /></el-icon>
                  </div>
                  <div class="stat-content">
                    <div class="stat-number">{{ studyStats.totalStudyTime }}</div>
                    <div class="stat-label">总学习时长(小时)</div>
                  </div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="stat-card">
                  <div class="stat-icon bg-green">
                    <el-icon><VideoPlay /></el-icon>
                  </div>
                  <div class="stat-content">
                    <div class="stat-number">{{ studyStats.completedLessons }}</div>
                    <div class="stat-label">完成课时数</div>
                  </div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="stat-card">
                  <div class="stat-icon bg-purple">
                    <el-icon><Document /></el-icon>
                  </div>
                  <div class="stat-content">
                    <div class="stat-number">{{ studyStats.completedCourses }}</div>
                    <div class="stat-label">完成课程数</div>
                  </div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="stat-card">
                  <div class="stat-icon bg-orange">
                    <el-icon><Star /></el-icon>
                  </div>
                  <div class="stat-content">
                    <div class="stat-number">{{ studyStats.currentStreak }}</div>
                    <div class="stat-label">连续学习天数</div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>

          <div class="charts-section">
            <el-row :gutter="20">
              <el-col :span="12">
                <div class="chart-container">
                  <h3><el-icon><Calendar /></el-icon>本周学习趋势</h3>
                  <div ref="chartRef" class="chart-wrapper"></div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="chart-container">
                  <h3><el-icon><Document /></el-icon>课程类别分布</h3>
                  <div ref="pieChartRef" class="pie-chart-wrapper"></div>
                </div>
              </el-col>
            </el-row>
            
            <el-row :gutter="20" style="margin-top: 20px;">
              <el-col :span="12">
                <div class="chart-container">
                  <h3><el-icon><Trophy /></el-icon>课程难度分布</h3>
                  <div ref="difficultyChartRef" class="pie-chart-wrapper"></div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="chart-container">
                  <h3><el-icon><DataAnalysis /></el-icon>学习完成状态</h3>
                  <div ref="completionChartRef" class="pie-chart-wrapper"></div>
                </div>
              </el-col>
            </el-row>
          </div>
          
          <!-- 最近学习记录 -->
          <div class="recent-records">
            <h3>最近学习记录</h3>
            <el-table :data="recentRecords" stripe v-loading="loading">
              <el-table-column prop="studyTime" label="学习时间" width="180" />
              <el-table-column prop="courseTitle" label="课程名称" min-width="200">
                <template #default="{ row }">
                  <div class="course-info">
                    <div class="course-title">{{ row.courseTitle }}</div>
                    <div class="lesson-title">{{ row.lessonTitle }}</div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="category" label="类别" width="100" />
              <el-table-column prop="duration" label="时长" width="80">
                <template #default="{ row }">{{ row.duration }}分钟</template>
              </el-table-column>
              <el-table-column prop="progress" label="进度" width="120">
                <template #default="{ row }">
                  <el-progress 
                    :percentage="row.progress" 
                    :status="row.progress === 100 ? 'success' : undefined"
                    :show-text="false"
                    :stroke-width="8"
                  />
                  <span class="progress-text">{{ row.progress }}%</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="学习记录" name="records">
          <div class="records-filter">
            <el-date-picker
              v-model="filterDate"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 300px; margin-right: 20px;"
              @change="handleFilterChange"
            />
            <el-select v-model="selectedCourse" placeholder="选择课程" style="width: 200px;" @change="handleFilterChange">
              <el-option label="全部课程" value=""></el-option>
              <el-option label="Java基础教程" value="1"></el-option>
              <el-option label="Vue 3从入门到实战" value="2"></el-option>
              <el-option label="Python数据分析入门" value="3"></el-option>
              <el-option label="Spring Boot企业级开发" value="4"></el-option>
            </el-select>
          </div>
          
          <div class="records-list">
            <el-table :data="filteredRecords" stripe v-loading="loading">
              <el-table-column prop="studyTime" label="学习时间" width="180">
                <template #default="{ row }">
                  {{ row.studyTime }}
                </template>
              </el-table-column>
              <el-table-column prop="courseTitle" label="课程名称" min-width="200">
                <template #default="{ row }">
                  <div class="course-info">
                    <div class="course-title">{{ row.courseTitle }}</div>
                    <div class="lesson-title">{{ row.lessonTitle }}</div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="duration" label="学习时长" width="120">
                <template #default="{ row }">
                  {{ row.duration }}分钟
                </template>
              </el-table-column>
              <el-table-column prop="progress" label="完成进度" width="150">
                <template #default="{ row }">
                  <el-progress 
                    :percentage="row.progress" 
                    :status="row.progress === 100 ? 'success' : undefined"
                    :show-text="false"
                  />
                  <span class="progress-text">{{ row.progress }}%</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button type="primary" link size="small">重新学习</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<style scoped>
.study-records-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
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

.stats-overview {
  margin-bottom: 40px;
}

.stat-card {
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  height: 120px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
}

.stat-icon .el-icon {
  font-size: 28px;
  color: white;
}

.bg-blue { background-color: #409eff; }
.bg-green { background-color: #67c23a; }
.bg-purple { background-color: #67c23a; }
.bg-orange { background-color: #fa8c16; }

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.charts-section {
  margin-top: 30px;
}

.chart-container {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.chart-container h3 {
  margin: 0 0 20px;
  color: #333;
}

.chart-wrapper {
  height: 300px;
  width: 100%;
}

.pie-chart-wrapper {
  height: 300px;
  width: 100%;
}

.records-filter {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.records-list {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.course-info .course-title {
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.course-info .lesson-title {
  color: #666;
  font-size: 14px;
}

.progress-text {
  margin-left: 10px;
  font-size: 12px;
  color: #666;
}

.time-stats {
  margin-bottom: 30px;
}

.time-stats h3 {
  margin-bottom: 20px;
  color: #333;
}

.time-stat-card {
  background: linear-gradient(135deg, #409eff 0%, #667eea 100%);
  border-radius: 12px;
  padding: 20px;
  color: white;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  transition: transform 0.3s;
}

.time-stat-card:hover {
  transform: translateY(-5px);
}

.period {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 10px;
}

.hours {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 10px;
}

.unit {
  font-size: 16px;
  font-weight: normal;
  margin-left: 5px;
}

.details {
  display: flex;
  justify-content: space-around;
  font-size: 14px;
  opacity: 0.9;
}

.details span {
  background: rgba(255,255,255,0.2);
  padding: 5px 10px;
  border-radius: 15px;
}

.recent-records {
  margin-top: 30px;
}

.recent-records h3 {
  margin-bottom: 20px;
  color: #333;
}

.chart-container h3 {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0 0 20px;
  color: #333;
}

.chart-container h3 .el-icon {
  font-size: 20px;
  color: #409eff;
}
</style>