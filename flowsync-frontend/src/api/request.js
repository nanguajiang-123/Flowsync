import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '',
  timeout: 15000
})

// 请求拦截器：自动附加 currentUserId
request.interceptors.request.use(config => {
  const userStr = sessionStorage.getItem('currentUser')
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      if (user.id) {
        if (config.method === 'get' || config.method === 'delete') {
          config.params = { ...config.params, currentUserId: user.id }
        }
      }
    } catch (e) {}
  }
  return config
}, error => Promise.reject(error))

// 响应拦截器：统一错误提示
request.interceptors.response.use(
  response => {
    const data = response.data
    if (data && data.success === false) {
      ElMessage.error(data.message || '操作失败')
      return Promise.reject(new Error(data.message))
    }
    return data
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
