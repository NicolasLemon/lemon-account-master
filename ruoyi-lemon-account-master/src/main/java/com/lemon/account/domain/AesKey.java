package com.lemon.account.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 柠檬账号大师 - AES密钥表
 * </p>
 *
 * @author Nicolas·Lemon
 * @since 2023/04/12
 */
@Data
@Accessors(chain = true)
@TableName("lam_aes_key")
public class AesKey implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 密钥ID
     */
    @TableId(value = "key_id", type = IdType.AUTO)
    private Long keyId;

    /**
     * AES密钥
     */
    private String aesKey;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
