export default {
  "appTitle": "Security Management",
  "text": {
    "systemprompt":"System Prompt",
    "cancel": "Cancel",
    "action": "Action",
    "query": "Query",
    "reset": "Reset",
    "expand": "Expand",
    "collapse": "Collapse",
    "manual": "Manual",
    "automatic": "Automatic",
    "terminate": "Terminate",
    "changepassword": "ChangePassword",
    "add": "Add",
    "edit": "Edit",
    "delete": "Delete",
    "test": "Test",
    "select": "Select",
    "confirm": "Confirm",
    "synchr": "Synchr",
    "close": "Close",
    "enable": "Enable",
    "disable": "Disable",
    "display": "Display",
    "hide": "Hide",
    "lock": "Lock",
    "unlock": "UnLock",
    "posts": "Posts",
    "hats": "Hats",
    "accounts": "Accounts",
    "roles": "Roles",
    "groups": "Groups",
    "owns": "Owns",
    "assignable": "Assignable",
    "moreaction": "More",
    "submit": "Submit",
    "generate": "Generate",
    "upload": "Upload",
    "save": "Save",
    "year": "Year",
    "month": "Month",
    "date": "Date",
    "day": "Day",
    "hour": "Hour",
    "minute": "Minute",
    "second": "second",
    "millisecond": "MilliSecond",
    "yes": "Yes",
    "no": "No",
    "id": "ID",
    "instId": "InstId",
    "instName": "Institution",
    "description": "description",
    "createdBy": "createdBy",
    "createdDate": "createdDate",
    "modifiedBy": "modifiedBy",
    "modifiedDate": "modifiedDate",
    "startDate": "startDateTime",
    "endDate": "endDateTime",
    "sortIndex": "sortOrder",
    "status": {
      "status": "status",
      "active": "Active",
      "inactive": "InActive"
    },
    "export": "export",
    "import": "import",
    "template": "template",
    "moreAction":"Mores",
    "success":{
      "success":"Success",
      "add":"Add Success!",
      "delete":"Delete Success!",
      "edit":"Edit Sccess!",
      "remove":"Remove Success!",
      "action":"Action Success!",
    },
    "error":{
      "add":"Add Error!",
      "delete":"Delete Error!",
      "edit":"Edit Error!",
      "remove":"Remove Error!",
      "action":"Action Errpr!",
    },
    "really":{
      "delete":"Are you sure to delete?",
      "remove":"Are you sure to remove?",
    },
    "expandOrFold":"Expand/Fold",
    "selectAllOrNot":"Select All/Select None",
    "connectChild":"Father son linkage",
    "treeLoading":"Loading, please wait",
    "showImg":"Show Img",
  },
  "common":{
    "rules":{
      "NotEmptyAndMaxLength255":"This length must be between 1 and 255",
      "NotEmptyAndMaxLength200":"This length must be between 1 and 200",
      "NotEmptyAndMaxLength128":"This length must be between 1 and 128",
      "NotEmptyAndMaxLength64":"This length must be between 1 and 64",
      "NotEmptyAndMaxLength32":"This length must be between 1 and 32",
      "NotEmpty":"This item cannot be empty",
      "uri":"This URL address format is incorrect",
      "ldap":"The format of this LDAP address is incorrect. Example: ldap://test.domain.com",
      "number":"This item can only input numeric types",
      "numberChar":"This item can only input numbers and letters",
      "validateFail":"Verification failed, please modify according to the prompts on the page and try again.",
      "noSpace":"The input cannot be a pure space character",
      "img":"Only supports JPG, JPEG, PNG formats!",
      "xml":"Only supports XML",
      "size":"The file size cannot exceed 2MB!",
    },
    "tips":{
      "upload":{
        "img":"Reminder: Uploading files supports JPG, JPEG, PNG formats, and the file size cannot exceed 2M",
        "xml":"Reminder: Uploading files supports XML, and the file size cannot exceed 2M",
      }
    },
  },
  "login":{
    "title":"LdapAuth",
    "zitext":"Based on OpenLDAP Enterprise Authentication Platform (EIAM), it is used to manage enterprise organizational structure, employee accounts, identity authentication, and application access, helping to integrate all identities of local or cloud based business systems and third-party SaaS systems, and enabling one account login to access all applications. Support SSO standard protocols such as OAuth2. x, OIDC, SAML2.0, JWT, CAS, etc.",
    "username":"Username",
    "password":"Password",
    "other":"Other Login",
    "code":"Code",
    "rememberMe":"Remember Password",
    "login":"Login",
    "logging":"Logging...",
    "usernameMessage":"Please enter the username",
    "passwordMessage":"Please enter the password",
    "codeMessage":"Please enter the code",
    "callBack":{
      "tips":"Tips",
      "tipsMessage1":"The system has obtained third-party authorized users. Please complete the binding",
      "tipsMessage2":"If you already have an account, please enter the corresponding account password. After verification, the relationship will be automatically bound. Accounts that have already been bound cannot be repeatedly bound",
      "tipsMessage3":"After successful binding, the system will log in directly for the second time without the need to bind again",
    }
  },
  "group":{
    "name":"Group Name",
    "namePlaceholder":"Please enter the Group Name",
    "nameMessage":"Group Name cannot be empty",
    "objectFrom":"Object From",
    "objectFromPlaceholder":"Please select the Object From",
    "status":"Status",
    "statusPlaceholder":"Please select the Status",
    "id":"Id",
    "authUser":"Member Management",
    "authResource":"Menu permissions",
    "descriptionPlaceholder":"Please enter the content",
    "editError":"Failed to query group information",
    "member":{
      "title":"Member Management",
      "username":"Username",
      "usernamePlaceholder":"Please enter the Username",
      "displayName":"DisplayName",
      "displayNamePlaceholder":"Please enter the DisplayName",
      "mobile":"Mobile",
      "mobilePlaceholder":"Please enter the Mobile",
      "add":"Add members",
      "remove":"Remove Members",
      "selectUser":"Select Users",
    },
    "resource":{
      "title":"Data permissions",
      "menu":"Menu permissions",
    },
  },
  "menu":{
    "name":"Name",
    "namePlaceholder":"Please enter the Name",
    "classify":"Classify",
    "classifyPlaceholder":"Please select the Classify",
    "status":"Status",
    "statusPlaceholder":"Please select the Status",
    "statusTip":"If you choose to disable, the route will not appear in the sidebar and cannot be accessed",
    "id":"Id",
    "permission":"Permission",
    "permissionPlaceholder":"Please enter the Permission",
    "permissionTip":"The permission characters defined in the controller",
    "routePath":"RoutePath",
    "routePathPlaceholder":"Please enter the Route Path",
    "routePathTip":"The routing address to be accessed, such as：`user`，If the external network address requires internal chain access, it should start with 'http (s)://'",
    "sortOrder":"SortOrder",
    "parentId":"Superior menu",
    "parentIdPlaceholder":"Please select the Superior menu",
    "icon":"Icon",
    "iconPlaceholder":"Click to select Icon",
    "frameTip":"If selecting an external link, the routing address needs to start with 'http (s)://'",
    "isFrame":"ExternalLink",
    "routeComponent":"Component",
    "routeComponentPlaceholder":"Please enter the Component path",
    "routeComponentTip":"The path to the accessed component, such as' system/user/index ', defaults to the' views' directory",
    "routeQuery":"Param",
    "routeQueryPlaceholder":"Please enter the Routing parameters",
    "routeQueryTip":"The default delivery parameters for accessing the route",
    "isCache":"Cache",
    "isCacheTip":"If you choose yes, it will be cached by 'keep alive', and the matching component's' name 'and address need to be consistent",
    "isVisible":"Visible",
    "isVisibleTip":"If you choose to hide, the route will not appear in the sidebar, but it can still be accessed",
    "classifyMessage":"Classify cannot be empty",
    "nameMessage":"Name cannot be empty",
    "sortOrderMessage":"SortOrder cannot be empty",
    "routePathMessage":"Route path cannot be empty",
  },
  "apps":{
    "addApp":"Add",
    "access":"访问应用",
    "function":"权限资源",
    "add":{
      "label":{
        "templateName":"Template Name",
        "protocol":"Protocol",
        "description":"description",
      },
    },
    "form":{
      "text":{
        "baseinfo":"Base Info",
        "extinfo":"Ext Info",
        "clientinfo":"Client Info",
      },
      "label":{
        "icon":"Icon",
        "appName":"App Name",
        "appCode":"App Code",
        "path":"Path",
        "protocol":"Protocol",
        "status":"Status",
        "createTime":"Create Time",
        "securityLevel":"Security Level",
        "deviceType":"Device Type",
        "homeUri":"Home Uri",
        "logoutUri":"Logout Uri",
        "description":"description",
        "clientId":"Client Id",
        "clientSecret":"Client Secret",
      },
      "search":{
        "placeholder":{
          "appCode":"Please enter the App Code going to search",
          "appName":"Please enter the App Name going to search",
          "protocol":"Please select the Protocol",
          "status":"Please select the Status",
        },
      },
      "placeholder":{
          "appCode":"Please enter the App Code",
          "appName":"Please enter the App Name",
          "deviceType":"Please select the Device Type",
          "path":"Application access path, please start with http or https",
          "homeUri":"Application access portal, please start with http or https",
          "logoutUri":"Application exit address, please start with http or https",
          "status":"Please select the Status",
        },
      "deviceType":{
        "mobile":"Mobile",
        "pc":"PC",
        "web":"WEB",
      },
      "oauth":{
        "label":{
          "authorizedGrantTypesList":"AuthorizedGrantTypesList",
          "redirectUri":"RedirectUri",
          "scopeList":"ScopeList",
          "accessTokenValidity":"ACCESS_TOKEN Validity Period",
          "refreshTokenValidity":"REFRESH_TOKEN Validity Period",
          "codeValidity":"Authorization code validity period",
        },
        "placeholder":{
          "authorizedGrantTypesList":"Please select Authorization mode",
          "redirectUri":"The URL address where the client initiates authentication, starting with http or https, for example: http://www.abc.com/user/sso",
          "scopeList":"Please select Scope",
        },
        "tips":{
          "idTokenValidity":"Unit Second",
        },
      },
      "saml":{
        "label":{
          "metaType":"Mata Data",
          "metaTypeFile":"Meta Type File",
          "no":"None",
          "certIssuer":"Cert Issuer",
          "certSubject":"Cert Subject",
          "certExpiration":"Cert Expiration",
          "subject":"Subject",
          "username":"Username",
          "mobile":"Mobile",
          "email":"Email",
          "spAcsUri":"SP ACS URL(SSO Location)",
          "entityId":"EntityId",
          "issuer":"Issuer",
          "audience":"Audience",
          "nameIdFormat":"NameIdFormat",
          "binding":"Binding",
          "viewMetadata":"View Metadata",
        },
        "placeholder":{
          "metaType":"Please select matadata",
          "subject":"Please select subject",
          "spAcsUri":"Please enter the SP ACS URL(SSO Location)",
        },
        "tips":{
          "binding":"The Bining method specified in the SAML protocol uses different communication methods and message bodies for different Binding methods. The commonly used method is for SP to forward SAMLRequest to IDP's SSO address through a browser using HTTP Redirect Binding, and IDP to return SAMLResponse to SP's ACS address using HTTP POST Binding.",
        },
      },
      "jwt":{
        "label":{
          "redirectUri":"Redirec tUri",
          "issuer":"Issuer",
          "signature":"Signature",
          "signatureKey":"Signature Key",
          "jwtName":"SSO JWT Name",
          "digestMethod":"DigestMethod",
          "ssoBinding":"SSO Binding",
          "idTokenValidity":"ID_TOKEN Validity Period",
          "accessUri":"Access Uri",
          "goAccessUri":"Go Access Uri",
          "publicKey":"Public Key",
          "viewPublicKey":"View Public Key",
          "isSignature":{
            "isSignature":"Is Signature",
            "yes":"Yes",
            "no":"No",
          },
        },
        "placeholder":{
          "redirectUri":"Callback address, starting with http or https, for example: http://www.abc.com/sp/sso/jwt or https://www.abc.com/sp/sso/jwt",
          "issuer":"Please enter the Issuer",
          "isSignature":"Please select yes or not",
          "signature":"Please select the signature",
          "jwtName":"Please enter the name of SSO JWT when calling back, for example, configuring id_token, the parameters when calling back http://xxx?id_token= {jwt}",
          "ssoBinding":"Please select SSO Binding",
          "idTokenValidity":"Unit second Period",
        },
      },
      "cas":{
        "label":{
          "serverNames":"ServerNames",
          "ticketValidity":"Ticket Validity",
          "subject":"Subject",
          "username":"Username",
          "mobile":"Mobile",
          "email":"Email",
        },
        "placeholder":{
          "serverNames":"CAS Services URI,  Please start with http or https",
        },
        "tips":{
          "serverNames":"The URL address for CAS client to initiate authentication, starting with http or https, for example: http://www.abc.com/user/sso",
          "ticketValidity":"Unit second",
        },
      },
      "oidc":{
        "label":{
          "authorizedGrantTypesList": "Authorized Grant Types",
          "responseMode":"ResponseMode",
          "redirectUri":"Redirect Url",
          "scopeList":"Scope List",
          "configuration":"Configuration",
          "publicKey":"Public Key",
          "codeValidity":"Authorization code CODE validity period",
          "idTokenValidity":"ID_TOKEN validity period",
          "accessTokenValidity":"ACCESS_TOKEN validity period",
          "refreshTokenValidity":"REFRESH_TOKEN validity period",
          "viewPublicKey":"View Public Key",
        },
        "placeholder":{
          "redirectUri":"The URL address where the client initiates authentication, starting with http or https, for example: http://www.abc.com/user/sso",
          "scopeList":"Please select scope",
          "responseMode":"Please select Response Mode",
          "authorizedGrantTypesList":"Please select Authorized Grant Types",
        },
        "tips":{
          "idTokenValidity":"Unit second",
        }

      },
    },

  },
  "org": {
    "organization": "Organization",
    "name": "Name",
    "openId": "Open ID",
    "from": "Source",
    "path": "Path",
    "status": "Status",
    "operate": "Operate",
    "edit": "Edit",
    "titleAdd": "Add new organizations",
    "titleEdit": "Edit",
    "deleteTip1": "Are you sure you want to delete the data item named \"",
    "deleteTip2": "\"?",
    "errorTree": "Error in getting organization tree data, unable to edit",
    "confirmDis": "Are you sure you want to disable this data?",
    "confirmActive": "Are you sure you want to enable this data?",
    "confirmDelete": "Are you sure you want to delete this data?",
    "confirmTip1": "Are you sure you want to ",
    "confirmTip2": "\" organization?",
    "confirmTip3": " the ",
    "type": "Type",
    "parent": "Parent",
    "sort": "Sorting",
    "cancel": "Cancel",
    "confirm": "Confirm",
    "typePh": "An organization is usually divided into multiple departments by administrative region.",
    "treeError": "Error in getting organization tree data, unable to edit",
    "success":{
      "delete": "successfully deleted",
      "disable": "Disable successfully",
      "active": "Enabled successfully",
      "add": "added successfully",
      "update": "Successfully modified",
    },
    "placeholder":{
      "name": "Please enter your organization name",
      "openId": "Please enter your organization's open ID",
      "parent": "Select the parent department",
    },
    "rule": {
      "name": "Organization name cannot be empty",
      "space": "Do not enter spaces"
    },
    "button":{
      "query": "Inquire",
      "reset": "Reset",
      "add": "Added",
      "disable": "Disable",
      "active": "Enable",
      "delete": "Delete",
    },
  },
  "user": {
    "username": "Login Account",
    "displayName": "Display Name",
    "mobile": "Phone Number",
    "email": "E-Mail",
    "belong": "Department",
    "confirmTip": "\"User?",
    "resetTip": "Are you sure you want to reset the password of the user with the login name \"",
    "resetTip1": "\"?",
    "userPwd": "user password",
    "addUser": "New users",
    "editUser": "Edit User",
    "password": "Password",
    "gender": "Gender",
    "avatar": "Avatar",
    "nickname": "Nick name",
    "birthDate": "Birth Date",
    "success": {
      "resetTip": "You have successfully reset the password of this user, which is",
      "resetTip1": ".Please inform this user in time."
    },
    "placeholder": {
      "username": "Please enter your login account",
      "displayName": "Please type in your name",
      "mobile": "Please enter the phone number"
    },
    "button": {
      "resetPwd": "reset Password",
      "sure": "Sure",
      "cancel": "Cancel",
    },
    "rule": {
      "name": "Login name cannot be empty",
      "nameLength": "Login name length must be between 2 and 64",
      "displayName": "Name cannot be empty",
      "displayNameLength": "Name length must be between 2 and 20 characters",
      "department": "Please select the user's department",
      "nickNameLength": "Nickname length cannot exceed 20",
      "pwd": "Please enter your password",
      "email": "Please input the correct email address",
      "mobile": "Please enter the correct phone number",
      "pwd1": "The account length needs to be",
      "pwd2": "Non-blank characters",
      "pwd3": "Must contain lowercase letters",
      "pwd4": "Must contain a number",
      "pwd5": "Special symbols must be included",
      "pwd6": "Cannot contain Chinese characters",
      "pwd7": "Cannot contain spaces",
    }
  },
  "profile": {
    "personal": "Personal Information",
    "male": "male",
    "female": "female",
    "other": "other",
    "basic": "basic information",
    "updatePwd": "change Password",
    "pwdPolicy": "Unable to read password policy configuration, please contact the administrator",
    "oldPwd": "Old Password",
    "newPwd": "New Password",
    "confirmPwd": "Confirm Password",
    "uploadAvatar": "Click to upload avatar",
    "select": "choose",
    "imgTip": "The file format is incorrect. Please upload an image of the same type, such as a file with a JPG or PNG suffix.",
    "placeholder": {
      "oldPwd": "Please enter your old password",
      "newPwd": "Please enter a new password",
      "confirmPwd": "Please enter the confirmation password",
    },
    "rule": {
      "twicePwd": "The passwords you entered twice do not match",
      "oldNew": "The passwords you entered twice do not match",
      "whiteSpace": "Do not enter pure spaces",
      "oldPwd": "The old password cannot be empty",
      "confirmPwd": "confirm password can not be blank",
      "birthDate": "Please enter your date of birth",
    }
  },
  "sms": {
    "provider": "SMS vendors",
    "sign": "SMS signature",
    "addSms": "Add SMS configuration",
    "edit": "edit",
    "tip": "\"SMS vendor?",
    "webSite": "Official website",
    "placeholder": {
      "provider": "Please enter the SMS manufacturer",
      "sign": "Please enter your SMS signature",
      "webSite": "Please enter the official website address",
      "appKey": "Please enter appkey",
      "secret": "Please enter appSecret",
    },
  },
  "forgot": {
    "title": "Recover Password",
    "placeholder": {
      "mobileOtp": "Please enter the SMS verification code"
    },
    "setNew": "Set New Password",
    "new": "New Password",
    "confirmNew": "Confirm password",
    "next": "Next step",
    "back": "Back to Login",
    "mobileTi1": "1. The mobile phone number that receives the verification code is the secure mobile phone number bound to your account.",
    "mobileTip2": "2. After sending the verification code, you can get it in the text message on your mobile phone (if you do not receive it within 1 minute, it is recommended that you check it in the spam or blocked text messages).",
    "mobileEmpty": "Phone number can not be blank",
    "pwdAgain": "confirm password can not be blank",
    "sendCode": "Send code",
    "waitAgain": "s",
    "sendAgain": "Resend",
    "sendSuccess": "Sent successfully",
    "setSuccess": "Set up for success",
    "pwdAgainInput": "Please enter your password again",
    "notSame": "The passwords you entered twice do not match",
  },
  "permissions": {
    "access": {
      "query":{
        "groupName": "Please enter the Group",
        "appName": "Please enter the App"
      },
      "label":{
        "groupName": "Group",
        "appName": "App",
        "createTime": "Time",
      },
      "button" :{
        "add": "Add",
        "delete": "Delete",
      },
      "rules":{
        "groupName": "Please check the Group",
        "appName": "Please check the App",
      },
      "message":{
        "confirmDelete": "Are you sure to cancel the authorization?",
        "success": "Success",
        "fail": "Fail",
      }
    }
  },
  "sync": {
    "tabs":{
      "workweixin":"Workweixin",
      "dingtalk":"Dingtalk",
      "feishu":"Feishu",
    },
    "common": {
      "ldap": {
        "baseApi": "Address",
        "clientId": "Administrator",
        "clientSecret": "Password",
        "baseDn": "BaseDn",
        "domain":"Domain",
        "userDn": "UserDn",
        "groupDn": "GroupDn",
        "orgFilter": "OrgFilter",
        "userFilter": "userFilter",
        "groupFilter": "GroupFilter",
        "placeholder": {
          "baseApi": "Please enter the address prefix, for example: ldap://test.com",
          "clientId": "LDAP administrator, example: cn=admin, dc=test, dc=com",
          "clientSecret": "LDAP administrator password",
          "baseDn": "example: dc=test, dc=com",
          "userDn": "example ：ou=people,dc=test,dc=com",
          "groupDn": "example ：ou=groups,dc=test,dc=com",
          "orgFilter": "example ：(&(objectClass=OrganizationalUnit))",
          "userFilter": "example ：(&(objectClass=inetOrgPerson)",
          "groupFilter": "example ：(&(objectClass=groupOfNames)",
        },
        "rules": {
          "baseApi": "Please enter the address",
          "clientId": "Please enter the Administrator ",
          "clientSecret": "Please enter the Password",
          "baseDn": "Please enter the BaseDn",
          "userDn": "Please enter the UserDn",
          "groupDn": "Please enter the GroupDn",
          "orgFilter": "Please enter the OrgFilter",
          "userFilter": "Please enter the UserFilter",
          "groupFilter": "Please enter the GroupFilter",
        }
      },
      "label": {
        "classify": "Data Classify",
        "baseApi": "Address",
        "sslfile": "File",
        "rootId": "RootId",
        "clientId": "ClientId",
        "clientSecret": "ClientSecret",
        "userClientSecret": "UserClientSecret",
        "status": "Status",
        "cron": "Cron",
      },
      "button": {
        "submitForm": "Submit",
        "submitSync": "Sync",
        "testLink": "Test",
        "logs": "Logs"
      },
      "placeholder": {
        "clientId": "Please enter the ClientId",
        "clientSecret": "Please enter the ClientSecret",
        "userClientSecret": "Please enter the UserClientSecret",
      },
      "rules": {
        "baseApi": "Please enter the address",
        "clientId": "Please enter the ClientId ",
        "clientSecret": "Please enter the ClientSecret",
        "userClientSecret": "Please enter the UserClientSecret"
      },
      "message": {
        "validate": "Verification failed, please modify according to the prompts on the page and try again.",
        "syncSuccess": "Processing in progress, please check the synchronization results later in the synchronization record",
        "syncFail": "Execution failed",
        "testSuccess": "Test connection successful",
        "testFail": "Test connection failed",
      }
    },
    "logs": {
      "label": {
        "trackingUniqueId": "TrackingUniqueId",
        "syncType": "SyncType",
        "syncActionType": "SyncAction",
        "syncResultStatus": "SyncResult",
        "id": "ID",
        "syncData": "Data",
        "createTime": "CreateTime",
        "syncMessage": "Message",
      },
    }
  },
  "distdata": {
    "sys_normal_disable": {
      "normal": "Normal",
      "disable": "Disable"
    },
    "sys_show_hide": {
      "show": "Visible",
      "hide": "Hide"
    },
    "sys_frame": {
      "yes": "Yes",
      "no": "No"
    },
    "sys_cache": {
      "yes": "Yes",
      "no": "No"
    },
    "sys_visible_hide": {
      "show": "visible",
      "hide": "Hide"
    },
    "sys_user_sex": {
      "unknown": "Unknown",
      "male": "Male",
      "female": "Female"
    },
    "sys_yes_no": {
      "yes": "Yes",
      "no": "No"
    },

    "sys_classify": {
      "directory": "Directory",
      "menu": "Menu",
      "button": "Button",
      "page": "Page",
      "api": "API",
      "other": "Other"
    },
    "sys_object_from": {
      "ldap": "LDAP",
      "local": "Local",
    },
    "org_classify": {
      "organization": "Organization",
      "department": "Department",
    },
    "sys_cron_list": {
      "corn2": "Execute every 2 hours",
      "corn4": "Execute every 4 hours",
      "corn6": "Execute every 6 hours",
      "corn8": "Execute every 8 hours",
      "corn12": "Execute every 12 hours",
    },
    "sys_sync_type": {
      "organization": "Organization",
      "user": "User",
      "group": "Group",
    },
    "sys_enable_status": {
      "enable": "Enable",
      "close": "Close",
    },
    "sys_sync_result_status": {
      "success": "Success",
      "fail": "Fail",
    },
    "sys_sync_action_type": {
      "add": "Add",
      "update": "Update",
      "delete": "Delete",
    },
    "sys_data_object_from": {
      "openLdap": "OpenLdap",
      "system": "Local",
      "dingding": "Dingtalk",
      "feishu": "Feishu",
      "workweixin": "Workweixin",
    },
    "sys_socials_type": {
      "wechatopen": "Wechatopen",
      "sinaweibo": "Sinaweibo",
      "dingtalk": "Dingtalk",
      "feishu": "Feishu",
      "alipay": "Alipay",
      "douyin": "Douyin",
      "workweixin": "Workweixin",
    }
  },
  "policy":{
    "tabs":{
      "login": "Login Policy",
      "password": "Passwor Policy"
    },
    "login":{
      "refreshTokenValidity": "RefreshTokenValidity",
      "accessTokenValidity": "AccessTokenValidity",
      "isCaptcha": "Captcha",
      "passwordAttempts": "PasswordAttempts",
      "loginLockInterval": "LockInterval",
    },
    "password":{
      "minLength": "MinLength",
      "maxLength": "MaxLength",
      "expirationDays": "expirationDays",
      "isDigit": "Digit",
      "isLowerCase": "LowerCase",
      "isSpecial": "Special",
    }
  },
  "socials":{
    "label":{
      "name":"Name",
      "status":"Status",
      "icon": "Icon",
      "type": "Type",
      "clientId": "ClientId",
      "clientSecret": "Secret",
      "agentId": "AgentId",
      "alipayPublicKey": "AlipayPublicKey",
      "redirectUri": "RedirectUri",
    },
    "placeholder":{
      "name":"Please enter a name",
      "status":"Please enter a status",
      "icon":"Please upload the icon",
      "type":"Please select type",
      "clientId": "Please enter a clientId",
      "clientSecret": "Please enter a secret",
      "agentId": "Please enter a agentId",
      "alipayPublicKey": "Please enter a alipayPublicKey",
      "redirectUri": "Please enter a redirectUri",
    }
  },
  "audit":{
    "login":{
      "userId":"UserId",
      "displayName":"Name",
      "provider":"Type",
      "ipAddr": "IP",
      "status": "Status",
      "operateSystem": "OperateSystem",
      "browser": "Browser",
      "message": "Message",
      "createTime": "CreateTime",
      "rangeDate": "RangeDate",
      "placeholder":{
        "displayName":"Please enter a name",
        "provider":"Please enter a type",
        "ipAddr": "Please enter a ip",
        "status": "Please enter a status",
        "startDate": "Please select startDate",
        "endDate": "Please select endDate",
      }
    },
    "applogin":{
      "userId":"UserId",
      "displayName":"Name",
      "appName":"AppName",
      "createTime": "CreateTime",
      "rangeDate": "RangeDate",
      "placeholder":{
        "displayName":"Please enter a name",
        "appName":"Please enter a AppName",
        "startDate": "Please select startDate",
        "endDate": "Please select endDate",
      }
    }
  },
  "system": {
    "icon": "Icon",
    "title": "Title",
    "copyright": "Copyright",
    "rules":{
      "icon": "Please upload icon",
      "title": "Please enter a title",
      "copyright": "Please enter a copyright",
    }
  },
  "navbar":{
    "language": "Language",
    "user": "Profile",
    "logout": "Logout",
    "logoutTips": "Warm reminder",
    "logoutText": "Are you sure to log out and exit the system?",
  }
}
