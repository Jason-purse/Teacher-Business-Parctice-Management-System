<template>
  <div class="flex-container">
    <div class="search-line">
      <el-form
        ref="searchForm"
        :inline="true"
        :model="searchForm"
        class="search-form"
        @keydown.enter.native="entryKeyDown"
      >
        <el-form-item label="项目名称" prop="name">
          <el-input ref="input" v-model="searchForm.name" placeholder="请输入项目名称" clearable/>
        </el-form-item>
        <el-form-item v-if="isAdmin()" label="发起人" prop="username">
          <el-input v-model="searchForm.username" placeholder="请输入发起人"/>
        </el-form-item>
        <el-form-item label="发起开始时间" prop="startTimeAt">
          <el-date-picker
            v-model="searchForm.startTimeAt"
            type="date"
            placeholder="选择日期"
            @change="validTimeRange('startTimeAt');"
            @keydown.enter.a.native="entryKeyDown"
          />
        </el-form-item>
        <el-form-item label="发起结束时间" prop="endTimeAt">
          <el-date-picker
            ref="endTime"
            v-model="searchForm.endTimeAt"
            type="date"
            placeholder="选择日期"
            @change="validTimeRange('endTimeAt')"
            @keydown.enter.native="entryKeyDown"
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
      <div class="content">
        <div class="margin-top-bottom-10 margin-left-25">
          <el-button type="success" @click="openProjectDrawer(false)">创建项目</el-button>
        </div>

        <div class="internal-view">
          <el-table
            ref="tableData"
            :data="tableData"
            row-key="id"
            class="tableData"
            style="width: 100%"
            @row-click="expandRow"
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
                    :key="row.id"
                    v-loading="currentRow.loading"
                    element-loading-spinner="el-icon-loading"
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
                      <el-table-column label="文件预览">
                        <template v-slot="{row}">
                          <el-link size="small" @click="pdfPreviewAction(row)">点击预览</el-link>
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
                            <el-button
                              v-if="roleInfos.includes('admin') && !row.assignFlag"
                              size="small"
                              type="primary"
                              @click="addAuditUser(row)"
                            >指派审核
                            </el-button>
                          </template>
                          <template
                            v-else-if="row.failureFlag !== null && row.failureFlag && row.submitUserId === currentUserInfo.id">
                            <el-button size="small" type="success" @click="restoreReportAction(row)">重新申请
                            </el-button>
                            <br>
                            <el-button size="small" type="primary" @click="fileReHandle(row)">报告修改</el-button>
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
                <el-button type="warning" size="small" @click="deleteDialogHandle(props,$event)">删除</el-button>
                <el-button type="danger" size="small" @click="forceDeleteDialogHandle(props,$event)">强制删除
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

    <el-drawer
      :title="drawerAction ? '项目更新': '新建项目'"
      :visible.sync="drawerFlag"
      @close = "closeProjectDialog"
      size="300px"
      direction="rtl"
    >
      <el-form
        ref="drawerRef"
        size="small"
        :model="drawerDialogData"
        :rules="rules"
        class="search-form"
        style="padding: 5px"
      >
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
          <el-input v-model="reportForm.data.reportName" autocomplete="off" :disabled="reportForm.updatable"/>
        </el-form-item>
        <el-form-item label="报告描述" prop="description">
          <el-input v-model="reportForm.data.description" autocomplete="off" :disabled="reportForm.updatable"/>
        </el-form-item>
        <el-form-item label="报告类型" prop="reportType">
          <el-select v-model="reportForm.data.reportType" placeholder="请选择报告类型" :disabled="reportForm.updatable">
            <el-option v-for="{itemType,itemValue,id} in reportTypes" :key="itemType" :label="itemValue" :value="id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="上传文件" prop="fileList">
          <el-upload
            ref="upload"
            :before-remove="removeFileList"
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
        <el-button type="primary" @click="commitReport(reportForm.updatable ? 'update-report': 'save-report')">确 定
        </el-button>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="visible" title="选择指派审核人" width="80%" @close="auditDialogCancel()">
      <template v-if="exists">
        <div>
          <el-steps :active="assignIndex" :align-center="true">
            <el-step
              v-for="({id,itemValue},index) in auditPhase"
              ref="assignStep"
              :key="id"
              finish-status="success"
              simple
              :status="(assignForm && assignForm[index]) ? 'success' : 'process'"
              :title="itemValue"
              description="请指派审核人"
            />
          </el-steps>
        </div>
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
              <el-table-column label="角色" prop="roles">
                <template v-slot="{row:{roles}}">
                  {{ roles.map(ele => ele.itemValue) }}
                </template>
              </el-table-column>
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
      </template>

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
    <el-dialog
      title="文件浏览"
      class="preview-dialog"
      width="fit-content"
      :visible.sync="pdfPreview.visible"
      @close="closePdfPreview"
    >
      <pdf
        v-if="pdfPreview.fileType.includes('pdf')"
        :key="pdfPreview.url"
        :page="pdfPreview.pageNum"
        :src="pdfPreview.url"
        @num-pages="pdfPreview.totalPages =$event"
        @error="previewError"
      />
      <template v-else>
        <vue-office-docx :key="pdfPreview.url" :src="pdfPreview.url"/>
      </template>
    </el-dialog>

  </div>

