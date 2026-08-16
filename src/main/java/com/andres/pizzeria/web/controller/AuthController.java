package com.andres.pizzeria.web.controller;

import com.andres.pizzeria.dto.LoginDto;
import com.andres.pizzeria.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Login y generacion del token JWT usado para autenticar el resto de la API")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Iniciar sesion",
            description = "Valida usuario/contrasena y devuelve un JWT en el header Authorization, " +
                    "que debe enviarse como 'Bearer <token>' en las siguientes peticiones protegidas.")
    @PostMapping("/login")
    public ResponseEntity<Void>login(@RequestBody LoginDto loginDto){
        UsernamePasswordAuthenticationToken login = new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password());
        Authentication authentication = this.authenticationManager.authenticate(login);

        String jwt = this.jwtUtil.create((loginDto.username()));
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, jwt).build();
    }
}
