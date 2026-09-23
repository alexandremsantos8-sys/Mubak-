package com.Senai.Mubak.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class DocumentoValidatorTests {

    @Test
    void deveRemoverMascaraDoDocumento() {
        assertEquals("12345678901", DocumentoValidator.digits("123.456.789-01"));
    }

    @Test
    void deveRetornarTextoVazioParaDocumentoNulo() {
        assertEquals("", DocumentoValidator.digits(null));
    }

    @Test
    void deveAceitarCpfOuCnpjPeloNumeroDeDigitos() {
        assertTrue(DocumentoValidator.valido("12345678901"));
        assertTrue(DocumentoValidator.valido("12.345.678/0001-99"));
    }

    @Test
    void deveRecusarDocumentoComQuantidadeDeDigitosInvalida() {
        assertFalse(DocumentoValidator.valido("1234567890"));
        assertFalse(DocumentoValidator.valido(null));
    }
}
