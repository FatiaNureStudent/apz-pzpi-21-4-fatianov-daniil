package com.nure.apz.fatianov.daniil.AuthService.auth;

import com.nure.apz.fatianov.daniil.AuthService.config.JwtService;
import com.nure.apz.fatianov.daniil.AuthService.user.Role;
import com.nure.apz.fatianov.daniil.AuthService.user.User;
import com.nure.apz.fatianov.daniil.AuthService.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .birthday(request.getBirthday())
                .gender(request.getGender())
                .creationDate(new Date())
                .role(Role.USER)
                .build();

        userRepository.save(user);

        var jwtTokent = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtTokent)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(request.getEmail()));
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public Boolean isAdmin(String token) {
        try{
            String username = jwtService.extractUsername(token);
            Optional<User> optionalUser = userRepository.findByEmail(username);
            if(optionalUser.isEmpty()) {
                throw new UsernameNotFoundException("User not found");
            }

            User user = optionalUser.get();
            return jwtService.isAdmin(token, user);

        }catch (Exception e){
            throw new UsernameNotFoundException("Something wrong: " + e.getMessage());
        }
    }

    public Boolean isUser(String token) {
        try{
            String username = jwtService.extractUsername(token);
            Optional<User> optionalUser = userRepository.findByEmail(username);
            if(optionalUser.isEmpty()) {
                throw new UsernameNotFoundException("User not found");
            }

            User user = optionalUser.get();
            return jwtService.isUser(token, user);

        }catch (Exception e){
            throw new UsernameNotFoundException("Something wrong: " + e.getMessage());
        }
    }
}
