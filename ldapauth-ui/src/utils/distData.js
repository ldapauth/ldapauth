import i18n from '@/languages'
const { t } = i18n.global;


export default {
  "sys_normal_disable": [
    {
      "label": t('distdata.sys_normal_disable.normal'),
      "value": 0
    },
    {
      "label": t('distdata.sys_normal_disable.disable'),
      "value": 1
    }
  ],
  "sys_show_hide": [
    {
      "label": t('distdata.sys_normal_disable.show'),
      "value": 0
    },
    {
      "label":t('distdata.sys_normal_disable.hide'),
      "value": 1
    }
  ],

  "sys_frame": [
    {
      "label":t('distdata.sys_frame.yes'),
      "value": 1
    },
    {
      "label":t('distdata.sys_frame.no'),
      "value": 0
    }
  ],
  "sys_cache": [
    {
      "label":t('distdata.sys_cache.no'),
      "value": 0
    },
    {
      "label":t('distdata.sys_cache.yes'),
      "value": 1
    }
  ],

  "sys_visible_hide": [
    {
      "label":t('distdata.sys_visible_hide.show'),
      "value": 1
    },
    {
      "label":t('distdata.sys_visible_hide.hide'),
      "value": 0
    }
  ],
  "sys_user_sex": [
    {
      "label":t('distdata.sys_user_sex.unknown'),
      "value": 0
    },
    {
      "label":t('distdata.sys_user_sex.male'),
      "value": 1
    },
    {
      "label":t('distdata.sys_user_sex.female'),
      "value": 2
    }
  ],
  "sys_yes_no": [
    {
      "label":t('distdata.sys_yes_no.yes'),
      "value": 1
    },
    {
      "label":t('distdata.sys_yes_no.no'),
      "value": 0
    }
  ],
  "sys_classify": [
    {
      "label":t('distdata.sys_classify.directory'),
      "value": "directory"
    },
    {
      "label":t('distdata.sys_classify.menu'),
      "value": "menu"
    },
    {
      "label":t('distdata.sys_classify.button'),
      "value": "button"
    },
    {
      "label":t('distdata.sys_classify.page'),
      "value": "page"
    },
    {
      "label":t('distdata.sys_classify.api'),
      "value": "api"
    },
    {
      "label":t('distdata.sys_classify.other'),
      "value": "other"
    }
  ],
  "sys_object_from" :[
    {
      "label":t('distdata.sys_object_from.ldap'),
      "value": 1
    },
    {
      "label":t('distdata.sys_object_from.local'),
      "value": 0
    }
  ],
  "org_classify": [
    {
      "label":t('distdata.org_classify.department'),
      "value": "department"
    },
    {
      "label":t('distdata.org_classify.organization'),
      "value": "organization"
    }
  ],

  "sys_cron_list": [
    {
      "label":t('distdata.sys_cron_list.corn2'),
      "value": "0 0 0/2 * * ?"
    },
    {
      "label":t('distdata.sys_cron_list.corn4'),
      "value": "0 0 0/4 * * ?"
    },
    {
      "label":t('distdata.sys_cron_list.corn6'),
      "value": "0 0 0/6 * * ?"
    },
    {
      "label":t('distdata.sys_cron_list.corn8'),
      "value": "0 0 0/8 * * ?"
    },
    {
      "label":t('distdata.sys_cron_list.corn12'),
      "value": "0 0 0/12 * * ?"
    }
  ],
  "sys_sync_type": [
    {
      "label":t('distdata.sys_sync_type.organization'),
      "value": "0"
    },
    {
      "label":t('distdata.sys_sync_type.user'),
      "value": "1"
    },
    {
      "label":t('distdata.sys_sync_type.group'),
      "value": "2"
    }
  ],
  "sys_enable_status": [
    {
      "label":t('distdata.sys_enable_status.enable'),
      "value": 0
    },
    {
      "label":t('distdata.sys_enable_status.close'),
      "value": 1
    }
  ],
  "sys_sync_result_status": [
    {
      "label":t('distdata.sys_sync_result_status.success'),
      "value": 0
    },
    {
      "label":t('distdata.sys_sync_result_status.fail'),
      "value": 1
    }
  ],
  "sys_sync_action_type": [
    {
      "label":t('distdata.sys_sync_action_type.add'),
      "value": "add"
    },
    {
      "label":t('distdata.sys_sync_action_type.update'),
      "value": "update"
    },
    {
      "label":t('distdata.sys_sync_action_type.delete'),
      "value": "delete"
    }
  ],
  "sys_data_object_from": [
    {
      "label":'Activedirectory',
      "value": "activedirectory"
    },
    {
      "label":t('distdata.sys_data_object_from.openLdap'),
      "value": "openldap"
    },
    {
      "label":t('distdata.sys_data_object_from.system'),
      "value": "system"
    },
    {
      "label":t('distdata.sys_data_object_from.dingding'),
      "value": "dingding"
    },
    {
      "label":t('distdata.sys_data_object_from.feishu'),
      "value": "feishu"
    },
    {
      "label":t('distdata.sys_data_object_from.workweixin'),
      "value": "workweixin"
    }
  ],
  "sys_socials_type": [
    {
      "label":t('distdata.sys_socials_type.wechatopen'),
      "value": "wechatopen"
    },
    {
      "label":t('distdata.sys_socials_type.workweixin'),
      "value": "workweixin"
    },
    {
      "label":t('distdata.sys_socials_type.sinaweibo'),
      "value": "sinaweibo"
    },
    {
      "label":t('distdata.sys_socials_type.dingtalk'),
      "value": "dingtalk"
    },
    {
      "label":t('distdata.sys_socials_type.feishu'),
      "value": "feishu"
    },
    {
      "label":t('distdata.sys_socials_type.alipay'),
      "value": "alipay"
    },
    {
      "label":t('distdata.sys_socials_type.douyin'),
      "value": "douyin"
    }
],

}
