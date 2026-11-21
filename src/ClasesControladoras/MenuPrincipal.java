package ClasesControladoras;

import ClasesPOJO.Jugador;
import ClasesPOJO.Usuario;
import Excepciones.ContraseniaIncorrectaException;

import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

public class MenuPrincipal {

        private final Scanner scanner = new Scanner(System.in);
        private final Administrador admin = new Administrador("utnmdp2026");

        // Colección Set donde vivirán los jugadores cargados desde MySQL (y en memoria mientras jugamos)
        private final Set<Jugador> listaJugadores = new LinkedHashSet<>();

        // Cuando conectemos SQL, solo descomentamos esta línea y cargamos la lista desde la BD:
        // private final JugadorService jugadorService = new JugadorService();

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
                            EscenasController escenasController = new EscenasController(jugadorActivo);
                            escenasController.iniciarJuego();

                            // Cuando el jugador toca '0' en una escena y vuelve acá, al conectar SQL descomentamos:
                            // jugadorService.guardarPartida(jugadorActivo);
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

