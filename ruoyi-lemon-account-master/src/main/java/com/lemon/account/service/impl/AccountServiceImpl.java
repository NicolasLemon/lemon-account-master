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

import java.util.*;
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
    private final static String fakePwd = "******************";

    /**
     * 分隔符
     */
    private final static String sep = ",";

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
        // 处理结果集，不显示真用户名和真密码
        return fakeAccountList(accountList);
    }

    /**
     * 处理结果集，不显示真用户名和真密码
     *
     * @param accountList 账号列表
     * @return 结果
     */
    private List<Account> fakeAccountList(List<Account> accountList) {
        // 集合去重
        accountList = accountList.stream().distinct().collect(Collectors.toList());
        // 不返回真实的用户名和用户密码
        accountList.forEach(a -> {
            a.setAccountUserPwd(StringUtils.isNotEmpty(a.getAccountUserPwd()) ? fakePwd : null);
            String name = a.getAccountUserName();
            if (StringUtils.isNotEmpty(name)) {
                a.setAccountUserName(name.charAt(0) + fakePwd.substring(0, 3) + name.charAt(name.length() - 1));
            } else {
                a.setAccountUserName(null);
            }
        });
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

        // 设置祖级列表
        Long parentId = entity.getParentId();
        if (!Objects.equals(parentId, 0L)) {
            // 查询上家的祖级节点列表并整合当前祖级节点列表
            Account one = super.getById(parentId);
            String ancestors = one.getAncestors() + sep + parentId;
            ancestors = String.join(sep, StringUtils.str2Set(ancestors, sep));
            entity.setAncestors(ancestors);
        }

        // 先往账户表中插入或更新一条记录
        boolean isSavedOrUpdated = super.saveOrUpdate(entity);
        // 插入或更新不成功，就返回false
        if (!isSavedOrUpdated) {
            return false;
        }

        /// 插入或更新成功
        Long accountId = entity.getAccountId();
        // 设置子孙列表
        if (!Objects.equals(parentId, 0L)) {
            Account accountById = super.getById(parentId);
            String posterities = accountById.getPosterities();
            posterities += (StringUtils.isEmpty(posterities) ? "" : sep) + accountId;
            accountById.setPosterities(posterities);
            super.updateById(accountById);
        }

        // 更新账户与用户关联表
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
     * @param accountNodeName 账号节点名称
     * @param accountInfo     账号说明
     * @param accountDomain   账号域名
     * @return 结果集
     */
    @Override
    public List<Account> list(String accountNodeName, String accountInfo, String accountDomain) {
        // 查询当前用户所有的账户
        final List<Account> accounts = super.getBaseMapper().selectAccountsByUserId(SecurityUtils.getUserId());

        /// 在所有账户信息中筛选出符合条件的结果
        // 筛选条件1：账号节点名称 + 账号说明 + 账号域名
        if (StringUtils.isNotEmpty(accountNodeName) && StringUtils.isNotEmpty(accountInfo) && StringUtils.isNotEmpty(accountDomain)) {
            log.info("触发【账号节点名称 + 账号说明 + 账号域名】搜索");
            List<Account> list = accounts.stream()
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountNodeName()) && u.getAccountNodeName().contains(accountNodeName))
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountInfo()) && u.getAccountInfo().contains(accountInfo))
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountDomain()) && u.getAccountDomain().contains(accountDomain))
                    .collect(Collectors.toList());
            return getAncestorsAndPosterities(accounts, list);
        }
        // 筛选条件2：账号节点名称 + 账号说明
        if (StringUtils.isNotEmpty(accountNodeName) && StringUtils.isNotEmpty(accountInfo)) {
            log.info("触发【账号节点名称 + 账号说明】搜索");
            List<Account> list = accounts.stream()
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountNodeName()) && u.getAccountNodeName().contains(accountNodeName))
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountInfo()) && u.getAccountInfo().contains(accountInfo))
                    .collect(Collectors.toList());
            return getAncestorsAndPosterities(accounts, list);
        }
        // 筛选条件3：账号节点名称 + 账号域名
        if (StringUtils.isNotEmpty(accountNodeName) && StringUtils.isNotEmpty(accountDomain)) {
            log.info("触发【账号节点名称 + 账号域名】搜索");
            List<Account> list = accounts.stream()
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountNodeName()) && u.getAccountNodeName().contains(accountNodeName))
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountDomain()) && u.getAccountDomain().contains(accountDomain))
                    .collect(Collectors.toList());
            return getAncestorsAndPosterities(accounts, list);
        }
        // 筛选条件4：账号说明 + 账号域名
        if (StringUtils.isNotEmpty(accountInfo) && StringUtils.isNotEmpty(accountDomain)) {
            log.info("触发【账号说明 + 账号域名】搜索");
            List<Account> list = accounts.stream()
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountInfo()) && u.getAccountInfo().contains(accountInfo))
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountDomain()) && u.getAccountDomain().contains(accountDomain))
                    .collect(Collectors.toList());
            return getAncestorsAndPosterities(accounts, list);
        }
        // 筛选条件5：账号节点名称
        if (StringUtils.isNotEmpty(accountNodeName)) {
            log.info("触发【账号节点名称】搜索");
            List<Account> list = accounts.stream()
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountNodeName()) && u.getAccountNodeName().contains(accountNodeName))
                    .collect(Collectors.toList());
            return getAncestorsAndPosterities(accounts, list);
        }
        // 筛选条件6：账号说明
        if (StringUtils.isNotEmpty(accountInfo)) {
            log.info("触发【账号说明】搜索");
            List<Account> list = accounts.stream()
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountInfo()) && u.getAccountInfo().contains(accountInfo))
                    .collect(Collectors.toList());
            return getAncestorsAndPosterities(accounts, list);
        }
        // 筛选条件7：账号域名
        if (StringUtils.isNotEmpty(accountDomain)) {
            log.info("触发【账号域名】搜索");
            List<Account> list = accounts.stream()
                    .filter(u -> StringUtils.isNotEmpty(u.getAccountDomain()) && u.getAccountDomain().contains(accountDomain))
                    .collect(Collectors.toList());
            return getAncestorsAndPosterities(accounts, list);
        }
        return fakeAccountList(accounts);
    }

    /**
     * 获取符合条件的搜索结果的祖先列表和子孙列表
     *
     * @param accounts 所有账号记录
     * @param list     符合条件的账号记录
     * @return 结果
     */
    private List<Account> getAncestorsAndPosterities(List<Account> accounts, List<Account> list) {
        List<Account> results = new ArrayList<>();
        // 获取子孙列表
        results.addAll(getPosterities(accounts, list));
        // 获取祖先列表
        results.addAll(getAncestors(accounts, list));
        // 隐藏用户名和密码
        return fakeAccountList(results);
    }

    /**
     * 递归获取子孙列表
     *
     * @param accounts 所有结果
     * @param list     符合条件的结果
     * @return 结果
     */
    private List<Account> getPosterities(List<Account> accounts, List<Account> list) {
        List<Account> results = new ArrayList<>();
        // 循环符合条件的搜索结果，去找寻它的子孙
        for (Account account : list) {
            // 获取子级列表
            String posterities = account.getPosterities();
            if (StringUtils.isEmpty(posterities)) {
                results.add(account);
                continue;
            }

            results.add(account);
            // 筛选出子级列表
            List<String> strings = StringUtils.str2List(posterities, sep, true, false);
            List<Account> collect = accounts.stream()
                    .filter(a -> strings.contains(a.getAccountId().toString()))
                    .collect(Collectors.toList());
            // 进入下一层递归
            results.addAll(getPosterities(accounts, collect));
        }
        return results;
    }

    /**
     * 获取祖先列表
     *
     * @param accounts 当前登录用户所有的账号
     * @param list     符合条件的结果
     * @return 结果
     */
    private static List<Account> getAncestors(List<Account> accounts, List<Account> list) {
        List<Account> result = new ArrayList<>();
        list.forEach(u -> {
            String ancestors = u.getAncestors();
            List<String> strings = StringUtils.str2List(ancestors, sep, true, false);
            List<Account> collect = accounts.stream().filter(a -> strings.contains(a.getAccountId().toString())).collect(Collectors.toList());
            result.addAll(collect);
        });
        return result;
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
