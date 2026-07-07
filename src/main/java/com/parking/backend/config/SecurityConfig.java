package com.parking.backend.config;

import com.parking.backend.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Value("${cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/vehicle-types/**").permitAll()

                        .requestMatchers("/api/staff/**").hasRole("ADMIN")
                        .requestMatchers("/api/vehicles/**").hasRole("CUSTOMER")

                        .requestMatchers("/api/parking-lots/*/config").hasRole("ADMIN")
                        .requestMatchers("/api/parking-lots/*/staff/**").hasRole("ADMIN")
                        .requestMatchers("/api/parking-lots/*/rates/**").hasRole("ADMIN")
                        .requestMatchers("/api/parking-lots/*/discounts/**").hasRole("ADMIN")

                        .requestMatchers("/api/parking-lots/*/entry/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/api/parking-lots/*/exit/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/api/parking-lots/*/active-entries").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/api/parking-lots/*/payments/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/api/parking-lots/*/reports/**").hasRole("ADMIN")    // RF_25: solo dueño/administrador
                        .requestMatchers("/api/invoices/**").hasAnyRole("ADMIN", "STAFF")

                        .requestMatchers(HttpMethod.GET, "/api/parking-lots/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/parking-lots/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/parking-lots/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/parking-lots/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/parking-lots/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
