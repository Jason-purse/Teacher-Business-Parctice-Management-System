<template>
  <div>
    <div class="search-line">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="项目名称">
          <el-input v-model="searchForm.projectName" placeholder="请输入项目名称"></el-input>
        </el-form-item>
        <el-form-item label="报告名称">
          <el-input v-model="searchForm.reportName" placeholder="请输入项目名称"></el-input>
        </el-form-item>
        <el-form-item label="发起人">
          <el-select v-model="searchForm.requestUser" placeholder="请输入发起人">
            <el-option label="发起时间" value="shanghai"></el-option>
            <el-option label="区域二" value="beijing"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="审核阶段">
          <el-select v-model="searchForm.auditPhase" placeholder="请选择审核阶段">
            <el-option label="发起时间" value="shanghai"></el-option>
            <el-option label="区域二" value="beijing"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发起开始时间">
          <el-date-picker
            v-model="searchForm.startTimeAt"
            type="date"
            placeholder="选择日期" @change="validTimeRange('startTimeAt')">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="发起结束时间">
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
      <el-table-column label="项目名称" prop="projectName"/>
      <el-table-column label="报告名称" prop="reportName"/>
      <el-table-column label="发起人" prop="requestUser"/>
      <el-table-column label="审核阶段" prop="auditPhase"/>
      <el-table-column label="报告类型" prop="reportType"/>
      <el-table-column label="报告格式" prop="reportFormat"/>
      <el-table-column label="操作" width="250px" align="center">
        <el-button @click="commit('allow')" type="success">通过</el-button>
        <el-button @click="commit('reject')" type="danger">打回</el-button>
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
import backendStyle from '../../utils/generic-backend-style-util'
export default {
  name: "index",
  data() {
    return {
      searchForm: {
        projectName: '',
        reportName: "",
        requestUser: "",
        auditPhase: "",
        startTimeAt: '',
        endTimeAt: '',
        creatTimeAt: '',
      },
      tableData: [{
        projectName: '江浙小吃、小吃零食',
        reportName: '荷兰优质淡奶，奶香浓而不腻',
        requestUser: "张三",
        auditPhase:"领导审核",
        reportType: "申请报告",
        reportFormat: "pdf",
        createTimeStr: '上海市普陀区真北路'
      }],
      ...backendStyle.data()
    }},
  methods: {
    ... backendStyle.methods,
    commit(val) {
      if(val === 'allow') {
        this.$message("调用接口")
      }
      else if(val === 'reject') {
        this.$message("驳回")
      }
    }
  }
}
</script>

<style scoped>

</style>
