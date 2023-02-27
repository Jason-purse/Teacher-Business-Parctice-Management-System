import request from "@/utils/request";

const mainUrl = "/api/admin/audit/v1"

const apiEndpoints = {
  createAuditUrl: `${mainUrl}`,
  allReportsForAuditUrl: `${mainUrl}`,
  updateAuditUrl: `${mainUrl}`
}

export default {
  methods: {
    createAudit(param) {
      return request.post(apiEndpoints.createAuditUrl, param)
    },
    getAllReportsForAudit(params, pager) {
      return request.get(apiEndpoints.allReportsForAuditUrl, {
        params: {
          ...params,
          page: pager.page,
          size: pager.size
        }
      })
    },
    auditUpdate(param) {
      return request.put(apiEndpoints.updateAuditUrl,param)
    }
  }
}
