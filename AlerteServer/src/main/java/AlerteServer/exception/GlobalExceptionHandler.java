package AlerteServer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Méthode utilitaire pour uniformiser le format des erreurs JSON
    private Map<String, Object> buildErrorBody(String message, HttpStatus status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }

    // Gestion des erreurs de type dans l'URL
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();
        String typeName = (requiredType != null) ? requiredType.getSimpleName() : "inconnu";
        String message = String.format("Le paramètre '%s' doit être de type %s.",
                ex.getName(), typeName);
        return new ResponseEntity<>(buildErrorBody(message, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    // Gestion des erreurs de base de données
    @ExceptionHandler({
            org.springframework.dao.DataIntegrityViolationException.class,
            org.springframework.dao.IncorrectResultSizeDataAccessException.class
    })
    public ResponseEntity<Object> handleDatabaseError(Exception ex) {
        String message = "Une erreur d'intégrité des données est survenue.";
        if (ex instanceof org.springframework.dao.IncorrectResultSizeDataAccessException) {
            message = "La requête a retourné plusieurs résultats alors qu'un seul était attendu.";
        }
        return new ResponseEntity<>(buildErrorBody(message, HttpStatus.CONFLICT), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralError(Exception ex) {
        // En production, on logue l'erreur complète sur le serveur, mais on cache le détail au client
        return new ResponseEntity<>(
                buildErrorBody("Une erreur inattendue est survenue.", HttpStatus.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<Object> handleIdNotFound(IdNotFoundException ex) {
        return new ResponseEntity<>(buildErrorBody(ex.getMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }
}