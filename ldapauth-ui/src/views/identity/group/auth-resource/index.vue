<template>
  <el-drawer
      v-model="drawer"
      :title="t('group.authResource')"
      size="1000px"
  >
    <div class="app-container1" style="margin-top: -10px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="t('group.name')" prop="groupName">
          <el-input v-model="currentGroupName" disabled :placeholder="t('group.namePlaceholder')" />
        </el-form-item>
        <el-form-item :label="t('group.resource.menu')">
          <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand($event, 'menu')">{{t('text.expandOrFold')}}</el-checkbox>
          <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll($event, 'menu')">{{t('text.selectAllOrNot')}}</el-checkbox>
          <el-checkbox v-model="form.menuCheckStrictly" @change="handleCheckedTreeConnect($event, 'menu')">{{t('text.connectChild')}}</el-checkbox>
          <el-tree
              class="tree-border"
              :data="menuOptions"
              show-checkbox
              ref="menuRef"
              node-key="id"
              :check-strictly="!form.menuCheckStrictly"
              :empty-text="t('text.treeLoading')"
              :props="{ value:'id',label: 'name', children: 'children' }"
          ></el-tree>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">{{t('text.confirm')}}</el-button>
          <el-button @click="cancel">{{t('text.close')}}</el-button>
        </el-form-item>

      </el-form>

    </div>
  </el-drawer>
</template>

<script setup name="group-auth-resource">

import { authResource, getAuthResourceIds} from "@/api/system/group.js";

import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const router = useRouter();

const { proxy } = getCurrentInstance();
const drawer = ref(false)
const menuRef = ref(null);
const deptRef = ref(null);
const menuOptions = ref([]);
const menuExpand = ref(false);
const menuNodeAll = ref(false);
const deptExpand = ref(true);
import {systemTree} from "@/api/system/menu.js";
const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    groupId: undefined,
    username: undefined,
    displayName: undefined,
    mobile: undefined
  }
});
const { queryParams, form, rules } = toRefs(data);

//定义当前分组ID
const currentGroupId = ref(null)

//定义当前分组ID
const currentGroupName = ref(null)

function authByGroupId(groupId,groupName) {
  currentGroupId.value = groupId;
  currentGroupName.value = groupName;
  queryParams.value.groupId = groupId;
  drawer.value = true;
  getMenuTreeselect()
}


/** 查询菜单树结构 */
function getMenuTreeselect() {
  systemTree().then(response => {
    if(response.success) {
      menuOptions.value = response.data;
    }
    //加载已授权的资源勾选状态
    getAuthResourceIds(currentGroupId.value).then(res => {
      if (res.success) {
        nextTick(() => {
          let checkedKeys = res.data.ids;
          checkedKeys.forEach((v) => {
            nextTick(() => {
              menuRef.value.setChecked(v, true, false);
            });
          });
        });
      }
    });
  });
}


/** 树权限（展开/折叠）*/
function handleCheckedTreeExpand(value, type) {
  if (type == "menu") {
    let treeList = menuOptions.value;
    for (let i = 0; i < treeList.length; i++) {
      menuRef.value.store.nodesMap[treeList[i].id].expanded = value;
    }
  } else if (type == "dept") {
    let treeList = deptOptions.value;
    for (let i = 0; i < treeList.length; i++) {
      deptRef.value.store.nodesMap[treeList[i].id].expanded = value;
    }
  }
}

/** 树权限（全选/全不选） */
function handleCheckedTreeNodeAll(value, type) {
  if (type == "menu") {
    menuRef.value.setCheckedNodes(value ? menuOptions.value : []);
  } else if (type == "dept") {
    deptRef.value.setCheckedNodes(value ? deptOptions.value : []);
  }
}

/** 树权限（父子联动） */
function handleCheckedTreeConnect(value, type) {
  if (type == "menu") {
    form.value.menuCheckStrictly = value ? true : false;
  } else if (type == "dept") {
    form.value.deptCheckStrictly = value ? true : false;
  }
}


/** 所有菜单节点数据 */
function getMenuAllCheckedKeys() {
  // 目前被选中的菜单节点
  let checkedKeys = menuRef.value.getCheckedKeys();
  // 半选中的菜单节点
  let halfCheckedKeys = menuRef.value.getHalfCheckedKeys();
  checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
  return checkedKeys;
}

/** 提交按钮 */
function submitForm() {
  form.value.ids = [currentGroupId.value]
  form.value.targetIds = getMenuAllCheckedKeys();
  authResource(form.value).then(res => {
    if (res.success) {
      proxy.$modal.msgSuccess(t("text.success.action"));
    } else {
      proxy.$modal.msgError(t("text.error.action"));
    }
  });
}

/** 重置新增的表单以及其他数据  */
function reset() {
  if (menuRef.value != undefined) {
    menuRef.value.setCheckedKeys([]);
  }
  menuExpand.value = false;
  menuNodeAll.value = false;
  deptExpand.value = true;
  deptNodeAll.value = false;
  form.value = {
    roleId: undefined,
    roleName: undefined,
    roleKey: undefined,
    roleSort: 0,
    status: "0",
    menuIds: [],
    deptIds: [],
    menuCheckStrictly: true,
    deptCheckStrictly: true,
    remark: undefined
  };
  proxy.resetForm("roleRef");
}

/** 取消按钮 */
function cancel() {
  drawer.value = false;
  reset();
}


defineExpose({
  authByGroupId
})


</script>
