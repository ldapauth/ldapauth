<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6" :xs="24">
        <el-card class="box-card">
          <template v-slot:header>
            <div class="clearfix">
              <span>{{t('profile.personal')}}</span>
            </div>
          </template>
          <div>
            <div class="text-center">
              <userAvatar/>
            </div>
            <ul class="list-group list-group-striped">
              <li class="list-group-item">
                <svg-icon icon-class="user"/>
                {{t('user.username')}}
                <div class="pull-right">
                  <span v-if="state.user.username && state.user.username.length <= 20">{{ state.user.username }}</span>
                  <span v-else>
                    <el-tooltip class="item" effect="dark" :content="state.user.username" placement="top">
                      <span>{{ state.user.username ? state.user.username.slice(0, 20) + '...' : '' }}</span>
                    </el-tooltip>
                  </span>
                </div>

              </li>
              <li class="list-group-item">
                <span>{{t('user.displayName')}}</span>
                <div class="pull-right">{{ state.user.displayName }}</div>
              </li>
              <li class="list-group-item">
                <span>{{t('user.nickname')}}</span>
                <div class="pull-right">{{ state.user.nickName }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="color"/>
                {{t('user.gender')}}
                <div class="pull-right">
                  <span v-if="state.user.gender === 1">{{t('profile.male')}}</span>
                  <span v-else-if="state.user.gender === 2">{{t('profile.female')}}</span>
                  <span v-else>{{t('profile.other')}}</span>
                </div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="phone"/>
                {{t('user.mobile')}}
                <div class="pull-right">{{ state.user.mobile }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="email"/>
                {{t('user.email')}}
                <div class="pull-right">
                  <span v-if="state.user.email && state.user.email.length <= 20">{{ state.user.email }}</span>
                  <span v-else>
                   <el-tooltip class="item" effect="dark" :content="state.user.email" placement="top">
                     <span>{{ state.user.email ? state.user.email.slice(0, 20) + '...' : '' }}</span>
                   </el-tooltip>
                  </span>
                </div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="tree"/>
                {{t('user.belong')}}
                <div class="pull-right">{{ state.user.departmentName }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="guide"/>
                {{t('org.from')}}
                <dict-tag class="pull-right" :options="sys_data_object_from" :value="state.user.objectFrom"/>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="date"/>
                {{t('user.birthDate')}}
                <div class="pull-right">{{ state.user.birthDate }}</div>
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>
      <el-col :span="18" :xs="24">
        <el-card>
          <template v-slot:header>
            <div class="clearfix">
              <span>{{t('profile.basic')}}</span>
            </div>
          </template>
          <el-tabs v-model="activeTab">
            <el-tab-pane :label="t('profile.basic')" name="userinfo">
              <userInfo :user="state.user" :genderOptions="sys_user_sex" @profileDisplay="profileDisplay"/>
            </el-tab-pane>
            <el-tab-pane :label="t('profile.updatePwd')" name="resetPwd">
              <resetPwd :pwdPolicy="pwdPolicy"/>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Profile">
import userAvatar from "./userAvatar.vue";
import userInfo from "./userInfo.vue";
import resetPwd from "./resetPwd.vue";
import {currentUser, getPwdPolicy} from "@/api/login.js";
import {useI18n} from "vue-i18n";

const {proxy} = getCurrentInstance();
const {sys_data_object_from, sys_user_sex} = proxy.useDict("sys_data_object_from", "sys_user_sex");

const {t} = useI18n();

const activeTab = ref("userinfo");
const state = reactive({
  user: {},
});

//密码策略
const pwdPolicy = ref({})

function getUser() {
  currentUser().then(response => {
    state.user = response.data;
  });
}

//获取密码策略
function passwordPolicy() {
  getPwdPolicy().then(res => {
    if (res.code === 200) {
      pwdPolicy.value = res.data;
    } else {
      proxy.$modal.msgError(t('profile.pwdPolicy'))
    }
  })
}

function profileDisplay(data) {
  state.user.displayName = data.displayName
  state.user.nickName = data.nickName
  state.user.mobile = data.mobile
  state.user.email = data.email
  state.user.birthDate = data.birthDate
  state.user.gender = data.gender
}

getUser();
passwordPolicy();
</script>
