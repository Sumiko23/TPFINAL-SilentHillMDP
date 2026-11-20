package ClasesControladoras;

import Excepciones.ContraseniaIncorrectaException;

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
                    case "1" -> subMenuABMLC(scanner, "Jugadores");
                    case "2" -> subMenuABMLC(scanner, "Ítems");
                    case "3" -> subMenuABMLC(scanner, "Enemigos");
                    case "4" -> subMenuABMLC(scanner, "Escenas");
                    case "0" -> volver = true;
                    default -> System.out.println("Opción incorrecta.");
                }
            }
        }

        private void subMenuABMLC(Scanner scanner, String entidad) {
            boolean atras = false;
            while (!atras) {
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

                String op = scanner.nextLine().trim();
                switch (op) {
                    case "1" -> System.out.println("[Alta de " + entidad + " -> llamará al Service]");
                    case "2" -> System.out.println("[Baja lógica de " + entidad + " -> llamará al Service]");
                    case "3" -> System.out.println("[Modificación de " + entidad + " -> llamará al Service]");
                    case "4" -> System.out.println("[Listado activos de " + entidad + " -> llamará al Service]");
                    case "5" -> System.out.println("[Consulta de " + entidad + " -> llamará al Service]");
                    case "6" -> System.out.println("[Listado dados de baja de " + entidad + " -> llamará al Service]");
                    case "7" -> System.out.println("[Reactivar " + entidad + " -> llamará al Service]");
                    case "0" -> atras = true;
                    default -> System.out.println("Opción incorrecta.");
                }
            }
        }
    }

