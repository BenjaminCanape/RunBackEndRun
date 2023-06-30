package canape.benjamin.runflutterrun.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class JsonAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (HttpMethod.POST.matches(request.getMethod())) {
            try (InputStream requestBody = request.getInputStream()) {
                Map<String, String> authenticationData = objectMapper.readValue(requestBody, Map.class);
                String username = authenticationData.get("username");
                String password = authenticationData.get("password");

                if (username == null || password == null) {
                    throw new IllegalArgumentException("Username or password is missing");
                }

                Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
                return getAuthenticationManager().authenticate(authentication);
            } catch (IOException e) {
                throw new IllegalArgumentException("Invalid JSON format");
            }
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method");
        }
    }
}
