import { getToken, setIdentity, setUserId, setFirst} from '@/utils/auth'

const getDefaultState = () => {
  return {
    token: getToken(),
    name: '',
    avatar: '',
    id: 0,
    identity: '',
    first: false
  }
}

const state = getDefaultState()

const mutations = {
  RESET_STATE: (state) => {
    Object.assign(state, getDefaultState())
  },
  SET_ID: (state, id) => {
    state.id = id
    setUserId(id)
  },
  SET_IDENTITY: (state, identity) => {
    state.identity = identity
    setIdentity(identity)
  },
  SET_FIRST:(state,first)=>{
    state.first=first
    setFirst(first)
  }
}

export default {
  namespaced: true,
  state,
  mutations
}

