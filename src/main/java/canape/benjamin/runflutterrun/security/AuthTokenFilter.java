package canape.benjamin.runflutterrun.security;

import canape.benjamin.runflutterrun.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenFilter.class);

    private JwtUtils jwtUtils;

    UserDetailsServiceImpl userService;

    private final AntPathRequestMatcher loginPathMatcher = new AntPathRequestMatcher("/api/user/login");

    public AuthTokenFilter() {
    }

    @Autowired
    public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        if (loginPathMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            String jwt = parseJwt(request);
            LOGGER.error("AuthTokenFilter | doFilterInternal | jwt: {}", jwt);

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, Collections.emptyList());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            LOGGER.error("AuthTokenFilter | doFilterInternal | Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }


    private String parseJwt(HttpServletRequest request) {

        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }
}