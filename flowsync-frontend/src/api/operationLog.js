import request from './request'

export function getOperationLogs(projectId) {
  return request.get('/api/operation-logs/list', {
    params: {
      projectId
    }
  })
}
