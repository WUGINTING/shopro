package com.info.ecommerce.modules.crm.entity;

import com.info.ecommerce.modules.crm.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 會員資料表
 */
@Entity
@Table(name = "crm_member", indexes = {
    @Index(name = "idx_member_email", columnList = "email"),
    @Index(name = "idx_member_phone", columnList = "phone"),
    @Index(name = "idx_member_status", columnList = "status"),
    @Index(name = "idx_member_level_id", columnList = "level_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 會員姓名
    @Column(nullable = false, columnDefinition = "NVARCHAR(100)")
    private String name;

    // 電子郵件
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    // 手機號碼
    @Column(length = 20)
    private String phone;

    // 生日
    @Column
    private LocalDate birthday;

    // 會員狀態
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberStatus status;

    // 會員等級 ID
    @Column(name = "level_id")
    private Long levelId;

    // 總積點
    @Column(nullable = false)
    private Integer totalPoints;

    // 可用積點
    @Column(nullable = false)
    private Integer availablePoints;

    // 地址
    @Column(columnDefinition = "NVARCHAR(500)")
    private String address;

    // 郵遞區號
    @Column(length = 10)
    private String postalCode;

    // 性別 (M/F/O)
    @Column(length = 1)
    private String gender;

    // 備註
    @Column(columnDefinition = "NVARCHAR(500)")
    private String notes;

    // 註冊日期
    @Column(nullable = false)
    private LocalDateTime registeredAt;

    // 最後登入時間
    @Column
    private LocalDateTime lastLoginAt;

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
        this.registeredAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = MemberStatus.ACTIVE;
        }
        if (this.totalPoints == null) {
            this.totalPoints = 0;
        }
        if (this.availablePoints == null) {
            this.availablePoints = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
