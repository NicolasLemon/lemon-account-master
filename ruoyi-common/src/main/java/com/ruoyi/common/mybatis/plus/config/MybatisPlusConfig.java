package com.ruoyi.common.mybatis.plus.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.ruoyi.common.utils.SecurityUtils;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.apache.commons.lang3.ObjectUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis-Plus 配置类
 *
 * @author https://blog.csdn.net/Michelle_Zhong/article/details/123926533
 * @author 尼古拉斯·柠檬
 * @since 2023/4/10
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@MapperScan("$(mybatis-plus.mapperPackage)")
public class MybatisPlusConfig {

    /**
     * MyBatis-Plus 拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 多租户插件配置
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            /**
             * 获取租户ID
             */
            @Override
            public Expression getTenantId() {
                // 多租户模式，租户id从当前登录用户获取
                Long tenantId = SecurityUtils.getTenantId();
                if (ObjectUtils.isEmpty(tenantId)) {
                    return new LongValue(0);
                }
                return new LongValue(tenantId);
            }

            /**
             * 租户ID的列名
             */
            @Override
            public String getTenantIdColumn() {
                // 对应数据库租户ID的列名
                return "user_id";
            }

            /**
             * 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
             */
            @Override
            public boolean ignoreTable(String tableName) {
                // 只拼接登录用户
                String loginUser = String.valueOf(SecurityUtils.getAuthentication());
                if (!loginUser.contains("LoginUser")) {
                    return true;
                }
                return !"lam_user_account".equalsIgnoreCase(tableName);
            }
        }));

        return interceptor;
    }

}
