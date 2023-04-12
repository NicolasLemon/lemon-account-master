package com.lemon.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.lemon.account.domain.AesKey;
import com.lemon.account.domain.UserAesKey;
import com.lemon.account.mapper.AesKeyMapper;
import com.lemon.account.mapper.UserAesKeyMapper;
import com.lemon.account.service.UserAesKeyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.KeyUtils;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UserAesKeyServiceImpl extends ServiceImpl<UserAesKeyMapper, UserAesKey> implements UserAesKeyService {

    /**
     * AES密钥 Mapper
     */
    private final AesKeyMapper aesKeyMapper;

    /**
     * 重写save()方法，实现AES密钥表与用户密钥关联表的同步插入
     *
     * @param entity AES实体对象
     */
    @Override
    public boolean save(UserAesKey entity) {
        AesKey aesKey = new AesKey();
        // 生成一个32位随机密钥，并加密该密钥
        String key = KeyUtils.aes256Encode(KeyUtils.generateKey(32));
        aesKey.setAesKey(key);
        int insert = aesKeyMapper.insert(aesKey);
        if (!SqlHelper.retBool(insert)) {
            return false;
        }
        // 查询一遍数据库，找到刚刚插入的Key值的id，并同步插入用户与密钥的关联表中
        QueryWrapper<AesKey> wrapper = new QueryWrapper<>();
        wrapper.eq("aes_key", aesKey.getAesKey());
        AesKey selectOne = aesKeyMapper.selectOne(wrapper);
        entity.setKeyId(selectOne.getKeyId());
        return super.save(entity);
    }

}
