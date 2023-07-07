package canape.benjamin.runflutterrun.security;

import canape.benjamin.runflutterrun.security.jwt.JwtUtils;
import canape.benjamin.runflutterrun.security.jwt.TokenManager;
import canape.benjamin.runflutterrun.services.IRefreshTokenService;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

/**
 * CustomLogoutSuccessHandler is responsible for handling successful logout requests.
 * It invalidates the JWT token, deletes the refresh token, and sends a success response.
 */
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IRefreshTokenService refreshTokenService;

    /**
     * Handles the successful logout by invalidating the JWT token, deleting the refresh token, and sending a success response.
     *
     * @param request        the HTTP request
     * @param response       the HTTP response
     * @param authentication the authentication object
     * @throws IOException      if an I/O error occurs during the handling of the request
     * @throws ServletException if a servlet-specific error occurs during the handling of the request
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String token = jwtUtils.extractTokenFromRequest(request);
        TokenManager.invalidateToken(token);

        try {
            String username = jwtUtils.getUserNameFromJwtToken(token);
            refreshTokenService.deleteByUsername(username);

            String jsonResponse = objectMapper.writeValueAsString(Collections.emptyMap());

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonResponse);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (TokenExpiredException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
