import request from "@/utils/request";

const mainReportUrl = '/api/admin/report/v1'
const apiEndpoints = {
  getAllReportsUrl: `${mainReportUrl}/list/`
}

export default {
  loadReports(projectId) {
    return request.get(apiEndpoints.getAllReportsUrl.concat(projectId))
  }
}
