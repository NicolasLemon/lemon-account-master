package com.lemon.account.service;

import com.lemon.account.domain.Account;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 柠檬账号大师 - 账号表 服务类
 * </p>
 *
 * @author Nicolas·Lemon
 * @since 2023/04/07
 */
public interface AccountService extends IService<Account> {

    /**
     * 根据传入的参数搜索相应的账户列表
     *
     * @param accountName   账号名称
     * @param accountInfo   账号说明
     * @param accountDomain 账号域名
     * @return 结果集
     */
    List<Account> list(String accountName, String accountInfo, String accountDomain);

    /**
     * 是否存在账户子节点
     *
     * @param accountId 账户ID
     * @return 结果
     */
    boolean hasChildByAccountId(Long accountId);

    /**
     * 通过用户ID查询所有账户信息
     *
     * @param userId 用户ID
     * @return 结果
     */
    @SuppressWarnings("unused")
    List<Account> listAccountsByUserId(Long userId);

}
