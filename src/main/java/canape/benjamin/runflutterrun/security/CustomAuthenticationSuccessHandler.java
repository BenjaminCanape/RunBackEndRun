package canape.benjamin.runflutterrun.security;

import canape.benjamin.runflutterrun.dto.UserSearchDto;
import canape.benjamin.runflutterrun.model.RefreshToken;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.security.jwt.JwtUtils;
import canape.benjamin.runflutterrun.services.IRefreshTokenService;
import canape.benjamin.runflutterrun.services.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.webjars.NotFoundException;

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
    @Autowired
    private IUserService userService;
    @Autowired
    private ModelMapper modelMapper;

    /**
     * Handles the successful authentication.
     *
     * @param request        The HttpServletRequest.
     * @param response       The HttpServletResponse.
     * @param authentication The Authentication object representing the successful authentication.
     * @throws IOException if an I/O error occurs during the handling of the successful authentication.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateTokenFromUsername(userDetails.getUsername());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
        UserSearchDto userDto = modelMapper.map(user, UserSearchDto.class);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("refreshToken", refreshToken.getToken());
        responseBody.put("token", jwt);
        responseBody.put("user", userDto);
        responseBody.put("message", "Authentication successful");

        String jsonResponse = objectMapper.writeValueAsString(responseBody);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
