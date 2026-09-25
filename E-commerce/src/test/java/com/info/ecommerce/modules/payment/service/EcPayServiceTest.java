package com.info.ecommerce.modules.payment.service;

import com.info.ecommerce.modules.payment.config.EcPayConfig;
import com.info.ecommerce.modules.payment.dto.PaymentConfirmDTO;
import com.info.ecommerce.modules.payment.dto.PaymentRequestDTO;
import com.info.ecommerce.modules.payment.dto.PaymentResponseDTO;
import com.info.ecommerce.modules.payment.enums.PaymentGateway;
import com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * ECPay 支付服務測試
 */
@ExtendWith(MockitoExtension.class)
class EcPayServiceTest {

    @Mock
    private EcPayConfig ecPayConfig;

    @InjectMocks
    private EcPayService ecPayService;

    @BeforeEach
    void setUp() {
        // 使用 lenient() 設定測試用的 ECPay 配置
        lenient().when(ecPayConfig.getMerchantId()).thenReturn("2000132");
        lenient().when(ecPayConfig.getHashKey()).thenReturn("5294y06JbISpM5x9");
        lenient().when(ecPayConfig.getHashIV()).thenReturn("v77hoKGq4kWxNNIS");
        lenient().when(ecPayConfig.getApiUrl()).thenReturn("https://payment-stage.ecpay.com.tw");
        lenient().when(ecPayConfig.getNotifyUrl()).thenReturn("http://localhost:8080/api/payment-gateway/callback/ecpay");
        lenient().when(ecPayConfig.getReturnUrl()).thenReturn("http://localhost:8080/payment/result");
    }

    @Test
    void testCreatePayment_Success() {
        // Arrange
        PaymentRequestDTO request = PaymentRequestDTO.builder()
                .orderNumber("TEST20240101001")
                .amount(new BigDecimal("1000"))
                .productName("測試商品")
                .customerEmail("test@example.com")
                .customerPhone("0912345678")
                .build();

        // Act
        PaymentResponseDTO response = ecPayService.createPayment(request);

        // Assert
        assertNotNull(response);
        assertEquals(PaymentGateway.ECPAY, response.getGateway());
        assertEquals(PaymentGatewayStatus.INITIATED, response.getStatus());
        assertEquals("TEST20240101001", response.getOrderNumber());
        assertEquals(new BigDecimal("1000"), response.getAmount());
        assertNotNull(response.getPaymentUrl());
        assertTrue(response.getPaymentUrl().contains("MerchantID=2000132"));
        assertTrue(response.getPaymentUrl().contains("MerchantTradeNo=TEST20240101001"));
    }

    @Test
    void testCreatePayment_WithMinimalInfo() {
        // Arrange
        PaymentRequestDTO request = PaymentRequestDTO.builder()
                .orderNumber("TEST20240101002")
                .amount(new BigDecimal("500"))
                .productName("簡單商品")
                .build();

        // Act
        PaymentResponseDTO response = ecPayService.createPayment(request);

        // Assert
        assertNotNull(response);
        assertEquals(PaymentGatewayStatus.INITIATED, response.getStatus());
        assertEquals("TEST20240101002", response.getOrderNumber());
        assertNotNull(response.getPaymentUrl());
    }

    @Test
    void testVerifyCallback_ValidCheckMacValue() {
        // Arrange
        Map<String, String> params = new HashMap<>();
        params.put("MerchantID", "2000132");
        params.put("MerchantTradeNo", "TEST20240101001");
        params.put("RtnCode", "1");
        params.put("RtnMsg", "交易成功");
        params.put("TradeNo", "2401011234567890");
        params.put("TradeAmt", "1000");
        params.put("PaymentType", "Credit_CreditCard");
        params.put("TradeDate", "2024/01/01 12:00:00");
        
        // 使用 ECPay 測試環境的 HashKey 和 HashIV 生成有效的 CheckMacValue
        // 這裡需要正確計算 CheckMacValue
        String validCheckMacValue = generateValidCheckMacValue(params);
        params.put("CheckMacValue", validCheckMacValue);

        // Act
        boolean isValid = ecPayService.verifyCallback(params);

        // Assert
        assertTrue(isValid, "CheckMacValue should be valid");
    }

