package com.bps.uts.sipakjabat.config;

import com.bps.uts.sipakjabat.dto.GlobalResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<GlobalResponseDTO<String>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN) // Memberikan status 403 Forbidden
                .body(GlobalResponseDTO.<String>builder()
                        .status("error")
                        .message("Akun tidak ditemukan, tidak aktif, atau telah diblokir.")
                        .build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<GlobalResponseDTO<String>> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // Memberikan status 401 Unauthorized
                .body(GlobalResponseDTO.<String>builder()
                        .status("error")
                        .message("NIP atau password salah.")
                        .build());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GlobalResponseDTO<String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(GlobalResponseDTO.<String>builder()
                        .status("error")
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<GlobalResponseDTO<String>> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(GlobalResponseDTO.<String>builder()
                        .status("error")
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GlobalResponseDTO<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(GlobalResponseDTO.<String>builder()
                        .status("error")
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<GlobalResponseDTO<String>> handleSecurityException(SecurityException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(GlobalResponseDTO.<String>builder()
                        .status("error")
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GlobalResponseDTO<String>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(GlobalResponseDTO.<String>builder()
                        .status("error")
                        .message("Akses ditolak. Anda tidak memiliki izin untuk mengakses resource ini.")
                        .build());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<GlobalResponseDTO<String>> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(GlobalResponseDTO.<String>builder()
                        .status("error")
                        .message("Autentikasi gagal. Silakan periksa kredensial Anda.")
                        .build());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<GlobalResponseDTO<String>> handleNotFoundException(NoHandlerFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(GlobalResponseDTO.<String>builder()
                        .status("error")
                        .message("Endpoint tidak ditemukan: " + ex.getRequestURL())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalResponseDTO<String>> handleGeneralException(Exception ex) {
        ex.printStackTrace(); // Log untuk debugging
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GlobalResponseDTO.<String>builder()
                        .status("error")
                        .message("Terjadi kesalahan pada server: " + ex.getMessage())
                        .build());
    }
}