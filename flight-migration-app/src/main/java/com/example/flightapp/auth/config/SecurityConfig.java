package com.example.flightapp.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .passwordEncoder(passwordEncoder())
            .withUser("admin")
            .password(passwordEncoder().encode("admin123"))
            .roles("ADMIN")
            .and()
            .withUser("agent")
            .password(passwordEncoder().encode("agent123"))
            .roles("AGENT")
            .and()
            .withUser("viewer")
            .password(passwordEncoder().encode("viewer123"))
            .roles("VIEWER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Migration debt: replace WebSecurityConfigurerAdapter with SecurityFilterChain on Boot 3+.
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/h2-console/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/api/flights/**").hasAnyRole("ADMIN", "AGENT", "VIEWER")
            .antMatchers(HttpMethod.POST, "/api/flights/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.PUT, "/api/flights/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.PATCH, "/api/flights/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.DELETE, "/api/flights/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/api/bookings/**").hasAnyRole("ADMIN", "AGENT")
            .antMatchers(HttpMethod.POST, "/api/bookings/**").hasAnyRole("ADMIN", "AGENT")
            .antMatchers(HttpMethod.PATCH, "/api/bookings/**").hasAnyRole("ADMIN", "AGENT")
            .antMatchers("/api/batch/**").hasRole("ADMIN")
            .antMatchers("/api/me").authenticated()
            .anyRequest().denyAll()
            .and()
            .httpBasic()
            .and()
            .headers().frameOptions().sameOrigin();
    }
}
