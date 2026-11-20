package ClasesControladoras;
import ClasesPOJO.Jugador;
import java.util.Scanner;

    public class EscenasController {

        private final Jugador jugador;
        private final Scanner scanner;

        public EscenasController(Jugador jugador, Scanner scanner) {
            this.jugador = jugador;
            this.scanner = scanner;
        }

        public void iniciarJuego() {
            System.out.println("\nComenzando partida de " + jugador.getNombre()
                    + " en la escena: " + jugador.getEscenaActual());
            System.out.println("(Acá iremos armando las escenas paso a paso).");
            System.out.print("Presioná Enter para volver al menú principal...");
            scanner.nextLine();
        }
    }
