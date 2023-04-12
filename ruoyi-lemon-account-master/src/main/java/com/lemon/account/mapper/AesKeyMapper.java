package com.lemon.account.mapper;

import com.lemon.account.domain.AesKey;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 柠檬账号大师 - AES密钥表 Mapper 接口
 * </p>
 *
 * @author Nicolas·Lemon
 * @since 2023/04/12
 */
@Mapper
public interface AesKeyMapper extends BaseMapper<AesKey> {

}
