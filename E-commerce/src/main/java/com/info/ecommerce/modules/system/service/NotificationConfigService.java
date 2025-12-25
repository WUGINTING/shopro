package com.info.ecommerce.modules.system.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.system.dto.NotificationConfigDTO;
import com.info.ecommerce.modules.system.entity.NotificationConfig;
import com.info.ecommerce.modules.system.repository.NotificationConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知設定服務
 */
@Service
@RequiredArgsConstructor
public class NotificationConfigService {

    private final NotificationConfigRepository notificationConfigRepository;

    /**
     * 創建通知設定
     */
    @Transactional
    public NotificationConfigDTO createNotificationConfig(NotificationConfigDTO dto) {
        NotificationConfig config = new NotificationConfig();
        BeanUtils.copyProperties(dto, config, "id");
        config = notificationConfigRepository.save(config);
        return toDTO(config);
    }

    /**
     * 更新通知設定
     */
    @Transactional
    public NotificationConfigDTO updateNotificationConfig(Long id, NotificationConfigDTO dto) {
        NotificationConfig config = notificationConfigRepository.findById(id)
                .orElseThrow(() -> new BusinessException("通知設定不存在"));
        
        BeanUtils.copyProperties(dto, config, "id", "createdAt", "updatedAt");
        config = notificationConfigRepository.save(config);
        return toDTO(config);
    }

    /**
     * 取得通知設定詳情
     */
    public NotificationConfigDTO getNotificationConfig(Long id) {
        NotificationConfig config = notificationConfigRepository.findById(id)
                .orElseThrow(() -> new BusinessException("通知設定不存在"));
        return toDTO(config);
    }

    /**
     * 刪除通知設定
     */
    @Transactional
    public void deleteNotificationConfig(Long id) {
        if (!notificationConfigRepository.existsById(id)) {
            throw new BusinessException("通知設定不存在");
        }
        notificationConfigRepository.deleteById(id);
    }

    /**
     * 分頁查詢通知設定
     */
    public Page<NotificationConfigDTO> listNotificationConfigs(Pageable pageable) {
        return notificationConfigRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * 查詢已啟用的通知設定
     */
    public List<NotificationConfigDTO> listEnabledNotificationConfigs() {
        return notificationConfigRepository.findByEnabledOrderBySortOrderAsc(true)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 依通知類型查詢
     */
    public Page<NotificationConfigDTO> listByNotificationType(String notificationType, Pageable pageable) {
        return notificationConfigRepository.findByNotificationType(notificationType, pageable)
                .map(this::toDTO);
    }

    /**
     * 依事件類型查詢
     */
    public List<NotificationConfigDTO> listByEventType(String eventType) {
        return notificationConfigRepository.findByEventType(eventType)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 實體轉 DTO
     */
    private NotificationConfigDTO toDTO(NotificationConfig entity) {
        NotificationConfigDTO dto = new NotificationConfigDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
