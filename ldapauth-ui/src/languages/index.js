import {createI18n} from "vue-i18n";

// 从语言包文件中导入语言包对象
import zhCN from './locales/zh-CN.js'
import enUS from './locales/en-US.js'
import zhTW from './locales/zh-TW.js'
import elZhCN from 'element-plus/es/locale/lang/zh-cn'
import elZhTW from 'element-plus/es/locale/lang/zh-tw'
import elEnUS from 'element-plus/es/locale/lang/en'

const langs = [
    {
        label: '简体中文',
        value: 'zh-CN'
    },
    {
        label: 'English',
        value: 'en-US'
    },
    {
        label: '繁體中文',
        value: 'zh-TW'
    }
]

const messages = {
    "zh-CN": {
        ...zhCN, ...elZhCN
    },
    "en-US": {
        ...enUS, ...elEnUS
    },
    "zh-TW": {
        ...zhTW, ...elZhTW
    },
}

const LangName = 'Admin-Lang'


export const getLocale = () => {
    // 获取缓存
    const storLanguage = localStorage.getItem(LangName)
    // 存在返回当前语言
    if (storLanguage) return storLanguage
    // 不存在 获取系统语言
    const language = (navigator.language || navigator.browserLanguage).toLowerCase()
    const locales = Object.keys(messages)
    for (const locale of locales) {
        if (language.indexOf(locale) > -1) {
            return locale
        }
    }
    // 默认返回简体中文
    return 'zh-CN'
}

export function setLang(land) {
    return localStorage.setItem(LangName, land)
}

export function removeLang() {
    return localStorage.removeItem(LangName)
}

export function getLangList() {
    return langs
}

const i18n = createI18n({
    legacy: false, // 使用Composition API，这里必须设置为false
    locale: getLocale(), // 默认显示语言
    globalInjection: true, // 全局注册$t方法
    messages: messages,
});

export default i18n;
