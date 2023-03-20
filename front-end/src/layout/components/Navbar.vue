<template>
  <div class="navbar">
    <div class="navbar-left">
      <hamburger :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />
      <breadcrumb />
    </div>
    <div class="navbar-right">
      <template v-if="currentUserInfo_temp.roles">
        <div v-if="isAdmin()" class="roles-list">
          <span>{{ currentUserInfo_temp.roles.filter(ele => ele.itemType === 'admin')[0].itemValue }}</span>
        </div>
        <div v-else class="roles-list"><span v-for="(item) in currentUserInfo_temp.roles" :key="item.id">{{ item.itemValue }}</span>
        </div>
      </template>
      <div class="right-menu">
        <el-dropdown class="avatar-container" trigger="click">
          <div class="avatar-wrapper">
            <!--          <img :src="avatar+'?imageView2/1/w/80/h/80'" class="user-avatar">-->
            <el-avatar class="sub-title" icon="el-icon-user-solid" />
            <i class="el-icon-caret-bottom" />
          </div>
          <el-dropdown-menu slot="dropdown" class="user-dropdown">
            <el-dropdown-item>
              <div @click="drawerVisable = !drawerVisable;">个人主页</div>
            </el-dropdown-item>
            <el-dropdown-item divided @click.native="logout">
              <span style="display:block;">退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    <el-drawer
      title="个人主页"
      :visible.sync="drawerVisable"
      size="20%"
      :wrapper-closable="false"
    >
      <div style="padding: 0 20px">
        <el-button v-if="!isEdit" type="primary" size="mini" @click="isEdit = true">编辑</el-button>
        <el-button v-if="isEdit" type="primary" size="mini" @click="updateUserInfo">保存</el-button>
        <el-button v-if="isEdit" type="warning" size="mini" @click="isEdit = false">取消</el-button>
        <el-form ref="form" :model="formInfo" label-width="50px" style="margin-top: 10px" :rules="rules">
          <el-form-item label="姓名">
            <div>{{ currentUserInfo_temp.username }}</div>
          </el-form-item>
          <el-form-item label="昵称" prop="nickname">
            <div v-if="!isEdit">{{ currentUserInfo_temp.nickname ? currentUserInfo_temp.nickname : '暂无' }}</div>
            <el-input v-else v-model="formInfo.nickname" size="small" clearable />
          </el-form-item>
          <el-form-item label="手机" prop="phone">
            <div v-if="!isEdit">{{ currentUserInfo_temp.phone ? currentUserInfo_temp.phone : '暂无' }}</div>
            <el-input v-else v-model="formInfo.phone" size="small" clearable />
          </el-form-item>
          <el-form-item label="简介" prop="description">
            <div v-if="!isEdit">{{ currentUserInfo_temp.description ? currentUserInfo_temp.description : '暂无' }}</div>
            <el-input v-else v-model="formInfo.description" size="small" clearable />
          </el-form-item>
          <el-form-item label="性别" prop="gex">
            <div v-if="!isEdit">
              <template>
                {{ currentUserInfo_temp.gex ? mapDictItemValue('genderStatus', currentUserInfo_temp.gex) : '暂无' }}
              </template>
            </div>
            <el-select v-else v-model="formInfo.gex" size="small" placeholder="请选择性别" clearable>
              <el-option v-for="({itemValue,id}) in genderStatus" :key="id" :label="itemValue" :value="id" />
            </el-select>
          </el-form-item>
          <el-form-item label="邮箱">
            <div>{{ currentUserInfo_temp.email ? currentUserInfo_temp.email : '暂无' }}</div>
          </el-form-item>
        </el-form>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { mapGetters, mapMutations, mapState } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import user from '@/api/user'
import dict from '@/api/dict'

export default {
  components: {
    Breadcrumb,
    Hamburger
  },
  data() {
    const validatePhone = (rule, value, callback) => {
      if (value.length < 1) {
        callback()
      }
      const verify = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/
      if (!verify.test(value)) {
        callback(new Error('请输入正确手机号'))
      } else {
        callback()
      }
    }
    return {
      drawerVisable: false,
      currentUserInfo_temp: {},
      formInfo: {},
      isEdit: false,
      ...dict.data(),
      rules: {
        // username: [{ required: true, trigger: ['blur', 'change'], message: '请输入用户姓名' }],
        // nickname: [{ required: false, trigger: ['blur', 'change'], message: '请输入用户昵称' }],
        phone: [{ required: false, trigger: 'blur', validator: validatePhone }]
      }
    }
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar'
    ])
  },

  created() {
    this.getInfo()
    this.getGenderStatus()
  },
  methods: {
    ...user.methods,
    ...dict.methods,
    ...mapState('user', ['userInfo']),
    ...mapMutations('user', ['SET_USERINFO']),
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async logout() {
      await this.$store.dispatch('user/logout')
      this.$router.push(`/login?redirect=${this.$route.fullPath}`)
      sessionStorage.removeItem('isFirst')
    },
    getInfo() {
      this.getCurrentUserInfo().then(res => {
        if (res.code === 200) {
          this.currentUserInfo_temp = res.result
          this.formInfo = { ...res.result }
          this.SET_USERINFO({ ...res.result })
        }
      })
    },
    updateUserInfo() {
      this.updateUser(this.formInfo).then(res => {
        this.isEdit = false
        this.getInfo()
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  height: 50px;
  overflow: hidden;
  position: relative;
  background: transparent;
  box-shadow: 0 1px 3px rgba(0, 21, 41, .08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, .025)
    }
  }

  .navbar-right {
    display: flex;
    align-items: center;

    .roles-list {
      font-size: 14px;
      color: #fff;

      span {
        padding: 8px;
        margin-right: 5px;
        border-radius: 15px;
        background: #409eff;
      }
    }
  }

  .right-menu {
    //float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s;

        &:hover {
          background: rgba(0, 0, 0, .025)
        }
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
