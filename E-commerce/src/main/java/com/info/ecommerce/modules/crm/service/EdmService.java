package com.info.ecommerce.modules.crm.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.crm.dto.EdmCampaignDTO;
import com.info.ecommerce.modules.crm.entity.EdmCampaign;
import com.info.ecommerce.modules.crm.entity.EdmSendLog;
import com.info.ecommerce.modules.crm.entity.Member;
import com.info.ecommerce.modules.crm.enums.EdmStatus;
import com.info.ecommerce.modules.crm.repository.EdmCampaignRepository;
import com.info.ecommerce.modules.crm.repository.EdmSendLogRepository;
import com.info.ecommerce.modules.crm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EdmService {

    private final EdmCampaignRepository edmCampaignRepository;
    private final EdmSendLogRepository edmSendLogRepository;
    private final MemberRepository memberRepository;
    private final MemberGroupService memberGroupService;

    @Transactional
    public EdmCampaignDTO createEdmCampaign(EdmCampaignDTO dto) {
        EdmCampaign edmCampaign = new EdmCampaign();
        BeanUtils.copyProperties(dto, edmCampaign, "id");
        edmCampaign = edmCampaignRepository.save(edmCampaign);
        return toDTO(edmCampaign);
    }

    @Transactional
    public EdmCampaignDTO updateEdmCampaign(Long id, EdmCampaignDTO dto) {
        EdmCampaign edmCampaign = edmCampaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("EDM 活動不存在"));

        if (edmCampaign.getStatus() == EdmStatus.SENT) {
            throw new BusinessException("已發送的 EDM 活動無法修改");
        }

        BeanUtils.copyProperties(dto, edmCampaign, "id", "createdAt", "updatedAt", "sentAt", "totalSent", "successCount", "failureCount");
        edmCampaign = edmCampaignRepository.save(edmCampaign);
        return toDTO(edmCampaign);
    }

    public EdmCampaignDTO getEdmCampaign(Long id) {
        EdmCampaign edmCampaign = edmCampaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("EDM 活動不存在"));
        return toDTO(edmCampaign);
    }

    @Transactional
    public void deleteEdmCampaign(Long id) {
        EdmCampaign edmCampaign = edmCampaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("EDM 活動不存在"));

        if (edmCampaign.getStatus() == EdmStatus.SENT || edmCampaign.getStatus() == EdmStatus.SENDING) {
            throw new BusinessException("已發送或發送中的 EDM 活動無法刪除");
        }

        edmCampaignRepository.deleteById(id);
    }

    public Page<EdmCampaignDTO> listEdmCampaigns(Pageable pageable) {
        return edmCampaignRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<EdmCampaignDTO> listEdmCampaignsByStatus(EdmStatus status, Pageable pageable) {
        return edmCampaignRepository.findByStatus(status, pageable).map(this::toDTO);
    }

    @Transactional
    public EdmCampaignDTO scheduleEdmCampaign(Long id, LocalDateTime scheduledAt) {
        EdmCampaign edmCampaign = edmCampaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("EDM 活動不存在"));

        if (edmCampaign.getStatus() != EdmStatus.DRAFT) {
            throw new BusinessException("只有草稿狀態的 EDM 活動可以排程");
        }

        edmCampaign.setScheduledAt(scheduledAt);
        edmCampaign.setStatus(EdmStatus.SCHEDULED);
        edmCampaign = edmCampaignRepository.save(edmCampaign);
        return toDTO(edmCampaign);
    }

    @Transactional
    public EdmCampaignDTO sendEdmCampaign(Long id) {
        EdmCampaign edmCampaign = edmCampaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("EDM 活動不存在"));

        if (edmCampaign.getStatus() == EdmStatus.SENT) {
            throw new BusinessException("EDM 活動已發送");
        }

        edmCampaign.setStatus(EdmStatus.SENDING);
        edmCampaign = edmCampaignRepository.save(edmCampaign);

        // 取得目標會員
        List<Member> targetMembers;
        if (edmCampaign.getTargetGroupId() != null) {
            List<Long> memberIds = memberGroupService.getGroupMembers(edmCampaign.getTargetGroupId());
            targetMembers = memberRepository.findAllById(memberIds);
        } else {
            targetMembers = memberRepository.findAll();
        }

        int successCount = 0;
        int failureCount = 0;

        // 模擬發送郵件（實際應用中應整合郵件服務）
        for (Member member : targetMembers) {
            try {
                // 這裡應該調用實際的郵件發送服務
                EdmSendLog log = EdmSendLog.builder()
                        .campaignId(id)
                        .memberId(member.getId())
                        .recipientEmail(member.getEmail())
                        .success(true)
                        .build();
                edmSendLogRepository.save(log);
                successCount++;
            } catch (Exception e) {
                EdmSendLog log = EdmSendLog.builder()
                        .campaignId(id)
                        .memberId(member.getId())
                        .recipientEmail(member.getEmail())
                        .success(false)
                        .errorMessage(e.getMessage())
                        .build();
                edmSendLogRepository.save(log);
                failureCount++;
            }
        }

        edmCampaign.setStatus(EdmStatus.SENT);
        edmCampaign.setSentAt(LocalDateTime.now());
        edmCampaign.setTotalSent(targetMembers.size());
        edmCampaign.setSuccessCount(successCount);
        edmCampaign.setFailureCount(failureCount);
        edmCampaign = edmCampaignRepository.save(edmCampaign);

        return toDTO(edmCampaign);
    }

    @Transactional
    public EdmCampaignDTO cancelEdmCampaign(Long id) {
        EdmCampaign edmCampaign = edmCampaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("EDM 活動不存在"));

        if (edmCampaign.getStatus() == EdmStatus.SENT) {
            throw new BusinessException("已發送的 EDM 活動無法取消");
        }

        edmCampaign.setStatus(EdmStatus.CANCELLED);
        edmCampaign = edmCampaignRepository.save(edmCampaign);
        return toDTO(edmCampaign);
    }

    public Page<EdmSendLog> getEdmSendLogs(Long campaignId, Pageable pageable) {
        return edmSendLogRepository.findByCampaignId(campaignId, pageable);
    }

    private EdmCampaignDTO toDTO(EdmCampaign edmCampaign) {
        EdmCampaignDTO dto = new EdmCampaignDTO();
        BeanUtils.copyProperties(edmCampaign, dto);
        return dto;
    }
}
