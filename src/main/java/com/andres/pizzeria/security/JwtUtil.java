package com.andres.pizzeria.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;


@Component
public class JwtUtil {
    private static final String SECRET_KEY = "andres_pizza";
    private static final  Algorithm ALGORITIHM = Algorithm.HMAC256(SECRET_KEY);

    public String create(String username){
        return JWT.create()
                .withSubject(username)
                .withIssuer("andres-pizzeria")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30)))
                .sign(ALGORITIHM);
    }

    public boolean isValid(String jwt)  {
        try{
            JWT.require(ALGORITIHM)
                    .build()
                    .verify(jwt);
            return true;
        }catch (JWTVerificationException error){
            return false;
        }
    }

    public String getUsername(String jwt){
        return JWT.require(ALGORITIHM)
                .build()
                .verify(jwt)
                .getSubject();
    }


}
