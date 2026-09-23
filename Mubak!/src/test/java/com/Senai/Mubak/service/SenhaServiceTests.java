package com.Senai.Mubak.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

class SenhaServiceTests {

    private final SenhaService senhaService = new SenhaService();

    @Test
    void deveGerarHashSha256Deterministico() {
        assertEquals(
            "2bb80d537b1da3e38bd30361aa855686bde0eacd7162fef6a25fe97bf527a25b",
                senhaService.hash("secret"));
    }

    @Test
    void deveGerarHashesDiferentesParaSenhasDiferentes() {
        assertNotEquals(senhaService.hash("senha-um"), senhaService.hash("senha-dois"));
    }
}
