package com.lemon.account.mapper;

import com.lemon.account.domain.Account;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

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
}
