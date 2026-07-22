import request from './request'

export function getTasks(projectId) {
  return request.get('/api/tasks', { params: { projectId } })
}

export function saveTask(task) {
  return request.post('/api/tasks', task)
}

export function deleteTask(id) {
  return request.delete(`/api/tasks/${id}`)
}
