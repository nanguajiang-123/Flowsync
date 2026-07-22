<template>
  <el-card class="table-card" shadow="hover">
    <el-table :data="users" border stripe style="width:100%">
      <el-table-column label="编号" prop="id" width="70" align="center" />
      <el-table-column label="用户名" prop="username" width="120" />
      <el-table-column label="真实姓名" prop="realName" width="120" />
      <el-table-column label="角色" prop="role" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="roleType(row.role)">{{ row.role }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="电话" prop="phone" width="140" />
      <el-table-column label="邮箱" prop="email" min-width="200" />
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUsers } from '../api/user'

defineProps({ currentUser: Object })

const users = ref([])
const roleType = (r) => ({ '管理员': 'danger', '负责人': 'success', '成员': 'info' })[r] || 'info'

onMounted(async () => {
  try {
    const res = await getUsers()
    if (res.success) users.value = res.data || []
  } catch (e) {}
})
</script>