</template>

<script>
import backendStyle from '../../utils/generic-backend-style-util'
import projectApi from '@/api/project'
import reportApi from '@/api/report'
import dictApi from '@/api/dict'
import userApi from '@/api/user'
import attachmentApi from '@/api/attachment'
import {getAccessToken, getRoleInfos} from '@/utils/auth'
import auditApi from '@/api/audit'
import {mapState} from 'vuex'
import pdf from 'vue-pdf'

import VueOfficeDocx from '@vue-office/docx'
import report from "@/api/report";

export default {
  name: 'Index',
  components: {pdf, VueOfficeDocx},
  data() {
    return {
      roleInfos: getRoleInfos(),
      currentUserInfo: null,
      assignForm: [],
      assignIndex: 1,
      exists: false,
      visible: false,
      pdfPreview: {
        url: '',
        pageNum: 1,
        totalPages: 1,
        pageRotate: '',
        fileType: '',
        visible: false
      },
      rules: {
        name: [{required: true, trigger: ['blur', 'change'], message: '请输入项目名称'}],
        description: [{required: true, trigger: ['blur', 'change'], message: '请输入项目描述'}]
      },
      audit: {
        // 当前报告数据
        data: {},
        // 审核阶段需要设置的数据 ...
        auditPhases: []
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
        index: -1,
        loading: false
      },
      reportForm: {
        // 表示更新
        updatable: false,
        data: {
          reportName: '',
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
        reportFormat: [{required: true, trigger: ['change'], message: '请选择报告格式'}],
        fileList: [{
          required: true,
          type: 'array',
          trigger: ['blur', 'change'],
          message: '必须提供报告附件'
        }]
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
    this.getMediaTypes()
    this.getCurrentUserInfo().then(({result}) => {
      this.currentUserInfo = result
    })
  },

  methods: {
    ...mapState('user', ['userInfo']),
    getAccessToken,
    ...backendStyle.methods,
    ...projectApi,
    ...reportApi.methods,
    ...dictApi.methods,
    ...auditApi.methods,
    ...attachmentApi.methods,
    ...userApi.methods,

    closeProjectDialog() {
      this.$refs.drawerRef?.resetFields()
    },
    removeFileList(file, fileList) {
      this.reportForm.data.fileList = []
    },
    // 文件重新提交
    fileReHandle(row) {
      this.reportForm.updatable = true;
      this.reportForm.currentRow = row; // 当前行 ..

      this.reportForm.data.fileList = [{name: row.reportFileName, url: row.reportUrlStr}];
      // this.reportForm.data.reportFormat = row.reportFormat
      // this.reportForm.data.reportType = row.reportType
      // this.reportForm.data.reportName = row.reportName
      // this.reportForm.data.reportUrlId = row.reportUrlId
      // this.reportForm.data.description = row.description
      this.reportForm.data = {
        ...this.reportForm.data,
        ...row
      }
      this.reportForm.visible = true;

      // 重新修改文件 ...
      // 也就是修改已有的已经上传的文件 ...
    },
    getAllUserList() {
      userApi.methods.getAllUsersByPage(this.userData.searchForm, this.userData.pager).then(({result: {content}}) => {
        this.userData.data = content || []
      })
    },
    previewError() {
      this.$message.error('预览文件失败,无法加载此文件 !!!')
      this.pdfPreview = {visible: false, pageNum: 1, url: '', totalPages: 1, fileType: ''}
    },
    expandRow(row) {
      if (this.currentRow.row !== row) {
        this.$refs.tableData.toggleRowExpansion(row, [row])
      } else {
        // 关闭 ..
        this.$refs.tableData.toggleRowExpansion(row, [])
      }
    },
    closePdfPreview() {
      this.pdfPreview.visible = false
    },
    openDescriptionDialog({failureReason, successDescription, failureFlag}) {
      this.auditResult.visible = true
      // console.log(failureReason)
      this.auditResult.description = (failureFlag ? failureReason : successDescription) || '没有任何说明 !!'
    },
    getDataFunc() {
      return this.getAllProjectsByPage(this.getSearchform(), this.pager).then(({result}) => {
        this.tableData = result.content.map((ele, index) => {
          ele.index = index
          return ele
        })
        if (this.currentRow.row) {
          let value = null;
          for (let i = 0; i < result.content.length; i++) {
            if (result.content[i].id === this.currentRow.row.id) {
              value = result.content[i];
              this.currentRow.row = value;
              this.currentRow.index = i;
              break;
            }
          }
          if (value != null) {
            this.loadReportsTrigger(this.currentRow.row, [this.currentRow.row])
          } else {
            // 外层数据没有了,那么内层直接滞空 ..
            this.currentRow.row = null;
            this.currentRow.index = -1;
            this.currentRow.reportList = []
          }

        }

        return result
      })
    },

    openProjectDrawer(action) {
      if (action) {
        this.drawerAction = true
      }
      this.drawerFlag = true

      // 关闭报告
      if (this.currentRow) {
        this.$refs.tableData.toggleRowExpansion(this.currentRow.row, false)
      }
    },
    auditDialogCancel() {
      this.userData.searchForm = {}
      this.audit = {}
      this.assignForm = []
      this.assignIndex = 1
      this.exists = false
      this.visible = false
    },
    uploadPostProcess({code, result}) {
      if (code !== 200) {
        this.$message.error('上传失败!!!')
        setTimeout(() => {
          this.$refs.upload?.clearFiles()
        }, 500)
      } else {
        this.reportForm.data.reportUrlId = result.id
        this.reportForm.data.fileList = [{name: result.fileName, url: result.fileUrl}]
        this.$message.success('上传成功 !!!')
      }
    },

    createAuditAction(user) {
      this.$nextTick(() => {
        this.assignForm[this.assignIndex - 1] = {
          reportId: this.audit.data.id,
          userId: user.id,
          username: user.username,
          auditPhaseId: this.auditPhase[this.assignIndex - 1].id
        }

        // 最后一个直接提交 ...
        if (this.auditPhase.length === this.assignIndex) {
          // 提交,加1
          this.createAudit({
            reportId: this.audit.data.id,
            auditHandlers: this.assignForm
          }).then(() => {
            this.$message.success('指派成功 !!!')
            // 关闭弹窗 !!!
            this.auditDialogCancel()
            // 重新加载项目
            this.updateReportList()
          }).catch(() => {
            this.$message.error('指派失败 !!!')
          })

          this.auditDialogCancel()
          console.log('清理', this.audit.visible)
          return
        }
        this.assignIndex += 1
      })
    },

    pdfPreviewAction({reportUrlStr, mediaType}) {
      // window.location.origin +
      this.pdfPreview.url = reportUrlStr.replace(/\\/g, '/')
      this.pdfPreview.visible = true
      this.pdfPreview.fileType = this.mapDictItemType('mediaType', mediaType)
    },
    validReportFormat(file) {
      const type = file.type
      const format = this.reportFormat.findIndex(ele => ele.itemType === type)
      if (format) {
        this.reportForm.data.reportFormat = format.id
        return true
      } else {
        this.$message.warning('当前上传类型和选择类型不符合,请检查 !!')
        return false
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
      this.reportForm.updatable = false
      this.$refs.reportForm?.resetFields()
      this.reportForm.data = {}
      this.reportForm.currentRow = row
    },

    loadReportsTrigger(row, expand) {
      // (row 表示展开)
      if (expand.indexOf(row) !== -1) {
        this.currentRow.loading = false
        // 表示此元素打开
        if (this.currentRow.row && this.currentRow.row !== row) {

          // 之前的bug, 拿到不同的响应式数据进行row 关闭,必然关闭失败 !!!
          // 原因是 外层表单数据查询之后是一个新的响应式对象列表，所以 不等于之前的对象 ...
          this.$refs.tableData.toggleRowExpansion(this.currentRow.row, false)
          this.currentRow.reportList = []
          this.currentRow.row = row
          this.currentRow.index = row.index
        } else if (!this.currentRow.row) {
          this.currentRow.reportList = []
          this.currentRow.row = row
          this.currentRow.index = row.index
        }
        // 否则不需要做任何事情 ..
        this.loadReports(row.id).then(({result}) => {
          this.currentRow.reportList = result
        }).catch(() => {
          this.currentRow.reportList = []
        })
          .finally(() => {
            this.currentRow.loading = false
          })
      } else {
        this.currentRow.loading = false
        console.log("当前关闭的行是" + this.currentRow.row.name)
      }
    },
    deleteDialogHandle({row: {id, name}}, event) {
      event.stopPropagation();
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

    forceDeleteDialogHandle({row: {id, name}}, event) {
      event.stopPropagation()
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
      this.exists = true
      this.visible = true
      this.getAllUserList()
      console.log(data)
      this.audit.data = data
    },

    restoreReportAction(row) {
      const params = {...row, restore: true}
      this.updateReport(params).then(() => {
        this.updateReportList()
      })
    },

    deleteReportDialogHandle(row) {
      const {reportName, id} = row
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
          // 清理 ..
          this.onCancel('reportForm')
          this.reportForm.currentRow = false;
          this.reportForm.updatable = false;
          this.reportForm.data = {}
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

<style scoped lang="scss">
.tableData {
  :hover {
    cursor: pointer;
  }
}

.flex-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  max-height: 100%;
  overflow-y: hidden;

  .content {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow-y: hidden;

    .internal-view {
      display: flex;
      flex-direction: column;
      flex: 1;
      overflow-y: hidden;

      .tableData {
        overflow-y: auto;
      }
     .el-table::before {
       height: 0;
     }
    }
  }
}

::v-deep .preview-dialog {
  .el-dialog__body {
    overflow: auto;
    min-height: 600px;
    height: 80vh;
    max-height: 700px;
    width: 80vw;
    min-width: 960px;
    max-width: 90vw;
  }

  .docx-wrapper {
    background: none;

    .docx {
      box-shadow: none;
    }
  }
}
</style>