    @Test
    void testVerifyCallback_InvalidCheckMacValue() {
        // Arrange
        Map<String, String> params = new HashMap<>();
        params.put("MerchantID", "2000132");
        params.put("MerchantTradeNo", "TEST20240101001");
        params.put("RtnCode", "1");
        params.put("CheckMacValue", "INVALID_CHECK_MAC_VALUE");

        // Act
        boolean isValid = ecPayService.verifyCallback(params);

        // Assert
        assertFalse(isValid, "Invalid CheckMacValue should fail verification");
    }

    @Test
    void testVerifyCallback_MissingCheckMacValue() {
        // Arrange
        Map<String, String> params = new HashMap<>();
        params.put("MerchantID", "2000132");
        params.put("MerchantTradeNo", "TEST20240101001");

        // Act
        boolean isValid = ecPayService.verifyCallback(params);

        // Assert
        assertFalse(isValid, "Missing CheckMacValue should fail verification");
    }

    @Test
    void testParseCallback_WithoutCustomField_StripsMerchantTradeNoSuffix() {
        // 舊交易沒有 CustomField1，MerchantTradeNo = 訂單編號 + 4 碼後綴
        Map<String, String> params = new HashMap<>();
        params.put("MerchantID", "2000132");
        params.put("MerchantTradeNo", "TEST202401010011234");
        params.put("RtnCode", "1");
        params.put("RtnMsg", "交易成功");
        params.put("TradeNo", "2401011234567890");
        params.put("TradeAmt", "1000");
        params.put("CheckMacValue", generateValidCheckMacValue(params));

        PaymentResponseDTO response = ecPayService.parseCallback(params);

        assertEquals(PaymentGatewayStatus.SUCCESS, response.getStatus());
        assertEquals("TEST20240101001", response.getOrderNumber());
    }

    @Test
    void testCreatePayment_SendsFullOrderNumberInCustomField1() {
        PaymentRequestDTO request = PaymentRequestDTO.builder()
                .orderNumber("ORD2026092404361506")
                .amount(new BigDecimal("959"))
                .productName("Shopro Order ORD2026092404361506")
                .build();

        PaymentResponseDTO response = ecPayService.createPayment(request);

        assertTrue(response.getPaymentUrl().contains("CustomField1=ORD2026092404361506"));
    }

    @Test
    void testCreatePayment_UrlParamsDecodeToTheSignedValues() {
        PaymentRequestDTO request = PaymentRequestDTO.builder()
                .orderNumber("ORD2026092404361507")
                .amount(new BigDecimal("500"))
                .productName("茶葉 x 1 & 茶杯+杯墊")
                .customerEmail("name+shop@gmail.com")
                .build();

        String url = ecPayService.createPayment(request).getPaymentUrl();

        // 前端以 URLSearchParams 取回參數（與 URLDecoder 規則相同）後 POST 給綠界
        Map<String, String> decoded = new java.util.LinkedHashMap<>();
        for (String pair : url.substring(url.indexOf('?') + 1).split("&")) {
            String[] kv = pair.split("=", 2);
            decoded.put(java.net.URLDecoder.decode(kv[0], java.nio.charset.StandardCharsets.UTF_8),
                    java.net.URLDecoder.decode(kv[1], java.nio.charset.StandardCharsets.UTF_8));
        }
        assertEquals("name+shop@gmail.com", decoded.get("Email"));
        assertEquals("茶葉 x 1 & 茶杯+杯墊", decoded.get("ItemName"));
        // 取回的參數仍能通過簽章驗證
        assertTrue(ecPayService.verifyCallback(decoded));
    }

    @Test
    void testParseCallback_Success() {
        // Arrange
        Map<String, String> params = new HashMap<>();
        params.put("MerchantID", "2000132");
        params.put("MerchantTradeNo", "TEST202401010011234");
        params.put("CustomField1", "TEST20240101001");
        params.put("RtnCode", "1");
        params.put("RtnMsg", "交易成功");
        params.put("TradeNo", "2401011234567890");
        params.put("TradeAmt", "1000");
        params.put("PaymentType", "Credit_CreditCard");
        
        String validCheckMacValue = generateValidCheckMacValue(params);
        params.put("CheckMacValue", validCheckMacValue);

        // Act
        PaymentResponseDTO response = ecPayService.parseCallback(params);

        // Assert
        assertNotNull(response);
        assertEquals(PaymentGateway.ECPAY, response.getGateway());
        assertEquals(PaymentGatewayStatus.SUCCESS, response.getStatus());
        assertEquals("TEST20240101001", response.getOrderNumber());
        assertEquals("2401011234567890", response.getTransactionId());
        assertEquals(new BigDecimal("1000"), response.getAmount());
    }

