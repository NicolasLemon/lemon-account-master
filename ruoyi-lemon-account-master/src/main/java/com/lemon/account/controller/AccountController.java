package com.lemon.account.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lemon.account.domain.Account;
import com.lemon.account.service.impl.AccountServiceImpl;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 柠檬账号大师账号表 前端控制器
 * </p>
 *
 * @author Nicolas·Lemon
 * @since 2023/04/07
 */
@RestController
@RequestMapping("/lam/account")
@RequiredArgsConstructor
public class AccountController extends BaseController {

    /**
     * 账号表 Service
     */
    private final AccountServiceImpl accountService;

    /**
     * 分页查询账号列表
     *
     * @return 分页查询的结果集（数据已做脱敏处理）
     */
    @GetMapping("/list")
    public TableDataInfo listAccounts() {
        startPage();
        return getDataTable(accountService.list());
    }

    /**
     * 通过账户id获取账户信息
     *
     * @param accountId 账户id
     */
    @GetMapping("/{accpuntId}")
    @RepeatSubmit(message = "请求过于频繁（两次请求小于5s）")
    public AjaxResult getAccountByAccountId(@PathVariable("accpuntId") String accountId) {
        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        wrapper.eq("account_id", accountId);
        return success(accountService.getOne(wrapper));
    }

    /**
     * 增加账户
     */
    @PostMapping
    public AjaxResult addAccount(@Validated @RequestBody Account account) {
        return toAjax(accountService.saveOrUpdate(account));
    }

    /**
     * 修改账户
     */
    @PutMapping
    public AjaxResult updateAccount(@Validated @RequestBody Account account) {
        return this.addAccount(account);
    }

}
