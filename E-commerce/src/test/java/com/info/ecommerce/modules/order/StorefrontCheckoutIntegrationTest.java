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
        mockMvc.perform(patch("/api/orders/" + orderId + "/status").param("status", "PAID")
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
}
