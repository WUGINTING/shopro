package com.info.ecommerce.modules.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.info.ecommerce.modules.auth.entity.Role;
import com.info.ecommerce.modules.auth.entity.User;
import com.info.ecommerce.modules.auth.repository.UserRepository;
import com.info.ecommerce.modules.auth.service.JwtService;
import com.info.ecommerce.modules.crm.repository.MemberRepository;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import com.info.ecommerce.modules.product.entity.Product;
import com.info.ecommerce.modules.product.entity.ProductInventory;
import com.info.ecommerce.modules.product.entity.ProductSpecification;
import com.info.ecommerce.modules.product.enums.ProductSalesMode;
import com.info.ecommerce.modules.product.enums.ProductStatus;
import com.info.ecommerce.modules.product.repository.InventoryMovementLogRepository;
import com.info.ecommerce.modules.product.repository.ProductInventoryRepository;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import com.info.ecommerce.modules.product.repository.ProductSpecificationRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 前台結帳整合測試（H2）：扣庫存、庫存不足整筆回滾、取消歸還庫存、訂單查詢
 * 不使用 @Transactional，讓每個 API 呼叫各自提交，才能驗證回滾行為。
 */
@SpringBootTest
@AutoConfigureMockMvc
class StorefrontCheckoutIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductSpecificationRepository specificationRepository;
    @Autowired private ProductInventoryRepository inventoryRepository;
    @Autowired private InventoryMovementLogRepository movementLogRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private com.info.ecommerce.modules.payment.repository.PaymentSettingRepository paymentSettingRepository;
    @Autowired private com.info.ecommerce.modules.product.repository.ProductCategoryRepository categoryRepository;
    @Autowired private com.info.ecommerce.modules.crm.repository.MemberLevelRepository memberLevelRepository;
    @Autowired private com.info.ecommerce.modules.crm.service.MemberService memberService;

    private Product cup;
    private ProductSpecification blueCup;
    private Product cone;
    private String adminToken;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        cup = productRepository.save(Product.builder()
                .name("日式茶杯").sku("CUP-" + suffix)
                .status(ProductStatus.ACTIVE).salesMode(ProductSalesMode.NORMAL).enabled(true)
                .basePrice(new BigDecimal("350"))
                .build());
        blueCup = specificationRepository.save(ProductSpecification.builder()
                .productId(cup.getId()).specName("藍色").sku("CUP-BL-" + suffix)
                .price(new BigDecimal("380")).stock(3).enabled(true)
                .build());
        cone = productRepository.save(Product.builder()
                .name("香草甜筒").sku("CONE-" + suffix)
                .status(ProductStatus.ACTIVE).salesMode(ProductSalesMode.NORMAL).enabled(true)
                .basePrice(new BigDecimal("120")).salePrice(new BigDecimal("99"))
                .build());
        inventoryRepository.save(ProductInventory.builder()
                .productId(cone.getId()).warehouseId(1L).availableStock(5).lockedStock(0).safetyStock(1)
                .build());

        User admin = userRepository.save(User.builder()
                .username("stock-admin-" + suffix).email("stock-admin-" + suffix + "@test.com")
                .password(passwordEncoder.encode("x")).role(Role.ADMIN).enabled(true)
                .build());
        adminToken = jwtService.generateToken(admin);
    }

    private String checkoutBody(String email, long productId, Long specId, int quantity) {
        return """
                {"customerName":"王小明","customerPhone":"0912345678","customerEmail":"%s",
                 "shippingAddress":"台北市信義區市府路 1 號","shippingMethod":"HOME_DELIVERY","paymentMethod":"COD",
                 "items":[{"productId":%d,"specificationId":%s,"quantity":%d}]}
                """.formatted(email, productId, specId == null ? "null" : specId.toString(), quantity);
    }

    private JsonNode checkout(String body) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/storefront/orders/checkout")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("order");
    }

    private int specStock() {
        return specificationRepository.findById(blueCup.getId()).orElseThrow().getStock();
    }

    private int coneStock() {
        return inventoryRepository.findByProductIdAndSpecificationId(cone.getId(), null).orElseThrow().getAvailableStock();
    }

    @Test
    void checkout_deductsStock_rejectsOversell_andCancelRestores() throws Exception {
        JsonNode order = checkout(checkoutBody("buyer@example.com", cup.getId(), blueCup.getId(), 2));
        assertEquals(0, new BigDecimal("860").compareTo(order.get("totalAmount").decimalValue())); // 760 + 100 運費
        assertEquals(1, specStock());
        assertFalse(movementLogRepository.findTop100ByProductIdOrderByCreatedAtDesc(cup.getId()).isEmpty());

        long ordersBefore = orderRepository.count();
        mockMvc.perform(post("/api/storefront/orders/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(checkoutBody("other@example.com", cup.getId(), blueCup.getId(), 2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("庫存不足")));
        assertEquals(1, specStock(), "失敗的結帳不可扣庫存");
        assertEquals(ordersBefore, orderRepository.count(), "失敗的結帳不可留下訂單");

        long orderId = order.get("id").asLong();
        mockMvc.perform(patch("/api/orders/" + orderId + "/status").param("status", "CANCELLED")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
        assertEquals(3, specStock());

        // 重複取消不可重複歸還
        mockMvc.perform(patch("/api/orders/" + orderId + "/status").param("status", "CANCELLED")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
        assertEquals(3, specStock());
    }

    @Test
    void checkout_productLevelInventory_deductedAndLookupWorks() throws Exception {
        JsonNode order = checkout(checkoutBody("Cone.Buyer@example.com", cone.getId(), null, 4));
        assertEquals(1, coneStock());

        mockMvc.perform(post("/api/storefront/orders/quote").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[{\"productId\":" + cone.getId() + ",\"quantity\":2}]}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/storefront/orders/lookup")
                        .param("orderNumber", order.get("orderNumber").asText())
                        .param("email", "cone.buyer@EXAMPLE.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.order.items[0].quantity").value(4))
                .andExpect(jsonPath("$.data.paymentMethod").value("COD"))
                .andExpect(jsonPath("$.data.canPayOnline").value(false));

        mockMvc.perform(get("/api/storefront/orders/lookup")
                        .param("orderNumber", order.get("orderNumber").asText())
                        .param("email", "attacker@example.com"))
                .andExpect(status().isBadRequest());
    }

    private String orderUpdateBody(JsonNode order, int quantity) throws Exception {
        com.fasterxml.jackson.databind.node.ObjectNode body = order.deepCopy();
        com.fasterxml.jackson.databind.node.ArrayNode items = objectMapper.createArrayNode();
        items.addObject()
                .put("productId", cup.getId())
                .put("specificationId", blueCup.getId())
                .put("unitPrice", 380)
                .put("quantity", quantity);
        body.set("items", items);
        return objectMapper.writeValueAsString(body);
    }

    @Test
    void adminEditingItems_adjustsReservedStock() throws Exception {
        JsonNode order = checkout(checkoutBody("edit@example.com", cup.getId(), blueCup.getId(), 2));
        assertEquals(1, specStock());
        long orderId = order.get("id").asLong();

        mockMvc.perform(put("/api/orders/" + orderId).header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content(orderUpdateBody(order, 1)))
                .andExpect(status().isOk());
        assertEquals(2, specStock(), "改為 1 件後應歸還 1 件");

        mockMvc.perform(put("/api/orders/" + orderId).header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content(orderUpdateBody(order, 5)))
                .andExpect(status().isBadRequest());
        assertEquals(2, specStock(), "庫存不足的修改需整筆回滾");

        mockMvc.perform(patch("/api/orders/" + orderId + "/status").param("status", "CANCELLED")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
        assertEquals(3, specStock(), "取消後依目前品項（1 件）歸還");
    }

    @Test
    void restoringCancelledOrder_reservesStockAgain_orFailsWhenSoldOut() throws Exception {
        JsonNode order = checkout(checkoutBody("restore@example.com", cup.getId(), blueCup.getId(), 2));
        long orderId = order.get("id").asLong();
        mockMvc.perform(patch("/api/orders/" + orderId + "/status").param("status", "CANCELLED")
                        .header("Authorization", "Bearer " + adminToken)).andExpect(status().isOk());
        assertEquals(3, specStock());

        mockMvc.perform(patch("/api/orders/" + orderId + "/status").param("status", "PENDING_PAYMENT")
                        .header("Authorization", "Bearer " + adminToken)).andExpect(status().isOk());
        assertEquals(1, specStock(), "恢復訂單需重新扣庫存");

        // 再取消後，他人買光庫存，就無法恢復
        mockMvc.perform(patch("/api/orders/" + orderId + "/status").param("status", "CANCELLED")
                        .header("Authorization", "Bearer " + adminToken)).andExpect(status().isOk());
        checkout(checkoutBody("other@example.com", cup.getId(), blueCup.getId(), 3));
        assertEquals(0, specStock());
        mockMvc.perform(patch("/api/orders/" + orderId + "/status").param("status", "PENDING_PAYMENT")
                        .header("Authorization", "Bearer " + adminToken)).andExpect(status().isBadRequest());
        assertEquals(0, specStock());
    }

    private void changeStatus(long orderId, String status) throws Exception {
        mockMvc.perform(patch("/api/orders/" + orderId + "/status").param("status", status)
                        .header("Authorization", "Bearer " + adminToken)).andExpect(status().isOk());
    }

    private BigDecimal totalSpent(String email) {
        return memberRepository.findByEmail(email).orElseThrow().getTotalSpent();
    }

    @Test
    void memberTotalSpent_countedOnce_andReversedOnRefund() throws Exception {
        String email = "spender-" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        long orderId = checkout(checkoutBody(email, cup.getId(), blueCup.getId(), 1)).get("id").asLong();
        BigDecimal orderTotal = new BigDecimal("480"); // 380 + 100 運費

        changeStatus(orderId, "PAID");
        assertEquals(0, orderTotal.compareTo(totalSpent(email)));
        changeStatus(orderId, "PROCESSING");
        changeStatus(orderId, "COMPLETED");
        assertEquals(0, orderTotal.compareTo(totalSpent(email)), "出貨再完成不可重複累計");
        changeStatus(orderId, "REFUNDED");
        assertEquals(0, BigDecimal.ZERO.compareTo(totalSpent(email)), "退款後應扣回");
    }

    @Test
    void editingSpecWithoutStock_keepsLiveStock_andExplicitStockIsLogged() throws Exception {
        checkout(checkoutBody("spec-edit@example.com", cup.getId(), blueCup.getId(), 2)); // 3 -> 1

        // 後台以開啟頁面時的資料改名稱，未帶庫存：不可把賣掉的 2 件加回去
        mockMvc.perform(put("/api/product-specifications/" + blueCup.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":%d,\"specName\":\"深藍色\",\"price\":380,\"enabled\":true}".formatted(cup.getId())))
                .andExpect(status().isOk());
        assertEquals(1, specStock());
        assertEquals("深藍色", specificationRepository.findById(blueCup.getId()).orElseThrow().getSpecName());

        // 明確設定庫存才會變動，並留下異動紀錄
        mockMvc.perform(put("/api/product-specifications/" + blueCup.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":%d,\"specName\":\"深藍色\",\"price\":380,\"stock\":10,\"enabled\":true}".formatted(cup.getId())))
                .andExpect(status().isOk());
        assertEquals(10, specStock());
        assertTrue(movementLogRepository.findTop100ByProductIdOrderByCreatedAtDesc(cup.getId()).stream()
                .anyMatch(log -> "SPEC_EDIT".equals(log.getSource()) && log.getAfterStock() == 10 && log.getBeforeStock() == 1));
    }

    @Test
    void orderDiscount_changesTotal_onlyWhilePendingPayment() throws Exception {
        long orderId = checkout(checkoutBody("discount@example.com", cup.getId(), blueCup.getId(), 1)).get("id").asLong(); // 380 + 100

        MvcResult added = mockMvc.perform(post("/api/orders/discounts")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":%d,\"discountType\":\"MANUAL\",\"discountAmount\":50}".formatted(orderId)))
                .andExpect(status().isOk()).andReturn();
        long discountId = objectMapper.readTree(added.getResponse().getContentAsString()).get("data").get("id").asLong();
        var order = orderRepository.findById(orderId).orElseThrow();
        assertEquals(0, new BigDecimal("50").compareTo(order.getDiscountAmount()));
        assertEquals(0, new BigDecimal("430").compareTo(order.getTotalAmount()));

        // 百分比折扣：依商品小計 380 * 10% = 38
        mockMvc.perform(post("/api/orders/discounts")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":%d,\"discountType\":\"PERCENT\",\"discountAmount\":0,\"discountPercentage\":10}".formatted(orderId)))
                .andExpect(status().isOk());
        assertEquals(0, new BigDecimal("392").compareTo(orderRepository.findById(orderId).orElseThrow().getTotalAmount()));

        mockMvc.perform(delete("/api/orders/discounts/" + discountId).header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
        assertEquals(0, new BigDecimal("442").compareTo(orderRepository.findById(orderId).orElseThrow().getTotalAmount()));

        changeStatus(orderId, "PAID");
        mockMvc.perform(post("/api/orders/discounts")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":%d,\"discountType\":\"MANUAL\",\"discountAmount\":10}".formatted(orderId)))
                .andExpect(status().isBadRequest());
        assertEquals(0, new BigDecimal("442").compareTo(orderRepository.findById(orderId).orElseThrow().getTotalAmount()));
    }

    private String ecpayCheckoutBody(String email) {
        return checkoutBody(email, cone.getId(), null, 1).replace("\"paymentMethod\":\"COD\"", "\"paymentMethod\":\"ECPAY\"");
    }

    private MvcResult ship(long orderId, String status) throws Exception {
        return mockMvc.perform(post("/api/orders/shipments").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":%d,\"shippingCompany\":\"黑貓宅急便\",\"trackingNumber\":\"TW123\",\"shippingStatus\":\"%s\"}".formatted(orderId, status)))
                .andReturn();
    }

    @Test
    void statusRules_paidOrderCannotBeCancelledDirectly() throws Exception {
        long orderId = checkout(checkoutBody("rules@example.com", cone.getId(), null, 1)).get("id").asLong();
        changeStatus(orderId, "PAID");
        mockMvc.perform(patch("/api/orders/" + orderId + "/status").param("status", "CANCELLED")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("退款")));
    }

    @Test
    void codOrder_canShipWhileUnpaid_deliveredCompletes_andCustomerSeesTracking() throws Exception {
        String email = "cod-ship@example.com";
        JsonNode order = checkout(checkoutBody(email, cone.getId(), null, 1));
        long orderId = order.get("id").asLong();
        String orderNumber = order.get("orderNumber").asText();

        MvcResult created = ship(orderId, "SHIPPED");
        assertEquals(200, created.getResponse().getStatus(), created.getResponse().getContentAsString());
        assertEquals("PROCESSING", orderRepository.findById(orderId).orElseThrow().getStatus().name());

        mockMvc.perform(get("/api/storefront/orders/lookup").param("orderNumber", orderNumber).param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.shipments[0].trackingNumber").value("TW123"))
                .andExpect(jsonPath("$.data.shipments[0].statusLabel").value("已出貨"))
                .andExpect(jsonPath("$.data.canCancel").value(false));

        long shipmentId = objectMapper.readTree(created.getResponse().getContentAsString()).get("data").get("id").asLong();
        mockMvc.perform(patch("/api/orders/shipments/" + shipmentId + "/status").param("status", "DELIVERED")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
        assertEquals("COMPLETED", orderRepository.findById(orderId).orElseThrow().getStatus().name());
    }

    @Test
    void unpaidOnlineOrder_cannotShip_butCustomerCanCancelIt() throws Exception {
        String email = "cancel-me@example.com";
        JsonNode order = checkout(ecpayCheckoutBody(email));
        long orderId = order.get("id").asLong();
        String orderNumber = order.get("orderNumber").asText();
        assertEquals(4, coneStock());

        assertEquals(400, ship(orderId, "SHIPPED").getResponse().getStatus());

        mockMvc.perform(get("/api/storefront/orders/lookup").param("orderNumber", orderNumber).param("email", email))
                .andExpect(jsonPath("$.data.canCancel").value(true));
        // Email 不符不可取消
        mockMvc.perform(post("/api/storefront/orders/cancel").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderNumber\":\"%s\",\"email\":\"someone@example.com\"}".formatted(orderNumber)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/storefront/orders/cancel").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderNumber\":\"%s\",\"email\":\"%s\"}".formatted(orderNumber, email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.order.status").value("CANCELLED"));
        assertEquals(5, coneStock());
        mockMvc.perform(post("/api/storefront/orders/cancel").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderNumber\":\"%s\",\"email\":\"%s\"}".formatted(orderNumber, email)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refund_partialThenFull_marksRefunded_withoutAutomaticRestock() throws Exception {
        long orderId = checkout(checkoutBody("refund@example.com", cone.getId(), null, 2)).get("id").asLong(); // 99*2 + 100
        assertEquals(3, coneStock());

        // 未付款不可退款
        mockMvc.perform(post("/api/orders/" + orderId + "/refund").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"test\"}"))
                .andExpect(status().isBadRequest());

        changeStatus(orderId, "PAID");
        mockMvc.perform(post("/api/orders/" + orderId + "/refund").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"amount\":100,\"reason\":\"運費退還\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"));
        // 超過可退金額
        mockMvc.perform(post("/api/orders/" + orderId + "/refund").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"amount\":999,\"reason\":\"x\"}"))
                .andExpect(status().isBadRequest());
        // 已有部分退款：剩餘金額退款不能自動歸還整筆庫存（退回的商品需手動補貨）
        mockMvc.perform(post("/api/orders/" + orderId + "/refund").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"顧客取消\",\"restock\":true}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/orders/" + orderId + "/refund").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"顧客取消\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("REFUNDED"));
        assertEquals(3, coneStock());
        mockMvc.perform(patch("/api/orders/" + orderId + "/status").param("status", "PAID")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void fullRefund_withRestock_returnsStock() throws Exception {
        long orderId = checkout(checkoutBody("full-refund@example.com", cone.getId(), null, 2)).get("id").asLong();
        assertEquals(3, coneStock());
        changeStatus(orderId, "PAID");
        mockMvc.perform(post("/api/orders/" + orderId + "/refund").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"顧客取消\",\"restock\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("REFUNDED"));
        assertEquals(5, coneStock());
    }

    @Test
    void paidOrderInProcessing_cannotBeCancelled_butUnpaidCodCan() throws Exception {
        long paidId = checkout(checkoutBody("paid-ship@example.com", cone.getId(), null, 1)).get("id").asLong();
        changeStatus(paidId, "PAID");
        changeStatus(paidId, "PROCESSING");
        mockMvc.perform(patch("/api/orders/" + paidId + "/status").param("status", "CANCELLED")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("退款")));

        long codId = checkout(checkoutBody("cod-refused@example.com", cone.getId(), null, 1)).get("id").asLong();
        changeStatus(codId, "PROCESSING");
        // 未收款的貨到付款不能退款，但拒收時可以取消
        mockMvc.perform(post("/api/orders/" + codId + "/refund").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"拒收\"}"))
                .andExpect(status().isBadRequest());
        changeStatus(codId, "CANCELLED");
    }

    @Test
    void partialRefund_cannotRestockWholeOrder() throws Exception {
        long orderId = checkout(checkoutBody("partial@example.com", cone.getId(), null, 2)).get("id").asLong();
        changeStatus(orderId, "PAID");
        mockMvc.perform(post("/api/orders/" + orderId + "/refund").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"amount\":50,\"reason\":\"補償\",\"restock\":true}"))
                .andExpect(status().isBadRequest());
        assertEquals(3, coneStock());

        // 先部分退款，剩餘金額的「全額」退款也不能自動歸還整筆庫存（避免與手動補貨重複入庫）
        mockMvc.perform(post("/api/orders/" + orderId + "/refund").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"amount\":50,\"reason\":\"退回一件\",\"restock\":false}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/orders/" + orderId + "/refund").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"其餘退款\",\"restock\":true}"))
                .andExpect(status().isBadRequest());
        assertEquals(3, coneStock());
    }

    @Test
    void orderLookup_isRateLimitedPerClient() throws Exception {
        for (int i = 0; i < 20; i++) {
            mockMvc.perform(get("/api/storefront/orders/lookup").with(request -> { request.setRemoteAddr("10.77.0.1"); return request; })
                            .param("orderNumber", "ORD-NOPE-" + i).param("email", "guess@example.com"))
                    .andExpect(status().isBadRequest());
        }
        mockMvc.perform(get("/api/storefront/orders/lookup").with(request -> { request.setRemoteAddr("10.77.0.1"); return request; })
                        .param("orderNumber", "ORD-NOPE-X").param("email", "guess@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("次數過多")));
        // 其他來源不受影響
        mockMvc.perform(get("/api/storefront/orders/lookup").with(request -> { request.setRemoteAddr("10.77.0.2"); return request; })
                        .param("orderNumber", "ORD-NOPE-X").param("email", "guess@example.com"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("次數過多"))));
    }

    @Test
    void specWithZeroPrice_isSoldAtTheProductPrice() throws Exception {
        ProductSpecification sameAsProduct = specificationRepository.save(ProductSpecification.builder()
                .productId(cup.getId()).specName("白色").sku("CUP-WH-" + UUID.randomUUID().toString().substring(0, 8))
                .price(BigDecimal.ZERO).stock(3).enabled(true).build());
        mockMvc.perform(post("/api/storefront/orders/quote").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[{\"productId\":" + cup.getId() + ",\"specificationId\":" + sameAsProduct.getId() + ",\"quantity\":1}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.subtotalAmount").value(350));
    }

    @Test
    void onlinePayment_isRefused_whileEcPayIsInMaintenance() throws Exception {
        var setting = paymentSettingRepository.findByGateway(com.info.ecommerce.modules.payment.enums.PaymentGateway.ECPAY).orElseThrow();
        Boolean previous = setting.getMaintenanceMode();
        setting.setMaintenanceMode(true);
        setting.setMaintenanceMessage("綠界維護中，預計 30 分鐘後恢復");
        paymentSettingRepository.save(setting);
        try {
            mockMvc.perform(get("/api/storefront/orders/payment-options"))
                    .andExpect(jsonPath("$.data[0].method").value("ECPAY"))
                    .andExpect(jsonPath("$.data[0].available").value(false))
                    .andExpect(jsonPath("$.data[0].message").value("綠界維護中，預計 30 分鐘後恢復"));
            mockMvc.perform(post("/api/storefront/orders/checkout").contentType(MediaType.APPLICATION_JSON)
                            .content(checkoutBody("maint@example.com", cone.getId(), null, 1).replace("\"COD\"", "\"ECPAY\"")))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("綠界維護中，預計 30 分鐘後恢復"));
            // 貨到付款不受影響
            checkout(checkoutBody("maint-cod@example.com", cone.getId(), null, 1));
        } finally {
            setting.setMaintenanceMode(previous);
            setting.setMaintenanceMessage(null);
            paymentSettingRepository.save(setting);
        }
    }

    @Test
    void categoryWithProducts_cannotBeDeleted() throws Exception {
        var category = categoryRepository.save(com.info.ecommerce.modules.product.entity.ProductCategory.builder()
                .name("茶具-" + UUID.randomUUID().toString().substring(0, 6)).build());
        cup.setCategoryId(category.getId());
        productRepository.save(cup);
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/product-categories/" + category.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("商品")));
        cup.setCategoryId(null);
        productRepository.save(cup);
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/product-categories/" + category.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void memberLevel_upgradesAutomatically_whenSpendingReachesTheThreshold() {
        String suffix = UUID.randomUUID().toString().substring(0, 6);
        var level = memberLevelRepository.save(com.info.ecommerce.modules.crm.entity.MemberLevel.builder()
                .name("鑽石-" + suffix).levelOrder(99).minSpendAmount(new BigDecimal("9000000"))
                .discountRate(new BigDecimal("0.9")).enabled(true).build());
        var member = memberRepository.save(com.info.ecommerce.modules.crm.entity.Member.builder()
                .name("大戶").email("vip-" + suffix + "@example.com").build());
        orderRepository.save(com.info.ecommerce.modules.order.entity.Order.builder()
                .orderNumber("ORD-VIP-" + suffix).customerId(member.getId())
                .status(com.info.ecommerce.modules.order.enums.OrderStatus.COMPLETED)
                .pickupType(com.info.ecommerce.modules.order.enums.PickupType.DELIVERY)
                .subtotalAmount(new BigDecimal("9500000")).totalAmount(new BigDecimal("9500000")).build());

        memberService.syncTotalSpent(member.getId());

        assertEquals(level.getId(), memberRepository.findById(member.getId()).orElseThrow().getLevelId());
    }
}
