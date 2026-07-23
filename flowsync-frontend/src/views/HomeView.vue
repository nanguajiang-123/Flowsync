<template>
  <div class="home-container">
    <!-- 左侧菜单 -->
    <div class="home-aside">
      <div style="padding:18px 16px 12px;text-align:center;color:#fff;font-size:18px;font-weight:600;letter-spacing:2px;">
        FlowSync
      </div>
      <el-menu
        :default-active="activeMenu"
        @select="handleMenuSelect"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-sub-menu index="workbench">
          <template #title><el-icon><DataAnalysis /></el-icon> 工作台</template>
          <el-menu-item index="overview">总览</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="business">
          <template #title><el-icon><Briefcase /></el-icon> 业务管理</template>
          <el-menu-item index="project">项目管理</el-menu-item>
          <el-menu-item v-if="isLeader" index="task-decompose">任务拆解</el-menu-item>
          <el-menu-item index="task">任务管理</el-menu-item>
          <el-menu-item index="progress">进度跟踪</el-menu-item>
          <el-menu-item index="summary">总结中心</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="system">
          <template #title><el-icon><Setting /></el-icon> 系统信息</template>
          <el-menu-item index="members">成员列表</el-menu-item>
          <el-menu-item v-if="isLeader" index="operation-log">操作记录</el-menu-item>
          <el-menu-item index="profile">个人信息</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </div>

    <!-- 右侧主区域 -->
    <div style="flex:1;display:flex;flex-direction:column;overflow:hidden;">
      <!-- 顶部栏 -->
      <div class="home-header">
        <span class="home-header-title">{{ panelTitle }}</span>
        <div class="user-info">
          <span style="color:#606266;">{{ currentUser?.realName }}（{{ currentUser?.role }}）</span>
          <el-button size="small" @click="handleLogout">退出登录</el-button>
        </div>
      </div>

      <!-- 面板内容区 -->
      <div class="home-main">
        <OverviewPanel     v-if="activeMenu === 'overview'"      :current-user="currentUser" />
        <ProjectPanel      v-if="activeMenu === 'project'"        :current-user="currentUser" />
        <TaskDecomposePanel v-if="activeMenu === 'task-decompose'" :current-user="currentUser" />
        <TaskPanel         v-if="activeMenu === 'task'"            :current-user="currentUser" />
        <ProgressPanel     v-if="activeMenu === 'progress'"       :current-user="currentUser" />
        <SummaryPanel      v-if="activeMenu === 'summary'"        :current-user="currentUser" />
        <MemberPanel       v-if="activeMenu === 'members'"        :current-user="currentUser" />
        <OperationLogPanel v-if="activeMenu === 'operation-log'" :current-user="currentUser" />
        <ProfilePanel      v-if="activeMenu === 'profile'"        :current-user="currentUser" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

import OverviewPanel from '../components/OverviewPanel.vue'
import ProjectPanel from '../components/ProjectPanel.vue'
import TaskDecomposePanel from '../components/TaskDecomposePanel.vue'
import TaskPanel from '../components/TaskPanel.vue'
import ProgressPanel from '../components/ProgressPanel.vue'
import SummaryPanel from '../components/SummaryPanel.vue'
import MemberPanel from '../components/MemberPanel.vue'
import OperationLogPanel from '../components/OperationLogPanel.vue'
import ProfilePanel from '../components/ProfilePanel.vue'

const router = useRouter()
const activeMenu = ref('overview')

const currentUser = ref(null)
const isLeader = computed(() => currentUser.value?.role === '负责人' || currentUser.value?.role === '管理员')

const panelTitleMap = {
  'overview': '工作台总览',
  'project': '项目管理',
  'task-decompose': 'AI 任务拆解',
  'task': '任务管理',
  'progress': '进度跟踪',
  'summary': '总结中心',
  'members': '成员列表',
  'operation-log': '操作记录',
  'profile': '个人信息'
}
const panelTitle = computed(() => panelTitleMap[activeMenu.value] || '')

const handleMenuSelect = (index) => {
  activeMenu.value = index
}

const handleLogout = () => {
  sessionStorage.clear()
  router.push('/login')
}

onMounted(() => {
  const userStr = sessionStorage.getItem('currentUser')
  if (userStr) {
    try { currentUser.value = JSON.parse(userStr) } catch (e) {}
  } else {
    router.push('/login')
  }
})
</script>
