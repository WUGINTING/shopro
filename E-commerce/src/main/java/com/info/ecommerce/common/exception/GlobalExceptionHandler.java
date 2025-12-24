package com.info.ecommerce.common.exception;

import com.info.ecommerce.common.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(message));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String message = "資料完整性錯誤";
        
        // 解析錯誤訊息，提供更友善的錯誤提示
        String errorMessage = e.getMessage();
        if (errorMessage != null) {
            if (errorMessage.contains("unique index") || errorMessage.contains("duplicate key")) {
                if (errorMessage.contains("sku") || errorMessage.contains("UKq1mafxn973ldq80m1irp3mpvq")) {
                    message = "商品編號（SKU）已存在，請使用其他編號";
                } else {
                    message = "資料重複，請檢查輸入的內容";
                }
            } else if (errorMessage.contains("foreign key")) {
                message = "資料關聯錯誤，請確認相關資料是否存在";
            } else if (errorMessage.contains("not null")) {
                message = "必填欄位不能為空";
            }
        }
        
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("系統發生錯誤，請稍後再試"));
    }
}
