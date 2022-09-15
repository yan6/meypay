package com.meypay.sdk.meypay.service.base;

import com.meypay.sdk.meypay.model.BaseOrder;

public interface Order {
    // 创建
    <T extends BaseOrder> T create();

    // 查询
    <T extends BaseOrder> T query();
}
