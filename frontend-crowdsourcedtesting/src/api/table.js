import request from '@/utils/request'
import { getIdentity } from '@/utils/auth'

export function getAllTask(params) {
  const identity = getIdentity()

  const url = identity === '众包工人' ? '/collect/task/recommendTask' : '/collect/task/allTask'

  return request({
    url: url,
    method: 'get',
    params
  })
}

export function checkIfShown(userId, taskId) {
  return request({
    url: '/collect/task/ifShown',
    method: 'get',
    params: {
      userId: userId,
      taskId: taskId
    }
  })
}

export function partTask(userId, taskId) {
  console.log(userId)
  return request({
    url: '/collect/work/part',
    method: 'post',
    data: {
      userId: userId,
      taskId: taskId
    }
  })
}

export function getMyTask(userId, workstatus) {
  return request({
    url: '/collect/task/searchFinishedTask',
    method: 'get',
    params: {
      userId: userId,
      workStatus: workstatus
    }
  })
}

export function findTaskByTaskId(userId, taskId) {
  return request({
    url: '/collect/task/info',
    method: 'get',
    params: {
      userId: userId,
      taskId: taskId
    }
  })
}

export function selectTaskByLabel(tag, if_finished, name) {

  const identity = getIdentity()
  const url = name !== '' ? '/collect/task/selectTaskByLabel' : (identity === '众包工人' ? '/collect/task/recommendTaskByLabel' : '/collect/task/selectTaskByLabel')

  return request({
    url: url,
    method: 'get',
    params: {
      tag: tag,
      if_finished: if_finished,
      name: name
    }
  })
}

export function searchFinishedTask(userId, workStatus) {
  return request({
    url: '/collect/task/searchFinishedTask',
    method: 'get',
    params: {
      userId: userId,
      workStatus: workStatus
    }
  })
}

export function checkWhetherPartTheTask(userId, taskId) {
  return request({
    url: '/collect/task/ifPart',
    method: 'get',
    params: {
      userId: userId,
      taskId: taskId
    }
  })
}

export function searchPublishedTask(userId) {
  return request({
    url: '/collect/task/searchPublishedTask',
    method: 'get',
    params: {
      userId: userId
    }
  })
}

