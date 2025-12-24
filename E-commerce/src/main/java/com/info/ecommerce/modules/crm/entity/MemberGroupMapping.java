package com.info.ecommerce.modules.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 會員群組對應表
 */
@Entity
@Table(name = "crm_member_group_mapping", indexes = {
    @Index(name = "idx_mapping_member_id", columnList = "member_id"),
    @Index(name = "idx_mapping_group_id", columnList = "group_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberGroupMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 會員 ID
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // 群組 ID
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    // 加入時間
    @Column(nullable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    public void prePersist() {
        this.joinedAt = LocalDateTime.now();
    }
}
