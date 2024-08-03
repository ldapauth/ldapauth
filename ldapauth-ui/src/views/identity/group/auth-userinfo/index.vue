<template>
  <el-drawer
      v-model="drawer"
      :title="t('group.member.title')"
      size="1000px"
  >
    <div class="app-container1" style="margin-top: -10px">
      <el-form :model="queryParams" ref="queryRef"  :inline="true">
        <el-form-item :label="t('group.member.username')" prop="username">
          <el-input
              v-model="queryParams.username"
              :placeholder="t('group.member.usernamePlaceholder')"
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item :label="t('group.member.displayName')" prop="displayName">
          <el-input
              v-model="queryParams.displayName"
              :placeholder="t('group.member.displayNamePlaceholder')"
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item :label="t('group.member.mobile')" prop="mobile">
          <el-input
              v-model="queryParams.mobile"
              :placeholder="t('group.member.mobilePlaceholder')"
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
              @click="handleUser"
              v-hasPermi="['group:auth:user:add']"
          >{{t('group.member.add')}}</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
              type="danger"
              :disabled="multiple"
              @click="batchHandleDelete"
              v-hasPermi="['group:auth:user:delete']"
          >{{t('group.member.remove')}}</el-button>
        </el-col>
      </el-row>
      <!-- 表格数据 -->
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="来源" align="center" min-width="60">
          <template #default="scope">
            <dict-tag :options="sys_data_object_from" :value="scope.row.objectFrom" />
          </template>
        </el-table-column>
        <el-table-column :label="t('group.member.username')" prop="username" :show-overflow-tooltip="true"  />
        <el-table-column :label="t('group.member.displayName')" prop="displayName" :show-overflow-tooltip="true" width="150" />
        <el-table-column :label="t('group.member.mobile')" prop="mobile" width="150" :show-overflow-tooltip="true" />
        <el-table-column :label="t('text.status.status')" align="center" width="100">
          <template #default="scope">
            <span v-if="scope.row.status === 0"><el-icon color="green"><SuccessFilled class="success" /></el-icon></span>
            <span v-if="scope.row.status === 1"><el-icon color="#808080"><CircleCloseFilled /></el-icon></span>
          </template>
        </el-table-column>

        <el-table-column v-hasPermi="['group:auth:user:delete']" :label="t('text.action')" align="center" width="100" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip :content="t('group.member.remove')" placement="top">
              <el-button link type="primary" icon="Delete"
                         v-hasPermi="['group:auth:user:delete']"
                         @click="handleDelete(scope.row)" ></el-button>
            </el-tooltip>
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
    </div>

    <not-auth-user-info ref="notAuthUserInfoRef" @selectUser="selectUser"></not-auth-user-info>

  </el-drawer>
</template>

<script setup name="group-auth-userinfo">

import NotAuthUserInfo from "@/views/identity/group/auth-userinfo/not-auth-userinfo/index.vue"

import {listGroupMemberAuth, addMember, removeMember} from "@/api/system/group.js";

import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const router = useRouter();

const { proxy } = getCurrentInstance();

const notAuthUserInfoRef = ref(null);

const { sys_data_object_from,sys_normal_disable ,sys_object_from} = proxy.useDict("sys_data_object_from","sys_normal_disable","sys_object_from");

const list = ref([]);
const open = ref(false);
const loading = ref(true);

const drawer = ref(false)
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
    groupId: undefined,
    username: undefined,
    displayName: undefined,
    mobile: undefined
  }
});
const { queryParams, form, rules } = toRefs(data);

//定义当前分组ID
const currentGroupId = ref(null)

function authByGroupId(groupId) {
  currentGroupId.value = groupId;
  queryParams.value.groupId = groupId;
  drawer.value = true;
  getList()
}

function handleUser() {
  notAuthUserInfoRef.value.openUser(currentGroupId)
}

function selectUser(ids){
  let data = {
    ids: [currentGroupId.value],
    targetIds: ids
  }
  console.log(data);
  addMember(data).then(res => {
    loading.value = false;
    if (res.success) {
      proxy.$modal.msgSuccess(t('text.success.add'));
      handleQuery();
    } else {
      proxy.$modal.msgError(t('text.error.add'));
    }
  });
}
/** 查询分组列表 */
function getList() {
  loading.value = true;
  listGroupMemberAuth(queryParams.value).then(res => {
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


/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

function batchHandleDelete(row) {
  proxy.$modal.confirm(t('text.really.remove')).then(function () {
    let data = {
      ids: [currentGroupId.value],
      targetIds: ids.value
    }
    removeMember(data).then(res => {
      loading.value = false;
      if (res.success) {
        proxy.$modal.msgSuccess(t('text.success.remove'));
        handleQuery();
      } else {
        proxy.$modal.msgError(t('text.error.remove'));
      }
    });
  }).catch(() => {});
}
/** 删除按钮操作 */
function handleDelete(row) {
  proxy.$modal.confirm(t('text.really.remove')).then(function () {
    let data = {
      ids: [currentGroupId.value],
      targetIds: [row.id]
    }
    removeMember(data).then(res => {
      loading.value = false;
      if (res.success) {
        proxy.$modal.msgSuccess(t('text.success.remove'));
        handleQuery();
      } else {
        proxy.$modal.msgError(t('text.error.remove'));
      }
    });
  }).catch(() => {});
}

defineExpose({
  authByGroupId
})


</script>
