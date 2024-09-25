<template>
  <div class="app-container">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="150px">

<!--      <el-form-item :label="t('sync.common.label.classify')" prop="classify">
        <el-select v-model="form.classify">
             <el-option label="OpenLdap" value="openldap"></el-option>
            <el-option label="Activedirectory" value="activedirectory"></el-option>
        </el-select>
      </el-form-item>-->

      <el-form-item :label="t('sync.common.ldap.baseApi')" prop="baseApi">
        <el-input v-model="form.baseApi" :placeholder="t('sync.common.ldap.placeholder.baseApi')" maxlength="255"  show-word-limit/>
      </el-form-item>
      <el-form-item :label="t('sync.common.ldap.clientId')" prop="clientId">
        <el-input v-model="form.clientId" :placeholder="t('sync.common.ldap.placeholder.clientId')" maxlength="64"  show-word-limit/>
      </el-form-item>
      <el-form-item :label="t('sync.common.ldap.clientSecret')" prop="clientSecret">
        <el-input  v-model="form.clientSecret" :placeholder="t('sync.common.ldap.placeholder.clientSecret')"type="password" show-password />
      </el-form-item>
      <el-form-item :label="t('sync.common.ldap.domain')" prop="domain">
        <el-input v-model="form.baseDomain"  maxlength="128"  show-word-limit />
      </el-form-item>
      <el-form-item :label="t('sync.common.ldap.baseDn')" prop="baseDn">
        <el-input v-model="form.baseDn" :placeholder="t('sync.common.ldap.placeholder.baseDn')" maxlength="64"  show-word-limit />
      </el-form-item>
      <el-form-item :label="t('sync.common.ldap.userDn')" prop="userDn">
        <el-input v-model="form.userDn" :placeholder="t('sync.common.ldap.placeholder.userDn')" maxlength="255"  show-word-limit />
      </el-form-item>
      <el-form-item :label="t('sync.common.ldap.groupDn')" prop="groupDn">
        <el-input v-model="form.groupDn" :placeholder="t('sync.common.ldap.placeholder.groupDn')"maxlength="255"  show-word-limit />
      </el-form-item>
      <el-form-item :label="t('sync.common.ldap.orgFilter')" prop="orgFilter">
        <el-input v-model="form.orgFilter" :placeholder="t('sync.common.ldap.placeholder.orgFilter')" maxlength="255"  show-word-limit />
      </el-form-item>
      <el-form-item :label="t('sync.common.ldap.userFilter')" prop="userFilter">
        <el-input v-model="form.userFilter" :placeholder="t('sync.common.ldap.placeholder.userFilter')" maxlength="255"  show-word-limit />
      </el-form-item>
      <el-form-item :label="t('sync.common.ldap.groupFilter')" prop="groupFilter">
        <el-input v-model="form.groupFilter" :placeholder="t('sync.common.ldap.placeholder.groupFilter')" maxlength="255"  show-word-limit />
      </el-form-item>
      <el-form-item label="SSL">
        <el-switch v-model="form.openssl" />
      </el-form-item>
      <el-form-item :label="t('sync.common.label.status')">
        <el-radio-group v-model.number="form.status">
          <el-radio
              v-for="dict in sys_normal_disable"
              :key="dict.value"
              :value="dict.value"
          >{{ dict.label }}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item :label="t('sync.common.label.cron')" prop="cron">
        <el-select v-model="form.cron">
          <el-option
              v-for="dict in sys_cron_list"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('text.description')">
        <el-input v-model="form.description" type="textarea" :placeholder="t('text.description')" maxlength="200" show-word-limit></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm"  v-hasPermi="['ldap-provider:save']">{{t('sync.common.button.submitForm')}}</el-button>
        <el-button @click="submitSync"  v-hasPermi="['ldap-provider:sync']">{{t('sync.common.button.submitSync')}}</el-button>
        <el-button @click="testLink"  v-hasPermi="['ldap-provider:testlink']">{{t('sync.common.button.testLink')}}</el-button>
        <el-button @click="logs"  v-hasPermi="['ldap-provider:logs']">{{t('sync.common.button.logs')}}</el-button>
      </el-form-item>
    </el-form>
    <sync-logs ref="syncLogsRef"/>
  </div>
</template>

<script setup name="Sync-Component-ldap">

import SyncLogs from "@/views/sync/logs/index.vue"

const syncLogsRef = ref(null);

