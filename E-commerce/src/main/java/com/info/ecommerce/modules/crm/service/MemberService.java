package com.info.ecommerce.modules.crm.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.crm.dto.MemberDTO;
import com.info.ecommerce.modules.crm.entity.Member;
import com.info.ecommerce.modules.crm.entity.PointRecord;
import com.info.ecommerce.modules.crm.enums.PointType;
import com.info.ecommerce.modules.crm.enums.MemberStatus;
import com.info.ecommerce.modules.crm.repository.MemberRepository;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final com.info.ecommerce.modules.crm.repository.PointRecordRepository pointRecordRepository;
    private final com.info.ecommerce.modules.crm.repository.MemberLevelRepository memberLevelRepository;

    @Transactional
    public MemberDTO createMember(MemberDTO dto) {
        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("電子郵件已存在");
        }

        Member member = new Member();
        BeanUtils.copyProperties(dto, member, "id");
        member = memberRepository.save(member);
        return toDTO(member);
    }

    @Transactional
    public MemberDTO updateMember(Long id, MemberDTO dto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException("會員不存在"));

        if (!member.getEmail().equals(dto.getEmail()) && memberRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("電子郵件已存在");
        }

        // 積點、累積消費、等級、行銷訂閱由各自的流程維護（積點明細、訂單、等級調整、顧客本人），
        // 編輯基本資料時不覆寫，避免以畫面載入時的舊值蓋掉期間的變動
        BeanUtils.copyProperties(dto, member, "id", "createdAt", "updatedAt", "registeredAt", "lastLoginAt",
                "totalPoints", "availablePoints", "totalSpent", "levelId", "marketingOptIn");
        member = memberRepository.save(member);
        return toDTO(member);
    }

    public MemberDTO getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException("會員不存在"));
        return toDTO(member);
    }

    public MemberDTO getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("會員不存在"));
        return toDTO(member);
    }

    @Transactional
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new BusinessException("會員不存在");
        }
        memberRepository.deleteById(id);
    }

    public Page<MemberDTO> listMembers(Pageable pageable) {
        return memberRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<MemberDTO> listMembersByStatus(MemberStatus status, Pageable pageable) {
        return memberRepository.findByStatus(status, pageable).map(this::toDTO);
    }

    public Page<MemberDTO> listMembersByLevel(Long levelId, Pageable pageable) {
        return memberRepository.findByLevelId(levelId, pageable).map(this::toDTO);
    }

    public Page<MemberDTO> searchMembers(String keyword, Pageable pageable) {
        return memberRepository.findByNameContaining(keyword, pageable).map(this::toDTO);
    }

    @Transactional
    public MemberDTO updateMemberStatus(Long id, MemberStatus status) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException("會員不存在"));
        member.setStatus(status);
        member = memberRepository.save(member);
        return toDTO(member);
    }

    @Transactional
    public MemberDTO updateMemberLevel(Long id, Long levelId) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException("會員不存在"));
        member.setLevelId(levelId);
        member = memberRepository.save(member);
        return toDTO(member);
    }

    @Transactional
    public MemberDTO addPoints(Long id, Integer points) {
        if (points == null || points <= 0) {
            throw new BusinessException("積點需大於 0");
        }
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException("會員不存在"));
        member.setTotalPoints((member.getTotalPoints() == null ? 0 : member.getTotalPoints()) + points);
        member.setAvailablePoints((member.getAvailablePoints() == null ? 0 : member.getAvailablePoints()) + points);
        member = memberRepository.save(member);
        // 記錄到積點明細，積點紀錄頁才看得到
        pointRecordRepository.save(PointRecord.builder()
                .memberId(id)
                .pointType(PointType.ADJUST)
                .points(points)
                .balanceAfter(member.getAvailablePoints())
                .reason("後台手動增加")
                .build());
        return toDTO(member);
    }

    @Transactional
    public MemberDTO deductPoints(Long id, Integer points) {
        if (points == null || points <= 0) {
            throw new BusinessException("積點需大於 0");
        }
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException("會員不存在"));
        int available = member.getAvailablePoints() == null ? 0 : member.getAvailablePoints();
        if (available < points) {
            throw new BusinessException("可用積點不足");
        }
        member.setAvailablePoints(available - points);
        member = memberRepository.save(member);
        pointRecordRepository.save(PointRecord.builder()
                .memberId(id)
                .pointType(PointType.ADJUST)
                .points(-points)
                .balanceAfter(member.getAvailablePoints())
                .reason("後台手動扣除")
                .build());
        return toDTO(member);
    }

    /** 計入累計消費的訂單狀態：已付款、處理中（已出貨）、已完成；取消或退款的訂單不計入 */
    public static final Set<OrderStatus> SPENDING_STATUSES =
            EnumSet.of(OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.COMPLETED);

    /**
     * 依訂單資料重新計算會員累計消費。
     * 訂單狀態每次變動後呼叫，因此重複付款通知、出貨再完成、取消或退款都不會造成重複累計或漏扣。
     * 會員不存在（例如訪客訂單）時略過。
     */
    @Transactional
    public void syncTotalSpent(Long memberId) {
        if (memberId == null) {
            return;
        }
        memberRepository.findById(memberId).ifPresent(member -> {
            member.setTotalSpent(orderRepository.sumTotalAmountByCustomerIdAndStatusIn(memberId, SPENDING_STATUSES));
            upgradeLevel(member, memberLevelRepository.findByEnabledOrderByLevelOrderAsc(true));
            memberRepository.save(member);
        });
    }

    /**
     * 累積消費達門檻自動升級到符合的最高等級。只升不降：退款或後台手動調高的等級不會被自動調低
     */
    /** 等級高低：以等級順序比較，順序相同或未設定時以消費門檻比較 */
    private static boolean ranksHigher(com.info.ecommerce.modules.crm.entity.MemberLevel candidate,
                                       com.info.ecommerce.modules.crm.entity.MemberLevel current,
                                       java.util.Comparator<com.info.ecommerce.modules.crm.entity.MemberLevel> byThreshold) {
        if (candidate.getLevelOrder() != null && current.getLevelOrder() != null
                && !candidate.getLevelOrder().equals(current.getLevelOrder())) {
            return candidate.getLevelOrder() > current.getLevelOrder();
        }
        // 等級順序相同（例如都留預設值 1）或未設定時，以消費門檻判斷
        return byThreshold.compare(candidate, current) > 0;
    }

    private void upgradeLevel(Member member, List<com.info.ecommerce.modules.crm.entity.MemberLevel> enabledLevels) {
        BigDecimal spent = member.getTotalSpent() != null ? member.getTotalSpent() : BigDecimal.ZERO;
        java.util.Comparator<com.info.ecommerce.modules.crm.entity.MemberLevel> byThreshold = java.util.Comparator.comparing(
                level -> level.getMinSpendAmount() != null ? level.getMinSpendAmount() : BigDecimal.ZERO);
        enabledLevels.stream()
                .filter(level -> (level.getMinSpendAmount() != null ? level.getMinSpendAmount() : BigDecimal.ZERO).compareTo(spent) <= 0)
                .max(byThreshold)
                .ifPresent(earned -> {
                    // 目前等級（含已停用或後台手動指定的等級）以等級順序比較，較高者保留，不會被自動調低
                    var current = member.getLevelId() == null ? java.util.Optional.<com.info.ecommerce.modules.crm.entity.MemberLevel>empty()
                            : enabledLevels.stream().filter(level -> level.getId().equals(member.getLevelId())).findFirst()
                                    .or(() -> memberLevelRepository.findById(member.getLevelId()));
                    if (current.isEmpty() || ranksHigher(earned, current.get(), byThreshold)) {
                        member.setLevelId(earned.getId());
                    }
                });
    }

    /**
     * 重新計算客戶的總消費金額
     */
    @Transactional
    public void recalculateTotalSpent(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new BusinessException("會員不存在"));
        syncTotalSpent(memberId);
    }

    /**
     * 重新計算所有客戶的總消費金額
     */
    @Transactional
    public void recalculateAllMembersTotalSpent() {
        Map<Long, BigDecimal> customerTotalSpent = orderRepository.findAll()
                .stream()
                .filter(order -> order.getCustomerId() != null && SPENDING_STATUSES.contains(order.getStatus()))
                .collect(Collectors.groupingBy(
                    Order::getCustomerId,
                    Collectors.reducing(BigDecimal.ZERO, Order::getTotalAmount, BigDecimal::add)
                ));

        List<com.info.ecommerce.modules.crm.entity.MemberLevel> enabledLevels = memberLevelRepository.findByEnabledOrderByLevelOrderAsc(true);
        memberRepository.findAll().forEach(member -> {
            member.setTotalSpent(customerTotalSpent.getOrDefault(member.getId(), BigDecimal.ZERO));
            upgradeLevel(member, enabledLevels);
            memberRepository.save(member);
        });
    }

    private MemberDTO toDTO(Member member) {
        MemberDTO dto = new MemberDTO();
        BeanUtils.copyProperties(member, dto);
        return dto;
    }
}
