package com.hotplacemap.webserver.hotplacemapwebserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import javax.sql.DataSource;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DataSource dataSource, PasswordEncoder passwordEncoder) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login.html", "/register", "/success.html", "/failure.html", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/success.html", true)
                        .failureUrl("/failure.html")
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());

        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder)
                .usersByUsernameQuery("select username, password, 1 as enabled from `user` where username=?")
                .authoritiesByUsernameQuery("select username, 'ROLE_USER' as authority from `user` where username=?");

        http.authenticationManager(auth.build());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ApplicationListener<AuthenticationFailureBadCredentialsEvent> authenticationFailureListener() {
        return event -> logger.info("Authentication failed: " + event.getAuthentication().getName());
    }

    @Bean
    public ApplicationListener<AuthenticationSuccessEvent> authenticationSuccessListener() {
        return event -> logger.info("Authentication successful: " + event.getAuthentication().getName());
    }
}
