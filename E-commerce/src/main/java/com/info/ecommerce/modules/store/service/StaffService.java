package com.info.ecommerce.modules.store.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.store.dto.StaffDTO;
import com.info.ecommerce.modules.store.entity.Staff;
import com.info.ecommerce.modules.store.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffService {

    private static final int MAX_STAFF_COUNT = 50;

    private final StaffRepository staffRepository;

    public List<StaffDTO> getAllStaff() {
        return staffRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public StaffDTO getStaffById(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new BusinessException("員工不存在"));
        return toDTO(staff);
    }

    @Transactional
    public StaffDTO createStaff(StaffDTO dto) {
        // 檢查帳號上限
        long currentCount = staffRepository.countByEnabledTrue();
        if (currentCount >= MAX_STAFF_COUNT) {
            throw new BusinessException("員工帳號已達上限 " + MAX_STAFF_COUNT + " 組");
        }

        // 檢查帳號是否重複
        if (staffRepository.existsByAccount(dto.getAccount())) {
            throw new BusinessException("帳號已存在：" + dto.getAccount());
        }

        // 密碼必填檢查
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new BusinessException("新增員工時密碼為必填");
        }

        Staff staff = new Staff();
        BeanUtils.copyProperties(dto, staff, "id", "createdAt", "lastLoginAt");
        
        // TODO: 密碼加密（等 Security 再處理）
        // staff.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        staff = staffRepository.save(staff);
        return toDTO(staff);
    }

    @Transactional
    public StaffDTO updateStaff(Long id, StaffDTO dto) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new BusinessException("員工不存在"));

        // 如果要改帳號，檢查是否重複
        if (!staff.getAccount().equals(dto.getAccount()) 
                && staffRepository.existsByAccount(dto.getAccount())) {
            throw new BusinessException("帳號已存在：" + dto.getAccount());
        }

        BeanUtils.copyProperties(dto, staff, "id", "password", "createdAt", "lastLoginAt");
        
        // 如果有填密碼才更新
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            // TODO: 密碼加密
            staff.setPassword(dto.getPassword());
        }

        staff = staffRepository.save(staff);
        return toDTO(staff);
    }

    @Transactional
    public void deleteStaff(Long id) {
        if (!staffRepository.existsById(id)) {
            throw new BusinessException("員工不存在");
        }
        staffRepository.deleteById(id);
    }

    @Transactional
    public StaffDTO toggleEnabled(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new BusinessException("員工不存在"));
        
        // 如果要啟用，檢查是否超過上限
        if (!staff.getEnabled()) {
            long currentCount = staffRepository.countByEnabledTrue();
            if (currentCount >= MAX_STAFF_COUNT) {
                throw new BusinessException("啟用的員工帳號已達上限 " + MAX_STAFF_COUNT + " 組");
            }
        }
        
        staff.setEnabled(!staff.getEnabled());
        staff = staffRepository.save(staff);
        return toDTO(staff);
    }

    private StaffDTO toDTO(Staff entity) {
        StaffDTO dto = new StaffDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setPassword(null);  // 不回傳密碼
        return dto;
    }
}
