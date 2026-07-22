<template>
  <div style="max-width:600px;">
    <!-- 个人信息卡片 -->
    <el-card shadow="hover" style="margin-bottom:20px;">
      <template #header><span>👤 个人资料</span></template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ currentUser?.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ currentUser?.realName }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="currentUser?.role === '管理员' ? 'danger' : currentUser?.role === '负责人' ? 'success' : 'info'">
            {{ currentUser?.role }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="电话">{{ currentUser?.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser?.email || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 修改密码 -->
    <el-card shadow="hover">
      <template #header><span>🔒 修改密码</span></template>
      <el-form :model="pwdForm" label-width="100px" style="max-width:400px;">
        <el-form-item label="旧密码" required>
          <el-input v-model="pwdForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" required>
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" required>
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdatePwd">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { updatePassword } from '../api/user'

const props = defineProps({ currentUser: Object })
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

const handleUpdatePwd = async () => {
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword) { ElMessage.warning('请填写完整'); return }
  if (pwdForm.value.newPassword !== pwdForm.value.confirmPassword) { ElMessage.warning('两次密码不一致'); return }
  try {
    await updatePassword(pwdForm.value.oldPassword, pwdForm.value.newPassword)
    ElMessage.success('密码修改成功')
    pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch (e) {}
}
</script>
