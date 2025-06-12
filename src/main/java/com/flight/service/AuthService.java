package com.flight.service;

import com.flight.dto.UserDTO;
import com.flight.dto.request.LoginRequestDTO;
import com.flight.dto.request.RegisterRequestDTO;
import com.flight.entity.User;
import com.flight.repository.UserRepository;
import com.flight.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;

    public AuthService(AuthenticationManager authenticationManager, 
                      UserRepository userRepository, 
                      PasswordEncoder passwordEncoder, 
                      JwtUtil jwtUtil,
                      ModelMapper modelMapper) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.modelMapper = modelMapper;
    }

    public ResponseEntity<?> login(LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), 
                    loginRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtUtil.generateToken(authentication);
            
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            userDTO.setToken(token);
            
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("用户名或密码错误");
        }
    }

    public ResponseEntity<?> register(RegisterRequestDTO registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("该邮箱已被注册");
        }

        User user = modelMapper.map(registerRequest, User.class);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        User savedUser = userRepository.save(user);
        
        // 注册成功后自动登录
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail(registerRequest.getEmail());
        loginRequest.setPassword(registerRequest.getPassword());
        return login(loginRequest);
    }

    public ResponseEntity<?> checkAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            return ResponseEntity.ok(userDTO);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登录");
    }

    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().body("登出成功");
    }
}
