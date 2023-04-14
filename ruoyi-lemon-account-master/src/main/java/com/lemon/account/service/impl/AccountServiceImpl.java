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
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    /**
     * 账户与用户关联表 Mapper
     */
    private final UserAccountMapper userMapper;

    /**
     * 重写list()方法，拼接内部sql，并屏蔽明文密码
     *
     * @param queryWrapper Wrapper
     * @return 处理后的结果集
     */
    @Override
    public List<Account> list(Wrapper<Account> queryWrapper) {
        // 需要将真密码替换成的假密码
        final String fakePwd = "*********";

        // 将Wrapper强制转换成QueryWrapper
        QueryWrapper<Account> wrapper = (QueryWrapper<Account>) queryWrapper;
        // 如果传入的是Wrappers.emptyWrapper()的话，就构造新的QueryWrapper
        if (queryWrapper.equals(Wrappers.emptyWrapper())) {
            wrapper = new QueryWrapper<>();
        }

        // 拼接内部sql
        wrapper.inSql("account_id", "select account_id from lam_user_account where user_id=" + SecurityUtils.getUserId());
        List<Account> accountList = super.list(wrapper);
        // 处理结果集，将密码改为星号或null
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
        // 将自动填充的字段全设为空，因为修改的时候，会携带原始的参数
        entity.setCreateBy(null);
        entity.setCreateTime(null);
        entity.setUpdateBy(null);
        entity.setUpdateTime(null);
        entity.setDelFlag(null);
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
     * TODO 逻辑有待实现
     * FIXME 目前数据库表中是都进行过加密了的，且每条数据加密的偏移量都是不同的，这对这种搜索，就有很大的麻烦
     *
     * @param accountName     账号名
     * @param accountPassword 账户密码
     * @param accountInfo     账号说明
     * @param accountDomain   账号域名
     * @return 结果集
     */
    @Override
    public List<Account> list(String accountName, String accountPassword, String accountInfo, String accountDomain) {
        // 判断上述条件是否全为空，全为空就查询所有符合条件的记录
        boolean isParamAllEmpty = StringUtils.isEmpty(accountName) &&
                StringUtils.isEmpty(accountPassword) &&
                StringUtils.isEmpty(accountInfo) &&
                StringUtils.isEmpty(accountDomain);
        if (isParamAllEmpty) {
            return super.list();
        }
        // 拼接查询条件（这里才是问题所在）
        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(accountName)) {
            wrapper.eq("account_name", accountName);
        }
        // return super.list(wrapper);
        return super.list();
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

}
