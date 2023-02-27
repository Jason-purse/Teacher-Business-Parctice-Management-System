<template>
  <div class="navbar">
    <hamburger :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

    <breadcrumb class="breadcrumb-container" />

    <div class="right-menu">
      <el-dropdown class="avatar-container" trigger="click">
        <div class="avatar-wrapper">
          <!--          <img :src="avatar+'?imageView2/1/w/80/h/80'" class="user-avatar">-->
          <el-avatar class="sub-title" icon="el-icon-user-solid" />
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown" class="user-dropdown">
          <el-dropdown-item>
            <div @click="drawerVisable = !drawerVisable">个人主页</div>
          </el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">
            <span style="display:block;">退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
    <el-drawer
      title="个人主页"
      :visible.sync="drawerVisable"
      size="20%"
    >
      <el-form ref="form" :model="userInfo" label-width="80px">
        <el-form-item label="姓名">
          <div>{{ userInfo.username }}</div>
          <!--          <el-input v-model="userInfo.username" />-->
        </el-form-item>
        <el-form-item label="昵称">
          <div>{{ userInfo.nickname ? userInfo.nickname : '暂无' }}</div>
        </el-form-item>
        <el-form-item label="手机">
          <div>{{ userInfo.phone ? userInfo.phone : '暂无' }}</div>
        </el-form-item>
        <el-form-item label="邮箱">
          <div>{{ userInfo.email ? userInfo.email : '暂无'}}</div>
          <!--          <el-input v-model="userInfo.email" />-->
        </el-form-item>
        <el-form-item label="简介">
          <div>{{ userInfo.description ? userInfo.description : '暂无'}}</div>
          <!--          <el-input v-model="userInfo.description" />-->
        </el-form-item>
        <el-form-item label="性别">
          <div>{{ userInfo.gex ? userInfo.gex : '暂无'}}</div>
        </el-form-item>
      </el-form>
    </el-drawer>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import user from '@/api/user'

export default {
  components: {
    Breadcrumb,
    Hamburger
  },
  data() {
    return {
      drawerVisable: false,
      userInfo: {}
    }
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar'
    ])
  },
  mounted() {
    this.getInfo()
  },
  methods: {
    ...user.methods,
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async logout() {
      await this.$store.dispatch('user/logout')
      this.$router.push(`/login?redirect=${this.$route.fullPath}`)
    },
    getInfo() {
      this.getCurrentUserInfo().then(res => {
        console.log(res, 'info')
        if (res.code === 200) {
          this.userInfo = res.result
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: transparent;
  box-shadow: 0 1px 3px rgba(0,21,41,.08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color:transparent;

    &:hover {
      background: rgba(0, 0, 0, .025)
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .right-menu {
    float: right;
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