import {useI18n} from "vue-i18n";

const { t } = useI18n()

import {
  getSynchronizers,
  syncSynchronizers,
  testSynchronizers,
  updateSynchronizers
} from "@/api/system/synchronizers.js";
import {uploadFile} from "@/api/system/upload.js";
const { proxy } = getCurrentInstance();

const { sys_normal_disable ,sys_cron_list } = proxy.useDict("sys_normal_disable","sys_cron_list");

const data = reactive({
  form: {
    id: 1,
    baseApi: "ldap://test.com",
    clientId: undefined,
    clientSecret: undefined,
    baseDn: undefined,
    userDn: undefined,
    groupDn: undefined,
    orgFilter: undefined,
    userFilter: undefined,
    groupFilter: undefined,
    status: 0,
    cron: "0 0 0/6 * * ?",
    openssl: false,
    description: undefined
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: undefined,
    status: undefined
  },
  rules: {
    baseApi: [{ required: true, message: t('sync.common.ldap.rules.baseApi'), trigger: "blur" }],
    clientId: [{ required: true, message: t('sync.common.ldap.rules.clientId'), trigger: "blur" }],
    clientSecret: [{ required: true, message: t('sync.common.ldap.rules.clientSecret'), trigger: "blur" }],
    baseDn: [{ required: true, message: t('sync.common.ldap.rules.baseDn'), trigger: "blur" }],
    userDn: [{ required: true, message: t('sync.common.ldap.rules.userDn'), trigger: "blur" }],
    groupDn: [{ required: true, message: t('sync.common.ldap.rules.groupDn'), trigger: "blur" }],
    orgFilter: [{ required: true, message: t('sync.common.ldap.rules.orgFilter'), trigger: "blur" }],
    userFilter: [{ required: true, message: t('sync.common.ldap.rules.userFilter'), trigger: "blur" }],
    groupFilter: [{ required: true, message: t('sync.common.ldap.rules.groupFilter'), trigger: "blur" }]
  },
});

const { queryParams, form, rules } = toRefs(data);

/** 重置新增的表单以及其他数据  */
function reset() {
  form.value = {
    id: 1,
    baseApi: "ldap://test.com",
    clientId: undefined,
    clientSecret: undefined,
    baseDn: undefined,
    orgFilter: undefined,
    userFilter: undefined,
    groupFilter: undefined,
    status: 0,
    cron: "0 0 0/6 * * ?",
    openssl: false,
    description: undefined
  };
}

function get(){
  getSynchronizers(form.value.id).then(res => {
    if (res.success) {
      form.value = res.data
    }
  });
}

function handleUploadFile(file){
  let miniFile = file.raw;
  const isLt2M = miniFile.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    proxy.$modal.msgError(t('common.rules.size'))
    return;
  }
  const formData = new FormData();
  formData.append("uploadFile",file.raw);
  uploadFile(formData).then((res) => {
    if(res.code == 200) {
      form.value.sslFileId = res.data;
    } else {
      proxy.$modal.msgError(res.message)
    }
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["formRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateSynchronizers(form.value).then(res => {
          if (res.success) {
            proxy.$modal.msgSuccess(t('text.success.edit'));
            get();
          } else {
            proxy.$modal.msgError(t('text.error.edit'));
          }
        });
      }
    } else {
      proxy.$modal.msgError(t('sync.common.message.validate'));
    }
  });
}




function submitSync(){
  proxy.$refs["formRef"].validate(valid => {
    if (valid) {
      syncSynchronizers(form.value.id).then(res => {
        if (res.success) {
          proxy.$modal.msgSuccess(t('sync.common.message.syncSuccess'));
        } else {
          proxy.$modal.msgError(t('sync.common.message.syncFail'));
        }
      });
    } else {
      proxy.$modal.msgError(t('sync.common.message.validate'));
    }
  });
}

/** 测试连接 */
function testLink() {
  proxy.$refs["formRef"].validate(valid => {
    if (valid) {
      testSynchronizers(form.value).then(res => {
        if (res.success) {
          proxy.$modal.msgSuccess(t('sync.common.message.testSuccess'));
        } else {
          proxy.$modal.msgError(t('sync.common.message.testFail'));
        }
      });
    } else {
      proxy.$modal.msgError(t('sync.common.message.validate'));
    }
  });
}

/** 测试连接 */
function logs() {
  syncLogsRef.value.openLogs(form.value.id)
}

get()

</script>
