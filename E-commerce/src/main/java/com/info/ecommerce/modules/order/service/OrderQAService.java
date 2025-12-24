package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.modules.order.dto.OrderQADTO;
import com.info.ecommerce.modules.order.entity.OrderQA;
import com.info.ecommerce.modules.order.repository.OrderQARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 訂單問與答服務 - Q&A 功能
 */
@Service
@RequiredArgsConstructor
public class OrderQAService {

    private final OrderQARepository orderQARepository;

    /**
     * 新增問題
     */
    @Transactional
    public OrderQADTO askQuestion(OrderQADTO dto) {
        OrderQA qa = convertToEntity(dto);
        qa = orderQARepository.save(qa);
        return convertToDTO(qa);
    }

    /**
     * 回答問題
     */
    @Transactional
    public OrderQADTO answerQuestion(Long qaId, String answer, Long answererId, String answererName) {
        OrderQA qa = orderQARepository.findById(qaId)
            .orElseThrow(() -> new RuntimeException("問題不存在"));
        
        qa.setAnswer(answer);
        qa.setAnswererId(answererId);
        qa.setAnswererName(answererName);
        qa.setAnsweredAt(LocalDateTime.now());
        
        qa = orderQARepository.save(qa);
        return convertToDTO(qa);
    }

    /**
     * 取得訂單的所有問答
     */
    @Transactional(readOnly = true)
    public List<OrderQADTO> getQAByOrderId(Long orderId) {
        return orderQARepository.findByOrderIdOrderByCreatedAtDesc(orderId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 分頁取得訂單問答
     */
    @Transactional(readOnly = true)
    public Page<OrderQADTO> getQAByOrderIdPage(Long orderId, Pageable pageable) {
        return orderQARepository.findByOrderId(orderId, pageable)
            .map(this::convertToDTO);
    }

    /**
     * 取得客戶的所有提問
     */
    @Transactional(readOnly = true)
    public List<OrderQADTO> getQAByAskerId(Long askerId) {
        return orderQARepository.findByAskerId(askerId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 刪除問答
     */
    @Transactional
    public void deleteQA(Long qaId) {
        orderQARepository.deleteById(qaId);
    }

    /**
     * 轉換 DTO 到 Entity
     */
    private OrderQA convertToEntity(OrderQADTO dto) {
        OrderQA qa = new OrderQA();
        BeanUtils.copyProperties(dto, qa);
        return qa;
    }

    /**
     * 轉換 Entity 到 DTO
     */
    private OrderQADTO convertToDTO(OrderQA qa) {
        OrderQADTO dto = new OrderQADTO();
        BeanUtils.copyProperties(qa, dto);
        return dto;
    }
}
