package com.info.ecommerce.modules.system.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.auth.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 上線檢查：目前環境設定是否完整（只回傳狀態，不回傳任何密鑰）
 */
@RestController
@RequestMapping("/api/system/status")
@RequiredArgsConstructor
@Tag(name = "系統狀態", description = "上線前設定檢查")
public class SystemStatusController {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final JwtService jwtService;

    @Value("${app.mail.from:}")
    private String mailFrom;
    @Value("${app.storefront-url:}")
    private String storefrontUrl;
    @Value("${app.admin-store-url:}")
    private String adminStoreUrl;
    @Value("${app.google.client-id:}")
    private String googleClientId;
    @Value("${payment.ecpay.merchant-id:}")
    private String ecpayMerchantId;
    @Value("${payment.ecpay.sandbox:true}")
    private boolean ecpaySandbox;
    @Value("${payment.ecpay.notify-url:}")
    private String ecpayNotifyUrl;
    @Value("${app.order.unpaid-timeout-hours:72}")
    private int unpaidTimeoutHours;
    @Value("${app.payment.ecpay-expire-days:2}")
    private int ecpayExpireDays;

    public record Check(String key, String label, boolean ok, String detail) {}

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "上線設定檢查")
    public ApiResponse<Map<String, Object>> status() {
        List<Check> checks = new ArrayList<>();
        boolean mail = mailSenderProvider.getIfAvailable() != null;
        checks.add(new Check("mail", "寄信（SMTP）", mail,
                mail ? "已設定，會寄送訂單通知、Email 驗證與重設密碼信" : "未設定 SPRING_MAIL_HOST：不會寄出任何通知信，會員也無法完成 Email 驗證"));
        checks.add(new Check("mailFrom", "寄件者地址", !mailFrom.isBlank(),
                mailFrom.isBlank() ? "建議設定 MAIL_FROM，避免被判為垃圾信" : mailFrom));
        checks.add(new Check("jwt", "登入金鑰", !jwtService.isTemporarySecret(),
                jwtService.isTemporarySecret() ? "未設定 JWT_SECRET：重啟後所有人需要重新登入" : "已設定"));
        boolean productionEcpay = !ecpaySandbox && !"2000132".equals(ecpayMerchantId);
        checks.add(new Check("ecpay", "綠界正式商店", productionEcpay,
                productionEcpay ? "正式環境，商店代號 " + ecpayMerchantId : "目前使用綠界測試環境，不會真的收款"));
        boolean notifyOk = ecpayNotifyUrl.startsWith("https://") && !ecpayNotifyUrl.contains("localhost");
        checks.add(new Check("ecpayNotify", "綠界付款通知網址", notifyOk,
                notifyOk ? ecpayNotifyUrl : "ECPAY_NOTIFY_URL 必須是綠界連得到的 HTTPS 網址，否則訂單不會變成已付款（目前：" + ecpayNotifyUrl + "）"));
        boolean storefrontOk = storefrontUrl.startsWith("https://");
        checks.add(new Check("storefrontUrl", "前台網址", storefrontOk,
                storefrontOk ? storefrontUrl : "STOREFRONT_URL 用於付款後導回與通知信連結（目前：" + storefrontUrl + "）"));
        boolean adminStoreOk = adminStoreUrl.startsWith("https://");
        checks.add(new Check("adminStoreUrl", "會員商城網址", adminStoreOk,
                adminStoreOk ? adminStoreUrl : "ADMIN_STORE_URL 用於驗證信、重設密碼信連結（目前：" + adminStoreUrl + "）"));
        checks.add(new Check("google", "Google 登入", !googleClientId.isBlank(),
                googleClientId.isBlank() ? "未設定 GOOGLE_CLIENT_ID，Google 登入停用（選用）" : "已啟用"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("checks", checks);
        result.put("unpaidTimeoutHours", unpaidTimeoutHours);
        result.put("ecpayExpireDays", ecpayExpireDays);
        return ApiResponse.success(result);
    }
}
