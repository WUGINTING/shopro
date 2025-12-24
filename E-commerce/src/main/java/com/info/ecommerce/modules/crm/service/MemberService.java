package com.info.ecommerce.modules.crm.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.crm.dto.MemberDTO;
import com.info.ecommerce.modules.crm.entity.Member;
import com.info.ecommerce.modules.crm.enums.MemberStatus;
import com.info.ecommerce.modules.crm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

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

    private MemberDTO toDTO(Member member) {
        MemberDTO dto = new MemberDTO();
        BeanUtils.copyProperties(member, dto);
        return dto;
    }
}
