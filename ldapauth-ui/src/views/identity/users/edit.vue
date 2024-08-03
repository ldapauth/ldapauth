<template>
  <!-- 添加或修改用户配置对话框 -->
  <el-dialog :title="title" v-model="dialogStatus" width="50%"
             :close-on-click-modal="false"
             @close="dialogOfClosedMethods(false)">
    <el-form :model="form" :rules="rules" ref="userRef" label-width="120px">
        <el-row :gutter="gutter">
          <el-col :span="12">
             <el-form-item :label="t('user.username')" prop="username">
               <el-input v-model="form.username" :disabled="formId !== undefined && formId !== null"></el-input>
             </el-form-item>
              <el-form-item :label="t('user.displayName')" prop="displayName">
                <el-input v-model="form.displayName"></el-input>
              </el-form-item>
            <el-form-item :label="t('user.password')" v-if="ifPass" prop="password" style="margin-bottom: 30px">
              <el-input v-model="form.password"></el-input>
            </el-form-item>
            <el-form-item :label="t('user.gender')">
              <el-radio-group v-model="form.gender">
                <el-radio-button v-for="item in genderOptions"
                                 :label="item.label"
                                 :value="item.value">

                </el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="t('user.avatar')">
              <el-upload
                  action="fakeAction"
                  accept="image/jpg,image/jpeg"
                  class="avatar-uploader"
                  name="uploadFile"
                  :http-request="doUploadAvatar"
                  :before-upload="beforeAvatarUpload"
                  :show-file-list="false">
                <img v-if="previewImage" :src="previewImage" class="avatar" alt="">
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
      <el-row :gutter="gutter">
        <el-col :span="12">
          <el-form-item :label="t('user.nickname')" prop="nickName">
            <el-input v-model="form.nickName"></el-input>
          </el-form-item>
          <el-form-item :label="t('user.mobile')" prop="mobile">
            <el-input v-model="form.mobile"></el-input>
          </el-form-item>
          <el-form-item :label="t('user.belong')" prop="departmentId">
            <el-tree-select
                v-model="form.departmentId"
                :data="deptOptions"
                :props="defaultProps"
                check-strictly
                value-key="id"
                :placeholder="t('org.placeholder.parent')"
                show-checkbox
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('user.email')" prop="email">
            <el-input v-model="form.email"></el-input>
          </el-form-item>
          <el-form-item :label="t('user.email')">
            <el-date-picker
                style="width: 100%"
                v-model="form.birthDate"
                type="date"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :placeholder="t('user.birthDate')">
            </el-date-picker>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="submitForm">{{t('user.button.sure')}}</el-button>
        <el-button @click="dialogStatus = false">{{t('user.button.cancel')}}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import {ref, watch} from 'vue';
import {defineProps} from 'vue';
import {addUser, getUser, updateUser, uploadImage} from "@/api/system/user.js";
import {Plus} from "@element-plus/icons-vue";
import defAva from '@/assets/images/profile.jpg'
import {useI18n} from "vue-i18n";
import {validatePass} from "@/api/commonApi.js";

const {t} = useI18n();

defineOptions({
  name: 'UserEdit'
})

const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  open: Boolean,
  formId: {
    default: undefined
  },
  deptOptions: {
    type: Array,
    default: []
  },
  genderOptions: {
    type: Array,
    default: []
  },
  pwdPolicy: {
    type: Object,
    default: {}
  },
  currentTreeId: {
    type: String,
    default: null,
  },
});

// 定义 emits
const emit = defineEmits(['dialogOfClosedMethods']);
//定义数据
const {proxy} = getCurrentInstance();
const data = reactive({
  form: {
    gender: 0,
    departmentId: props.currentTreeId
  },
  rules: {
    username: [{required: true, message: t('user.rule.name'), trigger: "blur"}, {
      min: 2,
      max: 64,
      message: t('user.rule.nameLength'),
      trigger: "blur"
    }],
    displayName: [{required: true, message: t('user.rule.displayName'), trigger: "blur"}, {
      min: 2,
      max: 20,
      message: t('user.rule.displayNameLength'),
      trigger: "blur"
    }],
    departmentId: [
      {
        required: true,
        message: t('user.rule.department'),
        trigger: 'change'
      }
    ],
    nickName: [{
      min: 0,
      max: 20,
      message: t('user.rule.nickNameLength'),
      trigger: "blur"
    }],
    password: [
      { required: true, message: t('user.rule.pwd'), trigger: 'blur' },
      { validator: (rule, value, callback) => validatePass(rule, value, callback, props.pwdPolicy), trigger: ['blur', 'change'] }
    ],
    email: [{type: "email", message: t('user.rule.email'), trigger: ["blur", "change"]}],
    mobile: [{pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: t('user.rule.mobile'), trigger: "blur"}]
  }
});
const {form, rules} = toRefs(data)
const dialogStatus = ref(false);
const gutter = 20;
const ifPass = ref(true);
const previewImage = ref("");
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
          ifPass.value = false;
          getUser(props.formId).then(res => {
            previewImage.value = (res.data.avatar === null || res.data.avatar === '') ?
            defAva : import.meta.env.VITE_APP_BASE_API + res.data.avatar;
            form.value = res.data
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

function submitForm() {
  const handleResponse = (res, successMessage) => {
    if (res.code === 200) {
      dialogStatus.value = false;
      proxy.$modal.msgSuccess(successMessage);
      dialogOfClosedMethods(false);
      reset();
    } else {
      proxy.$modal.msgError(res.message);
    }
  };

  proxy.$refs["userRef"].validate(valid => {
    if (valid) {
      const operation = props.formId ? updateUser : addUser;
      const successMessage = props.formId ? t('org.success.update') : t('org.success.add');

      operation(form.value).then(res => handleResponse(res, successMessage));
    }
  });
}

/** 重置操作表单 */
function reset() {
  form.value = {
    gender: 0,
    departmentId: props.currentTreeId
  };
  ifPass.value = true;
  previewImage.value = "";
  proxy.resetForm("userRef");
}


function doUploadAvatar(item) {
  const formData = new FormData();
  formData.append("uploadFile", item.file);
  uploadImage(formData).then(res => {
    if (res.code === 200) {
      previewImage.value = URL.createObjectURL(item.file);
      form.value.avatar = res.data;
    }
  });
}

/** 文件状态改变时的钩子 */
function handleFileChange(file, fileList) {
  if (fileList.length > 0) {
    fileList = [fileList[fileList.length - 1]]; // 获取最后一次选择的文件
  }
}

/** 上传头像前检查 */
function beforeAvatarUpload(rawFile) {
  console.log(rawFile)
  if (rawFile.type !== 'image/jpeg') {
    proxy.$modal.msgError('Avatar picture must be JPG format!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 4) {
    proxy.$modal.msgError('Avatar picture size can not exceed 4MB!')
    return false
  }
  return true
}

</script>

<style lang="scss" scoped>
.avatar-uploader {
  width: 128px;
  height: 128px;
  border: 3px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 128px;
  height: 128px;
  line-height: 128px;
  text-align: center;
}

.avatar {
  width: 128px;
  height: 128px;
  display: block;
}
</style>
