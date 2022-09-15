package com.meypay.sdk.meypay.api.paypal.payment;

import com.meypay.sdk.meypay.api.CommonPay;
import com.meypay.sdk.meypay.api.paypal.AccountContext;
import com.meypay.sdk.meypay.api.paypal.info.PayPalOrder;
import com.meypay.sdk.meypay.model.BaseOrder;
import com.meypay.sdk.meypay.model.ProxyInfo;
import com.meypay.sdk.meypay.service.base.Order;
import com.paypal.api.payments.*;
import com.paypal.base.rest.PayPalRESTException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ywj
 * PayPal Order API
 */

@Data
public class OrderPayPalAPI extends CommonPay implements Order {

    private static final Logger LOG = LoggerFactory.getLogger(OrderPayPalAPI.class);

    private static final String paymentMethod = "paypal";
    private static final String paymentIntent = "sale";

    public static final String SANDBOX = "sandbox";
    public static final String LIVE = "live";

    private String paymentId;
    private String total;
    private String name;
    private String description;
    private String sku;
    private String price;
    private String currency;
    private String quantity;
    private String cancelUrl;
    private String returnUrl;
    private String payerId;// 付款账号唯一标识

    public OrderPayPalAPI() {

    }

    /**
     * @param total       总数量
     * @param name        名称
     * @param description 描述
     * @param sku         属性
     * @param price       价格
     * @param currency    货币类型
     * @param quantity    数量
     * @param cancelUrl   取消地址或接口地址
     * @param returnUrl   准备购买地址或接口地址
     */
    public OrderPayPalAPI(String total, String name,
                          String description, String sku,
                          String price, String currency,
                          String quantity, String cancelUrl, String returnUrl) {
        this.total = total;
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.price = price;
        this.currency = currency;
        this.quantity = quantity;
        this.cancelUrl = cancelUrl;
        this.returnUrl = returnUrl;
    }

    public OrderPayPalAPI(String sku, String price, String currency,
                          String quantity, String cancelUrl, String returnUrl) {
        this(price, price, sku, sku, price, currency, quantity, cancelUrl, returnUrl);
    }

    /**
     * 查询订单构造
     */
    public OrderPayPalAPI(String paymentId) {
        this.paymentId = paymentId;
    }

    /**
     * 执行扣费构造
     */
    public OrderPayPalAPI(String price, String currency,
                          String paymentId, String payerId) {
        this(paymentId);
        this.price = price;
        this.currency = currency;
        this.payerId = payerId;
    }


    @Override
    public <T extends BaseOrder> T create() {
        return null;
    }

    @Override
    public <T extends BaseOrder> T query() {
        return null;
    }

