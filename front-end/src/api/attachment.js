const mainUrl = '/api/admin/attachment/v1'

const apiEndpoints = {
  uploadUrl: `${mainUrl}/upload`
}

export default  {
  data() {
    return {
      uploadActionUrl: apiEndpoints.uploadUrl
    }
  }
}
