package com.info.ecommerce.modules.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class GoogleTokenVerifierTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private GoogleTokenVerifier verifier;

    @BeforeEach
    void setUp() {
        verifier = new GoogleTokenVerifier();
        ReflectionTestUtils.setField(verifier, "clientId", "my-client.apps.googleusercontent.com");
    }

    @Test
    void parseClaims_acceptsVerifiedTokenForOurClient() throws Exception {
        var user = verifier.parseClaims(mapper.readTree("""
                {"aud":"my-client.apps.googleusercontent.com","iss":"https://accounts.google.com",
                 "email":"buyer@gmail.com","email_verified":"true","name":"Buyer"}"""));

        assertTrue(user.isPresent());
        assertEquals("buyer@gmail.com", user.get().getEmail());
        assertEquals("Buyer", user.get().getName());
    }

    @Test
    void parseClaims_rejectsOtherAudience() throws Exception {
        assertTrue(verifier.parseClaims(mapper.readTree("""
                {"aud":"someone-else","iss":"accounts.google.com","email":"a@b.com","email_verified":"true"}"""))
                .isEmpty());
    }

    @Test
    void parseClaims_rejectsUnverifiedEmailAndWrongIssuer() throws Exception {
        assertTrue(verifier.parseClaims(mapper.readTree("""
                {"aud":"my-client.apps.googleusercontent.com","iss":"accounts.google.com","email":"a@b.com","email_verified":"false"}"""))
                .isEmpty());
        assertTrue(verifier.parseClaims(mapper.readTree("""
                {"aud":"my-client.apps.googleusercontent.com","iss":"evil.example.com","email":"a@b.com","email_verified":"true"}"""))
                .isEmpty());
    }

    @Test
    void verify_disabledWithoutClientId_rejectsForgedToken() {
        ReflectionTestUtils.setField(verifier, "clientId", "");
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"email\":\"admin@example.com\"}".getBytes());
        String forged = "eyJhbGciOiJub25lIn0." + payload + ".sig";

        assertFalse(verifier.isEnabled());
        assertTrue(verifier.verify(forged).isEmpty());
    }
}
