package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.modules.product.repository.ProductInventoryRepository;
import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.crm.entity.Member;
import com.info.ecommerce.modules.crm.repository.MemberRepository;
import com.info.ecommerce.modules.order.dto.OrderDTO;
import com.info.ecommerce.modules.order.dto.StorefrontCheckoutRequest;
import com.info.ecommerce.modules.order.dto.StorefrontCheckoutResultDTO;
import com.info.ecommerce.modules.order.dto.StorefrontQuoteDTO;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.enums.PickupType;
import com.info.ecommerce.modules.order.entity.OrderHistory;
import com.info.ecommerce.modules.order.repository.OrderHistoryRepository;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import com.info.ecommerce.modules.payment.dto.PaymentRequestDTO;
import com.info.ecommerce.modules.payment.dto.PaymentResponseDTO;
import com.info.ecommerce.modules.payment.enums.PaymentGateway;
import com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus;
import com.info.ecommerce.modules.payment.service.PaymentGatewayFactory;
import com.info.ecommerce.modules.payment.service.PaymentGatewayService;
import com.info.ecommerce.modules.product.entity.Product;
import com.info.ecommerce.modules.product.entity.ProductSpecification;
import com.info.ecommerce.modules.product.enums.ProductStatus;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import com.info.ecommerce.modules.product.repository.ProductSpecificationRepository;
import com.info.ecommerce.modules.system.entity.ShippingConfig;
import com.info.ecommerce.modules.system.repository.ShippingConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

