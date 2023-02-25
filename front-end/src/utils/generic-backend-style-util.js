/**
 * 其中包含了类似后端的方式风格 ..
 * 包含了查询表单的校验 !!!
 *
 * 以及表格分页处理 ...
 */

const validTimeRange = function (identity) {
  let searchForm = this.searchForm;
  if (searchForm.startTimeAt && searchForm.endTimeAt) {
    if (!(searchForm.startTimeAt < searchForm.endTimeAt)) {
      this.$message.warning("结束时间必须大于开始时间 !!!")
      searchForm[identity] = '';
    }
  }
}

export default {
  methods: {
    validTimeRange,
    getSearchform() {
      return {
        ...this.searchForm,
        startTimeAt: this.searchForm.startTimeAt?.getTime(),
        endTimeAt: this.searchForm.endTimeAt?.getTime()
      };
    },
    onSubmit() {
      let searchForm = this.searchForm;
      this.getDataFunc(searchForm).then(response => {
      }, error => {
        this.$message.warning(error?.message || "系统错误 !!!`")
      })
    },
    onReset() {
      this.$refs['searchForm'].resetFields()
      console.log(this.$refs)
    }
  },
  data() {
    return {
      pager: {
        page: 0,
        size: 10,
        total: 0
      }
    }
  }
}
