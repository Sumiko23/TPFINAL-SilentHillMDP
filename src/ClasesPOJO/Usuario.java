package ClasesPOJO;

import Excepciones.UsuarioDuplicadoException;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;

public abstract class Usuario {
    protected int id;
    protected String nombre;
    protected boolean activo;

    public Usuario() {
        this.activo = true;
    }

    public Usuario(int id, String nombre, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.activo = activo;
    }
    public static void separarPantalla() {
        // En la consola integrada de IntelliJ no existe borrado nativo de texto;
        // se imprime un separador limpio para delimitar cada cambio de pantalla.
        System.out.println("\n-----------------------------------------------------------------\n");
    }
    public void mostrarMenu(){}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;
        return nombre != null && nombre.equalsIgnoreCase(usuario.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre == null ? "" : nombre.toLowerCase());
    }
    // Agregar dentro de ClasesPOJO/Usuario.java (junto a tus atributos y getters/setters):
    public static Jugador gestionarAccesoUsuario(Scanner scanner, Set<Jugador> listaJugadores) {
        while (true) {
            System.out.println("\n--- MENÚ DE USUARIO ---");
            System.out.println("1 - Continuar partida");
            System.out.println("2 - Iniciar partida nueva");
            System.out.println("0 - Volver al menú principal");
            System.out.print("Elegí una opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> {
                    if (listaJugadores.isEmpty()) {
                        System.out.println("La lista de partidas guardadas está vacía.");
                        separarPantalla();

                    } else {
                        System.out.println("\n--- PARTIDAS GUARDADAS ---");
                        int indice = 1;
                        for (Jugador j : listaJugadores) {
                            System.out.println(indice + " - " + j.getNombre() + " (Escena: " + j.getEscenaActual() + ")");
                            indice++;
                        }
                        System.out.println("0 - Volver");
                        System.out.print("Elegí el número de partida: ");

                        String seleccionTexto = scanner.nextLine().trim();
                        if ("0".equals(seleccionTexto)) {
                            break;
                        }

                        try {
                            int seleccion = Integer.parseInt(seleccionTexto);
                            int contador = 1;
                            for (Jugador j : listaJugadores) {
                                if (contador == seleccion) {
                                    System.out.println("Cargando partida de " + j.getNombre() + "...");
                                    return j;
                                }
                                contador++;
                            }
                            System.out.println("Número de partida inexistente.");
                        } catch (NumberFormatException e) {
                            System.out.println("Debés ingresar un número válido.");
                        }
                    }
                }
                case "2" -> {
                    System.out.print("Ingresá tu nombre de jugador (o 0 para volver): ");
                    String nombre = scanner.nextLine().trim();

                    if ("0".equals(nombre)) {
                        break;
                    }
                    if (nombre.isEmpty()) {
                        System.out.println("El nombre no puede estar vacío.");
                        break;
                    }

                    try {
                        for (Jugador j : listaJugadores) {
                            if (j.getNombre().equalsIgnoreCase(nombre)) {
                                throw new UsuarioDuplicadoException();
                            }
                        }

                        Jugador nuevoJugador = new Jugador(nombre);
                        listaJugadores.add(nuevoJugador);

                        // Cuando conectemos SQL, descomentamos esta línea para guardar el nuevo jugador en la BD:
                        // jugadorService.agregar(nuevoJugador);
                        // Y DESCOMENTAR EL CATCH DE SQL EXCEPTION
                        System.out.println("¡Usuario '" + nombre + "' creado con éxito!");
                        System.out.println("1 - JUGAR");
                        System.out.println("0 - Volver al menú principal");
                        System.out.print("Elegí una opción: ");
                        String jugarOp = scanner.nextLine().trim();

                        if ("1".equals(jugarOp)) {
                            return nuevoJugador;
                        }
                        return null;

                    } catch (UsuarioDuplicadoException e) {
                        System.out.println("Error: " + e.getMessage());
                    }  /* catch (SQLException e) {
                        System.out.println("Error de base de datos: " + e.getMessage());
                    }*/
                }
                case "0" -> {
                    return null;
                }
                default -> System.out.println("Opción incorrecta. Ingresá 1, 2 o 0.");
            }
        }
    }
}
