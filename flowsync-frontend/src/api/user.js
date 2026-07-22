import request from './request'

export function getUsers() {
  return request.get('/api/users')
}

export function updatePassword(userId, oldPassword, newPassword) {
  return request.post('/api/users/update-password', { userId, oldPassword, newPassword })
}
