package com.info.ecommerce.modules.crm.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.crm.dto.CustomPageDTO;
import com.info.ecommerce.modules.crm.entity.CustomPage;
import com.info.ecommerce.modules.crm.repository.CustomPageRepository;
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
public class CustomPageService {

    private final CustomPageRepository customPageRepository;

    @Transactional
    public CustomPageDTO createCustomPage(CustomPageDTO dto) {
        if (customPageRepository.existsBySlug(dto.getSlug())) {
            throw new BusinessException("頁面別名已存在");
        }

        CustomPage customPage = new CustomPage();
        BeanUtils.copyProperties(dto, customPage, "id");
        customPage = customPageRepository.save(customPage);
        return toDTO(customPage);
    }

    @Transactional
    public CustomPageDTO updateCustomPage(Long id, CustomPageDTO dto) {
        CustomPage customPage = customPageRepository.findById(id)
                .orElseThrow(() -> new BusinessException("自訂頁面不存在"));

        if (!customPage.getSlug().equals(dto.getSlug()) && customPageRepository.existsBySlug(dto.getSlug())) {
            throw new BusinessException("頁面別名已存在");
        }

        BeanUtils.copyProperties(dto, customPage, "id", "createdAt", "updatedAt");
        customPage = customPageRepository.save(customPage);
        return toDTO(customPage);
    }

    public CustomPageDTO getCustomPage(Long id) {
        CustomPage customPage = customPageRepository.findById(id)
                .orElseThrow(() -> new BusinessException("自訂頁面不存在"));
        return toDTO(customPage);
    }

    public CustomPageDTO getCustomPageBySlug(String slug) {
        CustomPage customPage = customPageRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException("自訂頁面不存在"));
        return toDTO(customPage);
    }

    @Transactional
    public void deleteCustomPage(Long id) {
        if (!customPageRepository.existsById(id)) {
            throw new BusinessException("自訂頁面不存在");
        }
        customPageRepository.deleteById(id);
    }

    public Page<CustomPageDTO> listCustomPages(Pageable pageable) {
        return customPageRepository.findAll(pageable).map(this::toDTO);
    }

    public List<CustomPageDTO> listAllCustomPages() {
        return customPageRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CustomPageDTO> listEnabledCustomPages() {
        return customPageRepository.findByEnabledOrderBySortOrderAsc(true)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomPageDTO toggleEnabled(Long id) {
        CustomPage customPage = customPageRepository.findById(id)
                .orElseThrow(() -> new BusinessException("自訂頁面不存在"));
        customPage.setEnabled(!customPage.getEnabled());
        customPage = customPageRepository.save(customPage);
        return toDTO(customPage);
    }

    private CustomPageDTO toDTO(CustomPage customPage) {
        CustomPageDTO dto = new CustomPageDTO();
        BeanUtils.copyProperties(customPage, dto);
        return dto;
    }
}
