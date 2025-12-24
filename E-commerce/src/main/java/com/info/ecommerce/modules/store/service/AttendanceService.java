package com.info.ecommerce.modules.store.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.store.dto.AttendanceRecordDTO;
import com.info.ecommerce.modules.store.entity.AttendanceRecord;
import com.info.ecommerce.modules.store.entity.Staff;
import com.info.ecommerce.modules.store.repository.AttendanceRecordRepository;
import com.info.ecommerce.modules.store.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final StaffRepository staffRepository;

    /**
     * 上班打卡
     */
    @Transactional
    public AttendanceRecordDTO clockIn(Long staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new BusinessException("員工不存在"));

        LocalDate today = LocalDate.now();
        
        // 檢查今天是否已打過上班卡
        attendanceRecordRepository.findByStaffIdAndWorkDate(staffId, today)
                .ifPresent(record -> {
                    if (record.getClockIn() != null) {
                        throw new BusinessException("今日已打過上班卡");
                    }
                });

        AttendanceRecord record = AttendanceRecord.builder()
                .staff(staff)
                .workDate(today)
                .clockIn(LocalDateTime.now())
                .build();

        record = attendanceRecordRepository.save(record);
        return toDTO(record);
    }

    /**
     * 下班打卡
     */
    @Transactional
    public AttendanceRecordDTO clockOut(Long staffId) {
        LocalDate today = LocalDate.now();
        
        AttendanceRecord record = attendanceRecordRepository
                .findByStaffIdAndWorkDate(staffId, today)
                .orElseThrow(() -> new BusinessException("今日尚未打上班卡"));

        if (record.getClockOut() != null) {
            throw new BusinessException("今日已打過下班卡");
        }

        record.setClockOut(LocalDateTime.now());
        record = attendanceRecordRepository.save(record);
        return toDTO(record);
    }

    /**
     * 查詢員工出勤紀錄
     */
    public List<AttendanceRecordDTO> getRecordsByStaff(Long staffId, LocalDate startDate, LocalDate endDate) {
        return attendanceRecordRepository
                .findByStaffIdAndWorkDateBetween(staffId, startDate, endDate)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 查詢某日全部出勤紀錄
     */
    public List<AttendanceRecordDTO> getRecordsByDate(LocalDate date) {
        return attendanceRecordRepository.findByWorkDate(date)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 補登或修改紀錄（主管用）
     */
    @Transactional
    public AttendanceRecordDTO updateRecord(Long recordId, AttendanceRecordDTO dto) {
        AttendanceRecord record = attendanceRecordRepository.findById(recordId)
                .orElseThrow(() -> new BusinessException("出勤紀錄不存在"));

        if (dto.getClockIn() != null) {
            record.setClockIn(dto.getClockIn());
        }
        if (dto.getClockOut() != null) {
            record.setClockOut(dto.getClockOut());
        }
        if (dto.getNote() != null) {
            record.setNote(dto.getNote());
        }

        record = attendanceRecordRepository.save(record);
        return toDTO(record);
    }

    private AttendanceRecordDTO toDTO(AttendanceRecord entity) {
        AttendanceRecordDTO dto = new AttendanceRecordDTO();
        dto.setId(entity.getId());
        dto.setStaffId(entity.getStaff().getId());
        dto.setStaffName(entity.getStaff().getName());
        dto.setWorkDate(entity.getWorkDate());
        dto.setClockIn(entity.getClockIn());
        dto.setClockOut(entity.getClockOut());
        dto.setNote(entity.getNote());
        return dto;
    }
}
