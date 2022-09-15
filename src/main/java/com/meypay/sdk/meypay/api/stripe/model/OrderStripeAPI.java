package com.meypay.sdk.meypay.api.stripe.model;

import com.meypay.sdk.meypay.api.stripe.AccountKey;
import com.meypay.sdk.meypay.api.stripe.info.StripeOrder;
import com.meypay.sdk.meypay.model.BaseOrder;
import com.meypay.sdk.meypay.model.ProxyInfo;
import com.meypay.sdk.meypay.service.base.Order;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.ChargeCollection;
import com.stripe.model.PaymentIntent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.List;
import java.util.Map;

/**
 * @author ywj
 * Stripe Order API
 */

public class OrderStripeAPI implements Order {
    @Override
    public <T extends BaseOrder> T create() {
        return null;
    }

    @Override
    public <T extends BaseOrder> T query() {
        return null;
    }

    /**
     * create payment
     * once account model
     * @param accountName
     * @param sk
     * @param proxyInfo
     * @param params
     * @return
     * @throws StripeException
     */
    public static StripeOrder create(String accountName,
                                     String sk,
                                     ProxyInfo proxyInfo,
                                     Map<String, Object> params) throws StripeException {
        initKey(accountName, sk, proxyInfo);

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        StripeOrder stripeOrder = new StripeOrder();
        stripeOrder.setPaymentIntent(paymentIntent);
        stripeOrder.setPaymentId(paymentIntent.getId());
        stripeOrder.setClientSecret(paymentIntent.getClientSecret());
        stripeOrder.setStatus("00");
        stripeOrder.setResultMsg("SUCCESS");

        return stripeOrder;
    }

    public static StripeOrder create(String sk, Map<String, Object> params) throws StripeException {
        return create(null, sk, null, params);
    }

    public static StripeOrder create(Map<String, Object> params, String accountName) throws StripeException {
        return create(accountName, null, null, params);
    }

    /**
     * query order by paymentId
     * @param sk
     * @param proxyInfo
     * @param paymentId：client_secret.split("_secret_")[0]
     * @return
     * @throws StripeException
     */
    public static StripeOrder query(String sk, ProxyInfo proxyInfo, String paymentId) throws StripeException {
        initKey(null, sk, proxyInfo);

        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentId);
        ChargeCollection charges = paymentIntent.getCharges();
        String chargeId = null;
        if (charges != null) {
            List<Charge> data = charges.getData();
            if (CollectionUtils.isNotEmpty(data)) {
                chargeId = data.get(0).getId();
            }
        }

        StripeOrder stripeOrder = new StripeOrder();
        stripeOrder.setPaymentIntent(paymentIntent);
        stripeOrder.setTransactionId(chargeId);
        if ("succeeded".equals(paymentIntent.getStatus())) {
            stripeOrder.setStatus("00");
            stripeOrder.setResultMsg("SUCCESS");
        }

        return stripeOrder;
    }

    public static StripeOrder query(String sk, String paymentId) throws StripeException {
        return query(sk, null, paymentId);
    }

    /**
     * init key
     * @param accountName
     * @param sk
     * @param proxyInfo
     */
    private static void initKey(String accountName, String sk, ProxyInfo proxyInfo) {
        if (StringUtils.isNotEmpty(accountName)) {
            AccountKey accountKey = new AccountKey(accountName);
            sk = accountKey.getAccountKeyParams().getSk();
            proxyInfo = accountKey.getAccountKeyParams().getProxyInfo();
        }
        Stripe.apiKey = sk;

        if (proxyInfo == null) {
            return;
        }
        Stripe.setConnectionProxy(
                new Proxy(Proxy.Type.HTTP,
                        new InetSocketAddress(proxyInfo.getHost(), Integer.valueOf(proxyInfo.getPort()))));
        Stripe.setProxyCredential(
                new PasswordAuthentication(proxyInfo.getUsername(), proxyInfo.getPassword().toCharArray()));
    }
}
