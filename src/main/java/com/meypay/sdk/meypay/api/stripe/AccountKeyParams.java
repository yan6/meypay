package com.meypay.sdk.meypay.api.stripe;

import com.meypay.sdk.meypay.model.BaseAccountParams;
import com.meypay.sdk.meypay.model.ProxyInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountKeyParams extends BaseAccountParams {
    private String sk;
    private ProxyInfo proxyInfo;
}
