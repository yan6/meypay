package com.meypay.sdk.meypay.api.paypal;

import com.paypal.base.rest.APIContext;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class AccountContext {
    private APIContext apiContext;

    /**
     * 多账号模式
     * key名称，唯一
     */
    private static volatile Map<String, APIContext> apiContextMap;

    // 单账号模式
    public AccountContext(String clientID,
                          String clientSecret,
                          String mode,
                          Map<String, String> configurations) {
        this.apiContext = new APIContext(clientID, clientSecret, mode, configurations);
    }

    // 多账号模式
    public AccountContext(String accountName) {
        this.apiContext = apiContextMap.get(accountName);
    }

    /**
     * 多账号模式使用时，可静态代码块方式初始化
     *
     * @param accountParamsList
     */
    public static void initApiContextMap(List<AccountParams> accountParamsList) {
        if (CollectionUtils.isEmpty(accountParamsList)) {
            // 或自定义异常抛出
            return;
        }

        if (apiContextMap == null) {
            synchronized (AccountContext.class) {
                if (apiContextMap == null) {
                    apiContextMap = new ConcurrentHashMap<>();

                    accountParamsList.forEach(x -> {
                        Map<String, String> configurations = null;
                        if (x.getProxyInfo() != null) {
                            configurations = new HashMap<>();
                            configurations.put("http.UseProxy", x.getProxyInfo().getUseProxy().toString());
                            configurations.put("http.ProxyPort", x.getProxyInfo().getPort());
                            configurations.put("http.ProxyHost", x.getProxyInfo().getHost());
                            configurations.put("http.ProxyUserName", x.getProxyInfo().getUsername());
                            configurations.put("http.ProxyPassword", x.getProxyInfo().getPassword());
                        }

                        apiContextMap.put(x.getAccountName(),
                                new APIContext(x.getClientId(),
                                        x.getClientSecret(),
                                        x.getMode(),
                                        configurations));
                    });
                }
            }
        }
    }
}
