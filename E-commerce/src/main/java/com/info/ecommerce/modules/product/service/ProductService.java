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
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

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
                // 同一張圖片只保留一次（保留第一次出現的位置）
                if (imageDTO.getImageUrl() != null && !imageDTO.getImageUrl().isEmpty()
                        && !imageUrls.contains(imageDTO.getImageUrl())) {
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
        return mapList(result, true);
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
        return mapList(productRepository.findPublic(List.of(status), false, List.of(-1L), null, pageable), true);
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

    /**
     * 商品列表：整頁商品的規格與庫存各以一次查詢取得（避免每個商品各查數次），列表不帶描述區塊
     */
    private Page<ProductDTO> mapList(Page<Product> products, boolean publicView) {
        List<Long> ids = products.getContent().stream().map(Product::getId).toList();
        java.util.Map<Long, List<ProductSpecification>> specsByProduct = ids.isEmpty() ? java.util.Map.of()
                : productSpecificationRepository.findByProductIdIn(ids).stream()
                        .collect(java.util.stream.Collectors.groupingBy(ProductSpecification::getProductId));
        java.util.Map<Long, ProductInventory> inventoryByProduct = ids.isEmpty() ? java.util.Map.of()
                : productInventoryRepository.findByProductIdInAndSpecificationIdIsNull(ids).stream()
                        .collect(java.util.stream.Collectors.toMap(ProductInventory::getProductId, inventory -> inventory,
                                (first, second) -> first));
        return products.map(product -> {
            ProductDTO dto = toDTO(product, specsByProduct.getOrDefault(product.getId(), List.of()),
                    java.util.Optional.ofNullable(inventoryByProduct.get(product.getId())), false);
            return publicView ? stripForPublic(dto) : dto;
        });
    }

    /** 前台用 DTO：移除成本價等內部資料，只保留啟用中的規格 */
    private ProductDTO toPublicDTO(Product product) {
        return stripForPublic(toDTO(product));
    }

    private ProductDTO stripForPublic(ProductDTO dto) {
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
        return mapList(productRepository.findAll(pageable), false);
    }

    /**
     * 依分類查詢商品
     */
    public Page<ProductDTO> listProductsByCategory(Long categoryId, Pageable pageable) {
        return mapList(productRepository.findByCategoryId(categoryId, pageable), false);
    }

    /**
     * 依狀態查詢商品
     */
    public Page<ProductDTO> listProductsByStatus(ProductStatus status, Pageable pageable) {
        return mapList(productRepository.findByStatus(status, pageable), false);
    }

    /**
     * 搜尋商品
     */
    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        return mapList(productRepository.findByNameContaining(keyword, pageable), false);
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

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("商品不存在"));

        // 獲取當前商品的圖片列表
        List<String> imageUrls = product.getImageUrls();
        if (imageUrls == null) {
            imageUrls = new ArrayList<>();
        }

        // 批次獲取所有相冊圖片（避免 N+1 查詢問題）
        List<AlbumImage> albumImages = albumImageRepository.findAllById(albumImageIds);

        // 驗證所有圖片都存在
        if (albumImages.size() != albumImageIds.size()) {
            throw new BusinessException("部分相冊圖片不存在");
        }

        // 添加相冊圖片的 URL
        for (AlbumImage albumImage : albumImages) {
            String url = albumImage.getImageUrl();
            // 添加圖片 URL（避免重複）
            if (!imageUrls.contains(url)) {
                imageUrls.add(url);
            } else {
            }
        }

        product.setImageUrls(imageUrls);
        product = productRepository.save(product);
        return toDTO(product);
    }

    private ProductDTO toDTO(Product entity) {
        if (entity.getId() == null) {
            return toDTO(entity, List.of(), java.util.Optional.empty(), false);
        }
        return toDTO(entity, productSpecificationRepository.findByProductId(entity.getId()),
                productInventoryRepository.findByProductIdAndSpecificationId(entity.getId(), null), true);
    }

    private ProductDTO toDTO(Product entity, List<ProductSpecification> specs,
                             java.util.Optional<ProductInventory> productLevelInventory, boolean withDescriptionBlocks) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(entity, dto, "images");

        // 將 imageUrls 轉換為 images
        List<String> urls = entity.getImageUrls();

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
        }

        // 添加描述區塊（商品詳情才需要）
        if (withDescriptionBlocks && entity.getId() != null) {
            try {
                List<ProductDescriptionBlockDTO> blocks = descriptionBlockService.getProductBlocks(entity.getId());
                dto.setDescriptionBlocks(blocks);
            } catch (Exception e) {
                // 如果獲取描述區塊失敗，設為空列表
                dto.setDescriptionBlocks(new ArrayList<>());
            }
        }

        // 添加商品規格
        dto.setSpecifications(specs.stream()
                .map(spec -> {
                    ProductSpecificationDTO specDTO = new ProductSpecificationDTO();
                    BeanUtils.copyProperties(spec, specDTO);
                    return specDTO;
                })
                .collect(java.util.stream.Collectors.toList()));

        dto.setStock(calculateProductStock(specs, productLevelInventory));

        return dto;
    }

    /**
     * 商品可售庫存（與結帳扣庫存的規則一致）：
     * - 有啟用中的規格：各規格庫存加總；任一規格未設定庫存（null）視為不限量，回傳 null
     * - 無規格：商品層級庫存；沒有庫存紀錄或未設定時回傳 null（不追蹤庫存、不限量）
     */
    private Integer calculateProductStock(List<ProductSpecification> allSpecs, java.util.Optional<ProductInventory> productLevelInventory) {
        List<ProductSpecification> specifications = allSpecs.stream()
                .filter(spec -> spec.getEnabled() == null || Boolean.TRUE.equals(spec.getEnabled()))
                .toList();
        if (!specifications.isEmpty()) {
            if (specifications.stream().anyMatch(spec -> spec.getStock() == null)) {
                return null;
            }
            return specifications.stream()
                    .mapToInt(spec -> Math.max(spec.getStock(), 0))
                    .sum();
        }

        return productLevelInventory
                .map(ProductInventory::getAvailableStock)
                .map(stock -> Math.max(stock, 0))
                .orElse(null);
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
            eventPublisher.publishEvent(new com.info.ecommerce.modules.product.event.StockChangedEvent(
                    List.of(productId), normalizedStock > beforeStock));
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
