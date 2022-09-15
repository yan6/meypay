package com.meypay.sdk.meypay.api.paypal;

import com.meypay.sdk.meypay.model.BaseAccountParams;
import com.meypay.sdk.meypay.model.ProxyInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountParams extends BaseAccountParams {
    private String clientId;
    private String clientSecret;
    private String mode;

    private ProxyInfo proxyInfo;

    public AccountParams(String accountName, String clientId, String clientSecret, String mode, ProxyInfo proxyInfo) {
        super(accountName);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.mode = mode;
        this.proxyInfo = proxyInfo;
    }
}
