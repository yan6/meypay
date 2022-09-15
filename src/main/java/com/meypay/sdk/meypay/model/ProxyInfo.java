package com.meypay.sdk.meypay.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProxyInfo {
    private Boolean useProxy;
    private String port;
    private String host;
    private String username;
    private String password;
}
