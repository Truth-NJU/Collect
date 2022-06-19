
const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  name: state => state.user.name,
  userIdentity: state => state.user.userIdentity,
  userId: state => state.user.userId,
  first: state => state.user.first,
  report: state => {
    return {
      taskId: state.report.taskId,
      update: state.report.update
    }
  },
  task: state => {
    return {
      taskId: state.task.taskId,
      update: state.task.update === undefined ? sessionStorage.getItem('_update') === 'true' : state.task.update,
      taskForm: state.task.form === undefined ? JSON.parse(sessionStorage.getItem('_taskform')) : state.task.form
    }
  }
}

export default getters
