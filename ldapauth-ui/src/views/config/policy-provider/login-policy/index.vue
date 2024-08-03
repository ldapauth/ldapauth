<template>
   <div class="app-container1">
     <el-form ref="formRef" :model="form" :rules="rules" label-width="250px">
       <el-form-item :label="t('policy.login.refreshTokenValidity')" prop="refreshTokenValidity">
         <el-input-number :min="1" :max="9999999999" v-model="form.refreshTokenValidity" />
       </el-form-item>
       <el-form-item :label="t('policy.login.accessTokenValidity')">
         <el-input-number :min="1" :max="9999999999" v-model="form.accessTokenValidity" />
       </el-form-item>

       <el-form-item :label="t('policy.login.passwordAttempts')"  prop="passwordAttempts">
         <el-input-number :min="1" :max="999" v-model="form.passwordAttempts"   />
       </el-form-item>
       <el-form-item :label="t('policy.login.loginLockInterval')"  prop="loginLockInterval">
         <el-input-number :min="1" :max="9999999999" v-model="form.loginLockInterval"   />
       </el-form-item>
       <el-form-item :label="t('policy.login.isCaptcha')"  prop="isCaptcha">
         <el-select v-model="form.isCaptcha">
           <el-option
               v-for="dict in sys_enable_status"
               :key="dict.value"
               :label="dict.label"
               :value="dict.value"
           />
         </el-select>
       </el-form-item>
       <el-form-item>
         <el-button type="primary" v-hasPermi="['policy-provider:save']" @click="submitForm">{{t('text.submit')}}</el-button>
       </el-form-item>
     </el-form>

   </div>
</template>

<script setup name="Config-Login-Policy">


import {useI18n} from "vue-i18n";

const { t } = useI18n()


import {policyLogin, savePolicyLogin} from "@/api/system/config/policy.js";
const { proxy } = getCurrentInstance();

const { sys_normal_disable ,sys_enable_status } = proxy.useDict("sys_normal_disable","sys_enable_status");


const data = reactive({
  form: {
    id: undefined,
    refreshTokenValidity: 18000,
    accessTokenValidity: 3600,
    isCaptcha: 1,
    passwordAttempts: 6,
    loginLockInterval: 1800
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: undefined,
    objectFrom: undefined,
    status: undefined
  },
  rules: {

  },
});

const { queryParams, form, rules } = toRefs(data);

/** 重置新增的表单以及其他数据  */
function reset() {
  form.value = {
    refreshTokenValidity: 18000,
    accessTokenValidity: 3600,
    isCaptcha: 1,
    passwordAttempts: 6,
    loginLockInterval: 1800
  };
}

function get(){
  policyLogin().then(res => {
    if (res.success) {
      form.value = res.data
    }
  });
}

function submitForm() {
  proxy.$refs["formRef"].validate(valid => {
    if (valid) {
      savePolicyLogin(form.value).then(res => {
        if (res.success) {
          proxy.$modal.msgSuccess(t('text.success.action'));
        }
      });
    }
  });
}

get()

</script>
