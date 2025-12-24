package com.info.ecommerce.modules.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 訂單問與答（Q&A 功能）
 */
@Entity
@Table(name = "order_qa", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderQA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 訂單 ID
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    // 提問者類型（CUSTOMER, STORE）
    @Column(name = "asker_type", nullable = false, length = 20)
    private String askerType;

    // 提問者 ID
    @Column(name = "asker_id", nullable = false)
    private Long askerId;

    // 提問者名稱
    @Column(name = "asker_name", columnDefinition = "NVARCHAR(100)")
    private String askerName;

    // 問題內容
    @Column(name = "question", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String question;

    // 回答內容
    @Column(name = "answer", columnDefinition = "NVARCHAR(MAX)")
    private String answer;

    // 回答者 ID
    @Column(name = "answerer_id")
    private Long answererId;

    // 回答者名稱
    @Column(name = "answerer_name", columnDefinition = "NVARCHAR(100)")
    private String answererName;

    // 回答時間
    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    // 建立時間
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
