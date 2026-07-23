<template>
  <div>
    <el-card shadow="hover" class="table-card">
      <div style="margin-bottom: 16px; display: flex; justify-content: space-between; align-items: center; gap: 12px; flex-wrap: wrap;">
        <div style="font-size: 16px; font-weight: 600; color: #303133;">操作记录</div>
        <div style="color: #606266; font-size: 13px;">仅负责人可查看，页面仅供查看</div>
      </div>

      <div style="margin-bottom: 16px; display: flex; gap: 12px; align-items: center; flex-wrap: wrap;">
        <span style="font-weight: 600; color: #303133;">选择项目</span>
        <el-select
          v-model="selectedProjectId"
          placeholder="请选择项目"
          clearable
          style="width: 280px;"
          @change="handleProjectChange"
        >
          <el-option
            v-for="project in projects"
            :key="project.id ?? project.projectId"
            :label="project.name || project.projectName || `项目${project.id ?? project.projectId}`"
            :value="project.id ?? project.projectId"
          />
        </el-select>
        <el-button type="primary" plain @click="loadData" :disabled="!selectedProjectId">
          查看记录
        </el-button>
      </div>

      <div v-if="!selectedProjectId" style="padding: 24px 0; color: #909399; text-align: center;">
        请选择一个项目后再查看对应的操作记录。
      </div>
      <div v-else-if="loading" style="padding: 24px 0; color: #909399; text-align: center;">
        正在加载操作记录…
      </div>
      <div v-else-if="logs.length === 0" style="padding: 24px 0; color: #909399; text-align: center;">
        该项目暂时没有操作记录。
      </div>
      <el-table v-else :data="logs" border stripe style="width: 100%">
        <el-table-column label="序号" width="80" align="center">
          <template #default="{ $index }">{{ $index + 1 }}</template>
        </el-table-column>
        <el-table-column label="项目ID" width="100" align="center">
          <template #default="{ row }">{{ row.projectId ?? '-' }}</template>
        </el-table-column>
        <el-table-column label="操作内容" min-width="260">
          <template #default="{ row }">{{ row.detail ?? row.content ?? '-' }}</template>
        </el-table-column>
        <el-table-column label="操作人" min-width="120">
          <template #default="{ row }">{{ row.userId ?? row.operatorId ?? '-' }}</template>
        </el-table-column>
        <el-table-column label="操作时间" min-width="180">
          <template #default="{ row }">{{ row.createTime ?? '-' }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOperationLogs } from '../api/operationLog'
import { getProjects } from '../api/project'

const props = defineProps({ currentUser: Object })
const logs = ref([])
const projects = ref([])
const selectedProjectId = ref('')
const loading = ref(false)

const getListFromPayload = (payload) => {
  const data = payload?.data ?? payload
  if (Array.isArray(data)) return data
  if (Array.isArray(data?.data)) return data.data
  if (Array.isArray(data?.list)) return data.list
  if (Array.isArray(data?.items)) return data.items
  return []
}

const loadProjects = async () => {
  try {
    const res = await getProjects()
    projects.value = getListFromPayload(res)
  } catch (e) {
    projects.value = []
  }
}

const loadData = async () => {
  if (!selectedProjectId.value) {
    logs.value = []
    return
  }

  loading.value = true
  try {
    const res = await getOperationLogs(selectedProjectId.value)
    logs.value = getListFromPayload(res)
  } catch (e) {
    logs.value = []
  } finally {
    loading.value = false
  }
}

const handleProjectChange = () => {
  if (!selectedProjectId.value) {
    logs.value = []
    return
  }
  loadData()
}

onMounted(() => {
  loadProjects()
})
</script>
