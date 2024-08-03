<template>
   <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" v-show="showSearch" :inline="true">
         <el-form-item :label="t('socials.label.name')" prop="name">
            <el-input
               v-model="queryParams.name"
               :placeholder="t('socials.placeholder.name')"
               clearable
               style="width: 240px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
         <el-form-item :label="t('socials.label.status')" prop="status">
            <el-select
               v-model="queryParams.status"
               :placeholder="t('socials.placeholder.status')"
               clearable
               style="width: 240px"
            >
               <el-option
                  v-for="dict in sys_normal_disable"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
               />
            </el-select>
         </el-form-item>
         <el-form-item>
            <el-button @click="handleQuery">{{t('text.query')}}</el-button>
            <el-button @click="resetQuery">{{t('text.reset')}}</el-button>
         </el-form-item>
      </el-form>
      <el-row :gutter="10" class="mb8">
         <el-col :span="1.5">
            <el-button
               type="primary"
               @click="handleAdd"
               v-hasPermi="['socials-provider:add']"
            >{{t('text.add')}}</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               :disabled="single"
               @click="handleUpdate"
               v-hasPermi="['socials-provider:edit']"
            >{{t('text.edit')}}</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="danger"
               :disabled="multiple"
               v-hasPermi="['socials-provider:delete']"
               @click="handleDelete"
            >{{t('text.delete')}}</el-button>
         </el-col>
      </el-row>

      <!-- 表格数据 -->
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
         <el-table-column type="selection" width="55" align="center" />
         <el-table-column :label="t('socials.label.icon')" prop="icon" width="200">
           <template #default="scope">
             <el-image :src="privateImage(scope.row.icon)">
               <template #error>
                 <div class="image-slot">
                   <el-icon><icon-picture /></el-icon>
                 </div>
               </template>
             </el-image>
           </template>

         </el-table-column>
         <el-table-column :label="t('socials.label.name')" prop="name" :show-overflow-tooltip="true" width="250" />
         <el-table-column :label="t('text.description')" prop="description" :show-overflow-tooltip="true" />
         <el-table-column :label="t('socials.label.status')" align="center" width="100">
           <template #default="scope">
             <span v-if="scope.row.status === 0"><el-icon color="green"><SuccessFilled class="success" /></el-icon></span>
             <span v-if="scope.row.status === 1"><el-icon color="#808080"><CircleCloseFilled /></el-icon></span>
           </template>
         </el-table-column>
         <el-table-column :label="t('text.createdDate')" align="center" prop="createTime" width="200" />
         <el-table-column :label="t('text.action')" align="center" class-name="small-padding fixed-width" width="200">
            <template #default="scope">
              <el-button @click="handleUpdate(scope.row)"  v-hasPermi="['socials-provider:edit']" >{{ t('text.edit') }}</el-button>
              <el-button plain type="danger" @click="handleDelete(scope.row)"  v-hasPermi="['socials-provider:delete']">{{t('text.delete')}}</el-button>
            </template>
         </el-table-column>
      </el-table>

      <pagination
         v-show="total > 0"
         :total="total"
         v-model:page="queryParams.pageNum"
         v-model:limit="queryParams.pageSize"
         @pagination="getList"
      />
      <!-- 添加或修改分组配置对话框 -->
     <el-drawer :title="title" v-model="open" size="600px" append-to-body>
         <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
           <el-form-item :label="t('socials.label.icon')" prop="icon">
             <el-upload
                 action="fakeAction"
                 accept="image/jpg,image/jpeg,image/png"
                 class="avatar-uploader"
                 name="uploadFile"
                 :http-request="doUploadAvatar"
                 :before-upload="beforeAvatarUpload"
                 :show-file-list="false">
               <img v-if="form.icon" :src="privateImage(form.icon)" class="avatar" alt="">
               <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
             </el-upload>
           </el-form-item>
           <el-form-item :label="t('socials.label.type')" prop="type">
             <el-select
                 v-model="form.type"
                 :placeholder="t('socials.placeholder.type')"
             >
               <el-option
                   v-for="dict in sys_socials_type"
                   :key="dict.value"
                   :label="dict.label"
                   :value="dict.value"
               />
             </el-select>
           </el-form-item>
           <el-form-item :label="t('socials.label.name')" prop="name">
             <el-input v-model="form.name" :placeholder="t('socials.placeholder.name')" maxlength="64"  show-word-limit/>
           </el-form-item>
           <el-form-item :label="t('socials.label.clientId')" prop="clientId">
             <el-input v-model="form.clientId" type="textarea" :placeholder="t('socials.placeholder.clientId')" rows="5" maxlength="1024"  show-word-limit></el-input>
           </el-form-item>
           <el-form-item :label="t('socials.label.clientSecret')" prop="clientSecret">
             <el-input v-model="form.clientSecret" type="textarea" :placeholder="t('socials.placeholder.clientSecret')" rows="5" maxlength="1024"  show-word-limit></el-input>
           </el-form-item>
           <el-form-item :label="t('socials.label.agentId')" prop="agentId" v-if="form.type =='workweixin'">
             <el-input v-model="form.agentId" :placeholder="t('socials.placeholder.agentId')" maxlength="64"  show-word-limit/>
           </el-form-item>
           <el-form-item :label="t('socials.label.alipayPublicKey')" prop="alipayPublicKey" v-if="form.type =='alipay'">
             <el-input v-model="form.alipayPublicKey" type="textarea" :placeholder="t('socials.placeholder.alipayPublicKey')" rows="5" maxlength="4096"  show-word-limit></el-input>
           </el-form-item>
           <el-form-item :label="t('socials.label.redirectUri')" prop="redirectUri">
             <el-input v-model="form.redirectUri" type="textarea" :placeholder="t('socials.placeholder.redirectUri')" rows="5" maxlength="255"  show-word-limit/>
           </el-form-item>
            <el-form-item :label="t('socials.label.status')">
               <el-radio-group v-model.number="form.status">
                  <el-radio
                     v-for="dict in sys_normal_disable"
                     :key="dict.value"
                     :value="dict.value"
                  >{{ dict.label }}</el-radio>
               </el-radio-group>
            </el-form-item>
            <el-form-item :label="t('text.description')">
               <el-input v-model="form.description" type="textarea"  maxlength="200"  rows="5" show-word-limit></el-input>
            </el-form-item>
         </el-form>
         <template #footer>
            <div class="dialog-footer">
               <el-button type="primary" @click="submitForm">{{t('text.confirm')}}</el-button>
               <el-button @click="cancel">{{t('text.close')}}</el-button>
            </div>
         </template>
     </el-drawer>
   </div>
