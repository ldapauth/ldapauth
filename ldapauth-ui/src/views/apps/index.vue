<template>
  <div class="app-container">
      <el-form :model="queryParams" ref="queryForm"  :inline="true" v-show="showSearch"><!-- size="small" -->
        <el-form-item :label="t('apps.form.label.appName')">
          <el-input
            v-model="queryParams.appName"
            :placeholder="t('apps.form.search.placeholder.appName')"
            clearable
            style="width: 220px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item :label="t('apps.form.label.appCode')">
          <el-input
            v-model="queryParams.appCode"
            :placeholder="t('apps.form.search.placeholder.appCode')"
            clearable
            style="width: 220px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item :label="t('apps.form.label.protocol')">
          <el-select
            v-model="queryParams.protocol"
            :placeholder="t('apps.form.search.placeholder.protocol')"
            style="width: 120px"
            clearable
          >
            <el-option label="OIDC" value="0"></el-option>
            <el-option label="SAML" value="1"></el-option>
            <el-option label="JWT" value="2"></el-option>
            <el-option label="CAS" value="3"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item :label="t('apps.form.label.status')">
          <el-select
            v-model="queryParams.status"
            :placeholder="t('apps.form.search.placeholder.status')"
            style="width: 120px"
            clearable
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
          <el-button  @click="handleQuery" type="primary"><!-- size="small" -->
            {{ t('text.query') }}
          </el-button>
          <el-button  @click="handleReset"><!--  -->
            {{ t('text.reset') }}
          </el-button>
        </el-form-item>
      </el-form>
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <div class="form-right-btns">
            <el-button type="primary" @click="handleAdd">
              {{t('apps.addApp')}}
            </el-button>
          </div>
        </el-col>
      </el-row>


    <!-- 表格数据 -->
    <div class="content-main-table">
      <el-table :data="appList" v-loading="loading"
                @selection-change="handleSelectionChange"
      >
        <el-table-column :label="t('apps.form.label.icon')" align="center" >
          <template  #default="scope">
            <el-avatar :src="privateImage(scope.row.icon)" @error="true">
              <!-- 如果加载失败则用默认图片-->
              <!-- <img :src="require('@/assets/app/deft.jpg')" /> -->
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column :label="t('apps.form.label.appName')" prop="appName" align="center" :show-overflow-tooltip="true" />
        <el-table-column :label="t('apps.form.label.appCode')" prop="appCode" align="center" :show-overflow-tooltip="true"/>
        <el-table-column :label="t('apps.form.label.path')" prop="path" align="center" :show-overflow-tooltip="true"/>
        <el-table-column :label="t('apps.form.label.protocol')" prop="protocol" align="center" width="120">
          <template  #default="scope">
            <el-tag v-if="scope.row.protocol == 0" type="info">OIDC</el-tag>
            <el-tag v-if="scope.row.protocol == 1" type="info">SAML</el-tag>
            <!-- <el-tag v-if="scope.row.protocol == 2" type="info">OIDC</el-tag> -->
            <el-tag v-if="scope.row.protocol == 2" type="info">JWT</el-tag>
            <el-tag v-if="scope.row.protocol == 3" type="info">CAS</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('apps.form.label.status')" prop="status" align="center" min-width="100">
          <template  #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="0"
              :inactive-value="1"
              @change="changeStatus(scope.row)">
            ><!-- :inactive-text="t('text.enable')"
              :active-text="t('text.disabled')" -->
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column :label="t('apps.form.label.createTime')" prop="createTime" align="center" width="180"/>
        <el-table-column :label="t('text.action')" align="center" width="300" fixed="right">
          <template  #default="scope">
            <!-- <el-button size="mini" type="text" @click.native="sso(scope.row)">{{ t('apps.access') }}</el-button> -->
            <!-- <el-divider direction="vertical"></el-divider> -->
            <!-- <el-button size="mini" type="text" @click.native="handleAuthOpen(scope.row)">{{ t('apps.function') }}</el-button> -->
           <!--  <el-divider direction="vertical"></el-divider> -->
            <el-button
              v-hasPermi="['apps:edit']"
              @click="handleEdit(scope.row)"
            >
              {{ t('text.edit') }}
            </el-button><!--  size="mini" type="text"-->
            <el-divider direction="vertical"></el-divider>
            <el-button
              v-hasPermi="['apps:delete']"
              @click="handleDelete(scope.row)"
              type="danger"
            >
              {{ t('text.delete') }}
            </el-button><!-- size="mini"  type="text" -->
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- <div class="content-main-pagination"> -->
      <pagination v-if="total>0" :total="total"
                  v-model:page="queryParams.pageNum"
                  v-model:limit="queryParams.pageSize"
                  @pagination="getList"
                  :page-sizes="queryParams.pageSizeOptions"/>
    <!-- </div> -->

    <!-- oidc表单-抽屉弹出-->
    <OidcForm ref="oidcForm" @getList="getList"></OidcForm>

    <!-- cas表单-抽屉弹出-->
    <CasForm ref="casForm" @getList="getList"></CasForm>

    <!-- oidc表单-抽屉弹出-->
    <!-- <oidc-form ref="oidcForm" @getList="getList"></oidc-form> -->

    <!-- jwt表单-抽屉弹出-->
    <JwtForm ref="jwtForm" @getList="getList"></JwtForm>

    <!-- saml表单-抽屉弹出-->
    <SamlForm ref="samlForm" @getList="getList"></SamlForm>


    <!--关联权限系统对话框-->
    <!-- <el-dialog
      :title="title"
      :visible.sync="authOpen"
      :append-to-body="true"
      :wrapperClosable="false"
      :before-close="handleClose"
      width="480px"
      >
      <div class="edit-form-box">
        <div class="form-box">
          <sys-auth  ref="sysAuth" :appId="appId"></sys-auth>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAuth"> {{ t('text.submit') }}</el-button>
        <el-button @click="cancel"> {{ t('text.cancel') }}</el-button>
      </div>
    </el-dialog> -->
  </div>
