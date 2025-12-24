package com.info.ecommerce.modules.product.enums;

/**
 * 商品銷售模式
 */
public enum ProductSalesMode {
    NORMAL("一般商品"),
    PRE_ORDER("預購商品"),
    VOUCHER("票券商品"),
    SUBSCRIPTION("訂閱購"),
    STORE_ONLY("門市限定商品");

    private final String description;

    ProductSalesMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