</template>

<script setup name="Config-Socials-Provider">

import {listPage,get,add,update,deleteByIds,generateId} from "@/api/system/config/socialsprovider.js";

import { useI18n } from 'vue-i18n'
import {ref} from "vue";
import {upload} from "@/api/system/upload.js";
import {privateImage} from "@/utils/privateImages.js";

const { t } = useI18n()
const router = useRouter();
const { proxy } = getCurrentInstance();
const { sys_normal_disable,sys_socials_type} = proxy.useDict("sys_normal_disable","sys_socials_type",);

const list = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: undefined,
    status: undefined
  },
  rules: {
    icon: [{ required: true, message: t('socials.placeholder.icon'), trigger: "blur" }],
    type: [{ required: true, message: t('socials.placeholder.type'), trigger: "blur" }],
    name: [{ required: true, message: t('socials.placeholder.name'), trigger: "blur" }],
    clientId: [{ required: true, message: t('socials.placeholder.clientId'), trigger: "blur" }],
    clientSecret: [{ required: true, message:t('socials.placeholder.clientSecret'), trigger: "blur" }],
    redirectUri: [{ required: true, message: t('socials.placeholder.redirectUri'), trigger: "blur" }],
  },
});

const { queryParams, form, rules } = toRefs(data);

/** 查询分组列表 */
function getList() {
  loading.value = true;
  listPage(queryParams.value).then(res => {
    loading.value = false;
    if (res.success) {
      list.value = res.data.records;
      total.value = res.data.total;
    }
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 删除按钮操作 */
function handleDelete(row) {
  const id = row.id || ids.value;
  proxy.$modal.confirm(t('text.really.delete')).then(function () {
    return deleteByIds(id);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess(t('text.success.delete'));
  }).catch(() => {});
}


/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}


/** 重置新增的表单以及其他数据  */
function reset() {
  form.value = {
    id: undefined,
    icon: undefined,
    name: undefined,
    clientId: undefined,
    clientSecret: undefined,
    agentId: undefined,
    alipayPublicKey: undefined,
    redirectUri: undefined,
    description: undefined,
    status: 0
  };
}

/** 添加分组 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = t('text.add');
  generateId().then(res => {
    if(res.success) {
      form.value.id = res.data;
      form.value.redirectUri = getRedirectUri(form.value.id);
    }
  });
}

function getRedirectUri(id){
  var protocol = window.location.protocol; // 获取协议（http或https）
  var hostname = window.location.hostname; // 获取主机名
  var port = window.location.port; // 获取端口号
  var baseUri = protocol+"//"+hostname
  console.log(protocol,baseUri)
  if (port == 443 ||port ==80) {
    baseUri = baseUri +":"+port;
  }
  return baseUri + "/callback?id="+id;
}

/** 修改分组 */
function handleUpdate(row) {
  reset();
  const id = row.id || ids.value;
  get(id).then(res => {
    if (res.success) {
      form.value = res.data;
      open.value = true;
      title.value = t('text.edit');
    } else {
      proxy.$modal.msgError(t('text.editError'));
    }
  });
}


/** 提交按钮 */
function submitForm() {
  proxy.$refs["formRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        update(form.value).then(res => {
          if (res.success) {
            proxy.$modal.msgSuccess(t('text.success.edit'));
            open.value = false;
            getList();
          } else {
            proxy.$modal.msgError(t('text.error.edit'));
          }
        });
      } else {
        add(form.value).then(res => {
          if (res.success) {
            proxy.$modal.msgSuccess(t('text.success.add'));
            open.value = false;
            getList();
          } else {
            proxy.$modal.msgError(t('text.error.add'));
          }
        });
      }
    }
  });
}

function doUploadAvatar(item) {
  const formData = new FormData();
  formData.append("uploadFile", item.file);
  upload(formData).then(res => {
    if (res.code === 200) {
      form.value.icon = res.data;
    }
  });
}


/** 上传头像前检查 */
function beforeAvatarUpload(rawFile) {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    proxy.$modal.msgError('Avatar picture must be JPG format!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 4) {
    proxy.$modal.msgError('Avatar picture size can not exceed 4MB!')
    return false
  }
  return true
}


/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

getList();

</script>
<style lang="scss" scoped>
.avatar-uploader {
  width: 128px;
  height: 128px;
  border: 3px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 128px;
  height: 128px;
  line-height: 128px;
  text-align: center;
}

.avatar {
  width: 128px;
  height: 128px;
  display: block;
}

::v-deep(.el-image img) {
  width: 48px;
  height: 48px;
  border-radius: 4px;
}

</style>
