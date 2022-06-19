const TokenKey = 'collect_token'
const UserIdentity = 'identity'
const UserId = 'id'
const First = 'first'

export function getToken() {
  return sessionStorage.getItem(TokenKey)
}

export function setToken(token) {
  return sessionStorage.setItem(TokenKey, token)
}

export function removeToken() {
  sessionStorage.removeItem(TokenKey)
}

export function getIdentity() {
  return sessionStorage.getItem(UserIdentity)
}

export function setIdentity(identity) {
  return sessionStorage.setItem(UserIdentity, identity)
}

export function removeIdentity() {
  sessionStorage.removeItem(UserIdentity)
}

export function getUserId() {
  return sessionStorage.getItem(UserId)
}

export function setUserId(userId) {
  return sessionStorage.setItem(UserId, userId)
}

export function removeUserId() {
  sessionStorage.removeItem(UserId)
}

export function setFirst(first) {
  sessionStorage.setItem(First,first)
}

export function getFirst(){
  return sessionStorage.getItem(First)
}
