import Cookies from 'js-cookie'

const TokenKey = 'access_token'

const RefreshTokenKey = 'refresh_token'

export function getAccessToken() {
  const value = Cookies.get(TokenKey)
  return value && `Bearer ${value}` || value
}

export function getRefreshToken() {
  const value = Cookies.get(RefreshTokenKey)
  return value && `Bearer ${value}` || value
}

export function setAccessToken(token) {
  return Cookies.set(TokenKey, token)
}

export function setRefreshToken(token) {
  return Cookies.set(RefreshTokenKey, token)
}

export function removeAccessToken() {
  return Cookies.remove(TokenKey)
}

export function removeRefreshToken() {
  return Cookies.remove(RefreshTokenKey)
}

export function setRoles(roles) {
  return Cookies.set('roles', roles)
}

export function getRoleInfos() {
  const roles = Cookies.get('roles')
  return roles && JSON.parse(roles) || []
}
