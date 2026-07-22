import request from './request'

export function getUsers() {
  return request.get('/api/users/list')
}

export function updatePassword(oldPassword, newPassword) {
  return request.post('/api/users/update-password', {
    oldPassword,
    newPassword
  })
}