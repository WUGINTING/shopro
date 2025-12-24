package com.info.ecommerce.modules.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 會員群組表
 */
@Entity
@Table(name = "crm_member_group", indexes = {
    @Index(name = "idx_group_name", columnList = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 群組名稱
    @Column(nullable = false, unique = true, columnDefinition = "NVARCHAR(100)")
    private String name;

    // 群組描述
    @Column(columnDefinition = "NVARCHAR(500)")
    private String description;

    // 是否啟用
    @Column(nullable = false)
    private Boolean enabled;

    // 創建時間
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 更新時間
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.enabled == null) {
            this.enabled = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
