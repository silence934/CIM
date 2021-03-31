<template>
    <div>
        <div class="container"></div>

        <div class="login-container">
            <el-form v-if="features==='login'" ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" auto-complete="on"
                     label-position="left">

                <div class="title-container">
                    <h3 class="title">登 录</h3>
                </div>

                <el-form-item prop="username">
        <span class="svg-container">
          <svg-icon icon-class="user"/>
        </span>
                    <el-input
                            ref="username"
                            v-model="loginForm.username"
                            placeholder="Username"
                            name="username"
                            type="text"
                            tabindex="1"
                            auto-complete="on"
                    />
                </el-form-item>

                <el-form-item prop="password">
        <span class="svg-container">
          <svg-icon icon-class="password"/>
        </span>
                    <el-input
                            :key="passwordType"
                            ref="password"
                            v-model="loginForm.password"
                            :type="passwordType"
                            placeholder="Password"
                            name="password"
                            tabindex="2"
                            auto-complete="on"
                            @keyup.enter.native="handleLogin"
                    />
                    <span class="show-pwd" @click="showPwd">
          <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'"/>
        </span>
                </el-form-item>

                <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;"
                           @click.native.prevent="handleLogin">
                    登录
                </el-button>

                <el-link @click="features='register'">立即注册</el-link>
                <el-link @click="features='forget'" style="float: right">忘记密码</el-link>

            </el-form>

            <el-form v-else-if="features==='register'" ref="register" :model="registerForm"
                     :rules="registerRules" class="login-form" auto-complete="on" label-position="left">

                <div class="title-container">
                    <h3 class="title">注册</h3>
                </div>

                <el-form-item prop="username">
                <span class="svg-container">
                  <svg-icon icon-class="user"/>
                </span>
                    <el-input
                            v-model="registerForm.username"
                            placeholder="请输入账号"
                            name="username"
                            type="text"
                            tabindex="1"
                            auto-complete="on"
                    />
                </el-form-item>

                <el-form-item prop="password">
                <span class="svg-container">
                  <svg-icon icon-class="password"/>
                </span>
                    <el-input
                            :key="passwordType"
                            v-model="registerForm.password"
                            :type="passwordType"
                            placeholder="请输入密码"
                            name="password"
                            tabindex="2"
                            auto-complete="on"
                            @keyup.enter.native="handleLogin"
                    />
                    <span class="show-pwd" @click="showPwd">
                  <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'"/>
                </span>
                </el-form-item>

                <el-form-item prop="password1">
                <span class="svg-container">
                  <svg-icon icon-class="password"/>
                </span>
                    <el-input
                            :key="passwordType"
                            v-model="registerForm.password1"
                            :type="passwordType"
                            placeholder="请输入确认密码"
                            name="password"
                            tabindex="2"
                            auto-complete="on"
                            @keyup.enter.native="handleLogin"
                    />
                    <span class="show-pwd" @click="showPwd">
                  <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'"/>
                </span>
                </el-form-item>

                <el-form-item prop="phone">
                <span class="svg-container">
                  <svg-icon icon-class="phone"/>
                </span>
                    <el-input
                            v-model="registerForm.phone"
                            placeholder="请输入电话"
                            name="username"
                            type="text"
                            tabindex="1"
                            auto-complete="on"
                    />
                </el-form-item>

                <el-form-item prop="mail">
                <span class="svg-container">
                  <svg-icon icon-class="mail"/>
                </span>
                    <el-input
                            v-model="registerForm.mail"
                            placeholder="请输入邮箱"
                            name="username"
                            type="text"
                            tabindex="1"
                            auto-complete="on"
                    />
                </el-form-item>

                <el-form-item prop="question">
                <span class="svg-container">
                  <svg-icon icon-class="security"/>
                </span>
                    <el-select v-model="registerForm.question" placeholder="请选择密保问题">
                        <el-option value="我的母亲叫什么">我的母亲叫什么</el-option>
                        <el-option value="我的父亲叫什么">我的父亲叫什么</el-option>
                        <el-option value="我的小学班主任叫什么">我的小学班主任叫什么</el-option>
                        <el-option value="我的高中班主任叫什么">我的高中班主任叫什么</el-option>
                    </el-select>
                </el-form-item>

                <el-form-item prop="answer">
                <span class="svg-container">
                  <svg-icon icon-class="answer"/>
                </span>
                    <el-input
                            v-model="registerForm.answer"
                            placeholder="请输入答案"
                            type="text"
                            tabindex="1"
                            auto-complete="on"
                    />
                </el-form-item>

                <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;"
                           @click.native.prevent="register">注册
                </el-button>
                <el-link @click="features='login'">返回</el-link>
            </el-form>

            <el-form v-else ref="forget" :model="forgetForm" :rules="forgetRules" class="login-form" auto-complete="on" label-position="left">

                <div class="title-container">
                    <h3 class="title">忘记密码</h3>
                </div>

                <el-form-item prop="username">
                <span class="svg-container">
                  <svg-icon icon-class="user"/>
                </span>
                    <el-input
                            ref="username"
                            v-model="forgetForm.username"
                            placeholder="请输入账号"
                            name="username"
                            type="text"
                            tabindex="1"
                            auto-complete="on"
                    />
                </el-form-item>

                <el-form-item prop="mail">
                <span class="svg-container">
                  <svg-icon icon-class="mail"/>
                </span>
                    <el-input
                            v-model="forgetForm.mail"
                            placeholder="请输入邮箱"
                            name="username"
                            type="text"
                            tabindex="1"
                            auto-complete="on"
                    />
                    <el-link v-if="time===0" class="code" @click="getCode">获取验证码</el-link>
                    <el-link v-else class="code">{{ time }}s 后获取</el-link>
                </el-form-item>

                <el-form-item prop="code">
                <span class="svg-container">
                  <svg-icon icon-class="verification"/>
                </span>
                    <el-input
                            v-model="forgetForm.code"
                            placeholder="请输入验证码"
                            name="username"
                            type="text"
                            tabindex="1"
                            auto-complete="on"
                    />
                </el-form-item>

                <el-form-item prop="password">
                <span class="svg-container">
                  <svg-icon icon-class="password"/>
                </span>
                    <el-input
                            :key="passwordType"
                            v-model="forgetForm.password"
                            :type="passwordType"
                            placeholder="请输入密码"
                            name="password"
                            tabindex="2"
                            auto-complete="on"
                            @keyup.enter.native="handleLogin"
                    />
                    <span class="show-pwd" @click="showPwd">
                  <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'"/>
                </span>
                </el-form-item>

                <el-form-item prop="password1">
                <span class="svg-container">
                  <svg-icon icon-class="password"/>
                </span>
                    <el-input
                            :key="passwordType"
                            v-model="forgetForm.password1"
                            :type="passwordType"
                            placeholder="请输入确认密码"
                            name="password"
                            tabindex="2"
                            auto-complete="on"
                            @keyup.enter.native="handleLogin"
                    />
                    <span class="show-pwd" @click="showPwd">
                  <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'"/>
                </span>
                </el-form-item>

                <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;"
                           @click.native.prevent="submit">
                    确定
                </el-button>

                <el-link @click="features='login'">返回</el-link>
            </el-form>
        </div>
    </div>

