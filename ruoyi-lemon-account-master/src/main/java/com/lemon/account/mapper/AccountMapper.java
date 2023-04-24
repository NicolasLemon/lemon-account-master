package com.lemon.account.mapper;

import com.lemon.account.domain.Account;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * 柠檬账号大师 - 账号表 Mapper 接口
 * </p>
 *
 * @author Nicolas·Lemon
 * @since 2023/04/07
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {

    /**
     * 通过账户id批量物理删除账户（delete语句）
     *
     * @param accountIds 账号ID集合
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> accountIds);

    /**
     * 通过账号ID查询在是否存在子节点账号
     *
     * @param accountId 账号ID
     * @return 结果
     */
    @Select("select count(1) from lam_account where parent_id=#{accountId} limit 1")
    int hasChildByAccountId(Long accountId);

    /**
     * 通过用户ID查询所有账户信息
     *
     * @param userId 用户ID
     * @return 结果
     */
    List<Account> selectAccountsByUserId(@Param("userId") Long userId);

}
