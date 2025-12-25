package com.info.ecommerce.modules.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.info.ecommerce.modules.payment.config.LinePayConfig;
import com.info.ecommerce.modules.payment.dto.PaymentConfirmDTO;
import com.info.ecommerce.modules.payment.dto.PaymentRequestDTO;
import com.info.ecommerce.modules.payment.dto.PaymentResponseDTO;
import com.info.ecommerce.modules.payment.enums.PaymentGateway;
import com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * LINE PAY 支付閘道服務實作
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LinePayService implements PaymentGatewayService {

    private final LinePayConfig linePayConfig;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    /**
     * 創建 LINE PAY 支付請求
     */
    @Override
    public PaymentResponseDTO createPayment(PaymentRequestDTO request) {
        try {
            log.info("Creating LINE PAY payment for order: {}", request.getOrderNumber());

            // 構建 LINE PAY 請求
            Map<String, Object> requestBody = buildLinePayRequest(request);
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
            
            // 生成簽章
            String nonce = UUID.randomUUID().toString();
            String signature = generateSignature(linePayConfig.getChannelSecretKey(),
                    "/v3/payments/request", requestBodyJson, nonce);

            // 發送請求到 LINE PAY
            WebClient webClient = webClientBuilder
                    .baseUrl(linePayConfig.getApiUrl())
                    .build();

            String response = webClient.post()
                    .uri("/v3/payments/request")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("X-LINE-ChannelId", linePayConfig.getChannelId())
                    .header("X-LINE-Authorization-Nonce", nonce)
                    .header("X-LINE-Authorization", signature)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("LINE PAY response: {}", response);

            // 解析回應
            return parseLinePayResponse(response, request.getOrderNumber());

        } catch (Exception e) {
            log.error("Failed to create LINE PAY payment", e);
            return PaymentResponseDTO.builder()
                    .gateway(PaymentGateway.LINE_PAY)
                    .status(PaymentGatewayStatus.FAILED)
                    .orderNumber(request.getOrderNumber())
                    .amount(request.getAmount())
                    .errorMessage("建立 LINE PAY 支付失敗: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 確認 LINE PAY 支付
     */
    @Override
    public PaymentResponseDTO confirmPayment(PaymentConfirmDTO confirm) {
        try {
            log.info("Confirming LINE PAY payment for transaction: {}", confirm.getTransactionId());

            // 構建確認請求
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("amount", 0); // 將由實際交易金額填入
            requestBody.put("currency", "TWD");
            
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
            
            // 生成簽章
            String nonce = UUID.randomUUID().toString();
            String uri = "/v3/payments/" + confirm.getTransactionId() + "/confirm";
            String signature = generateSignature(linePayConfig.getChannelSecretKey(),
                    uri, requestBodyJson, nonce);

            // 發送確認請求
            WebClient webClient = webClientBuilder
                    .baseUrl(linePayConfig.getApiUrl())
                    .build();

            String response = webClient.post()
                    .uri(uri)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("X-LINE-ChannelId", linePayConfig.getChannelId())
                    .header("X-LINE-Authorization-Nonce", nonce)
                    .header("X-LINE-Authorization", signature)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("LINE PAY confirm response: {}", response);

            // 解析確認回應
            return parseLinePayConfirmResponse(response, confirm.getOrderNumber());

        } catch (Exception e) {
            log.error("Failed to confirm LINE PAY payment", e);
            return PaymentResponseDTO.builder()
                    .gateway(PaymentGateway.LINE_PAY)
                    .status(PaymentGatewayStatus.FAILED)
                    .transactionId(confirm.getTransactionId())
                    .orderNumber(confirm.getOrderNumber())
                    .errorMessage("確認 LINE PAY 支付失敗: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 查詢 LINE PAY 支付狀態
     */
    @Override
    public PaymentResponseDTO queryPayment(String transactionId) {
        try {
            log.info("Querying LINE PAY payment status for transaction: {}", transactionId);

            // 生成簽章
            String nonce = UUID.randomUUID().toString();
            String uri = "/v3/payments/requests/" + transactionId;
            String signature = generateSignature(linePayConfig.getChannelSecretKey(),
                    uri, "", nonce);

            // 發送查詢請求
            WebClient webClient = webClientBuilder
                    .baseUrl(linePayConfig.getApiUrl())
                    .build();

            String response = webClient.get()
                    .uri(uri)
                    .header("X-LINE-ChannelId", linePayConfig.getChannelId())
                    .header("X-LINE-Authorization-Nonce", nonce)
                    .header("X-LINE-Authorization", signature)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("LINE PAY query response: {}", response);

            return parseLinePayQueryResponse(response, transactionId);

        } catch (Exception e) {
            log.error("Failed to query LINE PAY payment", e);
            return PaymentResponseDTO.builder()
                    .gateway(PaymentGateway.LINE_PAY)
                    .status(PaymentGatewayStatus.FAILED)
                    .transactionId(transactionId)
                    .errorMessage("查詢 LINE PAY 支付狀態失敗: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 取消 LINE PAY 支付
     */
    @Override
    public PaymentResponseDTO cancelPayment(String transactionId) {
        try {
            log.info("Cancelling LINE PAY payment for transaction: {}", transactionId);

            // 生成簽章
            String nonce = UUID.randomUUID().toString();
            String uri = "/v3/payments/" + transactionId + "/void";
            String signature = generateSignature(linePayConfig.getChannelSecretKey(),
                    uri, "", nonce);

            // 發送取消請求
            WebClient webClient = webClientBuilder
                    .baseUrl(linePayConfig.getApiUrl())
                    .build();

            String response = webClient.post()
                    .uri(uri)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("X-LINE-ChannelId", linePayConfig.getChannelId())
                    .header("X-LINE-Authorization-Nonce", nonce)
                    .header("X-LINE-Authorization", signature)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("LINE PAY cancel response: {}", response);

            return PaymentResponseDTO.builder()
                    .gateway(PaymentGateway.LINE_PAY)
                    .status(PaymentGatewayStatus.CANCELLED)
                    .transactionId(transactionId)
                    .rawResponse(response)
                    .build();

        } catch (Exception e) {
            log.error("Failed to cancel LINE PAY payment", e);
            return PaymentResponseDTO.builder()
                    .gateway(PaymentGateway.LINE_PAY)
                    .status(PaymentGatewayStatus.FAILED)
                    .transactionId(transactionId)
                    .errorMessage("取消 LINE PAY 支付失敗: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 構建 LINE PAY 請求
     */
    private Map<String, Object> buildLinePayRequest(PaymentRequestDTO request) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", request.getAmount().intValue());
        requestBody.put("currency", request.getCurrency());
        requestBody.put("orderId", request.getOrderNumber());
        
        // 商品資訊
        Map<String, Object> productInfo = new HashMap<>();
        productInfo.put("productName", request.getProductName());
        
        List<Map<String, Object>> packages = new ArrayList<>();
        Map<String, Object> packageInfo = new HashMap<>();
        packageInfo.put("id", request.getOrderNumber());
        packageInfo.put("amount", request.getAmount().intValue());
        packageInfo.put("name", request.getProductName());
        
        List<Map<String, Object>> products = new ArrayList<>();
        Map<String, Object> product = new HashMap<>();
        product.put("name", request.getProductName());
        product.put("quantity", 1);
        product.put("price", request.getAmount().intValue());
        products.add(product);
        
        packageInfo.put("products", products);
        packages.add(packageInfo);
        requestBody.put("packages", packages);
        
        // 重定向 URLs
        Map<String, String> redirectUrls = new HashMap<>();
        redirectUrls.put("confirmUrl", linePayConfig.getConfirmUrl() + "?orderId=" + request.getOrderNumber());
        redirectUrls.put("cancelUrl", linePayConfig.getCancelUrl() + "?orderId=" + request.getOrderNumber());
        requestBody.put("redirectUrls", redirectUrls);
        
        return requestBody;
    }

    /**
     * 生成 LINE PAY HMAC 簽章
     */
    private String generateSignature(String channelSecret, String uri, String requestBody, String nonce) {
        try {
            String signatureData = channelSecret + uri + requestBody + nonce;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    channelSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(signatureData.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }

    /**
     * 解析 LINE PAY 回應
     */
    private PaymentResponseDTO parseLinePayResponse(String response, String orderNumber) {
        try {
            JsonNode root = objectMapper.readTree(response);
            String returnCode = root.path("returnCode").asText();
            
            PaymentResponseDTO.PaymentResponseDTOBuilder builder = PaymentResponseDTO.builder()
                    .gateway(PaymentGateway.LINE_PAY)
                    .orderNumber(orderNumber)
                    .rawResponse(response);
            
            if ("0000".equals(returnCode)) {
                JsonNode info = root.path("info");
                String transactionId = info.path("transactionId").asText();
                String paymentUrl = info.path("paymentUrl").path("web").asText();
                String appPaymentUrl = info.path("paymentUrl").path("app").asText();
                
                builder.status(PaymentGatewayStatus.INITIATED)
                        .transactionId(transactionId)
                        .paymentUrl(paymentUrl)
                        .webPaymentUrl(paymentUrl)
                        .appPaymentUrl(appPaymentUrl);
            } else {
                String returnMessage = root.path("returnMessage").asText();
                builder.status(PaymentGatewayStatus.FAILED)
                        .errorMessage("LINE PAY 錯誤: " + returnMessage);
            }
            
            return builder.build();
        } catch (Exception e) {
            log.error("Failed to parse LINE PAY response", e);
            return PaymentResponseDTO.builder()
                    .gateway(PaymentGateway.LINE_PAY)
                    .status(PaymentGatewayStatus.FAILED)
                    .orderNumber(orderNumber)
                    .errorMessage("解析 LINE PAY 回應失敗")
                    .rawResponse(response)
                    .build();
        }
    }

    /**
     * 解析 LINE PAY 確認回應
     */
    private PaymentResponseDTO parseLinePayConfirmResponse(String response, String orderNumber) {
        try {
            JsonNode root = objectMapper.readTree(response);
            String returnCode = root.path("returnCode").asText();
            
            PaymentResponseDTO.PaymentResponseDTOBuilder builder = PaymentResponseDTO.builder()
                    .gateway(PaymentGateway.LINE_PAY)
                    .orderNumber(orderNumber)
                    .rawResponse(response);
            
            if ("0000".equals(returnCode)) {
                builder.status(PaymentGatewayStatus.SUCCESS);
            } else {
                String returnMessage = root.path("returnMessage").asText();
                builder.status(PaymentGatewayStatus.FAILED)
                        .errorMessage("LINE PAY 確認失敗: " + returnMessage);
            }
            
            return builder.build();
        } catch (Exception e) {
            log.error("Failed to parse LINE PAY confirm response", e);
            return PaymentResponseDTO.builder()
                    .gateway(PaymentGateway.LINE_PAY)
                    .status(PaymentGatewayStatus.FAILED)
                    .orderNumber(orderNumber)
                    .errorMessage("解析 LINE PAY 確認回應失敗")
                    .rawResponse(response)
                    .build();
        }
    }

    /**
     * 解析 LINE PAY 查詢回應
     */
    private PaymentResponseDTO parseLinePayQueryResponse(String response, String transactionId) {
        try {
            JsonNode root = objectMapper.readTree(response);
            String returnCode = root.path("returnCode").asText();
            
            PaymentResponseDTO.PaymentResponseDTOBuilder builder = PaymentResponseDTO.builder()
                    .gateway(PaymentGateway.LINE_PAY)
                    .transactionId(transactionId)
                    .rawResponse(response);
            
            if ("0000".equals(returnCode)) {
                JsonNode info = root.path("info");
                String status = info.path("status").asText();
                
                PaymentGatewayStatus gatewayStatus = switch (status) {
                    case "AUTHORIZATION" -> PaymentGatewayStatus.SUCCESS;
                    case "VOIDED" -> PaymentGatewayStatus.CANCELLED;
                    case "EXPIRED" -> PaymentGatewayStatus.EXPIRED;
                    default -> PaymentGatewayStatus.PROCESSING;
                };
                
                builder.status(gatewayStatus);
            } else {
                String returnMessage = root.path("returnMessage").asText();
                builder.status(PaymentGatewayStatus.FAILED)
                        .errorMessage("LINE PAY 查詢失敗: " + returnMessage);
            }
            
            return builder.build();
        } catch (Exception e) {
            log.error("Failed to parse LINE PAY query response", e);
            return PaymentResponseDTO.builder()
                    .gateway(PaymentGateway.LINE_PAY)
                    .status(PaymentGatewayStatus.FAILED)
                    .transactionId(transactionId)
                    .errorMessage("解析 LINE PAY 查詢回應失敗")
                    .rawResponse(response)
                    .build();
        }
    }
}
