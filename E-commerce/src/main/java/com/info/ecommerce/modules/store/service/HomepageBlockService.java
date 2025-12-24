package com.info.ecommerce.modules.store.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.store.block_types_based.*;
import com.info.ecommerce.modules.store.dto.HomepageBlockDTO;
import com.info.ecommerce.modules.store.entity.HomepageBlock;
import com.info.ecommerce.modules.store.enums.BlockType;
import com.info.ecommerce.modules.store.repository.HomepageBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomepageBlockService {

    private static final int MAX_BLOCKS_SILVER = 4;
    private static final int MAX_BLOCKS_GOLD = 7;

    private final HomepageBlockRepository homepageBlockRepository;
    private final StoreService storeService;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true); // 不允許非約定欄位
    public List<HomepageBlockDTO> getAllBlocks() {
        return homepageBlockRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<HomepageBlockDTO> getEnabledBlocks() {
        return homepageBlockRepository.findByEnabledTrueOrderBySortOrderAsc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public HomepageBlockDTO getBlockById(Long id) {
        HomepageBlock block = homepageBlockRepository.findById(id)
                .orElseThrow(() -> new BusinessException("區塊不存在"));
        return toDTO(block);
    }

    @Transactional
    public HomepageBlockDTO createBlock(HomepageBlockDTO dto) {
        // 檢查區塊數量上限
        int maxBlocks = getMaxBlockCount();
        long currentCount = homepageBlockRepository.countByEnabledTrue();
        validateContent(dto.getBlockType(),dto.getContent());
        if (Boolean.TRUE.equals(dto.getEnabled()) && currentCount >= maxBlocks) {
            throw new BusinessException("啟用的區塊已達上限 " + maxBlocks + " 個");
        }

        HomepageBlock block = new HomepageBlock();
        BeanUtils.copyProperties(dto, block, "id");

        block = homepageBlockRepository.save(block);
        return toDTO(block);
    }

    @Transactional
    public HomepageBlockDTO updateBlock(Long id, HomepageBlockDTO dto) {
        HomepageBlock block = homepageBlockRepository.findById(id)
                .orElseThrow(() -> new BusinessException("區塊不存在"));
        validateContent(dto.getBlockType(),dto.getContent());
        // 如果從停用改成啟用，檢查數量
        if (!block.getEnabled() && Boolean.TRUE.equals(dto.getEnabled())) {
            int maxBlocks = getMaxBlockCount();
            long currentCount = homepageBlockRepository.countByEnabledTrue();
            if (currentCount >= maxBlocks) {
                throw new BusinessException("啟用的區塊已達上限 " + maxBlocks + " 個");
            }
        }

        BeanUtils.copyProperties(dto, block, "id", "createdAt", "updatedAt");
        block = homepageBlockRepository.save(block);
        return toDTO(block);
    }

    @Transactional
    public void deleteBlock(Long id) {
        if (!homepageBlockRepository.existsById(id)) {
            throw new BusinessException("區塊不存在");
        }
        homepageBlockRepository.deleteById(id);
    }

    /**
     * 根據商店等級取得最大區塊數
     */
    private int getMaxBlockCount() {
        try {
            String themeLevel = storeService.getStore().getThemeLevel();
            return "GOLD".equals(themeLevel) ? MAX_BLOCKS_GOLD : MAX_BLOCKS_SILVER;
        } catch (Exception e) {
            return MAX_BLOCKS_SILVER;  // 預設 Silver
        }
    }

    private HomepageBlockDTO toDTO(HomepageBlock entity) {
        HomepageBlockDTO dto = new HomepageBlockDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public void validateContent(BlockType type, String contentJson) {
        if (contentJson == null || contentJson.isEmpty()) return;

        try {
            switch (type) {
                case BANNER -> objectMapper.readValue(contentJson, BannerContent.class);
                case PRODUCT_LIST -> objectMapper.readValue(contentJson, ProductListContent.class);
                case CATEGORY -> objectMapper.readValue(contentJson, CategoryContent.class);
                case TEXT -> objectMapper.readValue(contentJson, TextContent.class);
                case VIDEO -> objectMapper.readValue(contentJson, VideoContent.class);
                case IMAGE -> objectMapper.readValue(contentJson, ImageContent.class);
                case HTML -> objectMapper.readValue(contentJson, HtmlContent.class);
                default -> throw new BusinessException("未知的區塊類型");
            }
        } catch (JsonProcessingException e) {
            // 當 JSON 格式不正確或包含未知欄位時會進入此處
            throw new BusinessException("區塊內容 JSON 格式錯誤或包含非法欄位: " + e.getMessage());
        }
    }
}
