<template>
  <div class="sidebar-container">
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :text-color="variables.menuText"
        :unique-opened="false"
        :active-text-color="variables.menuActiveText"
        :collapse-transition="false"
        mode="vertical"
      >
        <sidebar-item v-for="route in routes" :key="route.path" :item="route" :base-path="route.path" />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex'
import Logo from './Logo'
import SidebarItem from './SidebarItem'
import variables from '@/styles/variables.scss'

export default {
  components: { SidebarItem, Logo },
  computed: {
    ...mapGetters([
      'sidebar'
    ]),
    ...mapState('user', ['userInfo']),
    routes() {
      const roles = sessionStorage.getItem('roles')
      // const roles = [ 'teacher', 'auditor' ]
      // if (roles.length == 0) {
      //   this.$router.push('/nothing')
      //   return []
      // }
      // let newRoute = this.$router.options.routes.filter(item => item.path == '/')[0].children
      // newRoute = newRoute.filter(item => {
      //   return roles.some(it => {
      //     return item.meta.roles.includes(it)
      //   })
      // })
      // return newRoute
      return this.$router.options.routes
    },
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    showLogo() {
      return this.$store.state.settings.sidebarLogo
    },
    variables() {
      return variables
    },
    isCollapse() {
      return !this.sidebar.opened
    }
  }
}
</script>

<style lang="scss" scoped>
.sidebar-container {
  background: transparent;
  box-shadow: 0 2px 5px rgba(0, 21, 41, .08);
}

::v-deep .el-menu {
  background: transparent;

  .el-submenu .el-menu-item {
    background: transparent;
  }

  .el-submenu__title:hover {
    background: rgba(236, 245, 255, .8);
  }

  .el-menu-item.is-active {
    background: rgba(232, 240, 254, .8);
  }

  .el-submenu__title *, .el-menu-item * {
    color: #000;
  }
}
</style>
