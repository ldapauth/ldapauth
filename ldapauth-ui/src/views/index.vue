<template>
  <div class="app-container">
      <el-row :gutter="10">
        <el-col v-for="(item, colIndex) in appList" :span="4" :key="item.id" style="margin-bottom: 10px">
          <el-card style="height: 180px;" shadow="hover" @click.native="onAuth(item.id)">

            <div style="display: flex;justify-content: center;align-items: center;height: 100%;flex-direction: column">
              <p style="text-align: center">
                <img class="appListimage" :src="privateImage(item.icon)" alt=""/>
              </p>
              <p style="text-align: center">{{ item.appName }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
  </div>
</template>


<script setup name="MyApps">
import {privateImage} from "@/utils/privateImages.js";


import {ref} from 'vue'
import { useI18n } from 'vue-i18n'
import useUserStore from "@/store/modules/user.js";
import {myApps} from "@/api/system/apps.js";

const { t } = useI18n()

const userStore = useUserStore()
const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();

const appList = ref([]);

const activeIndex = ref('1')
const handleSelect = (key, keyPath) => {
  console.log(key, keyPath)
}

const loadApps = () => {
  myApps().then(res => {
    appList.value = [];
    if (res.success) {
      appList.value = res.data;
    }
  });
}

const onAuth = (appId) => {
  const app = appList.value.find(
      (item) => item.id === appId && (item.protocol === 'Basic' || item.inducer === 'SP')
  );
  if (app) {
    window.open(app.loginUrl);
  } else {
    window.open(`${import.meta.env.VITE_APP_BASE_API}/auth/${appId}`);
  }
}

loadApps();
</script>
<style scoped>
.flex-grow {
  flex-grow: 1;
}
.appListimage {
  width: 65px;
  height: 65px;
  border: 0;
  border-radius: 4px;
}
.app-content {
  padding: 20px;
}
</style>
