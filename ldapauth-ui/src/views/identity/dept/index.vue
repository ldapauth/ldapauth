<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :xs="8" :sm="6" :md="6" :lg="4" :xl="4">
        <el-row style="display: flex;justify-content: center">
            <span style="line-height: 30px;" @click="handleClick"  class="clickable">{{t('org.organization')}}</span>
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
      <el-col :xs="16" :sm="18" :md="18" :lg="20" :xl="20">
        <div v-if="currentDepartmentName"
             style="height: 10px;font-size: 18px;">
          {{ `【${currentDepartmentName}】`}}
        </div>

        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" style="margin-top: 25px">
          <el-form-item :label="t('org.name')">
            <el-input
                v-model="queryParams.orgName"
                :placeholder="t('org.placeholder.name')"
                clearable
                style="width: 200px"
                @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item>
            <el-button  @click="handleQuery">{{t('org.button.query')}}</el-button>
            <el-button  @click="resetQuery">{{t('org.button.reset')}}</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
                type="primary"
                @click="handleAdd"
                v-hasPermi="['dept:add']"
            >{{t('org.button.add')}}</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
                @click="handleDisable"
                :disabled="ids.length === 0"
                v-hasPermi="['dept:disable']"
            >{{t('org.button.disable')}}</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
                @click="handleActive"
                :disabled="ids.length === 0"
                v-hasPermi="['dept:active']"
            >{{t('org.button.active')}}</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
                type="danger"
                @click="handleDeleteBatch"
                :disabled="ids.length === 0"
                v-hasPermi="['dept:delete']"
            >{{t('org.button.delete')}}</el-button>
          </el-col>
        </el-row>
        <el-table
            v-loading="loading"
            :data="deptList"
            @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" align="center"/>
          <el-table-column prop="orgName" :label="t('org.name')" align="center" min-width="100" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="namePath" :label="t('org.path')" align="left" :show-overflow-tooltip="true" min-width="100" />
          <el-table-column label="DN" prop="ldapDn" :show-overflow-tooltip="true" />
          <el-table-column prop="object_from" :label="t('org.from')" align="center" min-width="60">
            <template #default="scope">
              <dict-tag :options="sys_data_object_from" :value="scope.row.objectFrom" />
            </template>
          </el-table-column>
          <el-table-column prop="status" :label="t('org.status')" align="center" width="100">
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
          <el-table-column prop="actions" :label="t('org.operate')" align="center" width="200">
            <template #default="scope">
              <el-button @click="handleUpdate(scope.row)" v-hasPermi="['dept:edit']">{{t('org.edit')}}</el-button>
              <el-button type="danger" @click="handleDelete(scope.row)" v-hasPermi="['dept:delete']">{{t('org.button.delete')}}</el-button>
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
        <!--新增或修改对话框-->
        <OrgEdit :title="title" :open="open" :formId="id" @dialogOfClosedMethods="dialogOfClosedMethods"
                 :orgClassifies="org_classify" :deptOptions="deptOptions" :currentTreeId="currentDepartmentId"></OrgEdit>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Dept">
import {
  listDept,
  getTree,
  disableBatch,
  activeBatch,
  deleteBatch
} from "@/api/system/dept.js";

import OrgEdit from "./edit.vue";
import {useI18n} from "vue-i18n";

const { t } = useI18n()

const { proxy } = getCurrentInstance();
const  {org_classify, sys_data_object_from} = proxy.useDict("org_classify", "sys_data_object_from");

const deptList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const title = ref("");
const id = ref(undefined);
const total = ref(0);
const deptOptions = ref([]);
const defaultProps = ref({
  children: 'children',
  label: 'name'
});
//默认展开节点
const treeData = ref([]);
//当前选中节点
const currentDepartmentId = ref(null);
const currentDepartmentName = ref('');
const selectionlist = ref([]);
const ids = ref([]);

/** 通过条件过滤节点  */
const filterNode = (value, data) => {
  if (!value) return true;
  return data.label.indexOf(value) !== -1;
};

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
  }
});

