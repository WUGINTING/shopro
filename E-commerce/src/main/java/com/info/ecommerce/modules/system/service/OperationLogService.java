package com.info.ecommerce.modules.system.service;

import com.info.ecommerce.modules.system.dto.OperationLogDTO;
import com.info.ecommerce.modules.system.entity.OperationLog;
import com.info.ecommerce.modules.system.repository.OperationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 操作日誌服務
 */
@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final OperationLogRepository operationLogRepository;

    /**
     * 記錄操作日誌
     */
    @Transactional
    public OperationLogDTO createLog(OperationLogDTO dto) {
        OperationLog log = new OperationLog();
        BeanUtils.copyProperties(dto, log, "id");
        log = operationLogRepository.save(log);
        return toDTO(log);
    }

    /**
     * 取得日誌詳情
     */
    public OperationLogDTO getLog(Long id) {
        OperationLog log = operationLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("日誌不存在"));
        return toDTO(log);
    }

    /**
     * 分頁查詢所有日誌
     */
    public Page<OperationLogDTO> listLogs(Pageable pageable) {
        return operationLogRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * 依用戶查詢日誌
     */
    public Page<OperationLogDTO> listByUser(Long userId, Pageable pageable) {
        return operationLogRepository.findByUserId(userId, pageable).map(this::toDTO);
    }

    /**
     * 依操作類型查詢
     */
    public Page<OperationLogDTO> listByOperationType(String operationType, Pageable pageable) {
        return operationLogRepository.findByOperationType(operationType, pageable).map(this::toDTO);
    }

    /**
     * 依模組查詢
     */
    public Page<OperationLogDTO> listByModule(String module, Pageable pageable) {
        return operationLogRepository.findByModule(module, pageable).map(this::toDTO);
    }

    /**
     * 查詢敏感操作
     */
    public Page<OperationLogDTO> listSensitiveOperations(Pageable pageable) {
        return operationLogRepository.findBySensitive(true, pageable).map(this::toDTO);
    }

    /**
     * 依時間範圍查詢
     */
    public Page<OperationLogDTO> listByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return operationLogRepository.findByCreatedAtBetween(start, end, pageable).map(this::toDTO);
    }

    /**
     * 依用戶和模組查詢
     */
    public Page<OperationLogDTO> listByUserAndModule(Long userId, String module, Pageable pageable) {
        return operationLogRepository.findByUserIdAndModule(userId, module, pageable).map(this::toDTO);
    }

    /**
     * 實體轉 DTO
     */
    private OperationLogDTO toDTO(OperationLog entity) {
        OperationLogDTO dto = new OperationLogDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
