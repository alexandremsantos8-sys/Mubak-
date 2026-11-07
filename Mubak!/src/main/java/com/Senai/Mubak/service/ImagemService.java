package com.Senai.Mubak.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import java.util.UUID;

@Service
public class ImagemService {

    private static final Set<String> TIPOS_PERMITIDOS = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String supabaseUrl;
    private final String serviceRoleKey;

    public ImagemService(
        @org.springframework.beans.factory.annotation.Value("${supabase.url:}") String supabaseUrl,
        @org.springframework.beans.factory.annotation.Value("${supabase.service-role-key:}") String serviceRoleKey) {
        this.supabaseUrl = supabaseUrl;
        this.serviceRoleKey = serviceRoleKey;
    }

    public String salvar(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Selecione uma imagem para o produto.");
        }
        String tipo = arquivo.getContentType();
        if (!TIPOS_PERMITIDOS.contains(tipo)) {
            throw new IllegalArgumentException("A imagem deve estar em formato JPG, PNG, WEBP ou GIF.");
        }

        String extensao = StringUtils.getFilenameExtension(arquivo.getOriginalFilename());
        if (!StringUtils.hasText(extensao)) {
            extensao = extensaoPorTipo(tipo);
        }
        String nomeArquivo = UUID.randomUUID() + "." + extensao.toLowerCase();
        String urlBase = normalizarUrl(supabaseUrl);
        if (!StringUtils.hasText(urlBase) || !StringUtils.hasText(serviceRoleKey)) {
            throw new IllegalArgumentException("O armazenamento de imagens do Supabase não está configurado.");
        }

        try {
            byte[] conteudo = arquivo.getBytes();
            HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/storage/v1/object/imagens/" + nomeArquivo))
                .header("Authorization", "Bearer " + serviceRoleKey)
                .header("apikey", serviceRoleKey)
                .header("Content-Type", tipo)
                .header("x-upsert", "false")
                .POST(HttpRequest.BodyPublishers.ofByteArray(conteudo))
                .build();

            HttpResponse<String> resposta = httpClient.send(
                requisicao,
                HttpResponse.BodyHandlers.ofString());
            if (resposta.statusCode() < 200 || resposta.statusCode() >= 300) {
                throw new IllegalArgumentException(
                    "Não foi possível salvar a imagem no Supabase. Código HTTP: "
                        + resposta.statusCode() + ". Detalhe: " + resposta.body());
            }
            return urlBase + "/storage/v1/object/public/imagens/" + nomeArquivo;
        } catch (IOException exception) {
            throw new IllegalArgumentException("Não foi possível salvar a imagem.", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalArgumentException("O envio da imagem foi interrompido.", exception);
        }
    }

    private String normalizarUrl(String url) {
        if (url == null) return "";
        return url.replaceFirst("/(?:rest/v1|storage/v1)/?$", "");
    }

    private String extensaoPorTipo(String tipo) {
        return switch (tipo) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            case "image/gif" -> "gif";
            default -> throw new IllegalArgumentException("Tipo de imagem não suportado.");
        };
    }
}