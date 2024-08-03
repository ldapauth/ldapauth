<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            @click="handleAdd"
            v-hasPermi="['menu:add']"
        >{{t('text.add')}}</el-button>
      </el-col>
    </el-row>
    <el-table
        v-loading="loading"
        :data="list"
        row-key="id"
    >
      <el-table-column :label="t('menu.name')" align="left" key="name" prop="name"  :show-overflow-tooltip="true" />
      <el-table-column :label="t('menu.classify')" align="center">
        <template #default="scope">
          <dict-tag :options="sys_classify" :value="scope.row.classify" />
        </template>
      </el-table-column>
      <el-table-column :label="t('menu.permission')" align="left" key="permission" prop="permission"  :show-overflow-tooltip="true" />
      <el-table-column :label="t('menu.routePath')" align="left" key="routePath" prop="routePath"  :show-overflow-tooltip="true" />
      <el-table-column :label="t('menu.status')" align="center">
        <template #default="scope">
          <span v-if="scope.row.status === 0"><el-icon color="green"><SuccessFilled class="success" /></el-icon></span>
          <span v-if="scope.row.status === 1"><el-icon color="#808080"><CircleCloseFilled /></el-icon></span>
        </template>
      </el-table-column>
      <el-table-column :label="t('menu.sortOrder')" align="left" prop="sortOrder" width="160"></el-table-column>
      <el-table-column :label="t('text.createdDate')" align="center" prop="createTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="t('text.action')" align="center" width="250" class-name="small-padding fixed-width">
        <template #default="scope">
            <el-button @click="handleUpdate(scope.row)" style ="margin-right: 10px" v-hasPermi="['menu:edit']">{{t('text.edit')}}</el-button>
            <el-dropdown v-hasPermi="['menu:delete', 'menu:add']">
              <el-button>
                {{t('text.moreAction')}}<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <div  v-hasPermi="['menu:add']" >
                    <el-dropdown-item @click="handleChAdd(scope.row.id)">{{t('text.add')}}</el-dropdown-item>
                  </div>
                  <div  v-hasPermi="['menu:delete']">
                    <el-dropdown-item  @click="handleDelete(scope.row)">{{t('text.delete')}}</el-dropdown-item>
                  </div>
                </el-dropdown-menu>
              </template>
            </el-dropdown>


        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改菜单对话框 -->
    <el-dialog :title="title" v-model="open" width="680px" append-to-body>
      <el-form ref="menuRef" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="24" v-if="form.appId != form.parentId">
            <el-form-item :label="t('menu.parentId')">
              <el-tree-select
                  v-model="form.parentId"
                  :data="deptOptions"
                  :props="{ value: 'id', label: 'name', children: 'children' }"
                  value-key="id"
                  :placeholder="t('menu.parentIdPlaceholder')"
                  check-strictly
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="t('menu.classify')" prop="classify">
              <el-radio-group v-model="form.classify">
                <el-radio v-for="dict in sys_classify" :value="dict.value">{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="t('menu.icon')" prop="icon">
              <el-popover
                  placement="bottom-start"
                  :width="540"
                  trigger="click"
              >
                <template #reference>
                  <el-input v-model="form.icon" :placeholder="t('menu.iconPlaceholder')" @blur="showSelectIcon" readonly>
                    <template #prefix>
                      <svg-icon
                          v-if="form.icon"
                          :icon-class="form.icon"
                          class="el-input__icon"
                          style="height: 32px;width: 16px;"
                      />
                      <el-icon v-else style="height: 32px;width: 16px;"><search /></el-icon>
                    </template>
                  </el-input>
                </template>
                <icon-select ref="iconSelectRef" @selected="selected" :active-icon="form.icon" />
              </el-popover>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="t('menu.sortOrder')" prop="sortOrder">
              <el-input-number v-model.number="form.sortOrder" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="t('menu.name')" prop="name">
              <el-input v-model="form.name" :placeholder="t('menu.namePlaceholder')" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item>
              <template #label>
                        <span>
                           <el-tooltip :content="t('menu.frameTip')" placement="top">
                              <el-icon><question-filled /></el-icon>
                           </el-tooltip>{{t('menu.isFrame')}}
                        </span>
              </template>
              <el-radio-group v-model.number="form.isFrame">
                <el-radio
                    v-for="dict in sys_frame"
                    :key="dict.value"
                    :value="dict.value"
                >{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="routePath">
              <template #label>
                        <span>
                           <el-tooltip :content="t('menu.routePathTip')" placement="top">
                              <el-icon><question-filled /></el-icon>
                           </el-tooltip>
                           {{t('menu.routePath')}}
                        </span>
              </template>
              <el-input v-model="form.routePath" :placeholder="t('menu.routePathPlaceholder')" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="routeComponent">
              <template #label>
                        <span>
                           <el-tooltip :content="t('menu.routeComponentTip')" placement="top">
                              <el-icon><question-filled /></el-icon>
                           </el-tooltip>
                           {{t('menu.routeComponent')}}
                        </span>
              </template>
              <el-input v-model="form.routeComponent" :placeholder="t('menu.routeComponentPlaceholder')" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item>
              <el-input v-model="form.permission" :placeholder="t('menu.permissionPlaceholder')" maxlength="100" />
              <template #label>
                        <span>
                           <el-tooltip :content="t('menu.permissionTip')" placement="top">
                              <el-icon><question-filled /></el-icon>
                           </el-tooltip>
                           {{t('menu.permission')}}
                        </span>
              </template>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item>
              <el-input v-model="form.routeQuery" :placeholder="t('menu.routeQueryPlaceholder')" maxlength="255" />
              <template #label>
                        <span>
                           <el-tooltip :content="t('menu.routeQueryTip')" placement="top">
                              <el-icon><question-filled /></el-icon>
                           </el-tooltip>
                           {{t('menu.routeQuery')}}
                        </span>
              </template>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item>
              <template #label>
                        <span>
                           <el-tooltip :content="t('menu.isCacheTip')" placement="top">
                              <el-icon><question-filled /></el-icon>
                           </el-tooltip>
                           {{t('menu.isCache')}}
                        </span>
              </template>
              <el-radio-group v-model.number="form.isCache">
                <el-radio
                    v-for="dict in sys_cache"
                    :key="dict.value"
                    :value="dict.value"
                >{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item>
              <template #label>
                        <span>
                           <el-tooltip :content="t('menu.isVisibleTip')" placement="top">
                              <el-icon><question-filled /></el-icon>
                           </el-tooltip>
                           {{t('menu.isVisible')}}
                        </span>
              </template>
              <el-radio-group v-model.number="form.isVisible">
                <el-radio
                    v-for="dict in sys_visible_hide"
                    :key="dict.value"
                    :value="dict.value"
                >{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item>
              <template #label>
                        <span>
                           <el-tooltip :content="t('menu.statusTip')" placement="top">
                              <el-icon><question-filled /></el-icon>
                           </el-tooltip>
                           {{t('menu.status')}}
                        </span>
              </template>
              <el-radio-group v-model.number="form.status">
                <el-radio
                    v-for="dict in sys_normal_disable"
                    :key="dict.value"
                    :value="dict.value"
                >{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">{{t('text.confirm')}}</el-button>
          <el-button @click="cancel">{{t('text.close')}}</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup name="Config-Menu">
import SvgIcon from "@/components/SvgIcon/index.vue";
import IconSelect from "@/components/IconSelect/index.vue";
import {listMenu, systemTree, getMenu, updateMenu, addMenu, delMenu} from "@/api/system/menu.js";
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const router = useRouter();
const { proxy } = getCurrentInstance();
const { sys_normal_disable, sys_user_sex,sys_classify,sys_show_hide,sys_visible_hide,sys_frame,sys_cache }
    = proxy.useDict("sys_normal_disable", "sys_user_sex","sys_classify","sys_show_hide" ,"sys_visible_hide","sys_frame","sys_cache");
const list = ref([]);
const open = ref(false);
const loading = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const deptOptions = ref(undefined);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: undefined,
    classify: undefined,
    status: undefined,
    appId: 1,
    parentId: undefined
  },
  rules: {
    classify: [{ required: true, message: t('menu.classifyMessage'), trigger: "blur" }],
    name: [{ required: true, message: t('menu.nameMessage'), trigger: "blur" }],
    sortOrder: [{ required: true, message: t('menu.sortOrderMessage'), trigger: "blur" }],
    routePath: [{ required: true, message: t('menu.routePathMessage'), trigger: "blur" }]
  },
});

const { queryParams, form, rules } = toRefs(data);

/** 通过条件过滤节点  */
const filterNode = (value, data) => {
  if (!value) return true;
  return data.label.indexOf(value) !== -1;
};

/** 查询系统资源树 */
function initSystemTree() {

};

/** 查询用户列表 */
function getList() {
  loading.value = true;
  listMenu(queryParams.value).then(res => {
    loading.value = false;
    if (res.success) {
      list.value = res.data;
      deptOptions.value = res.data
    }
  });
};

/** 节点单击事件 */
function handleNodeClick(data) {
  queryParams.value.parentId = data.id;
  handleQuery();
};

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
};

/** 删除按钮操作 */
function handleDelete(row) {
  const paramIds = row.id || ids.value;
  proxy.$modal.confirm(t('text.really.delete')).then(function () {
    return delMenu(paramIds);
  }).then(() => {
    getList();
    initSystemTree();
    proxy.$modal.msgSuccess(t('text.success.delete'));
  }).catch(() => {});
};

/** 选择条数  */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  console.log(222,ids.value)
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};


