# MeyPay
各种支付方式集合，包含PayPal、Stripe、GooglePay、 Airwallex(空中云汇)、 PayerMax、 EecPay、 Wintopay...

## 使用方式

- 方式1：引入Jar包（基础）
```
<dependency>
	<groupId>com.meypay.sdk</groupId>
	<artifactId>meypay</artifactId>
        <version>0.0.1</version>
</dependency>
```
- 方式2：参考MeypayApplicationTests测试类用例按业务需要接入（灵活）

- 方式3：pull主分支，重写接口独立搭建服务（推荐）


## 支付接口简述

###  [PayPal](https://github.com/yan6/meypay/tree/master/src/main/java/com/meypay/sdk/meypay/api/paypal/payment)

 支付流程
![img.png](https://analyzer.popularup.com/paypal.png)

- [ ] [订单接口](https://github.com/yan6/meypay/blob/master/src/main/java/com/meypay/sdk/meypay/api/paypal/payment/OrderPayPalAPI.java)

