package com.lemon.account.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 柠檬账号大师 - 账号表
 * </p>
 *
 * @author Nicolas·Lemon
 * @since 2023/04/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("lam_account")
public class Account extends BaseEntity {

    /**
     * 账号id
     */
    @TableId(value = "account_id", type = IdType.AUTO)
    private Long accountId;

    /**
     * 父账号id
     */
    private Long parentId;

    /**
     * 祖级列表
     */
    private String ancestors;

    /**
     * 账户名称
     */
    private String accountName;

    /**
     * 账户密码
     */
    private String accountPassword;

    /**
     * 账户key值偏移量iv
     */
    private String accountKeyIv;

    /**
     * 账户说明
     */
    private String accountInfo;

    /**
     * 账户域名
     */
    private String accountDomain;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

}
