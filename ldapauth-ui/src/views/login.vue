<template>
  <div class="lang">
    <span style="margin-right: 10px">{{localeLabel}}</span>
    <language class="right-menu-item hover-effect"></language>
  </div>
  <div class="login">
    <div class="left">
      <h2>{{ t('login.title') }}</h2>
      <p>{{ t('login.zitext') }}</p>
      <img src="../assets/images/bg.png" />
    </div>
    <div class="rigjt">
      <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
        <h3 class="title">
          <img :src="staticAppInfo.logo" alt="" />
          {{ staticAppInfo.title || t('login.title') }}
        </h3>
        <el-form-item prop="username">
          <el-input
              v-model="loginForm.username"
              type="text"
              size="large"
              auto-complete="off"
              :placeholder="t('login.username')"
          >
            <template #prefix>
              <svg-icon icon-class="user" class="el-input__icon input-icon"/>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              size="large"
              auto-complete="off"
              :placeholder="t('login.password')"
              @keyup.enter="handleLogin"
          >
            <template #prefix>
              <svg-icon icon-class="password" class="el-input__icon input-icon"/>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code" v-if="captchaEnabled">
          <el-input
              v-model="loginForm.captcha"
              size="large"
              auto-complete="off"
              :placeholder="t('login.code')"
              style="width: 63%"
              @keyup.enter="handleLogin"
          >
            <template #prefix>
              <svg-icon icon-class="validCode" class="el-input__icon input-icon"/>
            </template>
          </el-input>
          <div class="login-code">
            <img :src="codeUrl" @click="getCode" class="login-code-img" alt=""/>
          </div>
        </el-form-item>
        <div class="operater-wrapper">
          <el-checkbox v-model="loginForm.rememberMe">
            <span class="rememberMe">{{ $t('login.rememberMe') }}</span>
          </el-checkbox>
          <router-link to="/forgot" class="find-password">
            {{ t('forgot.title') }}
          </router-link>
        </div>
        <el-form-item style="width:100%;">
          <el-button
              class="login-btn"
              :loading="loading"
              size="large"
              type="primary"
              style="width:100%;"
              @click.prevent="handleLogin">
            <span v-if="!loading">{{ t("login.login") }}</span>
            <span v-else>{{ t("login.logging") }}</span>
          </el-button>
        </el-form-item>
        <el-form-item style="width:100%;" v-if="others.length > 0">
          <div class="el-divider el-divider--horizontal" style="--el-border-style: solid;">
            <div class="el-divider__text is-center">{{ t('login.other') }}</div>
          </div>

          <div class="other" style="width: 100%;display: flex;justify-content: space-around;">
            <div class="item" v-for="other in others" @click="openOtherLogin(other.id)" :title="other.name"
                 style="cursor: pointer">
              <img :src="privateImage(other.icon)" style="width: 28px;height: 28px" alt=""/>
            </div>
          </div>
        </el-form-item>
      </el-form>
    </div>
    <!--  底部  -->
    <div class="el-login-footer">
      <span style="color:#000">{{ staticAppInfo.copyright }}</span>
    </div>
  </div>
</template>

<script setup>
import Language from '@/components/Language'
import {getCodeImg, loginPreGet, getThirdById} from "@/api/login";
import {privateImage} from "@/utils/privateImages.js";

import Cookies from "js-cookie";
import {encrypt, decrypt} from "@/utils/jsencrypt";
import useUserStore from '@/store/modules/user'
import appStore from '@/store/modules/app'
import {useI18n} from 'vue-i18n'

const {t} = useI18n()
import {getToken} from '@/utils/auth'
import {getLangList} from "@/languages/index.js";

const userStore = useUserStore()
const route = useRoute();
const router = useRouter();
const {proxy} = getCurrentInstance();

const loginForm = ref({
  username: "",
  password: "",
  rememberMe: false,
  captcha: "",
  state: ""
});

const loginRules = {
  username: [{required: true, trigger: "blur", message: t('login.usernameMessage')}],
  password: [{required: true, trigger: "blur", message: t('login.passwordMessage')}],
  captcha: [{required: true, trigger: "change", message: t('login.codeMessage')}]
};
const state = ref("");
const localeLabel = ref("中文");
const {locale} = useI18n()
const languages = getLangList()
languages.forEach( item =>{
  if (item.value == locale.value) {
    localeLabel.value = item.label;
  }
})

const staticAppInfo = ref({});
const codeUrl = ref("");
const loading = ref(false);
// 验证码开关
const captchaEnabled = ref(false);
// 注册开关
const register = ref(false);
const redirect = ref(undefined);
//其他登录方式
const others = ref([]);

watch(route, (newRoute) => {
  redirect.value = newRoute.query && newRoute.query.redirect;
}, {immediate: true});

