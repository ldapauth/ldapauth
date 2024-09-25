<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--部门数据-->
      <el-col :xs="8" :sm="6" :md="6" :lg="4" :xl="4">
        <el-row style="display: flex;justify-content: center">
          <span style="line-height: 30px;" class="clickable" @click="handleClick">{{t('org.organization')}}</span>
        </el-row>
        <el-row>
          <el-tree
              style="width: 100%"
              node-key="id"
              :data="deptOptions"
              :props="defaultProps"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              ref="tree"
              :default-expanded-keys="treeData"
              @node-click="handleNodeClick"
              v-slot="{ node, data }"
          >
            <span class="custom-tree-node">
              <span v-if="node.label.length<=10">{{ node.label }}</span>
              <span v-else>
                 <el-tooltip class="item" effect="dark" :content="node.label" placement="right">
                   <span>{{ node.label.slice(0, 10) + '...' }}</span>
                </el-tooltip>
              </span>
            </span>
          </el-tree>
        </el-row>
      </el-col>
      <!--用户数据-->
      <el-col :xs="16" :sm="18" :md="18" :lg="20" :xl="20">
        <div v-if="currentDepartmentName"
             style="height: 10px;font-size: 18px;">
          {{ `【${currentDepartmentName}】`}}
        </div>

        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" style="margin-top: 25px">
          <el-form-item :label="t('user.username')">
            <el-input
                v-model="queryParams.username"
                :placeholder="t('user.placeholder.username')"
                clearable
                style="width: 240px"
                @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item :label="t('user.displayName')">
            <el-input
                v-model="queryParams.displayName"
                :placeholder="t('user.placeholder.displayName')"
                clearable
                style="width: 240px"
                @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item :label="t('user.mobile')">
            <el-input
                v-model="queryParams.mobile"
                :placeholder="t('user.placeholder.mobile')"
                clearable
                style="width: 240px"
                @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item>
            <el-button @click="handleQuery">{{t('org.button.query')}}</el-button>
            <el-button @click="resetQuery">{{t('org.button.reset')}}</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
                type="primary"
                @click="handleAdd"
                v-hasPermi="['user:add']"
            >{{t('org.button.add')}}
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
                type="danger"
                :disabled="multiple"
                @click="handleDeleteBatch"
                v-hasPermi="['user:delete']"
            >{{t('org.button.delete')}}
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
                :disabled="multiple"
                @click="handleDisableBatch"
                v-hasPermi="['user:disable']"
            >{{t('org.button.disable')}}
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
                :disabled="multiple"
                @click="handleActiveBatch"
                v-hasPermi="['user:active']"
            >{{t('org.button.active')}}
            </el-button>
          </el-col>
          <!--               <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>-->
        </el-row>

        <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center"/>
          <el-table-column :label="t('user.username')" align="center" prop="username" min-width="110" :show-overflow-tooltip="true"/>
          <el-table-column :label="t('user.displayName')" align="center" prop="displayName" min-width="90" :show-overflow-tooltip="true"/>
          <el-table-column :label="t('user.mobile')" align="center" prop="mobile" min-width="80" :show-overflow-tooltip="true"/>
          <el-table-column :label="t('user.email')" align="center" prop="email"
                           :show-overflow-tooltip="true" min-width="105"/>
          <el-table-column :label="t('user.belong')" align="center" prop="departmentNamePath"
                           :show-overflow-tooltip="true" min-width="110"/>
          <el-table-column label="DN" prop="ldapDn" :show-overflow-tooltip="true" />
          <el-table-column prop="object_from" :label="t('org.from')" align="center" min-width="60">
            <template #default="scope">
              <dict-tag :options="sys_data_object_from" :value="scope.row.objectFrom" />
            </template>
          </el-table-column>
          <el-table-column prop="status" :label="t('org.status')" align="center" width="70">
            <template #default="scope">
              <el-switch
                  :width="40"
                  v-model="scope.row.status"
                  :active-value="0"
                  :inactive-value="1"
                  @change="handleStatusChange(scope.row)"
              >
              </el-switch>
            </template>
          </el-table-column>

          <el-table-column :label="t('org.operate')" align="center" class-name="small-padding fixed-width" width="215">
            <template #default="scope">
              <el-button @click="handleUpdate(scope.row)" style ="margin-right: 10px" v-hasPermi="['user:edit']">{{t('org.edit')}}</el-button>
              <el-dropdown @command="(command) => handleCommand(command, scope.row)" v-hasPermi="['user:resetPwd', 'user:delete']">
                <el-button>
                  {{t('text.moreAction')}}<el-icon class="el-icon--right"><arrow-down /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                      <div v-hasPermi="['user:resetPwd']">
                        <el-dropdown-item command="1">{{t('user.button.resetPwd')}}</el-dropdown-item>
                      </div>
                    <div v-hasPermi="['user:delete']">
                      <el-dropdown-item divided command="2">
                        <span style="color: red">{{t('text.delete')}}</span>
                      </el-dropdown-item>
                    </div>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-table-column>
        </el-table>
        <pagination
            v-show="total > 0"
            :total="total"
            v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize"
            @pagination="getList"
        />
      </el-col>
    </el-row>

    <!-- 新增或修改对话框 -->
    <UserEdit :title="title" :open="open" :formId="id" @dialogOfClosedMethods="dialogOfClosedMethods"
              :deptOptions="deptOptions" :genderOptions="sys_user_sex" :pwdPolicy="pwdPolicy"
        :current-tree-id="currentDepartmentId"></UserEdit>

