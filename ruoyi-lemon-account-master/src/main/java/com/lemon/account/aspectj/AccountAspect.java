package com.lemon.account.aspectj;

import com.lemon.account.domain.Account;
import com.lemon.account.utils.KeyUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sign.Base64;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 账户切面增强
 *
 * @author 尼古拉斯·柠檬
 * @since 2023/4/11
 */
@Aspect
@Component
@Slf4j
@SuppressWarnings("rawtypes")
public class AccountAspect {

    /**
     * 查询账户后置执行增强
     */
    @AfterReturning(pointcut = "execution(* com.lemon.account.mapper.AccountMapper.select*(..))", returning = "result")
    public Object doAfterSelectAccount(JoinPoint point, Object result) {
        // 打印提示消息
        printMessage(point, "@AfterReturning");
        // 判断结果是List集合还是Object对象
        if (result instanceof List) {
            // 增强账户对象，解密记录中的加密字段
            for (Object object : (List) result) {
                decodeMessage((Account) object);
            }
            return result;
        }
        decodeMessage((Account) result);
        return result;
    }

    /**
     * 插入或更新账户前置增强
     */
    @Before("execution(* com.lemon.account.mapper.AccountMapper.insert*(..)) || execution(* com.lemon.account.mapper.AccountMapper.update*(..))")
    public void doBeforeInsertOrUpdateAccount(JoinPoint point) {
        // 打印提示消息
        printMessage(point, "@Before");
        // 获取对应参数
        Object[] pointArgs = point.getArgs();
        for (Object pointArg : pointArgs) {
            Account account = (Account) pointArg;
            // 如果原对象中有keyIv的值的话，就延用该值
            String keyIv = account.getAccountKeyIv();
            if (StringUtils.isNotEmpty(keyIv)) {
                keyIv = new String(Base64.decode(keyIv));
            } else {
                // 反之就随机生成偏移量
                keyIv = KeyUtils.generateKey(16);
            }
            account.setAccountName(KeyUtils.aes256Encode(keyIv, account.getAccountName()));
            account.setAccountPassword(KeyUtils.aes256Encode(keyIv, account.getAccountPassword()));
            account.setAccountInfo(KeyUtils.aes256Encode(keyIv, account.getAccountInfo()));
            account.setAccountDomain(KeyUtils.aes256Encode(keyIv, account.getAccountDomain()));
            keyIv = Base64.encode(keyIv.getBytes(StandardCharsets.UTF_8));
            account.setAccountKeyIv(keyIv);
        }
    }

    /**
     * 打印提示消息
     */
    private void printMessage(JoinPoint joinPoint, String msg) {
        // 获取切入的方法名称
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method signatureMethod = signature.getMethod();
        log.info("开启{}方法{}增强", signatureMethod.getName(), msg);
    }


    /**
     * 解密账户密文信息
     *
     * @param account 账户对象
     */
    private void decodeMessage(Account account) {
        // 获取偏移量
        String keyIv = account.getAccountKeyIv();
        // Base64解码偏移量
        keyIv = new String(Base64.decode(keyIv));
        // 解密账户名
        account.setAccountName(KeyUtils.aes256Decode(keyIv, account.getAccountName()));
        // 解密账户密码
        account.setAccountPassword(KeyUtils.aes256Decode(keyIv, account.getAccountPassword()));
        // 解密账户说明
        account.setAccountInfo(KeyUtils.aes256Decode(keyIv, account.getAccountInfo()));
        // 解密账户所属域名
        account.setAccountDomain(KeyUtils.aes256Decode(keyIv, account.getAccountDomain()));
        // 等解密完成后将偏移量置为空（不给用户查看到偏移量）
        account.setAccountKeyIv(null);
    }
}
