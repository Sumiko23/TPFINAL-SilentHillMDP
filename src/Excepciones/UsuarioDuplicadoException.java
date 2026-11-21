package Excepciones;

public class UsuarioDuplicadoException extends Exception {
    public UsuarioDuplicadoException() {
        super("El nombre ya existe . Pruebe con otro ");
    }
}
