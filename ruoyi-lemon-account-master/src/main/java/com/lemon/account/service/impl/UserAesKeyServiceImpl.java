package com.lemon.account.service.impl;

import com.lemon.account.domain.UserAesKey;
import com.lemon.account.mapper.UserAesKeyMapper;
import com.lemon.account.service.UserAesKeyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 柠檬账号大师 - 用户和AES密钥关联表 服务实现类
 * </p>
 *
 * @author Nicolas·Lemon
 * @since 2023/04/12
 */
@Service
public class UserAesKeyServiceImpl extends ServiceImpl<UserAesKeyMapper, UserAesKey> implements UserAesKeyService {

}
