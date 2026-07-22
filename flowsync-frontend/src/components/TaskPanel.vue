<template>
  <div>
    <!-- 筛选 -->
    <div class="search-bar">
      <el-select v-model="filterProjectId" placeholder="按项目筛选" clearable style="width:220px" @change="loadData">
        <el-option v-for="p in projects" :key="p.id" :label="p.name" :value="p.id" />
      </el-select>
      <el-button v-if="isLeader" type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon> 新建任务
      </el-button>
    </div>

    <el-card class="table-card" shadow="hover">
      <el-table :data="tasks" border stripe style="width:100%">
        <el-table-column label="编号" prop="id" width="70" align="center" />
        <el-table-column label="任务标题" prop="title" min-width="180" show-overflow-tooltip />
        <el-table-column label="任务说明" prop="description" min-width="200" show-overflow-tooltip />
        <el-table-column label="所属项目" prop="projectId" width="100" align="center" />
        <el-table-column label="父任务" prop="parentId" width="90" align="center">
          <template #default="{ row }">{{ row.parentId || '-' }}</template>
        </el-table-column>
        <el-table-column label="负责人" prop="assigneeId" width="90" align="center" />
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="优先级" prop="priority" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="priorityType(row.priority)">{{ row.priority }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="截止日期" prop="dueDate" width="120" align="center">
          <template #default="{ row }">{{ row.dueDate || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <!-- 负责人可编辑全部 -->
            <template v-if="isLeader">
              <el-button size="small" type="primary" plain @click="openEditDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" plain @click="handleDelete(row)">删除</el-button>
            </template>
            <!-- 成员只能更新自己负责任务的状态 -->
            <template v-else-if="row.assigneeId === currentUser?.id">
              <el-button size="small" type="success" plain @click="openStatusDialog(row)">更新状态</el-button>
            </template>
            <span v-else style="color:#c0c4cc;">-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 任务弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" destroy-on-close>
      <el-form :model="form" label-width="100px">
        <el-form-item label="所属项目" required>
          <el-select v-model="form.projectId" style="width:100%" :disabled="isStatusOnly">
            <el-option v-for="p in projects" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="父任务">
          <el-input-number v-model="form.parentId" :min="0" style="width:100%" :disabled="isStatusOnly" placeholder="可选" />
        </el-form-item>
        <el-form-item label="任务标题" required>
          <el-input v-model="form.title" placeholder="请输入" maxlength="100" :disabled="isStatusOnly" />
        </el-form-item>
        <el-form-item label="任务说明">
          <el-input v-model="form.description" type="textarea" :rows="2" maxlength="1000" :disabled="isStatusOnly" />
        </el-form-item>
        <el-form-item label="负责人" required>
          <el-select v-model="form.assigneeId" style="width:100%" :disabled="isStatusOnly">
            <el-option v-for="u in users" :key="u.id" :label="u.realName" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" required>
          <el-select v-model="form.status" style="width:100%">
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" required>
          <el-select v-model="form.priority" style="width:100%" :disabled="isStatusOnly">
            <el-option label="低" value="低" />
            <el-option label="中" value="中" />
            <el-option label="高" value="高" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker v-model="form.dueDate" type="date" style="width:100%" :disabled="isStatusOnly" value-format="YYYY-MM-DD" />
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTasks, saveTask, deleteTask } from '../api/task'
import { getProjects } from '../api/project'
import { getUsers } from '../api/user'

const props = defineProps({ currentUser: Object })
const isLeader = computed(() => props.currentUser?.role === '负责人' || props.currentUser?.role === '管理员')

const tasks = ref([])
const projects = ref([])
const users = ref([])
const filterProjectId = ref(null)
const dialogVisible = ref(false)
const isEdit = ref(false)
const isStatusOnly = ref(false)
const form = ref({ id: null, projectId: null, parentId: null, title: '', description: '', assigneeId: null, creatorId: null, status: '未开始', priority: '中', dueDate: null })

const dialogTitle = computed(() => {
  if (isStatusOnly.value) return '更新任务状态'
  return isEdit.value ? '编辑任务' : '新建任务'
})

const statusType = (s) => ({ '未开始': 'info', '进行中': 'success', '已完成': '' })[s] || 'info'
const priorityType = (p) => ({ '低': 'info', '中': 'warning', '高': 'danger' })[p] || 'info'

const loadData = async () => {
  try {
    const [taskRes, projRes, userRes] = await Promise.all([
      getTasks(filterProjectId.value || undefined),
      getProjects(),
      getUsers()
    ])
    if (taskRes.success) tasks.value = taskRes.data || []
    if (projRes.success) projects.value = projRes.data || []
    if (userRes.success) users.value = userRes.data || []
  } catch (e) {}
}

const openAddDialog = () => {
  isEdit.value = false
  isStatusOnly.value = false
  form.value = { id: null, projectId: null, parentId: null, title: '', description: '', assigneeId: null, creatorId: props.currentUser?.id, status: '未开始', priority: '中', dueDate: null }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  isStatusOnly.value = false
  form.value = { ...row }
  dialogVisible.value = true
}

const openStatusDialog = (row) => {
  isEdit.value = true
  isStatusOnly.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.value.projectId || !form.value.title) { ElMessage.warning('请完善必填字段'); return }
  try {
    await saveTask(form.value)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {}
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除任务【${row.title}】吗？`, '提示', { type: 'warning' })
    .then(async () => { await deleteTask(row.id); ElMessage.success('删除成功'); loadData() })
    .catch(() => {})
}

onMounted(loadData)
</script>
