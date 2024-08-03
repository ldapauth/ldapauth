<template>
    <div class="login" v-if="bindShow">
      <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input
              v-model="loginForm.username"
              type="text"
              size="large"
              auto-complete="off"
              :placeholder="t('login.username')"
          >
            <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              size="large"
              auto-complete="off"
              :placeholder="t('login.password')"
              @keyup.enter="submitForm"
          >
            <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
          </el-input>
        </el-form-item>

        <el-form-item prop="captcha" v-if="captchaEnabled">
          <el-input
              v-model="loginForm.captcha"
              size="large"
              auto-complete="off"
              :placeholder="t('login.code')"
              @keyup.enter="submitForm"
              style="width: 63%"
          >
            <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
          </el-input>
          <div class="login-code">
            <img :src="codeUrl" @click="getCode" class="login-code-img"/>
          </div>
        </el-form-item>

        <el-form-item style="width:100%;">
          <el-button :loading="loading" size="medium" type="primary" style="width:100%;" @click="submitForm">提交</el-button>
        </el-form-item>
        <el-form-item>
          <h3 style="width: 100%;">{{t('login.callBack.tips')}}</h3>
          <ul class="topicList">
            <li>{{t('login.callBack.tipsMessage1')}}</li>
            <li>{{t('login.callBack.tipsMessage2')}}</li>
            <li>{{t('login.callBack.tipsMessage3')}}</li>
          </ul>
        </el-form-item>
      </el-form>
    </div>
</template>
<script setup name="CallBack">

import {ref} from 'vue'
import { useI18n } from 'vue-i18n'
import useUserStore from "@/store/modules/user.js";
import {callbackLogin, getCodeImg, loginPreGet} from "@/api/login.js";

const { t } = useI18n()

const userStore = useUserStore()
const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();

// 验证码开关
const captchaEnabled = ref(false);

const codeUrl = ref("");
const loading = ref(false);
const bindShow = ref(false);
const tempCode = ref(undefined);
const state = ref(undefined);

const loginForm = ref({
  username: "",
  password: "",
  rememberMe: false,
  captcha: "",
  state: ""
});

const loginRules = {
  username: [{ required: true, trigger: "blur", message: t('login.usernameMessage') }],
  password: [{ required: true, trigger: "blur", message: t('login.passwordMessage') }],
  captcha: [{ required: true, trigger: "change", message: t('login.codeMessage') }]
};

function submitForm(){
    proxy.$refs["loginRef"].validate(valid => {
      if (valid) {
        loading.value = true;
        loginForm.value.sTempCode = tempCode.value;
        loginForm.value.state = state.value;
        // 调用action的登录方法
        userStore.login(loginForm.value).then(() => {
          window.close();
        }).catch(() => {
          loading.value = false;
          // 重新获取验证码
          if (captchaEnabled.value) {
            getCode();
          }
        }).finally(() => {
          loading.value = false
        });
      }
    });
}

function load(){
  loginPreGet().then(resState => {
    if (resState.success) {

      state.value = resState.data.state;
      captchaEnabled.value = resState.data.captcha;

      if (captchaEnabled.value) {
        getCode();
      }

      const query = route.query;
      if (query.id) {
        callbackLogin(query).then(res => {
          if (res.code == 200) {
            userStore.setCredential(res.data)
            window.close();
          }
          //需要绑定用户
          else if (res.code == 1000001) {
            tempCode.value = res.data;
            //显示页面
            bindShow.value = true;
          }
        });
      }
    }
  });
}

function getCode() {
  getCodeImg(state.value).then(res => {
    if (captchaEnabled.value) {
      codeUrl.value = res.data.image;
      state.value = res.data.state
    }
  });
}

load();
</script>

<style scoped lang="scss">


.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-color: #FFFFFF;
  background-size: cover;
}

.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.login-form {
  border-radius: 4px;
  border: 1px solid #e8e8e8;
  background: #f8f8f8;
  width: 600px;
  padding: 25px 25px 5px 25px;

  .el-input {
    height: 38px;

    input {
      height: 38px;
    }
  }

  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}

.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}

.login-code {
  width: 30%;
  margin-left: 3%;
  height: 38px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
  }
}

.el-login-footer {
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}

.login-code-img {
  height: 38px;
}
</style>

<style lang="scss" scoped>
.button-account {
  width: 100%;
  height: 40px;
  padding: 0;
  //控制图标icon大小
  i {
    font-size: 14px;
  }
  //控制字大小
  ::v-deep  {
    font-size: 14px;
  }
}

/*按钮悬浮*/
.button-account:hover {
  background: white !important;
  color: orange !important;
  border-color: lightgrey !important;
}
.button-select {
  background: white !important;
  color: orange !important;
  border-color: lightgrey !important;
  text-decoration: underline;
}
</style>

