package com.meypay.sdk.meypay.api.paypal.info;

import com.meypay.sdk.meypay.model.BaseOrder;
import com.paypal.api.payments.Payment;
import lombok.Data;

@Data
public class PayPalOrder extends BaseOrder {
    /**
     * 订单信息
     */
    private Payment payment;
    /**
     * 付款地址
     */
    private String approvalUrl;
}
