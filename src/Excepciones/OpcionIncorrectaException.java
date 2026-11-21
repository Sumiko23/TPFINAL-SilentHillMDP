package Excepciones;

public class OpcionIncorrectaException extends Exception {
    public OpcionIncorrectaException() {
        super("La opción ingresada es inválida. Intente de nuevo");
    }

}
