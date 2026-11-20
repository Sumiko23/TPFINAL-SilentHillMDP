package Excepciones;

public class ContraseniaIncorrectaException extends Exception {

    public ContraseniaIncorrectaException() {
        super("Contraseña incorrecta. Intente de nuevo");
    }
}
