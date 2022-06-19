const state = {
  taskId: 0,
  form: undefined,
  update: undefined
}

const mutations = {
  SET_TASKID: (state, taskId) => {
    state.taskId = taskId
  },
  SET_UPDATE: (state, update) => {
    state.update = update
  },
  SET_FORM: (state, form) => {
    state.form = form
  }
}
export default {
  namespaced: true,
  state,
  mutations
}
