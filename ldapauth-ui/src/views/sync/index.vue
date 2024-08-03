<template>
   <div class="app-container">
     <el-tabs v-model="activeName" class="datya-tabs">
       <el-tab-pane :label="t('sync.tabs.dingtalk')" name="dingtalk">
         <component-dingding v-if="activeName == 'dingtalk'" />
       </el-tab-pane>
       <el-tab-pane :label="t('sync.tabs.feishu')" name="feishu">
         <component-feishu v-if="activeName == 'feishu'" />
       </el-tab-pane>
       <el-tab-pane :label="t('sync.tabs.workweixin')" name="workweixin">
         <component-workweixin v-if="activeName == 'workweixin'" />
       </el-tab-pane>
     </el-tabs>
   </div>
</template>

<script setup name="Sync-Jobs">

import ComponentDingding from "@/views/sync/components/dingding.vue"
import ComponentFeishu from "@/views/sync/components/feishu.vue"
import ComponentWorkweixin from "@/views/sync/components/workweixin.vue"
import { ref } from 'vue'
import {useI18n} from "vue-i18n";

const { t } = useI18n()
const activeName = ref('dingtalk')

</script>
