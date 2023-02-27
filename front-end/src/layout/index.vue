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
      title="提示"
      :visible.sync="isFirst"
      width="20%"
      :before-close="handleClose"
      :close-on-click-modal="false"
    >
      <el-card shadow="hover">
        <div style="display: flex;justify-content: space-between">
          <span>今日未打卡</span>
          <el-button type="primary" size="small">打 卡</el-button>
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
      isFirst: null
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
    }
  },
  mounted() {
    this.showAttendance()
    // this.getIsAttendance()
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
      // const res = this.getCurrentUserTodayAttendanceInfo()
      // console.log(res, 8888888888888)
      // this.getCurrentUserTodayAttendanceInfo().then(res=>{
      //   console.log(res, 9999999)
      // })
    },
    handleClose() {
      sessionStorage.setItem('isFirst', 0)
      this.isFirst = sessionStorage.getItem('isFirst') == 1
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
</style>
