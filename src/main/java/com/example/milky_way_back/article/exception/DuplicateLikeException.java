package com.example.milky_way_back.article.exception;

public class DuplicateLikeException extends RuntimeException {
    public DuplicateLikeException(String message) {
        super(message);
    }
}