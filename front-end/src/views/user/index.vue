<template>
  <!--  用户管理-->
  <div>
    <div class="search-line">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名称">
          <el-input v-model="searchForm.username" placeholder="请输入用户名称" clearable />
        </el-form-item>
        <el-form-item label="注册时间">
          <el-date-picker
            v-model="searchForm.startTimeAt"
            type="date"
            placeholder="选择日期"
            @change="validTimeRange('startTimeAt')"
          />
        </el-form-item>
        <el-form-item label="结束时间">
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
        </el-form-item>
      </el-form>
    </div>
    <el-divider class="margin-top-bottom-10" />
    <template>
      <div class="margin-top-bottom-10 margin-left-25">
        <el-button type="success" @click="openDrawer(false)">创建用户</el-button>
      </div>
      <el-table
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column label="昵称" prop="nickname" />
        <el-table-column label="姓名" prop="username" />
        <el-table-column label="角色" prop="roles">
          <template v-slot="{row:{roles}}">
            {{ roles.map(ele => ele.itemValue) }}
          </template>
        </el-table-column>
        <el-table-column label="邮箱" prop="email" />
        <el-table-column label="电话" prop="phone" />
        <el-table-column label="个人简介" prop="description" />
        <el-table-column label="性别">
          <template v-slot="{row: {gex}}">
            {{ mapDictItemValue('genderStatus', gex) }}
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTimeStr" />
        <el-table-column label="操作" width="250px" align="center">
          <template v-slot="props">
            <el-button type="primary" @click="assignRolesToUser(props)">分配角色</el-button>
            <el-button type="danger" @click="deleteDialogHandle(props)">删除</el-button>
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
        />
      </div>
    </template>
    <el-drawer
      :title="drawerAction ? '新增用户': '用户更新'"
      :visible.sync="drawerFlag"
      size="400px"
      direction="rtl"
    >
      <div class="scroll-view">
        <el-form size="small" :model="drawerDialogData" class="search-form" style="padding: 5px">
          <el-form-item label="用户名">
            <el-input v-model="drawerDialogData.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="drawerDialogData.nickname" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input
              v-model="drawerDialogData.description"
              type="textarea"
              size="medium"
              :autosize="{minRows: 3,maxRows: 6}"
              maxlength="300"
              show-word-limit
              placeholder="请输入个人简介"
            />
          </el-form-item>
          <el-form-item label="email">
            <el-input v-model="drawerDialogData.email" placeholder="请输入邮箱信息" />
          </el-form-item>
          <el-form-item label="phone">
            <el-input v-model="drawerDialogData.phone" placeholder="请输入手机号码" />
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="drawerDialogData.gex" placeholder="请选择性别">
              <el-option
                v-for="({itemType,itemValue,id}) in genderStatus"
                :key="itemType"
                :label="itemValue"
                :value="id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="出生日期">
            <el-date-picker
              v-model="drawerDialogData.birthday"
              type="date"
              placeholder="请选择出生日期"
            />
          </el-form-item>
          <el-form-item>
            <el-form-item style="margin-top: 20px">
              <el-button type="primary" style="width: 100%" @click="saveUser">提交</el-button>
            </el-form-item>
          </el-form-item>
        </el-form>
      </div>

    </el-drawer>

    <el-dialog
      key="assign-role"
      title="分配角色"
      :visible.sync="roleForm.visible"
      width="60%"
      @close="roleForm.visible = false; roleForm.data = {}"
    >
      <el-button type="primary" size="small" :disabled="roleForm.disabled" @click="subUserWithRoles">分配</el-button>
      <el-table
        key="assignRolesTable"
        ref="assignedRoles"
        :data="roles"
        reserve-selection="id"
        row-key="id"
        @select-all="configUserRoles"
        @select="configUserRoles"
      >
        <el-table-column type="selection" :reserve-selection="true" />
        <el-table-column label="角色" prop="itemValue" align="center" />
      </el-table>
    </el-dialog>
  </div>

</template>

<script>
import backendStyle from '../../utils/generic-backend-style-util'
import dict from '@/api/dict'
import user from '@/api/user'
import { auto } from 'html-webpack-plugin/lib/chunksorter'
import role from '@/api/role'

export default {
  name: 'Index',
  data() {
    return {
      searchForm: {
        username: '',
        startTimeAt: null,
        endTimeAt: null
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
      roleForm: {
        visible: false,
        disabled: true,
        data: {
          userId: '',
          roles: []
        }
      },
      ...backendStyle.data(),
      ...dict.data()
    }
  },

  created() {
    this.getGenderStatus()
    this.onSubmit()
    this.getRoles()
  },
  methods: {
    auto,
    ...backendStyle.methods,
    ...dict.methods,
    ...user.methods,
    ...role.methods,
    getDataFunc() {
      return this.getAllUsersByPage(this.getSearchform(), this.pager).then(({ result }) => {
        this.tableData = result.content
        return result
      })
    },
    defaultAssignRoles() {
      const values = []
      this.$nextTick(() => {
        if (this.roleForm.data.roles) {
          const ids = this.roleForm.data.roles.map(ele => ele.id)
          this.roles.forEach(ele => {
            if (ids.includes(ele.id)) {
              values.push(ele)
            }
          })
        }
        values.forEach(ele => {
          this.$refs.assignedRoles.toggleRowSelection(ele)
        })
      })
    },
    configUserRoles(selection) {
      this.roleForm.data.roles = selection
      this.roleForm.disabled = !(selection.length > 0)
    },
    saveUser() {
      if (!this.drawerAction) {
        this.registerUser(this.drawerDialogData).then(() => {
          this.$message.success('注册成功 !!!')
          this.getDataFunc()
        })
      } else {
        this.updateUser(this.drawerDialogData).then(() => {
          this.$message.success('更新成功 !!!')
          this.getDataFunc()
        })
      }
    },
    subUserWithRoles() {
      // 提交完毕之后,清理表单 ...
      this.updateRoleForUser({
        ... this.roleForm.data,
        // 更新角色信息
        roleIds: this.roleForm.data.roles.map(ele => ele.id)
      }).then(() => {
        this.$message.success('分配成功 !!!')
        this.onSubmit()
      }).catch(() => {
        this.$message.error('分配失败 !!!')
      })
        .finally(() => {
          this.roleForm.disabled = true
          this.roleForm.data = {}
          this.roleForm.visible = false
        })
    },
    openDrawer(action) {
      if (action) {
        this.drawerAction = true
      }
      this.drawerFlag = true
    },
    deleteDialogHandle({ row: { id, username }}) {
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
            })
            this.getDataFunc()
          })
      }).catch(() => {
      })
    },
    assignRolesToUser({ row }) {
      this.roleForm.visible = true
      this.roleForm.data.userId = row.id
      this.roleForm.data.roles = row.roles || []
      // 设置角色 ..
      this.defaultAssignRoles()
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
