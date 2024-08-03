<template>
    <div class="app-container">
       <el-form :model="queryParams" ref="queryRef"  :inline="true" v-show="showSearch">
         <el-form-item :label="t('audit.applogin.displayName')"  prop="displayName">
           <el-input
             v-model="queryParams.displayName"
             :placeholder="t('audit.applogin.placeholder.displayName')"
             clearable
             style="width: 220px"
             @keyup.enter="handleQuery"
           />
         </el-form-item>
         <el-form-item :label="t('audit.applogin.appName')"  prop="appName">
           <el-input
             v-model="queryParams.appName"
             :placeholder="t('audit.applogin.placeholder.appName')"
             clearable
             style="width: 220px"
             @keyup.enter="handleQuery"
           />
         </el-form-item>
         <el-form-item :label="t('audit.applogin.rangeDate')" prop="rangeDate">
           <el-date-picker
               v-model="rangeDate"
               type="datetimerange"
               value-format="YYYY-MM-DD hh:mm:ss"
               range-separator="-"
               :start-placeholder="t('audit.applogin.placeholder.startDate')"
               :end-placeholder="t('audit.applogin.placeholder.endDate')">
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
       <el-table :data="logList" v-loading="loading">
         <el-table-column :label="t('audit.applogin.userId')" prop="userId" align="center" :show-overflow-tooltip="true" />
         <el-table-column :label="t('audit.applogin.displayName')" prop="displayName" align="center" :show-overflow-tooltip="true" />
         <el-table-column :label="t('audit.applogin.appName')" prop="appName" align="center" :show-overflow-tooltip="true" />
         <el-table-column :label="t('audit.applogin.createTime')" prop="createTime" align="center" :show-overflow-tooltip="true"/>
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

 <script setup name="AppLoginLog">
 import {list} from '@/api/system/audit/appLoginLog.js'
 import { useI18n } from 'vue-i18n'
 import { reactive, ref } from "vue";

 const { t } = useI18n()
 const showSearch = ref(true);
 const { proxy } = getCurrentInstance();

 const loading = ref(true)
 const logList=ref([])
 const total=ref(0)
 const rangeDate = ref([proxy.parseTime(new Date(new Date().getTime() - 24 * 60 * 60 * 1000), "{y}-{m}-{d} {h}:{i}:{s}"),
 proxy.parseTime(new Date(), '{y}-{m}-{d} {h}:{i}:{s}')])

 const data=reactive({
   queryParams: {
     displayName: undefined,
     appName:undefined,
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
