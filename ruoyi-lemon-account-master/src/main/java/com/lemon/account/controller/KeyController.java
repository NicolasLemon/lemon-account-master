package com.lemon.account.controller;

import com.ruoyi.common.config.LemonConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.KeyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 密钥生成 前端控制器
 *
 * @author Nicolas·Lemon
 * @since 2023/4/16
 */
@RestController
@RequestMapping("/lam/key")
public class KeyController extends BaseController {

    /**
     * 随机生成一组密钥
     *
     * @return 结果
     */
    @GetMapping
    public AjaxResult generateKey() {
        LemonConfig lemonConfig = new LemonConfig();
        lemonConfig.setDefaultKey(KeyUtils.generateKey(32));
        lemonConfig.setDefaultKeyIv(KeyUtils.generateKey(16));
        return success(lemonConfig);
    }

}
