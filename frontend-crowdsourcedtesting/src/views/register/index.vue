<template>
  <div class="login-container">
    <el-form
      ref="loginForm"
      :model="registerForm"
      :rules="registerRules"
      class="login-form"
      auto-complete="on"
      label-position="left"
    >

      <div class="title-container">
        <h3 class="title">注册</h3>
      </div>

      <el-form-item prop="username">
        <span class="svg-container">
          <i class="el-icon-message" />
        </span>
        <el-input
          ref="username"
          v-model="registerForm.username"
          placeholder="请输入邮箱"
          name="username"
          type="text"
          tabindex="1"
          auto-complete="on"
        />
      </el-form-item>
      <el-form-item prop="name">
        <span class="svg-container">
          <svg-icon icon-class="user" />
        </span>
        <el-input
          v-model="registerForm.name"
          placeholder="请输入用户名"
          name="name"
          type="text"
          tabindex="1"
          auto-complete="on"
        />
      </el-form-item>
      <el-form-item prop="password">
        <span class="svg-container">
          <svg-icon icon-class="password" />
        </span>
        <el-input
          :key="passwordType"
          ref="password"
          v-model="registerForm.password"
          :type="passwordType"
          placeholder="请输入密码"
          name="password"
          tabindex="2"
          auto-complete="on"
        />
        <span class="show-pwd" @click="showPwd">
          <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
        </span>
      </el-form-item>
      <password-strength v-model="registerForm.password" />
      <el-form-item prop="password_again">
        <span class="svg-container">
          <svg-icon icon-class="password" />
        </span>
        <el-input
          :key="passwordType"
          ref="password_again"
          v-model="registerForm.password_again"
          :type="passwordType"
          name="password_again"
          placeholder="请再次输入密码"
          tabindex="3"
          auto-complete="on"
        />
      </el-form-item>
      <el-form-item style="width: fit-content">
        <span class="svg-container">
          <i class="el-icon-user-solid" />
        </span>
        <el-select v-model="identity" placeholder="选择注册身份">
          <el-option
            v-for="item in identities"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item v-if="identity===2" prop="code">
        <span class="svg-container">
          <i class="el-icon-notebook-1" />
        </span>
        <el-input
          :key="passwordType"
          ref="code"
          v-model="registerForm.code"
          :type="passwordType"
          name="code"
          placeholder="请输入验证码"
          tabindex="4"
          auto-complete="on"
        />
      </el-form-item>
      <div style="display: flex;justify-content: center;margin-bottom:30px;">
        <el-button type="primary" style="margin-right: 15px;width: 100%" @click.native.prevent="register">
          注册
        </el-button>
        <el-button type="primary" style="width: 100%" @click="() => {this.$router.push({path:'/login'})}">
          取消
        </el-button>
      </div>
    </el-form>

  </div>
</template>

<script>

import PasswordStrength from '@/components/PasswdStrength/index'

import { validUsername } from '@/utils/validate'
import request from '@/utils/request'

export default {
  name: 'Login',
  components: { PasswordStrength },
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!validUsername(value)) {
        callback(new Error('请输入正确的邮格式'))
      } else {
        callback()
      }
    }
    const validateName = (rule, value, callback) => {
      if (value.length===0||value.length>10) {
        callback(new Error('用户名长度应大于0小于10'))
      } else {
        callback()
      }

    }
    const validatePassword = (rule, value, callback) => {
      if (value.length < 8) {
        callback(new Error('密码应大于8位'))
      } else {
        callback()
      }
    }
    const samePassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次密码不一致'))
      } else {
        callback()
      }
    }
    const validateCode = (rule, value, callback) => {
      if (value.length === 0) {
        callback(new Error('验证码不能为空'))
      } else {
        callback()
      }
    }
    return {
      registerForm: {
        username: '',
        name: '',
        password: '',
        password_again: '',
        code: ''
      },
      registerRules: {
        username: [{ required: true, trigger: 'change', validator: validateUsername }],
        name: [{ required: true, trigger: 'change', validator: validateName }],
        password: [{ required: true, trigger: 'change', validator: validatePassword }],
        password_again: [{ required: true, trigger: 'blur', validator: samePassword }],
        code: [{ required: true, trigger: 'blur', validator: validateCode }]
      },
      identity: 0,
      identities: [
        {
          value: 0,
          label: '众包工人'
        },
        {
          value: 1,
          label: '发包方'
        },
        { value: 2,
          label: '管理员'
        }
      ],
      loading: false,
      passwordType: 'password',
      redirect: undefined
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
    register() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loading) {
            request({
              method: 'post',
              url: '/collect/user/register',
              data: {
                email: this.registerForm.username,
                name:this.registerForm.name,
                passwd: this.registerForm.password,
                code: this.registerForm.code,
                userIdentity: this.identity
              }
            }).then(res => {
              this.$router.push({ path: '/login' })
            })
          }
        } else {
          this.$message({ type: 'error', message: '创建格式有误！', duration: 1000 })
          return false
        }
      })
    }
  }
}
</script>

<style lang="scss">

$bg: #283443;
$light_gray: #fff;
$cursor: #fff;

@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
  .login-container .el-input input {
    color: $cursor;
  }
}

/* reset element-ui css */
.login-container {
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;

    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      color: $light_gray;
      height: 47px;
      caret-color: $cursor;

      &:-webkit-autofill {
        box-shadow: 0 0 0px 1000px $bg inset !important;
        -webkit-text-fill-color: $cursor !important;
      }
    }
  }

  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }
}
</style>

<style lang="scss" scoped>
$bg: #2d3a4b;
$dark_gray: #889aa4;
$light_gray: #eee;

.login-container {
  min-height: 100%;
  width: 100%;
  background-color: $bg;
  overflow: hidden;

  .login-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 160px 35px 0;
    margin: 0 auto;
    overflow: hidden;
  }

  .tips {
    font-size: 14px;
    color: #fff;
    margin-bottom: 10px;

    span {
      &:first-of-type {
        margin-right: 16px;
      }
    }
  }

  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .title-container {
    position: relative;

    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0px auto 40px auto;
      text-align: center;
      font-weight: bold;
    }
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }
}
</style>