/**
 * 前台結帳服務單元測試
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StorefrontCheckoutServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductSpecificationRepository productSpecificationRepository;

    @Mock
    private ShippingConfigRepository shippingConfigRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private PaymentGatewayFactory paymentGatewayFactory;

    @Mock
    private PaymentGatewayService ecPayService;

    @Mock
    private OrderStockService orderStockService;

    @Mock
    private OrderHistoryService orderHistoryService;

    @Mock
    private ProductInventoryRepository productInventoryRepository;

    @Mock
    private OrderHistoryRepository orderHistoryRepository;

    @Mock
    private org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Mock
    private com.info.ecommerce.modules.marketing.service.CheckoutDiscountService checkoutDiscountService;

    @Mock
    private com.info.ecommerce.modules.auth.service.CurrentUserService currentUserService;

    @Mock
    private com.info.ecommerce.modules.order.repository.OrderDiscountRepository orderDiscountRepository;

    @Mock
    private com.info.ecommerce.modules.marketing.repository.CouponRepository couponRepository;

    @Mock
    private com.info.ecommerce.modules.order.repository.OrderShipmentRepository orderShipmentRepository;

    @InjectMocks
    private StorefrontCheckoutService storefrontCheckoutService;

    private Product plainProduct;
    private Product specProduct;
    private ProductSpecification spec;

    @BeforeEach
    void setUp() {
        // 預設沒有任何折扣
        when(checkoutDiscountService.calculate(any(), any(), any(), anyBoolean(), any()))
                .thenReturn(new com.info.ecommerce.modules.marketing.service.CheckoutDiscountService.Result(
                        java.math.BigDecimal.ZERO, false, java.util.List.of(), null, null, null));
        plainProduct = Product.builder()
            .id(1L).name("香草甜筒").sku("P-001")
            .status(ProductStatus.ACTIVE).enabled(true)
            .basePrice(new BigDecimal("120")).salePrice(new BigDecimal("99"))
            .build();
        specProduct = Product.builder()
            .id(2L).name("日式茶杯").sku("P-002")
            .status(ProductStatus.ACTIVE).enabled(true)
            .basePrice(new BigDecimal("300"))
            .build();
        spec = ProductSpecification.builder()
            .id(20L).productId(2L).specName("藍色").sku("P-002-BL")
            .price(new BigDecimal("350")).stock(5).enabled(true)
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(plainProduct));
        when(productRepository.findById(2L)).thenReturn(Optional.of(specProduct));
        when(productSpecificationRepository.findById(20L)).thenReturn(Optional.of(spec));
        when(productSpecificationRepository.findByProductIdAndEnabledTrue(1L)).thenReturn(List.of());
        when(productSpecificationRepository.findByProductIdAndEnabledTrue(2L)).thenReturn(List.of(spec));
        when(shippingConfigRepository.findByEnabledOrderBySortOrderAsc(true)).thenReturn(List.of());
        when(paymentGatewayFactory.getPaymentGatewayService(PaymentGateway.ECPAY)).thenReturn(ecPayService);
        when(orderService.createOrder(any(OrderDTO.class))).thenAnswer(invocation -> {
            OrderDTO dto = invocation.getArgument(0);
            dto.setId(100L);
            dto.setOrderNumber("ORD2026010112000001");
            return dto;
        });
    }

    private static StorefrontCheckoutRequest.Item item(Long productId, Long specId, int quantity) {
        return StorefrontCheckoutRequest.Item.builder()
            .productId(productId).specificationId(specId).quantity(quantity)
            .build();
    }

    private static StorefrontCheckoutRequest.StorefrontCheckoutRequestBuilder checkoutRequest() {
        return StorefrontCheckoutRequest.builder()
            .customerName("王小明")
            .customerPhone("0912345678")
            .customerEmail("buyer@example.com")
            .shippingAddress("台北市信義區市府路 1 號")
            .items(List.of(item(1L, null, 2)));
    }

    @Test
    void quote_usesServerPricesAndDefaultShippingFee() {
        StorefrontQuoteDTO quote = storefrontCheckoutService.quote(
            List.of(item(1L, null, 2), item(2L, 20L, 1)), null);

        assertEquals(2, quote.getLines().size());
        assertEquals(0, new BigDecimal("99").compareTo(quote.getLines().get(0).getUnitPrice()));
        assertEquals(0, new BigDecimal("350").compareTo(quote.getLines().get(1).getUnitPrice()));
        assertEquals("藍色", quote.getLines().get(1).getSpecName());
        assertEquals(0, new BigDecimal("548").compareTo(quote.getSubtotalAmount()));
        assertEquals(0, new BigDecimal("100").compareTo(quote.getShippingFee()));
        assertEquals(0, new BigDecimal("648").compareTo(quote.getTotalAmount()));
        assertEquals(StorefrontCheckoutService.SHIPPING_HOME_DELIVERY, quote.getShippingMethod());
    }

    @Test
    void quote_freeShippingWhenThresholdReached() {
        StorefrontQuoteDTO quote = storefrontCheckoutService.quote(List.of(item(2L, 20L, 3)), "HOME_DELIVERY");

        assertEquals(0, new BigDecimal("1050").compareTo(quote.getSubtotalAmount()));
        assertEquals(0, BigDecimal.ZERO.compareTo(quote.getShippingFee()));
    }

    @Test
    void quote_usesEnabledShippingConfig() {
        ShippingConfig config = ShippingConfig.builder()
            .shippingMethod("HOME_DELIVERY").enabled(true)
            .baseShippingFee(new BigDecimal("80")).freeShippingThreshold(new BigDecimal("2000"))
            .build();
        when(shippingConfigRepository.findByEnabledOrderBySortOrderAsc(true)).thenReturn(List.of(config));

        StorefrontQuoteDTO quote = storefrontCheckoutService.quote(List.of(item(2L, 20L, 3)), "HOME_DELIVERY");

        assertEquals(0, new BigDecimal("80").compareTo(quote.getShippingFee()));
        assertEquals(0, new BigDecimal("2000").compareTo(quote.getFreeShippingThreshold()));
    }

    @Test
    void quote_storePickupHasNoShippingFee() {
        StorefrontQuoteDTO quote = storefrontCheckoutService.quote(List.of(item(1L, null, 1)), "store_pickup");

        assertEquals(0, BigDecimal.ZERO.compareTo(quote.getShippingFee()));
        assertEquals(StorefrontCheckoutService.SHIPPING_STORE_PICKUP, quote.getShippingMethod());
    }

    @Test
    void quote_rejectsInsufficientStockAcrossDuplicateLines() {
        BusinessException ex = assertThrows(BusinessException.class, () ->
            storefrontCheckoutService.quote(List.of(item(2L, 20L, 3), item(2L, 20L, 3)), null));

        assertTrue(ex.getMessage().contains("庫存不足"));
    }

    @Test
    void quote_requiresSpecificationWhenProductHasSpecs() {
        BusinessException ex = assertThrows(BusinessException.class, () ->
            storefrontCheckoutService.quote(List.of(item(2L, null, 1)), null));

        assertTrue(ex.getMessage().contains("請選擇"));
    }

    @Test
    void quote_rejectsInactiveProduct() {
        plainProduct.setStatus(ProductStatus.INACTIVE);

        assertThrows(BusinessException.class, () ->
            storefrontCheckoutService.quote(List.of(item(1L, null, 1)), null));
    }

    @Test
    void quote_rejectsSpecificationOfAnotherProduct() {
        assertThrows(BusinessException.class, () ->
            storefrontCheckoutService.quote(List.of(item(1L, 20L, 1)), null));
    }

    @Test
    void checkout_codCreatesMemberAndOrderWithServerAmounts() {
        when(memberRepository.findByEmail("buyer@example.com")).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
            Member member = invocation.getArgument(0);
            member.setId(7L);
            return member;
        });

        StorefrontCheckoutResultDTO result = storefrontCheckoutService.checkout(
            checkoutRequest().paymentMethod("cod").notes("請下午送達").build());

        ArgumentCaptor<OrderDTO> captor = ArgumentCaptor.forClass(OrderDTO.class);
        verify(orderService).createOrder(captor.capture());
        OrderDTO created = captor.getValue();

        assertEquals(7L, created.getCustomerId());
        assertEquals(PickupType.DELIVERY, created.getPickupType());
        assertEquals(0, new BigDecimal("198").compareTo(created.getSubtotalAmount()));
        assertEquals(0, new BigDecimal("100").compareTo(created.getShippingFee()));
        assertEquals(0, new BigDecimal("298").compareTo(created.getTotalAmount()));
        assertEquals(0, new BigDecimal("99").compareTo(created.getItems().get(0).getUnitPrice()));
        assertTrue(created.getNotes().contains("貨到付款"));
        assertTrue(created.getNotes().contains("請下午送達"));

        assertEquals("COD", result.getPaymentMethod());
        assertNull(result.getPaymentUrl());
        verifyNoInteractions(ecPayService);
        verify(orderStockService).reserve(100L, "ORD2026010112000001");
        verify(eventPublisher).publishEvent(new com.info.ecommerce.modules.order.event.OrderEmailEvent(
            100L, com.info.ecommerce.modules.order.event.OrderEmailEvent.Type.CREATED));
        verify(orderHistoryService).recordHistory(eq(100L), eq(StorefrontCheckoutService.ACTION_STOREFRONT_CHECKOUT),
            anyString(), isNull(), eq("COD"), isNull(), anyString());
    }

    @Test
    void checkout_ecpayReturnsPaymentUrlForServerTotal() {
        when(memberRepository.findByEmail("buyer@example.com"))
            .thenReturn(Optional.of(Member.builder().id(3L).email("buyer@example.com").build()));
        when(ecPayService.createPayment(any(PaymentRequestDTO.class))).thenReturn(PaymentResponseDTO.builder()
            .status(PaymentGatewayStatus.INITIATED)
            .paymentUrl("https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5?MerchantID=2000132")
            .build());

        StorefrontCheckoutResultDTO result = storefrontCheckoutService.checkout(checkoutRequest().build());

        ArgumentCaptor<PaymentRequestDTO> captor = ArgumentCaptor.forClass(PaymentRequestDTO.class);
        verify(ecPayService).createPayment(captor.capture());
        assertEquals(0, new BigDecimal("298").compareTo(captor.getValue().getAmount()));
        assertEquals("ORD2026010112000001", captor.getValue().getOrderNumber());
        assertEquals("ECPAY", result.getPaymentMethod());
        assertNotNull(result.getPaymentUrl());
        assertNull(result.getPaymentError());
        verify(memberRepository, never()).save(any());
    }

    @Test
    void checkout_keepsOrderWhenPaymentCreationFails() {
        when(memberRepository.findByEmail("buyer@example.com"))
            .thenReturn(Optional.of(Member.builder().id(3L).email("buyer@example.com").build()));
        when(ecPayService.createPayment(any(PaymentRequestDTO.class))).thenThrow(new RuntimeException("gateway down"));

        StorefrontCheckoutResultDTO result = storefrontCheckoutService.checkout(checkoutRequest().build());

        assertNotNull(result.getOrder());
        assertNull(result.getPaymentUrl());
        assertNotNull(result.getPaymentError());
    }

    @Test
    void checkout_homeDeliveryRequiresAddress() {
        assertThrows(BusinessException.class, () ->
            storefrontCheckoutService.checkout(checkoutRequest().shippingAddress("  ").build()));
        verify(orderService, never()).createOrder(any());
    }

    @Test
    void checkout_storePickupDoesNotRequireAddress() {
        when(memberRepository.findByEmail("buyer@example.com"))
            .thenReturn(Optional.of(Member.builder().id(3L).email("buyer@example.com").build()));

        storefrontCheckoutService.checkout(checkoutRequest()
            .shippingAddress(null).shippingMethod("STORE_PICKUP").paymentMethod("COD").build());

        ArgumentCaptor<OrderDTO> captor = ArgumentCaptor.forClass(OrderDTO.class);
        verify(orderService).createOrder(captor.capture());
        assertEquals(PickupType.STORE_PICKUP, captor.getValue().getPickupType());
        assertNull(captor.getValue().getShippingAddress());
    }

    @Test
    void checkout_rejectsUnknownPaymentMethod() {
        assertThrows(BusinessException.class, () ->
            storefrontCheckoutService.checkout(checkoutRequest().paymentMethod("BITCOIN").build()));
    }

    @Test
    void lookupOrder_matchesEmailCaseInsensitively() {
        Order order = Order.builder().id(100L).orderNumber("ORD1").customerEmail("Buyer@Example.com").build();
        OrderDTO dto = OrderDTO.builder().id(100L).orderNumber("ORD1").build();
        when(orderRepository.findByOrderNumber("ORD1")).thenReturn(Optional.of(order));
        when(orderService.getOrder(100L)).thenReturn(dto);

        when(orderHistoryRepository.findByOrderIdAndActionType(100L, StorefrontCheckoutService.ACTION_STOREFRONT_CHECKOUT))
            .thenReturn(List.of(OrderHistory.builder().newStatus("ECPAY").build()));
        order.setStatus(com.info.ecommerce.modules.order.enums.OrderStatus.PENDING_PAYMENT);

        var result = storefrontCheckoutService.lookupOrder(" ORD1 ", "buyer@example.com");
        assertSame(dto, result.getOrder());
        assertEquals("ECPAY", result.getPaymentMethod());
        assertTrue(result.isCanPayOnline());
    }

    @Test
    void lookupOrder_rejectsWrongEmail() {
        Order order = Order.builder().id(100L).orderNumber("ORD1").customerEmail("buyer@example.com").build();
        when(orderRepository.findByOrderNumber("ORD1")).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () ->
            storefrontCheckoutService.lookupOrder("ORD1", "someone@else.com"));
        verify(orderService, never()).getOrder(any());
    }

    @Test
    void checkout_stockReservationFailure_abortsBeforePayment() {
        when(memberRepository.findByEmail("buyer@example.com"))
            .thenReturn(Optional.of(Member.builder().id(3L).email("buyer@example.com").build()));
        doThrow(new BusinessException("庫存不足")).when(orderStockService).reserve(any(), any());

        assertThrows(BusinessException.class, () -> storefrontCheckoutService.checkout(checkoutRequest().build()));
        verifyNoInteractions(ecPayService);
    }

    @Test
    void quote_rejectsProductLevelStockShortage() {
        when(productInventoryRepository.findByProductIdAndSpecificationId(1L, null)).thenReturn(Optional.of(
            com.info.ecommerce.modules.product.entity.ProductInventory.builder().productId(1L).availableStock(1).build()));

        BusinessException ex = assertThrows(BusinessException.class, () ->
            storefrontCheckoutService.quote(List.of(item(1L, null, 2)), null));
        assertTrue(ex.getMessage().contains("庫存不足"));
    }

    @Test
    void payAgain_createsNewPaymentForPendingOnlineOrder() {
        Order order = Order.builder().id(100L).orderNumber("ORD1").customerEmail("buyer@example.com")
            .status(com.info.ecommerce.modules.order.enums.OrderStatus.PENDING_PAYMENT).build();
        when(orderRepository.findByOrderNumber("ORD1")).thenReturn(Optional.of(order));
        when(orderHistoryRepository.findByOrderIdAndActionType(100L, StorefrontCheckoutService.ACTION_STOREFRONT_CHECKOUT))
            .thenReturn(List.of(OrderHistory.builder().newStatus("ECPAY").build()));
        when(orderService.getOrder(100L)).thenReturn(OrderDTO.builder().id(100L).orderNumber("ORD1")
            .totalAmount(new BigDecimal("298")).build());
        when(ecPayService.createPayment(any(PaymentRequestDTO.class))).thenReturn(PaymentResponseDTO.builder()
            .status(PaymentGatewayStatus.INITIATED).paymentUrl("https://payment-stage.ecpay.com.tw/x?a=1").build());

        StorefrontCheckoutResultDTO result = storefrontCheckoutService.payAgain("ORD1", "BUYER@example.com");

        assertNotNull(result.getPaymentUrl());
        ArgumentCaptor<PaymentRequestDTO> captor = ArgumentCaptor.forClass(PaymentRequestDTO.class);
        verify(ecPayService).createPayment(captor.capture());
        assertEquals(0, new BigDecimal("298").compareTo(captor.getValue().getAmount()));
    }

    @Test
    void payAgain_rejectsCodAndPaidOrders() {
        Order cod = Order.builder().id(101L).orderNumber("ORD2").customerEmail("buyer@example.com")
            .status(com.info.ecommerce.modules.order.enums.OrderStatus.PENDING_PAYMENT).build();
        when(orderRepository.findByOrderNumber("ORD2")).thenReturn(Optional.of(cod));
        when(orderHistoryRepository.findByOrderIdAndActionType(101L, StorefrontCheckoutService.ACTION_STOREFRONT_CHECKOUT))
            .thenReturn(List.of(OrderHistory.builder().newStatus("COD").build()));
        assertThrows(BusinessException.class, () -> storefrontCheckoutService.payAgain("ORD2", "buyer@example.com"));

        Order paid = Order.builder().id(102L).orderNumber("ORD3").customerEmail("buyer@example.com")
            .status(com.info.ecommerce.modules.order.enums.OrderStatus.PAID).build();
        when(orderRepository.findByOrderNumber("ORD3")).thenReturn(Optional.of(paid));
        assertThrows(BusinessException.class, () -> storefrontCheckoutService.payAgain("ORD3", "buyer@example.com"));
        verifyNoInteractions(ecPayService);
    }

    @Test
    void quote_purchaseLimitsApplyPerProductAcrossSpecs() {
        specProduct.setMaxPurchaseQuantity(2);
        ProductSpecification white = ProductSpecification.builder()
            .id(21L).productId(2L).specName("白色").price(new BigDecimal("350")).stock(10).enabled(true).build();
        when(productSpecificationRepository.findById(21L)).thenReturn(Optional.of(white));

        BusinessException ex = assertThrows(BusinessException.class, () ->
            storefrontCheckoutService.quote(List.of(item(2L, 20L, 2), item(2L, 21L, 1)), null));
        assertTrue(ex.getMessage().contains("最多購買 2 件"));
    }
}
