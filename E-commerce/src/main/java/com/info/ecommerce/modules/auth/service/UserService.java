package com.info.ecommerce.modules.auth.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.auth.dto.UserDTO;
import com.info.ecommerce.modules.auth.entity.User;
import com.info.ecommerce.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for user management operations
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get all users
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get user by ID
     */
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("使用者不存在"));
        return toDTO(user);
    }

    /**
     * Create new user
     */
    @Transactional
    public UserDTO createUser(UserDTO dto) {
        // Check if username exists
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("使用者名稱已存在：" + dto.getUsername());
        }

        // Check if email exists
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email 已存在：" + dto.getEmail());
        }

        // Password is required for new users
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new BusinessException("新增使用者時密碼為必填");
        }

        // Create user
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : true)
                .build();

        User savedUser = userRepository.save(user);
        return toDTO(savedUser);
    }

    /**
     * Update existing user
     */
    @Transactional
    public UserDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("使用者不存在"));

        // Check username uniqueness if changed
        if (!user.getUsername().equals(dto.getUsername()) &&
                userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("使用者名稱已存在：" + dto.getUsername());
        }

        // Check email uniqueness if changed
        if (!user.getEmail().equals(dto.getEmail()) &&
                userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email 已存在：" + dto.getEmail());
        }

        // Update fields
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());

        // Update password if provided and validate length
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            if (dto.getPassword().length() < 6) {
                throw new BusinessException("密碼長度至少需要 6 個字元");
            }
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getEnabled() != null) {
            user.setEnabled(dto.getEnabled());
        }

        User updatedUser = userRepository.save(user);
        return toDTO(updatedUser);
    }

    /**
     * Delete user
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("使用者不存在");
        }
        userRepository.deleteById(id);
    }

    /**
     * Enable or disable user
     */
    @Transactional
    public UserDTO toggleUserStatus(Long id, boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("使用者不存在"));
        
        user.setEnabled(enabled);
        User updatedUser = userRepository.save(user);
        return toDTO(updatedUser);
    }

    /**
     * Convert User entity to UserDTO
     */
    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
