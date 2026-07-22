<template>
  <div>
    <!-- Step 1: 输入区域 -->
    <el-card shadow="hover" style="margin-bottom:20px;">
      <template #header>📝 选择项目并输入任务目标</template>
      <el-form :model="aiForm" label-width="100px">
        <el-form-item label="选择项目" required>
          <el-select v-model="aiForm.projectId" style="width:100%" @change="onProjectSelect">
            <el-option v-for="p in projects" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务目标" required>
          <el-input v-model="aiForm.goal" type="textarea" :rows="2"
                    placeholder="描述要完成的任务目标，例如：开发一个校园活动报名系统" />
        </el-form-item>
        <el-form-item label="补充说明">
          <el-input v-model="aiForm.description" type="textarea" :rows="2"
                    placeholder="补充说明，例如：需要支持微信登录、活动发布、报名审核等功能" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="generatePlan" :loading="aiLoading">
            🤖 AI 智能拆解任务
          </el-button>
          <span style="margin-left:12px;color:#909399;font-size:13px;">AI 将自动拆解并推荐负责人</span>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Step 2: AI 拆解结果 -->
    <el-card v-if="planData" shadow="hover" style="margin-bottom:20px;">
      <template #header>📋 AI 拆解结果</template>
      <div class="plan-summary">{{ planData.summary }}</div>
      <el-table :data="planData.items" border stripe style="width:100%"
                @selection-change="onSelectionChange" ref="planTable">
        <el-table-column type="selection" width="50" />
        <el-table-column label="任务标题" prop="title" min-width="160" />
        <el-table-column label="任务说明" prop="description" min-width="200" show-overflow-tooltip />
        <el-table-column label="优先级" prop="priority" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="priorityType(row.priority)">{{ row.priority }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="建议天数" prop="suggestedDays" width="90" align="center" />
        <el-table-column label="推荐负责人" width="200" align="center">
          <template #default="{ $index }">
            <el-select v-model="planData.items[$index].assigneeId" style="width:140px" placeholder="请选择">
              <el-option v-for="u in users" :key="u.id" :label="u.realName" :value="u.id" />
            </el-select>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:16px;text-align:right;">
        <el-button type="success" @click="importTasks" :disabled="selectedRows.length === 0">
          📥 导入选中任务 ({{ selectedRows.length }})
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getProjects } from '../api/project'
import { getUsers } from '../api/user'
import { getTaskPlan, importTaskPlan } from '../api/ai'

const props = defineProps({ currentUser: Object })

const projects = ref([])
const users = ref([])
const aiForm = ref({ projectId: null, goal: '', description: '' })
const aiLoading = ref(false)
const planData = ref(null)
const selectedRows = ref([])
const planTable = ref(null)

const priorityType = (p) => ({ '低': 'info', '中': 'warning', '高': 'danger' })[p] || 'info'

const onProjectSelect = () => {}
const onSelectionChange = (rows) => { selectedRows.value = rows }

const loadProjects = async () => {
  try {
    const [pRes, uRes] = await Promise.all([getProjects(), getUsers()])
    if (pRes.success) projects.value = pRes.data || []
    if (uRes.success) users.value = uRes.data || []
  } catch (e) {}
}

const generatePlan = async () => {
  if (!aiForm.value.projectId || !aiForm.value.goal) {
    ElMessage.warning('请选择项目并输入任务目标')
    return
  }
  const project = projects.value.find(p => p.id === aiForm.value.projectId)
  aiLoading.value = true
  try {
    const res = await getTaskPlan(
      aiForm.value.projectId,
      props.currentUser?.id,
      project?.name || '',
      aiForm.value.goal,
      aiForm.value.description
    )
    if (res.success && res.data) {
      planData.value = res.data
      ElMessage.success('AI 拆解完成，请确认后导入')
    }
  } catch (e) {} finally { aiLoading.value = false }
}

const importTasks = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少勾选一个任务')
    return
  }
  // 检查是否所有选中任务都有负责人
  const noAssignee = selectedRows.value.some(item => !item.assigneeId)
  if (noAssignee) { ElMessage.warning('所有选中任务都必须指定负责人'); return }

  try {
    const res = await importTaskPlan(aiForm.value.projectId, props.currentUser?.id, selectedRows.value)
    if (res.success) {
      ElMessage.success(res.message || '导入成功')
      planData.value = null
      selectedRows.value = []
    }
  } catch (e) {}
}

onMounted(loadProjects)
</script>
