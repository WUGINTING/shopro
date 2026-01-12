package com.info.ecommerce.modules.system.repository;

import com.info.ecommerce.modules.system.entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Long> {
    
    List<Settings> findBySettingType(String settingType);
    
    Optional<Settings> findBySettingTypeAndSettingKey(String settingType, String settingKey);
    
    void deleteBySettingType(String settingType);
}

