package com.parking.backend.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    private String error;
    private int status;
    private String timestamp;
    private List<FieldErrorDetail> errors;

    public ErrorResponse(String error, int status) {
        this.error = error;
        this.status = status;
        this.timestamp = LocalDateTime.now().toString();
    }

    public ErrorResponse(String error, int status, List<FieldErrorDetail> errors) {
        this(error, status);
        this.errors = errors;
    }

    public String getError() { return error; }
    public int getStatus() { return status; }
    public String getTimestamp() { return timestamp; }
    public List<FieldErrorDetail> getErrors() { return errors; }

    public static class FieldErrorDetail {
        private String field;
        private String message;

        public FieldErrorDetail(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() { return field; }
        public String getMessage() { return message; }
    }
}
