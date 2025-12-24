package com.info.ecommerce.modules.product.enums;

/**
 * 商品狀態
 */
public enum ProductStatus {
    DRAFT("草稿"),
    ACTIVE("上架"),
    INACTIVE("下架"),
    OUT_OF_STOCK("缺貨");

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
