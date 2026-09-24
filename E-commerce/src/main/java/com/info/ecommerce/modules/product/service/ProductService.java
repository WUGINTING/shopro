package com.info.ecommerce.modules.product.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.album.entity.AlbumImage;
import com.info.ecommerce.modules.album.repository.AlbumImageRepository;
import com.info.ecommerce.modules.product.dto.ProductDTO;
import com.info.ecommerce.modules.product.dto.ProductDescriptionBlockDTO;
import com.info.ecommerce.modules.product.dto.ProductImageDTO;
import com.info.ecommerce.modules.product.dto.ProductSpecificationDTO;
import com.info.ecommerce.modules.product.entity.Product;
import com.info.ecommerce.modules.product.entity.ProductInventory;
import com.info.ecommerce.modules.product.entity.InventoryMovementLog;
import com.info.ecommerce.modules.product.entity.ProductSpecification;
import com.info.ecommerce.modules.product.enums.ProductStatus;
import com.info.ecommerce.modules.product.repository.ProductCategoryRepository;
import com.info.ecommerce.modules.product.repository.InventoryMovementLogRepository;
import com.info.ecommerce.modules.product.repository.ProductInventoryRepository;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import com.info.ecommerce.modules.product.repository.ProductSpecificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final AlbumImageRepository albumImageRepository;
    private final ProductDescriptionBlockService descriptionBlockService;
    private final ProductInventoryRepository productInventoryRepository;
    private final ProductSpecificationRepository productSpecificationRepository;
    private final InventoryMovementLogRepository inventoryMovementLogRepository;

    /**
     * 驗證並標準化 SKU
     * @return 標準化後的 SKU，如果輸入為 null 或空白則返回 null
     */
    private String validateAndNormalizeSku(String sku) {
        if (sku != null && !sku.trim().isEmpty()) {
            return sku.trim();
        }
        return null;
    }

    /**
     * 驗證分類是否存在
     */
    private void validateCategory(Long categoryId) {
        if (categoryId != null && !productCategoryRepository.existsById(categoryId)) {
            throw new BusinessException("商品分類不存在");
        }
    }

    /**
     * 創建商品
     */
    @Transactional
    public ProductDTO createProduct(ProductDTO dto) {
        // 檢查分類是否存在
        validateCategory(dto.getCategoryId());

        // 檢查 SKU 是否已存在
        String normalizedSku = validateAndNormalizeSku(dto.getSku());
        if (normalizedSku != null) {
            if (productRepository.existsBySku(normalizedSku)) {
                throw new BusinessException("商品編號（SKU）已存在，請使用其他編號");
            }
            dto.setSku(normalizedSku);
        }

        Product product = new Product();
        BeanUtils.copyProperties(dto, product, "id");
        product = productRepository.save(product);
        syncProductLevelInventory(product.getId(), dto.getStock());
        return toDTO(product);
    }

    /**
     * 更新商品
     */
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("商品不存在"));

        // 檢查分類是否存在
        validateCategory(dto.getCategoryId());

        // 檢查 SKU 是否與其他商品重複
        String normalizedSku = validateAndNormalizeSku(dto.getSku());
        if (normalizedSku != null) {
            // 檢查新的 SKU 是否已被其他商品使用（排除當前商品本身）
            if (productRepository.existsBySkuAndIdNot(normalizedSku, id)) {
                throw new BusinessException("商品編號（SKU）已存在，請使用其他編號");
            }
            dto.setSku(normalizedSku);
        }

        // Copy properties excluding images field since we'll handle it separately
        BeanUtils.copyProperties(dto, product, "id", "createdAt", "updatedAt", "images");

        // Manually handle images field conversion from List<ProductImageDTO> to List<String>
        if (dto.getImages() != null) {
            List<String> imageUrls = new ArrayList<>();
            for (var imageDTO : dto.getImages()) {
                if (imageDTO.getImageUrl() != null && !imageDTO.getImageUrl().isEmpty()) {
                    imageUrls.add(imageDTO.getImageUrl());
                }
            }
            product.setImageUrls(imageUrls);
        }

        product = productRepository.save(product);
        syncProductLevelInventory(product.getId(), dto.getStock());
        return toDTO(product);
    }

    /**
     * 取得商品詳情
     */
    public ProductDTO getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("商品不存在"));
        return toDTO(product);
    }

    /**
     * 刪除商品
     */
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new BusinessException("商品不存在");
        }
        productRepository.deleteById(id);
    }

    /** 前台可見的商品狀態（缺貨商品可瀏覽但無法結帳） */
    public static final List<ProductStatus> PUBLIC_STATUSES = List.of(ProductStatus.ACTIVE, ProductStatus.OUT_OF_STOCK);

    private static final int MAX_PUBLIC_PAGE_SIZE = 60;

    /**
     * 前台商品列表：只含上架 / 缺貨且未停用的商品，分頁與排序皆在後端完成
     *
     * @param categoryId 分類 ID（含其子分類），null 表示全部
     * @param keyword    名稱或 SKU 關鍵字，可為 null
     * @param sort       newest（預設）/ price_asc / price_desc / name
     */
    public Page<ProductDTO> listPublicProducts(Long categoryId, String keyword, String sort, int page, int size) {
        java.util.Set<Long> categoryIds = new java.util.HashSet<>();
        if (categoryId != null) {
            collectCategoryTree(categoryId, categoryIds);
        }
        String normalizedKeyword = keyword == null || keyword.isBlank() ? null : keyword.trim();
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PUBLIC_PAGE_SIZE);
        boolean filterCategory = categoryId != null;
        java.util.Collection<Long> ids = categoryIds.isEmpty() ? List.of(-1L) : categoryIds;
        String sortKey = sort == null ? "newest" : sort;
        Page<Product> result = switch (sortKey) {
            case "price_asc" -> productRepository.findPublicOrderByPriceAsc(PUBLIC_STATUSES, filterCategory, ids,
                    normalizedKeyword, org.springframework.data.domain.PageRequest.of(safePage, safeSize));
            case "price_desc" -> productRepository.findPublicOrderByPriceDesc(PUBLIC_STATUSES, filterCategory, ids,
                    normalizedKeyword, org.springframework.data.domain.PageRequest.of(safePage, safeSize));
            default -> productRepository.findPublic(PUBLIC_STATUSES, filterCategory, ids, normalizedKeyword,
                    org.springframework.data.domain.PageRequest.of(safePage, safeSize, publicSort(sortKey)));
        };
        return result.map(this::toPublicDTO);
    }

    /**
     * 前台依狀態查詢（只允許前台可見狀態，排除停用商品並移除成本價）
     */
    public Page<ProductDTO> listPublicProductsByStatus(ProductStatus status, int page, int size) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(Math.max(page, 0),
                Math.min(Math.max(size, 1), MAX_PUBLIC_PAGE_SIZE), publicSort("newest"));
        if (!PUBLIC_STATUSES.contains(status)) {
            return Page.empty(pageable);
        }
        return productRepository.findPublic(List.of(status), false, List.of(-1L), null, pageable)
                .map(this::toPublicDTO);
    }

    /**
     * 前台商品詳情：未上架或已停用的商品視為不存在
     */
    public ProductDTO getPublicProduct(Long id) {
        Product product = productRepository.findById(id)
                .filter(p -> PUBLIC_STATUSES.contains(p.getStatus()) && !Boolean.FALSE.equals(p.getEnabled()))
                .orElseThrow(() -> new BusinessException("商品不存在"));
        return toPublicDTO(product);
    }

    /** 前台存取商品附屬資料（規格、圖片、描述區塊）前確認商品已上架 */
    public void assertPubliclyVisible(Long productId) {
        getPublicProductEntity(productId);
    }

    private Product getPublicProductEntity(Long productId) {
        return productRepository.findById(productId)
                .filter(p -> PUBLIC_STATUSES.contains(p.getStatus()) && !Boolean.FALSE.equals(p.getEnabled()))
                .orElseThrow(() -> new BusinessException("商品不存在"));
    }

    /** 前台用 DTO：移除成本價等內部資料，只保留啟用中的規格 */
    private ProductDTO toPublicDTO(Product product) {
        ProductDTO dto = toDTO(product);
        dto.setCostPrice(null);
        if (dto.getSpecifications() != null) {
            dto.setSpecifications(dto.getSpecifications().stream()
                    .filter(spec -> !Boolean.FALSE.equals(spec.getEnabled()))
                    .peek(spec -> spec.setCost(null))
                    .collect(java.util.stream.Collectors.toList()));
        }
        return dto;
    }

    private void collectCategoryTree(Long categoryId, java.util.Set<Long> result) {
        if (categoryId == null || !result.add(categoryId)) {
            return;
        }
        productCategoryRepository.findByParentId(categoryId)
                .forEach(child -> collectCategoryTree(child.getId(), result));
    }

    private static org.springframework.data.domain.Sort publicSort(String sort) {
        org.springframework.data.domain.Sort byId = org.springframework.data.domain.Sort.by("id");
        if ("name".equals(sort)) {
            return org.springframework.data.domain.Sort.by("name").and(byId);
        }
        return org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt")
                .and(byId.descending());
    }

    /**
     * 分頁查詢商品
     */
    public Page<ProductDTO> listProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * 依分類查詢商品
     */
    public Page<ProductDTO> listProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable).map(this::toDTO);
    }

    /**
     * 依狀態查詢商品
     */
    public Page<ProductDTO> listProductsByStatus(ProductStatus status, Pageable pageable) {
        return productRepository.findByStatus(status, pageable).map(this::toDTO);
    }

    /**
     * 搜尋商品
     */
    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContaining(keyword, pageable).map(this::toDTO);
    }

    /**
     * 上架商品
     */
    @Transactional
    public ProductDTO activateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("商品不存在"));
        product.setStatus(ProductStatus.ACTIVE);
        product = productRepository.save(product);
        return toDTO(product);
    }

    /**
     * 下架商品
     */
    @Transactional
    public ProductDTO deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("商品不存在"));
        product.setStatus(ProductStatus.INACTIVE);
        product = productRepository.save(product);
        return toDTO(product);
    }

    /**
     * 從相冊添加圖片到商品
     */
    @Transactional
    public ProductDTO addAlbumImagesToProduct(Long productId, List<Long> albumImageIds) {
        System.out.println("[ProductService] addAlbumImagesToProduct - productId: " + productId + ", albumImageIds: " + albumImageIds);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("商品不存在"));

        // 獲取當前商品的圖片列表
        List<String> imageUrls = product.getImageUrls();
        if (imageUrls == null) {
            imageUrls = new ArrayList<>();
        }
        System.out.println("[ProductService] 現有圖片數量: " + imageUrls.size());

        // 批次獲取所有相冊圖片（避免 N+1 查詢問題）
        List<AlbumImage> albumImages = albumImageRepository.findAllById(albumImageIds);
        System.out.println("[ProductService] 找到的相冊圖片數量: " + albumImages.size());

        // 驗證所有圖片都存在
        if (albumImages.size() != albumImageIds.size()) {
            System.err.println("[ProductService] 部分相冊圖片不存在! 請求: " + albumImageIds.size() + ", 找到: " + albumImages.size());
            throw new BusinessException("部分相冊圖片不存在");
        }

        // 添加相冊圖片的 URL
        for (AlbumImage albumImage : albumImages) {
            String url = albumImage.getImageUrl();
            System.out.println("[ProductService] 處理圖片: id=" + albumImage.getId() + ", url=" + url);
            // 添加圖片 URL（避免重複）
            if (!imageUrls.contains(url)) {
                imageUrls.add(url);
                System.out.println("[ProductService] 添加圖片 URL: " + url);
            } else {
                System.out.println("[ProductService] 圖片 URL 已存在，跳過: " + url);
            }
        }

        product.setImageUrls(imageUrls);
        product = productRepository.save(product);
        System.out.println("[ProductService] 保存後圖片數量: " + product.getImageUrls().size());
        return toDTO(product);
    }

    private ProductDTO toDTO(Product entity) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(entity, dto, "images");

        // 將 imageUrls 轉換為 images
        List<String> urls = entity.getImageUrls();
        System.out.println("[toDTO] 商品 " + entity.getId() + " (" + entity.getName() + ") 的 imageUrls: " + urls);

        if (urls != null && !urls.isEmpty()) {
            List<ProductImageDTO> imageDTOs = new ArrayList<>();
            for (int i = 0; i < urls.size(); i++) {
                String imageUrl = urls.get(i);
                ProductImageDTO imageDTO = new ProductImageDTO();
                imageDTO.setProductId(entity.getId());
                imageDTO.setImageUrl(imageUrl);
                imageDTO.setSortOrder(i);
                imageDTO.setIsPrimary(i == 0); // 第一張圖片設為主圖
                imageDTOs.add(imageDTO);
            }
            dto.setImages(imageDTOs);
            System.out.println("[toDTO] 設置了 " + imageDTOs.size() + " 張圖片");
        } else {
            System.out.println("[toDTO] 沒有圖片");
        }

        // 添加描述區塊
        if (entity.getId() != null) {
            try {
                List<ProductDescriptionBlockDTO> blocks = descriptionBlockService.getProductBlocks(entity.getId());
                dto.setDescriptionBlocks(blocks);
            } catch (Exception e) {
                // 如果獲取描述區塊失敗，設為空列表
                dto.setDescriptionBlocks(new ArrayList<>());
            }
        }

        // 添加商品規格
        if (entity.getId() != null) {
            try {
                List<ProductSpecification> specs = productSpecificationRepository.findByProductId(entity.getId());
                if (specs != null && !specs.isEmpty()) {
                    List<ProductSpecificationDTO> specDTOs = specs.stream()
                            .map(spec -> {
                                ProductSpecificationDTO specDTO = new ProductSpecificationDTO();
                                BeanUtils.copyProperties(spec, specDTO);
                                return specDTO;
                            })
                            .collect(java.util.stream.Collectors.toList());
                    dto.setSpecifications(specDTOs);
                } else {
                    dto.setSpecifications(new ArrayList<>());
                }
            } catch (Exception e) {
                dto.setSpecifications(new ArrayList<>());
            }
        }

        // 設置庫存（預設為 100，表示有貨）
        // TODO: 後續可以從 ProductSpecification 或 ProductInventory 計算實際庫存
        dto.setStock(calculateProductStock(entity.getId()));

        return dto;
    }

    private Integer calculateProductStock(Long productId) {
        if (productId == null) {
            return 0;
        }

        List<ProductInventory> inventories = productInventoryRepository.findByProductId(productId);
        if (inventories != null && !inventories.isEmpty()) {
            boolean hasSpecificationRows = inventories.stream()
                    .anyMatch(inventory -> inventory.getSpecificationId() != null);

            int inventoryStock = inventories.stream()
                    .filter(inventory -> !hasSpecificationRows || inventory.getSpecificationId() != null)
                    .map(ProductInventory::getAvailableStock)
                    .filter(stock -> stock != null && stock > 0)
                    .reduce(0, Integer::sum);

            return Math.max(inventoryStock, 0);
        }

        List<ProductSpecification> specifications = productSpecificationRepository.findByProductId(productId);
        if (specifications == null || specifications.isEmpty()) {
            return 0;
        }

        int specificationStock = specifications.stream()
                .filter(spec -> spec.getEnabled() == null || Boolean.TRUE.equals(spec.getEnabled()))
                .map(ProductSpecification::getStock)
                .filter(stock -> stock != null && stock > 0)
                .reduce(0, Integer::sum);

        return Math.max(specificationStock, 0);
    }

    private void syncProductLevelInventory(Long productId, Integer stock) {
        if (productId == null || stock == null) {
            return;
        }

        int normalizedStock = Math.max(stock, 0);

        List<ProductInventory> inventories = productInventoryRepository.findByProductId(productId);
        ProductInventory productLevelInventory = inventories.stream()
                .filter(inventory -> inventory.getSpecificationId() == null)
                .findFirst()
                .orElse(null);
        int beforeStock = productLevelInventory != null && productLevelInventory.getAvailableStock() != null
                ? productLevelInventory.getAvailableStock() : 0;

        if (productLevelInventory == null) {
            productLevelInventory = ProductInventory.builder()
                    .productId(productId)
                    .specificationId(null)
                    .warehouseId(1L)
                    .availableStock(normalizedStock)
                    .lockedStock(0)
                    .safetyStock(10)
                    .build();
        } else {
            productLevelInventory.setAvailableStock(normalizedStock);
            if (productLevelInventory.getWarehouseId() == null) {
                productLevelInventory.setWarehouseId(1L);
            }
        }

        productInventoryRepository.save(productLevelInventory);

        if (beforeStock != normalizedStock) {
            inventoryMovementLogRepository.save(InventoryMovementLog.builder()
                    .productId(productId)
                    .specificationId(null)
                    .warehouseId(productLevelInventory.getWarehouseId())
                    .changeType("SET")
                    .source("PRODUCT_EDIT")
                    .changeQuantity(normalizedStock - beforeStock)
                    .beforeStock(beforeStock)
                    .afterStock(normalizedStock)
                    .remark("商品編輯頁設定庫存")
                    .build());
        }
    }
}
