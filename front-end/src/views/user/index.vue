<template>
  <div>
    <div class="search-line">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名称">
          <el-input v-model="searchForm.username" placeholder="请输入用户名称"></el-input>
        </el-form-item>
        <el-form-item label="注册时间">
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
    <el-divider class="margin-top-bottom-10"></el-divider>
    <template>
      <div class="margin-top-bottom-10 margin-left-25">
        <el-button type="success" @click="openDrawer(false)">创建用户</el-button>
      </div>
      <el-table
        :data="tableData"
        style="width: 100%">
        <el-table-column label="昵称" prop="nickname"/>
        <el-table-column label="姓名" prop="username"/>
        <el-table-column label="邮箱" prop="email"/>
        <el-table-column label="电话" prop="phone"/>
        <el-table-column label="个人简介" prop="description"/>

        <el-table-column label="创建时间" prop="createTimeStr"/>
        <el-table-column label="操作" width="250px" align="center">
          <template v-slot="props">
            <el-button @click="deleteDialogHandle(props)" type="danger">删除</el-button>
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
      :title="drawerAction ? '新增用户': '用户更新'"
      :visible.sync="drawerFlag"
      size="400px"
      direction="rtl">
      <div class="scroll-view">
        <el-form size="small" :model="drawerDialogData" class="search-form" style="padding: 5px">
          <el-form-item label="用户名">
            <el-input v-model="drawerDialogData.username" placeholder="请输入用户名"></el-input>
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input v-model="drawerDialogData.description" type="textarea"
                      size="medium"
                      :autosize="{minRows: 3,maxRows: 6}"
                      maxlength="300"
                      show-word-limit
                      placeholder="请输入个人简介"></el-input>
          </el-form-item>
          <el-form-item label="email">
            <el-input v-model="drawerDialogData.email" placeholder="请输入邮箱信息"></el-input>
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="drawerDialogData.nickname" placeholder="请输入昵称"></el-input>
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="drawerDialogData.gex" placeholder="请选择性别">
              <el-option label="发起时间" value="shanghai"></el-option>
              <el-option label="区域二" value="beijing"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="出生日期">
            <el-date-picker
              v-model="drawerDialogData.birthday"
              type="date"
              placeholder="请选择出生日期">
            </el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-form-item style="margin-top: 20px">
              <el-button type="primary" style="width: 100%" @click="saveUser">提交</el-button>
            </el-form-item>
          </el-form-item>
        </el-form>
      </div>

    </el-drawer>
  </div>

</template>

<script>
import backendStyle from '../../utils/generic-backend-style-util'
import dict from "@/api/dict";
import user from "@/api/user";
import {auto} from "html-webpack-plugin/lib/chunksorter";

export default {
  name: "index",
  data() {
    return {
      searchForm: {
        username: '',
        startTimeAt: '',
        endTimeAt: ''
      },
      tableData: [],
      drawerFlag: false,
      drawerAction: false, // false 插入 / true 更新
      drawerDialogData: {
        username: '',
        description: '',
        nickname: '',
        birthday: '',
        email: '',
        phone: '',
        gex: ''
      },
      ...backendStyle.data(),
    }
  },

  created() {
    this.getDataFunc()
  },
  methods: {
    auto,
    ...backendStyle.methods,
    ...dict.methods,
    ...user.methods,
    getDataFunc() {
      return this.getAllUsersByPage(this.searchForm, this.pager).then(({result}) => {
        this.tableData = result.content;
      })
    },
    saveUser() {
      if (!this.drawerAction) {
        this.registerUser(this.drawerDialogData).then(() => {
          this.$message.success("注册成功 !!!")
          this.getDataFunc()
        })
      } else {
        this.updateUser(this.drawerDialogData).then(() => {
          this.$message.success("更新成功 !!!")
          this.getDataFunc()
        })
      }
    },
    openDrawer(action) {
      if(action) {
        this.drawerAction = true
      }
      this.drawerFlag = true
    },
    deleteDialogHandle({row: {id, username}}) {
      this.$confirm(`确定删除用户${username}?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteUserById(id)
          .then(resolve => {
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            this.getDataFunc()
          })

      }).catch(() => {
      });
    }
  }
}
</script>

<style scoped lang="scss">
  .scroll-view {
    width: 100%;
    height: 100%;
    overflow-y: auto;
    max-height: 100%;
    position: relative;

    .search-form {
      position: absolute;
      top: 0;
      left: 0;
      bottom: 0;
      right: 0;
    }
  }
</style>
