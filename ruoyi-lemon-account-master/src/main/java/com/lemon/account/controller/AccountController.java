package com.lemon.account.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lemon.account.domain.Account;
import com.lemon.account.service.impl.AccountServiceImpl;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
     * TODO 前端通过传入 账号名称、账号密码、账号说明、账号域名 上述操作没能实现
     * FIXME 具体bug，可以查看list()中的说明
     *
     * @param request 前端传入的查询参数
     * @return 分页查询的结果集（数据已做脱敏处理）
     */
    @GetMapping("/list")
    public TableDataInfo listAccounts(HttpServletRequest request) {
        startPage();
        String accountName = request.getParameter("accountName");
        String accountInfo = request.getParameter("accountInfo");
        String accountDomain = request.getParameter("accountDomain");
        return getDataTable(accountService.list(accountName, accountInfo, accountDomain));
    }

    /**
     * 通过账户id获取账户信息
     *
     * @param accountId 账户id
     */
    @GetMapping("/{accountId}")
    public AjaxResult getAccountById(@PathVariable("accountId") Long accountId) {
        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        wrapper.eq("account_id", accountId);
        return success(accountService.getOne(wrapper));
    }

    /**
     * 增加账户
     */
    @PostMapping
    public AjaxResult saveAccount(@Validated @RequestBody Account account) {
        return toAjax(accountService.saveOrUpdate(account));
    }

    /**
     * 修改账户
     */
    @PutMapping
    public AjaxResult updateAccount(@Validated @RequestBody Account account) {
        return this.saveAccount(account);
    }

    /**
     * 通过账户ID删除账户记录
     *
     * @param accountId 账户Id
     * @return 删除结果
     */
    @DeleteMapping("/{accountId}")
    public AjaxResult deleteAccountById(@PathVariable("accountId") Long accountId) {
        if (accountService.hasChildByAccountId(accountId)) {
            return warn("存在子账户，不允许删除");
        }
        return toAjax(accountService.removeById(accountId));
    }
}
