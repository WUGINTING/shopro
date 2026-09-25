package com.info.ecommerce.modules.marketing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.info.ecommerce.modules.auth.entity.Role;
import com.info.ecommerce.modules.auth.entity.User;
import com.info.ecommerce.modules.auth.repository.UserRepository;
import com.info.ecommerce.modules.auth.service.JwtService;
import com.info.ecommerce.modules.marketing.entity.Coupon;
import com.info.ecommerce.modules.marketing.entity.Promotion;
import com.info.ecommerce.modules.marketing.repository.CouponRepository;
import com.info.ecommerce.modules.marketing.repository.PromotionRepository;
import com.info.ecommerce.modules.order.repository.OrderDiscountRepository;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import com.info.ecommerce.modules.product.entity.Product;
import com.info.ecommerce.modules.product.enums.ProductSalesMode;
import com.info.ecommerce.modules.product.enums.ProductStatus;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 結帳折扣整合測試：促銷自動套用、優惠券、免運、取優惠者、優惠券次數扣除與取消歸還
 */
@SpringBootTest
@AutoConfigureMockMvc
class CheckoutDiscountIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ProductRepository productRepository;
    @Autowired private PromotionRepository promotionRepository;
    @Autowired private CouponRepository couponRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderDiscountRepository orderDiscountRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;

    private Product vase;
    private String adminToken;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        vase = productRepository.save(Product.builder()
                .name("陶瓷花瓶").sku("VASE-" + suffix)
                .status(ProductStatus.ACTIVE).salesMode(ProductSalesMode.NORMAL).enabled(true)
                .basePrice(new BigDecimal("400"))
                .build());
        User admin = userRepository.save(User.builder()
                .username("discount-admin-" + suffix).email("discount-admin-" + suffix + "@test.com")
                .password(passwordEncoder.encode("x")).role(Role.ADMIN).enabled(true)
                .build());
        adminToken = jwtService.generateToken(admin);
    }

    @AfterEach
    void cleanUp() {
        promotionRepository.deleteAll();
        couponRepository.deleteAll();
    }

    private Promotion promotion(String type, String discountType, String value, String min) {
        return promotionRepository.save(Promotion.builder()
                .name(type + " 活動").type(type).discountType(discountType)
                .discountValue(value == null ? null : new BigDecimal(value))
                .minPurchaseAmount(min == null ? null : new BigDecimal(min))
                .startDate(LocalDate.now().minusDays(1)).endDate(LocalDate.now().plusDays(1))
                .enabled(true).priority(1)
                .build());
    }

    private Coupon coupon(String code, String type, String value, int total) {
        return couponRepository.save(Coupon.builder()
                .code(code).name(code + " 券").type(type)
                .discountValue(value == null ? null : new BigDecimal(value))
                .totalCount(total).usedCount(0)
                .validFrom(LocalDate.now().minusDays(1)).validUntil(LocalDate.now().plusDays(7))
                .enabled(true)
                .build());
    }

    private JsonNode quote(int quantity, String couponCode) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/storefront/orders/quote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"shippingMethod":"HOME_DELIVERY","couponCode":%s,
                                 "items":[{"productId":%d,"quantity":%d}]}
                                """.formatted(couponCode == null ? "null" : "\"" + couponCode + "\"", vase.getId(), quantity)))
                .andExpect(status().isOk()).andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("data");
    }

    private String checkoutBody(String couponCode) {
        return """
                {"customerName":"林小華","customerPhone":"0912345678","customerEmail":"buyer-%s@example.com",
                 "shippingAddress":"台中市西屯區台灣大道 1 號","shippingMethod":"HOME_DELIVERY","paymentMethod":"COD",
                 "couponCode":%s,"items":[{"productId":%d,"quantity":1}]}
                """.formatted(UUID.randomUUID().toString().substring(0, 6),
                couponCode == null ? "null" : "\"" + couponCode + "\"", vase.getId());
    }

    private static void assertAmount(String expected, JsonNode node) {
        assertEquals(0, new BigDecimal(expected).compareTo(node.decimalValue()), () -> "expected " + expected + " but was " + node);
    }

    @Test
    void promotion_isAppliedAutomatically_withMinimumPurchase() throws Exception {
        promotion("DISCOUNT", "PERCENTAGE", "10", "500");

        JsonNode below = quote(1, null); // 400 未達 500 門檻
        assertAmount("0", below.get("discountAmount"));
        assertAmount("500", below.get("totalAmount"));

        JsonNode above = quote(2, null); // 800 * 10% = 80，800 未達預設免運 1000，運費 100
        assertAmount("80", above.get("discountAmount"));
        assertAmount("820", above.get("totalAmount"));
        assertEquals("PROMOTION", above.get("discounts").get(0).get("type").asText());
    }

    @Test
    void bestDiscountWins_andCouponIsOnlyConsumedWhenUsed() throws Exception {
        promotion("DISCOUNT", "FIXED", "50", null);
        coupon("SAVE100", "FIXED", "100", 1);
        coupon("SAVE10", "FIXED", "10", 5);

        JsonNode better = quote(1, "save100"); // 不分大小寫
        assertAmount("100", better.get("discountAmount"));
        assertEquals("SAVE100", better.get("couponCode").asText());

        JsonNode worse = quote(1, "SAVE10");
        assertAmount("50", worse.get("discountAmount"));
        assertTrue(worse.get("couponCode").isNull());
        assertTrue(worse.get("couponMessage").asText().contains("更優惠"));

        JsonNode invalid = quote(1, "NOPE");
        assertEquals("優惠券代碼不存在", invalid.get("couponMessage").asText());
        mockMvc.perform(post("/api/storefront/orders/checkout").contentType(MediaType.APPLICATION_JSON).content(checkoutBody("NOPE")))
                .andExpect(status().isBadRequest());

        // 下單：扣一次使用次數，訂單金額與折扣紀錄正確
        MvcResult result = mockMvc.perform(post("/api/storefront/orders/checkout")
                        .contentType(MediaType.APPLICATION_JSON).content(checkoutBody("SAVE100")))
                .andExpect(status().isOk()).andReturn();
        JsonNode order = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("order");
        long orderId = order.get("id").asLong();
        assertAmount("400", order.get("totalAmount")); // 400 - 100 + 100 運費
        assertEquals(1, couponRepository.findByCode("SAVE100").orElseThrow().getUsedCount());
        assertTrue(orderDiscountRepository.findByOrderId(orderId).stream().anyMatch(d -> "SAVE100".equals(d.getDiscountCode())));

        // 次數用完：試算提示、下單失敗
        assertTrue(quote(1, "SAVE100").get("couponMessage").asText().contains("使用完畢"));
        mockMvc.perform(post("/api/storefront/orders/checkout").contentType(MediaType.APPLICATION_JSON).content(checkoutBody("SAVE100")))
                .andExpect(status().isBadRequest());

        // 取消訂單歸還次數
        mockMvc.perform(patch("/api/orders/" + orderId + "/status").param("status", "CANCELLED")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
        assertEquals(0, couponRepository.findByCode("SAVE100").orElseThrow().getUsedCount());
    }

    @Test
    void freeShipping_fromPromotionOrCoupon_combinesWithDiscount() throws Exception {
        coupon("SHIPFREE", "FREE_SHIPPING", null, 10);
        JsonNode withCoupon = quote(1, "SHIPFREE");
        assertAmount("0", withCoupon.get("shippingFee"));
        assertAmount("400", withCoupon.get("totalAmount"));
        assertEquals("SHIPFREE", withCoupon.get("couponCode").asText());

        promotion("FREE_SHIPPING", null, null, "300");
        promotion("DISCOUNT", "PERCENTAGE", "5", null);
        JsonNode promo = quote(1, "SHIPFREE");
        assertAmount("0", promo.get("shippingFee"));
        assertAmount("20", promo.get("discountAmount"));
        assertAmount("380", promo.get("totalAmount"));
        assertTrue(promo.get("couponCode").isNull(), "已免運時不消耗免運券");
    }

    @Test
    void couponAdminApi_validatesInput() throws Exception {
        mockMvc.perform(post("/api/marketing/coupons").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"WELCOME","name":"新客","type":"PERCENTAGE","discountValue":150,"totalCount":10,
                                 "validFrom":"2026-01-01","validUntil":"2026-12-31","enabled":true}
                                """))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/marketing/coupons").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"welcome","name":"新客","type":"PERCENTAGE","discountValue":10,"totalCount":10,
                                 "validFrom":"2026-01-01","validUntil":"2026-12-31","enabled":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("WELCOME"));
        // 顧客不可管理優惠券
        mockMvc.perform(get("/api/marketing/coupons")).andExpect(status().isUnauthorized());

        // 前台只看得到公開且可用的優惠券
        Coupon secret = coupon("VIPONLY", "FIXED", "200", 5);
        Coupon open = coupon("OPEN50", "FIXED", "50", 5);
        open.setPublicVisible(true);
        couponRepository.save(open);
        mockMvc.perform(get("/api/storefront/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].code").value("OPEN50"))
                .andExpect(jsonPath("$.data[0].usedCount").isEmpty());
        assertNotNull(secret.getId());
    }
}
