package com.leep.security.hedge.exception.global;
import com.leep.security.hedge.exception.model.ErrorResponse;
import com.leep.security.hedge.exception.model.OsInjectionDetectedException;
import com.leep.security.hedge.exception.model.RateLimitExceededException;
import com.leep.security.hedge.exception.model.RoleAccessDeniedException;
import com.leep.security.hedge.exception.model.SqlInjectionDetectedException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for REST controllers.
 * <p>
 * This class catches specific exceptions thrown during request processing
 * and converts them into structured HTTP responses with meaningful error information.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles {@link RateLimitExceededException} thrown when a rate limit is violated.
     *
     * @param ex the exception containing the rate limit violation message
     * @return a 429 Too Many Requests response with an error body
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceeded(RateLimitExceededException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.TOO_MANY_REQUESTS);
    }


    /**
     * Handles validation failures on method arguments annotated with validation constraints.
     *
     * @param ex the exception containing validation errors
     * @return a 400 Bad Request response with the first validation message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed: " + ex.getBindingResult().getFieldError().getDefaultMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles any uncaught or unexpected exceptions.
     *
     * @param ex the thrown exception
     * @return a 500 Internal Server Error response with a generic message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected error: " + ex.getMessage()
        );

        error.setPath(request.getRequestURI());
        error.setMethod(request.getMethod());
        error.setRemoteIp(request.getRemoteAddr());
        error.setUserAgent(request.getHeader("User-Agent"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            error.setUserId(auth.getName());
        }

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(RoleAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleRoleAccessDenied(RoleAccessDeniedException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles SQL injection attempts detected by @Injection.
     *
     * @param ex the SQL injection exception
     * @return a 400 Bad Request response with a security warning
     */
    @ExceptionHandler(SqlInjectionDetectedException.class)
    public ResponseEntity<ErrorResponse> handleSqlInjection(SqlInjectionDetectedException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles OS command injection attempts detected by @Injection.
     *
     * @param ex the OS injection exception
     * @return a 400 Bad Request response with a security warning
     */
    @ExceptionHandler(OsInjectionDetectedException.class)
    public ResponseEntity<ErrorResponse> handleOsInjection(OsInjectionDetectedException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
