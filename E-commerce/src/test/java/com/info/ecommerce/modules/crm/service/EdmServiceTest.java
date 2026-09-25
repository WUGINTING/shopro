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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EdmService
 */
@ExtendWith(MockitoExtension.class)
class EdmServiceTest {

    @Mock
    private EdmCampaignRepository edmCampaignRepository;

    @Mock
    private EdmSendLogRepository edmSendLogRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberGroupService memberGroupService;

    @InjectMocks
    private EdmService edmService;

    @Mock
    private org.springframework.beans.factory.ObjectProvider<org.springframework.mail.javamail.JavaMailSender> mailSenderProvider;

    @Mock
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    @Mock
    private com.info.ecommerce.modules.auth.service.JwtService jwtService;

    private EdmCampaign edmCampaign;
    private EdmCampaignDTO edmCampaignDTO;
    private Member member;

    @BeforeEach
    void setUp() {
        edmCampaign = EdmCampaign.builder()
                .id(1L)
                .name("Test Campaign")
                .subject("Test Subject")
                .content("Test Content")
                .status(EdmStatus.DRAFT)
                .targetGroupId(null)
                .totalSent(0)
                .successCount(0)
                .failureCount(0)
                .build();

        edmCampaignDTO = new EdmCampaignDTO();
        edmCampaignDTO.setName("Test Campaign");
        edmCampaignDTO.setSubject("Test Subject");
        edmCampaignDTO.setContent("Test Content");
        edmCampaignDTO.setStatus(EdmStatus.DRAFT);

        member = Member.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test Member")
                .marketingOptIn(true)
                .build();
        org.mockito.Mockito.lenient().when(mailSenderProvider.getIfAvailable()).thenReturn(mailSender);
        org.mockito.Mockito.lenient().when(mailSender.createMimeMessage())
                .thenAnswer(invocation -> new jakarta.mail.internet.MimeMessage((jakarta.mail.Session) null));
        org.mockito.Mockito.lenient().when(jwtService.generatePurposeToken(any(), any(), any(), anyLong())).thenReturn("token");
        org.mockito.Mockito.lenient().when(edmCampaignRepository.claimForSending(anyLong())).thenReturn(1);
    }

