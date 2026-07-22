import request from './request'

export function getTaskSuggestion(projectName, taskTitle, taskDescription) {
  return request.post('/api/ai/task-suggestion', { projectName, taskTitle, taskDescription })
}

/** AI 任务拆解 —— 新接口，只需传入 projectName */
export function getTaskPlan(projectName) {
  return request.post('/api/ai/task-plan', { projectName })
}

export function importTaskPlan(projectId, creatorId, items) {
  return request.post('/api/ai/task-plan/import', { projectId, creatorId, items })
}
