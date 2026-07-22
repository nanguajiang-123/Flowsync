import request from './request'

export function getTaskLogs(taskId) {
  return request.get('/api/task-logs', { params: { taskId } })
}

export function addTaskLog(log) {
  return request.post('/api/task-logs', log)
}
