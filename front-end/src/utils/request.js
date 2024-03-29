import axios from 'axios'
import {MessageBox, Message} from 'element-ui'
import store from '@/store'
import {getAccessToken} from '@/utils/auth'

// create an axios instance
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 5000, // request timeout

  withCredentials: true
})

// request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent
    if (store.getters.accessToken) {
      // let each request carry token
      // ['X-Token'] is a custom headers key
      // please modify it according to the actual situation
      config.headers['Authorization'] = getAccessToken()
    }

    return config
  },
  error => {
    // do something with request error
    // console.log(error) // for debug
    return Promise.reject(error)
  }
)
/**
 * 多个错误只提醒一个 ..
 */
let messageDialog = null;
let reLoginPrompt = null;

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
    if (response.status === 200) {
      const res = response.data
      // console.log(res)
      // if the custom code is not 20000, it is judged as an error.
      if (res.code) {
        if (res.code !== 200) {
          if (messageDialog) {
            messageDialog.close()
          }


          messageDialog = Message({
            message: res.message || 'Error',
            type: 'error',
            duration: 5 * 1000
          })

          // 50008: Illegal token; 50012: Other clients logged in; 50014: Token expired;
          if (res.code === 401) {
            if (!reLoginPrompt) {
              // to re-login
              reLoginPrompt = MessageBox.confirm('You have been logged out, you can cancel to stay on this page, or log in again', 'Confirm logout', {
                confirmButtonText: 'Re-Login',
                cancelButtonText: 'Cancel',
                type: 'warning'
              }).then(() => {
                store.dispatch('user/resetToken').then(() => {
                  location.reload()
                })
              })
            }
          }

          return Promise.reject(new Error(res.message || 'Error'))
        } else {
          return res
        }
      }
      return response
    }
    return Promise.reject(new Error(response.message || 'Error'))
  },
  error => {
    if (messageDialog) {
      messageDialog.close()
    }
    console.log('err' + error) // for debug
    messageDialog = Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
