<template>
  <el-drawer
    :title="title"
    :before-close="handleClose"
    v-model="dialog"
    :close-on-click-modal="false"
    :wrapper-closable="false"
    size="40%"
    ref="drawer"
  >
    <!-- <div class="drawer-content-box"> -->
      <!-- <div class="drawer-content"> -->
        <el-form  ref="appsForm" :model="form.app" :rules="appRules" label-width="150px" v-loading="loading" inline-message>
          <el-form-item prop="icon" :label="t('apps.form.label.icon')">
            <el-upload
              ref="updload"
              action=""
              list-type="picture-card"
              class="avatar-uploader"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleUploadIcon"
            >
              <img v-if="form.app.icon" :src="privateImage(form.app.icon)" class="iccard">
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <el-image-viewer v-if="showImageViewer"  :url-list="handlePreviewData" @close="colseView"/>
          </el-form-item>
          <el-form-item >
            <el-button type="text"  v-if="form.app.icon" @click="handlePreview">{{ t('text.showImg') }}</el-button>
          </el-form-item>
          <el-form-item >
            <span class="tips">{{t('common.tips.upload.img')}}</span>
          </el-form-item>
          <el-form-item prop="appCode" :label="t('apps.form.label.appCode')">
            <el-input v-model="form.app.appCode" :placeholder="t('apps.form.placeholder.appCode')" :disabled="id !== undefined"/>
          </el-form-item>
          <el-form-item prop="appName" :label="t('apps.form.label.appName')">
            <el-input v-model="form.app.appName" :placeholder="t('apps.form.placeholder.appName')"/>
          </el-form-item>
          <el-form-item prop="path" :label="t('apps.form.label.path')">
            <el-input
              style="width: 100%"
              type="textarea"
              :placeholder="t('apps.form.placeholder.path')"
              maxlength="255"
              show-word-limit
              v-model="form.app.path"
            />
          </el-form-item>
          <el-form-item prop="homeUri" :label="t('apps.form.label.homeUri')">
            <el-input
              style="width: 100%"
              type="textarea"
              :placeholder="t('apps.form.placeholder.homeUri')"
              maxlength="255"
              show-word-limit
              v-model="form.app.homeUri"
            />
          </el-form-item>
          <el-form-item prop="logoutUri" :label="t('apps.form.label.logoutUri')">
            <el-input
              style="width: 100%"
              type="textarea"
              :placeholder="t('apps.form.placeholder.logoutUri')"
              maxlength="255"
              show-word-limit
              v-model="form.app.logoutUri"
            />
          </el-form-item>
          <el-form-item :label="t('apps.form.label.status')">
            <el-select
              v-model="form.app.status"
              :placeholder="t('apps.form.placeholder.status')"
              style="width: 100%">
              <el-option :label="t('text.enable')" value="0"></el-option>
              <el-option :label="t('text.disable')" value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="t('apps.form.label.description')">
            <el-input
              style="width: 100%"
              type="textarea"
              maxlength="200"
              show-word-limit
              v-model="form.app.description"
            />
          </el-form-item>
        </el-form>

        <el-form ref="detailsForm" :model="form.details" :rules="detailsRules" label-width="150px" inline-message>
          <el-form-item prop="subject" :label="t('apps.form.saml.label.subject')">
            <el-select
              v-model="form.details.subject"
              :placeholder="t('apps.form.saml.placeholder.subject')"
              style="width: 100%">
              <el-option :label="t('apps.form.saml.label.username')" value="username"></el-option>
              <el-option :label="t('apps.form.saml.label.mobile')" value="mobile"></el-option>
              <el-option :label="t('apps.form.saml.label.email')" value="email"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item prop="serverNames" :label="t('apps.form.cas.label.serverNames')">
            <el-input
              style="width: 100%"
              type="textarea"
              maxlength="255"
              show-word-limit
              :placeholder="t('apps.form.cas.placeholder.serverNames')"
              v-model="form.details.serverNames"
            />
          </el-form-item>
          <el-form-item >
            <span class="tips">{{ t('apps.form.cas.tips.serverNames') }}</span>
          </el-form-item>
          <el-form-item :label="t('apps.form.cas.label.ticketValidity')">
            <el-input-number v-model="form.details.ticketValidity" :min="300" :max="999999999"></el-input-number>
            <span class="tips">{{ t('apps.form.cas.tips.ticketValidity') }}</span>
          </el-form-item>

          <el-form-item :label="t('apps.form.label.clientId')" v-if="id !== undefined">
            <el-input v-model="form.app.clientId" :disabled="true" />
          </el-form-item>
          <el-form-item :label="t('apps.form.label.clientSecret')" v-if="id !== undefined">
            <el-input
              v-model="form.app.clientSecret"
              :type="clientSecretType"
              :disabled="true"
            />
            <el-button @click="showclientSecret">
              {{clientSecretType ==='password'?
              t('text.display')
              :
              t('text.hide')
              }}
            </el-button>
          </el-form-item>
         </el-form>
      <!-- </div> -->
      <div class="drawer-footer">
        <el-button type="primary" @click="submitForm">{{ t('text.submit') }}</el-button>
        <el-button @click="cancel">{{ t('text.close') }}</el-button>
      </div>
    <!-- </div> -->
  </el-drawer>
</template>