</template>

<script setup name="Apps">
/*
import {, getAppdetails, list, update, updateStatus, updateSys} from '@/api/access/instapp/instApp'
import sysAuth from './sys-auth.vue' */
import CasForm from "@/views/apps/form/casForm";
import JwtForm from "@/views/apps/form/jwtForm";
import OidcForm from "@/views/apps/form/oidcForm";
import SamlForm from "@/views/apps/form/samlForm";
import {list,deleteBatch,active,disable} from '@/api/system/apps'
import { useI18n } from 'vue-i18n'
import { reactive, ref } from "vue";
import {privateImage} from "@/utils/privateImages.js";


const { t } = useI18n()
const router = useRouter();
const showSearch = ref(true);
const { proxy } = getCurrentInstance();
const { sys_normal_disable ,sys_object_from} = proxy.useDict("sys_normal_disable","sys_object_from");

const loading = ref(true)
const appList=ref([])
const id=ref(null)
const title=ref('')
const open=ref(false)
const authOpen=ref(false)
const total=ref(0)
const ids=ref([])
const appId=ref(null)
const selectionList=ref([])
const casForm = ref(null)
const jwtForm = ref(null)
const oidcForm = ref(null)
const samlForm = ref(null)

const data=reactive({
  queryParams: {
        protocol: undefined,
        appName: undefined,
        code: undefined,
        status: undefined,
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

const { queryParams, form, rules } = toRefs(data);

  /** 查询列表 */
  function getList() {
    loading.value = true;
    list(queryParams.value).then(res => {
      appList.value = res.records
      total.value = res.total
      loading.value = false
    })/* ! */
  }

  /** 搜索按钮操作 */
  function handleQuery() {
    queryParams.value.pageNum = 1;
    getList();
  }

  /** 重置按钮操作 */
  function handleReset() {
    proxy.resetForm("queryForm");
    handleQuery();
  }

  /*跳转新增界面*/
  function handleAdd() {
    /* this.$router.push({path:'/access/apps/add'}) */
    router.push({path:'/apps/add'})
  }/* ! */

  /** 多选操作*/
  function handleSelectionChange(selection) {
    selectionList.value = selection
    ids.value = selectionList.value.map(item => item.id)
  }/* ! */

  //二次提示验证
  function changeStatus(row) {
    if(row.status == 1){
      proxy.$modal
      .confirm("是否禁用？")
      .then(() => {
        disable(row.id).then(res => {
          if(res.code == 200) {
            proxy.$modal.msgSuccess(t('text.success.success'))
            getList();
          } else {
            //提示异常信息
            proxy.$modal.msgError(res.message);
          }
        })
      })
      .catch((e) => {
        getList();
      });
    } else {
      proxy.$modal
        .confirm("是否启用？")
        .then(() => {
          active(row.id).then(res => {
            if(res.code == 200) {
              proxy.$modal.msgSuccess(t('text.success.success'))
              getList();
            } else {
              //提示异常信息
              proxy.$modal.msgError(res.message);
            }
          })
        })
        .catch((e) => {
          getList();
        });
    }
  }

  function handleEdit(row) {
    console.log(row)
      //模板选择
      if (row.protocol === 0) {
        //默认为空，如果是编辑，则进行赋值ID
        oidcForm.value.load(row.id);
      } else if(row.protocol === 1) {
        //默认为空，如果是编辑，则进行赋值ID
        samlForm.value.load(row.id);
      }  else if(row.protocol === 2) {
        //默认为空，如果是编辑，则进行赋值ID
        jwtForm.value.load(row.id);
      } else if(row.protocol === 3) {
        //默认为空，如果是编辑，则进行赋值ID
        casForm.value.load(row.id);
      }
  }

  //单个删除
  function handleDelete(row) {
    if (row.status === 0) {
      proxy.$modal.msgError("应用启用状态无法删除");
      return;
    }
    proxy.$modal.confirm("是否确认删除？").then(function () {
      return deleteBatch(row.id);
    })
    .then((res) => {
      proxy.$modal.msgSuccess("删除成功");
      getList();
    })
  }

  //批量删除
  /* function deleteBatch() {
    for (const item of selectionList.value) {
      if (item.status === 0) {
        proxy.$modal.msgError(this.t('sync.text.message10'));
        return; // 如果 status 为 0，则退出 deleteBatch 函数
      }
    }
    let that = this;
    this.$modal.confirm(this.t('sync.text.message11')).then(function () {
        return deleteBatch(that.ids);
      })
      .then((res) => {
        this.$modal.msgSuccess(this.t('sync.text.message12'));
        this.getList();
      })
  } */

  getList()
    // 打开授权页面
    /* function handleAuthOpen(row) {
      this.title = this.t('apps.function')
      this.ids = [row.id]
      this.appId = row.id
      this.authOpen = true
    } */
    // 打开授权页面
    /* function sso(row) {
      getAppdetails(row.id).then(res => {
        if (res.code == 200) {
          console.log(111,row)
          if(row.protocol  === 1 &&  row.homeUri.startsWith('https://auth.huaweicloud.com/authui/federation/websso')){
            window.open(row.homeUri);
            return
          }
          let ssoUrl = "1";
          if(row.protocol  === 0){
            ssoUrl = "/authz/oidc/v20/authorize?client_id="+row.clientId+"&response_type=code&state=idpinit&redirect_uri="+res.data.details.redirectUri;
          } else if(row.protocol  === 1){
            ssoUrl = "/authz/saml20/idpinit/"+row.id;
          } else if(row.protocol  === 2){
            ssoUrl = "/authz/oidc/authorize?client_id="+row.clientId+"&response_type=code&state=idpinit&redirect_uri="+res.data.details.redirectUri;
          } else if(row.protocol  === 3){
            ssoUrl = "/authz/jwt/" + row.id;
          } else if(row.protocol  === 4) {
            ssoUrl = "/authz/cas/login?service=" +encodeURIComponent(res.data.details.serverNames);
          }
          window.open(ssoUrl);
        }
      })
    } */
    /* function submitAuth() {
      let sysId = this.$refs['sysAuth'].form.sysId
      let appId = this.appId
      updateSys({id: appId, sysId: sysId}).then(res => {
        if(res.code ===200){
          this.msgSuccess(this.t('text.bind.success'));
          this.$refs['sysAuth'].getList()
          this.authOpen = false
        } else {
          this.msgError(res.message);
          this.authOpen = true
        }
      });
    } */
    /* function handleClose(done) {
      this.open = false;
      this.authOpen = false
      this.$refs['sysAuth'].getList()
    } */
    /* function cancel() {
      this.open = false;
      this.authOpen = false
      this.$refs['sysAuth'].getList()
    } */




</script>
<style lang="scss" scoped>
:v-deep .el-switch__core {
  width: 64px;
  height: 24px;
  border-radius: 100px;
  border: none;
}

:v-deep .el-switch__core::after {
  width: 20px;
  height: 20px;
  top: 2px;
}

:v-deep .el-switch.is-checked .el-switch__core::after {
  margin-left: -21px;
}

/*关闭时文字位置设置*/
:v-deep .el-switch__label--right {
  position: absolute;
  z-index: 1;
  right: 6px;
  margin-left: 0px;
  color: rgba(255, 255, 255, 0.9019607843137255);

  span {
    font-size: 12px;
  }
}

/* 激活时另一个文字消失 */
:v-deep .el-switch__label.is-active {
  display: none;
}

/*开启时文字位置设置*/
:v-deep .el-switch__label--left {
  position: absolute;
  z-index: 1;
  left: 5px;
  margin-right: 0px;
  color: rgba(255, 255, 255, 0.9019607843137255);

  span {
    font-size: 12px;
  }
}
</style>
