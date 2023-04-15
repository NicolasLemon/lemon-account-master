package com.lemon.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.lemon.account.domain.Account;
import com.lemon.account.domain.UserAccount;
import com.lemon.account.mapper.AccountMapper;
import com.lemon.account.mapper.UserAccountMapper;
import com.lemon.account.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 柠檬账号大师 - 账号表 服务实现类
 * </p>
 *
 * @author Nicolas·Lemon
 * @since 2023/04/07
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    /**
     * 账户与用户关联表 Mapper
     */
    private final UserAccountMapper userMapper;

    /**
     * 假密码
     */
    private final String fakePwd = "******************";

    /**
     * 重写list()方法，拼接内部sql，并屏蔽明文密码
     *
     * @param queryWrapper Wrapper
     * @return 处理后的结果集
     */
    @Override
    public List<Account> list(Wrapper<Account> queryWrapper) {
        // 将Wrapper强制转换成QueryWrapper
        QueryWrapper<Account> wrapper = (QueryWrapper<Account>) queryWrapper;
        // 如果传入的是Wrappers.emptyWrapper()的话，就构造新的QueryWrapper
        if (queryWrapper.equals(Wrappers.emptyWrapper())) {
            wrapper = new QueryWrapper<>();
        }

        // 拼接内部sql
        wrapper.inSql("account_id", "select account_id from lam_user_account where user_id=" + SecurityUtils.getUserId());
        List<Account> accountList = super.list(wrapper);
        // 处理结果集，替换成假密码
        accountList.forEach(a -> a.setAccountPassword(StringUtils.isNotEmpty(a.getAccountPassword()) ? fakePwd : null));

        return accountList;
    }

    /**
     * 重写getOne()方法，增强条件查询
     *
     * @param queryWrapper Wrapper
     * @param throwEx      是否抛出异常
     * @return 查询结果
     */
    @Override
    public Account getOne(Wrapper<Account> queryWrapper, boolean throwEx) {
        // 拼接内部sql
        QueryWrapper<Account> wrapper = (QueryWrapper<Account>) queryWrapper;
        wrapper.inSql("account_id", "select account_id from lam_user_account where user_id=" + SecurityUtils.getUserId());
        return super.getOne(wrapper, throwEx);
    }

    /**
     * 重写saveOrUpdate()方法，增强关联表插入
     *
     * @param entity 账户实体类
     * @return 是否插入或更新成功
     */
    @Override
    public boolean saveOrUpdate(Account entity) {
        // 将自动填充的字段全设为空，避免携带原始参数影响
        entity.setCreateBy(null);
        entity.setCreateTime(null);
        entity.setUpdateBy(null);
        entity.setUpdateTime(null);
        entity.setDelFlag(null);

        // 设置祖级节点列表
        Long parentId = entity.getParentId();
        if (parentId == 0L) {
            entity.setAncestors("0");
        } else {
            // 查询上家的祖级节点列表并整合当前祖级节点列表
            Account one = super.getById(parentId);
            String ancestors = one.getAncestors() + "," + parentId;
            String sortString = StringUtils.sortString(ancestors, ",");
            entity.setAncestors(sortString);
        }

        // 先往账户表中插入或更新一条记录
        boolean isSavedOrUpdated = super.saveOrUpdate(entity);
        // 插入或更新不成功，就返回false
        if (!isSavedOrUpdated) {
            return false;
        }

        /// 插入或更新成功，就更新账户与用户关联表
        // 获取账户id和当前登录用户的id
        Long accountId = entity.getAccountId();
        Long userId = SecurityUtils.getUserId();
        // 通过条件筛选，查询关联表中是否有此记录
        QueryWrapper<UserAccount> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("account_id", accountId);
        UserAccount user = userMapper.selectOne(wrapper);
        // 如果关联表中有该记录就不操作了
        if (ObjectUtils.isNotEmpty(user)) {
            return true;
        }

        // 如果关联表中没有关联的记录就新增该关联项
        user = new UserAccount(userId, accountId);
        return SqlHelper.retBool(userMapper.insert(user));
    }

    /**
     * 根据传入的参数搜索相应的账户列表
     *
     * @param accountName   账号名称
     * @param accountInfo   账号说明
     * @param accountDomain 账号域名
     * @return 结果集
     */
    @Override
    public List<Account> list(String accountName, String accountInfo, String accountDomain) {
        // 查询当前用户所有的账户
        List<Account> accounts = super.getBaseMapper().selectAccountsByUserId(SecurityUtils.getUserId());
        // 替换成假密码
        accounts.forEach(a -> a.setAccountPassword(StringUtils.isNotEmpty(a.getAccountPassword()) ? fakePwd : null));

        /// 在所有账户信息中筛选出符合条件的结果
        // 筛选条件1：账号名称 + 账号说明 + 账号域名
        if (StringUtils.isNotEmpty(accountName) && StringUtils.isNotEmpty(accountInfo) && StringUtils.isNotEmpty(accountDomain)) {
            log.info("触发【账号名称 + 账号说明 + 账号域名】搜索");
            return accounts.stream()
                    .filter(u -> u.getAccountName().contains(accountName))
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountInfo()) && u.getAccountInfo().contains(accountInfo))
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountDomain()) && u.getAccountDomain().contains(accountDomain))
                    .collect(Collectors.toList());
        }
        // 筛选条件2：账号名称 + 账号说明
        if (StringUtils.isNotEmpty(accountName) && StringUtils.isNotEmpty(accountInfo)) {
            log.info("触发【账号名称 + 账号说明】搜索");
            return accounts.stream()
                    .filter(u -> u.getAccountName().contains(accountName))
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountInfo()) && u.getAccountInfo().contains(accountInfo))
                    .collect(Collectors.toList());
        }
        // 筛选条件3：账号名称 + 账号域名
        if (StringUtils.isNotEmpty(accountName) && StringUtils.isNotEmpty(accountDomain)) {
            log.info("触发【账号名称 + 账号域名】搜索");
            return accounts.stream()
                    .filter(u -> u.getAccountName().contains(accountName))
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountDomain()) && u.getAccountDomain().contains(accountDomain))
                    .collect(Collectors.toList());
        }
        // 筛选条件4：账号说明 + 账号域名
        if (StringUtils.isNotEmpty(accountInfo) && StringUtils.isNotEmpty(accountDomain)) {
            log.info("触发【账号说明 + 账号域名】搜索");
            return accounts.stream()
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountInfo()) && u.getAccountInfo().contains(accountInfo))
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountDomain()) && u.getAccountDomain().contains(accountDomain))
                    .collect(Collectors.toList());
        }
        // 筛选条件5：账号名称
        if (StringUtils.isNotEmpty(accountName)) {
            log.info("触发【账号名称】搜索");
            return accounts.stream()
                    .filter(u -> u.getAccountName().contains(accountName))
                    .collect(Collectors.toList());
        }
        // 筛选条件6：账号说明
        if (StringUtils.isNotEmpty(accountInfo)) {
            log.info("触发【账号说明】搜索");
            return accounts.stream()
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountInfo()) && u.getAccountInfo().contains(accountInfo))
                    .collect(Collectors.toList());
        }
        // 筛选条件7：账号域名
        if (StringUtils.isNotEmpty(accountDomain)) {
            log.info("触发【账号域名】搜索");
            return accounts.stream()
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountDomain()) && u.getAccountDomain().contains(accountDomain))
                    .collect(Collectors.toList());
        }

        return accounts;
    }

    /**
     * 是否存在账户子节点
     *
     * @param accountId 账户ID
     * @return 结果
     */
    @Override
    public boolean hasChildByAccountId(Long accountId) {
        int result = super.getBaseMapper().hasChildByAccountId(accountId);
        return result > 0;
    }

    @Override
    public List<Account> listAccountsByUserId(Long userId) {
        return super.getBaseMapper().selectAccountsByUserId(userId);
    }

}
