<template>
  <el-drawer v-model="dialogStatus" :close-on-click-modal="false" size="40%"
             @close="dialogOfClosedMethods(false)">
    <template #header>
        <h4>{{ title }}</h4>
    </template>
    <template #default>
      <el-form :model="form" :rules="rules" ref="orgRef"  label-width="100px" inline-message>
        <el-form-item prop="orgName" :label="t('org.name')">
          <el-input v-model="form.orgName" :placeholder="t('org.placeholder.name')"
                    maxlength="32"
                    show-word-limit/>
        </el-form-item>
        <el-form-item prop="openDepartmentId" :label="t('org.openId')">
          <el-input v-model="form.openDepartmentId" :placeholder="t('org.placeholder.openId')"/>
        </el-form-item>
        <el-form-item prop="classify" :label="t('org.type')" style="margin-bottom: 10px">
          <el-select v-model="form.classify" style="width: 100%">
            <el-option v-for="item in orgClassifies"
                       :key="item.value"
                       :label="item.label"
                       :value="item.value">
            </el-option>
          </el-select>
          <span style="font-size: 12px;color: gray;">
          {{t('org.typePh')}}
      </span>
        </el-form-item>
        <el-form-item prop="parentId" :label="t('org.parent')">
          <el-tree-select
              v-model="form.parentId"
              :data="deptOptions"
              :props="defaultProps"
              check-strictly
              value-key="id"
              :placeholder="t('org.placeholder.parent')"
          />
        </el-form-item>
        <el-form-item prop="sortIndex" :label="t('org.sort')">
          <el-input-number v-model="form.sortIndex" :min="0" :max="999" style="width: 30%"/>
        </el-form-item>
        <el-form-item label="DN">
          <el-input v-model="form.ldapDn" type="textarea" disabled="true" maxlength="200"  show-word-limit></el-input>
        </el-form-item>
      </el-form>
    </template>
    <template #footer>
      <div style="flex: auto">
        <el-button @click="dialogStatus = false">{{t('org.cancel')}}</el-button>
        <el-button type="primary" @click="submitForm">{{t('org.confirm')}}</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup>
import {defineProps, ref, watch} from "vue";
import {addDept, getDept, updateDept} from "@/api/system/dept.js";
import {useI18n} from "vue-i18n";

const { t } = useI18n()

defineOptions({
  name: 'OrgEdit'
})

const {proxy} = getCurrentInstance();

const props = defineProps({
  title: {
    type: String,
    default: ""
  },
  open: Boolean,
  formId: {
    default: undefined
  },
  orgClassifies: {
    type: Array,
    default: []
  },
  deptOptions: {
    type: Array,
    default: [],
  },
  currentTreeId: {
    type: String,
    default: null,
  },
});


const emit = defineEmits(['dialogOfClosedMethods']);

const data = reactive({
  form: {
    sortIndex: 0,
    parentId: props.currentTreeId
  },
  rules: {
    orgName: [
      { required: true, message: t('org.rule.name'), trigger: "blur" },
      {
        validator: validateNoSpaces,
        trigger: 'blur'
      }
    ]
  }
})

const {form, rules} = toRefs(data)
const dialogStatus = ref(false);
const treeSelectKey = ref(1);
const defaultProps = ref({
  value: 'id',
  children: 'children',
  label: 'name',
})
// 监听 open 变化
watch(
    () => props.open,
    (val) => {
      if (val) {
        dialogStatus.value = props.open;
        if (props.formId) {
          //修改,查询当前组织
          getDept(props.formId).then(res => {
            if (res.code === 200) {
              form.value = res.data;
            }
          })
        } else {
          reset();
        }
      } else {
        reset();
      }
    },
    {immediate: true}
);

function dialogOfClosedMethods(val) {
  emit('dialogOfClosedMethods', val);
}


//设置禁用节点
function disableNodeSelect(data, node) {
  // 禁用匹配的节点
  if (proxy.formId === data.id) {
    return true;
  }
  console.log(data, 11111)

 /* // 禁用匹配节点的子节点
  if (data.children) {
    console.log(data.children, 'ddd')
  }*/
}


/** 重置操作表单 */
function reset() {
  form.value = {
    sortIndex: 0,
    parentId: props.currentTreeId
  };
  proxy.resetForm("orgRef");
}

/** 提交表单 */
function submitForm() {
  const handleResponse = (res, successMessage) => {
    if (res.code === 200) {
      dialogStatus.value = false;
      proxy.$modal.msgSuccess(successMessage);
      dialogOfClosedMethods(false);
      reset();
    }
  };

  proxy.$refs["orgRef"].validate(valid => {
    if (valid) {
      const operation = props.formId ? updateDept : addDept;
      const successMessage = props.formId ? t('org.success.update') : t('org.success.add');

      operation(form.value).then(res => handleResponse(res, successMessage));
    }
  });
}

function normalizer(node) {
  //去掉children=[]的children属性
  if (node.children && !node.children.length) {
    delete node.children;
  }
  return {
    id: node.id,
    //将name转换成必填的label键
    label: node.name,
    children: node.children,
    isDisabled: node.id === props.formId,
  };
}

function DRHA_EFaultModeTree_handleSelect(node, instanceId) {
  form.value.parentName = node.name;
}

function validateNoSpaces(rule, value, callback) {
  if (value.trim() === "") {
    callback(new Error(t('org.rule.space')));
  } else {
    callback();
  }
}
</script>

<style lang="scss" scoped>

</style>