<script setup name="casForm">
import {getAppdetails, casCreate, casUpdate} from '@/api/system/apps';
import {upload} from "@/api/system/upload";
import {
  commonValidateEmptyAndMaxLength255AndUri,
  commonValidateNotEmptyAndMaxLength128,
  commonValidateNotEmptyAndMaxLength255,
  commonValidateNotEmptyAndMaxLength255AndUri,
  commonValidateNotEmptyAndMaxLength64, commonValidateNotEmptyAndMaxLength64AndNumberAndChar
} from "@/api/commonApi";
import { useI18n } from 'vue-i18n'
import { reactive } from 'vue';
import {privateImage} from "@/utils/privateImages.js";
  const { proxy } = getCurrentInstance();
  const { t } = useI18n()
  const emit = defineEmits(['toApplist','getList'])
  const appsForm = ref()
  const detailsForm = ref()

  const clientSecretType = ref('password')
  const handlePreviewData= reactive([])
  const showImageViewer = ref(false)
  const id = ref(undefined)
  const dialog =ref(false)
  const title= ref("新增")
  const loading= ref(false)
  const data = reactive({
    form: {
        app: {
          path: '',
          appCode:'',
          appName:'',
          icon:'',
          status: '0',
          deviceType: '2',
          securityLevel: '1'
        },
        details:{
          subject:'mobile',
          serverNames: '',
          ticketValidity: 1800,
        }
    },
    appRules:{
      icon: commonValidateNotEmptyAndMaxLength128(),
      appCode: commonValidateNotEmptyAndMaxLength64AndNumberAndChar(),
      appName: commonValidateNotEmptyAndMaxLength64(),
      path: commonValidateNotEmptyAndMaxLength255(),
      homeUri: commonValidateEmptyAndMaxLength255AndUri(),
      logoutUri: commonValidateEmptyAndMaxLength255AndUri(),
      status: commonValidateNotEmptyAndMaxLength64(),
    },
    detailsRules:{
      serverNames: commonValidateNotEmptyAndMaxLength255AndUri()
    },
  })

  const {form,appRules,detailsRules} = toRefs(data);

  function load (rowid) {
    clientSecretType.value = "password";
    id.value = rowid;
    dialog.value = true;
    reset();
    if (rowid !==undefined) {
      title.value = t('text.edit')
      getAppdetails(id.value).then(res => {
        if (res.code == 200) {
          form.value.app = res.data.app;
          form.value.details = res.data.details || {};
          form.value.app.status = form.value.app.status.toString()
          form.value.app.deviceType = form.value.app.deviceType.toString()
          form.value.app.securityLevel = form.value.app.securityLevel.toString()
        }
      })
    } else {
      title.value = t('apps.addApp')
    }
  }

  defineExpose({load})

  function handlePreview(){
    handlePreviewData[0]= ''
    if(form.value.app.icon != undefined) {
      handlePreviewData[0]= privateImage(form.value.app.icon);
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
          form.value.app.icon = response.data;
        } else {
          proxy.$modal.msgError(response.message)
        }
      });
 }

  function handleClose(done) {
    loading.value = false;
    dialog.value= false;
    reset()
  }

  function showclientSecret() {
    if (clientSecretType.value === 'password'){
      clientSecretType.value = "text";
    } else {
      clientSecretType.value = "password";
    }
  }

  function cancel() {
    loading.value = false;
    dialog.value = false;
    reset()
  }

  function reset(){
    form.value = {
      app: {
        appCode:'',
        path: '',
        appName:'',
        icon:'',
        status: '0',
        deviceType: '1',
        securityLevel: '1'
      },
      details:{
        subject:'mobile',
        serverNames: '',
        ticketValidity: 1800
      }
    }
    try{
      appsForm.value.clearValidate();
    }catch (e){

    }
    try{
      detailsForm.value.clearValidate();
    }catch (e){

    }
  }

  /** 提交按钮 */
  function submitForm() {

    let formValidate = false;
    let detailsValidate = false;

    appsForm.value.validate(valid => {
      if (valid) {
        formValidate = true;
      }
    })
    detailsForm.value.validate(valid => {
      if (valid) {
        detailsValidate = true;
      }
    })
    setTimeout(() => {
      if(formValidate&& detailsValidate){
      if (id.value === undefined) {
        casCreate(form.value).then(res => {
          if (res.code == 200) {
            proxy.$modal.msgSuccess(t('text.success.add'));
            dialog.value = false
            emit('toApplist')
          } else {
            proxy.$modal.msgError(res.message)
          }
        })
      } else {
        casUpdate(form.value).then(res => {
          if (res.code == 200) {
            proxy.$modal.msgSuccess(t('text.success.edit'));
            dialog.value = false
            emit('getList')
          } else {
            proxy.$modal.msgError(res.message)
          }
        })
      }
    } else {
      proxy.$modal.msgError(t('common.rules.validateFail'))
    }
    });

  }


</script>

<style lang="scss" scoped>
.tips{
  color: #161616;
  margin-left: 10px;
  font-size: 12px;
}
:v-deep .el-upload--picture-card {
  overflow: hidden;
}
:v-deep .el-upload--picture-card img {
  max-width: 148px;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  float: left;
  margin-left: 20px;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  height: 148px;
  line-height: 48px;
  text-align: center;
}

.avatar-uploader-icon > .ms {
  font-size: 14px;
}

.iccard {
  width: 148px;
  height: 148PX;
  padding: 5px;
  display: block;
}

.drawer-footer {
  position: absolute;
  right: 16px;
  bottom: 16px;
}
</style>

