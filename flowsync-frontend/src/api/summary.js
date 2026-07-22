import request from './request'

export function getSummaries() {
  return request.get('/api/summaries/list')
}

export function addSummary(summary) {
  return request.post('/api/summaries/add', summary)
}