package com.example.restapi.config;

import com.example.restapi.accounts.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final AccountService accountService;

    private final PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    // not recommend
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer(){
//        return web -> web
//                .ignoring()
//                .requestMatchers("/docs/index.html")
//                .requestMatchers(HttpMethod.GET, "/api/events/**")
//                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
//    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                .requestMatchers("/docs/index.html").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/events/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/events/**").hasRole("USER")
                                .anyRequest().authenticated()
                );

        return http.build();
    }
}
