package com.info.ecommerce.modules.product;

import com.info.ecommerce.modules.auth.entity.Role;
import com.info.ecommerce.modules.auth.entity.User;
import com.info.ecommerce.modules.auth.repository.UserRepository;
import com.info.ecommerce.modules.auth.service.JwtService;
import com.info.ecommerce.modules.crm.repository.MemberRepository;
import com.info.ecommerce.modules.product.entity.Product;
import com.info.ecommerce.modules.product.entity.ProductSpecification;
import com.info.ecommerce.modules.product.enums.ProductSalesMode;
import com.info.ecommerce.modules.product.enums.ProductStatus;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import com.info.ecommerce.modules.product.repository.ProductSpecificationRepository;
import com.info.ecommerce.modules.product.repository.StockNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryAndEdmIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductSpecificationRepository specificationRepository;
    @Autowired private StockNotificationRepository notificationRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;

    private Product tea;
    private ProductSpecification green;
    private String adminToken;
    private String suffix;

    @BeforeEach
    void setUp() {
        suffix = UUID.randomUUID().toString().substring(0, 8);
        tea = productRepository.save(Product.builder()
                .name("烏龍茶-" + suffix).sku("TEA-" + suffix)
                .status(ProductStatus.ACTIVE).salesMode(ProductSalesMode.NORMAL).enabled(true)
                .basePrice(new BigDecimal("300")).build());
        green = specificationRepository.save(ProductSpecification.builder()
                .productId(tea.getId()).specName("綠茶").sku("TEA-G-" + suffix).price(new BigDecimal("300")).stock(0).enabled(true).build());
        adminToken = jwtService.generateToken(userRepository.save(User.builder()
                .username("inv-admin-" + suffix).email("inv-admin-" + suffix + "@test.com")
                .password(passwordEncoder.encode("x")).role(Role.ADMIN).enabled(true).build()));
    }

    @Test
    void soldOutSpec_customerSubscribes_adminRestocksTheSpec() throws Exception {
        mockMvc.perform(get("/api/storefront/products/" + tea.getId()))
                .andExpect(jsonPath("$.data.stock").value(0));

        String body = "{\"email\":\"fan@example.com\",\"specificationId\":%d}".formatted(green.getId());
        mockMvc.perform(post("/api/storefront/products/" + tea.getId() + "/restock-notification")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/storefront/products/" + tea.getId() + "/restock-notification")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
        assertEquals(1, notificationRepository.findByProductIdAndNotifiedFalse(tea.getId()).size(), "重複登記只保留一筆");
        mockMvc.perform(post("/api/storefront/products/" + tea.getId() + "/restock-notification")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"nope\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/storefront/products/" + tea.getId() + "/restock-notification")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"fan@example.com\",\"specificationId\":\"abc\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/storefront/products/" + tea.getId() + "/restock-notification")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"fan@example.com\",\"specificationId\":999999}"))
                .andExpect(status().isBadRequest());
        // 庫存不可扣成負數
        mockMvc.perform(put("/api/inventory/update").header("Authorization", "Bearer " + adminToken)
                        .param("productId", tea.getId().toString()).param("specificationId", green.getId().toString())
                        .param("warehouseId", "1").param("quantity", "-5"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/inventory/update").header("Authorization", "Bearer " + adminToken)
                        .param("productId", tea.getId().toString()).param("specificationId", green.getId().toString())
                        .param("warehouseId", "1").param("quantity", "12"))
                .andExpect(status().isOk());
        assertEquals(12, specificationRepository.findById(green.getId()).orElseThrow().getStock());
        mockMvc.perform(get("/api/storefront/products/" + tea.getId()))
                .andExpect(jsonPath("$.data.stock").value(12));
    }

    @Test
    void untrackedProduct_reportsNullStock() throws Exception {
        Product untracked = productRepository.save(Product.builder()
                .name("手工皂-" + suffix).sku("SOAP-" + suffix)
                .status(ProductStatus.ACTIVE).salesMode(ProductSalesMode.NORMAL).enabled(true)
                .basePrice(new BigDecimal("150")).build());
        mockMvc.perform(get("/api/storefront/products/" + untracked.getId()))
                .andExpect(jsonPath("$.data.stock").doesNotExist());
    }

    @Test
    void checkoutOptIn_andUnsubscribeLink() throws Exception {
        String email = "news-" + suffix + "@example.com";
        green.setStock(5);
        specificationRepository.save(green);
        mockMvc.perform(post("/api/storefront/orders/checkout").contentType(MediaType.APPLICATION_JSON).content("""
                        {"customerName":"周小姐","customerPhone":"0912345678","customerEmail":"%s",
                         "shippingAddress":"台中市 1 號","shippingMethod":"HOME_DELIVERY","paymentMethod":"COD",
                         "marketingOptIn":true,"items":[{"productId":%d,"specificationId":%d,"quantity":1}]}
                        """.formatted(email, tea.getId(), green.getId())))
                .andExpect(status().isOk());
        var member = memberRepository.findByEmail(email).orElseThrow();
        assertTrue(member.getMarketingOptIn());

        String token = jwtService.generatePurposeToken("edm-unsubscribe", String.valueOf(member.getId()), Map.of(), 60_000);
        mockMvc.perform(post("/api/storefront/unsubscribe").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + token + "\"}"))
                .andExpect(status().isOk());
        assertFalse(memberRepository.findByEmail(email).orElseThrow().getMarketingOptIn());

        mockMvc.perform(post("/api/storefront/unsubscribe").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"bad\"}"))
                .andExpect(status().isBadRequest());
        // 退訂 token 不可當登入 token
        mockMvc.perform(get("/api/auth/profile").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }
}
