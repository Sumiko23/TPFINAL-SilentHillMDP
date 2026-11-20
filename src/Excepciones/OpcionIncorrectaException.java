package Excepciones;

public class OpcionIncorrectaException extends Exception {
    public OpcionIncorrectaException(String opcion) {
        super("La opción ("+ opcion + ") ingresada es inválida. Intente de nuevo");
    }

}
