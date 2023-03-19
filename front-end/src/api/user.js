import request from '@/utils/request'

const mainUrl = '/api/admin/user/v1'
const loginMainUrl = '/api/login/v1'

const apiEndpoints = {
  getAllUsersByPageUrl: `${mainUrl}/list`,
  deleteUserByIdUrl: `${mainUrl}/delete`,
  updateUserUrl: `${mainUrl}/current/userinfo`,
  getCurrentUserInfoUrl: `${mainUrl}/current/userinfo`
}
const loginEndpoints = {
  registerUrl: `${loginMainUrl}/register`
}

export function login(data) {
  return request({
    url: '/login',
    method: 'post',
    params: data
  })
}

export function getInfo(token) {
  return request({
    url: '/vue-admin-template/user/info',
    method: 'get',
    params: { token }
  })
}

export function logout() {
  // return request({
  //   url: '/user/logout',
  //   method: 'post'
  // })
  return Promise.resolve()
}

export default {
  methods: {
    getAllUsersByPage(param, pager) {
      return request.get(apiEndpoints.getAllUsersByPageUrl, {
        params: {
          ...param,
          page: pager.page - 1,
          size: pager.size
        }
      })
        .then(data => {
          pager.total = data.result.totalElements || 0
          return data
        })
    },
    deleteUserById(id) {
      return request.delete(apiEndpoints.deleteUserByIdUrl.concat(`/${id}`))
    },
    registerUser(param) {
      return request.post(loginEndpoints.registerUrl, param)
    },
    updateUser(param) {
      return request.put(apiEndpoints.updateUserUrl, param)
    },

    /**
     * 获取当前用户信息
     */
    getCurrentUserInfo() {
      return request.get(apiEndpoints.getCurrentUserInfoUrl)
    },
    isAdmin() {
      return this.userInfo().roles?.filter(ele => ele.itemType === 'admin').length > 0
    }
  }
}
