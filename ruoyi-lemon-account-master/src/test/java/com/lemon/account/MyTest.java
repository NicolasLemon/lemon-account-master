package com.lemon.account;

import com.ruoyi.common.utils.KeyUtils;
import org.junit.Test;

/**
 * @author 尼古拉斯·柠檬
 * @since 2023/4/12
 */
public class MyTest {
    @Test
    public void myTest() {
        System.out.println("a：" + KeyUtils.aes256Encode("TRBuX0XCDpcdCYaGnzjY8M#4NA!O2e7r"));
    }
}
