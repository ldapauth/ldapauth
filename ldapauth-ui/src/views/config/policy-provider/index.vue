<template>
   <div class="app-container">
     <el-tabs v-model="activeName" class="datya-tabs">
       <el-tab-pane :label="t('policy.tabs.login')" name="login-policy">
          <config-login-policy v-if="activeName == 'login-policy'"/>
       </el-tab-pane>
       <el-tab-pane :label="t('policy.tabs.password')"  name="password-policy">
         <config-password-policy v-if="activeName == 'password-policy'" />
       </el-tab-pane>
     </el-tabs>
   </div>
</template>

<script setup name="Config-Policy-Provider">

import ConfigLoginPolicy from "@/views/config/policy-provider/login-policy/index.vue"
import ConfigPasswordPolicy from "@/views/config/policy-provider/password-policy/index.vue"
import { ref } from 'vue'
import {useI18n} from "vue-i18n";
const { t } = useI18n()

const activeName = ref('login-policy')

</script>
