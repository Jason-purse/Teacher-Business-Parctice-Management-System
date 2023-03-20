<template>
  <!--附件管理-->
  <div class="view-container">
    <div class="search-line">
      <el-form :inline="true" :model="searchForm" class="search-form" @keydown.enter.native="onSubmit">
        <el-form-item label="附件名称" prop="fileName">
          <el-input v-model="searchForm.fileName" placeholder="请输入附件名称" />
        </el-form-item>
        <el-form-item label="文件类型" prop="mediaType">
          <el-select v-model="searchForm.mediaType" placeholder="请选择文件类型" clearable>
            <el-option v-for="({itemValue,id}) in mediaType" :key="id" :label="itemValue" :value="id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onSubmit">查询</el-button>
          </el-form-item>
        </el-form-item>
      </el-form>
    </div>
    <div class="content">
      <el-table
        :data="tableData"
        class="table-content"
        row-key="id"
      >
        <el-table-column type="selection" align="center" />
        <el-table-column type="index" label="序号" align="center" />
        <el-table-column label="名称" prop="fileName" align="center" />
        <el-table-column label="类型" prop="mediaType" align="center" />
        <el-table-column label="创建时间" prop="createTimeStr" align="center" />
        <el-table-column label="所属项目" prop="projectName" align="center" />
        <el-table-column label="所属报告" prop="reportName" align="center" />
        <el-table-column label="文件类型" prop="identifier" align="center">
          <template v-slot="{row: {mediaType}}">
            {{ mapDictItemValue('mediaType', mediaType) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center">
          <template v-slot="{row}">
            <el-button
              :disabled="(row.reportRRCount || 0) > 0 || false"
              type="danger"
              size="small"
              @click="deleteAttachmentAction(row)"
            >
              删除
            </el-button>
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
          @current-change="onSubmit"
        />
      </div>
    </div>
  </div>

</template>

<script>
import backendStyle from '../../utils/generic-backend-style-util'
import attachment from '@/api/attachment'
import dict from '@/api/dict'

export default {
  name: 'Index',
  data() {
    return {
      tableData: [],
      searchForm: {
        fileName: '',
        mediaType: ''
      },
      ...backendStyle.data(),
      ...dict.data()
    }
  },
  created() {
    this.onSubmit()
    this.getMediaTypes()
  },
  methods: {
    ...backendStyle.methods,
    ...attachment.methods,
    ...dict.methods,
    deleteAttachmentAction(row) {
      const { id } = row
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
        return result
      })
    }
  }
}
</script>

<style scoped lang="scss">

.view-container {
  height: 100%;
  width: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  .content {
    flex: 1;
    max-height: 100%;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .el-table::before {
      height: 0;
    }

    .table-content {
      overflow-y: auto;
      max-height: 100%;
      flex: 1;
    }
  }
}
</style>
