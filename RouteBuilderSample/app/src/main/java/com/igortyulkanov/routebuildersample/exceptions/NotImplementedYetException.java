package com.igortyulkanov.routebuildersample.exceptions;

public class NotImplementedYetException extends RuntimeException {

    public NotImplementedYetException() {
        super();
    }

    public NotImplementedYetException(String detailMessage) {
        super(detailMessage);
    }

    public NotImplementedYetException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NotImplementedYetException(Throwable throwable) {
        super(throwable);
    }

}
