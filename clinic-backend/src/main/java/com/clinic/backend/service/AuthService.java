package com.clinic.backend.service;

import com.clinic.backend.entity.User;
import com.clinic.backend.repository.UserRepository;
import com.clinic.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ REGISTER
    public String register(User user) {

        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole("PATIENT");

        repo.save(user);

        return "User Registered Successfully";
    }

    // ✅ LOGIN
    public String login(User userRequest) {

        User user = repo.findByEmail(userRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(userRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(user.getEmail());
    }
}