const { queryParams } = toRefs(data);

/** 查询部门列表 */
function getList() {
  loading.value = true;
  listDept(queryParams.value).then(res => {
    if (res.code === 200) {
      loading.value = false;
      deptList.value = res.data.records;
      total.value = res.data.total
    }
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
  }
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 新增按钮操作 */
function handleAdd() {
  id.value = undefined;
  title.value = t('org.titleAdd')
  open.value = true;
}

/** 节点单击事件 */
function handleNodeClick(data) {
  currentDepartmentId.value = data.id
  currentDepartmentName.value = data.name
  queryParams.value.id = data.id;
  handleQuery();
}

/** 修改按钮操作 */
function handleUpdate(row) {
  id.value = row.id;
  getTree({id: id.value}).then(res => {
    if (res.code === 200) {
      deptOptions.value = res.data;
      title.value = t('org.titleEdit');
      open.value = true;
    } else {
      proxy.$modal.msgError(t('org.treeError'))
    }
  })

}

/** 用户修改或新增框关闭事件*/
function dialogOfClosedMethods(val) {
  open.value = false;
  id.value = undefined;
  getList();
  getOrgTree();
}

/** 删除按钮操作 */
function handleDelete(row) {
  proxy.$modal.confirm(t('org.deleteTip1') + row.orgName + t('org.deleteTip2')).then(function() {
    return deleteBatch({ids: [row.id]});
  }).then(res => {
    if (res.code === 200) {
      getList();
      proxy.$modal.msgSuccess(t('org.success.delete'));
    } else {
      proxy.$modal.msgError(res.message);
    }
  }).catch(() => {});
}

/** 获取组织树 */
function getOrgTree() {
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

/** 多选操作*/
function handleSelectionChange(selection) {
  selectionlist.value = selection;
  ids.value = selectionlist.value.map(item => item.id);
}

/** 批量禁用 */
function handleDisable() {
  let data = {
    ids: ids.value
  }
  proxy.$modal
      .confirm(t('org.confirmDis'))
      .then(function() {
        return disableBatch(data)
      })
      .then(res => {
        if (res.code === 200) {
          //分页查询
          getList();
          //重新获取组织树
          getOrgTree();
          proxy.$modal.msgSuccess(t('org.success.disable'))
        } else {
          proxy.$modal.msgError(res.message)
        }
      }).catch(error => {
  })
}

/** 批量启用 */
function handleActive() {
  let data = {
    ids: ids.value
  }
  proxy.$modal
      .confirm(t('org.confirmActive'))
      .then(function () {
        return activeBatch(data);
      })
      .then(res => {
        if (res.code === 200) {
          proxy.$modal.msgSuccess(t('org.success.active'))
          getList();
          getOrgTree();
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
        return deleteBatch(data);
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

/** 单启/单禁 */
function handleStatusChange(row) {
  let text = row.status === 0 ? t('org.button.active') : t('org.button.disable');
  let operation = row.status === 0 ? activeBatch : disableBatch;

  proxy.$modal.confirm(t('org.confirmTip1') + text + t('org.confirmTip3') + '"' + row.orgName + t('org.confirmTip2'))
      .then(function () {
        return operation({ids: [row.id]});
      }).then(res => {
        if (res.code === 200) {
          getOrgTree();
          proxy.$modal.msgSuccess(t('org.success.disable'));
        } else {
          row.status = row.status === 0 ? 1 : 0;
          proxy.$modal.msgError(res.message);
        }
  }).catch(function () {
    row.status = row.status === 0 ? 1 : 0;
  });
}

function handleClick() {
  currentDepartmentId.value = undefined
  currentDepartmentName.value = undefined
  queryParams.value.id = undefined;
  handleQuery();
}

getList();
getOrgTree();
</script>

<style lang="scss" scoped>
.clickable {
  line-height: 30px;
  cursor: pointer; /* 设置鼠标悬停时为指针样式 */
}
</style>
