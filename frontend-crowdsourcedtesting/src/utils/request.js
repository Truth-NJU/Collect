import axios from 'axios'
import { Message } from 'element-ui'
import { getToken, setToken } from '@/utils/auth'

export const baseUrl = 'http://124.222.139.8:8082'
// export const baseUrl = 'http://localhost:8080'

export const imageUrl = baseUrl + '/image/'

// create an axios instance
const service = axios.create({
  baseURL: baseUrl, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 5000000 // request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent
    if (getToken() !== undefined) {
      // let each request carry token
      // ['X-Token'] is a custom headers key
      // please modify it according to the actual situation
      config.headers['Authorization'] = 'Bearer ' + getToken()
    }
    return config
  },
  error => {
    // do something with request error
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  /**
   * If you want to get http information such as headers or status
   * Please return  response => response
   */

  /**
   * Determine the request status by custom code
   * Here is just an example
   * You can also judge the status by HTTP Status Code
   */
  response => {
    if (response.headers['token'] !== undefined) { setToken(response.headers['token']) }

    const res = response.data
    // if the custom code is not 4000, it is judged as an error.
    if (res.code !== 4000) {
      Message({
        message: res.message || res.msg,
        type: 'error',
        duration: 2000
      })

      // // 50008: Illegal token; 50012: Other clients logged in; 50014: Token expired;
      // if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
      //   // to re-login
      //   MessageBox.confirm('You have been logged out, you can cancel to stay on this page, or log in again', 'Confirm logout', {
      //     confirmButtonText: 'Re-Login',
      //     cancelButtonText: 'Cancel',
      //     type: 'warning'
      //   }).then(() => {
      //     store.dispatch('user/resetToken').then(() => {
      //       location.reload()
      //     })
      //   })
      // }
      // 50008: Illegal token; 50012: Other clients logged in; 50014: Token expired;
      return Promise.reject()
    } else {
      return res
    }
  }
  ,

  error => {
    // router.push({ path: '/404' })
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
  }
)

export default service
