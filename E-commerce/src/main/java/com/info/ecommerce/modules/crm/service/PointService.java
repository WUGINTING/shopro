package com.info.ecommerce.modules.crm.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.crm.dto.PointBatchDTO;
import com.info.ecommerce.modules.crm.dto.PointRecordDTO;
import com.info.ecommerce.modules.crm.entity.Member;
import com.info.ecommerce.modules.crm.entity.PointRecord;
import com.info.ecommerce.modules.crm.enums.PointType;
import com.info.ecommerce.modules.crm.repository.MemberRepository;
import com.info.ecommerce.modules.crm.repository.PointRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRecordRepository pointRecordRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PointRecordDTO addPoints(Long memberId, Integer points, PointType pointType, String reason, Long orderId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("會員不存在"));

        member.setTotalPoints(member.getTotalPoints() + points);
        member.setAvailablePoints(member.getAvailablePoints() + points);
        memberRepository.save(member);

        PointRecord record = PointRecord.builder()
                .memberId(memberId)
                .pointType(pointType)
                .points(points)
                .balanceAfter(member.getAvailablePoints())
                .orderId(orderId)
                .reason(reason)
                .build();
        record = pointRecordRepository.save(record);

        return toDTO(record);
    }

    @Transactional
    public PointRecordDTO deductPoints(Long memberId, Integer points, PointType pointType, String reason) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("會員不存在"));

        if (member.getAvailablePoints() < points) {
            throw new BusinessException("可用積點不足");
        }

        member.setAvailablePoints(member.getAvailablePoints() - points);
        memberRepository.save(member);

        PointRecord record = PointRecord.builder()
                .memberId(memberId)
                .pointType(pointType)
                .points(-points)
                .balanceAfter(member.getAvailablePoints())
                .reason(reason)
                .build();
        record = pointRecordRepository.save(record);

        return toDTO(record);
    }

    @Transactional
    public List<PointRecordDTO> batchGrantPoints(PointBatchDTO batchDTO) {
        List<PointRecordDTO> results = new ArrayList<>();
        
        for (Long memberId : batchDTO.getMemberIds()) {
            try {
                PointRecordDTO record = addPoints(
                    memberId, 
                    batchDTO.getPoints(), 
                    PointType.BATCH_GRANT, 
                    batchDTO.getReason(), 
                    null
                );
                results.add(record);
            } catch (BusinessException e) {
                // 記錄錯誤但繼續處理其他會員
            }
        }
        
        return results;
    }

    public Page<PointRecordDTO> listPointRecords(Long memberId, Pageable pageable) {
        return pointRecordRepository.findByMemberId(memberId, pageable).map(this::toDTO);
    }

    public Page<PointRecordDTO> listAllPointRecords(Pageable pageable) {
        return pointRecordRepository.findAll(pageable).map(this::toDTO);
    }

    public PointRecordDTO getPointRecord(Long id) {
        PointRecord record = pointRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException("積點紀錄不存在"));
        return toDTO(record);
    }

    public Integer getMemberPointBalance(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("會員不存在"));
        return member.getAvailablePoints();
    }

    @Transactional
    public void expirePoints(Long memberId, LocalDateTime expiryDate) {
        // 找出所有已過期的積點
        List<PointRecord> expiredRecords = pointRecordRepository.findByMemberIdAndPointType(
            memberId, PointType.EARN
        ).stream()
        .filter(r -> r.getExpiresAt() != null && r.getExpiresAt().isBefore(expiryDate))
        .toList();

        int totalExpiredPoints = expiredRecords.stream()
                .mapToInt(PointRecord::getPoints)
                .sum();

        if (totalExpiredPoints > 0) {
            deductPoints(memberId, totalExpiredPoints, PointType.EXPIRE, "積點已過期");
        }
    }

    private PointRecordDTO toDTO(PointRecord record) {
        PointRecordDTO dto = new PointRecordDTO();
        BeanUtils.copyProperties(record, dto);
        return dto;
    }
}
