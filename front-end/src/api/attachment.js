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
          page: pager.page,
          size: pager.size
        }
      })
    },
    deleteAttachmentById(id) {
      return request.delete(apiEndpoints.deleteAttachmentById.concat(`/${id}`))
    }
  }
}
