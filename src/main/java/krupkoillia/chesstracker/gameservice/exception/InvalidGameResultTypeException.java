package krupkoillia.chesstracker.gameservice.exception;

public class InvalidGameResultTypeException extends RuntimeException {

    public InvalidGameResultTypeException(String message) {
        super(message);
    }

    public InvalidGameResultTypeException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
