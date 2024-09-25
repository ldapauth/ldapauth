<template>
   <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" v-show="showSearch" :inline="true">
         <el-form-item :label="t('group.name')" prop="name">
            <el-input
               v-model="queryParams.name"
               :placeholder="t('group.namePlaceholder')"
               clearable
               style="width: 240px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
        <el-form-item :label="t('group.objectFrom')" prop="objectFrom">
          <el-select
              v-model="queryParams.objectFrom"
              :placeholder="t('group.objectFromPlaceholder')"
              clearable
              style="width: 240px"
          >
            <el-option
                v-for="dict in sys_data_object_from"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            />
          </el-select>
        </el-form-item>
         <el-form-item :label="t('group.status')" prop="status">
            <el-select
               v-model="queryParams.status"
               :placeholder="t('group.statusPlaceholder')"
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
               v-hasPermi="['group:add']"
            >{{t('text.add')}}</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="danger"
               :disabled="multiple"
               @click="handleDelete"
               v-hasPermi="['group:delete']"
            >{{t('text.delete')}}</el-button>
         </el-col>
      </el-row>

      <!-- 表格数据 -->
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
         <el-table-column type="selection" width="55" align="center" :selectable="isAdministraror" />
         <el-table-column :label="t('group.name')" prop="name" :show-overflow-tooltip="true" width="400" />
         <el-table-column :label="t('text.description')" prop="description" :show-overflow-tooltip="true" />
         <el-table-column label="DN" prop="ldapDn" :show-overflow-tooltip="true" />
         <el-table-column prop="object_from" :label="t('org.from')" align="center" min-width="60">
            <template #default="scope">
              <dict-tag :options="sys_data_object_from" :value="scope.row.objectFrom" />
            </template>
          </el-table-column>
         <el-table-column :label="t('group.status')" align="center" width="100">
           <template #default="scope">
             <span v-if="scope.row.status === 0"><el-icon color="green"><SuccessFilled class="success" /></el-icon></span>
             <span v-if="scope.row.status === 1"><el-icon color="#808080"><CircleCloseFilled /></el-icon></span>
           </template>
         </el-table-column>

         <el-table-column :label="t('text.createdDate')" align="center" prop="createTime" width="200" />
         <el-table-column :label="t('org.operate')" align="center" class-name="small-padding fixed-width" width="200">
            <template #default="scope">
              <el-button @click="handleUpdate(scope.row)" style ="margin-right: 10px" v-hasPermi="['group:edit']">{{ t('text.edit') }}</el-button>
              <el-dropdown v-hasPermi="['group:auth:user', 'group:auth:resource', 'group:delete']">
                <el-button>
                  {{t('text.moreAction')}}<el-icon class="el-icon--right"><arrow-down /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <div v-hasPermi="['group:auth:user']">
                      <el-dropdown-item @click="handleAuthUser(scope.row)"  v-if="scope.row.id != '2'">{{t('group.authUser')}}</el-dropdown-item>
                    </div>
                    <div v-hasPermi="['group:auth:resource']">
                      <el-dropdown-item @click="handleAuthResource(scope.row)">{{ t('group.authResource') }}</el-dropdown-item>
                    </div>
                    <div v-hasPermi="['group:delete']">
                      <el-dropdown-item @click="handleDelete(scope.row)" v-if="isAdministraror(scope.row)">{{t('text.delete')}}</el-dropdown-item>
                    </div>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
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
      <el-dialog :title="title" v-model="open" width="500px" append-to-body>
         <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
            <el-form-item :label="t('group.name')" prop="name">
               <el-input v-model="form.name" :placeholder="t('group.namePlaceholder')" maxlength="64"  show-word-limit/>
            </el-form-item>

            <el-form-item :label="t('group.status')">
               <el-radio-group v-model.number="form.status">
                  <el-radio
                     v-for="dict in sys_normal_disable"
                     :key="dict.value"
                     :value="dict.value"
                  >{{ dict.label }}</el-radio>
               </el-radio-group>
            </el-form-item>
            <el-form-item :label="t('text.description')">
               <el-input v-model="form.description" type="textarea" :placeholder="t('group.descriptionPlaceholder')" maxlength="200"  show-word-limit></el-input>
            </el-form-item>
           <el-form-item label="DN">
             <el-input v-model="form.ldapDn" type="textarea" disabled="true" maxlength="200"  show-word-limit></el-input>
           </el-form-item>
         </el-form>
         <template #footer>
            <div class="dialog-footer">
               <el-button type="primary" @click="submitForm">{{t('text.confirm')}}</el-button>
               <el-button @click="cancel">{{t('text.close')}}</el-button>
            </div>
         </template>
      </el-dialog>

     <auth-user-info ref="authUserInfoRef"></auth-user-info>
     <auth-resource ref="authResourceRef"></auth-resource>
   </div>
</template>

<script setup name="Group">
import AuthResource from "@/views/identity/group/auth-resource/index.vue"

import AuthUserInfo from "@/views/identity/group/auth-userinfo/index.vue"

import {listGroup,getGroup,addGroup,updateGroup,delGroup} from "@/api/system/group.js";

import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const router = useRouter();
const { proxy } = getCurrentInstance();
const { sys_normal_disable ,sys_data_object_from} = proxy.useDict("sys_normal_disable","sys_data_object_from");

const list = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const authUserInfoRef = ref(null);
const authResourceRef = ref(null);
const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: undefined,
    objectFrom: undefined,
    status: undefined
  },
  rules: {
    name: [{ required: true, message: t('group.nameMessage'), trigger: "blur" }],
  },
});

const { queryParams, form, rules } = toRefs(data);

/**
 * 判断是否管理员组
 * @param row
 * @param index
 * @returns {boolean}
 */
function isAdministraror(row, index) {
  if (row.id == '1' || row.id == '2') {
    return false  //不可勾选
  } else {
    return true  //可勾选
  }
}

/**
 * 判断是否管理员组
 * @param row
 * @param index
 * @returns {boolean}
 */
function isAdministrarors() {
  console.log(111,form.value.status)

    return false  //可勾选

}

/** 查询分组列表 */
function getList() {
  loading.value = true;
  listGroup(queryParams.value).then(res => {
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
    return delGroup(id);
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

/** 分配用户 */
function handleAuthUser(row) {
  authUserInfoRef.value.authByGroupId(row.id)
}

/** 授权资源 */
function handleAuthResource(row) {
  authResourceRef.value.authByGroupId(row.id,row.name)
}

/** 重置新增的表单以及其他数据  */
function reset() {
  form.value = {
    id: undefined,
    name: undefined,
    objectFrom: undefined,
    status: 0,
    description: undefined,
  };
}

/** 添加分组 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = t('text.add');
}

/** 修改分组 */
function handleUpdate(row) {
  reset();
  const id = row.id || ids.value;
  getGroup(id).then(res => {
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
        updateGroup(form.value).then(res => {
          if (res.success) {
            proxy.$modal.msgSuccess(t('text.success.edit'));
            open.value = false;
            getList();
          } else {
            proxy.$modal.msgError(t('text.error.edit'));
          }
        });
      } else {
        addGroup(form.value).then(res => {
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

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

getList();

</script>
