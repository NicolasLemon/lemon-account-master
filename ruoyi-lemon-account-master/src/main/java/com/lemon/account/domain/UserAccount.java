package com.lemon.account.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 柠檬账号大师 - 用户和账号关联表
 * </p>
 *
 * @author Nicolas·Lemon
 * @since 2023/04/07
 */
@Data
@Accessors(chain = true)
@TableName("lam_user_account")
public class UserAccount {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 柠檬账号ID
     */
    private Long accountId;

    @TableField(exist = false)
    List<Account> accountList;

    public UserAccount(Long userId, Long accountId) {
        this.userId = userId;
        this.accountId = accountId;
    }

}
