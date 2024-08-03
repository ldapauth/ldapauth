<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item :label="t('sms.provider')" prop="provider">
        <el-input
            v-model="queryParams.provider"
            :placeholder="t('sms.placeholder.provider')"
            clearable
            @keyup.enter.native="handleQuery"
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
            @click="handleAdd"
        >{{t('text.add')}}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            @click="handleDeleteBatch"
            :disabled="ids.length === 0"
            v-hasPermi="['sms:delete']"
        >{{t('text.delete')}}</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center"/>
      <el-table-column
          :label="t('sms.provider')" align="left"
          prop="provider"
          :show-overflow-tooltip="true"
          min-width="100"
      />
      <el-table-column
          label="appKey" align="left"
          prop="appKey"
          :show-overflow-tooltip="true"
          min-width="100"
      />
      <el-table-column
          :label="t('sms.sign')" align="left"
          prop="signName"
          :show-overflow-tooltip="true"
          min-width="100"
      />
      <el-table-column prop="status" :label="t('text.status.status')" align="center" min-width="100"
      >
        <template #default="scope">
          <el-switch
              :width="40"
              v-model="scope.row.status"
              :active-value="0"
              :inactive-value="1"
              @change="handleStatusChange(scope.row)"
          >
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column :label="t('text.createdDate')" align="left" prop="createTime" min-width="100"/>
      <el-table-column :label="t('text.action')" align="center" width="180" v-hasPermi="['sms:edit', 'sms:delete']">
        <template #default="scope">
          <el-button @click="handleUpdate(scope.row)" v-hasPermi="['sms:edit']">{{t('text.edit')}}</el-button>
          <el-button type="danger" @click="handleDelete(scope.row)" v-hasPermi="['sms:delete']">{{t('text.delete')}}</el-button>
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
    <!--新增或修改对话框-->
    <ProviderEdit :title="title" :open="open" :formId="id" @dialogOfClosedMethods="dialogOfClosedMethods"></ProviderEdit>
  </div>
</template>

<script setup name="sms-provider">
import {activeProvider, disableProvider, listProviders} from "@/api/system/config/smsProvider.js";
import {useI18n} from "vue-i18n";
import ProviderEdit from "./provider-edit.vue"
import {deleteBatch} from "@/api/system/config/smsProvider.js";

const { t } = useI18n()

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    provider: undefined,
  }
});

const { proxy } = getCurrentInstance();

const {queryParams} = toRefs(data);
const loading  = ref(true);
const list = ref([]);
const total = ref(0);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const id = ref(undefined);
const title = ref("");
const open = ref(false);

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 查询列表 */
function getList() {
  loading.value = true;
  listProviders(queryParams.value).then((response) => {
    list.value = response.data.records;
    total.value = response.data.total;
    loading.value = false;
  });
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    provider: undefined,
  }
  handleQuery();
}

/** 选择条数  */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  id.value = undefined;
  title.value = t('sms.addSms')
  open.value = true;
}

/** 用户修改或新增框关闭事件*/
function dialogOfClosedMethods(val) {
  open.value = false;
  id.value = undefined;
  getList();
}

/** 修改按钮操作 */
function handleUpdate(row) {
  id.value = row.id;
  title.value = t('sms.edit');
  open.value = true;
}

function handleDelete(row) {
  proxy.$modal.confirm(t('org.deleteTip1') + row.provider + t('org.deleteTip2')).then(function() {
    return deleteBatch({ids: [row.id]});
  }).then(res => {
    if (res.code === 200) {
      getList();
      proxy.$modal.msgSuccess(t('org.success.delete'));
    } else {
      proxy.$modal.msgError(res.message);
    }
  }).catch(() => {});
}

/** 批量删除 */
function handleDeleteBatch() {
  let data = {
    ids: ids.value
  }
  proxy.$modal
      .confirm(t('org.confirmDelete'))
      .then(function () {
        return deleteBatch(data);
      })
      .then(res => {
        if (res.code === 200) {
          proxy.$modal.msgSuccess(t('org.success.delete'))
          getList();
        } else {
          proxy.$modal.msgError(res.message);
        }
      }).catch(error => {
  })
}

/** 服务商状态修改  */
function handleStatusChange(row) {
  let text = row.status === 0 ? t('org.button.active') : t('org.button.disable');
  let operation = row.status === 0 ? activeProvider : disableProvider;

  proxy.$modal.confirm(t('org.confirmTip1') + text + t('org.confirmTip3') + '"' + row.provider + t('sms.tip'))
      .then(function () {
        return operation(row.id);
      }).then(res => {
    if (res.code === 200) {
      if (row.status === 0) {
        getList();
      }
      proxy.$modal.msgSuccess(res.data);
    } else {
      row.status = row.status === 0 ? 1 : 0;
      proxy.$modal.msgError(res.message);
    }
  }).catch(function () {
    row.status = row.status === 0 ? 1 : 0;
  });
}

getList();
</script>
