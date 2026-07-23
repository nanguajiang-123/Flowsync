import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '',
  timeout: 15000
})

const normalizeResponseData = (data) => {
  if (data && typeof data === 'object' && Object.prototype.hasOwnProperty.call(data, 'success')) {
    return data
  }
  return { success: true, message: '操作成功', data }
}

const getAuthToken = () => {
  try {
    const token = sessionStorage.getItem('token')
    if (token) return token

    const userStr = sessionStorage.getItem('currentUser')
    if (!userStr) return ''

    const user = JSON.parse(userStr)
    return user?.token || ''
  } catch (error) {
    console.error('当前用户信息解析失败：', error)
    sessionStorage.removeItem('currentUser')
    sessionStorage.removeItem('token')
    return ''
  }
}

const shouldSkipAuth = (url = '') => {
  return (
    url === '/api/auth/login' ||
    url === '/api/auth/register'
  )
}

// 请求拦截器：自动附加 JWT Authorization 头
request.interceptors.request.use(
  config => {
    if (shouldSkipAuth(config.url)) {
      return config
    }

    const token = getAuthToken()
    if (token) {
      config.headers = {
        ...(config.headers || {}),
        Authorization: `Bearer ${token}`
      }
    }

    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器：统一错误提示
request.interceptors.response.use(
  response => {
    const data = normalizeResponseData(response.data)

    if (data && data.success === false) {
      ElMessage.error(data.message || '操作失败')
      return Promise.reject(new Error(data.message || '操作失败'))
    }

    return data
  },
  error => {
    ElMessage.error(error.response?.data?.message || error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request