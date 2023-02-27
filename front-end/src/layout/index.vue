<template>
  <div :class="classObj" class="app-wrapper">
    <div v-if="device==='mobile'&&sidebar.opened" class="drawer-bg" @click="handleClickOutside" />
    <sidebar />
    <div class="main-container">
      <div :class="{'fixed-header':fixedHeader}">
        <navbar />
      </div>
      <app-main />
    </div>
    <el-dialog
      title="打卡"
      :visible.sync="isFirst"
      width="20%"
      :before-close="handleClose"
      :close-on-click-modal="false"
    >
      <el-card shadow="hover">
        <div style="display: flex;justify-content: space-between;align-items: center">
          <div class="card-box" v-if="!isAttendance">
            <i class="el-icon-close card-check"></i>
            今日未打卡</div>
          <div class="card-box" v-else>
            <i class="el-icon-check card-check"></i>
            今日已打卡</div>
          <el-button v-if="!isAttendance" type="primary" size="small" style="height: 30px" @click="fetchAttendance">打 卡</el-button>
        </div>
      </el-card>
    </el-dialog>
  </div>
</template>

<script>
import { Navbar, Sidebar, AppMain } from './components'
import ResizeMixin from './mixin/ResizeHandler'
import attendance from '@/api/attendance'
export default {
  name: 'Layout',
  components: {
    Navbar,
    Sidebar,
    AppMain
  },
  mixins: [ResizeMixin],
  data() {
    return {
      isFirst: null,
      isAttendance: null
    }
  },
  computed: {
    sidebar() {
      return this.$store.state.app.sidebar
    },
    device() {
      return this.$store.state.app.device
    },
    fixedHeader() {
      return this.$store.state.settings.fixedHeader
    },
    classObj() {
      return {
        hideSidebar: !this.sidebar.opened,
        openSidebar: this.sidebar.opened,
        withoutAnimation: this.sidebar.withoutAnimation,
        mobile: this.device === 'mobile'
      }
    },
    dialogVisiable() {
      return this.isFirst === true && this.isAttendance == false
    }
  },
  created() {
    this.showAttendance()
    this.getIsAttendance()
  },
  methods: {
    ...attendance.methods,
    handleClickOutside() {
      this.$store.dispatch('app/closeSideBar', { withoutAnimation: false })
    },
    showAttendance() {
      this.isFirst = sessionStorage.getItem('isFirst') == 1
    },
    getIsAttendance() {
      this.getCurrentUserTodayAttendanceInfo().then(res => {
        this.isAttendance = res
      })
    },
    handleClose() {
      sessionStorage.setItem('isFirst', 0)
      this.isFirst = sessionStorage.getItem('isFirst') == 1
    },
    fetchAttendance() {
      this.createAttendance().then(res => {
        if (res.code === 200) {
          this.$message.success('今日打卡成功')
          this.getIsAttendance()
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
  @import "~@/styles/mixin.scss";
  @import "~@/styles/variables.scss";

  .app-wrapper {
    @include clearfix;
    position: relative;
    height: 100%;
    width: 100%;
    background: linear-gradient(136deg, #fff4f4 1%, #f0f1f2 42%, #ecf5ff 100%);
    &.mobile.openSidebar{
      position: fixed;
      top: 0;
    }
  }
  .drawer-bg {
    //background: #000;
    opacity: 0.3;
    width: 100%;
    top: 0;
    height: 100%;
    position: absolute;
    z-index: 999;
  }

  .fixed-header {
    position: fixed;
    top: 0;
    right: 0;
    z-index: 9;
    width: calc(100% - #{$sideBarWidth});
    transition: width 0.28s;
  }

  .hideSidebar .fixed-header {
    width: calc(100% - 54px)
  }

  .mobile .fixed-header {
    width: 100%;
  }
  .sidebar-container {
    background: transparent;
  }
  .card-box {
    display: flex;
    align-items: center;
    color:#2b78eb;
    .card-check {
      font-size: 30px;
      width: 40px;
      line-height: 40px;
      text-align: center;
      border-radius: 50%;
      border: 1px solid #2b78eb;

      margin: 0 10px;
    }
  }
</style>
