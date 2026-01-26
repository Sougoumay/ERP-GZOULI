package com.gzouli.ERP.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permet d'utiliser @PreAuthorize dans les contrôleurs
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Désactiver CSRF (inutile pour une API Stateless avec JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Activer CORS
                .cors(Customizer.withDefaults())

                // 3. Configurer les autorisations d'URL
                .authorizeHttpRequests(auth -> auth
                        // Autoriser les endpoints publics (ex: Swagger, Actuator si besoin)
                        .requestMatchers("/actuator/**", "/public/**").permitAll()
                        // Tout le reste nécessite une authentification
                        .anyRequest().authenticated()
                )

                // 4. Configurer le serveur de ressources OAuth2 avec JWT
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    /**
     * Configuration CORS pour autoriser Angular
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // Mettez l'URL de votre front
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Convertisseur pour transformer les groupes Cognito en Rôles Spring Security
     * Cognito met les groupes dans le claim "cognito:groups"
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Par défaut Spring cherche "SCOPE_" ou "SCP_", on veut lire "cognito:groups"
            List<String> groups = jwt.getClaimAsStringList("cognito:groups");

            if (groups == null) {
                return List.of();
            }

            // On transforme "ADMIN" en "ROLE_ADMIN" pour Spring Security
            return groups.stream()
                    .map(group -> new SimpleGrantedAuthority("ROLE_" + group.toUpperCase()))
                    .collect(Collectors.toList());
        });

        return converter;
    }
}