package br.com.uninter.raizesnordeste.application.service;

import br.com.uninter.raizesnordeste.application.dto.CadastroUsuarioDTO;
import br.com.uninter.raizesnordeste.application.dto.LoginRequestDTO;
import br.com.uninter.raizesnordeste.application.dto.LoginResponseDTO;
import br.com.uninter.raizesnordeste.domain.entity.ConsentimentoLgpd;
import br.com.uninter.raizesnordeste.domain.entity.Usuario;
import br.com.uninter.raizesnordeste.domain.enums.PerfilEnum;
import br.com.uninter.raizesnordeste.domain.repository.ConsentimentoLgpdRepository;
import br.com.uninter.raizesnordeste.domain.repository.UsuarioRepository;
import br.com.uninter.raizesnordeste.infrastructure.exception.RegraNegocioException;
import br.com.uninter.raizesnordeste.infrastructure.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ConsentimentoLgpdRepository consentimentoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuditoriaService auditoriaService;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    public AuthService(UsuarioRepository usuarioRepository,
                       ConsentimentoLgpdRepository consentimentoRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider,
                       AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.consentimentoRepository = consentimentoRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas."));

        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new BadCredentialsException("Credenciais inválidas.");
        }

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new RegraNegocioException("Usuário inativo no sistema.");
        }

        String token = tokenProvider.gerarToken(
                usuario.getEmail(),
                usuario.getPerfil().name(),
                usuario.getId(),
                usuario.getNome()
        );

        LoginResponseDTO.UserDetailsDTO userDTO = new LoginResponseDTO.UserDetailsDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getPerfil().name()
        );

        return new LoginResponseDTO(token, jwtExpirationInMs / 1000, userDTO);
    }

    @Transactional
    public LoginResponseDTO cadastrar(CadastroUsuarioDTO dto, String ipOrigem) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RegraNegocioException("O e-mail informado já está cadastrado.");
        }

        if (!Boolean.TRUE.equals(dto.getConsentimentoLgpd())) {
            throw new RegraNegocioException("O consentimento para uso de dados (LGPD) é obrigatório.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setPerfil(dto.getPerfil() != null ? dto.getPerfil() : PerfilEnum.CLIENTE);
        usuario.setTelefone(dto.getTelefone());
        usuario.setAtivo(true);

        Usuario salvo = usuarioRepository.save(usuario);

        // Registro de Consentimento LGPD
        ConsentimentoLgpd consentimento = new ConsentimentoLgpd(
                salvo,
                "Cadastro para pedidos multicanal e fidelização na Rede Raízes do Nordeste",
                true,
                ipOrigem != null ? ipOrigem : "127.0.0.1"
        );
        consentimentoRepository.save(consentimento);

        // Registro de Auditoria
        auditoriaService.registrar(
                "CADASTRO_USUARIO",
                "Usuario",
                salvo.getId(),
                salvo.getEmail(),
                "Novo usuário registrado com perfil " + salvo.getPerfil().name()
        );

        String token = tokenProvider.gerarToken(
                salvo.getEmail(),
                salvo.getPerfil().name(),
                salvo.getId(),
                salvo.getNome()
        );

        LoginResponseDTO.UserDetailsDTO userDTO = new LoginResponseDTO.UserDetailsDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getPerfil().name()
        );

        return new LoginResponseDTO(token, jwtExpirationInMs / 1000, userDTO);
    }
}
