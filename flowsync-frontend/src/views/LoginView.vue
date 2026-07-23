<template>
  <div class="login-container">
    <el-card class="login-card" shadow="always">
      <h2 style="color:#8fabcd;">FlowSync</h2>
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
          <el-button size="large" style="width:100%; background:#8fabcd; border-color:#8fabcd; color:#fff;" @click="handleLogin" :loading="loading">
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
          <el-input v-model="registerForm.phone" placeholder="联系电话（选填）" size="large" prefix-icon="Phone" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="registerForm.email" placeholder="邮箱（选填）" size="large" prefix-icon="Message" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="registerForm.role" placeholder="请选择角色" size="large" style="width:100%">
            <el-option label="负责人" value="负责人" />
            <el-option label="成员" value="成员" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button size="large" style="width:100%; background:#8fabcd; border-color:#8fabcd; color:#fff;" @click="handleRegister" :loading="loading">
            注 册
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 底部切换 -->
      <p style="text-align:center;color:#909399;font-size:13px;">
        <template v-if="!isRegister">
          没有账号？<el-link style="color:#8fabcd" @click="isRegister = true">注册新账号</el-link>
        </template>
        <template v-else>
          已有账号？<el-link style="color:#8fabcd" @click="isRegister = false">返回登录</el-link>
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
const registerForm = ref({ username: '', password: '', confirmPassword: '', realName: '', phone: '', email: '', role: '' })

const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(loginForm.value.username, loginForm.value.password)
    const payload = res?.data || res
    const user = payload?.user || payload
    const token = payload?.token || sessionStorage.getItem('token')

    if (!user || !user.id || !token) {
      ElMessage.error('登录响应数据不完整')
      return
    }

    sessionStorage.setItem('currentUser', JSON.stringify(user))
    sessionStorage.setItem('token', token)

    ElMessage.success('登录成功')
    router.push('/home')
  } catch (error) {
    ElMessage.error(error?.message || '登录失败')
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
    const res = await register(f.username, f.password, f.confirmPassword, f.realName, f.role, f.phone, f.email)
    if (res?.success !== false) {
      ElMessage.success('注册成功，请登录')
      isRegister.value = false
      loginForm.value.username = f.username
      registerForm.value = { username: '', password: '', confirmPassword: '', realName: '', phone: '', email: '', role: '' }
    }
  } catch (error) {
    ElMessage.error(error?.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>
