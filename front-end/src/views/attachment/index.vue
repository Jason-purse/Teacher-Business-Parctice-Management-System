<template>
  <!--附件管理-->
  <div>
    <div class="search-line">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="附件名称">
          <el-input v-model="searchForm.filename" placeholder="请输入附件名称" />
        </el-form-item>
        <el-form-item label="文件类型">
          <el-select v-model="searchForm.fileType" placeholder="请选择文件类型">
            <el-option label="发起时间" value="shanghai" />
            <el-option label="区域二" value="beijing" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onSubmit">查询</el-button>
          </el-form-item>
        </el-form-item>
      </el-form>
    </div>
    <el-table
      :data="tableData"
      style="width: 100%"
    >
      <el-table-column type="selection" align="center" />
      <el-table-column type="index" label="序号" align="center" />
      <el-table-column label="名称" prop="fileName" align="center" />
      <el-table-column label="类型" prop="mediaType" align="center" />
      <el-table-column label="创建时间" prop="createTimeStr" align="center" />
      <el-table-column label="唯一标识" prop="identifier" align="center" />
      <el-table-column label="操作" align="center">
        <template v-slot="{row}">
          <el-button type="danger" size="small" @click="deleteAttachmentAction(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="margin-top: 5px;text-align: right;">
      <el-pagination
        small
        background
        :current-page.sync="pager.page"
        :page-size="pager.size"
        layout="prev, pager, next"
        :total="pager.total"
      />
    </div>
  </div>

</template>

<script>
import backendStyle from '../../utils/generic-backend-style-util'
import attachment from '@/api/attachment'
export default {
  name: 'Index',
  data() {
    return {
      tableData: [],
      searchForm: {
        filename: '',
        fileType: ''
      },
      ...backendStyle.data()
    }
  },
  created() {
    this.onSubmit()
  },
  methods: {
    ...backendStyle.methods,
    ...attachment.methods,
    deleteAttachmentAction({ id }) {
      this.$confirm(`确定删除此附件${name}?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this
          .deleteAttachmentById(id)
          .then(() => {
            this.$message({
              type: 'success',
              message: '删除成功!'
            })
            this.onSubmit()
          })
      }).catch(() => {
      })
    },
    getDataFunc() {
      return this.getAllAttachmentByPage(this.getSearchform(), this.pager).then(({ result }) => {
        this.tableData = result.content.map((ele, index) => {
          ele.index = index
          return ele
        })
      })
    }
  }
}
</script>

<style scoped>

</style>
