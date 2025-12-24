package com.info.ecommerce.modules.crm.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.crm.dto.MemberGroupDTO;
import com.info.ecommerce.modules.crm.entity.MemberGroup;
import com.info.ecommerce.modules.crm.entity.MemberGroupMapping;
import com.info.ecommerce.modules.crm.repository.MemberGroupMappingRepository;
import com.info.ecommerce.modules.crm.repository.MemberGroupRepository;
import com.info.ecommerce.modules.crm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberGroupService {

    private final MemberGroupRepository memberGroupRepository;
    private final MemberGroupMappingRepository memberGroupMappingRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public MemberGroupDTO createMemberGroup(MemberGroupDTO dto) {
        if (memberGroupRepository.existsByName(dto.getName())) {
            throw new BusinessException("群組名稱已存在");
        }

        MemberGroup memberGroup = new MemberGroup();
        BeanUtils.copyProperties(dto, memberGroup, "id");
        memberGroup = memberGroupRepository.save(memberGroup);
        return toDTO(memberGroup);
    }

    @Transactional
    public MemberGroupDTO updateMemberGroup(Long id, MemberGroupDTO dto) {
        MemberGroup memberGroup = memberGroupRepository.findById(id)
                .orElseThrow(() -> new BusinessException("會員群組不存在"));

        if (!memberGroup.getName().equals(dto.getName()) && memberGroupRepository.existsByName(dto.getName())) {
            throw new BusinessException("群組名稱已存在");
        }

        BeanUtils.copyProperties(dto, memberGroup, "id", "createdAt", "updatedAt");
        memberGroup = memberGroupRepository.save(memberGroup);
        return toDTO(memberGroup);
    }

    public MemberGroupDTO getMemberGroup(Long id) {
        MemberGroup memberGroup = memberGroupRepository.findById(id)
                .orElseThrow(() -> new BusinessException("會員群組不存在"));
        return toDTO(memberGroup);
    }

    @Transactional
    public void deleteMemberGroup(Long id) {
        if (!memberGroupRepository.existsById(id)) {
            throw new BusinessException("會員群組不存在");
        }
        memberGroupRepository.deleteById(id);
    }

    public Page<MemberGroupDTO> listMemberGroups(Pageable pageable) {
        return memberGroupRepository.findAll(pageable).map(this::toDTO);
    }

    public List<MemberGroupDTO> listEnabledMemberGroups() {
        return memberGroupRepository.findByEnabled(true)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addMemberToGroup(Long memberId, Long groupId) {
        if (!memberRepository.existsById(memberId)) {
            throw new BusinessException("會員不存在");
        }
        if (!memberGroupRepository.existsById(groupId)) {
            throw new BusinessException("會員群組不存在");
        }
        if (memberGroupMappingRepository.existsByMemberIdAndGroupId(memberId, groupId)) {
            throw new BusinessException("會員已在該群組中");
        }

        MemberGroupMapping mapping = MemberGroupMapping.builder()
                .memberId(memberId)
                .groupId(groupId)
                .build();
        memberGroupMappingRepository.save(mapping);
    }

    @Transactional
    public void removeMemberFromGroup(Long memberId, Long groupId) {
        if (!memberGroupMappingRepository.existsByMemberIdAndGroupId(memberId, groupId)) {
            throw new BusinessException("會員不在該群組中");
        }
        memberGroupMappingRepository.deleteByMemberIdAndGroupId(memberId, groupId);
    }

    public List<Long> getMemberGroups(Long memberId) {
        return memberGroupMappingRepository.findByMemberId(memberId)
                .stream()
                .map(MemberGroupMapping::getGroupId)
                .collect(Collectors.toList());
    }

    public List<Long> getGroupMembers(Long groupId) {
        return memberGroupMappingRepository.findByGroupId(groupId)
                .stream()
                .map(MemberGroupMapping::getMemberId)
                .collect(Collectors.toList());
    }

    private MemberGroupDTO toDTO(MemberGroup memberGroup) {
        MemberGroupDTO dto = new MemberGroupDTO();
        BeanUtils.copyProperties(memberGroup, dto);
        return dto;
    }
}
