package com.info.ecommerce.modules.crm.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.crm.dto.MemberLevelDTO;
import com.info.ecommerce.modules.crm.entity.MemberLevel;
import com.info.ecommerce.modules.crm.repository.MemberLevelRepository;
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
public class MemberLevelService {

    private final MemberLevelRepository memberLevelRepository;

    @Transactional
    public MemberLevelDTO createMemberLevel(MemberLevelDTO dto) {
        if (memberLevelRepository.existsByName(dto.getName())) {
            throw new BusinessException("等級名稱已存在");
        }

        MemberLevel memberLevel = new MemberLevel();
        BeanUtils.copyProperties(dto, memberLevel, "id");
        memberLevel = memberLevelRepository.save(memberLevel);
        return toDTO(memberLevel);
    }

    @Transactional
    public MemberLevelDTO updateMemberLevel(Long id, MemberLevelDTO dto) {
        MemberLevel memberLevel = memberLevelRepository.findById(id)
                .orElseThrow(() -> new BusinessException("會員等級不存在"));

        if (!memberLevel.getName().equals(dto.getName()) && memberLevelRepository.existsByName(dto.getName())) {
            throw new BusinessException("等級名稱已存在");
        }

        BeanUtils.copyProperties(dto, memberLevel, "id", "createdAt", "updatedAt");
        memberLevel = memberLevelRepository.save(memberLevel);
        return toDTO(memberLevel);
    }

    public MemberLevelDTO getMemberLevel(Long id) {
        MemberLevel memberLevel = memberLevelRepository.findById(id)
                .orElseThrow(() -> new BusinessException("會員等級不存在"));
        return toDTO(memberLevel);
    }

    @Transactional
    public void deleteMemberLevel(Long id) {
        if (!memberLevelRepository.existsById(id)) {
            throw new BusinessException("會員等級不存在");
        }
        memberLevelRepository.deleteById(id);
    }

    public Page<MemberLevelDTO> listMemberLevels(Pageable pageable) {
        return memberLevelRepository.findAll(pageable).map(this::toDTO);
    }

    public List<MemberLevelDTO> listAllMemberLevels() {
        return memberLevelRepository.findAllByOrderByLevelOrderAsc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<MemberLevelDTO> listEnabledMemberLevels() {
        return memberLevelRepository.findByEnabledOrderByLevelOrderAsc(true)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MemberLevelDTO toggleEnabled(Long id) {
        MemberLevel memberLevel = memberLevelRepository.findById(id)
                .orElseThrow(() -> new BusinessException("會員等級不存在"));
        memberLevel.setEnabled(!memberLevel.getEnabled());
        memberLevel = memberLevelRepository.save(memberLevel);
        return toDTO(memberLevel);
    }

    private MemberLevelDTO toDTO(MemberLevel memberLevel) {
        MemberLevelDTO dto = new MemberLevelDTO();
        BeanUtils.copyProperties(memberLevel, dto);
        return dto;
    }
}
