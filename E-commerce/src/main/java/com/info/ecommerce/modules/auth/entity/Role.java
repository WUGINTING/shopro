package com.info.ecommerce.modules.auth.entity;

/**
 * Role enum defining the available roles in the system.
 * 
 * ADMIN: Full system access, can manage all resources
 * MANAGER: Can manage products, orders, and view reports
 * STAFF: Limited access, can view and process orders
 * CUSTOMER: Basic access, can browse products and place orders
 */
public enum Role {
    ADMIN,
    MANAGER,
    STAFF,
    CUSTOMER
}
