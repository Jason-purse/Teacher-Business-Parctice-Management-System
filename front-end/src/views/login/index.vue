<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-pic">
        <img src="./images/bg.png">
        <div class="title">教师企业实战管理</div>
      </div>
      <div class="login-form">
        <div class="tab-box">
          <div v-for="(item,index) in tabList" :key="index" class="tab-item cu" :class="tabValue === item.value?'active':''" @click="testTabClick(item.value)">
            {{ item.name }}
          </div>
        </div>
        <div class="login">
          <el-form ref="loginForm" :model="tabValue === 'account' ? loginForm : registerForm" :rules="loginRules">
            <template v-if="tabValue === 'account'">
              <el-form-item prop="username">
                <el-input v-model="loginForm.username" prefix-icon="el-icon-user" placeholder="账号" clearable />
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="loginForm.password" show-password type="password" prefix-icon="el-icon-lock" placeholder="密码" clearable />
              </el-form-item>
            </template>
            <template v-else>
              <el-form-item prop="email">
                <el-input v-model="registerForm.email" prefix-icon="el-icon-user" placeholder="邮箱" clearable />
              </el-form-item>
              <el-form-item prop="username">
                <el-input v-model="registerForm.username" placeholder="姓名" clearable />
              </el-form-item>
              <el-form-item prop="nickname">
                <el-input v-model="registerForm.nickname" placeholder="昵称" clearable />
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="registerForm.password" show-password type="password" prefix-icon="el-icon-lock" placeholder="密码" clearable />
              </el-form-item>
            </template>
          </el-form>
          <div class="tips">
            密码为8-16位大小写字母、数字至少两种组合，不可包含空格、中文，特殊符号等字符
          </div>
          <el-button v-if="tabValue === 'account'" type="primary" size="small" style="width: 100%" @click="handleLogin">登 录</el-button>
          <el-button v-else type="primary" size="small" style="width: 100%" @click="handleRegister">注 册</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import user from '@/api/user'
import { mapMutations } from 'vuex'
import { setRoles } from '@/utils/auth'
export default {
  name: 'Login',
  data() {
    const validateEmail = (rule, value, callback) => {
      const verify = /^\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/
      if (!verify.test(value)) {
        callback(new Error('邮箱格式错误, 请重新输入'))
      } else {
        callback()
      }
    }
    return {
      loginForm: {
        username: 'test',
        password: '123456'
      },
      registerForm: {
        username: '',
        nickname: '',
        email: '',
        password: ''
      },
      loginRules: {
        username: [{ required: true, trigger: 'blur', message: '请输入用户账号' }],
        password: [{ required: true, trigger: 'blur', message: '请输入用户密码' }],
        nickname: [{ required: true, trigger: 'blur', message: '请输入用户昵称' }],
        email: [{ required: true, trigger: 'blur', validator: validateEmail }]
      },
      loading: false,
      passwordType: 'password',
      redirect: undefined,
      tabValue: 'account',
      tabList: [
        {
          name: '账号密码',
          value: 'account'
        },
        {
          name: '注册',
          value: 'register'
        }
      ]
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  methods: {
    ...user.methods,
    ...mapMutations('user', ['SET_USERINFO']),
    /**
     * 切换tab
     * @param {*} value
     */
    testTabClick(value) {
      // this.$refs['account'].clearValidate()
      // this.$refs['phone'].clearValidate()
      // this.$refs['change'].clearValidate()
      this.tabValue = value
      this.$refs.loginForm.clearValidate()
    },
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store.dispatch('user/login', this.loginForm).then(() => {
            const component = this.$message.success('登陆成功 !!!')
            sessionStorage.setItem('isFirst', 1)
            this.loading = false
            this.getCurrentUserInfo().then(({ result }) => {
              this.userInfo = result
              this.formInfo = { ...result }
              console.log((result.roles || []).map(ele => ele.id))
              setRoles((result.roles || []).map(ele => ele.itemType))
              this.SET_USERINFO({ ...result })
            }).finally(() => {
              setTimeout(() => {
                // this.$router.push({ path: this.redirect || '/' })
                this.$router.push('/')
                component.close()
              }, 500)
            })
          }).catch(() => {
            this.loading = false
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    handleRegister() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.registerUser(this.registerForm).then(res => {
            if (res.code === 200) {
              this.$message.success('注册用户成功!')
              this.tabValue = 'account'
              this.registerForm = {
                username: '',
                nickname: '',
                email: '',
                password: ''
              }
            }
          })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
$bg: #2d3a4b;
$dark_gray: #889aa4;
$light_gray: #eee;

.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100%;
  height: 100%;
  width: 100%;
  background: linear-gradient(136deg, #fff4f4 1%, #f0f1f2 42%, #ecf5ff 100%);
  overflow: hidden;

  .login-container {
    display: flex;
    //width: 50%;
    height: 482px;

    .login-pic {
      width: 390px;
      height: 100%;
      margin-right: 20px;
      img {
        width: 390px;
        height: 318px;
      }
      .title {
        width: 100%;
        text-align: center;
        color: #303133;
        font-size: 24px;
        font-weight: 600;
        margin-top: 20px;
      }
    }
  }

}
.login-form {
  width: 320px;
  //height: 70%;
  height: fit-content;
  padding: 30px 20px;
  background: #fff;
  border-radius: 10px;
  .tab-box {
    display: flex;
    margin-bottom: 20px;
    .tab-item {
      flex-shrink: 0;
      position: relative;
      transition: all 0.2s linear;
      margin-right: 30px;
      cursor: pointer;
      &::after {
        transition: all 0.2s linear;
        transform: translateX(-50%) scaleX(0);
        content: "";
        width: 20px;
        position: absolute;
        left: 50%;
        bottom: -4px;
        border-bottom: 2px solid #409eff;
        border-radius: 4px;
      }
      &.active {
        color: #409eff;
        font-weight: 600;
        &::after {
          content: "";
          width: 20px;
          position: absolute;
          left: 50%;
          transform: translateX(-50%) scaleX(1);
          bottom: -4px;
          border-bottom: 2px solid #409eff;
        }
      }
    }
  }
  .login {
    .tips {
      color: #909399;
      line-height: 18px;
      font-size: 12px;
      margin: 20px 0;
    }
  }
}
::v-deep .el-form-item {
  height: 40px;
  width: 280px;
  border-radius: 5px;
  border: none;
  .el-form-item__content {
    width: 100%;
    height: 100%;
  }
  .el-input {
    width: 100%;
    height: 100%;
    padding: 0;
    input {
      width: 100%;
      height: 100%;
      color: #000;
      padding: 5px 25px;
      box-sizing: border-box;
      background: #ecf5ff;
      border-radius: 5px;
    }
  }
}
</style>
