<template>
  <!--附件管理-->
  <div>
    <div class="search-line">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="附件名称">
          <el-input v-model="searchForm.filename" placeholder="请输入附件名称"></el-input>
        </el-form-item>
        <el-form-item label="文件类型">
          <el-select v-model="searchForm.fileType" placeholder="请选择文件类型">
            <el-option label="发起时间" value="shanghai"></el-option>
            <el-option label="区域二" value="beijing"></el-option>
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
      style="width: 100%">
      <el-table-column label="名称" prop="filename"/>
      <el-table-column label="类型" prop="fileType"/>
      <el-table-column label="创建时间" prop="createTimeStr"/>
      <el-table-column label="唯一标识" prop="identify"/>
      <el-table-column label="操作" align="center">
        <template>
          <el-button type="danger" @click="deleteAttachment">删除</el-button>
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
        :total="pager.total">
      </el-pagination>
    </div>
  </div>

</template>

<script>
import {throttle} from "@/utils/throttle";
import backendStyle from '../../utils/generic-backend-style-util'
export default {
  name: "index",
  data() {
    return {
      tableData: [
        {
          filename: "12312",
          fileType: "pdf",
          createTimeStr: "2022-12-23 23:23:23",
          identify: "申请报告"
        }
      ],
      searchForm: {
        filename: "",
        fileType: ""
      },
     ...backendStyle.data()
    }
  },
  methods: {
    ...backendStyle.methods,
    deleteAttachment() {
      this.$throttle(() => {
         this.$message.warning("删除动作触发!!")
      })
    }
  }
}
</script>

<style scoped>

</style>
