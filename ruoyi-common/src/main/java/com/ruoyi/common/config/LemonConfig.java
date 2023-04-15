package com.ruoyi.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取柠檬相关配置项
 *
 * @author Nicolas·Lemon
 * @since 2023/4/15
 */
@Data
@Component
@ConfigurationProperties(prefix = "lemon")
public class LemonConfig {

    /**
     * 默认密钥（32位）
     */
    private String defaultKey;
    /**
     * 默认密钥向量（16位）
     */
    private String defaultKeyIv;

}
