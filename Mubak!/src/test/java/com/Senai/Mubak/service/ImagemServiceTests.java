package com.Senai.Mubak.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class ImagemServiceTests {

    private final ImagemService imagemService = new ImagemService();

    @Test
    void deveRecusarArquivoAusente() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> imagemService.salvar(null));

        assertEquals("Selecione uma imagem para o produto.", exception.getMessage());
    }

    @Test
    void deveRecusarTipoNaoPermitido() {
        MultipartFile arquivo = new MockMultipartFile("arquivo", "arquivo.txt", "text/plain", "conteudo".getBytes());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> imagemService.salvar(arquivo));

        assertEquals("A imagem deve estar em formato JPG, PNG, WEBP ou GIF.", exception.getMessage());
    }

    @Test
    void deveSalvarImagemPermitida() throws IOException {
        MultipartFile arquivo = new MockMultipartFile("arquivo", "produto.PNG", "image/png", "conteudo".getBytes());

        String caminho = imagemService.salvar(arquivo);
        Path arquivoSalvo = Paths.get("uploads", caminho.substring("/uploads/".length()));

        try {
            assertTrue(caminho.startsWith("/uploads/"));
            assertTrue(caminho.endsWith(".png"));
            assertTrue(Files.exists(arquivoSalvo));
            assertEquals("conteudo", Files.readString(arquivoSalvo));
        } finally {
            Files.deleteIfExists(arquivoSalvo);
        }
    }

    @Test
    void deveConverterFalhaDeLeituraEmErroDeValidacao() {
        MultipartFile arquivo = new MultipartFile() {
            @Override public String getName() { return "arquivo"; }
            @Override public String getOriginalFilename() { return "produto.png"; }
            @Override public String getContentType() { return "image/png"; }
            @Override public boolean isEmpty() { return false; }
            @Override public long getSize() { return 1; }
            @Override public byte[] getBytes() { return new byte[] {1}; }
            @Override public java.io.InputStream getInputStream() throws IOException { throw new IOException("falha"); }
            @Override public Resource getResource() { return new MockMultipartFile("arquivo", new byte[] {1}).getResource(); }
            @Override public void transferTo(File destination) throws IOException { throw new IOException("falha"); }
        };

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> imagemService.salvar(arquivo));

        assertEquals("Não foi possível salvar a imagem.", exception.getMessage());
        assertTrue(exception.getCause() instanceof IOException);
    }
}
