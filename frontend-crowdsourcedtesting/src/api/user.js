import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/collect/user/login',
    method: 'post',
    data
  })
}


export function tryLogin() {
  return request({
    url: '/collect/user/tryLogin',
    method: 'get'
  })
}
