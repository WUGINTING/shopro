package com.info.ecommerce.modules.payment.service;

import com.info.ecommerce.modules.payment.dto.PaymentConfirmDTO;
import com.info.ecommerce.modules.payment.dto.PaymentRequestDTO;
import com.info.ecommerce.modules.payment.dto.PaymentResponseDTO;

/**
 * 支付閘道服務介面
 */
public interface PaymentGatewayService {
    
    /**
     * 創建支付請求
     * 
     * @param request 支付請求資料
     * @return 支付回應 (包含支付 URL)
     */
    PaymentResponseDTO createPayment(PaymentRequestDTO request);
    
    /**
     * 確認支付
     * 
     * @param confirm 支付確認資料
     * @return 支付回應 (包含最終狀態)
     */
    PaymentResponseDTO confirmPayment(PaymentConfirmDTO confirm);
    
    /**
     * 查詢支付狀態
     * 
     * @param transactionId 支付閘道交易 ID
     * @return 支付回應 (包含目前狀態)
     */
    PaymentResponseDTO queryPayment(String transactionId);
    
    /**
     * 取消支付
     * 
     * @param transactionId 支付閘道交易 ID
     * @return 支付回應
     */
    PaymentResponseDTO cancelPayment(String transactionId);
}
