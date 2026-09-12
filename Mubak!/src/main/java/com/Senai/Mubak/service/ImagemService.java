package com.Senai.Mubak.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class ImagemService {

    private static final Set<String> TIPOS_PERMITIDOS = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
    private final Path pastaUpload = Paths.get("uploads").toAbsolutePath().normalize();

    public String salvar(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Selecione uma imagem para o produto.");
        }
        if (!TIPOS_PERMITIDOS.contains(arquivo.getContentType())) {
            throw new IllegalArgumentException("A imagem deve estar em formato JPG, PNG, WEBP ou GIF.");
        }

        String extensao = StringUtils.getFilenameExtension(arquivo.getOriginalFilename());
        String nomeArquivo = UUID.randomUUID() + "." + extensao.toLowerCase();
        try {
            Files.createDirectories(pastaUpload);
            Files.copy(arquivo.getInputStream(), pastaUpload.resolve(nomeArquivo));
            return "/uploads/" + nomeArquivo;
        } catch (IOException exception) {
            throw new IllegalArgumentException("Não foi possível salvar a imagem.", exception);
        }
    }
}