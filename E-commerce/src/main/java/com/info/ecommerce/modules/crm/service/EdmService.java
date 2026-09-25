package com.info.ecommerce.modules.crm.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.crm.dto.EdmCampaignDTO;
import com.info.ecommerce.modules.crm.entity.EdmCampaign;
import com.info.ecommerce.modules.crm.entity.EdmSendLog;
import com.info.ecommerce.modules.crm.entity.Member;
import com.info.ecommerce.modules.crm.enums.EdmStatus;
import com.info.ecommerce.modules.crm.repository.EdmCampaignRepository;
import com.info.ecommerce.modules.crm.repository.EdmSendLogRepository;
import com.info.ecommerce.modules.crm.repository.MemberRepository;
import com.info.ecommerce.modules.auth.service.JwtService;
import com.info.ecommerce.modules.crm.enums.MemberStatus;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EdmService {

    private final EdmCampaignRepository edmCampaignRepository;
    private final EdmSendLogRepository edmSendLogRepository;
    private final MemberRepository memberRepository;
    private final MemberGroupService memberGroupService;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final JwtService jwtService;

    static final String UNSUBSCRIBE_PURPOSE = "edm-unsubscribe";
    private static final long UNSUBSCRIBE_TOKEN_TTL_MILLIS = 365L * 24 * 60 * 60 * 1000;

    @Value("${app.mail.from:}")
    private String mailFrom;

    @Value("${app.mail.store-name:遇日小舖}")
    private String storeName;

    @Value("${app.storefront-url:}")
    private String storefrontUrl;

    @Transactional
    public EdmCampaignDTO createEdmCampaign(EdmCampaignDTO dto) {
        EdmCampaign edmCampaign = new EdmCampaign();
        BeanUtils.copyProperties(dto, edmCampaign, "id");
        edmCampaign = edmCampaignRepository.save(edmCampaign);
        return toDTO(edmCampaign);
    }

    @Transactional
    public EdmCampaignDTO updateEdmCampaign(Long id, EdmCampaignDTO dto) {
        EdmCampaign edmCampaign = edmCampaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("EDM 活動不存在"));

        if (edmCampaign.getStatus() == EdmStatus.SENT) {
            throw new BusinessException("已發送的 EDM 活動無法修改");
        }

        BeanUtils.copyProperties(dto, edmCampaign, "id", "createdAt", "updatedAt", "sentAt", "totalSent", "successCount", "failureCount");
        edmCampaign = edmCampaignRepository.save(edmCampaign);
        return toDTO(edmCampaign);
    }

    public EdmCampaignDTO getEdmCampaign(Long id) {
        EdmCampaign edmCampaign = edmCampaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("EDM 活動不存在"));
        return toDTO(edmCampaign);
    }

    @Transactional
    public void deleteEdmCampaign(Long id) {
        EdmCampaign edmCampaign = edmCampaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("EDM 活動不存在"));

        if (edmCampaign.getStatus() == EdmStatus.SENT || edmCampaign.getStatus() == EdmStatus.SENDING) {
            throw new BusinessException("已發送或發送中的 EDM 活動無法刪除");
        }

        edmCampaignRepository.deleteById(id);
    }

    public Page<EdmCampaignDTO> listEdmCampaigns(Pageable pageable) {
        return edmCampaignRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<EdmCampaignDTO> listEdmCampaignsByStatus(EdmStatus status, Pageable pageable) {
        return edmCampaignRepository.findByStatus(status, pageable).map(this::toDTO);
    }

    @Transactional
    public EdmCampaignDTO scheduleEdmCampaign(Long id, LocalDateTime scheduledAt) {
        EdmCampaign edmCampaign = edmCampaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("EDM 活動不存在"));

        if (edmCampaign.getStatus() != EdmStatus.DRAFT) {
            throw new BusinessException("只有草稿狀態的 EDM 活動可以排程");
        }

        edmCampaign.setScheduledAt(scheduledAt);
        edmCampaign.setStatus(EdmStatus.SCHEDULED);
        edmCampaign = edmCampaignRepository.save(edmCampaign);
        return toDTO(edmCampaign);
    }

    /**
     * 發送 EDM。刻意不包在單一交易內：先以條件式更新把活動標成「發送中」並立即提交（避免重複發送，
     * 也不會在寄信期間長時間鎖住活動資料列），逐封寄送並記錄，最後更新結果。
     */
    public EdmCampaignDTO sendEdmCampaign(Long id) {
        EdmCampaign edmCampaign = edmCampaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("EDM 活動不存在"));

        if (edmCampaign.getStatus() == EdmStatus.SENT) {
            throw new BusinessException("EDM 活動已發送");
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new BusinessException("尚未設定寄信（SMTP），無法發送 EDM");
        }

        // 取得目標會員：只寄給狀態正常、且同意接收行銷 Email 的會員（同一 Email 只寄一次）
        List<Member> candidates;
        if (edmCampaign.getTargetGroupId() != null) {
            List<Long> memberIds = memberGroupService.getGroupMembers(edmCampaign.getTargetGroupId());
            candidates = memberRepository.findAllById(memberIds);
        } else {
            candidates = memberRepository.findAll();
        }
        java.util.Set<String> seenEmails = new java.util.HashSet<>();
        List<Member> targetMembers = candidates.stream()
                .filter(member -> Boolean.TRUE.equals(member.getMarketingOptIn()))
                .filter(member -> member.getStatus() == null || member.getStatus() == MemberStatus.ACTIVE)
                .filter(member -> member.getEmail() != null && !member.getEmail().isBlank())
                .filter(member -> seenEmails.add(member.getEmail().trim().toLowerCase(java.util.Locale.ROOT)))
                .toList();
        if (targetMembers.isEmpty()) {
            throw new BusinessException("沒有符合條件的收件人：EDM 只會寄給狀態正常且同意接收優惠通知的會員");
        }

        if (edmCampaignRepository.claimForSending(id) == 0) {
            throw new BusinessException("EDM 活動正在發送、已發送或已取消");
        }
        edmCampaign.setStatus(EdmStatus.SENDING);
        try {
            return deliver(id, edmCampaign, targetMembers, mailSender);
        } catch (RuntimeException e) {
            // 非預期錯誤：不要讓活動永遠停在「發送中」
            edmCampaign.setStatus(EdmStatus.FAILED);
            edmCampaignRepository.save(edmCampaign);
            throw e;
        }
    }

    private EdmCampaignDTO deliver(Long id, EdmCampaign edmCampaign, List<Member> targetMembers, JavaMailSender mailSender) {
        int successCount = 0;
        int failureCount = 0;

        for (Member member : targetMembers) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
                if (mailFrom != null && !mailFrom.isBlank()) {
                    helper.setFrom(mailFrom, storeName);
                }
                helper.setTo(member.getEmail().trim());
                helper.setSubject(edmCampaign.getSubject() != null ? edmCampaign.getSubject() : edmCampaign.getName());
                helper.setText(withUnsubscribeFooter(edmCampaign.getContent(), member), true);
                mailSender.send(message);
                edmSendLogRepository.save(EdmSendLog.builder()
                        .campaignId(id)
                        .memberId(member.getId())
                        .recipientEmail(member.getEmail())
                        .success(true)
                        .build());
                successCount++;
            } catch (Exception e) {
                edmSendLogRepository.save(EdmSendLog.builder()
                        .campaignId(id)
                        .memberId(member.getId())
                        .recipientEmail(member.getEmail())
                        .success(false)
                        .errorMessage(e.getMessage())
                        .build());
                failureCount++;
            }
        }

        edmCampaign.setStatus(EdmStatus.SENT);
        edmCampaign.setSentAt(LocalDateTime.now());
        edmCampaign.setTotalSent(targetMembers.size());
        edmCampaign.setSuccessCount(successCount);
        edmCampaign.setFailureCount(failureCount);
        edmCampaign = edmCampaignRepository.save(edmCampaign);

        return toDTO(edmCampaign);
    }

    @Transactional
    public EdmCampaignDTO cancelEdmCampaign(Long id) {
        EdmCampaign edmCampaign = edmCampaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("EDM 活動不存在"));

        if (edmCampaign.getStatus() == EdmStatus.SENT) {
            throw new BusinessException("已發送的 EDM 活動無法取消");
        }

        edmCampaign.setStatus(EdmStatus.CANCELLED);
        edmCampaign = edmCampaignRepository.save(edmCampaign);
        return toDTO(edmCampaign);
    }

    public Page<EdmSendLog> getEdmSendLogs(Long campaignId, Pageable pageable) {
        return edmSendLogRepository.findByCampaignId(campaignId, pageable);
    }

    /** EDM 內容加上退訂說明與一鍵退訂連結（個資法：行銷信需提供拒絕接收的方式） */
    private String withUnsubscribeFooter(String content, Member member) {
        String token = jwtService.generatePurposeToken(UNSUBSCRIBE_PURPOSE, String.valueOf(member.getId()),
                java.util.Map.of(), UNSUBSCRIBE_TOKEN_TTL_MILLIS);
        String base = storefrontUrl == null ? "" : storefrontUrl.replaceAll("/+$", "");
        String link = base + "/shop/unsubscribe?token=" + java.net.URLEncoder.encode(token, java.nio.charset.StandardCharsets.UTF_8);
        return (content == null ? "" : content)
                + "<hr style=\"margin-top:32px;border:none;border-top:1px solid #eee\">"
                + "<p style=\"font-size:12px;color:#888\">您收到這封信是因為您同意接收" + escape(storeName)
                + "的優惠與新品通知。若不想再收到，請<a href=\"" + link + "\">按此取消訂閱</a>。</p>";
    }

    private static String escape(String text) {
        return text == null ? "" : text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /**
     * 以 EDM 信中的退訂連結取消訂閱
     */
    @Transactional
    public void unsubscribe(String token) {
        io.jsonwebtoken.Claims claims = jwtService.parsePurposeToken(token, UNSUBSCRIBE_PURPOSE)
                .orElseThrow(() -> new BusinessException("退訂連結無效或已過期"));
        memberRepository.findById(Long.valueOf(claims.getSubject())).ifPresent(member -> {
            member.setMarketingOptIn(false);
            memberRepository.save(member);
        });
    }

    private EdmCampaignDTO toDTO(EdmCampaign edmCampaign) {
        EdmCampaignDTO dto = new EdmCampaignDTO();
        BeanUtils.copyProperties(edmCampaign, dto);
        return dto;
    }
}
