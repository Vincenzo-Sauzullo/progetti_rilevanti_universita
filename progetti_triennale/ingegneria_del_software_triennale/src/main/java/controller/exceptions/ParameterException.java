package controller.exceptions;

public class ParameterException extends Exception {
    public ParameterException(String message) {
        super("Parametro non valido: %s".formatted(message));
    }
}
