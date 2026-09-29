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

    /**
     * 驗證廣告內容：長度、連結協定（僅允許 http(s) 或站內路徑，避免 javascript: 等連結在前台執行）、時間區間
     */
    private void validate(PopupAdDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new BusinessException("請輸入廣告標題");
        }
        if (dto.getTitle().length() > 100) {
            throw new BusinessException("廣告標題不可超過 100 字");
        }
        validateUrl(dto.getImageUrl(), "圖片網址");
        validateUrl(dto.getLinkUrl(), "連結網址");
        if (dto.getStartTime() != null && dto.getEndTime() != null && !dto.getEndTime().isAfter(dto.getStartTime())) {
            throw new BusinessException("結束時間必須晚於開始時間");
        }
    }

    private static void validateUrl(String url, String label) {
        if (url == null || url.isBlank()) {
            return;
        }
        if (url.length() > 500) {
            throw new BusinessException(label + "不可超過 500 字");
        }
        String trimmed = url.trim().toLowerCase();
        boolean allowed = trimmed.startsWith("https://") || trimmed.startsWith("http://")
                || (trimmed.startsWith("/") && !trimmed.startsWith("//"));
        if (!allowed) {
            throw new BusinessException(label + "只能是 http(s):// 開頭的網址或站內路徑（/ 開頭）");
        }
    }

    @Transactional
    public PopupAdDTO createAd(PopupAdDTO dto) {
        validate(dto);
        PopupAd ad = new PopupAd();
        BeanUtils.copyProperties(dto, ad, "id");
        ad = popupAdRepository.save(ad);
        return toDTO(ad);
    }

    @Transactional
    public PopupAdDTO updateAd(Long id, PopupAdDTO dto) {
        PopupAd ad = popupAdRepository.findById(id)
                .orElseThrow(() -> new BusinessException("廣告不存在"));

        validate(dto);
        Boolean currentEnabled = ad.getEnabled();
        BeanUtils.copyProperties(dto, ad, "id", "createdAt", "updatedAt");
        if (ad.getEnabled() == null) {
            ad.setEnabled(currentEnabled != null ? currentEnabled : Boolean.TRUE);
        }
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
