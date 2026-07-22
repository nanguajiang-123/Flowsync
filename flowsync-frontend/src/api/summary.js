import request from './request'

export function getSummaries() {
  return request.get('/api/summaries')
}

export function addSummary(summary) {
  return request.post('/api/summaries', summary)
}
