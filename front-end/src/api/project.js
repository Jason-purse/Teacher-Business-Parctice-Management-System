import request from '@/utils/request'

const mainUrl = '/api/admin/project/v1'
const apiEndpoints = {

  getAllProjectsByPageUrl: `${mainUrl}/list`,
  createProjectUrl: `${mainUrl}`,
  updateProjectUrl: `${mainUrl}`,
  deleteProjectByIdUrl: `${mainUrl}/`,
  getProjectByIdUrl: `${mainUrl}`

}

export default {
  getAllProjectsByPage(param, pager) {
    return request.get(apiEndpoints.getAllProjectsByPageUrl, {
      params: {
        ...param,
        page: pager.page - 1,
        size: pager.size
      }
    })
  },

  createProject(param) {
    return request.post(apiEndpoints.createProjectUrl, param)
  },

  updateProject(param) {
    return request.put(apiEndpoints.updateProjectUrl, param)
  },

  deleteProjectById(id, params) {
    return request.delete(apiEndpoints.deleteProjectByIdUrl.concat(id), { params })
  },

  getProjectById(id) {
    return request.get(apiEndpoints.getProjectByIdUrl.concat(`/${id}`))
  }
}
