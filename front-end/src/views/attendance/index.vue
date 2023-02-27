<template>
  <!--附件管理-->
  <div>
    <div class="search-line">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名称">
          <el-input v-model="searchForm.filename" placeholder="请输入用户名称" clearable />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="searchForm.startTimeAt"
            type="date"
            placeholder="选择日期"
            @change="validTimeRange('startTimeAt')" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="searchForm.endTimeAt"
            type="date"
            @change="validTimeRange('endTimeAt')"
            placeholder="选择日期" />
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
      <el-table-column label="用户名称" prop="username"/>
      <el-table-column label="考勤状态">
        已考勤
      </el-table-column>
      <el-table-column label="考勤时间" prop="createTimeStr"/>
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
import attendanceApi from "@/api/attendance";

export default {
  name: "index",
  data() {
    return {
      tableData: [],
      searchForm: {
        filename: "",
        fileType: ""
      },
      ...backendStyle.data()
    }
  },
  created() {
    this.onSubmit();
  },
  methods: {
    ...backendStyle.methods,
    ...attendanceApi.methods,
    deleteAttachment() {
      this.$throttle(() => {
        this.$message.warning("删除动作触发!!")
      })
    },
    getDataFunc() {
      return this.getAllAttendanceByPage(this.getSearchform(), this.pager).then(({result}) => {
        this.tableData = result.content.map((ele, index) => {
          ele.index = index;
          return ele;
        });
        return result;
      })
    }
  }
}
</script>

<style scoped>

</style>
