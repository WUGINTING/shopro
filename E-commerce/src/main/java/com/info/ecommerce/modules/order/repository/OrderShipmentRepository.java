package com.info.ecommerce.modules.order.repository;

import com.info.ecommerce.modules.order.entity.OrderShipment;
import com.info.ecommerce.modules.order.enums.ShippingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderShipmentRepository extends JpaRepository<OrderShipment, Long> {
    
    List<OrderShipment> findByOrderId(Long orderId);
    
    Optional<OrderShipment> findByTrackingNumber(String trackingNumber);
    
    List<OrderShipment> findByShippingStatus(ShippingStatus shippingStatus);
}