<!--    &lt;!&ndash; 用户导入对话框 &ndash;&gt;
    <el-dialog :title="upload.title" v-model="upload.open" width="400px" append-to-body>
      <el-upload
          ref="uploadRef"
          :limit="1"
          accept=".xlsx, .xls"
          :headers="upload.headers"
          :action="upload.url + '?updateSupport=' + upload.updateSupport"
          :disabled="upload.isUploading"
          :on-progress="handleFileUploadProgress"
          :on-success="handleFileSuccess"
          :auto-upload="false"
          drag
      >
        <el-icon class="el-icon&#45;&#45;upload">
          <upload-filled/>
        </el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <div class="el-upload__tip">
              <el-checkbox v-model="upload.updateSupport"/>
              是否更新已经存在的用户数据
            </div>
            <span>仅允许导入xls、xlsx格式文件。</span>
            <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;"
                     @click="importTemplate">下载模板
            </el-link>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">确 定</el-button>
          <el-button @click="upload.open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>-->
  </div>
</template>

<script setup name="User">
import UserEdit from "./edit.vue";
import {getToken} from "@/utils/auth.js";
import {
  activeUser,
  delUser,
  disableUser,
  listUser, resetSubUserPassword,
} from "@/api/system/user.js";
import {getTree} from "@/api/system/dept.js";
import {useI18n} from "vue-i18n";
import {getPwdPolicy} from "@/api/login.js";

const {proxy} = getCurrentInstance();
const { t } = useI18n()
const  {sys_data_object_from, sys_user_sex} = proxy.useDict("sys_data_object_from", "sys_user_sex");

const userList = ref([]);
const open = ref(false);
const loading = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const deptName = ref("");
const deptOptions = ref([]);
//默认展开节点
const treeData = ref([]);
/** 主键 */
const id = ref(undefined)
/*** 用户导入参数 */
const upload = reactive({
  // 是否显示弹出层（用户导入）
  open: false,
  // 弹出层标题（用户导入）
  title: "",
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的用户数据
  updateSupport: 0,
  // 设置上传的请求头部
  headers: {Authorization: "Bearer " + getToken()},
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/system/user/importData"
});

const defaultProps = ref({
  children: 'children',
  label: 'name'
});

//密码策略
const pwdPolicy = ref({})

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    username: undefined,
    displayName: undefined,
    mobile: undefined
  }
});
//当前选中节点
const currentDepartmentId = ref(null);
const currentDepartmentName = ref('');

const {queryParams, rules} = toRefs(data);

/** 通过条件过滤节点  */
const filterNode = (value, data) => {
  if (!value) return true;
  return data.label.indexOf(value) !== -1;
};

/** 根据名称筛选部门树 */
watch(deptName, val => {
  proxy.$refs["deptTreeRef"].filter(val);
});

/** 查询部门下拉树结构 */
function getDeptTree() {
  getTree().then(res => {
    // 定义一个递归函数来遍历树形数据并获取所有层级的节点
    const traverse = (node, level) => {
      if (level <= 2) { // 只展开到第三级
        treeData.value.push(node.id);
      }
      if (node.children) {
        node.children.forEach(child => {
          traverse(child, level + 1);
        });
      }
    };
    res.data.forEach(rootNode => {
      traverse(rootNode, 1);
    });
    deptOptions.value = res.data;
  })
}

