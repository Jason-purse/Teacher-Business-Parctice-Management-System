import request from '@/utils/request'

const mainUrl = '/api/admin/dict/v1'

const apiEndpoints = {
  getAllDictByPageUrl: `${mainUrl}/list/page`,
  getDataItemsByParentTypeUrl: `${mainUrl}/by/datatype`
}

const ItemTypes = {
  reportType: 'report_type',
  reportFormat: 'report_format',
  auditPhase: 'audit_phase',
  auditStatus: 'report_status',
  projectStatus: 'project_status',
  genderStatus: 'gender',
  mediaType: 'media_format',
  roles: 'role'
}

function getDataItemsByParentType(parentType) {
  return request.get(apiEndpoints.getDataItemsByParentTypeUrl.concat(`/${parentType}`))
}

export default {
  data() {
    return {
      reportTypes: [],
      reportFormat: [],
      auditStatus: [],
      auditPhase: [],
      projectStatus: [],
      genderStatus: [],
      mediaType: [],
      roles: []
    }
  },
  methods: {
    getAllDictByPage(param, pager) {
      return request.get(apiEndpoints.getAllDictByPageUrl, {
        params: {
          ...param,
          page: pager.page - 1,
          size: pager.size
        }
      })
    },
    getReportTypes() {
      return getDataItemsByParentType(ItemTypes.reportType)
        .then(({ result }) => {
          this.reportTypes = result || []
        })
    },
    getReportFormat() {
      return getDataItemsByParentType(ItemTypes.reportFormat).then(({ result }) => {
        this.reportFormat = result || []
      })
    },
    getAuditStatus() {
      return getDataItemsByParentType(ItemTypes.auditStatus).then(({ result }) => {
        this.auditStatus = result || []
      })
    },
    getAuditPhase() {
      return getDataItemsByParentType(ItemTypes.auditPhase).then(({ result }) => {
        this.auditPhase = result
      })
    },
    getProjectStatus() {
      return getDataItemsByParentType(ItemTypes.projectStatus).then(({ result }) => {
        this.projectStatus = result
      })
    },

    getGenderStatus() {
      return getDataItemsByParentType(ItemTypes.genderStatus).then(({ result }) => {
        this.genderStatus = result
      })
    },
    getMediaTypes() {
      return getDataItemsByParentType(ItemTypes.mediaType).then(({ result }) => {
        this.mediaType = result
      })
    },

    getRoles() {
      return getDataItemsByParentType(ItemTypes.roles).then(({ result }) => {
        this.roles = result
      })
    },
    mapDictItemValue(name, ele) {
      const data = this[name] || []
      if (data) {
        const value = data.filter(({ id, itemValue }) => id == ele)
        if (value.length > 0) {
          return value[0].itemValue
        }
      }
      return '----'
    },
    mapDictItemType(name, ele) {
      const data = this[name] || []
      if (data) {
        const value = data.filter(({ id, itemValue }) => id == ele)
        if (value.length > 0) {
          return value[0].itemType
        }
      }
      return '----'
    }
  }
}
