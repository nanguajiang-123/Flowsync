import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import HomeView from '../views/HomeView.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', name: 'Login', component: LoginView },
  { path: '/home', name: 'Home', component: HomeView, meta: { requiresAuth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录跳转登录页
const getLoginUser = () => {
  const userStr = sessionStorage.getItem('currentUser')
  const token = sessionStorage.getItem('token')

  if (!userStr || !token) {
    return null
  }

  try {
    const user = JSON.parse(userStr)

    if (!user || !user.id) {
      throw new Error('用户信息不完整')
    }

    return user
  } catch (error) {
    sessionStorage.removeItem('currentUser')
    sessionStorage.removeItem('token')
    return null
  }
}

// 路由守卫：未登录跳转登录页
router.beforeEach((to, from, next) => {
  const user = getLoginUser()

  if (to.meta.requiresAuth && !user) {
    next('/login')
  } else if (to.path === '/login' && user) {
    next('/home')
  } else {
    next()
  }
})

export default router
