import request from '@/utils/request'

const mainApiUrl = '/api/admin/role/v1'
const apiEndpoints = {
  updateRoleForUserUrl: `${mainApiUrl}/rru/update`
}

export default {
  methods: {
    updateRoleForUser(param) {
      return request.post(apiEndpoints.updateRoleForUserUrl, param)
    }
  }
}