    @Test
    void should_CreateEdmCampaign_When_ValidDataProvided() {
        // given
        when(edmCampaignRepository.save(any(EdmCampaign.class))).thenReturn(edmCampaign);

        // when
        EdmCampaignDTO result = edmService.createEdmCampaign(edmCampaignDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Campaign");
        assertThat(result.getSubject()).isEqualTo("Test Subject");
        verify(edmCampaignRepository, times(1)).save(any(EdmCampaign.class));
    }

    @Test
    void should_UpdateEdmCampaign_When_CampaignExistsAndNotSent() {
        // given
        when(edmCampaignRepository.findById(1L)).thenReturn(Optional.of(edmCampaign));
        when(edmCampaignRepository.save(any(EdmCampaign.class))).thenReturn(edmCampaign);

        EdmCampaignDTO updateDTO = new EdmCampaignDTO();
        updateDTO.setName("Updated Campaign");
        updateDTO.setSubject("Updated Subject");

        // when
        EdmCampaignDTO result = edmService.updateEdmCampaign(1L, updateDTO);

        // then
        assertThat(result).isNotNull();
        verify(edmCampaignRepository, times(1)).findById(1L);
        verify(edmCampaignRepository, times(1)).save(any(EdmCampaign.class));
    }

    @Test
    void should_ThrowBusinessException_When_UpdateNonExistentCampaign() {
        // given
        when(edmCampaignRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> edmService.updateEdmCampaign(1L, edmCampaignDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("EDM 活動不存在");
        verify(edmCampaignRepository, times(1)).findById(1L);
        verify(edmCampaignRepository, never()).save(any(EdmCampaign.class));
    }

    @Test
    void should_ThrowBusinessException_When_UpdateSentCampaign() {
        // given
        edmCampaign.setStatus(EdmStatus.SENT);
        when(edmCampaignRepository.findById(1L)).thenReturn(Optional.of(edmCampaign));

        // when & then
        assertThatThrownBy(() -> edmService.updateEdmCampaign(1L, edmCampaignDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("已發送的 EDM 活動無法修改");
        verify(edmCampaignRepository, times(1)).findById(1L);
        verify(edmCampaignRepository, never()).save(any(EdmCampaign.class));
    }

    @Test
    void should_GetEdmCampaign_When_CampaignExists() {
        // given
        when(edmCampaignRepository.findById(1L)).thenReturn(Optional.of(edmCampaign));

        // when
        EdmCampaignDTO result = edmService.getEdmCampaign(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Campaign");
        verify(edmCampaignRepository, times(1)).findById(1L);
    }

    @Test
    void should_ThrowBusinessException_When_GetNonExistentCampaign() {
        // given
        when(edmCampaignRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> edmService.getEdmCampaign(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("EDM 活動不存在");
        verify(edmCampaignRepository, times(1)).findById(1L);
    }

    @Test
    void should_DeleteEdmCampaign_When_CampaignNotSent() {
        // given
        when(edmCampaignRepository.findById(1L)).thenReturn(Optional.of(edmCampaign));

        // when
        edmService.deleteEdmCampaign(1L);

        // then
        verify(edmCampaignRepository, times(1)).findById(1L);
        verify(edmCampaignRepository, times(1)).deleteById(1L);
    }

    @ParameterizedTest
    @EnumSource(value = EdmStatus.class, names = {"SENT", "SENDING"})
    void should_ThrowBusinessException_When_DeleteCampaignInNonDeletableStatus(EdmStatus status) {
        // given
        edmCampaign.setStatus(status);
        when(edmCampaignRepository.findById(1L)).thenReturn(Optional.of(edmCampaign));

        // when & then
        assertThatThrownBy(() -> edmService.deleteEdmCampaign(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("已發送或發送中的 EDM 活動無法刪除");
        verify(edmCampaignRepository, times(1)).findById(1L);
        verify(edmCampaignRepository, never()).deleteById(anyLong());
    }

    @Test
    void should_ListEdmCampaigns_When_RequestingPage() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<EdmCampaign> campaigns = List.of(edmCampaign);
        Page<EdmCampaign> page = new PageImpl<>(campaigns, pageable, campaigns.size());
        when(edmCampaignRepository.findAll(pageable)).thenReturn(page);

        // when
        Page<EdmCampaignDTO> result = edmService.listEdmCampaigns(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Campaign");
        verify(edmCampaignRepository, times(1)).findAll(pageable);
    }

    @Test
    void should_ListEdmCampaignsByStatus_When_RequestingPageWithStatus() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<EdmCampaign> campaigns = List.of(edmCampaign);
        Page<EdmCampaign> page = new PageImpl<>(campaigns, pageable, campaigns.size());
        when(edmCampaignRepository.findByStatus(EdmStatus.DRAFT, pageable)).thenReturn(page);

        // when
        Page<EdmCampaignDTO> result = edmService.listEdmCampaignsByStatus(EdmStatus.DRAFT, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(edmCampaignRepository, times(1)).findByStatus(EdmStatus.DRAFT, pageable);
    }

    @Test
    void should_ScheduleEdmCampaign_When_CampaignIsDraft() {
        // given
        LocalDateTime scheduledTime = LocalDateTime.now().plusDays(1);
        when(edmCampaignRepository.findById(1L)).thenReturn(Optional.of(edmCampaign));
        when(edmCampaignRepository.save(any(EdmCampaign.class))).thenReturn(edmCampaign);

        // when
        EdmCampaignDTO result = edmService.scheduleEdmCampaign(1L, scheduledTime);

        // then
        assertThat(result).isNotNull();
        verify(edmCampaignRepository, times(1)).findById(1L);
        verify(edmCampaignRepository, times(1)).save(any(EdmCampaign.class));
    }

    @Test
    void should_ThrowBusinessException_When_ScheduleNonDraftCampaign() {
        // given
        edmCampaign.setStatus(EdmStatus.SCHEDULED);
        LocalDateTime scheduledTime = LocalDateTime.now().plusDays(1);
        when(edmCampaignRepository.findById(1L)).thenReturn(Optional.of(edmCampaign));

        // when & then
        assertThatThrownBy(() -> edmService.scheduleEdmCampaign(1L, scheduledTime))
                .isInstanceOf(BusinessException.class)
                .hasMessage("只有草稿狀態的 EDM 活動可以排程");
        verify(edmCampaignRepository, times(1)).findById(1L);
        verify(edmCampaignRepository, never()).save(any(EdmCampaign.class));
    }

    @Test
    void should_SendEdmCampaign_When_CampaignNotSent() {
        // given
        when(edmCampaignRepository.findById(1L)).thenReturn(Optional.of(edmCampaign));
        when(edmCampaignRepository.save(any(EdmCampaign.class))).thenReturn(edmCampaign);
        when(memberRepository.findAll()).thenReturn(List.of(member));
        when(edmSendLogRepository.save(any(EdmSendLog.class))).thenReturn(new EdmSendLog());

        // when
        EdmCampaignDTO result = edmService.sendEdmCampaign(1L);

        // then
        assertThat(result).isNotNull();
        verify(edmCampaignRepository, times(1)).findById(1L);
        verify(edmCampaignRepository, times(1)).claimForSending(1L);
        verify(edmCampaignRepository, times(1)).save(any(EdmCampaign.class));
        verify(memberRepository, times(1)).findAll();
        verify(edmSendLogRepository, atLeastOnce()).save(any(EdmSendLog.class));
    }

    @Test
    void should_ThrowBusinessException_When_SendAlreadySentCampaign() {
        // given
        edmCampaign.setStatus(EdmStatus.SENT);
        when(edmCampaignRepository.findById(1L)).thenReturn(Optional.of(edmCampaign));

        // when & then
        assertThatThrownBy(() -> edmService.sendEdmCampaign(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("EDM 活動已發送");
        verify(edmCampaignRepository, times(1)).findById(1L);
        verify(memberRepository, never()).findAll();
    }

    @Test
    void should_NotSendTwice_When_AnotherSendAlreadyClaimedTheCampaign() {
        when(edmCampaignRepository.findById(1L)).thenReturn(Optional.of(edmCampaign));
        when(memberRepository.findAll()).thenReturn(List.of(member));
        when(edmCampaignRepository.claimForSending(1L)).thenReturn(0);

        assertThatThrownBy(() -> edmService.sendEdmCampaign(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("正在發送");
        verify(mailSender, never()).send(any(jakarta.mail.internet.MimeMessage.class));
    }

    @Test
    void should_SendEdmCampaignToTargetGroup_When_TargetGroupIdProvided() {
        // given
        edmCampaign.setTargetGroupId(1L);
        List<Long> memberIds = List.of(1L);
        when(edmCampaignRepository.findById(1L)).thenReturn(Optional.of(edmCampaign));
        when(edmCampaignRepository.save(any(EdmCampaign.class))).thenReturn(edmCampaign);
        when(memberGroupService.getGroupMembers(1L)).thenReturn(memberIds);
        when(memberRepository.findAllById(memberIds)).thenReturn(List.of(member));
        when(edmSendLogRepository.save(any(EdmSendLog.class))).thenReturn(new EdmSendLog());

        // when
        EdmCampaignDTO result = edmService.sendEdmCampaign(1L);

        // then
        assertThat(result).isNotNull();
        verify(memberGroupService, times(1)).getGroupMembers(1L);
        verify(memberRepository, times(1)).findAllById(memberIds);
        verify(edmSendLogRepository, atLeastOnce()).save(any(EdmSendLog.class));
    }

    @Test
    void should_CancelEdmCampaign_When_CampaignNotSent() {
        // given
        when(edmCampaignRepository.findById(1L)).thenReturn(Optional.of(edmCampaign));
        when(edmCampaignRepository.save(any(EdmCampaign.class))).thenReturn(edmCampaign);

        // when
        EdmCampaignDTO result = edmService.cancelEdmCampaign(1L);

        // then
        assertThat(result).isNotNull();
        verify(edmCampaignRepository, times(1)).findById(1L);
        verify(edmCampaignRepository, times(1)).save(any(EdmCampaign.class));
    }

    @Test
    void should_ThrowBusinessException_When_CancelSentCampaign() {
        // given
        edmCampaign.setStatus(EdmStatus.SENT);
        when(edmCampaignRepository.findById(1L)).thenReturn(Optional.of(edmCampaign));

        // when & then
        assertThatThrownBy(() -> edmService.cancelEdmCampaign(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("已發送的 EDM 活動無法取消");
        verify(edmCampaignRepository, times(1)).findById(1L);
        verify(edmCampaignRepository, never()).save(any(EdmCampaign.class));
    }

    @Test
    void should_GetEdmSendLogs_When_RequestingByCampaignId() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<EdmSendLog> logs = new ArrayList<>();
        Page<EdmSendLog> page = new PageImpl<>(logs, pageable, logs.size());
        when(edmSendLogRepository.findByCampaignId(1L, pageable)).thenReturn(page);

        // when
        Page<EdmSendLog> result = edmService.getEdmSendLogs(1L, pageable);

        // then
        assertThat(result).isNotNull();
        verify(edmSendLogRepository, times(1)).findByCampaignId(1L, pageable);
    }

    @Test
    void should_OnlyMailOptedInActiveMembers_AndIncludeUnsubscribeLink() throws Exception {
        Member noConsent = Member.builder().id(2L).email("no@example.com").marketingOptIn(false).build();
        Member suspended = Member.builder().id(3L).email("s@example.com").marketingOptIn(true)
                .status(com.info.ecommerce.modules.crm.enums.MemberStatus.SUSPENDED).build();
        Member duplicate = Member.builder().id(4L).email("TEST@example.com").marketingOptIn(true).build();
        when(edmCampaignRepository.findById(1L)).thenReturn(Optional.of(edmCampaign));
        when(edmCampaignRepository.save(any(EdmCampaign.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(memberRepository.findAll()).thenReturn(List.of(member, noConsent, suspended, duplicate));

        EdmCampaignDTO result = edmService.sendEdmCampaign(1L);

        org.mockito.ArgumentCaptor<jakarta.mail.internet.MimeMessage> sent =
                org.mockito.ArgumentCaptor.forClass(jakarta.mail.internet.MimeMessage.class);
        verify(mailSender, times(1)).send(sent.capture());
        assertThat(sent.getValue().getAllRecipients()[0].toString()).isEqualTo("test@example.com");
        sent.getValue().saveChanges();
        assertThat(sent.getValue().getContent().toString()).contains("/shop/unsubscribe?token=token");
        assertThat(result.getSuccessCount()).isEqualTo(1);
    }

    @Test
    void should_RefuseToSend_When_MailNotConfiguredOrNoRecipients() {
        when(edmCampaignRepository.findById(1L)).thenReturn(Optional.of(edmCampaign));
        when(mailSenderProvider.getIfAvailable()).thenReturn(null);
        assertThatThrownBy(() -> edmService.sendEdmCampaign(1L)).isInstanceOf(BusinessException.class)
                .hasMessageContaining("SMTP");

        when(mailSenderProvider.getIfAvailable()).thenReturn(mailSender);
        when(memberRepository.findAll()).thenReturn(List.of(Member.builder().id(9L).email("x@example.com").build()));
        assertThatThrownBy(() -> edmService.sendEdmCampaign(1L)).isInstanceOf(BusinessException.class)
                .hasMessageContaining("同意接收");
        verify(edmCampaignRepository, never()).save(any());
    }
}