    /**
     * 创建订单
     *
     * @param clientId
     * @param clientSecret
     * @param mode         环境
     * @param proxyInfo    代理
     * @return
     * @throws PayPalRESTException
     */
    public PayPalOrder create(String accountName,
                              String clientId,
                              String clientSecret,
                              String mode,
                              ProxyInfo proxyInfo) throws PayPalRESTException {
        // 普通请求，并发请求，代理请求
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(total);

        ItemList itemList = new ItemList();
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setSku(sku);
        item.setPrice(price);
        item.setCurrency(currency);
        item.setQuantity(quantity);
        items.add(item);
        itemList.setItems(items);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setItemList(itemList);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(paymentMethod);

        Payment payment = new Payment();
        payment.setIntent(paymentIntent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(returnUrl);
        payment.setRedirectUrls(redirectUrls);

        Map<String, String> configurations = null;
        if (proxyInfo != null) {
            configurations = new HashMap<>();
            configurations.put("http.UseProxy", proxyInfo.getUseProxy().toString());
            configurations.put("http.ProxyPort", proxyInfo.getPort());
            configurations.put("http.ProxyHost", proxyInfo.getHost());
            configurations.put("http.ProxyUserName", proxyInfo.getUsername());
            configurations.put("http.ProxyPassword", proxyInfo.getPassword());
        }

        AccountContext accountContext;
        if (StringUtils.isEmpty(accountName)) {
            accountContext = new AccountContext(clientId, clientSecret, mode, configurations);
        } else {
            accountContext = new AccountContext(accountName);
        }
        payment = payment.create(accountContext.getApiContext());
        LOG.info("OrderPayPalAPI create payment:{}", payment);

        PayPalOrder paypalOrder = new PayPalOrder();
        paypalOrder.setPayment(payment);
        for (Links links : payment.getLinks()) {
            if (!"approval_url".equals(links.getRel())) {
                continue;
            }
            paypalOrder.setApprovalUrl(links.getHref());//客户付款登陆地址
            paypalOrder.setStatus("00");
            paypalOrder.setResultMsg("SUCCESS");
            break;
        }

        return paypalOrder;
    }

    /**
     * 创建订单
     *
     * @param clientId
     * @param clientSecret
     * @param mode         环境
     * @return
     * @throws PayPalRESTException
     */
    public PayPalOrder create(String clientId, String clientSecret, String mode) throws PayPalRESTException {
        return this.create(null, clientId, clientSecret, mode, null);
    }

    /**
     * 创建订单多账号同时在线使用
     * 注意调用之前需初始化 apiContextMap
     *
     * @throws PayPalRESTException
     */
    public PayPalOrder create(String accountName) throws PayPalRESTException {
        return this.create(accountName, null, null, null, null);
    }

    /**
     * 查询订单
     *
     * @param clientId
     * @param clientSecret
     * @param mode           环境
     * @param configurations
     * @return
     * @throws PayPalRESTException
     */
    public PayPalOrder query(String accountName,
                             String clientId,
                             String clientSecret,
                             String mode,
                             Map<String, String> configurations) throws PayPalRESTException {
        AccountContext accountContext;
        if (StringUtils.isEmpty(accountName)) {
            accountContext = new AccountContext(clientId, clientSecret, mode, configurations);
        } else {
            accountContext = new AccountContext(accountName);
        }
        Payment payment = Payment.get(accountContext.getApiContext(), paymentId);

        PayPalOrder paypalOrder = new PayPalOrder();
        paypalOrder.setPayment(payment);
        if (payment != null) {
            paypalOrder.setStatus("00");
            paypalOrder.setResultMsg("SUCCESS");
        }

        return paypalOrder;
    }

    /**
     * 查询订单
     *
     * @param clientId
     * @param clientSecret
     * @param mode         环境
     * @return
     * @throws PayPalRESTException
     */
    public PayPalOrder query(String clientId, String clientSecret, String mode) throws PayPalRESTException {
        return this.query(null, clientId, clientSecret, mode, null);
    }

    public PayPalOrder query(String accountName) throws PayPalRESTException {
        return this.query(accountName, null, null, null, null);
    }

    /**
     * 执行扣费
     *
     * @param clientId
     * @param clientSecret
     * @param mode
     * @param configurations
     * @return
     * @throws PayPalRESTException
     */
    public PayPalOrder execute(String accountName,
                               String clientId,
                               String clientSecret,
                               String mode,
                               Map<String, String> configurations) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setTotal(price);
        amount.setCurrency(currency);

        Transactions transaction = new Transactions();
        transaction.setAmount(amount);
        List<Transactions> transactions = new ArrayList<>();
        transactions.add(transaction);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        paymentExecution.setTransactions(transactions);

        Payment payment = new Payment();
        payment.setId(paymentId);
        AccountContext accountContext;
        if (StringUtils.isEmpty(accountName)) {
            accountContext = new AccountContext(clientId, clientSecret, mode, configurations);
        } else {
            accountContext = new AccountContext(accountName);
        }
        // 或者设置唯一request_id
        accountContext.getApiContext().setMaskRequestId(true);

        payment = payment.execute(accountContext.getApiContext(), paymentExecution);
        LOG.info("OrderPayPalAPI query payment:{}", payment);

        PayPalOrder paypalOrder = new PayPalOrder();
        paypalOrder.setPayment(payment);
        if ("approved".equals(payment.getState())) {
            paypalOrder.setStatus("00");
            paypalOrder.setStatus("SUCCESS");
        }

        return paypalOrder;
    }

    /**
     * 执行扣费
     *
     * @param clientId
     * @param clientSecret
     * @param mode
     * @return
     * @throws PayPalRESTException
     */
    public PayPalOrder execute(String clientId, String clientSecret, String mode) throws PayPalRESTException {
        return this.execute(null, clientId, clientSecret, mode, null);
    }

    public PayPalOrder execute(String accountName) throws PayPalRESTException {
        return this.execute(accountName, null, null, null, null);
    }
}
