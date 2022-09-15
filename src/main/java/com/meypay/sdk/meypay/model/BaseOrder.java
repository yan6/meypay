package com.meypay.sdk.meypay.model;

import lombok.Data;

@Data
public class BaseOrder {
    /**
     * response status
     * default "01" not found error reason
     */
    private String status = "01";
    /**
     * response message
     */
    private String resultMsg;
}
