package ClasesControladoras;

import ClasesPOJO.Escena;
import ClasesPOJO.Items.Item;
import ClasesPOJO.Items.TipoItem;
import ClasesPOJO.Jugador;
import ClasesService.EscenaService;
import ClasesService.ItemService;
import ClasesService.JugadorService;
import Excepciones.ContraseniaIncorrectaException;
import Excepciones.UsuarioDuplicadoException;

import java.sql.SQLException;
import java.util.Scanner;

public class Administrador {
    private final String password;

    public Administrador(String password) {
        this.password = password;
    }

    public void validarPassword(String ingreso) throws ContraseniaIncorrectaException {
        if (!this.password.equals(ingreso)) {
            throw new ContraseniaIncorrectaException();
        }
    }

    // ===================== MENÚ PRINCIPAL =====================

    public void mostrarMenuABMLC(Scanner scanner) {
        boolean volver = false;

        while (!volver) {
            System.out.println("\n--- PANEL DE ADMINISTRADOR (ABMLC) ---");
            System.out.println("1 - Gestionar Jugadores");
            System.out.println("2 - Gestionar Ítems");
            System.out.println("3 - Gestionar Enemigos");
            System.out.println("4 - Gestionar Escenas");
            System.out.println("0 - Volver al menú principal");
            System.out.print("Elegí una entidad: ");

            String entidad = scanner.nextLine().trim();

            switch (entidad) {
                case "1" -> subMenuJugadores(scanner);
                case "2" -> subMenuItems(scanner);
                case "3" -> System.out.println("Enemigos todavía no tiene Service conectado.");
                case "4" -> subMenuEscenas(scanner);
                case "0" -> volver = true;
                default -> System.out.println("Opción incorrecta.");
            }
        }
    }

    private void mostrarOpcionesABMLC(String entidad) {
        System.out.println("\n--- ABMLC DE " + entidad.toUpperCase() + " ---");
        System.out.println("1 - Alta (Crear nuevo)");
        System.out.println("2 - Baja lógica (Desactivar)");
        System.out.println("3 - Modificar");
        System.out.println("4 - Listar activos");
        System.out.println("5 - Consultar por ID / Nombre");
        System.out.println("6 - Listar dados de baja");
        System.out.println("7 - Reactivar dado de baja");
        System.out.println("0 - Volver atrás");
        System.out.print("Elegí una operación: ");
    }

    // ===================== JUGADORES =====================

    private void subMenuJugadores(Scanner scanner) {
        JugadorService jugadorService;
        try {
            jugadorService = new JugadorService();
        } catch (SQLException e) {
            System.out.println("No se pudo conectar con la base de datos: " + e.getMessage());
            return;
        }

        boolean atras = false;
        while (!atras) {
            mostrarOpcionesABMLC("Jugadores");
            String op = scanner.nextLine().trim();
            try {
                switch (op) {
                    case "1" -> {
                        Jugador nuevo = leerJugador(scanner);
                        System.out.println(jugadorService.agregar(nuevo)
                                ? "Jugador creado." : "No se pudo crear el jugador.");
                    }
                    case "2" -> {
                        int id = leerId(scanner);
                        System.out.println(jugadorService.eliminar(id)
                                ? "Jugador dado de baja." : "No existe un jugador activo con ese ID.");
                    }
                    case "3" -> {
                        int id = leerId(scanner);
                        if (jugadorService.buscarPorIdBD(id) == null) {
                            System.out.println("No existe un jugador con ese ID.");
                        } else {
                            Jugador modificado = leerJugador(scanner);
                            System.out.println(jugadorService.modificar(id, modificado)
                                    ? "Jugador modificado." : "No se pudo modificar el jugador.");
                        }
                    }
                    case "4" -> imprimirListado(jugadorService.mostrarListado(), "No hay jugadores activos.");
                    case "5" -> {
                        Jugador j = consultarJugador(scanner, jugadorService);
                        System.out.println(j == null ? "No se encontró el jugador." : jugadorService.mostrar(j));
                    }
                    case "6" -> imprimirListado(jugadorService.mostrarDadosDeBaja(), "No hay jugadores dados de baja.");
                    case "7" -> {
                        int id = leerId(scanner);
                        System.out.println(jugadorService.reactivar(id)
                                ? "Jugador reactivado." : "No se pudo reactivar (ID inexistente o ya activo).");
                    }
                    case "0" -> atras = true;
                    default -> System.out.println("Opción incorrecta.");
                }
            } catch (UsuarioDuplicadoException e) {
                System.out.println("Ya existe un jugador con ese nombre.");
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Ingresá un número válido.");
            }
        }
    }

