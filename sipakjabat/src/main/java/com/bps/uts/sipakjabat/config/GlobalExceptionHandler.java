package com.bps.uts.sipakjabat.config;

import com.bps.uts.sipakjabat.dto.GlobalResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GlobalResponseDTO<String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(GlobalResponseDTO.<String>builder().status("error").message(ex.getMessage()).build());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<GlobalResponseDTO<String>> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(GlobalResponseDTO.<String>builder().status("error").message(ex.getMessage()).build());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<GlobalResponseDTO<String>> handleSecurityException(SecurityException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(GlobalResponseDTO.<String>builder().status("error").message(ex.getMessage()).build());
    }
}