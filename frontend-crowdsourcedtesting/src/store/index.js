import Vue from 'vue'
import Vuex from 'vuex'
import getters from './getters'
import app from './modules/app'
import settings from './modules/settings'
import user from './modules/user'
import task from './modules/task'
import report from './modules/report'

Vue.use(Vuex)

const store = new Vuex.Store({
  state: {
    userId: null,
    userIdentity: null,
    isLogin: false,// 登录状态
    // token: '' // 用户登录成功后持有的token
  },
  modules: {
    app,
    settings,
    user,
    task,
    report
  },
  getters
})

export default store