/** 重置操作表单 */
function reset() {
  form.value = {
    id: undefined,
    name: undefined,
    permission: undefined,
    parentId: undefined,
    classify: "directory",
    routePath: undefined,
    routeComponent: undefined,
    routeQuery: undefined,
    isFrame: 0,
    isCache: 0,
    isVisible: 1,
    sortOrder: 10,
    resAction: "read",
    requestMethod: "GET",
    icon: undefined,
    appId: 1,
    status: 0,
    description: undefined
  };
  proxy.resetForm("menuRef");
};

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
};

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = t('text.add');
  if (queryParams.value.parentId) {
    form.value.parentId = queryParams.value.parentId;
  }
};

/** 新增子节点按钮操作 */
function handleChAdd(parentId) {
  reset();
  open.value = true;
  title.value = t('text.add');
  form.value.parentId = parentId
};


/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const menuId = row.id || ids.value;
  getMenu(menuId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = t('text.edit');
  });
};

/** 提交按钮 */
function submitForm() {
  proxy.$refs["menuRef"].validate(valid => {
    if (valid) {
      if (form.value.parentId==undefined) {
        form.value.parentId = 1;
      }
      if (form.value.id != undefined) {
        updateMenu(form.value).then(response => {
          proxy.$modal.msgSuccess(t('text.success.edit'));
          open.value = false;
          initSystemTree()
          getList();
        });
      } else {
        addMenu(form.value).then(response => {
          proxy.$modal.msgSuccess(t('text.success.add'));
          open.value = false;
          initSystemTree()
          getList();
        });
      }
    }
  });
};


/** 选择图标 */
function selected(name) {
  form.value.icon = name;
}


initSystemTree();
getList();

</script>
