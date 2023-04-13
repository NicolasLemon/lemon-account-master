package com.lemon.account.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 柠檬账号大师 - 用户和AES密钥关联表
 * </p>
 *
 * @author Nicolas·Lemon
 * @since 2023/04/12
 */
@Data
@Accessors(chain = true)
@TableName("lam_user_aes_key")
public class UserAesKey {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 密钥ID
     */
    private Long keyId;

    /**
     * AES密钥实体类
     */
    @TableField(exist = false)
    private AesKey aesKey;

}
