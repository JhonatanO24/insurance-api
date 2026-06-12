package com.insurance_api.shared.exceptions;

import com.insurance_api.customers.domain.exceptions.CustomerNotFoundException;
import com.insurance_api.customers.domain.exceptions.EmailAlreadyExistsException;
import com.insurance_api.policies.domain.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Filtro global de excepciones.
 * Mapea excepciones de dominio → respuestas HTTP con ProblemDetail (RFC 7807).
 * Un solo lugar para toda la lógica de manejo de errores — SRP.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ── 404 Not Found

    @ExceptionHandler(CustomerNotFoundException.class)
    public ProblemDetail handleCustomerNotFound(CustomerNotFoundException ex) {
        log.warn("[404] {}", ex.getMessage());
        return buildProblem(
                HttpStatus.NOT_FOUND,
                "Cliente no encontrado",
                ex.getMessage(),
                "customer-not-found"
        );
    }

    @ExceptionHandler(PolicyNotFoundException.class)
    public ProblemDetail handlePolicyNotFound(PolicyNotFoundException ex) {
        log.warn("[404] {}", ex.getMessage());
        return buildProblem(
                HttpStatus.NOT_FOUND,
                "Póliza no encontrada",
                ex.getMessage(),
                "policy-not-found"
        );
    }

    // ── 400 Bad Request

    @ExceptionHandler(InvalidStateTransitionException.class)
    public ProblemDetail handleInvalidStateTransition(InvalidStateTransitionException ex) {
        log.warn("[400] {}", ex.getMessage());
        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "Transición de estado inválida",
                ex.getMessage(),
                "invalid-state-transition"
        );
    }

    @ExceptionHandler(InvalidRiskProfileException.class)
    public ProblemDetail handleInvalidRiskProfile(InvalidRiskProfileException ex) {
        log.warn("[400] {}", ex.getMessage());
        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "Perfil de riesgo inválido",
                ex.getMessage(),
                "invalid-risk-profile"
        );
    }

    @ExceptionHandler(UnsupportedBranchException.class)
    public ProblemDetail handleUnsupportedBranch(UnsupportedBranchException ex) {
        log.warn("[400] {}", ex.getMessage());
        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "Ramo no soportado",
                ex.getMessage(),
                "unsupported-branch"
        );
    }

    @ExceptionHandler(UnsupportedRatingStrategyException.class)
    public ProblemDetail handleUnsupportedRatingStrategy(UnsupportedRatingStrategyException ex) {
        log.warn("[400] {}", ex.getMessage());
        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "Estrategia de tarificación no soportada",
                ex.getMessage(),
                "unsupported-rating-strategy"
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        log.warn("[400] {}", ex.getMessage());
        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "Estado inválido",
                ex.getMessage(),
                "illegal-state"
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("[400] Validación fallida: {}", errors);
        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "Error de validación",
                errors,
                "validation-error"
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String detail = "Parámetro '" + ex.getName() + "' tiene un formato inválido: "
                + ex.getValue();
        log.warn("[400] {}", detail);
        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "Parámetro inválido",
                detail,
                "type-mismatch"
        );
    }

    // ── 409 Conflict

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ProblemDetail handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        log.warn("[409] {}", ex.getMessage());
        return buildProblem(
                HttpStatus.CONFLICT,
                "Conflicto de email",
                ex.getMessage(),
                "email-already-exists"
        );
    }

    // ── 500 Internal Server Error

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        log.error("[500] Error inesperado: {}", ex.getMessage(), ex);
        return buildProblem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                "Ocurrió un error inesperado. Por favor intente más tarde.",
                "internal-server-error"
        );
    }

    // ── Helper

    private ProblemDetail buildProblem(HttpStatus status, String title,
                                       String detail, String errorCode) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("https://insurance-api.com/errors/" + errorCode));
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("errorCode", errorCode);
        return problem;
    }
}