<template>
  <div>
    <div class="stats-row">
      <el-card class="stat-card" shadow="hover">
        <div class="number">{{ stats.userCount }}</div>
        <div class="label">用户总数</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="number">{{ stats.projectCount }}</div>
        <div class="label">项目总数</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="number">{{ stats.taskCount }}</div>
        <div class="label">任务总数</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="number">{{ stats.summaryCount }}</div>
        <div class="label">总结总数</div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOverview } from '../api/overview'

defineProps({ currentUser: Object })

const stats = ref({ userCount: 0, projectCount: 0, taskCount: 0, summaryCount: 0 })

onMounted(async () => {
  try {
    const res = await getOverview()
    if (res.success && res.data) stats.value = res.data
  } catch (e) {}
})
</script>