/** 查询用户列表 */
function getList() {
  loading.value = true;
  listUser(queryParams.value).then(res => {
    loading.value = false;
    userList.value = res.data.records;
    total.value = res.data.total;
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    username: undefined,
    displayName: undefined,
    mobile: undefined
  }
  handleQuery();
}

/** 删除按钮操作 */
function handleDelete(row) {
  proxy.$modal.confirm(t('org.deleteTip1') + row.username + t('org.deleteTip2')).then(function() {
    return delUser({ids: [row.id]});
  }).then(res => {
    if (res.code === 200) {
      getList();
      proxy.$modal.msgSuccess(t('org.success.delete'));
    } else {
      proxy.$modal.msgError(res.message);
    }
  }).catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download("system/user/export", {
    ...queryParams.value,
  }, `user_${new Date().getTime()}.xlsx`);
}

/** 用户状态修改  */
function handleStatusChange(row) {
  let text = row.status === 0 ? t('org.button.active') : t('org.button.disable');
  let operation = row.status === 0 ? activeUser : disableUser;

  proxy.$modal.confirm(t('org.confirmTip1') + text + t('org.confirmTip3') + '"' + row.username + t('user.confirmTip'))
      .then(function () {
        return operation({ids: [row.id]});
      }).then(res => {
    if (res.code === 200) {
      proxy.$modal.msgSuccess(res.data);
    } else {
      row.status = row.status === 0 ? 1 : 0;
      proxy.$modal.msgError(res.message);
    }
  }).catch(function () {
    row.status = row.status === 0 ? 1 : 0;
  });
}

/** 重置密码按钮操作 */
function handleResetPwd(row) {
  let id = row.id
  proxy.$modal
      .confirm(t('user.resetTip') + row.username + t('user.resetTip1'))
      .then(function() {
        return resetSubUserPassword(id)
      })
      .then(res => {
        if (res.code === 200) {
          proxy.$alert(`${t('user.success.resetTip')}<span style="color: red; font-weight: bold">${res.data}</span>
${t('user.success.resetTip1')}`,
              t('user.userPwd'), {
                confirmButtonText: t('user.button.sure'),
                dangerouslyUseHTMLString: true
              })
        } else {
          proxy.$modal.msgError(res.message)
        }

      }).catch(error => {
  })
}

/** 选择条数  */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 导入按钮操作 */
function handleImport() {
  upload.title = "用户导入";
  upload.open = true;
}

/** 下载模板操作 */
function importTemplate() {
  proxy.download("system/user/importTemplate", {}, `user_template_${new Date().getTime()}.xlsx`);
}

/**文件上传中处理 */
const handleFileUploadProgress = (event, file, fileList) => {
  upload.isUploading = true;
};

/** 文件上传成功处理 */
const handleFileSuccess = (response, file, fileList) => {
  upload.open = false;
  upload.isUploading = false;
  proxy.$refs["uploadRef"].handleRemove(file);
  proxy.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", {dangerouslyUseHTMLString: true});
  getList();
};

/** 提交上传文件 */
function submitFileForm() {
  proxy.$refs["uploadRef"].submit();
}

/** 新增按钮操作 */
function handleAdd() {
  open.value = true;
  title.value = t('user.addUser')
}

/** 修改按钮操作 */
function handleUpdate(row) {
  id.value = row.id;
  open.value = true;
  title.value = t('user.editUser')
}

/** 用户修改或新增框关闭事件*/
function dialogOfClosedMethods(val) {
  open.value = false;
  id.value = undefined;
  getList();
}

/** 节点单击事件 */
function updateDepartment(id, name) {
  currentDepartmentId.value = id;
  currentDepartmentName.value = name;
  queryParams.value.departmentId = id;
  handleQuery();
}

function handleNodeClick(data) {
  updateDepartment(data.id, data.name);
}

function handleClick() {
  updateDepartment(undefined, undefined);
}

/** 批量禁用 */
function handleDisableBatch() {
  let data = {
    ids: ids.value
  }
  proxy.$modal
      .confirm(t('org.confirmDis'))
      .then(function () {
        return disableUser(data);
      })
      .then(res => {
        if (res.code === 200) {
          proxy.$modal.msgSuccess(t('org.success.disable'))
          getList();
        } else {
          proxy.$modal.msgError(res.message);
        }
      }).catch(error => {
  })
}

/** 批量启用 */
function handleActiveBatch() {
  let data = {
    ids: ids.value
  }
  proxy.$modal
      .confirm(t('org.confirmActive'))
      .then(function () {
        return activeUser(data);
      })
      .then(res => {
        if (res.code === 200) {
          proxy.$modal.msgSuccess(t('org.success.active'))
          getList();
        } else {
          proxy.$modal.msgError(res.message);
        }
      }).catch(error => {
  })
}

/** 批量删除 */
function handleDeleteBatch() {
  let data = {
    ids: ids.value
  }
  proxy.$modal
      .confirm(t('org.confirmDelete'))
      .then(function () {
        return delUser(data);
      })
      .then(res => {
        if (res.code === 200) {
          proxy.$modal.msgSuccess(t('org.success.delete'))
          getList();
        } else {
          proxy.$modal.msgError(res.message);
        }
      }).catch(error => {
  })
}

function handleCommand(command, row) {
  switch(command){
    case "1":
      handleResetPwd(row)
      break;
    case "2":
      handleDelete(row)
      break;
    default:
      break
  }
}

//获取密码策略
function passwordPolicy() {
  getPwdPolicy().then(res => {
    if (res.code === 200) {
      pwdPolicy.value = res.data;
    } else {
      proxy.$modal.msgError("无法读取密码策略配置，请联系管理员")
    }
  })
}

getDeptTree();
getList();
passwordPolicy();
</script>

<style lang="scss" scoped>
.clickable {
  line-height: 30px;
  cursor: pointer; /* 设置鼠标悬停时为指针样式 */
}
</style>

