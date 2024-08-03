<template>
   <div class="app-container">
      <el-form :model="queryParams" ref="queryRef"  :inline="true" v-show="showSearch">

        <el-form-item :label="t('audit.login.displayName')" prop="displayName">
          <el-input
              v-model="queryParams.displayName"
              :placeholder="t('audit.login.placeholder.displayName')"
              clearable
              style="width: 220px"
              @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item :label="t('audit.login.provider')" prop="provider">
          <el-input
              v-model="queryParams.provider"
              :placeholder="t('audit.login.placeholder.provider')"
              clearable
              style="width: 220px"
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item :label="t('audit.login.ipAddr')" prop="ipAddr">
          <el-input
            v-model="queryParams.ipAddr"
            :placeholder="t('audit.login.placeholder.ipAddr')"
            clearable
            style="width: 220px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item :label="t('audit.login.status')" prop="status">
          <el-select
            v-model="queryParams.status"
            :placeholder="t('audit.login.placeholder.status')"
            style="width: 180px"
            clearable
          >
            <el-option
              v-for="dict in sys_sync_result_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('audit.login.rangeDate')" prop="rangeDate">
          <el-date-picker
            v-model="rangeDate"
            type="datetimerange"
            value-format="YYYY-MM-DD hh:mm:ss"
            range-separator="-"
            :start-placeholder="t('audit.login.placeholder.startDate')"
            :end-placeholder="t('audit.login.placeholder.endDate')">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button  @click="handleQuery" type="primary">
            {{ t('text.query') }}
          </el-button>
          <el-button  @click="handleReset">
            {{ t('text.reset') }}
          </el-button>
        </el-form-item>
      </el-form>

    <!-- 表格数据 -->
    <div class="content-main-table">
      <el-table :data="logList" v-loading="loading"
                @selection-change="handleSelectionChange"
      >
        <el-table-column :label="t('audit.login.userId')" prop="userId" align="center" :show-overflow-tooltip="true" />
        <el-table-column :label="t('audit.login.displayName')" prop="displayName" align="center" :show-overflow-tooltip="true" />
        <el-table-column :label="t('audit.login.provider')" prop="provider" align="center" :show-overflow-tooltip="true" />
        <el-table-column :label="t('audit.login.ipAddr')" prop="ipAddr" align="center" :show-overflow-tooltip="true"/>
        <el-table-column :label="t('audit.login.operateSystem')"  prop="operateSystem" align="center" :show-overflow-tooltip="true"/>
        <el-table-column :label="t('audit.login.browser')" prop="browser" align="center" :show-overflow-tooltip="true"/>
        <el-table-column :label="t('audit.login.status')" prop="status" align="center" width="100">
          <template #default="scope">
            <span v-if="scope.row.status === '0'"><el-icon color="green"><SuccessFilled class="success" /></el-icon></span>
            <span v-if="scope.row.status === '1'"><el-icon color="#808080"><CircleCloseFilled /></el-icon></span>
          </template>
        </el-table-column>
        <el-table-column :label="t('audit.login.message')" prop="message" align="center" :show-overflow-tooltip="true"/>
        <el-table-column :label="t('audit.login.createTime')" prop="createTime" align="center" :show-overflow-tooltip="true"/>
      </el-table>
    </div>
    <pagination v-if="total>0" :total="total"
                v-model:page="queryParams.pageNum"
                v-model:limit="queryParams.pageSize"
                @pagination="getList"
                :page-sizes="queryParams.pageSizeOptions"
      />
  </div>
</template>

<script setup name="LoginLog">
import {list} from '@/api/system/audit/loginLog.js'
import { useI18n } from 'vue-i18n'
import { reactive, ref } from "vue";

const { t } = useI18n()
const showSearch = ref(true);
const { proxy } = getCurrentInstance();
const { sys_sync_result_status } = proxy.useDict("sys_sync_result_status");

const loading = ref(true)
const logList=ref([])
const total=ref(0)
const rangeDate = ref([proxy.parseTime(new Date(new Date().getTime() - 24 * 60 * 60 * 1000), "{y}-{m}-{d} {h}:{i}:{s}"),
proxy.parseTime(new Date(), '{y}-{m}-{d} {h}:{i}:{s}')])

const data=reactive({
  queryParams: {
    displayName: undefined,
    loginType: undefined,
    ipAddr: undefined,
    status: undefined,
    startDate: undefined,
    endDate: undefined,
    pageSize: 10,
    pageNum: 1,
    pageSizeOptions: [10, 20, 50],
    orderByColumn: 'createTime',
    isAsc: 'descending'
  },
  rules: {
    /* name: [{ required: true, message: t('group.nameMessage'), trigger: "blur" }], */
  },
})

const { queryParams } = toRefs(data);

  /** 查询列表 */
  function getList() {
    loading.value = true;
    if (rangeDate.value && rangeDate.value.length > 1) {
      queryParams.value.startDate = rangeDate.value[0]
      queryParams.value.endDate = rangeDate.value[1]
    } else {
      queryParams.value.startDate = null
      queryParams.value.endDate = null
    }
    list(queryParams.value).then(res => {
      logList.value = res.records
      total.value = res.total
      loading.value = false
    })
  }

  /** 搜索按钮操作 */
  function handleQuery() {
    queryParams.value.pageNum = 1;
    getList();
  }

  /** 重置按钮操作 */
  function handleReset() {
    proxy.resetForm("queryRef");
    handleQuery();
  }

  getList()


</script>
<style lang="scss" scoped>
</style>
