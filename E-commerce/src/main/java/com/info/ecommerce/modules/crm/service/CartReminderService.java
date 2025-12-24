package com.info.ecommerce.modules.crm.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.crm.dto.CartReminderDTO;
import com.info.ecommerce.modules.crm.entity.CartReminder;
import com.info.ecommerce.modules.crm.repository.CartReminderRepository;
import com.info.ecommerce.modules.crm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartReminderService {

    private final CartReminderRepository cartReminderRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CartReminderDTO createCartReminder(CartReminderDTO dto) {
        if (!memberRepository.existsById(dto.getMemberId())) {
            throw new BusinessException("會員不存在");
        }

        CartReminder cartReminder = new CartReminder();
        BeanUtils.copyProperties(dto, cartReminder, "id");
        cartReminder = cartReminderRepository.save(cartReminder);
        return toDTO(cartReminder);
    }

    @Transactional
    public CartReminderDTO updateCartReminder(Long id, CartReminderDTO dto) {
        CartReminder cartReminder = cartReminderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("購物車提醒不存在"));

        BeanUtils.copyProperties(dto, cartReminder, "id", "createdAt", "updatedAt");
        cartReminder = cartReminderRepository.save(cartReminder);
        return toDTO(cartReminder);
    }

    public CartReminderDTO getCartReminder(Long id) {
        CartReminder cartReminder = cartReminderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("購物車提醒不存在"));
        return toDTO(cartReminder);
    }

    @Transactional
    public void deleteCartReminder(Long id) {
        if (!cartReminderRepository.existsById(id)) {
            throw new BusinessException("購物車提醒不存在");
        }
        cartReminderRepository.deleteById(id);
    }

    public Page<CartReminderDTO> listCartReminders(Pageable pageable) {
        return cartReminderRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<CartReminderDTO> listCartRemindersByMember(Long memberId, Pageable pageable) {
        return cartReminderRepository.findByMemberId(memberId, pageable).map(this::toDTO);
    }

    public List<CartReminderDTO> listPendingReminders(LocalDateTime beforeDate) {
        return cartReminderRepository.findByIsSentAndCreatedAtBefore(false, beforeDate)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CartReminderDTO sendReminder(Long id) {
        CartReminder cartReminder = cartReminderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("購物車提醒不存在"));

        if (cartReminder.getIsSent()) {
            throw new BusinessException("提醒已發送");
        }

        // 這裡應該調用實際的通知服務（郵件、簡訊等）
        
        cartReminder.setIsSent(true);
        cartReminder.setSentAt(LocalDateTime.now());
        cartReminder = cartReminderRepository.save(cartReminder);
        return toDTO(cartReminder);
    }

    @Transactional
    public void sendPendingReminders(int hoursThreshold) {
        LocalDateTime threshold = LocalDateTime.now().minusHours(hoursThreshold);
        List<CartReminder> pendingReminders = cartReminderRepository
                .findByIsSentAndCreatedAtBefore(false, threshold);

        for (CartReminder reminder : pendingReminders) {
            try {
                sendReminder(reminder.getId());
            } catch (Exception e) {
                // 記錄錯誤但繼續處理其他提醒
            }
        }
    }

    private CartReminderDTO toDTO(CartReminder cartReminder) {
        CartReminderDTO dto = new CartReminderDTO();
        BeanUtils.copyProperties(cartReminder, dto);
        return dto;
    }
}
