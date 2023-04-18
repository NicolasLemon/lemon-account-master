package com.lemon.account.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 心跳 前端控制器
 *
 * @author 尼古拉斯·柠檬
 * @since 2023/4/18
 */
@RestController
@RequestMapping("/lam/heart")
public class HeartController extends BaseController {

    /**
     * 判断当前项目是否正常运行
     *
     * @return 成功的提示
     */
    @GetMapping
    public AjaxResult getHeartBeat() {
        return success();
    }
}
