import request from '@/utils/request'
import {handleTree} from '@/utils/ruoyi.js'
import useUserStore from '@/store/modules/user'

function setChildrenMenu(menus) {
    menus.forEach(t => {
        if (t.meta.activeMenu && !t.meta.activeMenu.startsWith('/')) {
            t.meta.activeMenu = '/' + t.meta.activeMenu
        }
        if (t.children && t.children.length > 0) {
            setChildrenMenu(t.children)
        }
    })
}

// 获取路由
export const getRouters = () => {
    return new Promise((resolve, reject) => {
        request({
            url: '/users/getRouters',
            method: 'get'
        }).then(res => {
            useUserStore().permissions = res.data.map(t => {
                return t.permission
            })

            const menus = res.data.filter(t => t.status === 0 && t.classify === 'menu')

            const mapMenu = {}
            const mapParentIds = {}
            menus.forEach(item => {
                mapMenu[item.id] = item
                mapParentIds[item.parentId] = item
            })

            let tree = menus.filter(t => t.status === 0 && t.classify === 'menu')
                .map(menu => {
                    const icon = (menu.icon || "")
                    return {
                        "id": menu.id,
                        "parentId": menu.parentId,
                        "name": menu.permission,
                        "path": menu.routePath,
                        "hidden": menu.isVisible === 0,
                        "redirect": "noRedirect",
                        "component": mapParentIds[menu.id] ? "ParentView" : menu.routeComponent,
                        "alwaysShow": !!mapParentIds[menu.id],
                        "permissions": [menu.permission],
                        "meta": {
                            "title": menu.name,
                            "icon": icon,
                            "noCache": menu.isCache === 1,
                            "link": menu.isFrame === 1 ? menu.routePath : null,
                            "activeMenu": mapMenu[menu.parentId] ? mapMenu[menu.parentId].routePath : null
                        }
                    }
                })

            tree = handleTree(tree || [])
            tree = tree.map(t => {
                if (t.path && !t.path.startsWith('/')) {
                    t.path = '/' + t.path
                }
                if (t.meta.activeMenu && !t.meta.activeMenu.startsWith('/')) {
                    t.meta.activeMenu = '/' + t.meta.activeMenu
                }

                if (!t.children || t.children.length < 1) {
                    const r = JSON.parse(JSON.stringify(t))
                    r.children = [t]
                    r.component = 'Layout'
                    r.path = ''
                    r.name = null
                    r.permissions = null
                    return r
                } else {
                    t.component = 'Layout'
                    setChildrenMenu(t.children)
                    return t
                }
            })

            // 将首页重定向到第一个页面
            if (tree.length > 0) {
                const index = tree[0].children[0]
                tree[0].redirect = index.path
            }

            resolve({
                code: 200,
                data: tree
            })
        }).catch(err => {
            reject(err)
        })
    })
}
