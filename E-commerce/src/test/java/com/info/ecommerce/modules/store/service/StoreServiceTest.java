package com.info.ecommerce.modules.store.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.store.dto.StoreDTO;
import com.info.ecommerce.modules.store.entity.Store;
import com.info.ecommerce.modules.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StoreService
 */
@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreService storeService;

    private Store store;
    private StoreDTO storeDTO;

    @BeforeEach
    void setUp() {
        store = Store.builder()
                .id(1L)
                .name("Test Store")
                .logo("/images/logo.png")
                .themeLevel("GOLD")
                .defaultCurrency("TWD")
                .invoiceEnabled(true)
                .metaTitle("Test Store Title")
                .metaDescription("Test Store Description")
                .metaKeywords("test,store")
                .businessStart(LocalTime.of(9, 0))
                .businessEnd(LocalTime.of(21, 0))
                .address("Test Address")
                .phone("02-1234-5678")
                .email("test@example.com")
                .build();

        storeDTO = new StoreDTO();
        storeDTO.setName("Test Store");
        storeDTO.setLogo("/images/logo.png");
        storeDTO.setThemeLevel("GOLD");
        storeDTO.setDefaultCurrency("TWD");
        storeDTO.setInvoiceEnabled(true);
        storeDTO.setMetaTitle("Test Store Title");
        storeDTO.setMetaDescription("Test Store Description");
        storeDTO.setMetaKeywords("test,store");
        storeDTO.setBusinessStart(LocalTime.of(9, 0));
        storeDTO.setBusinessEnd(LocalTime.of(21, 0));
        storeDTO.setAddress("Test Address");
        storeDTO.setPhone("02-1234-5678");
        storeDTO.setEmail("test@example.com");
    }

    @Test
    void should_ReturnStoreDTO_When_GetStoreWithValidId() {
        // given
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

        // when
        StoreDTO result = storeService.getStore();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Store");
        assertThat(result.getThemeLevel()).isEqualTo("GOLD");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(storeRepository, times(1)).findById(1L);
    }

    @Test
    void should_ThrowBusinessException_When_GetStoreNotFound() {
        // given
        when(storeRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> storeService.getStore())
                .isInstanceOf(BusinessException.class)
                .hasMessage("商店設定不存在");
        verify(storeRepository, times(1)).findById(1L);
    }

    @Test
    void should_CreateStore_When_NoStoreExists() {
        // given
        when(storeRepository.count()).thenReturn(0L);
        when(storeRepository.save(any(Store.class))).thenReturn(store);

        // when
        StoreDTO result = storeService.createStore(storeDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Store");
        assertThat(result.getThemeLevel()).isEqualTo("GOLD");
        verify(storeRepository, times(1)).count();
        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    void should_ThrowBusinessException_When_CreateStoreDuplicateExists() {
        // given
        when(storeRepository.count()).thenReturn(1L);

        // when & then
        assertThatThrownBy(() -> storeService.createStore(storeDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("商店設定已存在");
        verify(storeRepository, times(1)).count();
        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    void should_UpdateStore_When_StoreExists() {
        // given
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(storeRepository.save(any(Store.class))).thenReturn(store);

        StoreDTO updateDTO = new StoreDTO();
        updateDTO.setName("Updated Store");
        updateDTO.setThemeLevel("SILVER");

        // when
        StoreDTO result = storeService.saveStore(updateDTO);

        // then
        assertThat(result).isNotNull();
        verify(storeRepository, times(1)).findById(1L);
        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    void should_ThrowBusinessException_When_UpdateStoreNotInitialized() {
        // given
        when(storeRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> storeService.saveStore(storeDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("商店未初始化");
        verify(storeRepository, times(1)).findById(1L);
        verify(storeRepository, never()).save(any(Store.class));
    }
}
