package com.Senai.Mubak.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.Senai.Mubak.model.Usuario;
import com.Senai.Mubak.model.UsuarioForm;
import com.Senai.Mubak.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTests {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SenhaService senhaService;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveCriarRootQuandoAindaNaoExiste() {
        when(usuarioRepository.findByEmailIgnoreCase("root@root.local")).thenReturn(Optional.empty());
        when(senhaService.hash("root")).thenReturn("hash-root");
        when(usuarioRepository.save(org.mockito.ArgumentMatchers.any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.root();

        verify(usuarioRepository).save(org.mockito.ArgumentMatchers.argThat(usuario ->
                "root".equals(usuario.getNome())
                        && "root@root.local".equals(usuario.getEmail())
                        && "ROOT".equals(usuario.getPerfil())
                        && "hash-root".equals(usuario.getSenhaHash())));
    }

    @Test
    void naoDeveCriarRootQuandoJaExiste() {
        when(usuarioRepository.findByEmailIgnoreCase("root@root.local"))
                .thenReturn(Optional.of(new Usuario()));

        usuarioService.root();

        verify(usuarioRepository, org.mockito.Mockito.never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deveCadastrarClienteNormalizandoDados() {
        UsuarioForm form = formularioValido();
        when(senhaService.hash("senha-segura")).thenReturn("hash");
        when(usuarioRepository.save(org.mockito.ArgumentMatchers.any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.cadastrar(form);

        assertEquals("Nome do cliente", resultado.getNome());
        assertEquals("12345678901", resultado.getDocumento());
        assertEquals("cliente@exemplo.com", resultado.getEmail());
        assertEquals("CLIENTE", resultado.getPerfil());
        assertEquals("hash", resultado.getSenhaHash());
    }

    @Test
    void deveRecusarCadastroComDocumentoInvalido() {
        UsuarioForm form = formularioValido();
        form.setDocumento("123");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.cadastrar(form));

        assertEquals("CPF ou CNPJ inválido.", exception.getMessage());
        verifyNoInteractions(usuarioRepository, senhaService);
    }

    @Test
    void deveRecusarEmailsDiferentes() {
        UsuarioForm form = formularioValido();
        form.setConfirmacaoEmail("outro@exemplo.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.cadastrar(form));

        assertEquals("Os e-mails não conferem.", exception.getMessage());
    }

    @Test
    void deveAutenticarPorEmailDocumentoERoot() {
        Usuario usuario = usuarioSalvo();
        when(senhaService.hash("senha-segura")).thenReturn("hash");
        when(usuarioRepository.findByEmailIgnoreCase("cliente@exemplo.com")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByDocumento("12345678901")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmailIgnoreCase("root@root.local")).thenReturn(Optional.of(usuario));

        assertEquals(usuario, usuarioService.login("cliente@exemplo.com", "senha-segura"));
        assertEquals(usuario, usuarioService.login("123.456.789-01", "senha-segura"));
        assertEquals(usuario, usuarioService.login("root", "senha-segura"));
    }

    @Test
    void deveRecusarLoginComUsuarioOuSenhaInvalida() {
        when(usuarioRepository.findByEmailIgnoreCase("inexistente@exemplo.com")).thenReturn(Optional.empty());
        when(senhaService.hash("senha-errada")).thenReturn("hash-errado");

        assertEquals(IllegalArgumentException.class, assertThrows(IllegalArgumentException.class,
            () -> usuarioService.login("inexistente@exemplo.com", "senha-errada")).getClass());

        Usuario usuario = usuarioSalvo();
        when(usuarioRepository.findByEmailIgnoreCase("cliente@exemplo.com")).thenReturn(Optional.of(usuario));
        assertEquals(IllegalArgumentException.class, assertThrows(IllegalArgumentException.class,
            () -> usuarioService.login("cliente@exemplo.com", "senha-errada")).getClass());
    }

    @Test
    void deveListarUsuarios() {
        List<Usuario> usuarios = List.of(new Usuario());
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        assertEquals(usuarios, usuarioService.listar());
    }

    private UsuarioForm formularioValido() {
        UsuarioForm form = new UsuarioForm();
        form.setNome("  Nome do cliente  ");
        form.setDocumento("123.456.789-01");
        form.setEmail(" Cliente@Exemplo.com ");
        form.setConfirmacaoEmail(" Cliente@Exemplo.com ");
        form.setSenha("senha-segura");
        form.setConfirmacaoSenha("senha-segura");
        form.setDataNascimento(LocalDate.of(2000, 1, 1));
        return form;
    }

    private Usuario usuarioSalvo() {
        Usuario usuario = new Usuario();
        usuario.setEmail("cliente@exemplo.com");
        usuario.setDocumento("12345678901");
        usuario.setSenhaHash("hash");
        return usuario;
    }
}
