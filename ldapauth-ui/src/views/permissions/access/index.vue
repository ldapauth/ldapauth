<template>
   <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" v-show="showSearch" :inline="true">
         <el-form-item :label="t('permissions.access.label.groupName')" prop="groupName">
            <el-input
               v-model="queryParams.groupName"
               :placeholder="t('permissions.access.query.groupName')"
               clearable
               style="width: 240px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
        <el-form-item :label="t('permissions.access.label.appName')" prop="appName">
          <el-input
              v-model="queryParams.appName"
              :placeholder="t('permissions.access.query.appName')"
              clearable
              style="width: 240px"
              @keyup.enter="handleQuery"
          />
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
               v-hasPermi="['permissions:access:auth']"
               @click="handleAdd"
            >{{ t('permissions.access.button.add') }}</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="danger"
               :disabled="multiple"
               v-hasPermi="['permissions:access:delete']"
               @click="handleDelete"
            >{{ t('permissions.access.button.delete') }}</el-button>
         </el-col>
      </el-row>
      <!-- 表格数据 -->
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
         <el-table-column type="selection" width="55" align="center" />
         <el-table-column :label="t('permissions.access.label.groupName')" prop="groupName" :show-overflow-tooltip="true" />
         <el-table-column :label="t('permissions.access.label.appName')" prop="appName" :show-overflow-tooltip="true" />
         <el-table-column :label="t('permissions.access.label.createTime')" align="center" prop="createTime" width="200" />
         <el-table-column :label="t('text.action')" align="center" class-name="small-padding fixed-width" width="200">
            <template #default="scope">
              <el-button type="danger" plain @click="handleDelete(scope.row)"
                         v-hasPermi="['permissions:access:delete']">{{ t('permissions.access.button.delete') }}</el-button>
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
      <el-dialog :title="title" v-model="open" width="80%" append-to-body>
         <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
           <el-form-item :label="t('permissions.access.label.groupName')" prop="ids">
             <el-checkbox-group v-model="form.ids">
               <el-checkbox :label="item.name" :value="item.id" :title="item.name" :disabled="item.status == 1"
                            v-for="item in allGroupData" border style="width: 200px;margin-bottom: 10px;" />
             </el-checkbox-group>
           </el-form-item>
           <el-form-item :label="t('permissions.access.label.appName')"  prop="targetIds">
             <el-checkbox-group v-model="form.targetIds">
               <el-checkbox :label="item.appName" :value="item.id" :title="item.appName" :disabled="item.status == 1"
                            v-for="item in allAppsData" border  style="width: 200px;margin-bottom: 10px;" />
             </el-checkbox-group>
           </el-form-item>
         </el-form>
         <template #footer>
            <div class="dialog-footer">
               <el-button type="primary" @click="submitForm">{{t('text.confirm')}}</el-button>
               <el-button @click="cancel">{{t('text.close')}}</el-button>
            </div>
         </template>
      </el-dialog>
   </div>
</template>

<script setup name="Group">


import {allGroup} from "@/api/system/group.js";
import {allApps} from "@/api/system/apps.js";

import { useI18n } from 'vue-i18n'
import {authData, delAuth, fetchAuth} from "@/api/system/groupApp.js";

const { t } = useI18n()
const router = useRouter();
const { proxy } = getCurrentInstance();
const { sys_normal_disable ,sys_data_object_from} = proxy.useDict("sys_normal_disable","sys_data_object_from");

//全部分组
const allGroupData = ref([]);
//全部应用
const allAppsData = ref([]);

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
  form: {
    ids: [],
    targetIds: []
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    appName: undefined,
    groupName: undefined
  },
  rules: {
    ids: [{ required: true, message: t('permissions.access.rules.groupName'), trigger: "blur" }],
    targetIds: [{ required: true, message: t('permissions.access.rules.appName'), trigger: "blur" }],
  },
});

const { queryParams, form, rules } = toRefs(data);

/** 分页列表 */
function getList() {
  loading.value = true;
  fetchAuth(queryParams.value).then(res => {
    loading.value = false;
    if (res.success) {
      list.value = res.data.records;
      total.value = res.data.total;
    }
  });
}

/** 查询全部分组 */
function getGroupALL() {
  allGroup().then(res => {
    if (res.success) {
      allGroupData.value = res.data;
    }
  });
}

/** 查询全部应用 */
function getAppsAll() {
  allApps().then(res => {
    if (res.success) {
      allAppsData.value = res.data;
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
  proxy.$modal.confirm(t('permissions.access.message.confirmDelete')).then(function () {
    return delAuth(id);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess(t('text.success.action'));
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
    ids: [],
    targetIds: []
  };
}

/** 添加分组 */
function handleAdd() {
  getGroupALL();
  getAppsAll();
  reset();
  open.value = true;
  title.value = t('permissions.access.button.add');
}



/** 提交按钮 */
function submitForm() {
  proxy.$refs["formRef"].validate(valid => {
    if (valid) {
      authData(form.value).then(res => {
        if (res.success) {
          proxy.$modal.msgSuccess(t('permissions.access.message.success'));
          open.value = false;
          getList();
        } else {
          proxy.$modal.msgError(t('permissions.access.message.fail'));
        }
      });
    }
  });
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

getList();

</script>
<style scoped>
::v-deep(.el-checkbox__label) {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap
}

</style>
