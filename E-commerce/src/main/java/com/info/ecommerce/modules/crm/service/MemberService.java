package com.info.ecommerce.modules.crm.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.crm.dto.MemberDTO;
import com.info.ecommerce.modules.crm.entity.Member;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

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

        BeanUtils.copyProperties(dto, member, "id", "createdAt", "updatedAt", "registeredAt");
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
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException("會員不存在"));
        member.setTotalPoints(member.getTotalPoints() + points);
        member.setAvailablePoints(member.getAvailablePoints() + points);
        member = memberRepository.save(member);
        return toDTO(member);
    }

    @Transactional
    public MemberDTO deductPoints(Long id, Integer points) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException("會員不存在"));
        if (member.getAvailablePoints() < points) {
            throw new BusinessException("可用積點不足");
        }
        member.setAvailablePoints(member.getAvailablePoints() - points);
        member = memberRepository.save(member);
        return toDTO(member);
    }

    /**
     * 增加客戶總消費金額
     * 當訂單完成或付款時調用此方法
     * @param memberId 會員 ID（對應訂單的 customerId）
     * @param amount 訂單金額
     */
    @Transactional
    public void addTotalSpent(Long memberId, BigDecimal amount) {
        if (memberId == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Warning: addTotalSpent called with invalid parameters - memberId: " + memberId + ", amount: " + amount);
            return;
        }
        
        memberRepository.findById(memberId).ifPresentOrElse(
            member -> {
                if (member.getTotalSpent() == null) {
                    member.setTotalSpent(BigDecimal.ZERO);
                }
                BigDecimal oldTotal = member.getTotalSpent();
                member.setTotalSpent(member.getTotalSpent().add(amount));
                memberRepository.save(member);
                System.out.println("Successfully updated member " + memberId + " total spent: " + oldTotal + " -> " + member.getTotalSpent());
            },
            () -> {
                System.err.println("Error: Member not found with ID: " + memberId);
            }
        );
    }

    /**
     * 重新計算客戶的總消費金額（從所有已付款或已完成的訂單中計算）
     */
    @Transactional
    public void recalculateTotalSpent(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("會員不存在"));
        
        // 查詢該客戶所有已付款或已完成的訂單
        List<Order> paidOrCompletedOrders = orderRepository.findByCustomerId(memberId, Pageable.unpaged())
                .getContent()
                .stream()
                .filter(order -> order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.COMPLETED)
                .collect(Collectors.toList());
        
        // 計算總消費
        BigDecimal totalSpent = paidOrCompletedOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        member.setTotalSpent(totalSpent);
        memberRepository.save(member);
    }

    /**
     * 重新計算所有客戶的總消費金額
     */
    @Transactional
    public void recalculateAllMembersTotalSpent() {
        // 查詢所有已付款或已完成的訂單
        List<Order> paidOrCompletedOrders = orderRepository.findAll()
                .stream()
                .filter(order -> order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.COMPLETED)
                .collect(Collectors.toList());
        
        // 按客戶 ID 分組並計算總消費
        Map<Long, BigDecimal> customerTotalSpent = paidOrCompletedOrders.stream()
                .collect(Collectors.groupingBy(
                    Order::getCustomerId,
                    Collectors.reducing(
                        BigDecimal.ZERO,
                        Order::getTotalAmount,
                        BigDecimal::add
                    )
                ));
        
        // 更新所有會員的總消費
        memberRepository.findAll().forEach(member -> {
            BigDecimal totalSpent = customerTotalSpent.getOrDefault(member.getId(), BigDecimal.ZERO);
            member.setTotalSpent(totalSpent);
            memberRepository.save(member);
        });
    }

    private MemberDTO toDTO(Member member) {
        MemberDTO dto = new MemberDTO();
        BeanUtils.copyProperties(member, dto);
        return dto;
    }
}
