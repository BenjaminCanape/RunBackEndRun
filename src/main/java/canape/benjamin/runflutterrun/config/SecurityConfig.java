package canape.benjamin.runflutterrun.config;

import canape.benjamin.runflutterrun.security.AuthEntryPointJwt;
import canape.benjamin.runflutterrun.security.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static canape.benjamin.runflutterrun.security.SecurityConstants.*;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    public UserDetailsService userDetailsService;
    @Autowired
    public CustomAuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    public CustomAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    public CustomLogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;
    @Autowired
    private AuthTokenFilter authFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                .requestMatchers("/resources/**").permitAll()
                .requestMatchers(REGISTER_URL, SIGN_UP_URL, REFRESH_TOKEN_URL).permitAll()
                .requestMatchers("/api/private/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(config -> config.authenticationEntryPoint(unauthorizedHandler))
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin()
                .loginProcessingUrl(SIGN_UP_URL)
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage(SIGN_UP_URL)
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .permitAll()
                .and()
                .logout()
                .logoutUrl(LOGOUT_URL)
                .logoutSuccessHandler(logoutSuccessHandler)
                .permitAll();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}