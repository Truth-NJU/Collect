import request from '@/utils/request'

export function issue(data) {
  return request({
    url: '/collect/task/issue',
    method: 'post',
    data
  })
}

export function update(data) {
  return request({
    url: '/collect/task/update',
    method: 'post',
    data
  })
}
