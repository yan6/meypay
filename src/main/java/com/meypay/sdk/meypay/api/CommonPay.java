package com.meypay.sdk.meypay.api;

import org.modelmapper.ModelMapper;

public class CommonPay {
    private static final ModelMapper MODEL_MAPPER = new ModelMapper();

    protected static <B> B mapObject(Object from, Class<B> targetClass) {
        if (from == null) {
            return null;
        }
        return MODEL_MAPPER.map(from, targetClass);
    }
}
