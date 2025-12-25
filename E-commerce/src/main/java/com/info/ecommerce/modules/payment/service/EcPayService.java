package com.info.ecommerce.modules.payment.service;

import com.info.ecommerce.modules.payment.config.EcPayConfig;
import com.info.ecommerce.modules.payment.dto.PaymentConfirmDTO;
import com.info.ecommerce.modules.payment.dto.PaymentRequestDTO;
import com.info.ecommerce.modules.payment.dto.PaymentResponseDTO;
import com.info.ecommerce.modules.payment.enums.PaymentGateway;
import com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ECPay 綠界支付閘道服務實作
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EcPayService implements PaymentGatewayService {

    private final EcPayConfig ecPayConfig;

    /**
     * 創建 ECPay 支付請求
     */
    @Override
    public PaymentResponseDTO createPayment(PaymentRequestDTO request) {
        try {
            log.info("Creating ECPay payment for order: {}", request.getOrderNumber());

            // 構建 ECPay 請求參數
            Map<String, String> params = buildEcPayRequest(request);
            
            // 生成檢查碼
            String checkMacValue = generateCheckMacValue(params);
            params.put("CheckMacValue", checkMacValue);
            
            // 構建支付 URL
            String paymentUrl = buildPaymentUrl(params);
            
            log.info("ECPay payment URL generated for order: {}", request.getOrderNumber());

            return PaymentResponseDTO.builder()
                    .gateway(PaymentGateway.ECPAY)
                    .status(PaymentGatewayStatus.INITIATED)
                    .orderNumber(request.getOrderNumber())
                    .amount(request.getAmount())
                    .paymentUrl(paymentUrl)
                    .webPaymentUrl(paymentUrl)
                    .transactionId(request.getOrderNumber()) // ECPay 使用訂單編號作為交易 ID
                    .build();

        } catch (Exception e) {
            log.error("Failed to create ECPay payment", e);
            return PaymentResponseDTO.builder()
                    .gateway(PaymentGateway.ECPAY)
                    .status(PaymentGatewayStatus.FAILED)
                    .orderNumber(request.getOrderNumber())
                    .amount(request.getAmount())
                    .errorMessage("建立 ECPay 支付失敗: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 確認 ECPay 支付（處理回調）
     */
    @Override
    public PaymentResponseDTO confirmPayment(PaymentConfirmDTO confirm) {
        try {
            log.info("Confirming ECPay payment for order: {}", confirm.getOrderNumber());

            // ECPay 的確認是透過回調完成的
            // 這裡主要用於驗證回調資料的有效性
            
            return PaymentResponseDTO.builder()
                    .gateway(PaymentGateway.ECPAY)
                    .status(PaymentGatewayStatus.SUCCESS)
                    .transactionId(confirm.getTransactionId())
                    .orderNumber(confirm.getOrderNumber())
                    .build();

        } catch (Exception e) {
            log.error("Failed to confirm ECPay payment", e);
            return PaymentResponseDTO.builder()
                    .gateway(PaymentGateway.ECPAY)
                    .status(PaymentGatewayStatus.FAILED)
                    .transactionId(confirm.getTransactionId())
                    .orderNumber(confirm.getOrderNumber())
                    .errorMessage("確認 ECPay 支付失敗: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 查詢 ECPay 支付狀態（ECPay 不支援主動查詢，需透過回調）
     */
    @Override
    public PaymentResponseDTO queryPayment(String transactionId) {
        log.warn("ECPay does not support active payment query, transaction ID: {}", transactionId);
        
        return PaymentResponseDTO.builder()
                .gateway(PaymentGateway.ECPAY)
                .status(PaymentGatewayStatus.PROCESSING)
                .transactionId(transactionId)
                .errorMessage("ECPay 不支援主動查詢，請等待付款回調通知")
                .build();
    }

    /**
     * 取消 ECPay 支付
     */
    @Override
    public PaymentResponseDTO cancelPayment(String transactionId) {
        log.info("ECPay payment cancellation requested for transaction: {}", transactionId);
        
        // ECPay 的取消需要透過後台操作或特定的退款 API
        return PaymentResponseDTO.builder()
                .gateway(PaymentGateway.ECPAY)
                .status(PaymentGatewayStatus.CANCELLED)
                .transactionId(transactionId)
                .errorMessage("ECPay 取消支付需要透過後台操作")
                .build();
    }

    /**
     * 驗證 ECPay 回調的 CheckMacValue
     */
    public boolean verifyCallback(Map<String, String> params) {
        try {
            String receivedCheckMacValue = params.get("CheckMacValue");
            if (receivedCheckMacValue == null) {
                return false;
            }
            
            // 移除 CheckMacValue 後計算
            Map<String, String> paramsForCheck = new HashMap<>(params);
            paramsForCheck.remove("CheckMacValue");
            
            String calculatedCheckMacValue = generateCheckMacValue(paramsForCheck);
            
            return receivedCheckMacValue.equalsIgnoreCase(calculatedCheckMacValue);
        } catch (Exception e) {
            log.error("Failed to verify ECPay callback", e);
            return false;
        }
    }

    /**
     * 構建 ECPay 請求參數
     */
    private Map<String, String> buildEcPayRequest(PaymentRequestDTO request) {
        Map<String, String> params = new LinkedHashMap<>();
        
        // 基本參數
        params.put("MerchantID", ecPayConfig.getMerchantId());
        params.put("MerchantTradeNo", request.getOrderNumber());
        params.put("MerchantTradeDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        params.put("PaymentType", "aio");
        params.put("TotalAmount", String.valueOf(request.getAmount().intValue()));
        params.put("TradeDesc", urlEncode("商品交易"));
        params.put("ItemName", urlEncode(request.getProductName()));
        params.put("ReturnURL", ecPayConfig.getNotifyUrl());
        params.put("ChoosePayment", "ALL"); // 顯示所有付款方式
        params.put("EncryptType", "1"); // SHA256
        
        // 客戶資料（選填）
        if (request.getCustomerEmail() != null) {
            params.put("Email", urlEncode(request.getCustomerEmail()));
        }
        if (request.getCustomerPhone() != null) {
            params.put("PhoneNo", request.getCustomerPhone());
        }
        
        // 付款完成後的導向頁面
        if (ecPayConfig.getReturnUrl() != null) {
            params.put("ClientBackURL", ecPayConfig.getReturnUrl());
        }
        
        return params;
    }

    /**
     * 生成 ECPay CheckMacValue
     */
    private String generateCheckMacValue(Map<String, String> params) {
        // 按照 ECPay 規範：
        // 1. 參數依照 Key 的字母順序排序（由 A 到 Z，大小寫不分）
        // 2. 組成字串：HashKey=xxx&參數1=值1&參數2=值2...&HashIV=yyy
        // 3. URL encode
        // 4. 轉小寫
        // 5. SHA256 加密
        // 6. 轉大寫
        
        String hashKey = ecPayConfig.getHashKey();
        String hashIV = ecPayConfig.getHashIV();
        
        // 排序並組合參數
        String sortedParams = params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER))
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        
        // 組合完整字串
        String rawString = "HashKey=" + hashKey + "&" + sortedParams + "&HashIV=" + hashIV;
        
        // URL encode
        String encodedString = urlEncode(rawString);
        
        // 轉小寫
        String lowerString = encodedString.toLowerCase();
        
        // SHA256 加密並轉大寫
        return DigestUtils.sha256Hex(lowerString).toUpperCase();
    }

    /**
     * URL 編碼
     */
    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20")
                    .replace("%2A", "*")
                    .replace("%7E", "~");
        } catch (Exception e) {
            throw new RuntimeException("Failed to URL encode value", e);
        }
    }

    /**
     * 構建支付 URL（用於 HTML 表單提交）
     */
    private String buildPaymentUrl(Map<String, String> params) {
        // ECPay 需要透過 HTML 表單 POST 提交
        // 這裡返回的是帶參數的 URL，前端需要用表單提交
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(ecPayConfig.getApiUrl() + "/Cashier/AioCheckOut/V5");
        
        params.forEach(builder::queryParam);
        
        return builder.build().toUriString();
    }

    /**
     * 解析 ECPay 回調參數
     */
    public PaymentResponseDTO parseCallback(Map<String, String> params) {
        try {
            // 驗證 CheckMacValue
            if (!verifyCallback(params)) {
                return PaymentResponseDTO.builder()
                        .gateway(PaymentGateway.ECPAY)
                        .status(PaymentGatewayStatus.FAILED)
                        .errorMessage("CheckMacValue 驗證失敗")
                        .build();
            }
            
            String merchantTradeNo = params.get("MerchantTradeNo");
            String tradeNo = params.get("TradeNo");
            String rtnCode = params.get("RtnCode");
            String rtnMsg = params.get("RtnMsg");
            String tradeAmt = params.get("TradeAmt");
            String paymentType = params.get("PaymentType");
            
            PaymentResponseDTO.PaymentResponseDTOBuilder builder = PaymentResponseDTO.builder()
                    .gateway(PaymentGateway.ECPAY)
                    .orderNumber(merchantTradeNo)
                    .transactionId(tradeNo);
            
            if (tradeAmt != null) {
                builder.amount(new BigDecimal(tradeAmt));
            }
            
            // 判斷交易狀態
            if ("1".equals(rtnCode)) {
                builder.status(PaymentGatewayStatus.SUCCESS);
            } else {
                builder.status(PaymentGatewayStatus.FAILED)
                        .errorMessage("ECPay 錯誤: " + rtnMsg);
            }
            
            return builder.build();
            
        } catch (Exception e) {
            log.error("Failed to parse ECPay callback", e);
            return PaymentResponseDTO.builder()
                    .gateway(PaymentGateway.ECPAY)
                    .status(PaymentGatewayStatus.FAILED)
                    .errorMessage("解析 ECPay 回調失敗: " + e.getMessage())
                    .build();
        }
    }
}
