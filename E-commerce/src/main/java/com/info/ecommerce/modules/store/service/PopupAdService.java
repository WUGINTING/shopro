package com.info.ecommerce.modules.store.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.store.dto.PopupAdDTO;
import com.info.ecommerce.modules.store.entity.PopupAd;
import com.info.ecommerce.modules.store.repository.PopupAdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PopupAdService {

    private final PopupAdRepository popupAdRepository;

    public List<PopupAdDTO> getAllAds() {
        return popupAdRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 取得目前有效的廣告（前台用）
     */
    public List<PopupAdDTO> getActiveAds() {
        return popupAdRepository.findActiveAds(LocalDateTime.now())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PopupAdDTO getAdById(Long id) {
        PopupAd ad = popupAdRepository.findById(id)
                .orElseThrow(() -> new BusinessException("廣告不存在"));
        return toDTO(ad);
    }

    @Transactional
    public PopupAdDTO createAd(PopupAdDTO dto) {
        PopupAd ad = new PopupAd();
        BeanUtils.copyProperties(dto, ad, "id");
        ad = popupAdRepository.save(ad);
        return toDTO(ad);
    }

    @Transactional
    public PopupAdDTO updateAd(Long id, PopupAdDTO dto) {
        PopupAd ad = popupAdRepository.findById(id)
                .orElseThrow(() -> new BusinessException("廣告不存在"));
        
        BeanUtils.copyProperties(dto, ad, "id", "createdAt", "updatedAt");
        ad = popupAdRepository.save(ad);
        return toDTO(ad);
    }

    @Transactional
    public void deleteAd(Long id) {
        if (!popupAdRepository.existsById(id)) {
            throw new BusinessException("廣告不存在");
        }
        popupAdRepository.deleteById(id);
    }

    private PopupAdDTO toDTO(PopupAd entity) {
        PopupAdDTO dto = new PopupAdDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
