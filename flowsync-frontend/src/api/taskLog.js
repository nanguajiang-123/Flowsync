import request from './request'

export function getTaskLogs(taskId) {
  return request.get('/api/task-logs/list', {
    params: { taskId }
  })
}

export function addTaskLog(log) {
  return request.post('/api/task-logs/add', log)
}