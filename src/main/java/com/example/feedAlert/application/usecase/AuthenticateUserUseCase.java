package com.example.feedAlert.application.usecase;

import com.example.feedAlert.application.dto.LoginRequest;
import com.example.feedAlert.application.dto.LoginResponse;
import com.example.feedAlert.domain.gateway.UserRepository;
import com.example.feedAlert.domain.model.User;
import com.example.feedAlert.infrastructure.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateUserUseCase {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public AuthenticateUserUseCase(AuthenticationManager authenticationManager,
                                   JwtService jwtService,
                                   UserDetailsService userDetailsService,
                                   UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    public LoginResponse execute(LoginRequest request) {
        // Autenticar usuário
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
            )
        );

        // Carregar UserDetails e gerar token
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        
        // Buscar User domain para pegar userId e role
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        String token = jwtService.generateToken(
            userDetails,
            user.getUserId(),
            user.getRole().getName()
        );

        return LoginResponse.of(
            token,
            user.getUserId(),
            user.getEmail().value(),
            user.getName().value(),
            user.getRole().getName()
        );
    }
}

