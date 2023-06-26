package canape.benjamin.runflutterrun.config;

import canape.benjamin.runflutterrun.security.JwtUtils;
import canape.benjamin.runflutterrun.security.TokenManager;
import canape.benjamin.runflutterrun.service.IRefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IRefreshTokenService refreshTokenService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String token = jwtUtils.extractTokenFromRequest(request);
        TokenManager.invalidateToken(token);

        String username = jwtUtils.getUserNameFromJwtToken(token);
        refreshTokenService.deleteByUsername(username);
        
        String jsonResponse = objectMapper.writeValueAsString(Collections.emptyMap());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}