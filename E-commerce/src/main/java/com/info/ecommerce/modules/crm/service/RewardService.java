package com.info.ecommerce.modules.crm.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.crm.dto.RewardConfigDTO;
import com.info.ecommerce.modules.crm.entity.Member;
import com.info.ecommerce.modules.crm.entity.RewardClaim;
import com.info.ecommerce.modules.crm.entity.RewardConfig;
import com.info.ecommerce.modules.crm.enums.PointType;
import com.info.ecommerce.modules.crm.repository.MemberRepository;
import com.info.ecommerce.modules.crm.repository.RewardClaimRepository;
import com.info.ecommerce.modules.crm.repository.RewardConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardConfigRepository rewardConfigRepository;
    private final RewardClaimRepository rewardClaimRepository;
    private final MemberRepository memberRepository;
    private final PointService pointService;

    @Transactional
    public RewardConfigDTO createRewardConfig(RewardConfigDTO dto) {
        RewardConfig rewardConfig = new RewardConfig();
        BeanUtils.copyProperties(dto, rewardConfig, "id");
        rewardConfig = rewardConfigRepository.save(rewardConfig);
        return toDTO(rewardConfig);
    }

    @Transactional
    public RewardConfigDTO updateRewardConfig(Long id, RewardConfigDTO dto) {
        RewardConfig rewardConfig = rewardConfigRepository.findById(id)
                .orElseThrow(() -> new BusinessException("獎勵設定不存在"));

        BeanUtils.copyProperties(dto, rewardConfig, "id", "createdAt", "updatedAt");
        rewardConfig = rewardConfigRepository.save(rewardConfig);
        return toDTO(rewardConfig);
    }

    public RewardConfigDTO getRewardConfig(Long id) {
        RewardConfig rewardConfig = rewardConfigRepository.findById(id)
                .orElseThrow(() -> new BusinessException("獎勵設定不存在"));
        return toDTO(rewardConfig);
    }

    @Transactional
    public void deleteRewardConfig(Long id) {
        if (!rewardConfigRepository.existsById(id)) {
            throw new BusinessException("獎勵設定不存在");
        }
        rewardConfigRepository.deleteById(id);
    }

    public Page<RewardConfigDTO> listRewardConfigs(Pageable pageable) {
        return rewardConfigRepository.findAll(pageable).map(this::toDTO);
    }

    public List<RewardConfigDTO> listEnabledRewardConfigs() {
        return rewardConfigRepository.findByEnabled(true)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void claimWelcomeReward(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("會員不存在"));

        List<RewardConfig> welcomeRewards = rewardConfigRepository
                .findByRewardTypeAndEnabled("WELCOME", true);

        for (RewardConfig reward : welcomeRewards) {
            if (rewardClaimRepository.existsByMemberIdAndRewardConfigId(memberId, reward.getId())) {
                continue; // 已領取過
            }

            RewardClaim claim = RewardClaim.builder()
                    .memberId(memberId)
                    .rewardConfigId(reward.getId())
                    .rewardType(reward.getRewardType())
                    .pointsReceived(reward.getRewardPoints())
                    .couponId(reward.getCouponId())
                    .build();
            rewardClaimRepository.save(claim);

            // 發放積點
            if (reward.getRewardPoints() != null && reward.getRewardPoints() > 0) {
                pointService.addPoints(
                    memberId, 
                    reward.getRewardPoints(), 
                    PointType.REWARD, 
                    "入會禮獎勵", 
                    null
                );
            }
        }
    }

    @Transactional
    public void claimBirthdayReward(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("會員不存在"));

        if (member.getBirthday() == null) {
            throw new BusinessException("會員未設定生日");
        }

        LocalDate today = LocalDate.now();
        LocalDate birthday = member.getBirthday();

        // 檢查是否為生日月
        if (today.getMonth() != birthday.getMonth()) {
            throw new BusinessException("目前不在生日月份");
        }

        List<RewardConfig> birthdayRewards = rewardConfigRepository
                .findByRewardTypeAndEnabled("BIRTHDAY", true);

        for (RewardConfig reward : birthdayRewards) {
            // 檢查今年是否已領取
            List<RewardClaim> thisYearClaims = rewardClaimRepository
                    .findByMemberIdAndRewardType(memberId, "BIRTHDAY")
                    .stream()
                    .filter(c -> c.getClaimedAt().getYear() == today.getYear())
                    .toList();

            if (!thisYearClaims.isEmpty()) {
                continue; // 今年已領取過
            }

            RewardClaim claim = RewardClaim.builder()
                    .memberId(memberId)
                    .rewardConfigId(reward.getId())
                    .rewardType(reward.getRewardType())
                    .pointsReceived(reward.getRewardPoints())
                    .couponId(reward.getCouponId())
                    .build();
            rewardClaimRepository.save(claim);

            // 發放積點
            if (reward.getRewardPoints() != null && reward.getRewardPoints() > 0) {
                pointService.addPoints(
                    memberId, 
                    reward.getRewardPoints(), 
                    PointType.REWARD, 
                    "生日禮獎勵", 
                    null
                );
            }
        }
    }

    public Page<RewardClaim> listMemberRewardClaims(Long memberId, Pageable pageable) {
        return rewardClaimRepository.findByMemberId(memberId, pageable);
    }

    private RewardConfigDTO toDTO(RewardConfig rewardConfig) {
        RewardConfigDTO dto = new RewardConfigDTO();
        BeanUtils.copyProperties(rewardConfig, dto);
        return dto;
    }
}
