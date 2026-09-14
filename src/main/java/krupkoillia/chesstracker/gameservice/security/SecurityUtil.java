package krupkoillia.chesstracker.gameservice.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {

    private static final String INVALID_AUTHENTICATION_MESSAGE =
            "Authenticated user's id is missing. "
            + "This method should only be called within authenticated endpoints";

    public static Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Long userId) {
            return userId;
        }

        log.error(INVALID_AUTHENTICATION_MESSAGE);

        throw new IllegalStateException(INVALID_AUTHENTICATION_MESSAGE);
    }

}
