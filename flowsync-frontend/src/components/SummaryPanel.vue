<template>
  <div>
    <div style="margin-bottom:16px;">
      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon> 新增总结
      </el-button>
    </div>

    <el-card class="table-card" shadow="hover">
      <el-table :data="summaries" border stripe style="width:100%" :row-style="({ row }) => ({ minHeight: Math.max(48, Math.min(96, Math.ceil((row.content?.length || 0) / 36) * 24 + 16)) + 'px' })">
        <el-table-column label="编号" prop="id" width="70" align="center" />
        <el-table-column label="所属项目" prop="projectId" width="100" align="center" />
        <el-table-column label="关联任务" prop="taskId" width="100" align="center">
          <template #default="{ row }">{{ row.taskId || '-' }}</template>
        </el-table-column>
        <el-table-column label="总结类型" prop="summaryType" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.summaryType === '最终总结' ? 'success' : 'warning'">{{ row.summaryType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="总结内容" prop="content" min-width="300">
          <template #default="{ row }">
            <div style="white-space: pre-wrap; word-break: break-word; line-height: 1.5;">{{ row.content || '-' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="创建人" prop="createdBy" width="90" align="center" />
        <el-table-column label="时间" prop="createTime" width="170" align="center" />
      </el-table>
    </el-card>

    <!-- 新增弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增总结" width="500px" destroy-on-close>
      <el-form :model="form" label-width="100px">
        <el-form-item label="所属项目" required>
          <el-select v-model="form.projectId" style="width:100%">
            <el-option v-for="p in projects" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联任务">
          <el-select v-model="form.taskId" style="width:100%" clearable placeholder="可选">
            <el-option v-for="t in tasks" :key="t.id" :label="`#${t.id} ${t.title}`" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="总结类型" required>
          <el-select v-model="form.summaryType" style="width:100%">
            <el-option label="阶段总结" value="阶段总结" />
            <el-option label="最终总结" value="最终总结" />
          </el-select>
        </el-form-item>
        <el-form-item label="总结内容" required>
          <el-input v-model="form.content" type="textarea" :rows="4" maxlength="2000" show-word-limit />
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
import { getSummaries, addSummary } from '../api/summary'
import { getProjects } from '../api/project'
import { getTasks } from '../api/task'

defineProps({ currentUser: Object })

const summaries = ref([])
const projects = ref([])
const tasks = ref([])
const dialogVisible = ref(false)
const form = ref({ projectId: null, taskId: null, summaryType: '阶段总结', content: '', createdBy: null })

const loadData = async () => {
  try {
    const [sRes, pRes, tRes] = await Promise.all([getSummaries(), getProjects(), getTasks()])
    if (sRes.success) summaries.value = sRes.data || []
    if (pRes.success) projects.value = pRes.data || []
    if (tRes.success) tasks.value = tRes.data || []
  } catch (e) {}
}

const openAddDialog = () => {
  const user = JSON.parse(sessionStorage.getItem('currentUser') || '{}')
  form.value = { projectId: null, taskId: null, summaryType: '阶段总结', content: '', createdBy: user.id }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.value.projectId || !form.value.content) { ElMessage.warning('请完善必填字段'); return }
  try {
    await addSummary(form.value)
    ElMessage.success('新增成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {}
}

onMounted(loadData)
</script>
