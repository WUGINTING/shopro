package com.info.ecommerce.modules.store.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.store.dto.PopupAdDTO;
import com.info.ecommerce.modules.store.entity.PopupAd;
import com.info.ecommerce.modules.store.repository.PopupAdRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PopupAdServiceTest {

    @Mock
    private PopupAdRepository popupAdRepository;

    @InjectMocks
    private PopupAdService popupAdService;

    private static PopupAdDTO ad(String linkUrl) {
        PopupAdDTO dto = new PopupAdDTO();
        dto.setTitle("秋季新品");
        dto.setImageUrl("/api/albums/images/a.jpg");
        dto.setLinkUrl(linkUrl);
        dto.setStartTime(LocalDateTime.now());
        dto.setEndTime(LocalDateTime.now().plusDays(7));
        return dto;
    }

    @Test
    void rejectsScriptAndProtocolRelativeLinks() {
        assertThrows(BusinessException.class, () -> popupAdService.createAd(ad("javascript:alert(1)")));
        assertThrows(BusinessException.class, () -> popupAdService.createAd(ad(" JavaScript:alert(1)")));
        assertThrows(BusinessException.class, () -> popupAdService.createAd(ad("//evil.example.com")));
        verify(popupAdRepository, never()).save(any());
    }

    @Test
    void rejectsEndBeforeStart() {
        PopupAdDTO dto = ad("/shop/product/list");
        dto.setEndTime(dto.getStartTime().minusHours(1));
        assertThrows(BusinessException.class, () -> popupAdService.createAd(dto));
    }

    @Test
    void acceptsHttpsAndSitePaths() {
        when(popupAdRepository.save(any(PopupAd.class))).thenAnswer(invocation -> invocation.getArgument(0));
        assertDoesNotThrow(() -> popupAdService.createAd(ad("https://shop.example.com/shop/news")));
        assertDoesNotThrow(() -> popupAdService.createAd(ad("/shop/product/list?category=1")));
    }
}
