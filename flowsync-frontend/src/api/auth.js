import request from './request'

export function login(username, password) {
  return request.post('/api/auth/login', { username, password })
}

export function register(username, password, confirmPassword, realName, role) {
  return request.post('/api/auth/register', { username, password, confirmPassword, realName, role })
}
