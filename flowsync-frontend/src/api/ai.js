import request from './request'

export function getTaskSuggestion(projectName, taskTitle, taskDescription) {
  return request.post('/api/ai/task-suggestion', { projectName, taskTitle, taskDescription })
}

export function getTaskPlan(projectId, operatorId, projectName, goal, description) {
  return request.post('/api/ai/task-plan', { projectId, operatorId, projectName, goal, description })
}

export function importTaskPlan(projectId, creatorId, items) {
  return request.post('/api/ai/task-plan/import', { projectId, creatorId, items })
}
