<template>
  <el-drawer v-model="dialogStatus" :close-on-click-modal="false" size="40%"
             @close="dialogOfClosedMethods(false)">
    <template #header>
      <h4>{{ title }}</h4>
    </template>
    <template #default>
      <el-form :model="form" :rules="rules" ref="providerRef"  label-width="120px" inline-message>
        <el-form-item :label="t('sms.provider')" prop="provider">
          <el-input v-model="form.provider" :placeholder="t('sms.placeholder.provider')"/>
        </el-form-item>
        <el-form-item label="appKey" prop="appKey">
          <el-input v-model="form.appKey" :placeholder="t('sms.placeholder.appKey')" autocomplete="off"/>
        </el-form-item>
        <el-form-item label="appSecret" prop="appSecret">
          <el-input
              autocomplete="off"
              type="password"
              v-model="form.appSecret"
              show-password
              :placeholder="t('sms.placeholder.secret')"
          />
        </el-form-item>
        <el-form-item :label="t('sms.sign')" prop="signName">
          <el-input v-model="form.signName" :placeholder="t('sms.placeholder.sign')"/>
        </el-form-item>
        <el-form-item :label="t('sms.webSite')">
          <el-input v-model="form.hostname" :placeholder="t('sms.placeholder.webSite')"/>
        </el-form-item>
      </el-form>
    </template>
    <template #footer>
      <div style="flex: auto">
        <el-button @click="dialogStatus = false">{{t('org.cancel')}}</el-button>
        <el-button type="primary" @click="submitForm">{{t('text.confirm')}}</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup>
import {defineProps, ref} from "vue";
import {addProvider, editProvider, getProvider} from "@/api/system/config/smsProvider.js";
import {useI18n} from "vue-i18n";

defineOptions({
  name: 'ProviderEdit'
})

const {t} = useI18n();

const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  open: Boolean,
  formId: {
    default: undefined
  }
});
const {proxy} = getCurrentInstance();
const emit = defineEmits(['dialogOfClosedMethods']);

const data = reactive({
  form: {},
  rules: {
    provider: [
      {required: true, message: t('sms.placeholder.provider'), trigger: "blur"},
    ],
    appKey: [{required: true, message: t('sms.placeholder.appKey'), trigger: "blur"}],
    appSecret: [
      {required: true, message: t('sms.placeholder.secret'), trigger: "blur"},
    ],
    signName: [
      {required: true, message: t('sms.placeholder.sign'), trigger: "blur"},
    ]
  }
})
const {form, rules} = toRefs(data)
const dialogStatus = ref(false);


// 监听 open 变化
watch(
    () => props.open,
    (val) => {
      if (val) {
        dialogStatus.value = props.open;
        if (props.formId) {
          //修改,查询当前组织
          getProvider(props.formId).then(res => {
            if (res.code === 200) {
              form.value = res.data;
            }
          })
        } else {
          reset();
        }
      } else {
        reset();
      }
    },
    {immediate: true}
);

function dialogOfClosedMethods(val) {
  emit('dialogOfClosedMethods', val);
}

/** 重置操作表单 */
function reset() {
  form.value = {};
  proxy.resetForm("providerRef");
}


/** 提交表单 */
function submitForm() {
  const handleResponse = (res, successMessage) => {
    if (res.code === 200) {
      dialogStatus.value = false;
      proxy.$modal.msgSuccess(successMessage);
      dialogOfClosedMethods(false);
      reset();
    }
  };

  proxy.$refs["providerRef"].validate(valid => {
    if (valid) {
      const operation = props.formId ? editProvider : addProvider;
      const successMessage = props.formId ?t('org.success.update') : t('org.success.add');

      operation(form.value).then(res => handleResponse(res, successMessage));
    }
  });
}
</script>
