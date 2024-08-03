<template>
  <el-config-provider :locale="messages[locale]">
    <RouterView/>
  </el-config-provider>
</template>

<script setup>
import useSettingsStore from '@/store/modules/settings'
import {handleThemeStyle} from '@/utils/theme'
import {ElConfigProvider} from 'element-plus'
import {useI18n} from 'vue-i18n'

const {locale, messages} = useI18n()

onMounted(() => {
  nextTick(() => {
    // 初始化主题样式
    handleThemeStyle(useSettingsStore().theme)
  })
})
</script>
