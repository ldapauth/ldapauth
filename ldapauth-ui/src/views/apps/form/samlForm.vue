<template>
  <el-drawer
    :title="title"
    :before-close="handleClose"
    v-model="dialog"
    :close-on-click-modal="false"
    :wrapper-closable="false"
    size="40%"
    ref="drawer"
  ><!-- custom-class="demo-drawer" -->
    <!-- <div class="drawer-content-box">
      <div class="drawer-content"> -->
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
            <span class="tips">{{ t('common.tips.upload.img') }}</span>
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

        <el-form ref="detailsForm" :model="form.details" :rules="detailsRules" label-width="150px" inline-message >
          <el-form-item prop="metaType" :label="t('apps.form.saml.label.metaType')">
            <el-select
              v-model="form.details.metaType"
              clearable
              :placeholder="t('apps.form.saml.placeholder.metaType')"
              style="width: 100%">
              <el-option :label="t('apps.form.saml.label.no')" value="none"></el-option>
              <el-option :label="t('apps.form.saml.label.metaTypeFile')" value="file"></el-option>
            </el-select>
          </el-form-item>
            <el-form-item :label="t('apps.form.saml.label.metaTypeFile')"  v-if="form.details.metaType === 'file'">
              <el-upload
                ref="updload"
                action=""
                list-type="picture-card"
                :show-file-list="false"
                class="avatar-uploader"
                :auto-upload="false"
                :on-change="handleUpload"
              >
              <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
            <el-form-item v-if="form.details.metaType === 'file'">
              <span class="tips">{{ t('common.tips.upload.xml') }}</span>
            </el-form-item>
            <el-form-item :label="t('apps.form.saml.label.certIssuer')" v-if="form.details.certIssuer !== undefined">
              <el-input v-model="form.details.certIssuer" :disabled="true" />
            </el-form-item>
            <el-form-item :label="t('apps.form.saml.label.certSubject')" v-if="form.details.certIssuer !== undefined">
              <el-input
                style="width: 100%"
                type="textarea"
                :disabled="true"
                v-model="form.details.certSubject"
              />
            </el-form-item>
            <el-form-item :label="t('apps.form.saml.label.certExpiration')" v-if="form.details.certExpiration !== undefined">
              <el-input v-model="form.details.certExpiration" :disabled="true"/>
            </el-form-item>
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
            <el-form-item prop="spAcsUri" :label="t('apps.form.saml.label.spAcsUri')">
              <el-input
                style="width: 100%"
                type="textarea"
                maxlength="255"
                show-word-limit
                :placeholder="t('apps.form.saml.placeholder.spAcsUri')"
                v-model="form.details.spAcsUri"
              />
            </el-form-item>
            <el-form-item prop="entityId" :label="t('apps.form.saml.label.entityId')">
              <el-input
                style="width: 100%"
                type="textarea"
                maxlength="200"
                show-word-limit
                v-model="form.details.entityId"
              />
            </el-form-item>
            <el-form-item prop="issuer" :label="t('apps.form.saml.label.issuer')">
              <el-input
                style="width: 100%"
                type="textarea"
                maxlength="200"
                show-word-limit
                v-model="form.details.issuer"
              />
            </el-form-item>
            <el-form-item prop="audience" :label="t('apps.form.saml.label.audience')">
              <el-input
                style="width: 100%"
                type="textarea"
                maxlength="200"
                show-word-limit
                v-model="form.details.audience"
              />
            </el-form-item>
            <el-form-item prop="nameIdFormat" :label="t('apps.form.saml.label.nameIdFormat')">
              <el-select v-model="form.details.nameIdFormat"  style="width: 100%">
                <el-option label="urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified" value="urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified"></el-option>
                <el-option label="urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress" value="urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress"></el-option>
                <el-option label="urn:oasis:names:tc:SAML:2.0:nameid-format:transient" value="urn:oasis:names:tc:SAML:2.0:nameid-format:transient"></el-option>
                <el-option label="urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName" value="urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName"></el-option>
                <el-option label="urn:oasis:names:tc:SAML:1.1:nameid-format:WindowsDomainQualifiedName" value="urn:oasis:names:tc:SAML:1.1:nameid-format:WindowsDomainQualifiedName"></el-option>
                <el-option label="urn:oasis:names:tc:SAML:2.0:nameid-format:kerberos" value="urn:oasis:names:tc:SAML:2.0:nameid-format:kerberos"></el-option>
                <el-option label="urn:oasis:names:tc:SAML:2.0:nameid-format:entity" value="urn:oasis:names:tc:SAML:2.0:nameid-format:entity"></el-option>
                <el-option label="urn:oasis:names:tc:SAML:2.0:nameid-format:persistent" value="urn:oasis:names:tc:SAML:2.0:nameid-format:persistent"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item prop="binding" :label="t('apps.form.saml.label.binding')">
              <el-select v-model="form.details.binding" style="width: 100%">
                <el-option label="Redirect" value="Redirect-Post"></el-option>
                <el-option label="Post" value="Post-Post"></el-option>
               </el-select>
            </el-form-item>
            <el-form-item>
              <span style="color: #504f4f;font-size: 12px;">{{ t('apps.form.saml.tips.binding') }}</span>
            </el-form-item>
          <el-form-item :label="t('apps.form.jwt.label.signature')">
            <el-select v-model="form.details.signature" style="width: 100%">
              <el-option label="RSAwithSHA1" value="RSAwithSHA1"></el-option>
              <el-option label="RSAwithSHA256" value="RSAwithSHA256"></el-option>
              <el-option label="RSAwithSHA384" value="RSAwithSHA384"></el-option>
              <el-option label="RSAwithSHA512" value="RSAwithSHA512"></el-option>
              <el-option label="RSAwithMD5" value="RSAwithMD5"></el-option>
              <el-option label="RSAwithRIPEMD160" value="RSAwithRIPEMD160"></el-option>
              <el-option label="DSAwithSHA1" value="DSAwithSHA1"></el-option>
              <el-option label="ECDSAwithSHA256" value="ECDSAwithSHA256"></el-option>
              <el-option label="ECDSAwithSHA384" value="ECDSAwithSHA384"></el-option>
              <el-option label="ECDSAwithSHA512" value="ECDSAwithSHA512"></el-option>
              <el-option label="RSAwithRIPEMD160" value="ECDSAwithSHA512"></el-option>
              <el-option label="HMAC-MD5" value="HMAC-MD5"></el-option>
              <el-option label="HMAC-SHA1" value="HMAC-SHA1"></el-option>
              <el-option label="HMAC-SHA256" value="HMAC-SHA256"></el-option>
              <el-option label="HMAC-SHA384" value="HMAC-SHA384"></el-option>
              <el-option label="HMAC-SHA512" value="HMAC-SHA512"></el-option>
              <el-option label="HMAC-RIPEMD160" value="HMAC-RIPEMD160"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="t('apps.form.jwt.label.digestMethod')">
            <el-select v-model="form.details.digestMethod" style="width: 100%">
              <el-option label="MD5" value="MD5"></el-option>
              <el-option label="SHA1" value="SHA1"></el-option>
              <el-option label="SHA256" value="SHA256"></el-option>
              <el-option label="SHA384" value="SHA384"></el-option>
              <el-option label="SHA512" value="SHA512"></el-option>
              <el-option label="RIPEMD-160" value="RIPEMD-160"></el-option>
            </el-select>
          </el-form-item>


          <el-form-item :label="t('apps.form.saml.label.viewMetadata')"  v-if="id !== undefined ">
              <span style="color: #409eff;font-size: 12px;">
                <a @click="goMetadata">{{ t('apps.form.saml.label.viewMetadata') }}</a>
              </span>
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

