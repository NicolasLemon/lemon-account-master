package com.lemon.account.mapper;

import com.lemon.account.domain.UserAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

/**
 * <p>
 * 柠檬账号大师 - 用户和账号关联表 Mapper 接口
 * </p>
 *
 * @author Nicolas·Lemon
 * @since 2023/04/07
 */
@Mapper
public interface UserAccountMapper extends BaseMapper<UserAccount> {
}
