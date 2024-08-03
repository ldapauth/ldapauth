import Cookies from 'js-cookie'

const TokenKey = 'Admin-Token'
const RefreshTokenKey = 'refresh_token'
const TokenInfoKey = '_token'
const UserInfoKey = 'user'
const InstKey = 'inst'
const AppKey = 'app'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

export function getRefreshToken() {
  return window.localStorage.getItem(RefreshTokenKey)
}

export function setRefreshToken(token) {
  return window.localStorage.setItem(RefreshTokenKey, token)
}

export function removeRefreshToken() {
  return window.localStorage.removeItem(RefreshTokenKey)
}


export function getTokenInfo() {
  return JSON.parse(window.localStorage.getItem(TokenInfoKey));
}

export function setTokenInfo(info) {
  return window.localStorage.setItem(TokenInfoKey, JSON.stringify(info || {}))
}

export function removeTokenInfo() {
  return window.localStorage.removeItem(TokenInfoKey)
}

export function getUserInfo() {
  return JSON.parse(window.localStorage.getItem(UserInfoKey))
}

export function setUserInfo(info) {
  return window.localStorage.setItem(UserInfoKey, JSON.stringify(info || {}))
}

export function removeUserInfo() {
  return window.localStorage.removeItem(UserInfoKey)
}

export function getInst() {
  return JSON.parse(window.localStorage.getItem(InstKey))
}

export function setInst(info) {
  return window.localStorage.setItem(InstKey, JSON.stringify(info || {}))
}

export function removeInst() {
  return window.localStorage.removeItem(InstKey)
}

export function getApp() {
  return JSON.parse(window.localStorage.getItem(AppKey))
}

export function setApp(info) {
  window.localStorage.setItem(AppKey, JSON.stringify(info || {}))
}

export function removeApp() {
  return window.localStorage.removeItem(AppKey)
}

