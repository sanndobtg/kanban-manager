
package fr.ubo.kanban.common.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Secret partagé pour vérifier le JWT (à remplacer par ta future clé publique ou JWKS)
    private static final String SECRET_KEY ="my-very-long-secret-key-for-hs256-minimum-32-chars!";


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // désactive CSRF pour une API REST
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated() // toutes les routes nécessitent un JWT
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> {}) // active la vérification des JWT
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // pas de session
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Decoder pour JWT signé avec HS256 et le secret partagé
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
}