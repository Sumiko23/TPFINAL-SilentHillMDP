package ClasesControladoras;

import ClasesPOJO.Jugador;
import ClasesPOJO.Usuario;
import ClasesService.JugadorService;
import Excepciones.ContraseniaIncorrectaException;
import Excepciones.UsuarioDuplicadoException;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

public class MenuPrincipal {

        private final Scanner scanner = new Scanner(System.in);
        private final Administrador admin = new Administrador("utnmdp2026");

        // Colección Set donde vivirán los jugadores cargados desde MySQL (y en memoria mientras jugamos)
        private final Set<Jugador> listaJugadores = new LinkedHashSet<>();

        // Cuando conectemos SQL, solo descomentamos esta línea y cargamos la lista desde la BD:


        /* private  JugadorService jugadorService ;

         try {
        jugadorService = new JugadorService();
       } catch ( SQLException e) {
        System.out.println("No se pudo conectar con la base de datos: " + e.getMessage());

       }    */



        public void iniciar() {
            // Al conectar SQL:
            // listaJugadores.addAll(jugadorService.listarActivos());

            boolean salir = false;

            while (!salir) {
                mostrarLogo();
                System.out.println("\n1 - Entrar como usuario");
                System.out.println("2 - Entrar como administrador");
                System.out.println("0 - Salir");
                System.out.print("Elegí una opción: ");
                String opcion = scanner.nextLine().trim();
                separarPantalla();

                switch (opcion) {
                    case "1" -> {
                        Jugador jugadorActivo = Usuario.gestionarAccesoUsuario(scanner, listaJugadores);
                        if (jugadorActivo != null) {
                            EscenasController escenasController = new EscenasController(jugadorActivo,scanner);
                            escenasController.iniciarJuego();

                            /* Cuando el jugador toca '0' en una escena y vuelve acá, al conectar SQL descomentamos:

                            try {
                                if (jugadorService.guardarPartida(jugadorActivo)) {
                                    System.out.println("Partida guardada correctamente.");
                                } else {
                                    System.out.println("No se encontró el jugador en la base, no se guardó la partida.");
                                }
                            } catch (SQLException e) {
                                System.out.println("No se pudo guardar la partida: " + e.getMessage());
                            }   */

                        }
                    }
                    case "2" -> {
                        System.out.print("Ingresá la contraseña de administrador: ");
                        String password = scanner.nextLine().trim();
                        try {
                            admin.validarPassword(password);
                            System.out.println("Contraseña correcta. Ingresando al Panel de Control...");
                            separarPantalla();

                            // Acá llamaremos a PanelControl (ABMLC)
                        } catch (ContraseniaIncorrectaException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                    }
                    case "0" -> {
                        System.out.println("Saliendo de Silent Hill: Mar del Plata...");
                        salir = true;
                    }
                    default -> System.out.println("Opción incorrecta. Ingresá 1, 2 o 0.");
                }
            }
        }

    // ===================== MENÚ DE USUARIO =====================

    /**
     * Menú de acceso del usuario. Devuelve el Jugador elegido/creado para jugar,
     * o null si vuelve al menú principal.
     */
    private Jugador gestionarAccesoUsuario() throws SQLException {
        while (true) {
            System.out.println("\n--- MENÚ DE USUARIO ---");
            System.out.println("1 - Continuar partida");
            System.out.println("2 - Iniciar partida nueva");
            System.out.println("0 - Volver al menú principal");
            System.out.print("Elegí una opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> {
                    Jugador elegido = continuarPartida();
                    if (elegido != null) {
                        return elegido;
                    }
                }
                case "2" -> {
                    Jugador nuevo = iniciarPartidaNueva();
                    if (nuevo != null) {
                        return nuevo;
                    }
                }
                case "0" -> {
                    return null;
                }
                default -> System.out.println("Opción incorrecta. Ingresá 1, 2 o 0.");
            }
        }
    }

    private Jugador continuarPartida() throws SQLException {

        JugadorService jugadorService = new JugadorService();

        Set<Jugador> listaJugadores = jugadorService.listarActivos();

        if (listaJugadores.isEmpty()) {
            System.out.println("La lista de partidas guardadas está vacía.");
            separarPantalla();
            return null;
        }

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
            return null;
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
        return null;
    }

    private Jugador iniciarPartidaNueva() throws SQLException {
        JugadorService jugadorService = new JugadorService();
        System.out.print("Ingresá tu nombre de jugador (o 0 para volver): ");
        String nombre = scanner.nextLine().trim();

        if ("0".equals(nombre)) {
            return null;
        }
        if (nombre.isEmpty()) {
            System.out.println("El nombre no puede estar vacío.");
            return null;
        }

        try {
            Jugador nuevoJugador = new Jugador(nombre);

            // El service valida el nombre contra la base y lo agrega a la lista en memoria
            if (!jugadorService.agregar(nuevoJugador)) {
                System.out.println("No se pudo crear el jugador.");
                return null;
            }

            System.out.println("¡Usuario '" + nombre + "' creado con éxito!");
            System.out.println("1 - JUGAR");
            System.out.println("0 - Volver al menú principal");
            System.out.print("Elegí una opción: ");
            String jugarOp = scanner.nextLine().trim();

            return "1".equals(jugarOp) ? nuevoJugador : null;

        } catch (UsuarioDuplicadoException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
        return null;
    }

        private void mostrarLogo() {
            String logo =
                    "  ██████╗ ██╗██╗     ███████╗███╗   ██╗████████╗   ██╗  ██╗██╗██╗     ██╗     \n" +
                            " ██╔════╝ ██║██║     ██╔════╝████╗  ██║╚══██╔══╝   ██║  ██║██║██║     ██║     \n" +
                            " ╚█████╗  ██║██║     █████╗  ██╔██╗ ██║   ██║      ███████║██║██║     ██║     \n" +
                            "  ╚═══██╗ ██║██║     ██╔══╝  ██║╚██╗██║   ██║      ██╔══██║██║██║     ██║     \n" +
                            " ██████╔╝ ██║███████╗███████╗██║ ╚████║   ██║      ██║  ██║██║███████╗███████╗\n" +
                            " ╚═════╝  ╚═╝╚══════╝╚══════╝╚═╝  ╚═══╝   ╚═╝      ╚═╝  ╚═╝╚═╝╚══════╝╚══════╝\n" +
                            "                            M A R   D E L   P L A T A\n" +
                            "                                      :::          ..  . .. ... ... .. .\n" +
                            "                                    :::*:::  .:'  . . ..::*:'*:'*:'*:'*:'*:\n" +
                            "                                    ':::::'\n" +
                            "             /|                      |:::|                      |\\\n" +
                            "            / |                     [|===|]                     | \\\n" +
                            "           /  |                      |:::|                      |  \\\n" +
                            "          /___|                     [|===|]                     |___\\\n" +
                            "      _.-'    '-._                   |:::|                   _.-'    '-._\n" +
                            "     (___  ⚓  ___)                  |:::|                  (___  ⚓  ___)\n" +
                            "       \\________/    |               |:::|               |    \\________/\n" +
                            "~~~~~~~~~~~~~~~~~~~~/|~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|\\~~~~~~~~~~~~~~~~~\n" +
                            "   . . . . . . . __/  \\__ . . . . . . . . . . . . . . __/  \\__. . . . . . \n" +
                            "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
            System.out.println(logo);
        }

    public static void separarPantalla() {
        // En la consola integrada de IntelliJ no existe borrado nativo de texto;
        // se imprime un separador limpio para delimitar cada cambio de pantalla.
        System.out.println("\n-------------------------------------------------------\n");
    }

}

