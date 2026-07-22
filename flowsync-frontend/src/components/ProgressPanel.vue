<template>
  <div>
    <div class="search-bar">
      <el-select v-model="filterTaskId" placeholder="按任务筛选" clearable style="width:220px" @change="loadData">
        <el-option v-for="t in tasks" :key="t.id" :label="`#${t.id} ${t.title}`" :value="t.id" />
      </el-select>
      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon> 新增进度记录
      </el-button>
    </div>

    <el-card class="table-card" shadow="hover">
      <el-table :data="logs" border stripe style="width:100%">
        <el-table-column label="编号" prop="id" width="70" align="center" />
        <el-table-column label="任务 ID" prop="taskId" width="90" align="center" />
        <el-table-column label="进度（%）" prop="progressPercent" width="120" align="center">
          <template #default="{ row }">
            <el-progress :percentage="row.progressPercent" :status="row.progressPercent >= 100 ? 'success' : ''" />
          </template>
        </el-table-column>
        <el-table-column label="进度说明" prop="content" min-width="300" show-overflow-tooltip />
        <el-table-column label="记录人" prop="operatorId" width="90" align="center" />
        <el-table-column label="时间" prop="createTime" width="170" align="center" />
      </el-table>
    </el-card>

    <!-- 新增弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增进度记录" width="450px" destroy-on-close>
      <el-form :model="form" label-width="100px">
        <el-form-item label="关联任务" required>
          <el-select v-model="form.taskId" style="width:100%">
            <el-option v-for="t in tasks" :key="t.id" :label="`#${t.id} ${t.title}`" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="进度百分比" required>
          <el-input-number v-model="form.progressPercent" :min="0" :max="100" style="width:100%" />
        </el-form-item>
        <el-form-item label="进度说明" required>
          <el-input v-model="form.content" type="textarea" :rows="3" maxlength="1000" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTaskLogs, addTaskLog } from '../api/taskLog'
import { getTasks } from '../api/task'

defineProps({ currentUser: Object })

const logs = ref([])
const tasks = ref([])
const filterTaskId = ref(null)
const dialogVisible = ref(false)
const form = ref({ taskId: null, progressPercent: 0, content: '', operatorId: null })

const loadData = async () => {
  try {
    const [logRes, taskRes] = await Promise.all([
      getTaskLogs(filterTaskId.value || undefined),
      getTasks()
    ])
    if (logRes.success) logs.value = logRes.data || []
    if (taskRes.success) tasks.value = taskRes.data || []
  } catch (e) {}
}

const openAddDialog = () => {
  const user = JSON.parse(sessionStorage.getItem('currentUser') || '{}')
  form.value = { taskId: null, progressPercent: 0, content: '', operatorId: user.id }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.value.taskId || !form.value.content) { ElMessage.warning('请完善必填字段'); return }
  try {
    await addTaskLog(form.value)
    ElMessage.success('新增成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {}
}

onMounted(loadData)
</script>
