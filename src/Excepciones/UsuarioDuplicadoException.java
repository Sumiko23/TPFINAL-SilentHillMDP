package Excepciones;

public class UsuarioDuplicadoException extends Exception {
    public UsuarioDuplicadoException(String nombre) {
        super("El nombre: "+nombre+" ya esta en uso.");
    }
}
