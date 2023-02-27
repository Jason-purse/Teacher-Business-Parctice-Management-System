import request from '@/utils/request'

const mainUrl = '/api/admin/attachment/v1'

const apiEndpoints = {
  uploadUrl: `${mainUrl}/upload`,
  getAllAttendanceByPageUrl: `${mainUrl}/list`,
  deleteAttachmentById: `${mainUrl}`
}

export default {
  data() {
    return {
      uploadActionUrl: apiEndpoints.uploadUrl
    }
  },
  methods: {
    getAllAttachmentByPage(param, pager) {
      return request.get(apiEndpoints.getAllAttendanceByPageUrl, {
        params: {
          ...param,
          pager
        }
      })
    },
    deleteAttachmentById(id) {
      return request.get(apiEndpoints.deleteAttachmentById.concat(`/${id}`))
    }
  }
}
