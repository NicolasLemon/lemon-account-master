/*
 * @Author: Nicolas·Lemon
 * @Date: 2023-04-07 09:59:33
 * @LastEditors: Nicolas·Lemon
 * @LastEditTime: 2023-04-10 22:16:27
 * @Description: API接口
 */
import request from '@/utils/request'

// 查询柠檬账号大师账号列表
export function listAccount(query) {
  return request({
    url: '/account/accounts',
    method: 'get',
    params: query
  })
}

// 查询柠檬账号大师账号详细
export function getAccount(accountId) {
  return request({
    url: '/account/account/' + accountId,
    method: 'get'
  })
}

// 新增柠檬账号大师账号
export function addAccount(data) {
  return request({
    url: '/account/account',
    method: 'post',
    data: data
  })
}

// 修改柠檬账号大师账号
export function updateAccount(data) {
  return request({
    url: '/account/account',
    method: 'put',
    data: data
  })
}

// 删除柠檬账号大师账号
export function delAccount(accountId) {
  return request({
    url: '/account/account/' + accountId,
    method: 'delete'
  })
}
