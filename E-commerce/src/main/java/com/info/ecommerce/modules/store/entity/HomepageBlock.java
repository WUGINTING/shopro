package com.info.ecommerce.modules.store.entity;

import com.info.ecommerce.modules.store.enums.BlockType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "homepage_block")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomepageBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BlockType blockType;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String title;

    @Column(nullable = false)
    private Integer sortOrder;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;  // JSON 格式

    private LocalDateTime createdAt;

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
