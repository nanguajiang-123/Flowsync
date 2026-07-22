import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '',
  timeout: 15000
})

// 不需要 currentUserId 的接口
const shouldSkipCurrentUserId = (url = '') => {
  return (
    url === '/api/auth/login' ||
    url === '/api/auth/register' ||
    url.startsWith('/api/ai/')
  )
}

// 请求拦截器：自动附加 currentUserId
request.interceptors.request.use(
  config => {
    if (shouldSkipCurrentUserId(config.url)) {
      return config
    }

    const userStr = sessionStorage.getItem('currentUser')

    if (userStr) {
      try {
        const user = JSON.parse(userStr)

        if (user && user.id) {
          config.params = {
            ...(config.params || {}),
            currentUserId: user.id
          }
        }
      } catch (error) {
        console.error('当前用户信息解析失败：', error)
        sessionStorage.removeItem('currentUser')
        sessionStorage.removeItem('token')
      }
    }

    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器：统一错误提示
request.interceptors.response.use(
  response => {
    const data = response.data

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