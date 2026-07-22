<template>
  <div class="login-container">
    <el-card class="login-card" shadow="always">
      <h2>🔗 FlowSync</h2>
      <p style="text-align:center;color:#909399;margin-bottom:24px;">小组任务协同管理系统</p>
      <el-form :model="loginForm" label-width="0">
        <el-form-item>
          <el-input v-model="loginForm.username" placeholder="用户名" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="loginForm.password" type="password" placeholder="密码" size="large"
                    prefix-icon="Lock" @keyup.enter="handleLogin" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%" @click="handleLogin" :loading="loading">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <p style="text-align:center;color:#c0c4cc;font-size:12px;">
        测试账号：leader / member1 / member2 ｜ 密码：123456
      </p>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/auth'

const router = useRouter()
const loading = ref(false)
const loginForm = ref({ username: '', password: '' })

const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(loginForm.value.username, loginForm.value.password)
    if (res.success && res.data) {
      sessionStorage.setItem('currentUser', JSON.stringify(res.data.user))
      sessionStorage.setItem('token', res.data.token)
      ElMessage.success('登录成功')
      router.push('/home')
    }
  } finally {
    loading.value = false
  }
}
</script>
