
const state = {
  taskId: 0
}

const mutations = {
  SET_TASKID: (state, taskId) => {
    state.taskId = taskId
  }
}
export default {
  namespaced: true,
  state,
  mutations
}
