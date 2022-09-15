package com.meypay.sdk.meypay.api.stripe;

import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class AccountKey {
    private AccountKeyParams accountKeyParams;

    private volatile static Map<String, AccountKeyParams> apiKeyMap;

    public AccountKey(String accountName) {
        this.accountKeyParams = apiKeyMap.get(accountName);
    }

    // 多账号模式初始化
    public static void initApiKeyMap(List<AccountKeyParams> accountKeyParams) {
        if (CollectionUtils.isEmpty(accountKeyParams)) {
            // 或自定义异常抛出
            return;
        }
        if (apiKeyMap == null) {
            synchronized (AccountKey.class) {
                if (apiKeyMap == null) {
                    apiKeyMap = new ConcurrentHashMap<>();
                    accountKeyParams.forEach(x -> apiKeyMap.put(x.getAccountName(),
                            new AccountKeyParams(x.getSk(), x.getProxyInfo())));
                }
            }
        }
    }
}
