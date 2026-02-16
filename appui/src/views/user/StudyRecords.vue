<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { VideoPlay, Clock, Document, DataAnalysis } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const userStore = useUserStore()
const activeTab = ref('records')
const loading = ref(false)

// 学习统计数据
const studyStats = reactive({
  totalStudyTime: 145.5, // 总学习时长（小时）
  completedLessons: 87, // 完成课时数
  completedCourses: 12, // 完成课程数
  currentStreak: 7, // 连续学习天数
  totalCourses: 23 // 总学习课程数
})

// 学习记录数据
const studyRecords = ref([
  {
    id: 1,
    courseId: 1,
    courseTitle: 'Java基础教程',
    lessonTitle: 'Java环境搭建',
    studyTime: '2024-01-15 14:30:00',
    duration: 45, // 分钟
    progress: 100
  },
  {
    id: 2,
    courseId: 1,
    courseTitle: 'Java基础教程',
    lessonTitle: '变量和数据类型',
    studyTime: '2024-01-15 10:15:00',
    duration: 38,
    progress: 100
  },
  {
    id: 3,
    courseId: 2,
    courseTitle: 'Vue 3从入门到实战',
    lessonTitle: 'Vue 3简介',
    studyTime: '2024-01-14 16:20:00',
    duration: 25,
    progress: 75
  },
  {
    id: 4,
    courseId: 2,
    courseTitle: 'Vue 3从入门到实战',
    lessonTitle: 'Composition API',
    studyTime: '2024-01-14 14:00:00',
    duration: 42,
    progress: 30
  }
])

// 本周学习数据（用于图表）
const weeklyData = ref([
  { day: '周一', hours: 2.5 },
  { day: '周二', hours: 3.2 },
  { day: '周三', hours: 1.8 },
  { day: '周四', hours: 4.1 },
  { day: '周五', hours: 2.9 },
  { day: '周六', hours: 3.7 },
  { day: '周日', hours: 1.3 }
])

// 课程分布数据
const courseDistribution = ref([
  { name: '编程语言', value: 8 },
  { name: '前端开发', value: 6 },
  { name: '后端开发', value: 5 },
  { name: '移动开发', value: 2 },
  { name: '数据库', value: 2 }
])

const chartRef = ref()
const pieChartRef = ref()

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
}

// 页面加载完成后初始化图表
onMounted(() => {
  setTimeout(() => {
    initCharts()
  }, 100)
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
                    <el-icon><DataAnalysis /></el-icon>
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
            <el-row :gutter="30">
              <el-col :span="16">
                <div class="chart-container">
                  <h3>本周学习趋势</h3>
                  <div ref="chartRef" class="chart-wrapper"></div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="chart-container">
                  <h3>课程类别分布</h3>
                  <div ref="pieChartRef" class="pie-chart-wrapper"></div>
                </div>
              </el-col>
            </el-row>
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
            />
            <el-select placeholder="选择课程" style="width: 200px;">
              <el-option label="全部课程" value=""></el-option>
              <el-option label="Java基础教程" value="1"></el-option>
              <el-option label="Vue 3从入门到实战" value="2"></el-option>
            </el-select>
          </div>
          
          <div class="records-list">
            <el-table :data="studyRecords" stripe>
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
.bg-purple { background-color: #722ed1; }
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
</style>