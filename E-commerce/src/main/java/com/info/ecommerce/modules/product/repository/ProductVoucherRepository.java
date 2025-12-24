package com.info.ecommerce.modules.product.repository;

import com.info.ecommerce.modules.product.entity.ProductVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductVoucherRepository extends JpaRepository<ProductVoucher, Long> {
    
    Optional<ProductVoucher> findByProductId(Long productId);
}
