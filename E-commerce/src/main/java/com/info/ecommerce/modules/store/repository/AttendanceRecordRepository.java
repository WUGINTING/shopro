package com.info.ecommerce.modules.store.repository;

import com.info.ecommerce.modules.store.entity.AttendanceRecord;
import com.info.ecommerce.modules.store.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findByStaff(Staff staff);

    List<AttendanceRecord> findByStaffId(Long staffId);

    List<AttendanceRecord> findByWorkDate(LocalDate workDate);

    Optional<AttendanceRecord> findByStaffIdAndWorkDate(Long staffId, LocalDate workDate);

    List<AttendanceRecord> findByStaffIdAndWorkDateBetween(Long staffId, LocalDate startDate, LocalDate endDate);

    List<AttendanceRecord> findByWorkDateBetween(LocalDate startDate, LocalDate endDate);
}
