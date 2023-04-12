package com.lemon.account.service.impl;

import com.lemon.account.domain.AesKey;
import com.lemon.account.mapper.AesKeyMapper;
import com.lemon.account.service.AesKeyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 柠檬账号大师 - AES密钥表 服务实现类
 * </p>
 *
 * @author Nicolas·Lemon
 * @since 2023/04/12
 */
@Service
public class AesKeyServiceImpl extends ServiceImpl<AesKeyMapper, AesKey> implements AesKeyService {

}
