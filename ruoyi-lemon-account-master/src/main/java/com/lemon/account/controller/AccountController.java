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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * 分页查询账号列表（含模糊条件查询）
     *
     * @param request 前端传入的查询参数
     * @return 分页查询的结果集（数据已做脱敏处理）
     */
    @GetMapping("/list")
    public TableDataInfo listAccounts(HttpServletRequest request) {
        // 获取传入的查询参数
        String accountName = request.getParameter("accountName");
        String accountInfo = request.getParameter("accountInfo");
        String accountDomain = request.getParameter("accountDomain");
        boolean isParamAllEmpty = StringUtils.isEmpty(accountName) &&
                StringUtils.isEmpty(accountInfo) &&
                StringUtils.isEmpty(accountDomain);
        // 不带条件的分页查询
        if (isParamAllEmpty) {
            startPage();
            return getDataTable(accountService.list());
        }

        // 带条件的分页查询
        List<Account> accounts = accountService.list(accountName, accountInfo, accountDomain);
        // 对符合条件的结果进行分页处理
        int pageNum = Integer.parseInt(request.getParameter("pageNum"));
        int pageSize = Integer.parseInt(request.getParameter("pageSize"));
        // 不需要分页
        if (pageSize >= accounts.size()) {
            return getDataTable(accounts);
        }

        // 将结果均分成pageSize的List数组
        List<List<Account>> splitList = Stream.iterate(0, n -> n + 1)
                .limit(countStep(accounts.size(), pageSize)).parallel()
                .map(a -> accounts.stream().skip((long) a * pageSize)
                        .limit(pageSize).parallel().collect(Collectors.toList()))
                .collect(Collectors.toList());
        // 设置当前页的结果
        TableDataInfo dataTable = getDataTable(splitList.get(pageNum - 1));
        // 这里需要重新设置一下总条数
        dataTable.setTotal(accounts.size());
        return dataTable;
    }

    /**
     * 计算切分次数
     *
     * @param listSize List的长度
     * @param maxSize  一次最大长度
     */
    private static Integer countStep(Integer listSize, Integer maxSize) {
        return (listSize + maxSize - 1) / maxSize;
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
