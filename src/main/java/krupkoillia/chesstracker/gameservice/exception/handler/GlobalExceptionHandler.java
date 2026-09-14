package krupkoillia.chesstracker.gameservice.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import krupkoillia.chesstracker.gameservice.exception.EntityNotFoundException;
import krupkoillia.chesstracker.gameservice.exception.InvalidGameResultTypeException;
import krupkoillia.chesstracker.gameservice.exception.InvalidPgnException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(Exception e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({
            InvalidGameResultTypeException.class,
            InvalidPgnException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(
            Exception e, HttpServletRequest request
    ) {
        log.error(
                "Unhandled exception. Method: {}, path: {}",
                request.getMethod(),
                request.getRequestURI(),
                e
        );

        return buildResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal server error"
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        return ResponseEntity
            .status(status)
            .body(new ErrorResponse(
                status.value(),
                message,
                Instant.now()
            ));
    }

}
