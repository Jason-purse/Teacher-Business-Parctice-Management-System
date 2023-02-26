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
        <el-form-item label="报告发起人">
          <el-input v-model="searchForm.requestUser" placeholder="请输入发起人名称"/>
        </el-form-item>
        <el-form-item label="审核阶段">
          <el-select v-model="searchForm.auditPhase" placeholder="请选择审核阶段">
            <el-option v-for="{itemValue,id} in auditPhase" :label="itemValue" :key="id" :value="id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="searchForm.startTimeAt"
            type="date"
            placeholder="选择日期" @change="validTimeRange('startTimeAt')">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="或结束时间">
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
      <el-table-column label="发起人" prop="submitUserName"/>
      <el-table-column label="审核阶段" prop="auditPhase">
        <template v-slot="{row:{auditPhase}}">
          {{mapDictItemValue('auditPhase',auditPhase)}}
        </template>
      </el-table-column>
      <el-table-column label="报告类型" prop="reportType">
        <template v-slot="{row:{reportType}}">
          {{mapDictItemValue('reportTypes',reportType)}}
        </template>
      </el-table-column>
      <el-table-column label="报告格式" prop="reportFormat">
        <template v-slot="{row:{reportFormat}}">
          {{mapDictItemValue('reportFormat',reportFormat)}}
        </template>
      </el-table-column>
      <el-table-column label="文件预览">
        <template v-slot="{row}">
          点击文件预览
        </template>
      </el-table-column>
      <el-table-column label="提交时间" prop="createTimeStr" />
      <el-table-column label="状态" align="center">
        <template v-slot="{row: {status}}">
          {{mapDictItemValue('auditStatus',status)}}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250px" align="center">
        <template v-slot="{row}">
          <template v-if="row.failureFlag === null">
            <el-button @click="commit('allow',row)" type="success">通过</el-button>
            <el-button @click="commit('reject',row)" type="danger">打回</el-button>
          </template>
          <template v-else>
            已审核
          </template>
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

    <el-dialog title="审核确认" :visible.sync="audit.visible" width="80%" @close="auditDialogCancel()">
      <div>
        <div class="search-line">
          <el-form  ref="auditForm" :model="audit.form" class="search-form">
            <el-form-item label="通过原因" v-if="audit.flag">
              <el-input :autosize="{minRows: 3,maxRows: 10}" maxlength="500" show-word-limit  v-model="audit.form.successDescription" type="textarea" placeholder="请输入通过原因(可选)"></el-input>
            </el-form-item>
            <el-form-item label="打回原因" v-else>
              <el-input :autosize="{minRows: 3,maxRows: 10}" maxlength="500" show-word-limit  v-model="audit.form.failureReason" type="textarea" placeholder="请输入打回原因(可选)"></el-input>
            </el-form-item>
          </el-form>
        </div>
        <div slot="footer" class="dialog-footer" :style="{textAlign: 'right'}">
          <el-button :type="audit.flag ? 'success': 'danger'" @click="updateAuditAction">{{audit.flag ? '打回' : '通过'}}</el-button>
        </div>
      </div>
    </el-dialog>
  </div>

</template>

<script>
import backendStyle from '../../utils/generic-backend-style-util'
import auditApi from "@/api/audit";
import dictApi from "@/api/dict"

export default {
  name: "index",
  data() {
    return {
      ...dictApi.data(),
      searchForm: {
        projectName: '',
        reportName: "",
        requestUser: "",
        auditPhase: "",
        startTimeAt: '',
        endTimeAt: '',
        creatTimeAt: '',
      },
      tableData: [],
      audit: {
        flag: false,
        visible: false,
        row: null, // 当前行
       form: {
         successDescription: '',
         failureReason: '',
         failureFlag: false, // 默认成功
       }
      },
      ...backendStyle.data()
    }
  },

  created() {
    this.getAuditPhase();
    this.getReportTypes()
    this.getReportFormat()
    this.getAuditStatus();
    this.onSubmit()
  },

  methods: {
    ...backendStyle.methods,
    ...auditApi.methods,
    ...dictApi.methods,
    commit(val, row) {
      this.audit.row = row;
      if (val === 'allow') {
        this.audit.flag = true;
        this.audit.form.failureFlag = false;
      } else if (val === 'reject') {
        this.audit.flag = false;
        // 失败
        this.audit.form.failureFlag = true;
      }
      this.audit.visible = true;
    },
    updateAuditAction() {
      let value = {...this.audit.row,reportId: this.audit.row.id,... this.audit.form}
      this.auditUpdate(value).then(() => {
        this.$message.success("审核完成 !!!")
        this.audit = {}
        // 提交即可 ...
        this.onSubmit()
      })
    },

    getDataFunc() {
      return this.getAllReportsForAudit(this.searchForm, this.pager).then(({result}) => {
        this.tableData = result.content.map((ele, index) => {
          ele.index = index;
          return ele
        })
        return result
      })
    },
    auditDialogCancel() {

    }
  }
}
</script>

<style scoped>

</style>
