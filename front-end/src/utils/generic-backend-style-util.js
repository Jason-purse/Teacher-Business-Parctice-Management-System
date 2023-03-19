/**
 * 其中包含了类似后端的方式风格 ..
 * 包含了查询表单的校验 !!!
 *
 * 以及表格分页处理 ...
 */

const validTimeRange = function (identity, form = 'searchForm') {
  let searchForm = this[form]
  if (typeof form === 'object') {
    searchForm = form
  }
  if (form != null) {
    if (searchForm.startTimeAt && searchForm.endTimeAt) {
      if (!(searchForm.startTimeAt < searchForm.endTimeAt)) {
        this.$message.warning('结束时间必须大于开始时间 !!!')
        searchForm[identity] = ''
      }
    }
  }
}

export default {
  methods: {
    validTimeRange,
    getSearchform(form = this.searchForm) {
      return {
        ...form,
        startTimeAt: this.searchForm.startTimeAt?.getTime(),
        endTimeAt: this.searchForm.endTimeAt?.getTime()
      }
    },
    onSubmit(form = 'searchForm') {
      let searchForm = this[form]
      if (typeof form === 'object') {
        searchForm = form
      }
      this.getDataFunc(this.getSearchform(searchForm))
        .then((result) => {
          // 第一页不需要额外处理 ...
          if (this.pager.page > 1 && result && result.totalPages && result.totalPages <= 1) {
            this.pager.page = 1
            this.onSubmit()
          }
          this.pager.total = result?.totalElements || 0
        })
        .catch(error => {
          this.$message.warning(error?.message || '系统错误 !!!`')
        })
    },
    onReset() {
      this.$refs['searchForm'].resetFields()
      console.log(this.$refs)
    },
    onCancel(form) {
      this[form].visible = false
      this.$refs[form]?.resetFields()
    },
    entryKeyDown(event) {
      // 判断是否为回车键
      this.onSubmit()
    },
    datepickerInputFocus(name) {
      console.log('失去焦点')
      this.$refs[name].focus()
    }
  },
  data() {
    return {
      pager: {
        page: 1,
        size: 10,
        total: 0
      }
    }
  }
}
