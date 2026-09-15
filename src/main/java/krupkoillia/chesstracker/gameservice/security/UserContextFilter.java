package krupkoillia.chesstracker.gameservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class UserContextFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER_NAME = "X-User-Id";

    private static final String GATEWAY_SECRET_HEADER_NAME = "X-Gateway-Secret";

    @Value("${security.gateway.secret}")
    private String gatewaySecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String userIdFromHeader = request.getHeader(USER_ID_HEADER_NAME);
        String gatewaySecretFromHeader = request.getHeader(GATEWAY_SECRET_HEADER_NAME);

        if (!gatewaySecret.equals(gatewaySecretFromHeader)) {
            log.warn("Request received with invalid gateway secret: method={}, uri={}",
                    request.getMethod(),
                    request.getRequestURI());
            response.setStatus(401);
            return;
        }

        if (userIdFromHeader == null) {
            log.error("User id was not provided. Rejecting request");
            response.setStatus(401);
            return;
        }

        Long userId;

        try {
            userId = Long.parseLong(userIdFromHeader);
        } catch (NumberFormatException e) {
            log.warn("Invalid number format for {} header",
                    USER_ID_HEADER_NAME, e);
            response.setStatus(401);
            return;
        }

        SecurityContext securityContext = SecurityContextHolder.getContext();

        if (securityContext.getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userId, null, List.of()
        );

        securityContext.setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

}