    @Test
    void testParseCallback_Failed() {
        // Arrange
        Map<String, String> params = new HashMap<>();
        params.put("MerchantID", "2000132");
        params.put("MerchantTradeNo", "TEST202401010021234");
        params.put("CustomField1", "TEST20240101002");
        params.put("RtnCode", "0");
        params.put("RtnMsg", "交易失敗");
        params.put("TradeNo", "2401011234567891");
        params.put("TradeAmt", "1000");
        
        String validCheckMacValue = generateValidCheckMacValue(params);
        params.put("CheckMacValue", validCheckMacValue);

        // Act
        PaymentResponseDTO response = ecPayService.parseCallback(params);

        // Assert
        assertNotNull(response);
        assertEquals(PaymentGateway.ECPAY, response.getGateway());
        assertEquals(PaymentGatewayStatus.FAILED, response.getStatus());
        assertEquals("TEST20240101002", response.getOrderNumber());
        assertTrue(response.getErrorMessage().contains("交易失敗"));
    }

    @Test
    void testParseCallback_InvalidCheckMacValue() {
        // Arrange
        Map<String, String> params = new HashMap<>();
        params.put("MerchantID", "2000132");
        params.put("MerchantTradeNo", "TEST20240101003");
        params.put("RtnCode", "1");
        params.put("CheckMacValue", "INVALID_MAC");

        // Act
        PaymentResponseDTO response = ecPayService.parseCallback(params);

        // Assert
        assertNotNull(response);
        assertEquals(PaymentGatewayStatus.FAILED, response.getStatus());
        assertTrue(response.getErrorMessage().contains("CheckMacValue"));
    }

    @Test
    void testConfirmPayment() {
        // Arrange
        PaymentConfirmDTO confirm = PaymentConfirmDTO.builder()
                .transactionId("2401011234567890")
                .orderNumber("TEST20240101001")
                .build();

        // Act
        PaymentResponseDTO response = ecPayService.confirmPayment(confirm);

        // Assert
        assertNotNull(response);
        assertEquals(PaymentGateway.ECPAY, response.getGateway());
        assertEquals(PaymentGatewayStatus.SUCCESS, response.getStatus());
        assertEquals("TEST20240101001", response.getOrderNumber());
    }

    @Test
    void testQueryPayment() {
        // Arrange
        String transactionId = "2401011234567890";

        // Act
        PaymentResponseDTO response = ecPayService.queryPayment(transactionId);

        // Assert
        assertNotNull(response);
        assertEquals(PaymentGateway.ECPAY, response.getGateway());
        assertEquals(PaymentGatewayStatus.PROCESSING, response.getStatus());
        assertTrue(response.getErrorMessage().contains("不支援主動查詢"));
    }

    @Test
    void testCancelPayment() {
        // Arrange
        String transactionId = "2401011234567890";

        // Act
        PaymentResponseDTO response = ecPayService.cancelPayment(transactionId);

        // Assert
        assertNotNull(response);
        assertEquals(PaymentGateway.ECPAY, response.getGateway());
        assertEquals(PaymentGatewayStatus.CANCELLED, response.getStatus());
    }

    /**
     * 生成有效的 CheckMacValue 用於測試
     * 這個方法模擬 ECPay 的 CheckMacValue 生成邏輯
     */
    private String generateValidCheckMacValue(Map<String, String> params) {
        // 使用實際的 EcPayService 方法來生成 CheckMacValue
        // 因為 generateCheckMacValue 是 private，我們需要通過 verifyCallback 間接測試
        // 這裡使用一個簡化的實現用於測試
        try {
            java.lang.reflect.Method method = EcPayService.class.getDeclaredMethod("generateCheckMacValue", Map.class);
            method.setAccessible(true);
            return (String) method.invoke(ecPayService, params);
        } catch (Exception e) {
            // 如果反射失敗，返回一個假的值用於測試失敗場景
            return "MOCK_CHECK_MAC_VALUE";
        }
    }
}
