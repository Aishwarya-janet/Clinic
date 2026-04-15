package com.clinic.backend.controller;

import com.clinic.backend.entity.User;
import com.clinic.backend.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthService service;

    // ✅ REGISTER (FIXED)
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody User user) {

        service.register(user);

        Map<String, String> res = new HashMap<>();
        res.put("message", "Registered Successfully");

        return res;
    }
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User user) {

        String token = service.login(user);

        Map<String, String> res = new HashMap<>();
        res.put("token", token);

        return res;
    }
}