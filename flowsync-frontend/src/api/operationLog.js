import request from './request'

export function getOperationLogs(projectId, currentUserId) {
  return request.get('/api/operation-logs/list', {
    params: {
      projectId,
      currentUserId
    }
  })
}