</template>

<script>

import {getVerificationCode, registerUser, retrievePassword} from "@/api/user"

export default {
    name: 'Login',
    data() {
        return {
            features: 'login',
            loginForm: {
                username: 'admin',
                password: '123456'
            },
            loginRules: {
                username: [{min: 5, max: 10, message: '长度在 6 到 10 个字符', trigger: 'blur'}],
                password: [{min: 6, max: 10, message: '长度在 6 到 10 个字符', trigger: 'blur'}],
            },
            loading: false,
            passwordType: 'password',
            redirect: undefined,
            registerForm: {
                username: '',
                password: '',
                password1: '',
                phone: '',
                mail: '',
                question: '',
                answer: ''
            },
            registerRules: {
                username: [{min: 5, max: 10, message: '长度在 6 到 10 个字符', trigger: 'blur'}],
                password: [{min: 6, max: 10, message: '长度在 6 到 10 个字符', trigger: 'blur'}],
                password1: [{
                    required: true, trigger: 'blur', validator: (rule, value, callback) => {
                        if (value === '') {
                            callback(new Error('请再次输入密码'));
                        } else if (value !== this.registerForm.password) {
                            callback(new Error('两次输入密码不一致!'));
                        } else {
                            callback();
                        }
                    }
                }],
                phone: [{required: true, min: 11, max: 11, message: '请输入正确的手机号', trigger: 'blur'}],
                mail: [{type: 'email', required: true, message: "请输入正确的邮箱"}],
                question: [{required: true, message: '请输选择密保问题', trigger: 'blur'}],
                answer: [{required: true, message: '请输入密保问题答案', trigger: 'blur'}],
            },
            forgetForm: {
                username: '',
                password: '',
                password1: '',
                mail: '',
                code: ''
            },
            forgetRules: {
                username: [{min: 5, max: 10, message: '长度在 6 到 10 个字符', trigger: 'blur'}],
                password: [{min: 6, max: 10, message: '长度在 6 到 10 个字符', trigger: 'blur'}],
                password1: [{
                    required: true, trigger: 'blur', validator: (rule, value, callback) => {
                        if (value === '') {
                            callback(new Error('请再次输入密码'));
                        } else if (value !== this.forgetForm.password) {
                            callback(new Error('两次输入密码不一致!'));
                        } else {
                            callback();
                        }
                    }
                }],
                mail: [{type: 'email', required: true, message: "请输入正确的邮箱"}],
                code: [{required: true, message: '请输入验证码', trigger: 'blur'}],
            },
            time: 0
        }
    },
    watch: {
        $route: {
            handler: function (route) {
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
        handleLogin() {
            this.$refs.loginForm.validate(valid => {
                if (valid) {
                    this.loading = true
                    this.$store.dispatch('user/login', this.loginForm).then(() => {
                        this.$router.push({path: this.redirect || '/'})
                        this.loading = false
                    }).catch(() => {
                        this.loading = false
                    })
                } else {
                    console.log('error submit!!')
                    return false
                }
            })
        },
        register() {
            this.$refs.register.validate(valid => {
                if (valid) {
                    this.loading = true
                    registerUser(this.registerForm).then(res => {
                        this.$message.success("注册成功")
                        this.features = 'login'
                        this.registerForm = {}
                        this.loading = false
                    }).catch(() => {
                        this.loading = false
                    })
                } else {
                    return false
                }
            })
        },
        getCode() {
            getVerificationCode({username: this.forgetForm.username, mail: this.forgetForm.mail}).then(res => {
                this.$message.success("验证码已发送至您的邮箱")
                this.time = 60
                this.setTime()
            })
        },
        submit() {
            this.$refs.forget.validate(valid => {
                if (valid) {
                    this.loading = true
                    retrievePassword(this.forgetForm).then(res => {
                        this.$message.success("重置密码成功")
                        this.features = 'login'
                        this.forgetForm = {}
                        this.loading = false
                    }).catch(() => {
                        this.loading = false
                    })
                } else {
                    return false
                }
            })

        },
        setTime() {
            setTimeout(() => {
                this.time--
                if (this.time > 0) {
                    this.setTime()
                }
            }, 1000)
        }
    },
    mounted() {
        let getContainer = document.getElementsByClassName('container')[0];
        let createDiv = '';
        for (let i = 0; i < 5000; i++) {
            createDiv += '<div class=\'box\'></div>'
        }
        getContainer.innerHTML = createDiv;
    }
}
</script>

<style lang="scss">
/* 修复input 背景不协调 和光标变色 */
/* Detail see https://github.com/PanJiaChen/vue-element-admin/pull/927 */

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


* {
    margin: 0;
    padding: 0;

}

.container {
    width: 100vw;
    height: 100vh;
    background: #111;
    overflow: hidden;

    .box {
        width: 24px;
        height: 24px;
        border: 1px solid rgba(0, 0, 0, .2);
        box-sizing: border-box;
        float: left;
        position: relative;
    }

    .box::before {
        content: '';
        position: absolute;
        top: 4px;
        bottom: 4px;
        right: 4px;
        left: 4px;
        background: #1d1d1d;
        box-shadow: 0 1px 4px #000;
        transition: 5s ease-in-out;
    }

    .box:hover::before {
        transition: 0s ease-in-out;
    }

    .box:nth-child(9n+1):hover::before {
        background: #f00;
        box-shadow: 0 0 3px #f00, 0 0 10px #f00;
    }

    .box:nth-child(9n+2):hover::before {
        background: #00f;
        box-shadow: 0 0 3px #00f, 0 0 10px #00f;
    }

    .box:nth-child(9n+3):hover::before {
        background: #ff0;
        box-shadow: 0 0 3px #ff0, 0 0 10px #ff0;
    }

    .box:nth-child(9n+4):hover::before {
        background: #0f0;
        box-shadow: 0 0 3px #0f0, 0 0 10px #0f0;
    }

    .box:nth-child(9n+5):hover::before {
        background: #0ff;
        box-shadow: 0 0 3px #0ff, 0 0 10px #0ff;
    }

    .box:nth-child(9n+6):hover::before {
        background: #f0f;
        box-shadow: 0 0 3px #f0f, 0 0 10px #f0f;
    }

    .box:nth-child(9n+7):hover::before {
        background: #00ffad;
        box-shadow: 0 0 3px #00ffad, 0 0 10px #00ffad;
    }

    .box:nth-child(9n+8):hover::before {
        background: #e91e63;
        box-shadow: 0 0 3px #e91e63, 0 0 10px #e91e63;
    }

    .box:nth-child(9n+9):hover::before {
        background: #f45510;
        box-shadow: 0 0 3px #f45510, 0 0 10px #f45510;
    }
}


</style>

<style lang="scss" scoped>
$bg: #2d3a4b;
$dark_gray: #889aa4;
$light_gray: #eee;

.login-container {
    width: 520px;
    background-color: $bg;
    overflow: hidden;
    z-index: 100;
    position: absolute;
    top: 150px;
    left: 50%;
    transform: translate(-50%, 0);
    box-shadow: 0 2px 4px rgba(0, 0, 0, .12);

    .login-form {
        padding: 35px;
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

.code {
    position: absolute;
    right: -80px;
    top: 5px
}

> > > .el-select {
    width: 380px;

    .el-input__suffix {
        right: -85px;
    }
}
</style>
