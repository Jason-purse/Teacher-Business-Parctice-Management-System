<template>
  <div>
    <div class="search-line">
      <el-form ref="searchForm" :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="searchForm.name" placeholder="请输入项目名称"></el-input>
        </el-form-item>
        <el-form-item label="发起人" prop="username">
          <el-select v-model="searchForm.username" placeholder="请输入发起人">
            <el-option label="发起时间" value="shanghai"></el-option>
            <el-option label="区域二" value="beijing"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发起开始时间" prop="startTimeAt">
          <el-date-picker
            v-model="searchForm.startTimeAt"
            type="date"
            placeholder="选择日期" @change="validTimeRange('startTimeAt')">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="发起结束时间" prop="endTimeAt">
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
          <el-form-item>
            <el-button type="danger" @click="onReset">重置</el-button>
          </el-form-item>
        </el-form-item>
      </el-form>
    </div>
    <el-divider class="margin-top-bottom-10"></el-divider>
    <template>
      <div class="margin-top-bottom-10 margin-left-25">
        <el-button type="success" @click="openProjectDrawer(false)">创建项目</el-button>
      </div>

      <el-table
        @expand-change="loadReportsTrigger"
        :data="tableData"
        style="width: 100%">
        <el-table-column type="expand">
          <template v-slot="{reportList}">
            <div class="margin-top-bottom-10 margin-left-25">
              <el-button type="primary" size="small" @click="openDialogCommitReport">提交报告</el-button>
            </div>
            <el-table :data="reportList">
              <el-table-column label="报告列表">
                <el-table-column label="名称" prop="name"/>
                <el-table-column label="描述" prop="description"/>
                <el-table-column label="发起人" prop="username"/>
                <el-table-column label="报告类型" prop="reportType"/>
                <el-table-column label="报告格式" prop="reportFormat"/>
                <el-table-column label="发起时间" prop="createTimeStr"/>
                <el-table-column label="审核人" prop="auditPeople"/>
                <el-table-column label="审核阶段" prop="auditPhase"/>
                <el-table-column label="审核状态" prop="auditStatus"/>
                <el-table-column label="最终状态" prop="status"/>
              </el-table-column>
            </el-table>
          </template>
        </el-table-column>
        <el-table-column label="名称" prop="name"/>
        <el-table-column label="描述" prop="description"/>
        <el-table-column label="发起人" prop="username"/>
        <el-table-column label="发起时间" prop="createTimeStr"/>
        <el-table-column label="状态" prop="status"/>
        <el-table-column label="操作" align="center">
          <template v-slot="props">
            <el-button type="danger" size="small" @click="deleteDialogHandle(props)">删除</el-button>
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
    </template>

    <el-drawer
      :title="drawerAction ? '项目更新': '新建项目'"
      :visible.sync="drawerFlag"
      size="300px"
      direction="rtl">
      <el-form size="small" :model="drawerDialogData" class="search-form" style="padding: 5px">
        <el-form-item label="项目名称">
          <el-input v-model="drawerDialogData.name" placeholder="请输入项目名称"></el-input>
        </el-form-item>
        <el-form-item label="项目描述">
          <el-input v-model="drawerDialogData.description" placeholder="请输入项目描述"></el-input>
        </el-form-item>
        <el-form-item>
          <el-form-item style="margin-top: 20px">
            <el-button type="primary" style="width: 100%" @click="saveProject()">提交</el-button>
          </el-form-item>
        </el-form-item>
      </el-form>
    </el-drawer>

    <el-dialog title="提交报告" :visible.sync="reportForm.visible">
      <el-form ref="reportForm" :model="reportForm.data">
        <el-form-item label="报告名称">
          <el-input v-model="reportForm.data.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="报告描述">
          <el-input v-model="reportForm.data.description" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="报告类型">
          <el-select v-model="reportForm.data.reportType" placeholder="请选择活动区域">
            <el-option label="区域一" value="shanghai"></el-option>
            <el-option label="区域二" value="beijing"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="报告格式">
          <el-select v-model="reportForm.data.reportFormat" placeholder="请选择活动区域">
            <el-option label="区域一" value="shanghai"></el-option>
            <el-option label="区域二" value="beijing"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="上传文件">
          <el-upload
            ref="upload"
            :action="uploadActionUrl"
            accept=".pdf,.doc,.docx,.xml,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            :limit="1"
            :headers="{
              'Authorization': getAccessToken()
            }"
            :on-success="uploadPostProcess"
            :on-exceed="maxFileLimitTop"
            :file-list="reportForm.data.fileList">
            <el-button size="small" type="primary">上传文件</el-button>
            <div slot="tip" class="el-upload__tip">只能同时上传1个pdf/word文件，且不超过10M</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="reportForm.visible = false">取 消</el-button>
        <el-button type="primary" @click="reportForm.visible = false">确 定</el-button>
      </div>
    </el-dialog>
  </div>

</template>

<script>
import backendStyle from '../../utils/generic-backend-style-util'
import projectApi from '@/api/project'
import reportApi from "@/api/report"
import attachmentApi from "@/api/attachment";
import {getAccessToken} from "@/utils/auth";

export default {
  name: "index",
  data() {
    return {
      searchForm: {
        name: '',
        status: '',
        username: '',
        startTimeAt: null,
        endTimeAt: null,
        creatTimeAt: null,
      },
      tableData: [],
      ...backendStyle.data(),
      ...attachmentApi.data()
      ,
      drawerFlag: false,
      drawerAction: false, // false 插入 / true 更新
      drawerDialogData: {
        name: '',
        description: ''
      },
      reportForm: {
        data: {
          name: '',
          description: '',
          reportType: '',
          reportFormat: '',
          fileList: [],
        },
        visible: false
      }
    }
  },

  created() {
    // 开始抓取数据  !!!
    this.getDataFunc()
  },

  methods: {
    getAccessToken,
    ...backendStyle.methods,
    ...projectApi,
    ...reportApi,
    getDataFunc() {
      console.log(this.pager)
      return this.getAllProjectsByPage(this.getSearchform(), this.pager).then(({result}) => {
        this.tableData = result.content
      })
    },

    openProjectDrawer(action) {
      if (action) {
        this.drawerAction = true
      }
      this.drawerFlag = true
    },

    uploadPostProcess({code}) {
      if (code !== 200) {
        this.$message.error("上传失败!!!")
        setTimeout(() => {
          this.$refs.upload?.clearFiles()
        },500)
      } else {
        this.$message.success("上传成功 !!!")
      }
    },
    saveProject() {
      if (!this.drawerAction) {
        // 保存
        this.createProject(this.drawerDialogData).then(() => {
          this.$message.success("新建项目成功 !!!")
          this.getDataFunc()
        })
      } else {
        this.updateProject(this.drawerDialogData).then(() => {
          this.$message.success("更新项目成功 !!!")
          this.getDataFunc()
        })
      }
    },
    openDialogCommitReport() {
      this.reportForm.visible = true;
      this.$refs.reportForm?.resetFields()
    },

    loadReportsTrigger(row, expand) {
      if (expand) {
        this.loadReports(row.id).then(({result}) => {
          row.reportList = result || []
        })
      }
    },
    deleteDialogHandle({row: {id, name}}) {
      this.$confirm(`确定删除项目${name}?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this
          .deleteProjectById(id)
          .then(() => {
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            this.getDataFunc()
          })

      }).catch(() => {
      });
    },
    maxFileLimitTop() {
      this.$message.warning("只能上传一个文件!!!")
    }
  }
}
</script>

<style scoped>

</style>