    private Jugador consultarJugador(Scanner scanner, JugadorService jugadorService) throws SQLException {
        if (preguntarPorId(scanner)) {
            return jugadorService.buscarPorIdBD(leerId(scanner));
        }
        return jugadorService.buscarPorNombreBD(leerTexto(scanner, "Nombre: "));
    }

    private Jugador leerJugador(Scanner scanner) {
        String nombre = leerTexto(scanner, "Nombre: ");
        return new Jugador(nombre);
    }

    // ===================== ÍTEMS =====================

    private void subMenuItems(Scanner scanner) {
        ItemService itemService;
        try {
            itemService = new ItemService();
        } catch (SQLException e) {
            System.out.println("No se pudo conectar con la base de datos: " + e.getMessage());
            return;
        }

        boolean atras = false;
        while (!atras) {
            mostrarOpcionesABMLC("Ítems");
            String op = scanner.nextLine().trim();
            try {
                switch (op) {
                    case "1" -> {
                        Item nuevo = leerItem(scanner);
                        System.out.println(itemService.agregar(nuevo)
                                ? "Ítem creado." : "Ya existe un ítem con ese nombre o no se pudo insertar.");
                    }
                    case "2" -> {
                        int id = leerId(scanner);
                        System.out.println(itemService.eliminar(id)
                                ? "Ítem dado de baja." : "No existe un ítem activo con ese ID.");
                    }
                    case "3" -> {
                        int id = leerId(scanner);
                        if (itemService.buscarPorIdBD(id) == null) {
                            System.out.println("No existe un ítem con ese ID.");
                        } else {
                            Item modificado = leerItem(scanner);
                            System.out.println(itemService.modificar(id, modificado)
                                    ? "Ítem modificado." : "El nombre ya lo usa otro ítem o no se pudo modificar.");
                        }
                    }
                    case "4" -> imprimirListado(itemService.mostrarListado(), "No hay ítems activos.");
                    case "5" -> {
                        Item i = consultarItem(scanner, itemService);
                        System.out.println(i == null ? "No se encontró el ítem." : itemService.mostrar(i));
                    }
                    case "6" -> imprimirListado(itemService.mostrarDadosDeBaja(), "No hay ítems dados de baja.");
                    case "7" -> {
                        int id = leerId(scanner);
                        System.out.println(itemService.reactivar(id)
                                ? "Ítem reactivado." : "No se pudo reactivar (ID inexistente o ya activo).");
                    }
                    case "0" -> atras = true;
                    default -> System.out.println("Opción incorrecta.");
                }
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Ingresá un número válido.");
            }
        }
    }

    private Item consultarItem(Scanner scanner, ItemService itemService) throws SQLException {
        if (preguntarPorId(scanner)) {
            return itemService.buscarPorIdBD(leerId(scanner));
        }
        return itemService.buscarPorNombreBD(leerTexto(scanner, "Nombre: "));
    }

