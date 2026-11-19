package br.com.uninter.raizesnordeste.service;

import br.com.uninter.raizesnordeste.application.dto.CadastroUsuarioDTO;
import br.com.uninter.raizesnordeste.application.dto.LoginRequestDTO;
import br.com.uninter.raizesnordeste.application.dto.LoginResponseDTO;
import br.com.uninter.raizesnordeste.application.service.AuditoriaService;
import br.com.uninter.raizesnordeste.application.service.AuthService;
import br.com.uninter.raizesnordeste.domain.entity.ConsentimentoLgpd;
import br.com.uninter.raizesnordeste.domain.entity.Usuario;
import br.com.uninter.raizesnordeste.domain.enums.PerfilEnum;
import br.com.uninter.raizesnordeste.domain.repository.ConsentimentoLgpdRepository;
import br.com.uninter.raizesnordeste.domain.repository.UsuarioRepository;
import br.com.uninter.raizesnordeste.infrastructure.exception.RegraNegocioException;
import br.com.uninter.raizesnordeste.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes Unitários de Autenticação e LGPD com JUnit 5 e Mockito.
 * Testando login, senhas e consentimento obrigatório da LGPD.
 */
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ConsentimentoLgpdRepository consentimentoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioTeste;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "jwtExpirationInMs", 86400000L);

        usuarioTeste = new Usuario(
                1L,
                "Ana Silva",
                "ana.silva@email.com",
                "$2a$10$hashSimuladoBCrypt",
                PerfilEnum.CLIENTE,
                "81988887777",
                true
        );
    }

    @Test
    @DisplayName("T01 - Deve autenticar com sucesso quando as credenciais forem válidas e retornar token")
    void deveAutenticarComSucessoQuandoCredenciaisValidas() {
        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO("ana.silva@email.com", "Senha@123");

        when(usuarioRepository.findByEmail("ana.silva@email.com")).thenReturn(Optional.of(usuarioTeste));
        when(passwordEncoder.matches("Senha@123", usuarioTeste.getSenha())).thenReturn(true);
        when(tokenProvider.gerarToken(any(), any(), any(), any())).thenReturn("mocked.jwt.token");

        // Act
        LoginResponseDTO response = authService.login(dto);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertEquals("mocked.jwt.token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("Ana Silva", response.getUser().getNome());
        assertEquals("CLIENTE", response.getUser().getPerfil());

        verify(usuarioRepository, times(1)).findByEmail("ana.silva@email.com");
        verify(passwordEncoder, times(1)).matches("Senha@123", usuarioTeste.getSenha());
    }

    @Test
    @DisplayName("T01.2 - Deve lançar BadCredentialsException quando a senha informada for incorreta")
    void deveLancarExcecaoQuandoSenhaInvalida() {
        LoginRequestDTO dto = new LoginRequestDTO("ana.silva@email.com", "SenhaErrada123");

        when(usuarioRepository.findByEmail("ana.silva@email.com")).thenReturn(Optional.of(usuarioTeste));
        when(passwordEncoder.matches("SenhaErrada123", usuarioTeste.getSenha())).thenReturn(false);

        // Deve lançar a exceção de credencial inválida
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.login(dto);
        });

        assertEquals("Credenciais inválidas.", exception.getMessage());
        verify(tokenProvider, never()).gerarToken(any(), any(), any(), any());
    }

    @Test
    @DisplayName("T02.1 - Deve cadastrar novo cliente e registrar consentimento explícito da LGPD")
    void deveCadastrarUsuarioComConsentimentoLgpd() {
        CadastroUsuarioDTO dto = new CadastroUsuarioDTO();
        dto.setNome("Bruno Ferreira");
        dto.setEmail("bruno@email.com");
        dto.setSenha("SenhaForte@2026");
        dto.setPerfil(PerfilEnum.CLIENTE);
        dto.setTelefone("81977778888");
        dto.setConsentimentoLgpd(true); // LGPD Aceito

        when(usuarioRepository.existsByEmail("bruno@email.com")).thenReturn(false);
        when(passwordEncoder.encode("SenhaForte@2026")).thenReturn("$2a$10$novoHash");

        Usuario usuarioSalvo = new Usuario(2L, "Bruno Ferreira", "bruno@email.com", "$2a$10$novoHash", PerfilEnum.CLIENTE, "81977778888", true);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);
        when(tokenProvider.gerarToken(any(), any(), any(), any())).thenReturn("mocked.jwt.token.novo");

        // Act
        LoginResponseDTO response = authService.cadastrar(dto, "192.168.0.10");

        // Assert
        assertNotNull(response);
        assertEquals("mocked.jwt.token.novo", response.getAccessToken());

        // Verifica que o consentimento da LGPD foi gravado na base
        verify(consentimentoRepository, times(1)).save(any(ConsentimentoLgpd.class));
        verify(auditoriaService, times(1)).registrar(eq("CADASTRO_USUARIO"), eq("Usuario"), any(), any(), any());
    }

    @Test
    @DisplayName("T02.2 - Deve recusar cadastro quando o cliente não concordar com o termo de privacidade LGPD")
    void deveRecusarCadastroQuandoNaoConsentirLgpd() {
        CadastroUsuarioDTO dto = new CadastroUsuarioDTO();
        dto.setNome("Carlos Recusado");
        dto.setEmail("carlos@email.com");
        dto.setSenha("Senha@123");
        dto.setConsentimentoLgpd(false); // Recusou LGPD

        when(usuarioRepository.existsByEmail("carlos@email.com")).thenReturn(false);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> {
            authService.cadastrar(dto, "127.0.0.1");
        });

        assertTrue(ex.getMessage().contains("consentimento"));
        verify(usuarioRepository, never()).save(any());
        verify(consentimentoRepository, never()).save(any());
    }
}
