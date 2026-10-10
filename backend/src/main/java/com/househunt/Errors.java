package com.househunt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.slf4j.*;
import java.util.Map;
@RestControllerAdvice
class Errors {
 private static final Logger log=LoggerFactory.getLogger(Errors.class);
 @ExceptionHandler(ResponseStatusException.class) ResponseEntity<?> status(ResponseStatusException e){return ResponseEntity.status(e.getStatusCode()).body(Map.of("message",e.getReason()==null?"Request failed":e.getReason()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> validation(MethodArgumentNotValidException e){return ResponseEntity.badRequest().body(Map.of("message",e.getBindingResult().getFieldErrors().stream().map(x->x.getField()+": "+x.getDefaultMessage()).toList().toString()));}
 @ExceptionHandler({org.springframework.http.converter.HttpMessageNotReadableException.class,org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class}) ResponseEntity<?> malformed(Exception e){return ResponseEntity.badRequest().body(Map.of("message","Invalid request value"));}
 @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<?> conflict(Exception e){return ResponseEntity.status(409).body(Map.of("message","This change conflicts with existing data"));}
 @ExceptionHandler(AccessDeniedException.class) ResponseEntity<?> denied(Exception e){return ResponseEntity.status(403).body(Map.of("message","Access denied"));}
 @ExceptionHandler(Exception.class) ResponseEntity<?> unexpected(Exception e){log.error("request_failed",e);return ResponseEntity.internalServerError().body(Map.of("message","Unexpected server error"));}
}