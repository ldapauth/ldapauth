<template>
  <div class="app-container1">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="250px">
      <el-form-item :label="t('policy.password.minLength')" prop="minLength">
        <el-input-number :min="1" :max="99" v-model="form.minLength" />
      </el-form-item>
      <el-form-item :label="t('policy.password.maxLength')" prop="maxLength">
        <el-input-number :min="1" :max="99" v-model="form.maxLength" />
      </el-form-item>
<!--      <el-form-item :label="t('policy.password.expirationDays')" prop="expirationDays">
        <el-input-number :min="1" :max="99999" v-model="form.expirationDays" />
      </el-form-item>-->
      <el-form-item :label="t('policy.password.isDigit')"  prop="isDigit">
        <el-select v-model="form.isDigit">
          <el-option
              v-for="dict in sys_yes_no"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('policy.password.isLowerCase')"  prop="isLowerCase">
        <el-select v-model="form.isLowerCase">
          <el-option
              v-for="dict in sys_yes_no"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('policy.password.isSpecial')"  prop="isSpecial">
        <el-select v-model="form.isSpecial">
          <el-option
              v-for="dict in sys_yes_no"
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

<script setup name="Config-Password-Policy">


import {useI18n} from "vue-i18n";

const { t } = useI18n()


import {policyPassword, savePolicyPassword} from "@/api/system/config/policy.js";
const { proxy } = getCurrentInstance();

const { sys_normal_disable ,sys_yes_no } = proxy.useDict("sys_normal_disable","sys_yes_no");


const data = reactive({
  form: {
    id: undefined,
    minLength: 8,
    maxLength: 20,
    expirationDays: 180,
    isDigit: 1,
    isLowerCase: 1,
    isSpecial: 1,
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
    minLength: 8,
    maxLength: 20,
    expirationDays: 180,
    isDigit: 1,
    isLowerCase: 1,
    isSpecial: 1,
  };
}

function get(){
  policyPassword().then(res => {
    if (res.success) {
      form.value = res.data
    }
  });
}

function submitForm() {
  proxy.$refs["formRef"].validate(valid => {
    if (valid) {
      savePolicyPassword(form.value).then(res => {
        if (res.success) {
          proxy.$modal.msgSuccess(t('text.success.action'));
        }
      });
    }
  });
}

get()

</script>
