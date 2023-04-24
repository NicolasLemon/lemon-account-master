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
     * 子级列表
     */
    private String posterities;

    /**
     * 账号节点名
     */
    private String accountNodeName;

    /**
     * 用户名
     */
    private String accountUserName;

    /**
     * 用户密码
     */
    private String accountUserPwd;

    /**
     * 账号说明
     */
    private String accountInfo;

    /**
     * key值偏移量iv
     */
    private String accountKeyIv;

    /**
     * 账号域名
     */
    private String accountDomain;

}
