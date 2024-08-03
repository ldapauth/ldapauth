<template>
  <div class="app-container">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item prop="logo" :label="t('system.icon')">
        <el-upload
            ref="updload"
            action=""
            list-type="picture-card"
            class="avatar-uploader"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleUploadIcon"
        >
          <img v-if="form.logo" :src="privateImage(form.logo)" class="iccard">
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
        <el-image-viewer v-if="showImageViewer"  :url-list="handlePreviewData" @close="colseView"/>
      </el-form-item>
      <el-form-item :label="t('system.title')" prop="title">
        <el-input v-model="form.title"  maxlength="255" show-word-limit/>
      </el-form-item>
      <el-form-item :label="t('system.copyright')" prop="copyright">
        <el-input v-model="form.copyright" type="textarea" maxlength="1024"  rows="5" show-word-limit></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">{{ t('text.submit') }}</el-button>
      </el-form-item>
    </el-form>

  </div>
</template>

<script setup name="Config-System-Provider">


import {useI18n} from "vue-i18n";

const { t } = useI18n()

import {privateImage} from "@/utils/privateImages.js";
import {saveSystemsInfo, systemInfo} from "@/api/system/config/systems.js";
import {upload} from "@/api/system/upload.js";
import {reactive} from "vue";
const { proxy } = getCurrentInstance();

const { sys_normal_disable ,sys_enable_status } = proxy.useDict("sys_normal_disable","sys_enable_status");

const handlePreviewData= reactive([])
const showImageViewer = ref(false)

const data = reactive({
  form: {
    id: undefined,
    logo: undefined,
    title: undefined,
    copyright: undefined
  },
  rules: {
    logo: [{ required: true, message: t('system.rules.icon'), trigger: "blur" }],
    title: [{ required: true,  message: t('system.rules.title'), trigger: "blur" }],
    copyright: [{ required: true, message: t('system.rules.copyright'), trigger: "blur" }],
  },
});

const { queryParams, form, rules } = toRefs(data);

/** 重置新增的表单以及其他数据  */
function reset() {
  form.value = {
    id: undefined,
    logo: undefined,
    title: undefined,
    copyright: undefined
  };
}

function handlePreview(){
  handlePreviewData[0]= ''
  if(form.value.logo != undefined) {
    handlePreviewData[0]= privateImage(form.value.logo);
  }
  showImageViewer.value = true;
}

function colseView(){
  showImageViewer.value = false
}

function handleUploadIcon(file){
  /* this.$refs['updload'].clearFiles() */
  let miniFile = file.raw;
  const isIMG = miniFile.type === 'image/jpg' ||
      miniFile.type === 'image/jpeg' ||
      miniFile.type === 'image/png'
  if (!isIMG) {
    proxy.$modal.msgError(t('common.rules.img'))
    return;
  }
  const isLt2M = miniFile.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    proxy.$modal.msgError(t('common.rules.size'))
    return;
  }

  const formData = new FormData();
  formData.append("uploadFile",file.raw);
  //formData.append("modelName", "LdapAuth");
  upload(formData).then((response) => {
    if(response.code == 200) {
      form.value.logo = response.data;
    } else {
      proxy.$modal.msgError(response.message)
    }
  });
}


function get(){
  systemInfo().then(res => {
    if (res.success) {
      form.value = res.data
    }
  });
}

function submitForm() {
  proxy.$refs["formRef"].validate(valid => {
    if (valid) {
      saveSystemsInfo(form.value).then(res => {
        if (res.success) {
          proxy.$modal.msgSuccess(t('text.success.action'));
        } else {
          proxy.$modal.msgError(t('text.error.action'));
        }
      });
    } else {
      proxy.$modal.msgError(t('sync.common.message.validate'));
    }
  });
}

get()

</script>
