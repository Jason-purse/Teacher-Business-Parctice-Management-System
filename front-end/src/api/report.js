import request from '@/utils/request'

const mainReportUrl = '/api/admin/report/v1'
const apiEndpoints = {
  getAllReportsUrl: `${mainReportUrl}/list/`,
  createReportUrl: `${mainReportUrl}`,
  updateReportUrl: `${mainReportUrl}`,
  deleteReportUrl: `${mainReportUrl}`
}

export default {
  methods: {
    loadReports(projectId) {
      return request.get(apiEndpoints.getAllReportsUrl.concat(projectId))
    },
    createReport(param) {
      return request.post(apiEndpoints.createReportUrl, param)
    },
    updateReport(param) {
      return request.put(apiEndpoints.updateReportUrl, param)
    },
    deleteReport(id) {
      return request.delete(apiEndpoints.deleteReportUrl.concat(`/${id}`))
    }
  }
}
