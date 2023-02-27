import request from '@/utils/request'

const mainUrl = 'api/admin/attendance/v1'

const apiEndpoints = {
  createAttendanceUrl: `${mainUrl}`,
  getAllAttendanceByPageUrl: `${mainUrl}/list`,
  getCurrentUserTodayAttendanceInfoUrl: `${mainUrl}/currentuser/info`
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
          page: pager.page - 1,
          size: pager.size
        }
      })
    },
    /**
     * 查看今日当前用户是否考勤
     */
    async getCurrentUserTodayAttendanceInfo() {
      return await request.get(apiEndpoints.getCurrentUserTodayAttendanceInfoUrl).then(({ result }) => {
        return result || false
      })
    }
  }
}
