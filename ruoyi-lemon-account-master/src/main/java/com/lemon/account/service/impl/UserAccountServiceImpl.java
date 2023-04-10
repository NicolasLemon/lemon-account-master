package com.lemon.account.service.impl;

import com.lemon.account.domain.UserAccount;
import com.lemon.account.mapper.UserAccountMapper;
import com.lemon.account.service.UserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 柠檬账号大师 - 用户和账号关联表 服务实现类
 * </p>
 *
 * @author Nicolas·Lemon
 * @since 2023/04/07
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {
}
