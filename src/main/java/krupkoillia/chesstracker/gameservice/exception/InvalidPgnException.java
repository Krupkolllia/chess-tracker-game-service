package krupkoillia.chesstracker.gameservice.exception;

public class InvalidPgnException extends RuntimeException {

    public InvalidPgnException(String message) {
        super(message);
    }

    public InvalidPgnException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
