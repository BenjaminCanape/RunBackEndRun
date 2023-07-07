package canape.benjamin.runflutterrun.security;

import canape.benjamin.runflutterrun.security.jwt.AuthEntryPointJwt;
import canape.benjamin.runflutterrun.security.jwt.AuthTokenFilter;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    /**
     * Configures the authentication provider.
     *
     * @return the AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * Configures the authentication manager.
     *
     * @return the AuthenticationManager
     * @throws Exception if an error occurs while configuring the AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configures the password encoder.
     *
     * @return the PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates a new instance of JsonAuthenticationFilter and configures it.
     *
     * @return the JsonAuthenticationFilter
     * @throws Exception if an error occurs while configuring the JsonAuthenticationFilter
     */
    @Bean
    public JsonAuthenticationFilter jsonAuthFilter() throws Exception {
        JsonAuthenticationFilter filter = new JsonAuthenticationFilter(new AntPathRequestMatcher(SIGN_UP_URL, "POST"));
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(authenticationFailureHandler);
        return filter;
    }

    /**
     * Configures the security filter chain.
     *
     * @param http the HttpSecurity object
     * @return the SecurityFilterChain
     * @throws Exception if an error occurs while configuring the SecurityFilterChain
     */
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
                .addFilterBefore(jsonAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl(LOGOUT_URL)
                .logoutSuccessHandler(logoutSuccessHandler)
                .permitAll();

        return http.build();
    }
}
