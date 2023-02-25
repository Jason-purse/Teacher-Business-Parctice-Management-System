import request from "@/utils/request";

const mainUrl = "/api/admin/dict/v1"

const apiEndpoints = {
  getAllDictByPageUrl: `${mainUrl}/list/page`
}

export default {
  data: {

  },
  methods: {
    getAllDictByPage(param,pager) {
      return request.get(apiEndpoints.getAllDictByPageUrl,{
        params: {
          ...param,
          pager
        }
      })
    }
  }
}
