import request from "@/utils/request";

const mainUrl = 'api/admin/attendance/v1'

const apiEndpoints = {
  createAttendanceUrl: `${mainUrl}`,
  getAllAttendanceByPageUrl: `${mainUrl}/list`
}

export default {
  methods: {
    createAttendance(params) {
      return request.post(apiEndpoints.createAttendanceUrl, params)
    },
    getAllAttendanceByPage(params, pager) {
      return request.get(apiEndpoints.getAllAttendanceByPageUrl, {
        params: {
          ...params,
          pager
        }
      })

    }
  }
}
