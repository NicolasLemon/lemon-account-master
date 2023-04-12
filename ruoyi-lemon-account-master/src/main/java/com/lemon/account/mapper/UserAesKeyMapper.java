package com.lemon.account.mapper;

import com.lemon.account.domain.UserAesKey;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.io.Serializable;

/**
 * <p>
 * 柠檬账号大师 - 用户和AES密钥关联表 Mapper 接口
 * </p>
 *
 * @author Nicolas·Lemon
 * @since 2023/04/12
 */
@Mapper
public interface UserAesKeyMapper extends BaseMapper<UserAesKey> {

    /**
     * 一对一联表查询
     */
    @Override
    @Results({
            @Result(column = "key_id", property = "keyId"),
            @Result(column = "key_id", property = "aesKey",
                    one = @One(select = "com.lemon.account.mapper.AesKeyMapper.selectById"))
    })
    @Select("select * from lam_user_aes_key where user_id=#{id};")
    UserAesKey selectById(Serializable id);

}
