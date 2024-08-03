<template>
   <el-form ref="pwdRef" :model="user" :rules="rules" label-width="80px">
      <el-form-item :label="t('profile.oldPwd')" prop="oldPassword">
         <el-input v-model="user.oldPassword" :placeholder="t('profile.placeholder.oldPwd')" type="password" show-password />
      </el-form-item>
      <el-form-item :label="t('profile.newPwd')" prop="newPassword">
         <el-input v-model="user.newPassword" :placeholder="t('profile.placeholder.newPwd')" type="password" show-password />
      </el-form-item>
      <el-form-item :label="t('profile.confirmPwd')" prop="confirmPassword">
         <el-input v-model="user.confirmPassword" :placeholder="t('profile.placeholder.confirmPwd')" type="password" show-password/>
      </el-form-item>
      <el-form-item>
      <el-button type="primary" @click="submit">{{t('text.save')}}</el-button>
      <el-button type="danger" @click="close">{{t('text.close')}}</el-button>
      </el-form-item>
   </el-form>
</template>

<script setup>
import { updateUserPwd } from "@/api/system/user.js";
import {validatePass} from "@/api/commonApi.js";
import {useI18n} from "vue-i18n";

const props = defineProps({
  pwdPolicy: {
    type: Object,
    default: {}
  }
})

const { proxy } = getCurrentInstance();

const {t} = useI18n();

const user = reactive({
  oldPassword: undefined,
  newPassword: undefined,
  confirmPassword: undefined
});

const equalToPassword = (rule, value, callback) => {
  if (user.newPassword !== value) {
    callback(new Error(t('profile.rule.twicePwd')));
  } else {
    callback();
  }
};

const notAllowedEqualToPassword = (rule, value, callback) => {
  if (user.newPassword === user.oldPassword) {
    callback(new Error(t('profile.rule.oldNew')));
  } else {
    callback();
  }
};

const validateWhitespace = (rule, value, callback) => {
  // 使用正则表达式检查输入是否只包含空格
  if (/^\s+$/.test(value)) {
    callback(new Error(t('profile.rule.whiteSpace')));
  } else {
    callback();
  }
}

const rules = ref({
  oldPassword: [
    { required: true, trigger: "blur", message: t('profile.rule.oldPwd') },
    { validator: notAllowedEqualToPassword, trigger: ['blur', 'change'] },
    { validator: validateWhitespace, trigger: ['blur', 'change'] }
  ],
  newPassword: [
    { required: true, message: t('user.rule.pwd'), trigger: 'blur' },
    { validator: (rule, value, callback) => validatePass(rule, value, callback, props.pwdPolicy), trigger: ['blur', 'change'] },
    { validator: notAllowedEqualToPassword, trigger: ['blur', 'change'] }
  ],
  confirmPassword: [
    { required: true, message: t('profile.rule.confirmPwd'), trigger: "blur" },
    { validator: equalToPassword, trigger: "blur" }
  ]
});

/** 提交按钮 */
function submit() {
  proxy.$refs.pwdRef.validate(valid => {
    if (valid) {
      let params = {
        oldPassword: user.oldPassword,
        newPassword: user.newPassword
      }
      updateUserPwd(params).then(res => {
        if (res.code === 200) {
          proxy.$modal.msgSuccess(t('org.success.update'));
        } else {
          proxy.$modal.msgError(res.message);
        }
      });
    }
  });
};

/** 关闭按钮 */
function close() {
  proxy.$tab.closePage();
}

</script>
