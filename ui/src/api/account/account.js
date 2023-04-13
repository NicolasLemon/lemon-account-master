/*
 * @Author: Nicolas·Lemon
 * @Date: 2023-04-07 09:59:33
 * @LastEditors: Nicolas·Lemon
 * @LastEditTime: 2023-04-13 18:53:01
 * @Description: API接口
 */
import request from '@/utils/request'

// 查询柠檬账号大师账号列表
export function listAccount(query) {
  return request({
    url: '/lam/account/list',
    method: 'get',
    params: query
  })
}

// 查询柠檬账号大师账号详细
export function getAccount(accountId) {
  return request({
    url: '/lam/account/' + accountId,
    method: 'get'
  })
}

// 新增柠檬账号大师账号
export function addAccount(data) {
  return request({
    url: '/lam/account/',
    method: 'post',
    data: data
  })
}

// 修改柠檬账号大师账号
export function updateAccount(data) {
  return request({
    url: '/lam/account/',
    method: 'put',
    data: data
  })
}

// 删除柠檬账号大师账号
export function delAccount(accountId) {
  return request({
    url: '/lam/account/' + accountId,
    method: 'delete'
  })
}
