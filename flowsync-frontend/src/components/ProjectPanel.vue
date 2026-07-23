<template>
  <div>
    <div style="margin-bottom:16px;">
      <el-button v-if="isLeader" class="create-action-btn" @click="openAddDialog">
        <el-icon><Plus /></el-icon> 新建项目
      </el-button>
    </div>

    <el-card class="table-card" shadow="hover">
      <el-table :data="projects" border stripe style="width:100%">
        <el-table-column label="编号" prop="id" width="70" align="center" />
        <el-table-column label="项目名称" prop="name" min-width="180" />
        <el-table-column label="项目说明" prop="description" min-width="200" show-overflow-tooltip />
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
        <el-table-column label="负责人 ID" prop="ownerId" width="100" align="center" />
        <el-table-column label="时间范围" width="200" align="center">
          <template #default="{ row }">{{ row.startDate || '-' }} ~ {{ row.endDate || '-' }}</template>
        </el-table-column>
        <el-table-column v-if="isLeader" label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain @click="openEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑项目' : '新建项目'" width="500px" destroy-on-close>
      <el-form :model="form" label-width="100px">
        <el-form-item label="项目名称" required>
          <el-input v-model="form.name" placeholder="请输入项目名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="项目说明">
          <el-input v-model="form.description" type="textarea" :rows="3" maxlength="500" />
        </el-form-item>
        <el-form-item label="项目状态" required>
          <el-select v-model="form.status" style="width:100%">
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" required>
          <el-select v-model="form.priority" style="width:100%">
            <el-option label="低" value="低" />
            <el-option label="中" value="中" />
            <el-option label="高" value="高" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="form.startDate" type="date" placeholder="选择日期" style="width:100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="form.endDate" type="date" placeholder="选择日期" style="width:100%" value-format="YYYY-MM-DD" />
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
import { getProjects, saveProject, deleteProject } from '../api/project'

const props = defineProps({ currentUser: Object })
const isLeader = computed(() => props.currentUser?.role === '负责人' || props.currentUser?.role === '管理员')

const projects = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ id: null, name: '', description: '', status: '未开始', priority: '中', ownerId: null, startDate: null, endDate: null })

const statusType = (s) => ({ '未开始': 'info', '进行中': 'success', '已完成': '' })[s] || 'info'
const priorityType = (p) => ({ '低': 'info', '中': 'warning', '高': 'danger' })[p] || 'info'

const loadData = async () => {
  try {
    const res = await getProjects()
    if (res.success) projects.value = res.data || []
  } catch (e) {}
}

const openAddDialog = () => {
  isEdit.value = false
  form.value = { id: null, name: '', description: '', status: '未开始', priority: '中', ownerId: props.currentUser?.id, startDate: null, endDate: null }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.value.name) { ElMessage.warning('请输入项目名称'); return }
  try {
    await saveProject(form.value)
    ElMessage.success(isEdit.value ? '修改成功' : '新建成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {}
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除项目【${row.name}】吗？`, '提示', { type: 'warning' })
    .then(async () => { await deleteProject(row.id); ElMessage.success('删除成功'); loadData() })
    .catch(() => {})
}

onMounted(loadData)
</script>
