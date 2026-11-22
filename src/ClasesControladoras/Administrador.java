package ClasesControladoras;

import ClasesPOJO.Enemigos.Enemigo;
import ClasesPOJO.Enemigos.EnemigoComun;
import ClasesPOJO.Enemigos.JefeFinal;
import ClasesPOJO.Escena;
import ClasesPOJO.Items.Item;
import ClasesPOJO.Items.TipoItem;
import ClasesPOJO.Jugador;
import ClasesService.EnemigoService;
import ClasesService.EscenaService;
import ClasesService.ItemService;
import ClasesService.JugadorService;
import Excepciones.ContraseniaIncorrectaException;
import Excepciones.UsuarioDuplicadoException;

import java.sql.SQLException;
import java.util.Scanner;

public class Administrador {
    private final String password;
    private final Scanner scanner ;

    public Administrador(String password, Scanner scanner) {
        this.password = password;
        this.scanner = scanner;
    }


    /// metodo para validar la contrasenia del admin
    public void validarPassword(String ingreso) throws ContraseniaIncorrectaException {
        if (!this.password.equals(ingreso)) {
            throw new ContraseniaIncorrectaException();
        }
    }

    // ===================== MENÚ PRINCIPAL =====================

    public void mostrarMenuABMLC() {

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
                case "1" -> subMenuJugadores();
                case "2" -> subMenuItems();
                case "3" -> subMenuEnemigos();
                case "4" -> subMenuEscenas();
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

    private void subMenuJugadores() {
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

            switch (op) {
                case "1" -> {
                    System.out.print("Ingrese nombre Jugador: ");
                    String nombre = scanner.nextLine().trim();
                    try {
                        System.out.println(jugadorService.agregar(new Jugador(nombre))
                                ? "Jugador creado." : "No se pudo crear el jugador.");
                    } catch (UsuarioDuplicadoException e) {
                        System.out.println(e.getMessage());
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos: " + e.getMessage());
                    }
                }
                case "2" -> {
                    try {

                        System.out.println("Ingrese un id para dar de baja al  jugador :");
                        int id = Integer.parseInt(scanner.nextLine().trim());

                        System.out.println(jugadorService.eliminar(id)
                                ? "Jugador dado de baja." : "No existe un jugador activo con ese ID.");
                    } catch (NumberFormatException e) {
                        System.out.println("Ingresá un número válido.");
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos " + e.getMessage());
                    }

                }
                case "3" -> {
                    try {
                        System.out.println("Ingrese un id para modificar al  jugador :");
                        int id = Integer.parseInt(scanner.nextLine().trim());


                        if (jugadorService.buscarPorIdBD(id) == null) {
                            System.out.println("No existe un jugador con ese ID.");
                        } else {
                            System.out.print("Ingrese el nuevo nombre : ");
                            String nombre = scanner.nextLine().trim();
                            System.out.println(jugadorService.modificar(id, new Jugador(nombre))
                                    ? "Jugador modificado." : "No se pudo modificar el jugador.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Ingresá un número válido.");
                    } catch (UsuarioDuplicadoException e) {
                        System.out.println(e.getMessage());
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos " + e.getMessage());
                    }

                }
                case "4" -> {
                    System.out.println(" LISTADO DE JUGADORES ACTIVOS ...");
                    System.out.println(jugadorService.mostrarListado());
                }
                case "5" -> {

                    try {
                        System.out.println("1 - Buscar por ID");
                        System.out.println("2 - Buscar por nombre");
                        System.out.print("Ingrese una opcion : ");
                        String opcion = scanner.nextLine().trim();
                        Jugador aux;
                        if (opcion.equals("1")) {
                            System.out.print("ID: ");
                            aux = jugadorService.buscarPorIdBD(Integer.parseInt(scanner.nextLine().trim()));
                        } else {
                            System.out.print("Nombre: ");
                            aux = jugadorService.buscarPorNombreBD(scanner.nextLine().trim());
                        }
                        System.out.println(aux == null ? "No se encontró el jugador." : jugadorService.mostrar(aux));
                    } catch (NumberFormatException e) {
                        System.out.println("Ingresá un número válido.");
                    } catch (SQLException e) {
                        System.out.println("Error de base de datos: " + e.getMessage());
                    }

                }
                case "6" -> {
                    try {
                        System.out.println(jugadorService.mostrarDadosDeBaja());
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos " + e.getMessage());
                    }
                }

                case "7" -> {
                    try {
                        System.out.println("Ingrese el id del jugador para reactivarlo");
                        int id = Integer.parseInt(scanner.nextLine().trim());

                        System.out.println(jugadorService.reactivar(id)
                                ? "Jugador reactivado." : "No se pudo reactivar (ID inexistente o ya activo).");
                    } catch (NumberFormatException e) {
                        System.out.println("Ingresá un número válido.");
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos " + e.getMessage());
                    }

                }
                case "0" -> atras = true;
                default -> System.out.println("Opción incorrecta.");
            }

        }
    }


    // ===================== ITEMS =====================


    private void subMenuItems() {
        ItemService itemService;
        try {
            itemService = new ItemService();
        } catch (SQLException e) {
            System.out.println("Error en la base de datos: " + e.getMessage());
            return;
        }

        boolean atras = false;
        while (!atras) {
            mostrarOpcionesABMLC("Ítems");
            String op = scanner.nextLine().trim();

            switch (op) {
                case "1" -> {
                    try {
                        System.out.print("Ingrese nombre del ítem: ");
                        String nombre = scanner.nextLine().trim();
                        System.out.print("Ingrese descripción: ");
                        String descripcion = scanner.nextLine().trim();
                        TipoItem tipo = leerTipoItem();
                        System.out.print("Ingrese efecto de curación: ");
                        int curacion = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Ingrese daño: ");
                        int danio = Integer.parseInt(scanner.nextLine().trim());

                        Item nuevo = new Item(0, nombre, descripcion, tipo, curacion, danio, true);
                        System.out.println(itemService.agregar(nuevo)
                                ? "Ítem creado." : "Ya existe un ítem con ese nombre o no se pudo insertar.");
                    } catch (NumberFormatException e) {
                        System.out.println("Ingresá un número válido.");
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos: " + e.getMessage());
                    }
                }
                case "2" -> {
                    try {
                        System.out.print("Ingrese un ID para dar de baja al ítem: ");
                        int id = Integer.parseInt(scanner.nextLine().trim());
                        System.out.println(itemService.eliminar(id)
                                ? "Ítem dado de baja." : "No existe un ítem activo con ese ID.");
                    } catch (NumberFormatException e) {
                        System.out.println("Ingresá un número válido.");
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos: " + e.getMessage());
                    }
                }
                case "3" -> {
                    try {
                        System.out.print("Ingrese un ID para modificar el ítem: ");
                        int id = Integer.parseInt(scanner.nextLine().trim());

                        if (itemService.buscarPorIdBD(id) == null) {
                            System.out.println("No existe un ítem con ese ID.");
                        } else {
                            System.out.print("Ingrese el nuevo nombre: ");
                            String nombre = scanner.nextLine().trim();
                            System.out.print("Ingrese la nueva descripción: ");
                            String descripcion = scanner.nextLine().trim();
                            TipoItem tipo = leerTipoItem();
                            System.out.print("Ingrese el nuevo efecto de curación: ");
                            int curacion = Integer.parseInt(scanner.nextLine().trim());
                            System.out.print("Ingrese el nuevo daño: ");
                            int danio = Integer.parseInt(scanner.nextLine().trim());

                            Item modificado = new Item(0, nombre, descripcion, tipo, curacion, danio, true);
                            System.out.println(itemService.modificar(id, modificado)
                                    ? "Ítem modificado." : "El nombre ya lo usa otro ítem o no se pudo modificar.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Ingresá un número válido.");
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos: " + e.getMessage());
                    }
                }
                case "4" -> {
                    System.out.println("LISTADO DE ÍTEMS ACTIVOS...");
                    System.out.println(itemService.mostrarListado());
                }
                case "5" -> {
                    try {
                        System.out.println("1 - Buscar por ID");
                        System.out.println("2 - Buscar por nombre");
                        System.out.print("Elegí: ");
                        String opcion = scanner.nextLine().trim();
                        Item aux;
                        if (opcion.equals("1")) {
                            System.out.print("ID: ");
                            aux = itemService.buscarPorIdBD(Integer.parseInt(scanner.nextLine().trim()));
                        } else {
                            System.out.print("Nombre: ");
                            aux = itemService.buscarPorNombreBD(scanner.nextLine().trim());
                        }
                        System.out.println(aux == null ? "No se encontró el ítem." : itemService.mostrar(aux));
                    } catch (NumberFormatException e) {
                        System.out.println("Ingresá un número válido.");
                    } catch (SQLException e) {
                        System.out.println("Error de base de datos: " + e.getMessage());
                    }
                }
                case "6" -> {
                    try {
                        System.out.println(itemService.mostrarDadosDeBaja());
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos: " + e.getMessage());
                    }
                }
                case "7" -> {
                    try {
                        System.out.print("Ingrese el ID del ítem para reactivarlo: ");
                        int id = Integer.parseInt(scanner.nextLine().trim());
                        System.out.println(itemService.reactivar(id)
                                ? "Ítem reactivado." : "No se pudo reactivar (ID inexistente o ya activo).");
                    } catch (NumberFormatException e) {
                        System.out.println("Ingresá un número válido.");
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos: " + e.getMessage());
                    }
                }
                case "0" -> atras = true;
                default -> System.out.println("Opción incorrecta.");
            }
        }
    }



    /// METODO PARA QUE EL USUARIO INGRESE EL TIPO DE ITEM (ENUM), PARA VALIDARLO Y NO REPETIR CODIGO
    private TipoItem leerTipoItem() {
        while (true) {
            System.out.println("Tipos disponibles:");
            for (TipoItem t : TipoItem.values()) {
                System.out.println(" - " + t);
            }
            System.out.print("Ingrese el tipo de ítem: ");
            String ingreso = scanner.nextLine().trim().toUpperCase().replace(" ", "_");
            try {
                return TipoItem.valueOf(ingreso);
            } catch (IllegalArgumentException e) {
                System.out.println("Tipo inválido.");
            }
        }
    }

    // ===================== ENEMIGOS =====================


// ===================== ENEMIGOS =====================

    private void subMenuEnemigos() {
        EnemigoService enemigoService;
        try {
            enemigoService = new EnemigoService();
        } catch (SQLException e) {
            System.out.println("Error en la base de datos: " + e.getMessage());
            return;
        }

        boolean atras = false;
        while (!atras) {
            mostrarOpcionesABMLC("Enemigos");
            String op = scanner.nextLine().trim();

            switch (op) {
                case "1" -> {
                    try {
                        Enemigo nuevo = leerDatosEnemigo();
                        System.out.println(enemigoService.agregar(nuevo)
                                ? "Enemigo creado." : "No se pudo agregar a el enemigo .");
                    } catch (NumberFormatException e) {
                        System.out.println("Ingresá un número válido.");
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos: " + e.getMessage());
                    }
                }
                case "2" -> {
                    try {
                        System.out.print("Ingrese un ID para dar de baja al enemigo: ");
                        int id = Integer.parseInt(scanner.nextLine().trim());
                        System.out.println(enemigoService.eliminar(id)
                                ? "Enemigo dado de baja." : "No existe un enemigo activo con ese ID.");
                    } catch (NumberFormatException e) {
                        System.out.println("Ingresá un número válido.");
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos: " + e.getMessage());
                    }
                }
                case "3" -> {
                    try {
                        System.out.print("Ingrese un ID para modificar el enemigo: ");
                        int id = Integer.parseInt(scanner.nextLine().trim());

                        if (enemigoService.buscarPorIdBD(id) == null) {
                            System.out.println("No existe un enemigo con ese ID.");
                        } else {
                            Enemigo modificado = leerDatosEnemigo();
                            System.out.println(enemigoService.modificar(id, modificado)
                                    ? "Enemigo modificado." : "No se pudo modificar al enemigo .");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Ingresá un número válido.");
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos: " + e.getMessage());
                    }
                }
                case "4" -> {
                    System.out.println("LISTADO DE ENEMIGOS ACTIVOS...");
                    System.out.println(enemigoService.mostrarListado());
                }
                case "5" -> {
                    try {
                        System.out.println("1 - Buscar por ID");
                        System.out.println("2 - Buscar por nombre");
                        System.out.print("Elegí: ");
                        String opcion = scanner.nextLine().trim();
                        Enemigo aux;
                        if (opcion.equals("1")) {
                            System.out.print("ID: ");
                            aux = enemigoService.buscarPorIdBD(Integer.parseInt(scanner.nextLine().trim()));
                        } else {
                            System.out.print("Nombre: ");
                            aux = enemigoService.buscarPorNombreBD(scanner.nextLine().trim());
                        }
                        System.out.println(aux == null ? "No se encontró el enemigo." : enemigoService.mostrar(aux));
                    } catch (NumberFormatException e) {
                        System.out.println("Ingresá un número válido.");
                    } catch (SQLException e) {
                        System.out.println("Error de base de datos: " + e.getMessage());
                    }
                }
                case "6" -> {
                    try {
                        System.out.println(enemigoService.mostrarDadosDeBaja());
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos: " + e.getMessage());
                    }
                }
                case "7" -> {
                    try {
                        System.out.print("Ingrese el ID del enemigo para reactivarlo: ");
                        int id = Integer.parseInt(scanner.nextLine().trim());
                        System.out.println(enemigoService.reactivar(id)
                                ? "Enemigo reactivado." : "No se pudo reactivar al enemigo.");
                    } catch (NumberFormatException e) {
                        System.out.println("Ingresá un número válido.");
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos: " + e.getMessage());
                    }
                }
                case "0" -> atras = true;
                default -> System.out.println("Opción incorrecta.");
            }
        }
    }


    /// metodo auxiliar para crear al enemigo segun si es jefe o enemigo comun
    private Enemigo leerDatosEnemigo() {
        System.out.print("Ingrese nombre del enemigo: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Ingrese salud máxima: ");
        int saludMaxima = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Ingrese daño de ataque: ");
        int danio = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("¿Es esquivable? (s/n): ");
        boolean esquivable = scanner.nextLine().trim().equalsIgnoreCase("s");
        System.out.print("¿Es jefe final? (s/n): ");
        boolean esJefe = scanner.nextLine().trim().equalsIgnoreCase("s");

        if (esJefe) {
            System.out.print("Ingrese tiempo límite en ms: ");
            long tiempoLimiteMs = Long.parseLong(scanner.nextLine().trim());
            return new JefeFinal(0, nombre, saludMaxima, danio, esquivable, true, tiempoLimiteMs);
        }
        return new EnemigoComun(0, nombre, saludMaxima, danio, esquivable, true);
    }


// ===================== ESCENAS =====================

    private void subMenuEscenas() {
        EscenaService escenaService;
        try {
            escenaService = new EscenaService();
        } catch (SQLException e) {
            System.out.println("Error en la base de datos: " + e.getMessage());
            return;
        }

        boolean atras = false;
        while (!atras) {
            mostrarOpcionesABMLC("Escenas");
            String op = scanner.nextLine().trim();

            switch (op) {
                case "1" -> {
                    try {
                        System.out.print("Ingrese nombre de la escena: ");
                        String nombre = scanner.nextLine().trim();
                        System.out.print("Ingrese descripción: ");
                        String descripcion = scanner.nextLine().trim();

                        Escena nueva = new Escena(0, nombre, descripcion, true);
                        System.out.println(escenaService.agregar(nueva)
                                ? "Escena creada." : "Ya existe una escena con ese nombre o no se pudo insertar.");
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos: " + e.getMessage());
                    }
                }
                case "2" -> {
                    try {
                        System.out.print("Ingrese un ID para dar de baja la escena: ");
                        int id = Integer.parseInt(scanner.nextLine().trim());
                        System.out.println(escenaService.eliminar(id)
                                ? "Escena dada de baja." : "No existe una escena activa con ese ID.");
                    } catch (NumberFormatException e) {
                        System.out.println("Ingresá un número válido.");
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos: " + e.getMessage());
                    }
                }
                case "3" -> {
                    try {
                        System.out.print("Ingrese un ID para modificar la escena: ");
                        int id = Integer.parseInt(scanner.nextLine().trim());

                        if (escenaService.buscarPorIdBD(id) == null) {
                            System.out.println("No existe una escena con ese ID.");
                        } else {
                            System.out.print("Ingrese el nuevo nombre: ");
                            String nombre = scanner.nextLine().trim();
                            System.out.print("Ingrese la nueva descripción: ");
                            String descripcion = scanner.nextLine().trim();

                            Escena modificada = new Escena(0, nombre, descripcion, true);
                            System.out.println(escenaService.modificar(id, modificada)
                                    ? "Escena modificada." : "El nombre ya lo usa otra escena o no se pudo modificar.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Ingresá un número válido.");
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos: " + e.getMessage());
                    }
                }
                case "4" -> {
                    System.out.println("LISTADO DE ESCENAS ACTIVAS...");
                    System.out.println(escenaService.mostrarListado());
                }
                case "5" -> {
                    try {
                        System.out.println("1 - Buscar por ID");
                        System.out.println("2 - Buscar por nombre");
                        System.out.print("Elegí: ");
                        String opcion = scanner.nextLine().trim();
                        Escena aux;
                        if (opcion.equals("1")) {
                            System.out.print("ID: ");
                            aux = escenaService.buscarPorIdBD(Integer.parseInt(scanner.nextLine().trim()));
                        } else {
                            System.out.print("Nombre: ");
                            aux = escenaService.buscarPorNombreBD(scanner.nextLine().trim());
                        }
                        System.out.println(aux == null ? "No se encontró la escena." : escenaService.mostrar(aux));
                    } catch (NumberFormatException e) {
                        System.out.println("Ingresá un número válido.");
                    } catch (SQLException e) {
                        System.out.println("Error de base de datos: " + e.getMessage());
                    }
                }
                case "6" -> {
                    try {
                        System.out.println(escenaService.mostrarDadosDeBaja());
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos: " + e.getMessage());
                    }
                }
                case "7" -> {
                    try {
                        System.out.print("Ingrese el ID de la escena para reactivarla: ");
                        int id = Integer.parseInt(scanner.nextLine().trim());
                        System.out.println(escenaService.reactivar(id)
                                ? "Escena reactivada." : "No se pudo reactivar (ID inexistente o ya activa).");
                    } catch (NumberFormatException e) {
                        System.out.println("Ingresá un número válido.");
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos: " + e.getMessage());
                    }
                }
                case "0" -> atras = true;
                default -> System.out.println("Opción incorrecta.");
            }
        }
    }






}

















