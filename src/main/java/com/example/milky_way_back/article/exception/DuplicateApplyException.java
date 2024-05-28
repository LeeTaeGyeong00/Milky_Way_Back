package com.example.milky_way_back.article.exception;

public class DuplicateApplyException extends RuntimeException {
    public DuplicateApplyException(String message) {
        super(message);
    }
}