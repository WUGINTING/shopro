package com.info.ecommerce.modules.product;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.info.ecommerce.modules.product.entity.Product;
import com.info.ecommerce.modules.product.entity.ProductCategory;
import com.info.ecommerce.modules.product.enums.ProductSalesMode;
import com.info.ecommerce.modules.product.enums.ProductStatus;
import com.info.ecommerce.modules.product.repository.ProductCategoryRepository;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 前台商品查詢：未上架商品不可見、分類含子分類、後端分頁排序
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StorefrontProductIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductCategoryRepository categoryRepository;

    private String tag;
    private ProductCategory parent;
    private Product draft;

    @BeforeEach
    void setUp() {
        tag = "T" + UUID.randomUUID().toString().substring(0, 6);
        parent = categoryRepository.save(ProductCategory.builder().name(tag + "-日系").enabled(true).build());
        ProductCategory child = categoryRepository.save(ProductCategory.builder()
                .name(tag + "-文具").parentId(parent.getId()).enabled(true).build());

        save(tag + " 茶杯", parent.getId(), ProductStatus.ACTIVE, "350", null);
        save(tag + " 筆記本", child.getId(), ProductStatus.ACTIVE, "500", "120");   // 特價 120
        save(tag + " 缺貨毛巾", parent.getId(), ProductStatus.OUT_OF_STOCK, "200", null);
        draft = save(tag + " 草稿商品", parent.getId(), ProductStatus.DRAFT, "100", null);
    }

    private Product save(String name, Long categoryId, ProductStatus status, String basePrice, String salePrice) {
        return productRepository.save(Product.builder()
                .name(name).sku("SKU-" + UUID.randomUUID().toString().substring(0, 8))
                .categoryId(categoryId).status(status).salesMode(ProductSalesMode.NORMAL).enabled(true)
                .basePrice(new BigDecimal(basePrice))
                .salePrice(salePrice == null ? null : new BigDecimal(salePrice))
                .build());
    }

    private List<String> names(String url) throws Exception {
        String body = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode content = objectMapper.readTree(body).get("data").get("content");
        List<String> names = new ArrayList<>();
        content.forEach(node -> names.add(node.get("name").asText()));
        return names;
    }

    @Test
    void categoryListing_includesChildren_hidesDrafts_sortsByEffectivePrice() throws Exception {
        List<String> byPrice = names("/api/storefront/products?categoryId=" + parent.getId() + "&sort=price_asc");

        assertEquals(List.of(tag + " 筆記本", tag + " 缺貨毛巾", tag + " 茶杯"), byPrice);
        assertFalse(byPrice.contains(tag + " 草稿商品"));
    }

    @Test
    void keywordSearch_andPaging() throws Exception {
        // MockMvc 的 URL 字串會再編碼一次，中文關鍵字改用 param 傳遞
        String body = mockMvc.perform(get("/api/storefront/products").param("keyword", tag + " 茶"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        List<String> found = new ArrayList<>();
        objectMapper.readTree(body).get("data").get("content").forEach(node -> found.add(node.get("name").asText()));
        assertEquals(List.of(tag + " 茶杯"), found);

        body = mockMvc.perform(get("/api/storefront/products?keyword=" + tag + "&size=2&page=1"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode data = objectMapper.readTree(body).get("data");
        assertEquals(3, data.get("totalElements").asInt());
        assertEquals(2, data.get("totalPages").asInt());
        assertEquals(1, data.get("content").size());
    }

    @Test
    void draftProduct_notVisibleToAnonymousCallers() throws Exception {
        mockMvc.perform(get("/api/storefront/products/" + draft.getId())).andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/products/" + draft.getId())).andExpect(status().isBadRequest());
        assertFalse(names("/api/products/category/" + parent.getId()).contains(tag + " 草稿商品"));
        mockMvc.perform(get("/api/products/status/DRAFT")).andExpect(status().isOk());
        assertTrue(names("/api/products/status/DRAFT").isEmpty());
    }

    @Test
    void publicProduct_hidesCostPrice() throws Exception {
        Product withCost = productRepository.save(Product.builder()
                .name(tag + " 成本測試").sku("SKU-" + UUID.randomUUID().toString().substring(0, 8))
                .status(ProductStatus.ACTIVE).salesMode(ProductSalesMode.NORMAL).enabled(true)
                .basePrice(new BigDecimal("300")).costPrice(new BigDecimal("80"))
                .build());

        String body = mockMvc.perform(get("/api/storefront/products/" + withCost.getId()))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertTrue(objectMapper.readTree(body).get("data").get("costPrice").isNull());

        body = mockMvc.perform(get("/api/products/" + withCost.getId()))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertTrue(objectMapper.readTree(body).get("data").get("costPrice").isNull());
    }
}
