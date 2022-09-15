package com.meypay.sdk.meypay.api.stripe.info;

import com.meypay.sdk.meypay.model.BaseOrder;
import com.stripe.model.PaymentIntent;
import lombok.Data;

@Data
public class StripeOrder extends BaseOrder {
    private PaymentIntent paymentIntent;

    private String paymentId;
    private String transactionId;
    private String clientSecret;
}
