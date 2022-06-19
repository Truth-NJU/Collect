<template>
  <div :class="{'has-logo':showLogo}">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="variables.menuBg"
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
import { mapGetters } from 'vuex'
import Logo from './Logo'
import SidebarItem from './SidebarItem'
import variables from '@/styles/variables.scss'
import { getIdentity } from '../../../utils/auth'

export default {
  components: { SidebarItem, Logo },
  data() {
    return {
      routes: null
    }
  },
  created() {
    console.log(getIdentity())
    const router = this.$router.options.routes
    // console.log(router)
    const fliterRouter = []
    if (getIdentity() === '众包工人') {
      for (var i = 0; i < router.length; i++) {
        if (router[i].path !== '/taskForm' && router[i].path !== '/reports' && router[i].path !== '/reportForm' &&

          router[i].path !== '/reportDetail' && router[i].path !== '/admin' && router[i].path !== '/reportClassDetail') { fliterRouter.push(router[i]) }
      }
    } else if (getIdentity() === '发包方') {
      for (var j = 0; j < router.length; j++) {
        if (router[j].path !== '/reportForm' && router[j].path !== '/reports' && router[j].path !== '/reportDetail' && router[j].path !== '/admin' && router[j].path !== '/reportClassDetail') { fliterRouter.push(router[j]) }
      }
    } else if (getIdentity() === '管理员') {
      for (var m = 0; m < router.length; m++) {
        if (router[m].path !== '/reportForm' && router[m].path !== '/reports' && router[m].path !== '/myTask' &&
          router[m].path !== '/taskForm' && router[m].path !== '/reportDetail' && router[m].path !== '/reportClassDetail') { fliterRouter.push(router[m]) }
      }
    } else {
      for (var n = 0; n < router.length; n++) {
        if (router[n].path !== '/reportForm' && router[n].path !== '/reports' && router[n].path !== '/myTask' &&
          router[n].path !== '/taskForm' && router[n].path !== '/reportDetail' && router[n].path !== '/admin' && router[n].path !== '/reportClassDetail') { fliterRouter.push(router[n]) }
      }
    }
    this.routes = fliterRouter
  },
  computed: {
    ...mapGetters([
      'sidebar'
    ]),
    // routes() {
    //   // 过滤路由，不同的身份显示不同的页面
    //   const router = this.$router.options.routes
    //   // console.log(router)
    //   const fliterRouter = []
    //   if (getIdentity() === '众包工人') {
    //     for (var i = 0; i < router.length; i++) {
    //       if (router[i].path !== '/taskForm' && router[i].path !== '/reports') { fliterRouter.push(router[i]) }
    //     }
    //   } else if (getIdentity() === '发包方') {
    //     for (var j = 0; j < router.length; j++) {
    //       if (router[j].path !== '/reportForm' && router[j].path !== '/reports') { fliterRouter.push(router[j]) }
    //     }
    //   } else {
    //     for (var m = 0; m < router.length; m++) {
    //       if (router[m].path !== '/reportForm' && router[m].path !== '/reports' && router[m].path !== '/myTask' &&
    //         router[m].path !== '/taskForm') { fliterRouter.push(router[m]) }
    //     }
    //   }
    //   console.log(fliterRouter)
    //   return fliterRouter
    // },
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
