<template>
  <div>
    <!-- Step 1: 输入区域 -->
    <el-card shadow="hover" style="margin-bottom:20px;">
      <template #header>任务拆解</template>
      <el-form label-width="100px">
        <el-form-item label="选择项目" required>
          <el-select v-model="selectedProject" style="width:100%" placeholder="请选择项目">
            <el-option v-for="p in projects" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="generatePlan" :loading="aiLoading" :disabled="!selectedProject">
            智能拆解任务
          </el-button>
          <span style="margin-left:12px;color:#909399;font-size:13px;">AI 将根据项目名称自动拆解并分配负责人</span>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Step 2: 任务列表 -->
    <el-card v-if="planItems.length > 0" shadow="hover">
      <el-table :data="planItems" border stripe style="width:100%"
                @selection-change="onSelectionChange" ref="planTable">
        <el-table-column type="selection" width="50" />
        <el-table-column label="任务描述" prop="taskdesc" min-width="240" show-overflow-tooltip />
        <el-table-column label="AI推荐负责人" prop="assignee" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="assigneeTag(row.assignee)">{{ row.assignee }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="优先级" prop="priority" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="priorityType(row.priority)">{{ row.priority }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="截止日期" prop="dueDate" width="120" align="center" />
        <el-table-column label="负责人调整" width="160" align="center">
          <template #default="{ $index }">
            <el-select v-model="planItems[$index].assigneeId" style="width:130px" size="small">
              <el-option label="项目负责人" :value="2" />
              <el-option label="张三" :value="3" />
              <el-option label="李四" :value="4" />
              <el-option label="系统管理员" :value="1" />
            </el-select>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:16px;text-align:right;">
        <el-button type="success" @click="importTasks" :disabled="selectedRows.length === 0">
          导入选中任务 ({{ selectedRows.length }})
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getProjects } from '../api/project'
import { getTaskPlan, importTaskPlan } from '../api/ai'

const props = defineProps({ currentUser: Object })

const projects = ref([])
const selectedProject = ref(null)
const aiLoading = ref(false)
const planItems = ref([])
const selectedRows = ref([])
const planTable = ref(null)

const priorityType = (p) => ({ '低': 'info', '中': 'warning', '高': 'danger' })[p] || 'info'
const assigneeTag = (a) => ({ '项目负责人': 'success', '张三': '', '李四': 'warning', '系统管理员': 'danger' })[a] || 'info'

const onSelectionChange = (rows) => { selectedRows.value = rows }

const loadProjects = async () => {
  try {
    const res = await getProjects()
    if (res.success) projects.value = res.data || []
  } catch (e) {}
}

const generatePlan = async () => {
  if (!selectedProject.value) { ElMessage.warning('请先选择项目'); return }
  const project = projects.value.find(p => p.id === selectedProject.value)
  if (!project) { ElMessage.error('项目不存在'); return }

  aiLoading.value = true
  planItems.value = []
  try {
    const res = await getTaskPlan(project.name)
    if (res.success && res.data) {
      planItems.value = res.data || []
      ElMessage.success(`AI 拆解完成，共 ${planItems.value.length} 条任务，请确认后导入`)
    }
  } catch (e) {
    ElMessage.error('AI 拆解失败，请检查 API Key 配置')
  } finally { aiLoading.value = false }
}

const importTasks = async () => {
  if (selectedRows.value.length === 0) { ElMessage.warning('请至少勾选一个任务'); return }

  // 确保每个任务都有负责人
  for (let item of selectedRows.value) {
    if (!item.assigneeId) {
      ElMessage.warning(`任务【${item.taskdesc}】未指定负责人`)
      return
    }
  }

  try {
    const res = await importTaskPlan(selectedProject.value, props.currentUser?.id, selectedRows.value)
    if (res.success) {
      ElMessage.success(res.message || '导入成功')
      planItems.value = []
      selectedRows.value = []
    }
  } catch (e) {}
}

onMounted(loadProjects)
</script>
