package com.info.ecommerce.modules.system.service;

import com.info.ecommerce.modules.system.dto.OperationLogDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 操作日誌異步服務
 * 用於異步保存操作日誌，避免阻塞主線程
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogAsyncService {

    private final OperationLogService operationLogService;

    /**
     * 異步保存操作日誌
     */
    @Async
    public void saveLogAsync(OperationLogDTO logDTO) {
        try {
            operationLogService.createLog(logDTO);
        } catch (Exception e) {
            log.error("保存操作日誌失敗: {}", e.getMessage(), e);
        }
    }
}

