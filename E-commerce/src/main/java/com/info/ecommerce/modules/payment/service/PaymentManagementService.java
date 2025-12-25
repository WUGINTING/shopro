package com.info.ecommerce.modules.payment.service;

import com.info.ecommerce.modules.payment.dto.PaymentStatisticsDTO;
import com.info.ecommerce.modules.payment.dto.PaymentTransactionDTO;
import com.info.ecommerce.modules.payment.entity.PaymentGatewayTransaction;
import com.info.ecommerce.modules.payment.entity.PaymentSetting;
import com.info.ecommerce.modules.payment.enums.PaymentGateway;
import com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus;
import com.info.ecommerce.modules.payment.repository.PaymentGatewayTransactionRepository;
import com.info.ecommerce.modules.payment.repository.PaymentSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 支付管理服務
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentManagementService {

    private final PaymentGatewayTransactionRepository transactionRepository;
    private final PaymentSettingRepository settingRepository;

    /**
     * 取得支付統計資料
     */
    @Transactional(readOnly = true)
    public PaymentStatisticsDTO getStatistics() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
        
        // 今日統計
        Long todaySuccessCount = transactionRepository.countByStatusAndCreatedAtAfter(
            PaymentGatewayStatus.SUCCESS, todayStart);
        Long todayTotalCount = transactionRepository.countByStatusAndCreatedAtAfter(
            null, todayStart);
        BigDecimal todayAmount = transactionRepository.sumAmountByStatusAndCreatedAtAfter(
            PaymentGatewayStatus.SUCCESS, todayStart);
        
        // 本月統計
        Long monthSuccessCount = transactionRepository.countByStatusAndCreatedAtAfter(
            PaymentGatewayStatus.SUCCESS, monthStart);
        BigDecimal monthAmount = transactionRepository.sumAmountByStatusAndCreatedAtAfter(
            PaymentGatewayStatus.SUCCESS, monthStart);
        
        // 成功率計算
        Double todaySuccessRate = todayTotalCount > 0 
            ? (todaySuccessCount.doubleValue() / todayTotalCount.doubleValue() * 100) 
            : 0.0;
        
        // 退款統計（簡化版，實際應該有專門的退款表）
        Long todayRefundCount = transactionRepository.countByStatusAndCreatedAtAfter(
            PaymentGatewayStatus.CANCELLED, todayStart);
        Long monthRefundCount = transactionRepository.countByStatusAndCreatedAtAfter(
            PaymentGatewayStatus.CANCELLED, monthStart);
        
        // 各閘道佔比
        List<Object[]> gatewayStats = transactionRepository.getGatewayStatistics(monthStart);
        BigDecimal totalAmount = monthAmount != null ? monthAmount : BigDecimal.ZERO;
        
        Map<String, PaymentStatisticsDTO.GatewayShare> gatewayShares = new HashMap<>();
        for (Object[] stat : gatewayStats) {
            PaymentGateway gateway = (PaymentGateway) stat[0];
            Long count = ((Number) stat[1]).longValue();
            BigDecimal amount = (BigDecimal) stat[2];
            
            Double percentage = totalAmount.compareTo(BigDecimal.ZERO) > 0
                ? amount.divide(totalAmount, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0.0;
            
            gatewayShares.put(gateway.name(), PaymentStatisticsDTO.GatewayShare.builder()
                .gateway(gateway.getDisplayName())
                .count(count)
                .amount(amount)
                .percentage(percentage)
                .build());
        }
        
        return PaymentStatisticsDTO.builder()
            .todayAmount(todayAmount != null ? todayAmount : BigDecimal.ZERO)
            .todayCount(todaySuccessCount)
            .todaySuccessRate(todaySuccessRate)
            .monthAmount(monthAmount != null ? monthAmount : BigDecimal.ZERO)
            .monthCount(monthSuccessCount)
            .refundStatistics(PaymentStatisticsDTO.RefundStatistics.builder()
                .todayRefundCount(todayRefundCount)
                .todayRefundAmount(BigDecimal.ZERO) // 簡化版
                .monthRefundCount(monthRefundCount)
                .monthRefundAmount(BigDecimal.ZERO) // 簡化版
                .build())
            .gatewayShares(gatewayShares)
            .build();
    }

    /**
     * 搜尋交易記錄
     */
    @Transactional(readOnly = true)
    public List<PaymentTransactionDTO> searchTransactions(String keyword, PaymentGateway gateway, 
                                                           PaymentGatewayStatus status, 
                                                           LocalDateTime startDate, LocalDateTime endDate) {
        List<PaymentGatewayTransaction> transactions;
        
        if (keyword != null && !keyword.isEmpty()) {
            transactions = transactionRepository.searchByKeyword(keyword);
        } else if (startDate != null && endDate != null) {
            transactions = transactionRepository.findByCreatedAtBetween(startDate, endDate);
        } else if (gateway != null && status != null) {
            transactions = transactionRepository.findByGatewayAndStatus(gateway, status);
        } else {
            transactions = transactionRepository.findAll();
        }
        
        return transactions.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 取得交易詳情
     */
    @Transactional(readOnly = true)
    public PaymentTransactionDTO getTransaction(Long id) {
        return transactionRepository.findById(id)
            .map(this::convertToDTO)
            .orElseThrow(() -> new RuntimeException("交易記錄不存在"));
    }

    /**
     * 儲存交易記錄
     */
    @Transactional
    public PaymentGatewayTransaction saveTransaction(PaymentGatewayTransaction transaction) {
        return transactionRepository.save(transaction);
    }

    /**
     * 取得所有支付設定
     */
    @Transactional(readOnly = true)
    public List<PaymentSetting> getAllSettings() {
        return settingRepository.findAllByOrderBySortOrderAsc();
    }

    /**
     * 取得單一支付設定
     */
    @Transactional(readOnly = true)
    public PaymentSetting getSetting(PaymentGateway gateway) {
        return settingRepository.findByGateway(gateway)
            .orElse(null);
    }

    /**
     * 更新支付設定
     */
    @Transactional
    public PaymentSetting updateSetting(PaymentSetting setting) {
        return settingRepository.save(setting);
    }

    /**
     * 檢查閘道是否可用
     */
    @Transactional(readOnly = true)
    public boolean isGatewayAvailable(PaymentGateway gateway) {
        return settingRepository.findByGateway(gateway)
            .map(setting -> setting.getEnabled() && !setting.getMaintenanceMode())
            .orElse(false);
    }

    /**
     * 轉換為 DTO
     */
    private PaymentTransactionDTO convertToDTO(PaymentGatewayTransaction transaction) {
        PaymentTransactionDTO dto = new PaymentTransactionDTO();
        BeanUtils.copyProperties(transaction, dto);
        return dto;
    }
}