<script setup name="samlForm">
import {getAppdetails, samlUpdate,samlCreate,fileTransform} from '@/api/system/apps';
import {
  commonValidateEmptyAndMaxLength255AndUri,
  commonValidateNotEmptyAndMaxLength128,
  commonValidateNotEmptyAndMaxLength255,
  commonValidateNotEmptyAndMaxLength255AndUri,
  commonValidateNotEmptyAndMaxLength64, commonValidateNotEmptyAndMaxLength64AndNumberAndChar
} from "@/api/commonApi";
import {upload} from "@/api/system/upload";
import { useI18n } from 'vue-i18n'
import { defineEmits, toRefs } from 'vue';
import {privateImage} from "@/utils/privateImages.js";
  const router = useRouter();
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
          appCode:'',
          path: '',
          appName:'',
          icon:'',
          status: '0',
          deviceType: '2',
          securityLevel: '1'
        },
        details: {
          audience:'',
          certExpiration:undefined,
          certIssuer:undefined,
          certSubject:undefined,
          entityId:'',
          issuer:'',
          spAcsUri:'',
          b64Encoder:'',
          metaType: 'file',
          binding: 'Redirect',
          encrypted: 'no',
          signature: 'RSAwithSHA1',
          digestMethod: 'SHA1',
          nameIdFormat:'urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified',
          subject:'email'
        }
      },
      appRules: {
        icon: commonValidateNotEmptyAndMaxLength128(),
        appCode: commonValidateNotEmptyAndMaxLength64AndNumberAndChar(),
        appName: commonValidateNotEmptyAndMaxLength64(),
        path: commonValidateNotEmptyAndMaxLength255(),
        homeUri: commonValidateEmptyAndMaxLength255AndUri(),
        logoutUri: commonValidateEmptyAndMaxLength255AndUri(),
        status: commonValidateNotEmptyAndMaxLength64(),
      },
      detailsRules: {
        binding: commonValidateNotEmptyAndMaxLength255(),
        nameIdFormat: commonValidateNotEmptyAndMaxLength255(),
        audience: commonValidateNotEmptyAndMaxLength255(),
        entityId: commonValidateNotEmptyAndMaxLength255(),
        issuer: commonValidateNotEmptyAndMaxLength255(),
        spAcsUri: commonValidateNotEmptyAndMaxLength255AndUri(),
      }
  })

  const {form,appRules,detailsRules} = toRefs(data)

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

  function goMetadata(){
    window.open(import.meta.env.VITE_APP_BASE_API +"/metadata/saml20/"+form.value.app.id+".xml")
  }

  function colseView(){
    showImageViewer.value = false
  }

    function handleUploadIcon(file){
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

    function handleUpload(file){
      let miniFile = file.raw;
      const isXML = miniFile.type === 'text/xml';
      if (!isXML) {
        proxy.$modal.msgError(t('common.rules.xml'))
        return;
      }
      const isLt2M = miniFile.size / 1024 / 1024 < 2;
      if (!isLt2M) {
        proxy.$modal.msgError(t('common.rules.size'))
        return;
      }
      const formData = new FormData();
      formData.append("file",file.raw);
      fileTransform(formData).then((response) => {
        if(response.code == 200) {
          form.value.details.audience = response.data.audience;
          form.value.details.certExpiration = response.data.certExpiration;
          form.value.details.certIssuer = response.data.certIssuer;
          form.value.details.certSubject = response.data.certSubject;
          form.value.details.entityId = response.data.entityId;
          form.value.details.issuer = response.data.issuer;
          form.value.details.spAcsUri = response.data.spAcsUri;
          form.value.details.b64Encoder = response.data.b64Encoder;
          form.value.details.binding = response.data.firstBinding;
          form.value.details.nameIdFormat = response.data.firstNameIDFormat;
          if (response.data.firstNameIDFormat === 'urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress') {
             form.value.details.subject = "email";
          }
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
          path: '',
          appCode:'',
          appName:'',
          icon:'',
          status: '0',
          deviceType: '1',
          securityLevel: '1'
        },
        details:{
          audience:'',
          certExpiration:undefined,
          certIssuer:undefined,
          certSubject:undefined,
          entityId:'',
          issuer:'',
          spAcsUri:'',
          b64Encoder:'',
          metaType: 'file',
          binding: 'Redirect',
          encrypted: 'no',
          subject:'email',
          signature: 'RSAwithSHA1',
          digestMethod: 'SHA1',
          nameIdFormat:'urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified'
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
        if(formValidate && detailsValidate){
        if (id.value === undefined) {
          samlCreate(form.value).then(res => {
            if (res.code == 200) {
              proxy.$modal.msgSuccess(t('text.success.add'));
              dialog.value = false
              emit('toApplist')
            } else {
              proxy.$modal.msgError(res.message)
            }
          })
        } else {
            samlUpdate(form.value).then(res => {
              if (res.code == 200) {
                proxy.$modal.msgSuccess(t('text.success.update'));
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

