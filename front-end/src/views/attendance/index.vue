<template>
  <!--附件管理-->
  <div>
    <div class="search-line">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名称">
          <el-input v-model="searchForm.filename" placeholder="请输入用户名称"></el-input>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="searchForm.startTimeAt"
            type="date"
            placeholder="选择日期" @change="validTimeRange('startTimeAt')">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="searchForm.endTimeAt"
            type="date"
            @change="validTimeRange('endTimeAt')"
            placeholder="选择日期">
          </el-date-picker>
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
      <el-table-column label="唯一标识" prop="identify" align="center"/>
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
import backendStyle from '@/utils/generic-backend-style-util'

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
    ... backendStyle.methods,
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
