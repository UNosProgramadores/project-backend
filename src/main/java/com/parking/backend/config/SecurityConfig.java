package com.parking.backend.config;

import com.parking.backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/api/auth/**").permitAll()

                        .requestMatchers("/api/parking-lots/*/config").hasRole("ADMIN")
                        .requestMatchers("/api/parking-lots/*/staff/**").hasRole("ADMIN")
                        .requestMatchers("/api/parking-lots/*/rates/**").hasRole("ADMIN")
                        .requestMatchers("/api/parking-lots/*/discounts/**").hasRole("ADMIN")

                        .requestMatchers("/api/parking-lots/*/entry/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/api/parking-lots/*/exit/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/api/parking-lots/*/active-entries").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/api/parking-lots/*/payments/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/api/parking-lots/*/reports/**").hasAnyRole("ADMIN", "STAFF")
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
