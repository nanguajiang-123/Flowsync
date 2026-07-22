<template>
  <div class="login-container">
    <el-card class="login-card" shadow="always">
      <h2>🔗 FlowSync</h2>
      <p style="text-align:center;color:#909399;margin-bottom:24px;">小组任务协同管理系统</p>

      <!-- ===== 登录模式 ===== -->
      <el-form v-if="!isRegister" :model="loginForm" label-width="0">
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

      <!-- ===== 注册模式 ===== -->
      <el-form v-else :model="registerForm" label-width="0">
        <el-form-item>
          <el-input v-model="registerForm.username" placeholder="用户名" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="registerForm.password" type="password" placeholder="密码" size="large"
                    prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="确认密码" size="large"
                    prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-input v-model="registerForm.realName" placeholder="真实姓名" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="registerForm.role" placeholder="请选择角色" size="large" style="width:100%">
            <el-option label="负责人" value="负责人" />
            <el-option label="成员" value="成员" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="success" size="large" style="width:100%" @click="handleRegister" :loading="loading">
            注 册
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 底部切换 -->
      <p style="text-align:center;color:#909399;font-size:13px;">
        <template v-if="!isRegister">
          没有账号？<el-link type="primary" @click="isRegister = true">注册新账号</el-link>
        </template>
        <template v-else>
          已有账号？<el-link type="primary" @click="isRegister = false">返回登录</el-link>
        </template>
      </p>
      <p style="text-align:center;color:#c0c4cc;font-size:12px;margin-top:8px;">
        测试账号：leader / member1 / member2 ｜ 密码：123456
      </p>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register } from '../api/auth'

const router = useRouter()
const loading = ref(false)
const isRegister = ref(false)
const loginForm = ref({ username: '', password: '' })
const registerForm = ref({ username: '', password: '', confirmPassword: '', realName: '', role: '' })

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

const handleRegister = async () => {
  const f = registerForm.value
  if (!f.username || !f.password || !f.confirmPassword || !f.realName || !f.role) {
    ElMessage.warning('请完整填写所有字段')
    return
  }
  loading.value = true
  try {
    const res = await register(f.username, f.password, f.confirmPassword, f.realName, f.role)
    if (res.success) {
      ElMessage.success('注册成功，请登录')
      isRegister.value = false
      loginForm.value.username = f.username
      registerForm.value = { username: '', password: '', confirmPassword: '', realName: '', role: '' }
    }
  } finally {
    loading.value = false
  }
}
</script>
