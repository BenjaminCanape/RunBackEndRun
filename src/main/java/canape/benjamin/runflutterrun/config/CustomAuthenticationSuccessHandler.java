package canape.benjamin.runflutterrun.config;

import canape.benjamin.runflutterrun.model.RefreshToken;
import canape.benjamin.runflutterrun.security.JwtUtils;
import canape.benjamin.runflutterrun.service.IRefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private IRefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateTokenFromUsername(userDetails.getUsername());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        // Construire un objet JSON de réponse avec le jeton
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("resfreshToken", refreshToken.getToken());
        responseBody.put("token", jwt);
        responseBody.put("message", "Authentication successful");

        String jsonResponse = objectMapper.writeValueAsString(responseBody);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}