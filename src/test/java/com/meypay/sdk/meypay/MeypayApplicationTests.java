package com.meypay.sdk.meypay;

import com.meypay.sdk.meypay.api.paypal.AccountContext;
import com.meypay.sdk.meypay.api.paypal.AccountParams;
import com.meypay.sdk.meypay.api.paypal.info.PayPalOrder;
import com.meypay.sdk.meypay.api.paypal.payment.OrderPayPalAPI;
import com.meypay.sdk.meypay.api.stripe.info.StripeOrder;
import com.meypay.sdk.meypay.api.stripe.model.OrderStripeAPI;
import com.meypay.sdk.meypay.model.ProxyInfo;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Sale;
import com.paypal.base.rest.PayPalRESTException;
import com.stripe.exception.StripeException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
class MeypayApplicationTests {

    static {
        // PayPal 多账号模式，账号信息初始化
        List<AccountParams> accountParamsList = new ArrayList<>();
        accountParamsList.add(new AccountParams("paypal0_sandbox",
                "xx",
                "xx",
                OrderPayPalAPI.SANDBOX,
                null));
        accountParamsList.add(new AccountParams("paypal0_live",
                "xx",
                "xx",
                OrderPayPalAPI.LIVE,
                new ProxyInfo(true, "123", "11.11.11.11", "xxxx", "xxxx")));

        AccountContext.initApiContextMap(accountParamsList);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void paypalCreate() {
        OrderPayPalAPI orderPayPalAPI = new OrderPayPalAPI("test", "0.99", "USD",
                "1", "testCancelUrl", "testReturnUrl");
        try {
            PayPalOrder payPalOrder = orderPayPalAPI.create("", "", OrderPayPalAPI.SANDBOX);
            if ("00".equals(payPalOrder.getStatus())) {
                // approvalUrl 交给客户端处理
                String approvalUrl = payPalOrder.getApprovalUrl();
                // 订单详情，根据业务需要保存
                Payment payment = payPalOrder.getPayment();
            } else {
                // 其它状态
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
    }

    @Test
    void paypalProxyCreate() {
        OrderPayPalAPI orderPayPalAPI = new OrderPayPalAPI("test", "0.99", "USD",
                "1", "testCancelUrl", "testReturnUrl");
        try {
            ProxyInfo proxyInfo = new ProxyInfo(true, "123", "11.11.11.11", "xxxx", "xxxx");
            PayPalOrder payPalOrder = orderPayPalAPI.create("",
                    "", "", OrderPayPalAPI.SANDBOX, proxyInfo);
            if ("00".equals(payPalOrder.getStatus())) {
                // approvalUrl 交给客户端处理
                String approvalUrl = payPalOrder.getApprovalUrl();
                // 订单详情，根据业务需要保存
                Payment payment = payPalOrder.getPayment();
            } else {
                // 其它状态
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
    }

    @Test
    void paypalAccoutNameProxyCreate() {
        OrderPayPalAPI orderPayPalAPI = new OrderPayPalAPI("test", "0.99", "USD",
                "1", "testCancelUrl", "testReturnUrl");
        try {
            // accountName 随意，但唯一
            PayPalOrder payPalOrder = orderPayPalAPI.create("paypal0_sandbox");
            if ("00".equals(payPalOrder.getStatus())) {
                // approvalUrl 交给客户端处理
                String approvalUrl = payPalOrder.getApprovalUrl();
                // 订单详情，根据业务需要保存
                Payment payment = payPalOrder.getPayment();
            } else {
                // 其它状态
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
    }

    @Test
    void paypalExecute() {
        // PayPal以GET方式发送通知或回调
        // 异步通知/浏览器回调时URL中包含参数paymentId,payerId
        OrderPayPalAPI orderPayPalAPI = new OrderPayPalAPI("0.99", "USD", "", "");
        try {
            PayPalOrder payPalOrder = orderPayPalAPI.execute("", "", OrderPayPalAPI.SANDBOX);
            if ("00".equals(payPalOrder.getStatus())) {
                // 支付成功，继续处理业务，比如发货
            } else {
                // 其它状态
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
    }

    @Test
    void paypalQuery() {
        OrderPayPalAPI orderPayPalAPI = new OrderPayPalAPI("");
        try {
            PayPalOrder payPalOrder = orderPayPalAPI.query("", "", OrderPayPalAPI.SANDBOX);
            if (payPalOrder == null) {
                return;
            }
            Sale sale = payPalOrder.getPayment().getTransactions().get(0).getRelatedResources().get(0).getSale();
            if (StringUtils.equals(sale.getState(), "completed")) {
                // 完成支付
            } else if (StringUtils.equals(sale.getState(), "partially_refunded")) {
                // 部分退款
            } else if (StringUtils.equals(sale.getState(), "refunded")) {
                // 退款
            } else if (StringUtils.equals(sale.getState(), "denied")) {
                // 被拒绝
            } else {
                // 其他
            }
            if (StringUtils.equals(sale.getState(), "pending") && "ECHECK".equals(sale.getPaymentMode())) {
                //ECHECK 模式付款，需特殊处理
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
    }

    @Test
    void stripeCreate() {
        try {
            // sk区分环境
            Map<String, Object> params = new HashMap<>();
            params.put("amount", 100); // int类型 注意：USD时单位美分
            params.put("currency", "USD");
            params.put("receipt_email", "");
            List<String> payment_method_types = Arrays.asList("card".split("\\s*,\\s*"));
            params.put(
                    "payment_method_types",
                    payment_method_types
            );
            StripeOrder stripeOrder = OrderStripeAPI.create("", params);
            if (stripeOrder == null) {
                return;
            }
            // stripeId,stripeClientSecret返回客户端
            String stripeId = stripeOrder.getPaymentIntent().getId();
            // 存入数据库，订单唯一标识，webhook使用
            String stripeClientSecret = stripeOrder.getPaymentIntent().getClientSecret();
        } catch (StripeException e) {
            e.printStackTrace();
        }
    }

    @Test
    void stripeQuery() {
        try {
            // webhook携带参数client_secret，可以查到数据库对应订单，使用client_secret.split("_secret_")[0]
            StripeOrder stripeOrder = OrderStripeAPI.query("", "");
            if (stripeOrder != null && "00".equals(stripeOrder.getStatus())) {
                // 支付成功
            }
        } catch (StripeException e) {
            e.printStackTrace();
        }
    }
}
