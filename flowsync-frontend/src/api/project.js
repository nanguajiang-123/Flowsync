import request from './request'

export function getProjects() {
  return request.get('/api/projects')
}

export function saveProject(project) {
  return request.post('/api/projects', project)
}

export function deleteProject(id) {
  return request.delete(`/api/projects/${id}`)
}
