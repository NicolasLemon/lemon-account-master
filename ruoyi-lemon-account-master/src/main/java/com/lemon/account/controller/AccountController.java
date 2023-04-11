package com.lemon.account.controller;


import com.lemon.account.domain.Account;
import com.lemon.account.service.impl.AccountServiceImpl;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController extends BaseController {

    /**
     * 账号表 Service
     */
    private final AccountServiceImpl accountService;

    /**
     * 分页查询账号列表
     *
     * @return 分页查询的结果集（密码密码已做脱敏处理）
     */
    @GetMapping("/accounts")
    public AjaxResult listAccounts() {
        startPage();
        return success(accountService.list());
    }

    @GetMapping("/account/{id}")
    public AjaxResult getAccountByAccountId(@PathVariable("id") String accountId) {
        return success(accountService.getById(accountId));
    }

    @PostMapping("/add")
    public AjaxResult addAccount() {
        Account account = new Account();
        account.setAccountId(203L);
        account.setAccountName("二级名称2");
        account.setAccountPassword("二级密码2");
        account.setAccountDomain("二级域名2");
        account.setAccountInfo("二级说明2");
        return toAjax(accountService.saveOrUpdate(account));
    }

}
