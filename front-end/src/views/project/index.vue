<template>
  <div>
    <div class="search-line">
      <el-form ref="searchForm" :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="searchForm.name" placeholder="请输入项目名称" clearable/>
        </el-form-item>
        <el-form-item label="发起人" prop="username">
          <el-input v-model="searchForm.username" placeholder="请输入发起人"/>
        </el-form-item>
        <el-form-item label="发起开始时间" prop="startTimeAt">
          <el-date-picker
            v-model="searchForm.startTimeAt"
            type="date"
            placeholder="选择日期"
            @change="validTimeRange('startTimeAt')"
          />
        </el-form-item>
        <el-form-item label="发起结束时间" prop="endTimeAt">
          <el-date-picker
            v-model="searchForm.endTimeAt"
            type="date"
            placeholder="选择日期"
            @change="validTimeRange('endTimeAt')"
          />
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
    <el-divider class="margin-top-bottom-10"/>
    <template>
      <div class="margin-top-bottom-10 margin-left-25">
        <el-button type="success" @click="openProjectDrawer(false)">创建项目</el-button>
      </div>

      <el-table
        ref="tableData"
        :data="tableData"
        row-key="id"
        style="width: 100%"
        @expand-change="loadReportsTrigger"
      >
        <el-table-column type="expand">
          <template v-slot="{row}">
            <div :style="{margin: '10px'}">
              <div class="margin-top-bottom-10 margin-left-25">
                <el-button type="primary" size="small" @click="openDialogCommitReport(row)">提交报告</el-button>
              </div>
              <el-table
                ref="reportList"
                size="small"
                row-key="id"
                :data="currentRow.reportList"
              >
                <el-table-column label="报告列表">
                  <el-table-column label="名称" prop="reportName" align="center"/>
                  <el-table-column label="描述" prop="description" align="center"/>
                  <el-table-column label="发起人" prop="submitUserName" align="center"/>
                  <el-table-column label="报告类型" prop="reportType" align="center">
                    <template v-slot="{row: {reportType}}">
                      {{ mapDictItemValue('reportTypes', reportType) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="报告格式" prop="reportFormat" align="center">
                    <template v-slot="{row: {reportFormat}}">
                      {{ mapDictItemValue('reportFormat', reportFormat) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="发起时间" prop="createTimeStr" align="center"/>
                  <el-table-column label="审核人" prop="auditUserName" align="center"/>
                  <el-table-column label="文件预览" >
                    <template v-slot="{row}">
                        <el-link size="small" @click="pdfPreviewAction">点击预览</el-link>
                    </template>
                  </el-table-column>
                  <el-table-column label="审核阶段" prop="auditPhase" align="center">
                    <template v-slot="{row: {auditPhase}}">
                      {{ mapDictItemValue('auditPhase', auditPhase) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="审核状态" prop="status" align="center">
                    <template v-slot="{row}">
                      <el-link type="primary" @click="openDescriptionDialog(row)">
                        {{ mapDictItemValue('auditStatus', row.status) }}
                      </el-link>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" align="center">
                    <template v-slot="{row}">
                      <template v-if="!row.auditUserId">
                        <el-button size="small" type="primary" @click="addAuditUser(row)">指派审核</el-button>
                      </template>
                      <template v-else-if="row.failureFlag !== null && row.failureFlag">
                        <el-button size="small" type="success" @click="restoreReportAction(row)">重新申请</el-button>
                      </template>
                      <el-button
                        :disabled="row.finished"
                        size="small"
                        type="danger"
                        @click="deleteReportDialogHandle(row)"
                      >删除
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table-column>
              </el-table>
            </div>
          </template>
        </el-table-column>
        <el-table-column type="index" label="序号"/>
        <el-table-column label="名称" prop="name"/>
        <el-table-column label="描述" prop="description"/>
        <el-table-column label="发起人" prop="username"/>
        <el-table-column label="发起时间" prop="createTimeStr"/>
        <el-table-column label="状态" prop="status">
          <template v-slot="{row: {status}}">
            {{ mapDictItemValue('projectStatus', status) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center">
          <template v-slot="props">
            <el-button type="warning" size="small" @click="deleteDialogHandle(props)">删除</el-button>
            <el-button type="danger" size="small" @click="forceDeleteDialogHandle(props)">强制删除</el-button>
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
    </template>

    <el-drawer
      :title="drawerAction ? '项目更新': '新建项目'"
      :visible.sync="drawerFlag"
      size="300px"
      direction="rtl"
    >
      <el-form size="small" ref="drawerRef" :model="drawerDialogData" :rules="rules" class="search-form"
               style="padding: 5px">
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="drawerDialogData.name" placeholder="请输入项目名称"/>
        </el-form-item>
        <el-form-item label="项目描述" prop="description">
          <el-input v-model="drawerDialogData.description" placeholder="请输入项目描述"/>
        </el-form-item>
        <el-form-item>
          <el-form-item style="margin-top: 20px">
            <el-button type="primary" style="width: 100%" @click="saveProject()">提交</el-button>
          </el-form-item>
        </el-form-item>
      </el-form>
    </el-drawer>

    <el-dialog title="提交报告" :visible.sync="reportForm.visible">
      <el-form ref="reportForm" :model="reportForm.data" :rules="reportFormRule">
        <el-form-item label="报告名称" prop="reportName">
          <el-input v-model="reportForm.data.reportName" autocomplete="off"/>
        </el-form-item>
        <el-form-item label="报告描述" prop="description">
          <el-input v-model="reportForm.data.description" autocomplete="off"/>
        </el-form-item>
        <el-form-item label="报告类型" prop="reportType">
          <el-select v-model="reportForm.data.reportType" placeholder="请选择报告类型">
            <el-option v-for="{itemType,itemValue,id} in reportTypes" :key="itemType" :label="itemValue" :value="id"/>
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
            :before-upload="validReportFormat"
            :on-success="uploadPostProcess"
            :on-exceed="maxFileLimitTop"
            :file-list="reportForm.data.fileList"
          >
            <el-button size="small" type="primary">上传文件</el-button>
            <div slot="tip" class="el-upload__tip">只能同时上传1个pdf/word文件，且不超过10M</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="onCancel('reportForm')">取 消</el-button>
        <el-button type="primary" @click="commitReport('save-report')">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="选择指派审核人" :visible.sync="audit.visible" width="80%" @close="auditDialogCancel()">
      <div>
        <div class="search-line">
          <el-form :inline="true" :model="userData.searchForm" class="search-form">
            <el-form-item label="用户名称">
              <el-input v-model="userData.searchForm.username" placeholder="请输入用户名称"/>
            </el-form-item>
            <el-form-item label="注册时间">
              <el-date-picker
                v-model="userData.searchForm.startTimeAt"
                type="date"
                placeholder="选择日期"
                @change="validTimeRange('startTimeAt'),userData.searchForm"
              />
            </el-form-item>
            <el-form-item label="结束时间">
              <el-date-picker
                v-model="userData.searchForm.endTimeAt"
                type="date"
                placeholder="选择日期"
                @change="validTimeRange('endTimeAt',userData.searchForm)"
              />
            </el-form-item>
            <el-form-item>
              <el-form-item>
                <el-button type="primary" @click="getAllUserList">查询</el-button>
              </el-form-item>
            </el-form-item>
          </el-form>
        </div>
        <el-divider class="margin-top-bottom-10"/>
        <template>
          <el-table
            :data="userData.data"
            style="width: 100%"
          >
            <el-table-column label="昵称" prop="nickname"/>
            <el-table-column label="姓名" prop="username"/>
            <el-table-column label="角色" prop="roles"/>
            <el-table-column label="操作" width="250px" align="center">
              <template v-slot="{row}">
                <el-button type="primary" @click="createAuditAction(row)">指派</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div style="margin-top: 5px;text-align: right;">
            <el-pagination
              small
              background
              :current-page.sync="userData.pager.page"
              :page-size="userData.pager.size"
              layout="prev, pager, next"
              :total="userData.pager.total"
            />
          </div>
        </template>
      </div>
    </el-dialog>

    <el-dialog
      title="审核结果-历史记录"
      :visible.sync="auditResult.visible"
      width="60%"
      @close="auditResult.visible = false; auditResult.description = ''"
    >
      <span>{{ auditResult.description }}</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="auditResult.visible = false; auditResult.description = ''">关闭</el-button>
      </span>
    </el-dialog>
    <pdf  ref="pdf"
         :src="pdfPreview.url"
         :page="pdfPreview.pageNum">
    </pdf>
  </div>

</template>

<script>
import backendStyle from '../../utils/generic-backend-style-util'
import projectApi from '@/api/project'
import reportApi from '@/api/report'
import dictApi from '@/api/dict'
import userApi from '@/api/user'
import attachmentApi from '@/api/attachment'
import {getAccessToken} from '@/utils/auth'
import auditApi from '@/api/audit'
import {mapState} from 'vuex'

export default {
  name: 'Index',
  data() {
    return {

      pdfPreview: {
        url: '',
        pageNum: '',
        pageRotate: '',
      },
      rules: {
        name: [{required: true, trigger: ['blur', 'change'], message: '请输入项目名称'}],
        description: [{required: true, trigger: ['blur', 'change'], message: '请输入项目描述'}]
      },
      audit: {
        // 当前报告数据
        data: {},
        visible: false
      },
      auditResult: {
        description: '',
        visible: false
      },
      userData: {
        searchForm: {
          username: '',
          startTimeAt: '',
          endTimeAt: ''
        },
        data: [],
        pager: {
          page: 0,
          size: 10
        }
      },
      searchForm: {
        name: '',
        status: '',
        username: '',
        startTimeAt: null,
        endTimeAt: null,
        creatTimeAt: null
      },
      tableData: [],
      ...backendStyle.data(),
      ...attachmentApi.data(),
      ...dictApi.data(),
      drawerFlag: false,
      drawerAction: false, // false 插入 / true 更新
      drawerDialogData: {
        name: '',
        description: ''
      },

      fileFormats: ['application/pdf', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'application/msword'],

      /**
       * 使用它的原因是,tableData中的数据中的row 的reportList发生变化的时候,无法更新试图,这种排他性处理,让
       * 试图通过currentRow.reportList强制刷新
       */
      currentRow: {
        row: null,
        reportList: [],
        index: -1
      },
      reportForm: {
        data: {
          name: '',
          description: '',
          reportType: '',
          reportFormat: '',
          reportUrlId: '',
          fileList: []
        },
        currentRow: null,
        visible: false
      },
      reportFormRule: {
        reportName: [{required: true, trigger: ['blur', 'change'], message: '请输入报告名称'}],
        description: [{required: true, trigger: ['blur', 'change'], message: '请输入报告描述'}],
        reportType: [{required: true, trigger: ['blur', 'change'], message: '请选择报告类型'}],
        reportFormat: [{required: true, trigger: ['blur', 'change'], message: '请选择报告格式'}]
      }
    }
  },

  created() {
    // 开始抓取数据  !!!
    this.onSubmit()
    this.getReportTypes()
    this.getReportFormat()
    this.getAuditPhase()
    this.getAuditStatus()
    this.getProjectStatus()
  },

  methods: {
    mapState,
    getAccessToken,
    ...backendStyle.methods,
    ...projectApi,
    ...reportApi.methods,
    ...dictApi.methods,
    ...auditApi.methods,
    getAllUserList() {
      userApi.methods.getAllUsersByPage(this.userData.searchForm, this.userData.pager).then(({result: {content}}) => {
        this.userData.data = content || []
      })
    },
    openDescriptionDialog({failureReason, successDescription, failureFlag}) {
      this.auditResult.visible = true
      console.log(failureReason)
      this.auditResult.description = (failureFlag ? failureReason : successDescription) || '没有任何说明 !!'
    },
    getDataFunc() {
      return this.getAllProjectsByPage(this.getSearchform(), this.pager).then(({result}) => {
        this.tableData = result.content.map((ele, index) => {
          {
            ele.index = index
            return ele
          }
        })
        return result
      })
    },

    openProjectDrawer(action) {
      if (action) {
        this.drawerAction = true
      }
      this.drawerFlag = true
    },
    auditDialogCancel() {
      this.userData.searchForm = {}
      this.audit = {}
    },
    uploadPostProcess({code, result}) {
      if (code !== 200) {
        this.$message.error('上传失败!!!')
        setTimeout(() => {
          this.$refs.upload?.clearFiles()
        }, 500)
      } else {
        this.reportForm.data.reportUrlId = result.id
        this.$message.success('上传成功 !!!')
      }
    },

    createAuditAction(user) {
      const param = {
        reportId: this.audit.data.id,
        auditUserId: user.id,
        auditUserName: user.username
      }
      this.createAudit(param).then(() => {
        // 重新加载项目
        this.updateReportList()
        // 关闭弹窗 !!!
        this.auditDialogCancel()
      })
    },

    pdfPreviewAction({reportUrl}) {
      this.pdfPreview.url = reportUrl;
    },

    validReportFormat(file) {
      let type = file.type;
      const format = this.reportFormat.findIndex(ele => ele.itemType === type);
      if (format) {
        this.reportForm.data.reportFormat = format.id
        return true;
      } else {
        this.$message.warning("当前上传类型和选择类型不符合,请检查 !!")
        return false;
      }
    },

    closeDrawer() {
      this.drawerFlag = false
      this.drawerDialogData = {}
    },
    saveProject() {
      this.$refs.drawerRef.validate(valid => {
        if (valid) {
          if (!this.drawerAction) {
            // 保存
            this.createProject(this.drawerDialogData).then(() => {
              this.$message.success('新建项目成功 !!!')
              this.onSubmit()
              this.closeDrawer()
            })
          } else {
            this.updateProject(this.drawerDialogData).then(() => {
              this.$message.success('更新项目成功 !!!')
              this.onSubmit()
              this.closeDrawer()
            })
          }
          // 优化可见性
          this.currentRow = {
            row: null,
            index: -1,
            reportList: []
          }
        } else {
          this.$message.warning('请输入')
        }
      })
    },
    openDialogCommitReport(row) {
      this.reportForm.visible = true
      this.$refs.reportForm?.resetFields()
      this.reportForm.currentRow = row
    },

    loadReportsTrigger(row, expand) {
      if (expand) {
        if (this.currentRow.row && this.currentRow.row !== row) {
          this.$refs.tableData.toggleRowExpansion(this.currentRow.row, false)
        }
        this.loadReports(row.id).then(({result}) => {
          this.currentRow = {
            row,
            reportList: result
          }
        })
      } else {
        this.currentRow = {}
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
            })
            this.onSubmit()
          })
      }).catch(() => {
      })
    },

    forceDeleteDialogHandle({row: {id, name}}) {
      this.$confirm(`确定强制删除项目${name}? 这会删除项目有关的所有东西，包括提交的报告。`, '强制删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this
          .deleteProjectById(id, {force: true})
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

    // 更新当前项目  以及它的报告列表
    updateReportList() {
      this.getProjectById(this.currentRow.row.id).then(({result}) => {
        // 直接刷新
        this.$set(this.tableData, this.currentRow.row.index, result)
      })
      this.loadReports(this.currentRow.row.id).then(({result}) => {
        this.currentRow.reportList = result
      })
    },

    addAuditUser(data) {
      this.audit.visible = true
      this.getAllUserList()
      this.audit.data = data
    },

    restoreReportAction(row) {
      const params = {...row, restore: true}
      this.updateReport(params).then(() => {
        this.updateReportList()
      })
    },

    deleteReportDialogHandle(row) {
      const {reportName, id, projectId} = row
      this.$confirm(`确定删除报告${reportName}?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this
          .deleteReport(id)
          .then(() => {
            this.$message({
              type: 'success',
              message: '删除成功!'
            })
            this.updateReportList()
          })
      }).catch(() => {
      })
    },
    commitReport(action) {
      this.$refs.reportForm.validate(valid => {
        if (valid) {
          if (action === 'save-report') {
            this.createReport({...this.reportForm.data, projectId: this.reportForm.currentRow?.id}).then(() => {
              this.$message.success('新增报告成功!!!')
              this.updateReportList()
            })
          } else if (action === 'update-report') {
            this.updateReport(this.reportForm.data).then(() => {
              this.$message.success('更新报告成功 !!!')
              this.updateReportList()
            })
          }
          this.onCancel('reportForm')
        } else {
          this.$message.warning('请输入')
        }
      })
    },
    maxFileLimitTop() {
      this.$message.warning('只能上传一个文件!!!')
    }
  }
}
</script>

<style scoped>

</style>
