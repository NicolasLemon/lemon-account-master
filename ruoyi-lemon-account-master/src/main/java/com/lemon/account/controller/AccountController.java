package com.lemon.account.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lemon.account.domain.Account;
import com.lemon.account.domain.UserAccount;
import com.lemon.account.service.impl.AccountServiceImpl;
import com.lemon.account.service.impl.UserAccountServiceImpl;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
     * 用户与账号关联表 Service
     */
    private final UserAccountServiceImpl userAccountService;

    /**
     * 查询账号列表（含模糊条件查询）
     *
     * @param request 前端传入的查询参数
     * @return 结果（数据已做脱敏处理）
     */
    @GetMapping("/list")
    public TableDataInfo listAccounts(HttpServletRequest request) {
        // 获取传入的查询参数
        String accountNodeName = request.getParameter("accountNodeName");
        String accountInfo = request.getParameter("accountInfo");
        String accountDomain = request.getParameter("accountDomain");
        boolean isParamAllEmpty = StringUtils.isEmpty(accountNodeName) &&
                StringUtils.isEmpty(accountInfo) &&
                StringUtils.isEmpty(accountDomain);

        List<Account> accounts;
        if (isParamAllEmpty) {
            // 不带条件的查询
            accounts = accountService.list();
        } else {
            /// 带条件的查询
            // 无视大小写比较
            if (StringUtils.isNotEmpty(accountNodeName)) {
                accountNodeName = accountNodeName.toLowerCase();
            }
            if (StringUtils.isNotEmpty(accountInfo)) {
                accountInfo = accountInfo.toLowerCase();
            }
            if (StringUtils.isNotEmpty(accountDomain)) {
                accountDomain = accountDomain.toLowerCase();
            }
            accounts = accountService.list(accountNodeName, accountInfo, accountDomain);
        }
        TableDataInfo dataTable = getDataTable(accounts);

        // 统计有效账号数量（用户名或者密码不为空的记录）
        long total = 0L;
        for (Account account : accounts) {
            if (StringUtils.isEmpty(account.getAccountUserName()) && StringUtils.isEmpty(account.getAccountUserPwd())) {
                continue;
            }
            total++;
        }
        dataTable.setTotal(total);
        return dataTable;
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
            return warn("存在子账号节点，不允许删除");
        }
        // 删除账号表中的数据
        boolean isRemoved = accountService.removeById(accountId);
        // 删除用户与账号关联表中的数据
        if (isRemoved) {
            QueryWrapper<UserAccount> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", getUserId())
                    .eq("account_id", accountId);
            userAccountService.remove(wrapper);
        }
        return toAjax(isRemoved);
    }

}
