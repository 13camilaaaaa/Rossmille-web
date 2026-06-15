package com.rossmille.service;

import com.rossmille.dto.LoginRequest;
import com.rossmille.dto.LoginResponse;
import com.rossmille.entity.Usuario;
import com.rossmille.repository.UsuarioRepository;
import com.rossmille.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByIdUsuario(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales incorrectas"));

        if (!usuario.getRolUsuarios().equalsIgnoreCase(request.getCargo())) {
            throw new IllegalArgumentException("Credenciales incorrectas");
        }

        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new IllegalArgumentException("Credenciales incorrectas");
        }

        String token = jwtTokenProvider.generateToken(
                usuario.getIdUsuario(),
                usuario.getNombreUsuario(),
                usuario.getRolUsuarios()
        );

        return new LoginResponse(token, usuario.getNombreUsuario(), usuario.getRolUsuarios());
    }
}