function handleLogin() {
  proxy.$refs.loginRef.validate(valid => {
    if (valid) {
      loading.value = true;
      // 勾选了需要记住密码设置在 cookie 中设置记住用户名和密码
      if (loginForm.value.rememberMe) {
        Cookies.set("username", loginForm.value.username, {expires: 30});
        Cookies.set("password", encrypt(loginForm.value.password), {expires: 30});
        Cookies.set("rememberMe", loginForm.value.rememberMe, {expires: 30});
      } else {
        // 否则移除
        Cookies.remove("username");
        Cookies.remove("password");
        Cookies.remove("rememberMe");
      }
      loginForm.value.state = state.value;
      // 调用action的登录方法
      userStore.login(loginForm.value).then(() => {
        const query = route.query;
        const otherQueryParams = Object.keys(query).reduce((acc, cur) => {
          if (cur !== "redirect") {
            acc[cur] = query[cur];
          }
          return acc;
        }, {});
        if (redirect.value && redirect.value.startsWith("http")) {
          location.replace(redirect.value)
          return;
        }
        router.push({path: redirect.value || "/", query: otherQueryParams});
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

function getCode() {
  getCodeImg(state.value).then(res => {
    if (captchaEnabled.value) {
      codeUrl.value = res.data.image;
      state.value = res.data.state
    }
  });
}

function getCookie() {
  const username = Cookies.get("username");
  const password = Cookies.get("password");
  const rememberMe = Cookies.get("rememberMe");
  loginForm.value = {
    username: username === undefined ? loginForm.value.username : username,
    password: password === undefined ? loginForm.value.password : decrypt(password),
    rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
  };
}

function getState() {
  loginPreGet().then(res => {
    if (res.success) {
      staticAppInfo.value = res.data
      staticAppInfo.value.logo = import.meta.env.VITE_APP_BASE_API + staticAppInfo.value.logo;
      appStore().setAppInfo(staticAppInfo.value)
      state.value = res.data.state
      captchaEnabled.value = res.data.captcha
      if (captchaEnabled.value) {
        getCode();
      }
      others.value = res.data.auths;
    }
  });
}

function openOtherLogin(id) {
  getThirdById(id).then(res => {
    if (res.code === 200) {
      var flag = window.open(res.data, '', "height=800, width=1400, top=100, left=100,toolbar=no, menubar=no, scrollbars=no, resizable=no, loca tion=no, status=no")
      var than2 = this;
      var loop = setInterval(function () {
        if (flag.closed) {
          clearInterval(loop);
          if (getToken()) {
            const query = route.query;
            const otherQueryParams = Object.keys(query).reduce((acc, cur) => {
              if (cur !== "redirect") {
                acc[cur] = query[cur];
              }
              return acc;
            }, {});
            if (redirect.value && redirect.value.startsWith("http")) {
              location.replace(redirect.value)
              return;
            }
            router.push({path: redirect.value || "/", query: otherQueryParams});
          }
        }
      }, 3);
    } else {
      this.msgError(res.message);
    }
  })

}

getState();

getCookie();
</script>

<style lang='scss' scoped>
.lang {
  position: absolute;
  z-index: 999;
  right: 50px;
  top: 10px;
  justify-content: center;
  align-items: center;
  display: flex;
}
.login {
  position: relative;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  height: 100%;
  width: 100%;
  background-image: linear-gradient(25deg, #9cd5f2, #ffffff, #ffffff, #ffffff);
  background-position: 50% 0;
  .left {
      width: 25%;
      margin-right: 15%;
      h2{
        color:blue;
        letter-spacing: 2px;
      }
      p{
        color: black;
        letter-spacing: 1px;
      }
     img {
        width: 600px;
     }
  }
  .right {
    width: 70%;
  }
}



.title {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto 30px auto;
  text-align: center;
  //color: #707070;
  //color: #FFFFFF;

  img {
    width: 48px;
    margin-right: 10px;
  }
}

.login-form {
  //background-color: #FFFFFF;
  border-radius: 15px;
  width: 350px;
  padding: 25px 25px 5px 25px;
  margin-right: 300px;
  border: 1px #e0e0e0 solid;
  box-shadow: -3px -3px 20px 5px rgba(222, 222, 222, 0.2);

  ::v-deep(.el-form-item__error) {
    color: #FF8888;
  }

  .login-btn {
    border-radius: 4px !important;
  }

  .el-input {
    height: 40px;
    border-radius: 4px !important;
    overflow: hidden;
    ::v-deep(.el-input__wrapper) {
      background-color: #f8f8f8 !important;
    }

    input {
      height: 40px;
    }
  }

  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 0;
  }
}

.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}

.login-code {
  width: 33%;
  height: 40px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
  }
}

.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}

.login-code-img {
  height: 40px;
  padding-left: 12px;
}

.operater-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;

  .find-password {
    cursor: pointer;
    font-size: 14px;
  }

  .rememberMe {
    font-size: 14px;
  }
}

.other {
  .item {
    margin: 0 6px;

    img {
      border-radius: 50%;
    }
  }

  .item:first-child {
    margin-left: 10px;
  }
}

</style>