    private Item leerItem(Scanner scanner) {
        String nombre = leerTexto(scanner, "Nombre: ");
        String descripcion = leerTexto(scanner, "Descripción: ");
        TipoItem tipo = leerTipoItem(scanner);
        int curacion = Integer.parseInt(leerTexto(scanner, "Efecto de curación: "));
        int danio = Integer.parseInt(leerTexto(scanner, "Daño: "));
        return new Item(0, nombre, descripcion, tipo, curacion, danio, true);
    }
    private TipoItem leerTipoItem(Scanner scanner) {
        while (true) {
            System.out.println("Tipos disponibles:");
            for (TipoItem t : TipoItem.values()) {
                System.out.println(" - " + t);
            }
            String ingreso = leerTexto(scanner, "Tipo: ").toUpperCase();
            try {
                return TipoItem.valueOf(ingreso);
            } catch (IllegalArgumentException e) {
                System.out.println("Tipo inválido.");
            }
        }
    }

    // ===================== ESCENAS =====================

    private void subMenuEscenas(Scanner scanner) {
        EscenaService escenaService;
        try {
            escenaService = new EscenaService();
        } catch (SQLException e) {
            System.out.println("No se pudo conectar con la base de datos: " + e.getMessage());
            return;
        }

        boolean atras = false;
        while (!atras) {
            mostrarOpcionesABMLC("Escenas");
            String op = scanner.nextLine().trim();
            try {
                switch (op) {
                    case "1" -> {
                        Escena nueva = leerEscena(scanner);
                        System.out.println(escenaService.agregar(nueva)
                                ? "Escena creada." : "Ya existe una escena con ese nombre o no se pudo insertar.");
                    }
                    case "2" -> {
                        int id = leerId(scanner);
                        System.out.println(escenaService.eliminar(id)
                                ? "Escena dada de baja." : "No existe una escena activa con ese ID.");
                    }
                    case "3" -> {
                        int id = leerId(scanner);
                        if (escenaService.buscarPorIdBD(id) == null) {
                            System.out.println("No existe una escena con ese ID.");
                        } else {
                            Escena modificada = leerEscena(scanner);
                            System.out.println(escenaService.modificar(id, modificada)
                                    ? "Escena modificada." : "El nombre ya lo usa otra escena o no se pudo modificar.");
                        }
                    }
                    case "4" -> imprimirListado(escenaService.mostrarListado(), "No hay escenas activas.");
                    case "5" -> {
                        Escena e = consultarEscena(scanner, escenaService);
                        System.out.println(e == null ? "No se encontró la escena." : e);
                    }
                    case "6" -> imprimirListado(escenaService.mostrarDadosDeBaja(), "No hay escenas dadas de baja.");
                    case "7" -> {
                        int id = leerId(scanner);
                        System.out.println(escenaService.reactivar(id)
                                ? "Escena reactivada." : "No se pudo reactivar (ID inexistente o ya activa).");
                    }
                    case "0" -> atras = true;
                    default -> System.out.println("Opción incorrecta.");
                }
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Ingresá un número válido.");
            }
        }
    }

    private Escena consultarEscena(Scanner scanner, EscenaService escenaService) throws SQLException {
        if (preguntarPorId(scanner)) {
            return escenaService.buscarPorIdBD(leerId(scanner));
        }
        return escenaService.buscarPorNombreBD(leerTexto(scanner, "Nombre: "));
    }

    private Escena leerEscena(Scanner scanner) {
        String nombre = leerTexto(scanner, "Nombre: ");
        String descripcion = leerTexto(scanner, "Descripción: ");
        return new Escena(0, nombre, descripcion, true);
    }
    // ===================== AUXILIARES =====================

    private int leerId(Scanner scanner) {
        return Integer.parseInt(leerTexto(scanner, "ID: "));
    }

    private String leerTexto(Scanner scanner, String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private boolean preguntarPorId(Scanner scanner) {
        System.out.println("1 - Buscar por ID");
        System.out.println("2 - Buscar por nombre");
        return leerTexto(scanner, "Elegí: ").equals("1");
    }

    private void imprimirListado(String listado, String vacio) {
        System.out.println(listado.isBlank() ? vacio : listado);
    }
}
