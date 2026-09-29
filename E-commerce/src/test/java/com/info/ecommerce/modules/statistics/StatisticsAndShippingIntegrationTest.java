package com.info.ecommerce.modules.statistics;

import com.info.ecommerce.modules.auth.entity.Role;
import com.info.ecommerce.modules.auth.entity.User;
import com.info.ecommerce.modules.auth.repository.UserRepository;
import com.info.ecommerce.modules.auth.service.JwtService;
import com.info.ecommerce.modules.product.entity.Product;
import com.info.ecommerce.modules.product.enums.ProductSalesMode;
import com.info.ecommerce.modules.product.enums.ProductStatus;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import com.info.ecommerce.modules.system.entity.ShippingConfig;
import com.info.ecommerce.modules.system.repository.ShippingConfigRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StatisticsAndShippingIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ProductRepository productRepository;
    @Autowired private ShippingConfigRepository shippingConfigRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;

    private Product lamp;
    private String adminToken;
    private String staffToken;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        lamp = productRepository.save(Product.builder()
                .name("桌燈-" + suffix).sku("LAMP-" + suffix)
                .status(ProductStatus.ACTIVE).salesMode(ProductSalesMode.NORMAL).enabled(true)
                .basePrice(new BigDecimal("1200")).salePrice(new BigDecimal("1500")) // 特價高於定價時以定價計
                .build());
        adminToken = jwtService.generateToken(userRepository.save(User.builder()
                .username("stat-admin-" + suffix).email("stat-admin-" + suffix + "@test.com")
                .password(passwordEncoder.encode("x")).role(Role.ADMIN).enabled(true).build()));
        staffToken = jwtService.generateToken(userRepository.save(User.builder()
                .username("stat-staff-" + suffix).email("stat-staff-" + suffix + "@test.com")
                .password(passwordEncoder.encode("x")).role(Role.STAFF).enabled(true).build()));
    }

    @AfterEach
    void cleanUp() {
        shippingConfigRepository.deleteAll();
    }

    private String checkoutBody(String shippingMethod) {
        return """
                {"customerName":"陳先生","customerPhone":"0912345678","customerEmail":"stat@example.com",
                 "shippingAddress":"高雄市前金區 1 號","shippingMethod":"%s","paymentMethod":"COD",
                 "items":[{"productId":%d,"quantity":1}]}
                """.formatted(shippingMethod, lamp.getId());
    }

    @Test
    void statistics_countOnlyPaidOrders_andRequireManager() throws Exception {
        String body = mockMvc.perform(post("/api/storefront/orders/checkout").contentType(MediaType.APPLICATION_JSON)
                        .content(checkoutBody("HOME_DELIVERY")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.order.totalAmount").value(1200.0)) // 滿 1000 免運，特價無效
                .andReturn().getResponse().getContentAsString();
        long orderId = com.jayway.jsonpath.JsonPath.parse(body).read("$.data.order.id", Long.class);

        String today = LocalDate.now().toString();
        mockMvc.perform(get("/api/statistics/overall").param("startDate", today).param("endDate", today)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.topProducts[?(@.id == %d)]".formatted(lamp.getId())).isEmpty());

        mockMvc.perform(patch("/api/orders/" + orderId + "/status").param("status", "PAID")
                        .header("Authorization", "Bearer " + adminToken)).andExpect(status().isOk());
        mockMvc.perform(get("/api/statistics/overall").param("startDate", today).param("endDate", today)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.topProducts[?(@.id == %d)].sales".formatted(lamp.getId())).value(contains(1)))
                .andExpect(jsonPath("$.data.salesTrend", hasSize(1)))
                .andExpect(jsonPath("$.data.paymentMethods['貨到付款']").value(greaterThanOrEqualTo(1)));

        mockMvc.perform(get("/api/statistics/overall").header("Authorization", "Bearer " + staffToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shippingConfig_drivesFees_andDisabledMethodIsRejected() throws Exception {
        shippingConfigRepository.save(ShippingConfig.builder()
                .providerName("黑貓").enabled(true).shippingMethod("HOME_DELIVERY").baseShippingFee(new BigDecimal("80"))
                .freeShippingThreshold(new BigDecimal("2000")).sortOrder(1).testMode(false).build());
        shippingConfigRepository.save(ShippingConfig.builder()
                .providerName("門市自取").enabled(false).shippingMethod("STORE_PICKUP").baseShippingFee(BigDecimal.ZERO)
                .sortOrder(1).testMode(false).build());

        mockMvc.perform(get("/api/storefront/orders/shipping-options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].method").value("HOME_DELIVERY"))
                .andExpect(jsonPath("$.data[0].fee").value(80));

        mockMvc.perform(post("/api/storefront/orders/quote").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"shippingMethod\":\"HOME_DELIVERY\",\"items\":[{\"productId\":%d,\"quantity\":1}]}".formatted(lamp.getId())))
                .andExpect(jsonPath("$.data.shippingFee").value(80))
                .andExpect(jsonPath("$.data.totalAmount").value(1280.0));

        mockMvc.perform(post("/api/storefront/orders/quote").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"shippingMethod\":\"STORE_PICKUP\",\"items\":[{\"productId\":%d,\"quantity\":1}]}".formatted(lamp.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("暫停服務")));
    }

    @Test
    void systemStatus_isAdminOnly_andNeverReturnsSecrets() throws Exception {
        String response = mockMvc.perform(get("/api/system/status").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.checks[?(@.key == 'mail')].ok").value(contains(false)))
                .andReturn().getResponse().getContentAsString();
        org.junit.jupiter.api.Assertions.assertFalse(response.contains("5294y06JbISpM5x9"), "不可回傳綠界金鑰");
        mockMvc.perform(get("/api/system/status").header("Authorization", "Bearer " + staffToken))
                .andExpect(status().isForbidden());
    }
}
