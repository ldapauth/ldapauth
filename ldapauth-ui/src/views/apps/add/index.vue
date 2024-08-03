<template>
  <div class="app-container">
       <!--  <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
          <el-form-item :label="t('apps.add.label.templateName')">
            <el-input
              v-model="queryParams.templateName"
              :placeholder="t('apps.add.placeholder.templateName')"
              clearable
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleQuery">
              {{ t('text.query') }}
            </el-button>
            <el-button  @click="handleReset">
              {{ t('text.reset') }}
            </el-button>
          </el-form-item>
        </el-form> -->


        <el-table :data="appList">
          <el-table-column prop="templateName" :label="t('apps.add.label.templateName')" align="center" width="300" :show-overflow-tooltip="true"/>
          <el-table-column :label="t('apps.add.label.protocol')" align="center" width="150" >
            <template #default="scope">
              <el-tag v-if="scope.row.protocol == 0" type="info">OIDC</el-tag>
              <el-tag v-if="scope.row.protocol == 1" type="info">SAML</el-tag>
              <el-tag v-if="scope.row.protocol == 2" type="info">JWT</el-tag>
              <el-tag v-if="scope.row.protocol == 3" type="info">CAS</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="description" :label="t('apps.add.label.description')" align="center" />
          <el-table-column :label="t('text.action')" align="center" width="100" fixed="right">
            <template #default="scope">
              <el-button
                v-hasPermi="['appAdd:add']"
                @click="handleChange(scope.row)"
              ><!--  type="text" -->
                {{t('apps.addApp')}}
               
              </el-button><!-- size="mini" -->
            </template>
          </el-table-column>
        </el-table>



    <!-- oidc表单-抽屉弹出-->
    <OidcForm ref="oidcForm" @toApplist="toApplist"></OidcForm>

    <!-- cas表单-抽屉弹出-->
    <CasForm ref="casForm" @toApplist="toApplist"></CasForm>

    <!-- jwt表单-抽屉弹出-->
    <JwtForm ref="jwtForm" @toApplist="toApplist"></JwtForm>

    <!-- saml表单-抽屉弹出-->
    <SamlForm ref="samlForm" @toApplist="toApplist"></SamlForm>

  </div>
</template>

<script setup name="appsAdd">
import CasForm from "@/views/apps/form/casForm";
import JwtForm from "@/views/apps/form/jwtForm";
import OidcForm from "@/views/apps/form/oidcForm";
import SamlForm from "@/views/apps/form/samlForm";
import {ref, reactive } from "vue";
import { useI18n } from 'vue-i18n'

  const { t } = useI18n()
  const router = useRouter();

  const casForm = ref(null)
  const jwtForm = ref(null)
  const oidcForm = ref(null)
  const samlForm = ref(null)
  const appList= ref(
    [
      {templateName:'OIDC',protocol:'0',description:'OIDC是OpenID Connect的简称，OIDC=(Identity, Authentication) + OAuth 2.0。平台使用 OIDC 进行分布式站点的单点登录 （SSO）'},
      {templateName:'SAML',protocol:'1',description:'SAML（Security Assertion Markup Language，安全断言标记语言，版本 2.0）基于 XML 协议，使用包含断言（Assertion）的安全令牌，在授权方和消费方（应用）之间传递身份信息，实现基于网络跨域的单点登录。SAML 协议是成熟的认证协议，在国内外的公有云和私有云中有非常广泛的运用。'},
      {templateName:'JWT',protocol:'2',description:'JWT（JSON Web Token）是在网络应用环境声明的一种基于 JSON 的开放标准。平台使用 JWT 进行分布式站点的单点登录 （SSO）。'},
      {templateName:'CAS',protocol:'3',description:'CAS（Central Authentication Service，集中式认证服务，版本 2.0）是一种基于挑战、应答的开源单点登录协议。在集成客户端和服务端之间网络通畅的情况下广泛在企业中使用，有集成简便，扩展性强的优点。'},
    ]
  )
  const total= ref(0)
  const data= reactive({
    queryParams: {
        templateName: undefined,
        pageSize: 10,
        pageNum: 1,
        pageSizeOptions: [10, 20, 50],
        orderByColumn: 'createTime',
        isAsc: 'descending'
      },
  })
  const { queryParams } = toRefs(data);

  function toApplist(){
    router.push({path:'/apps/list'})
  }

  function  handleChange(row) {
      //模板选择
      if (row.protocol === '0') {
        //默认为空，如果是编辑，则进行赋值ID
        oidcForm.value.load(undefined);
      } else if(row.protocol === '1') {
        //默认为空，如果是编辑，则进行赋值ID
        samlForm.value.load(undefined);
      } else if(row.protocol === '2') {
        //默认为空，如果是编辑，则进行赋值ID
        jwtForm.value.load(undefined);
      } else if(row.protocol === '3') {
        //默认为空，如果是编辑，则进行赋值ID
        casForm.value.load(undefined);
      }
  }
</script>
<style lang="scss" scoped>

:v-deep  .el-table .cell {
  white-space: initial !important;
}
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
