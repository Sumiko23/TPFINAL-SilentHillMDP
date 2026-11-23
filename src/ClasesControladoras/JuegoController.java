package ClasesControladoras;

import ClasesPOJO.Enemigos.Enemigo;
import ClasesPOJO.Enemigos.EnemigoComun;
import ClasesPOJO.Enemigos.JefeFinal;
import ClasesPOJO.Items.ArmaDeFuego;
import ClasesPOJO.Items.Item;
import ClasesPOJO.Items.TipoItem;
import ClasesPOJO.Jugador;
import ClasesService.EnemigoService;
import ClasesService.ItemService;
import ClasesService.JugadorService;
import Excepciones.OpcionIncorrectaException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class JuegoController {

    private final Jugador jugador;
    private final Scanner scanner = new Scanner(System.in);
    private boolean salirAlMenu = false;
    private JugadorService jugadorService;
     private ItemService itemService ;
     private EnemigoService enemigoService ;

    public JuegoController(Jugador jugador) {
        this.jugador = jugador;
        inicializarServicios();
    }

    private void inicializarServicios() {
        try {
            this.jugadorService = new JugadorService();
        } catch (SQLException e) {
            System.out.println("Error al inicializar JugadorService en la base de datos : " + e.getMessage());
            this.salirAlMenu = true;
        }

        try {
            this.itemService = new ItemService();
        } catch (SQLException e) {
            System.out.println("Error al inicializar ItemService en la base de datos : " + e.getMessage());
            this.salirAlMenu = true;
        }

        try {
            this.enemigoService = new EnemigoService();
        } catch (SQLException e) {
            System.out.println("Error al inicializar EnemigoService en la base de datos : " + e.getMessage());
            this.salirAlMenu = true;
        }
    }

    private Item obtenerItem(String nombre) {
        if (nombre == null || itemService == null) {
            return null;
        }
        try {
            return itemService.buscarPorNombreBD(nombre);
        } catch (SQLException e) {
            System.out.println("Error al buscar ítem en la base de datos  " + e.getMessage());
            return null;
        }
    }

    private Enemigo obtenerEnemigo(String nombre) {
        if (nombre == null || enemigoService == null) {
            return null;
        }
        try {
            return enemigoService.buscarPorNombreBD(nombre);
        } catch (SQLException e) {
            System.out.println("Error al buscar enemigo enla base de datos "+ e.getMessage());
            return null;
        }
    }


    private void separarPantalla() {
        System.out.println("\n------------------------------------------------------------------------------\n");
    }

    public void iniciarJuego() {
        while (!salirAlMenu && jugador.estaVivo()) {
            String escenaActual = jugador.getEscenaActual();

            switch (escenaActual) {
                case "Departamento" -> escenaDepartamento();
                case "Celular" -> escenaCelular();
                case "Revisar" -> escenaRevisar();
                case "Muebles" -> escenaMuebles();
                case "Puerta" -> escenaPuerta();
                case "Baño" -> escenaBanio();
                case "Agujero" -> escenaAgujero();
                case "Muelle" -> escenaMuelle();
                case "Lancha" -> escenaLancha();
                case "Reja" -> escenaReja();
                case "Posta Policial" -> escenaPostaPolicial();
                case "Mirar Alrededor" -> escenaMirarAlrededor();
                case "Heladera" -> escenaHeladera();
                case "Meter la mano" -> escenaMeterLaMano();
                case "Puerto" -> escenaPuerto();
                case "Cofre" -> escenaCofre();
                case "Abrir cofre" -> escenaAbrirCofre();
                case "Barco" -> escenaBarco();
                case "Seguirla" -> escenaSeguirla();
                case "Prefectura"         -> escenaPrefectura();
                case "Sala Prefectura"    -> escenaSalaPrefectura();
                case "Caja fuerte"        -> escenaCajaFuerte();
                case "Policías Prefectura"-> escenaPoliciasPrefectura();
                case "Calle"              -> escenaCalle();
                case "Universidad"  -> escenaUniversidad();
                case "Buffet"       -> escenaBuffet();
                case "Pool"         -> escenaPool();
                case "Ascensor"     -> escenaAscensor();
                case "BañoUTN"      -> escenaBanioUTN();
                case "Segundo piso" -> escenaSegundoPiso();
                case "SAE"          -> escenaSAE();
                case "Escaleras"    -> escenaEscaleras();
                case "Patio"        -> escenaPatio();
                case "Final"        -> escenaFinal();

                default -> {
                    System.out.println("Escena no encontrada: " + escenaActual);
                    salirAlMenu = true;
                }
            }

            if (!salirAlMenu && !jugador.estaVivo()) {
                manejarMuerte();
            }
        }
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DE INTERACCIÓN, INVENTARIO Y COMBATE
    // =========================================================================

    /**
     * Valida que el usuario ingrese únicamente 1 a maxOpciones, 'i' (inventario) o '0' (guardar y salir).
     */
    private String leerOpcionValida(int maxOpciones) {
        while (true) {
            System.out.println("\n[i] Abrir inventario | [0] Guardar partida y volver al menú principal");
            System.out.print("Elegí una opción: ");
            String entrada = scanner.nextLine().trim();

            try {
                if ("0".equals(entrada) || "i".equalsIgnoreCase(entrada)) {
                    return entrada.toLowerCase();
                }

                boolean valida = false;
                for (int i = 1; i <= maxOpciones; i++) {
                    if (String.valueOf(i).equals(entrada)) {
                        valida = true;
                        break;
                    }
                }

                if (!valida) {
                    throw new OpcionIncorrectaException();
                }
                return entrada;
            } catch (OpcionIncorrectaException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Procesa las opciones globales 'i' (inventario) y '0' (guardar y salir).
     */
    private boolean manejarOpcionGlobal(String opcion) {
        if ("i".equals(opcion)) {
            abrirInventario();
            return true;
        }
        if ("0".equals(opcion)) {
            // Al conectar SQL descomentamos:
            // jugadorService.guardarPartida(jugador);
            System.out.println("Partida guardada correctamente. Volviendo al menú principal...");
            salirAlMenu = true;
            return true;
        }
        return false;
    }

    private void abrirInventario() {
        separarPantalla();
        System.out.println("=== INVENTARIO ===");
        System.out.println("Salud: " + jugador.getSalud() + "/100");

        if (jugador.getArmaEquipada() != null) {
            if (jugador.getArmaEquipada() instanceof ArmaDeFuego armaFuego) {
                System.out.println("Arma equipada: " + armaFuego.getNombre()
                        + " | Balas en cargador: " + armaFuego.getBalasCargador() + "/" + armaFuego.getCapacidadCargador());
            } else {
                System.out.println("Arma equipada: " + jugador.getArmaEquipada().getNombre());
            }
        } else {
            System.out.println("Arma equipada: Ninguna (daño base 10)");
        }

        if (jugador.getInventario().getItems().isEmpty()) {
            System.out.println("No tenés ítems en el inventario.");
            return;
        }

        int i = 1;
        for (Item item : jugador.getInventario().getItems()) {
            System.out.println(i + " - " + item.getNombre());
            i++;
        }

        System.out.print("\nIngresá el número del ítem para usar/equipar/leer (o Enter para volver): ");
        String entrada = scanner.nextLine().trim();
        if (entrada.isEmpty()) {
            return;
        }
        while (!entrada.isEmpty()) {
            try {
                int indice = Integer.parseInt(entrada);
                if (indice >= 1 && indice <= jugador.getInventario().getItems().size()) {
                    Item elegido = jugador.getInventario().getItems().get(indice - 1);
                    usarItemDelInventario(elegido);
                } else {
                    System.out.println("Número de ítem inválido.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida.");
            }
            // Después de usar un ítem, actualiza el inventario y vuelve a preguntar
            separarPantalla();
            System.out.println("=== INVENTARIO ===");
            System.out.println("Salud: " + jugador.getSalud() + "/100");
            if (jugador.getArmaEquipada() != null) {
                if (jugador.getArmaEquipada() instanceof ArmaDeFuego armaFuego) {
                    System.out.println("Arma equipada: " + armaFuego.getNombre()
                            + " | Balas en cargador: " + armaFuego.getBalasCargador() + "/" + armaFuego.getCapacidadCargador());
                } else {
                    System.out.println("Arma equipada: " + jugador.getArmaEquipada().getNombre());
                }
            } else {
                System.out.println("Arma equipada: Ninguna (daño base 10)");
            }

            if (jugador.getInventario().getItems().isEmpty()) {
                System.out.println("No tenés más ítems.");
                return;
            }

            int j = 1;
            for (Item item : jugador.getInventario().getItems()) {
                System.out.println(j + " - " + item.getNombre());
                j++;
            }
            System.out.print("\n¿Querés usar/equipar/leer otro ítem? Ingresá su número (o Enter para volver): ");
            entrada = scanner.nextLine().trim();

        }
    }

    private void usarItemDelInventario(Item item) {
        switch (item.getTipo()) {
            case CONSUMIBLE -> {
                if (item.getEfectoCuracion() > 0) {
                    jugador.curar(item.getEfectoCuracion());
                    jugador.getInventario().removerItem(item);
                    System.out.println("Usaste " + item.getNombre() + " (+" + item.getEfectoCuracion() + " de salud).");
                } else if (item.getNombre().equalsIgnoreCase("Caja de balas")) {
                    if (jugador.getArmaEquipada() instanceof ArmaDeFuego armaFuego) {
                        armaFuego.setCajasBalas(armaFuego.getCajasBalas() + 1);
                        if (armaFuego.recargar()) {
                            jugador.setBalasCargador(armaFuego.getBalasCargador());
                            jugador.setCajasBalas(armaFuego.getCajasBalas());
                            jugador.getInventario().removerItem(item);
                        }
                    } else {
                        System.out.println("Tenés que equipar la Beretta 92 antes de recargar.");
                    }
                }
            }
            case ARMA, ARMA_FUEGO -> {
                jugador.setArmaEquipada(item);
                System.out.println("Equipaste: " + item.getNombre());
            }
            case NOTA -> {
                System.out.println("\n--- " + item.getNombre().toUpperCase() + " ---");
                System.out.println(item.getDescripcion());
            }
            case LLAVE_MISION -> {
                if (item.getNombre().equalsIgnoreCase("Bola de billar")
                        && jugador.getEscenaActual().equals("Pool")) {
                    System.out.println("Metés la bola de billar en uno de los agujeros de la mesa.");
                    System.out.println("Se escucha un mecanismo girando y, por la abertura de abajo, cae una llave con una etiqueta: \"Ascensor\".");
                    jugador.getInventario().removerItem(item);
                    jugador.registrarNuevoItem(obtenerItem("Llave del ascensor"));
                    System.out.println("Nuevo ítem: Llave del ascensor.");
                } else if (item.getNombre().equalsIgnoreCase("Llave de la SAE")
                            && jugador.getEscenaActual().equals("SAE")) {
                    System.out.println("Usás la llave de la SAE y la puerta se abre.");
                    jugador.setSaePuertaAbierta(true);
                    jugador.setEscenaActual("SAE"); // la escena se re-ejecuta con maniquiMuerto=false y llave en mano
                } else {
                    System.out.println(item.getNombre() + ": " + item.getDescripcion());
                }
            }
        }
    }

    /**
     * Combate reiterativo:
     * - No muestra cuánta vida le queda al monstruo ni al jugador.
     * - Si el jugador necesita darle más de dos golpes, el monstruo contraataca.
     */
    private boolean ejecutarCombate(Enemigo enemigo, boolean permiteCorrer) {
        enemigo.reiniciarSalud();
        int cantidadGolpes = 0;

        while (enemigo.estaVivo() && jugador.estaVivo()) {
            System.out.println("\nOpción 1: Atacar");
            if (permiteCorrer) {
                System.out.println("Opción 2: Correr");
            }

            String op = leerOpcionValida(permiteCorrer ? 2 : 1);
            if (manejarOpcionGlobal(op)) {
                if (salirAlMenu) return false;
                continue;
            }

            if ("1".equals(op)) {
                jugador.atacar(enemigo);
                cantidadGolpes++;

                if (enemigo.estaVivo()) {
                    System.out.println("Golpeás a " + enemigo.getNombre() + ", pero sigue en pie...");
                    if (cantidadGolpes >= 2) {
                        enemigo.atacar(jugador);
                        System.out.println("¡El monstruo aprovecha y te hiere!");
                    }
                }
            } else if ("2".equals(op) && permiteCorrer) {
                return false;
            }
        }

        if (!enemigo.estaVivo()) {
            jugador.registrarEnemigoMuerto();
            return true;
        }
        return false;
    }

    private void manejarMuerte() {
        separarPantalla();
        System.out.println("Has muerto...");
        System.out.println("1 - Continuar desde la última partida guardada");
        System.out.println("0 - Salir al menú principal");
        System.out.print("Elegí una opción: ");
        String op = scanner.nextLine().trim();

        if ("1".equals(op)) {
            // Al conectar SQL, acá recargaríamos el último estado guardado desde la BD:
            // jugadorService.recargarUltimoGuardado(jugador);
            jugador.setSalud(100); //ELIMINAR DESPUÉS
            System.out.println("Retomando partida en: " + jugador.getEscenaActual());
        } else {
            salirAlMenu = true;
        }
    }

    // =========================================================================
    // BLOQUE 1 DE ESCENAS: DEPARTAMENTO
    // =========================================================================

    private void escenaDepartamento() {
        separarPantalla();

        if (!jugador.isDeptoVisitado()) {
            jugador.registrarNuevoItem(obtenerItem("Celular"));

            System.out.println("Te despertás en una habitación vieja, en un departamento en la costa.");
            System.out.println("Podés ver el mar desde la ventana.");
            System.out.println("No te acordás cómo llegaste...");
            System.out.println("*¿Dónde estoy?... Mi cabeza...*");
            jugador.setDeptoVisitado(true);
        } else {
            System.out.println("Seguís en la habitación del departamento. Desde la ventana se escucha el mar.");
        }

        System.out.println("\nOpción 1: Revisar departamento");
        System.out.println("Opción 2: Revisar celular");
        System.out.println("Opción 3: Intentar salir por la puerta");

        String op = leerOpcionValida(3);
        if (manejarOpcionGlobal(op)) return;

        switch (op) {
            case "1" -> jugador.setEscenaActual("Revisar");
            case "2" -> jugador.setEscenaActual("Celular");
            case "3" -> jugador.setEscenaActual("Puerta");
        }
    }

    // -------------------------------------------------------------------------

    private void escenaCelular() {
        separarPantalla();
        System.out.println("Tu celular no prende.");
        System.out.println("\nOpción 1: Volver");

        String op = leerOpcionValida(1);
        if (manejarOpcionGlobal(op)) return;

        jugador.setEscenaActual("Departamento");
    }

    // -------------------------------------------------------------------------

    private void escenaRevisar() {
        separarPantalla();
        boolean tieneLinterna = jugador.getInventario().tieneItem("Linterna");

        if (!tieneLinterna) {
            System.out.println("Hay un agujero grande en la pared, como un túnel.");
            System.out.println("Se escucha el ruido del mar...");
            System.out.println("\nOpción 1: Revisar muebles");
            System.out.println("Opción 2: Entrar al agujero");
            System.out.println("Opción 3: Volver");

            String op = leerOpcionValida(3);
            if (manejarOpcionGlobal(op)) return;

            switch (op) {
                case "1" -> jugador.setEscenaActual("Muebles");
                case "2" -> jugador.setEscenaActual("Agujero");
                case "3" -> jugador.setEscenaActual("Departamento");
            }
        } else {
            System.out.println("*No hay nada más que pueda servirme o funcione a simple vista...*");
            System.out.println("\nOpción 1: Entrar al agujero");
            System.out.println("Opción 2: Intentar salir por la puerta");
            System.out.println("Opción 3: Volver");

            String op = leerOpcionValida(3);
            if (manejarOpcionGlobal(op)) return;

            switch (op) {
                case "1" -> jugador.setEscenaActual("Agujero");
                case "2" -> jugador.setEscenaActual("Puerta");
                case "3" -> jugador.setEscenaActual("Departamento");
            }
        }
    }

    // -------------------------------------------------------------------------

    private void escenaMuebles() {
        separarPantalla();
        if (!jugador.getInventario().tieneItem("Linterna")) {
            System.out.println("Encontraste una linterna y unas pastillas energizantes en uno de los muebles.");
            jugador.registrarNuevoItem(obtenerItem("Linterna"));
            jugador.registrarNuevoItem(obtenerItem("Pastillas energizantes"));
            System.out.println("Nuevos ítems: Linterna y Pastillas energizantes.");
        } else {
            System.out.println("Ya revisaste los muebles. No queda nada más.");
        }
        System.out.println("\nOpción 1: Seguir revisando departamento");
        String op = leerOpcionValida(1);
        if (manejarOpcionGlobal(op)) return;

        jugador.setEscenaActual("Revisar");
    }

    // -------------------------------------------------------------------------

    private void escenaPuerta() {
        separarPantalla();
        if (!jugador.getInventario().tieneItem("Cuchillo")) {
            System.out.println("La puerta está trabada, no abre de ninguna manera.");
            System.out.println("Pero hay un cuchillo clavado en ella...");

            jugador.registrarNuevoItem(obtenerItem("Cuchillo"));
            System.out.println("Nuevo ítem: Cuchillo.");
        }
        if (!jugador.isBanioVisitado()) {

            System.out.println("Se escuchan ruidos que vienen desde el baño...");
            System.out.println("\nOpción 1: Ver qué hay en el baño");
            System.out.println("Opción 2: Seguir revisando departamento");

            String op = leerOpcionValida(2);
            if (manejarOpcionGlobal(op)) return;

            if ("1".equals(op)) {
                jugador.setEscenaActual("Baño");
            } else {
                jugador.setEscenaActual("Revisar");
            }
        } else {
            System.out.println("No hay forma de salir...");
            System.out.println("\nOpción 1: Seguir revisando departamento");

            String op = leerOpcionValida(1);
            if (manejarOpcionGlobal(op)) return;

            jugador.setEscenaActual("Revisar");
        }
    }

    // -------------------------------------------------------------------------

    private void escenaBanio() {
        separarPantalla();

        if (!jugador.isBanioVisitado()) {

            System.out.println("Abrís la puerta del baño y una figura inhumana empieza a acercarse a vos rápidamente.");

            Enemigo cloaca = obtenerEnemigo("Monstruo de la cloaca");
            boolean loMato = ejecutarCombate(cloaca, true);

            if (salirAlMenu || !jugador.estaVivo()) return;
            jugador.setBanioVisitado(true);
            if (loMato) {
                System.out.println("El monstruo ha muerto...");
                System.out.println("*¡¿QUÉ FUE ESO?!*");
                System.out.println("Alcanzás a ver una bebida saludable en un estante del baño.");
                jugador.setCloacaMuerto(true);
                jugador.registrarNuevoItem(obtenerItem("Bebida saludable"));
                System.out.println("Nuevo ítem: Bebida saludable.");
            } else {
                System.out.println("Intentás escapar pero el monstruo te ataca por la espalda antes de que logres cerrar la puerta... (-10 de salud)");
                jugador.recibirDanio(10);
                if (!jugador.estaVivo()) return;
            }
        } else {
            System.out.println("*Ya salí del baño. No pienso intentar entrar de nuevo...*");
        }

        System.out.println("\nOpción 1: Volver a revisar el departamento");
        String op = leerOpcionValida(1);
        if (manejarOpcionGlobal(op)) return;

        jugador.setEscenaActual("Departamento");
    }

    // -------------------------------------------------------------------------

    private void escenaAgujero() {
        separarPantalla();
        if (!jugador.getInventario().tieneItem("Linterna")) {
            System.out.println("*Está muy oscuro, ¡no voy a ver nada!*");
            System.out.println("\nOpción 1: Seguir revisando el departamento");

            String op = leerOpcionValida(1);
            if (manejarOpcionGlobal(op)) return;

            jugador.setEscenaActual("Revisar");
        } else {
            System.out.println("Con la linterna te metés por el agujero. El túnel baja hacia la costa y el ruido del mar se intensifica...");
            System.out.println("\nOpción 1: Seguir avanzando");

            String op = leerOpcionValida(1);
            if (manejarOpcionGlobal(op)) return;

            jugador.setEscenaActual("Muelle");
        }
    }

    // =========================================================================
    // BLOQUE 2 DE ESCENAS: MUELLE
    // =========================================================================

    private void escenaMuelle() {
        separarPantalla();
        boolean tieneLlave = jugador.getInventario().tieneItem("Llave de la lancha");

        if (!tieneLlave) {

            System.out.println("Llegaste al muelle. La ciudad está tapada de una niebla espesa...");
            System.out.println("Hay una lancha atada al final...");
            System.out.println("\nOpción 1: Revisar lancha");
            System.out.println("Opción 2: Revisar posta policial");
            System.out.println("Opción 3: Salir a la calle");

            String op = leerOpcionValida(3);
            if (manejarOpcionGlobal(op)) return;

            switch (op) {
                case "1" -> jugador.setEscenaActual("Lancha");
                case "2" -> jugador.setEscenaActual("Posta Policial");
                case "3" -> jugador.setEscenaActual("Reja");
            }

        } else if (!jugador.isPolicia2Aparecio()) {

            System.out.println("Volvés al muelle y ves que otro policía muerto empieza a correr hacia vos.");
            System.out.println("*¿De dónde salió?!*");

            Enemigo policia2 = obtenerEnemigo("Policía muerto 2");
            boolean loMato = ejecutarCombate(policia2, true);

            if (salirAlMenu || !jugador.estaVivo()) return;
            jugador.setPolicia2Aparecio(true);

            if (loMato) {
                System.out.println("El policía cae al piso y aprovechás para terminarlo...");
                jugador.setPolicia2Muerto(true);

            } else {
                System.out.println("Corrés con todas tus fuerzas y lográs escapar...");
            }

            System.out.println("\nOpción 1: Ir a la lancha");

            String op = leerOpcionValida(1);
            if (manejarOpcionGlobal(op)) return;

            jugador.setEscenaActual("Lancha");

        } else {

            System.out.println("El muelle sigue envuelto en una niebla espesa.");
            System.out.println("\nOpción 1: Revisar lancha");
            System.out.println("Opción 2: Revisar posta policial");
            System.out.println("Opción 3: Salir a la calle");

            String op = leerOpcionValida(3);
            if (manejarOpcionGlobal(op)) return;

            switch (op) {
                case "1" -> jugador.setEscenaActual("Lancha");
                case "2" -> jugador.setEscenaActual("Posta Policial");
                case "3" -> jugador.setEscenaActual("Reja");
            }
        }
    }

    // -------------------------------------------------------------------------

    private void escenaLancha() {
        separarPantalla();
        if (!jugador.getInventario().tieneItem("Llave de la lancha")) {
            System.out.println("La lancha no arranca...");
            System.out.println("*Necesito la llave del motor.*");
            System.out.println("\nOpción 1: Volver al muelle");

            String op = leerOpcionValida(1);
            if (manejarOpcionGlobal(op)) return;

            jugador.setEscenaActual("Muelle");
        } else {
            System.out.println("Usás la llave... (ruido de motor)");
            System.out.println("El motor funciona...");
            System.out.println("\nOpción 1: Ir al puerto");

            String op = leerOpcionValida(1);
            if (manejarOpcionGlobal(op)) return;

            jugador.setEscenaActual("Puerto");
        }
    }

    // -------------------------------------------------------------------------

    private void escenaReja() {
        separarPantalla();
        if (!jugador.getInventario().tieneItem("Barra de hierro")) {
            System.out.println("La reja está cerrada con cadenas y varios candados. No hay forma de salir...");
            System.out.println("Pero encontrás una barra de hierro.");
            jugador.registrarNuevoItem(obtenerItem("Barra de hierro"));
            System.out.println("Nuevo ítem: Barra de hierro.");
        } else {
            System.out.println("La reja sigue cerrada con cadenas y candados. No hay forma de salir.");
        }

        System.out.println("\nOpción 1: Volver al muelle");

        String op = leerOpcionValida(1);
        if (manejarOpcionGlobal(op)) return;

        jugador.setEscenaActual("Muelle");
    }

    // -------------------------------------------------------------------------

    private void escenaPostaPolicial() {
        separarPantalla();
        boolean tieneRadio = jugador.getInventario().tieneItem("Radio");
        boolean tieneLlave = jugador.getInventario().tieneItem("Llave de la lancha");

        if (!jugador.isPolicia1Muerto()) {

            System.out.println("Abrís la puerta de la garita policial abandonada y un fuerte hedor te pega en la cara.");
            System.out.println("Alumbrás con la linterna y ves algo moviéndose en las sombras...");
            System.out.println("Antes de poder reaccionar o defenderte, un policía muerto te ataca y te lanza contra el otro extremo de la habitación! (-20 de vida)");
            jugador.recibirDanio(20);
            System.out.println("» Salud actual: " + jugador.getSalud() + "/100");

            if (!jugador.estaVivo()) return;

            Enemigo policia1 = obtenerEnemigo("Policía muerto 1");
            boolean loMato = ejecutarCombate(policia1, false);

            if (salirAlMenu || !jugador.estaVivo()) return;

            // Con permiteCorrer=false, ejecutarCombate solo sale si el enemigo muere
            System.out.println("El policía cae al piso y no se mueve más.");
            jugador.setPolicia1Muerto(true);

            // Vuelve a ejecutar la misma escena para mostrar el menú post-combate
            jugador.setEscenaActual("Posta Policial");

            //Si todavía no tiene ni la llave ni la radio
        } else if (!tieneRadio && !tieneLlave) {
            System.out.println("El lugar se ve bastante abandonado y huele horrible.");
            System.out.println("Hay una heladera con una nota pegada...");
            System.out.println("\nOpción 1: Echar un vistazo alrededor");
            System.out.println("Opción 2: " + textoHeladera());
            System.out.println("Opción 3: Volver al muelle");

            String op = leerOpcionValida(3);
            if (manejarOpcionGlobal(op)) return;

            switch (op) {
                case "1" -> jugador.setEscenaActual("Mirar Alrededor");
                case "2" -> jugador.setEscenaActual("Heladera");
                case "3" -> jugador.setEscenaActual("Muelle");
            }

        } else if (!tieneRadio) {
            // Tiene llave pero no la radio
            System.out.println("El lugar se ve bastante abandonado y huele horrible.");
            System.out.println("\nOpción 1: Echar un vistazo alrededor");
            System.out.println("Opción 2: Volver al muelle");

            String op = leerOpcionValida(2);
            if (manejarOpcionGlobal(op)) return;

            switch (op) {
                case "1" -> jugador.setEscenaActual("Mirar Alrededor");
                case "2" -> jugador.setEscenaActual("Muelle");
            }

        } else if (!tieneLlave) {
            // Tiene la radio pero no la llave
            System.out.println("El lugar se ve bastante abandonado y huele horrible.");
            System.out.println("Hay una heladera con una nota pegada...");
            System.out.println("\nOpción 1: " + textoHeladera());
            System.out.println("Opción 2: Volver al muelle");

            String op = leerOpcionValida(2);
            if (manejarOpcionGlobal(op)) return;

            switch (op) {
                case "1" -> jugador.setEscenaActual("Heladera");
                case "2" -> jugador.setEscenaActual("Muelle");
            }

        } else {
            System.out.println("*Ya no hay más nada que revisar... Debería apurarme para llegar al puerto, alguien necesita ayuda...*");
            System.out.println("\nOpción 1: Ir al muelle");

            String op = leerOpcionValida(1);
            if (manejarOpcionGlobal(op)) return;

            jugador.setEscenaActual("Muelle");
        }
    }

    /**
     * Texto dinámico del link a Heladera según si ya se leyó la nota.
     */
    private String textoHeladera() {
        return jugador.getInventario().tieneItem("Nota heladera")
                ? "Abrir heladera"
                : "Leer nota de la heladera";
    }

    // -------------------------------------------------------------------------

    private void escenaMirarAlrededor() {
        separarPantalla();
        if (!jugador.getInventario().tieneItem("Radio")) {
            System.out.println("Encontrás una radio vieja sobre una mesa. De golpe se enciende sola y se escucha entre ruido de estática:");
            System.out.println("*-¿Hay alguien ahí? Por favor, ¡ayudame!... Tenés que venir al puerto...*");
            System.out.println("*-¿Hola? ¿Quién está ahí? ¡¿Hola?!*");
            System.out.println("No responde más... *¿Qué fue eso?*");
            jugador.registrarNuevoItem(obtenerItem("Radio"));
            System.out.println("Nuevo ítem: Radio.");
        } else {
            System.out.println("No hay nada más a la vista. Sólo un espantoso hedor.");
        }

        if (!jugador.getInventario().tieneItem("Llave de la lancha")) {
            System.out.println("\nOpción 1: " + textoHeladera());
            System.out.println("Opción 2: Volver al muelle");

            String op = leerOpcionValida(2);
            if (manejarOpcionGlobal(op)) return;

            switch (op) {
                case "1" -> jugador.setEscenaActual("Heladera");
                case "2" -> jugador.setEscenaActual("Muelle");
            }
        } else {
            System.out.println("\nOpción 1: Volver al muelle");

            String op = leerOpcionValida(1);
            if (manejarOpcionGlobal(op)) return;

            jugador.setEscenaActual("Muelle");
        }
    }

    // -------------------------------------------------------------------------

    private void escenaHeladera() {
        separarPantalla();
        if (!jugador.getInventario().tieneItem("Nota heladera")) {
            System.out.println("La nota tiene manchas de sangre...");
            System.out.println("\"La llave del viaje que debés emprender,");
            System.out.println("un cuerpo que flota no puede encender.");
            System.out.println("El frío de este metal ya no congela al tiempo,");
            System.out.println("pero duerme en su estómago un legado de hierro.\"");
            jugador.registrarNuevoItem(obtenerItem("Nota heladera"));
            System.out.println("Nuevo ítem: Nota heladera.");
        }

        if (!jugador.getInventario().tieneItem("Llave de la lancha")) {
            System.out.println("Hay una bolsa llena de sangre con algún tipo de carne que huele terrible. Muchas moscas y larvas salen de ella...");
            System.out.println("\nOpción 1: Meter la mano");
            System.out.println("Opción 2: Volver a la posta");

            String op = leerOpcionValida(2);
            if (manejarOpcionGlobal(op)) return;

            switch (op) {
                case "1" -> jugador.setEscenaActual("Meter la mano");
                case "2" -> jugador.setEscenaActual("Posta Policial");
            }
        } else {
            System.out.println("El cadáver embolsado sigue ahí, pero ya le quitaste la llave. No hay razón para volver a tocar eso.");
            System.out.println("\nOpción 1: Volver al muelle");

            String op = leerOpcionValida(1);
            if (manejarOpcionGlobal(op)) return;

            jugador.setEscenaActual("Muelle");
        }
    }

    // -------------------------------------------------------------------------

    private void escenaMeterLaMano() {
        separarPantalla();
        System.out.println("Metés la mano dentro de la bolsa, sintiendo una viscosidad repugnante...");
        System.out.println("¡Ahí está! Sacás una llave cubierta de sangre.");
        jugador.registrarNuevoItem(obtenerItem("Llave de la lancha"));
        System.out.println("Nuevo ítem: Llave de la lancha.");

        System.out.println("\nOpción 1: Seguir revisando");
        System.out.println("Opción 2: Volver al muelle");

        String op = leerOpcionValida(2);
        if (manejarOpcionGlobal(op)) return;

        switch (op) {
            case "1" -> jugador.setEscenaActual("Mirar Alrededor");
            case "2" -> jugador.setEscenaActual("Muelle");
        }
    }
    //========================================================================
    // BLOQUE 3 DE ESCENAS: PUERTO
    // =======================================================================

    private void escenaPuerto() {
        separarPantalla();
        boolean tieneCuchillo = jugador.getInventario().tieneItem("Cuchillo de pescador");
        boolean barcoVisitado = jugador.isBarcoVisitado();
        boolean tieneBotiquin = jugador.getInventario().tieneItem("Botiquín de primeros auxilios");
        boolean notaCofre = jugador.getInventario().tieneItem("Nota cofre");

        // Texto de llegada según estado
        if (!tieneCuchillo && !barcoVisitado) {
            System.out.println("Llegaste al puerto.");
            System.out.println("El olor a pescado podrido es espantoso...");
            System.out.println("Escuchás ruidos que vienen de un barco...");
        } else if (tieneCuchillo && tieneBotiquin) {

            System.out.println("*Ya no tengo nada que hacer acá...*");
            System.out.println("\nOpción 1: Seguir a la niña");
            String op = leerOpcionValida(1);
            if (manejarOpcionGlobal(op)) return;
            jugador.setEscenaActual("Seguirla");
            return;

        } else {
            // Cualquier estado intermedio: solo el olor
            System.out.println("El olor a pescado podrido es espantoso...");
        }

        // Opciones dinámicas según qué falta conseguir
        // Siempre se muestra cofre si falta el cuchillo, barco si no fue visitado o falta botiquín
        boolean mostrarCofre = !tieneCuchillo;
        boolean mostrarBarco = !barcoVisitado || !tieneBotiquin;
        boolean mostrarNina = barcoVisitado && (!tieneCuchillo || tieneBotiquin);

        int idx = 1;
        if (mostrarCofre) {
            System.out.println("Opción " + idx + ": " + (notaCofre ? "Volver al cofre" : "Investigar alrededor"));
            idx++;
        }
        if (mostrarBarco) {
            System.out.println("Opción " + idx + ": " + (barcoVisitado ? "Volver al barco" : "Investigar barco"));
            idx++;
        }
        if (mostrarNina) {
            System.out.println("Opción " + idx + ": Seguir a la niña");
            idx++;
        }

        String op = leerOpcionValida(idx - 1);
        if (manejarOpcionGlobal(op)) return;

        // Mapear número elegido a destino
        int cursor = 1;
        if (mostrarCofre) {
            if (op.equals(String.valueOf(cursor))) {
                jugador.setEscenaActual("Cofre");
                return;
            }
            cursor++;
        }
        if (mostrarBarco) {
            if (op.equals(String.valueOf(cursor))) {
                jugador.setEscenaActual("Barco");
                return;
            }
            cursor++;
        }
        if (mostrarNina) {
            if (op.equals(String.valueOf(cursor))) {
                jugador.setEscenaActual("Seguirla");
                return;
            }
        }
    }

    // -------------------------------------------------------------------------

    private void escenaCofre() {
        separarPantalla();
        if (!jugador.getInventario().tieneItem("Nota cofre")) {
            System.out.println("Encontraste un cofre de madera vieja.");
            System.out.println("Está cerrado con un candado de combinación de 4 dígitos.");
            System.out.println("Al lado, pegada con algo que parece grasa de pescado seca, hay una nota amarillenta:");
            System.out.println("\"La niebla entró desde la escollera y se lo tragó todo.");
            System.out.println("Ya no hay lobos marinos, ya no hay turistas,");
            System.out.println("sólo el sonido del metal oxidado en el agua...");
            System.out.println("Si del puerto querés escapar,");
            System.out.println("contá el largo de los que te voy a nombrar:");
            System.out.println("Unas se usaron para sacar los cuerpos del agua como si fueran peces");
            System.out.println("Los segundos nadaron hacia el sur para no volver");
            System.out.println("El tercero se apagó para siempre dejando al mar en completa oscuridad");
            System.out.println("Y el cuarto tragó con vida a quien quisiera escapar...\"");
            jugador.registrarNuevoItem(obtenerItem("Nota cofre"));
            System.out.println("Nuevo ítem: Nota cofre.");
        }

        System.out.println("Es un cofre antiguo... me pregunto qué tendrá dentro.");
        System.out.println("\nOpción 1: Insertar código");
        System.out.println("Opción 2: Volver al puerto");

        String op = leerOpcionValida(2);
        if (manejarOpcionGlobal(op)) return;

        if ("1".equals(op)) {
            System.out.print("Ingresá el código de 4 dígitos del candado: ");
            String codigo = scanner.nextLine().trim();

            if ("5543".equals(codigo)) {
                System.out.println("El candado se ha abierto.");
                jugador.setEscenaActual("Abrir cofre");
            } else {
                System.out.println("El código es incorrecto...");
                System.out.println("\nOpción 1: Intentar de nuevo");
                System.out.println("Opción 2: Volver al puerto");

                String op2 = leerOpcionValida(2);
                if (manejarOpcionGlobal(op2)) return;

                jugador.setEscenaActual("1".equals(op2) ? "Cofre" : "Puerto");
            }
        } else {
            jugador.setEscenaActual("Puerto");
        }
    }

    // -------------------------------------------------------------------------

    private void escenaAbrirCofre() {
        separarPantalla();
        if (!jugador.isAbrirCofreOcurrido()) {
            System.out.println("Adentro del cofre hay un cuchillo de pescador extremadamente afilado y una llave con la inscripción \"SAE - UTN\".");
            jugador.registrarNuevoItem(obtenerItem("Cuchillo de pescador"));
            jugador.registrarNuevoItem(obtenerItem("Llave de la SAE"));
            System.out.println("Nuevos ítems: Cuchillo de pescador y Llave de la SAE.");

            System.out.println("Apenas recogés las cosas, notás que el agua empieza a moverse formando un remolino...");
            System.out.println("Un monstruo parecido a un lobo marino sale del agua intentando aplastarte...");

            Enemigo acuatico = obtenerEnemigo("Monstruo acuático");
            boolean loMato = ejecutarCombate(acuatico, true);

            if (salirAlMenu || !jugador.estaVivo()) return;
            jugador.setAbrirCofreOcurrido(true);

            if (loMato) {
                System.out.println("El enemigo está muerto...");
                System.out.println("*Este monstruo huele realmente horrible... ¡Necesito salir de acá!*");
                jugador.setAcuaticoMuerto(true);
            } else {
                System.out.println("Lográs esquivar al monstruo pero, debido a la densa niebla, no ves bien, te tropezás, caés y te doblás el pie... (-30 de salud)");
                jugador.recibirDanio(30);
                if (!jugador.estaVivo()) return;
            }
        }
        System.out.println("\nOpción 1: Volver al puerto");

        String op = leerOpcionValida(1);
        if (manejarOpcionGlobal(op)) return;

        jugador.setEscenaActual("Puerto");
    }

    // -------------------------------------------------------------------------

    private void escenaBarco() {
        separarPantalla();
        if (!jugador.isBarcoVisitado()) {
            System.out.println("Ves a una niña pequeña jugando con un barco de juguete.");
            System.out.println("Al verte se asusta...");
            System.out.println("*-¡No deberías estar acá!*");
            System.out.println("¡Se fue corriendo!");
            System.out.println("Al escapar deja caer una bola de billar que rueda por el piso del barco hasta tus pies. La levantás y te la guardás.");
            jugador.setBarcoVisitado(true);
            jugador.registrarNuevoItem(obtenerItem("Bola de billar"));
            System.out.println("Nuevo ítem: Bola de billar.");
        } else {
            System.out.println("El barco está vacío. La niña ya no está.");
        }

        if (!jugador.getInventario().tieneItem("Botiquín de primeros auxilios")) {
            System.out.println("Sobre la cubierta hay un maletín cerrado.");
            System.out.println("\nOpción 1: Seguir a la niña");
            System.out.println("Opción 2: Abrir maletín");
            System.out.println("Opción 3: Volver al puerto");

            String op = leerOpcionValida(3);
            if (manejarOpcionGlobal(op)) return;

            switch (op) {
                case "1" -> jugador.setEscenaActual("Seguirla");
                case "2" -> {
                    System.out.println("Has encontrado un botiquín de primeros auxilios.");
                    jugador.registrarNuevoItem(obtenerItem("Botiquín de primeros auxilios"));
                    System.out.println("Nuevo ítem: Botiquín de primeros auxilios.");
                    System.out.println("*Probablemente debería seguir a esa niña. Es muy peligroso que ande sola.*");
                    System.out.println("\nOpción 1: Seguir a la niña");
                    System.out.println("Opción 2: Volver al puerto");

                    String op2 = leerOpcionValida(2);
                    if (manejarOpcionGlobal(op2)) return;

                    jugador.setEscenaActual("1".equals(op2) ? "Seguirla" : "Puerto");
                }
                case "3" -> jugador.setEscenaActual("Puerto");
            }
        } else {
            System.out.println("*Probablemente debería seguir a esa niña. Es muy peligroso que ande sola.*");
            System.out.println("\nOpción 1: Seguir a la niña");
            System.out.println("Opción 2: Volver al puerto");

            String op = leerOpcionValida(2);
            if (manejarOpcionGlobal(op)) return;

            jugador.setEscenaActual("1".equals(op) ? "Seguirla" : "Puerto");
        }
    }

    // -------------------------------------------------------------------------

    private void escenaSeguirla() {
        separarPantalla();

        if (!jugador.isSeguirlaOcurrida()) {
            System.out.println("Empezás a caminar pero la niebla es cada vez más espesa...");
            System.out.println("Una especie de monstruo sin brazos se acerca para atacarte...");

            System.out.println("\nOpción 1: Atacar monstruo");
            System.out.println("Opción 2: Esquivar y correr");

            String op = leerOpcionValida(2);
            if (manejarOpcionGlobal(op)) return;

            if ("1".equals(op)) {
                System.out.println("Te acercás pero del agujero de su pecho sale ácido que desintegra tu ropa...");

                System.out.println("\nOpción 1: Atacar por atrás");
                System.out.println("Opción 2: Esquivar y correr");

                String op2 = leerOpcionValida(2);
                if (manejarOpcionGlobal(op2)) return;

                if ("1".equals(op2)) {
                    Enemigo lyingFigure = obtenerEnemigo("Lying Figure 1");
                    boolean loMato = ejecutarCombate(lyingFigure, true);
                    System.out.println("Lográs hacerle daño al monstruo atacando por atrás.");
                    if (salirAlMenu || !jugador.estaVivo()) return;
                    if (loMato) {
                        System.out.println("El monstruo cayó al piso muerto...");
                        jugador.setLyingFigure1Muerto(true);
                    }
                } else {
                    System.out.println("Logras escapar pero el ácido alcanza a quemar tu ropa y tu piel... (-30 de salud)");
                    jugador.recibirDanio(30);
                    if (!jugador.estaVivo()) return;
                }
            }
            jugador.setSeguirlaOcurrida(true);
        }

        System.out.println("Logras escapar pero el ácido alcanza a quemar tu ropa..." +
                "*Estos monstruos son asquerosos... Será mejor que consiga una campera y un arma...*");
        System.out.println("\nOpción 1: Buscar abrigo y arma");

        String opFinal = leerOpcionValida(1);
        if (manejarOpcionGlobal(opFinal)) return;

        jugador.setEscenaActual("Prefectura");
    }

    // =========================================================================
    // BLOQUE 4 DE ESCENAS: PREFECTURA
    // =========================================================================

    // -------------------------------------------------------------------------

    private void escenaPrefectura() {
        separarPantalla();
        if (!jugador.isPrefecturaVisitada()) {
            System.out.println("Llegás a la Prefectura Naval. El edificio está a oscuras y la puerta principal cede con un chirrido...");
            System.out.println("Apenas cruzás el umbral, la puerta se cierra de golpe detrás tuyo y escuchás cómo la traba se asegura sola.");
            System.out.println("Golpeás la puerta pero no cede. Tenés que encontrar otra forma de salir...");
            System.out.println("Es una oficina grande y oscura. Al fondo se ve una caja fuerte empotrada en la pared y, sobre los escritorios, montones de papeles.");
            jugador.setPrefecturaVisitada(true);
        } else {
            separarPantalla();
            System.out.println("La oficina sigue en penumbras. Al fondo se ve la caja fuerte empotrada en la pared.");
        }

        System.out.println("\nOpción 1: Revisar la sala");
        System.out.println("Opción 2: Acercarse a la caja fuerte");
        System.out.println("Opción 3: Intentar salir por la puerta");

        String op = leerOpcionValida(3);
        if (manejarOpcionGlobal(op)) return;

        switch (op) {
            case "1" -> jugador.setEscenaActual("Sala Prefectura");
            case "2" -> jugador.setEscenaActual("Caja fuerte");
            case "3" -> {
                System.out.println("Tirás con todas tus fuerzas de la puerta pero no se mueve ni un centímetro.");
                System.out.println("*Tiene que haber alguna forma de abrirla. Quizás la caja fuerte tenga algo que ver...*");
                // La opción 3 no navega: vuelve a mostrar la misma escena
                jugador.setEscenaActual("Prefectura");
            }
        }
    }

    // -------------------------------------------------------------------------

    private void escenaSalaPrefectura() {
        separarPantalla();
        System.out.println("Estás en el centro de la oficina. Está tan oscuro que sólo se distinguen las siluetas de los escritorios y algunos papeles desparramados.");

        boolean tieneNota = jugador.getInventario().tieneItem("Nota Prefectura");

        if (!tieneNota) {
            // Paso 1: revisar escritorios → encuentra la nota
            System.out.println("\nOpción 1: Revisar los escritorios");
            System.out.println("Opción 2: Volver a la entrada");

            String op = leerOpcionValida(2);
            if (manejarOpcionGlobal(op)) return;

            if ("1".equals(op)) {
                separarPantalla();
                System.out.println("Entre los papeles empapados hay una nota manchada de sangre:");
                System.out.println("\"Quien llegue hasta acá ya sabe que la guardia nunca termina.");
                System.out.println("Lo que guardo es para quien sepa mirar:");
                System.out.println("Los trapos de colores que ya no flamean, dame primero su número.");
                System.out.println("Las mesas donde se escribieron los partes, que sean las segundas.");
                System.out.println("El único que todavía marca una hora que ya no existe, será el tercero.");
                System.out.println("Y los ojos tapiados que no dejan pasar la niebla serán el último.\"");
                jugador.registrarNuevoItem(obtenerItem("Nota Prefectura"));
                System.out.println("Nuevo ítem: Nota Prefectura.");

                // Paso 2 (encadenado): alumbrar la sala
                alumbrarSala();
            } else {
                jugador.setEscenaActual("Prefectura");
            }

        } else {
            // Ya tiene la nota:
            System.out.println("Ya tenés la nota. En esta sala debe haber alguna pista...");
            System.out.println("\nOpción 1: Revisar los escritorios");
            System.out.println("Opción 2: Volver a la entrada");
            String op = leerOpcionValida(2);
            if (manejarOpcionGlobal(op)) return;
            if ("1".equals(op)) {
                alumbrarSala();
            }else {
                jugador.setEscenaActual("Prefectura");
            }
        }
    }

    /** Muestra la descripción detallada de la sala al alumbrarla con la linterna. */
    private void alumbrarSala() {
        System.out.println("\nOpción 1: Alumbrar la sala con la linterna");
        System.out.println("Opción 2: Volver a la entrada");

        String op = leerOpcionValida(2);
        if (manejarOpcionGlobal(op)) return;

        if ("1".equals(op)) {
            separarPantalla();
            System.out.println("La linterna ilumina una oficina grande y desordenada, con las paredes manchadas de humedad.");
            System.out.println("Hay cuatro ventanas selladas con tablas por donde no entra ni un rayo de luz y, en la pared de enfrente, un reloj detenido a las 3:15.");
            System.out.println("Los escritorios están cubiertos de papeles empapados: son seis.");
            System.out.println("En el rincón, junto a una silla volcada, hay un mástil con dos banderas sucias, la argentina y la de la Prefectura.");
            System.out.println("De un perchero cuelga una campera de abrigo de la Prefectura. Te la ponés: es lo único cálido que encontraste desde que despertaste.");
        }

        System.out.println("\nOpción 1: Volver a la entrada");

        String opVolver = leerOpcionValida(1);
        if (manejarOpcionGlobal(opVolver)) return;

        jugador.setEscenaActual("Prefectura");
    }

    // -------------------------------------------------------------------------

    private void escenaCajaFuerte() {
        separarPantalla();

        if (!jugador.isCajaFuerteAbierta()) {
            System.out.println("Una caja fuerte de acero empotrada en la pared. Tiene un teclado numérico de 4 dígitos y una luz roja parpadeando.");

            if (!jugador.getInventario().tieneItem("Nota Prefectura")) {
                System.out.println("*Seguro hay algo en esta sala que me diga el código...*");
            }

            System.out.println("\nOpción 1: Insertar código");
            System.out.println("Opción 2: Volver a la entrada");

            String op = leerOpcionValida(2);
            if (manejarOpcionGlobal(op)) return;

            if ("1".equals(op)) {
                System.out.print("Ingresá el código de 4 dígitos de la caja fuerte: ");
                scanner.nextLine();
                String codigo = scanner.nextLine().trim();

                if ("2614".equals(codigo)) {
                    separarPantalla();
                    System.out.println("La luz se pone verde y la puerta de la caja fuerte se abre.");
                    System.out.println("Adentro hay una pistola Beretta 92 y una caja de balas.");
                    System.out.println("Sacás el cargador: tiene 15 balas.");

                    ArmaDeFuego beretta = new ArmaDeFuego(
                            16, "Beretta 92",
                            "Pistola semiautomática (45 de daño, cargador de 15 balas).",
                            45, 15, 15, 1, true
                    );
                    jugador.registrarNuevoItem(beretta);
                    jugador.registrarNuevoItem(obtenerItem("Caja de balas"));
                    System.out.println("Nuevos ítems: Beretta 92 y 1 caja de balas.");
                    System.out.println("*Ahora sí tengo con qué defenderme...*");

                    jugador.setCajaFuerteAbierta(true);

                } else {
                    System.out.println("Un pitido seco. El código es incorrecto...");
                    System.out.println("\nOpción 1: Intentar de nuevo");
                    System.out.println("Opción 2: Volver a la entrada");

                    String op2 = leerOpcionValida(2);
                    if (manejarOpcionGlobal(op2)) return;

                    jugador.setEscenaActual("1".equals(op2) ? "Caja fuerte" : "Prefectura");
                    return;
                }
            } else {
                jugador.setEscenaActual("Prefectura");
                return;
            }
        }

        // Fuera del if: solo llega acá cuando la caja ya fue abierta
        separarPantalla();
        System.out.println("De repente las luces parpadean y se apagan. Se escuchan pasos arrastrados que vienen del fondo de la sala...");
        System.out.println("\nOpción 1: Prepararse");

        String opPrep = leerOpcionValida(1);
        if (manejarOpcionGlobal(opPrep)) return;

        jugador.setEscenaActual("Policías Prefectura");
    }

    // -------------------------------------------------------------------------

    private void escenaPoliciasPrefectura() {
        separarPantalla();

        if (!jugador.isPolicia3Muerto()) {
            System.out.println("Dos policías muertos aparecen entre las sombras...");
            System.out.println("Te cortan el paso hacia la puerta...");

            System.out.println("\nOpción 1: Atacar");
            System.out.println("Opción 2: Correr hacia la puerta");

            String op = leerOpcionValida(2);
            if (manejarOpcionGlobal(op)) return;

            if ("1".equals(op)) {
                System.out.println("Te plantás y los enfrentás de a uno...");

                Enemigo policia3 = obtenerEnemigo("Policía muerto 3");
                policia3.reiniciarSalud();
                ejecutarCombate(policia3, false);
                if (salirAlMenu || !jugador.estaVivo()) return;
                System.out.println("Mataste el primer policía...");
                jugador.setPolicia3Muerto(true);

                Enemigo policia4 = obtenerEnemigo("Policía muerto 4");
                policia4.reiniciarSalud();
                ejecutarCombate(policia4, false);
                if (salirAlMenu || !jugador.estaVivo()) return;
                System.out.println("Mataste al segundo policía...");
                jugador.setPolicia4Muerto(true);

            } else {
                System.out.println("La puerta sigue trabada y los policías te alcanzan... (-20 de vida)");
                jugador.recibirDanio(20);
                if (!jugador.estaVivo()) return;
                System.out.println("*No tengo escapatoria. Tengo que pelear.*");
                jugador.setEscenaActual("Policías Prefectura");
                return;
            }
        }

        // Fuera del if: solo se muestra cuando el combate ya terminó
        System.out.println("Uno de los policías deja caer una bebida saludable al piso.");
        jugador.registrarNuevoItem(obtenerItem("Bebida saludable"));
        System.out.println("Nuevo ítem: Bebida saludable.");
        System.out.println("Se escucha un clic metálico: la traba de la puerta principal se soltó.");

        System.out.println("\nOpción 1: Salir de la Prefectura");
        String opSalir = leerOpcionValida(1);
        if (manejarOpcionGlobal(opSalir)) return;

        jugador.setEscenaActual("Calle");
    }

    // -------------------------------------------------------------------------

    private void escenaCalle() {
        separarPantalla();

        if (!jugador.isCalleOcurrida()) {
            System.out.println("Salís de la Prefectura a la niebla...");
            System.out.println("Una figura sin brazos avanza hacia vos...");

            System.out.println("\nOpción 1: Matarlo");
            System.out.println("Opción 2: Escapar");

            String op = leerOpcionValida(2);
            if (manejarOpcionGlobal(op)) return;

            if ("1".equals(op)) {
                Enemigo lyingFigure2 = obtenerEnemigo("Lying Figure 2");
                boolean loMato = ejecutarCombate(lyingFigure2, false);
                if (salirAlMenu || !jugador.estaVivo()) return;
                jugador.setLyingFigure2Muerto(true);
                System.out.println("*Cada vez hay más monstruos... Será mejor que me apure.*");
            } else {
                System.out.println("Esquivás el ácido por centímetros y salís corriendo...");
            }

            // Se marca después de que el combate o la huida terminaron
            jugador.setCalleOcurrida(true);
        }

        System.out.println("""
                Cuando por fin recuperás el aliento, lo escuchás: una música débil, una melodía de piano distorsionada que se repite una y otra vez.
                Viene de un edificio grande que se recorta entre la niebla, justo enfrente de la Prefectura: la Universidad.
                *¿Quién estaría tocando música en un lugar como éste...?*
                Es el único sonido que se escucha en todo el puerto.""");

        System.out.println("\nOpción 1: Seguir la música");
        String op = leerOpcionValida(1);
        if (manejarOpcionGlobal(op)) return;

        jugador.setEscenaActual("Universidad");
    }
    // =========================================================================
    // BLOQUE 5 DE ESCENAS: UNIVERSIDAD (final del juego)
    // =========================================================================

    private void escenaUniversidad() {
        separarPantalla();
        System.out.println("Llegás a la Universidad Tecnológica Nacional. Las puertas de vidrio están rotas y la música suena más fuerte desde adentro.");
        System.out.println("El pasillo principal de la facultad está destruido. Hay carpetas, mochilas y apuntes desparramados por el piso.");
        System.out.println("Hacia el frente, el pasillo está bloqueado por escombros: vigas caídas, pedazos de techo y un montón de mesas y bancos apilados. No hay forma de pasar.");
        System.out.println("La música viene de la derecha, donde está el buffet...");
        System.out.println("Es raro: el silencio entre una nota y la siguiente se siente más amenazante que la música.");
        System.out.println("\nOpción 1: Ir hacia el buffet");

        String op = leerOpcionValida(1);
        if (manejarOpcionGlobal(op)) return;

        jugador.setEscenaActual("Buffet");
    }

    // -------------------------------------------------------------------------

    private void escenaBuffet() {
        separarPantalla();
        System.out.println("El buffet está a oscuras, con las sillas dadas vuelta y las mesas cubiertas de polvo.");
        System.out.println("La música sale de un viejo parlante que cuelga sobre la barra.");
        System.out.println("Al fondo se ve la mesa de pool. Las escaleras que llevan al segundo piso están tapadas por escombros");
        System.out.println("y no hay forma de subir por ahí, pero al lado se ve el ascensor.");
        System.out.println("\nOpción 1: Revisar la mesa de pool");
        System.out.println("Opción 2: Ir al ascensor");

        String op = leerOpcionValida(2);
        if (manejarOpcionGlobal(op)) return;

        switch (op) {
            case "1" -> jugador.setEscenaActual("Pool");
            case "2" -> jugador.setEscenaActual("Ascensor");
        }
    }

    // -------------------------------------------------------------------------

    private void escenaPool() {
        separarPantalla();
        System.out.println("La mesa de pool está cubierta de polvo y no tiene ninguna bola.");
        System.out.println("Sobre el paño verde, escrito con sangre, se lee:");
        System.out.println("\"Sólo una hace falta para subir\"");
        System.out.println("La mesa tiene agujeros para meter las bolas y, abajo, una abertura por donde salen las bolas cuando se meten.");

        boolean tieneLlaveAscensor = jugador.getInventario().tieneItem("Llave del ascensor");

        if (!tieneLlaveAscensor) {
            System.out.println("\nAbrí el inventario si querés interactuar con algo.");
            System.out.println("Opción 1: Volver a revisar el buffet");

            String op = leerOpcionValida(1);
            if (manejarOpcionGlobal(op)) return;

            jugador.setEscenaActual("Buffet");
            }else{
            System.out.println("\nOpción 1: Ir al ascensor");
            String op = leerOpcionValida(1);
            if (manejarOpcionGlobal(op)) return;

            jugador.setEscenaActual("Ascensor");
        }

            System.out.println("Ya usaste la bola de billar. No hay nada más para hacer en la mesa.");
            System.out.println("\nOpción 1: Volver a revisar el buffet");

            String op = leerOpcionValida(1);
            if (manejarOpcionGlobal(op)) return;

            jugador.setEscenaActual("Buffet");
    }

    // -------------------------------------------------------------------------

    private void escenaAscensor() {
        separarPantalla();
        boolean tieneLlaveAscensor = jugador.getInventario().tieneItem("Llave del ascensor");
        boolean cloaca2Muerto      = jugador.isCloaca2Muerto();

        if (!tieneLlaveAscensor) {
            System.out.println("El ascensor tiene una cerradura en lugar de botones. Sin la llave no se mueve.");
            System.out.println("\nOpción 1: Volver al buffet");

            String op = leerOpcionValida(1);
            if (manejarOpcionGlobal(op)) return;

            jugador.setEscenaActual("Buffet");

        } else if (!cloaca2Muerto) {
            // Primera vez con llave: el monstruo de la cloaca sale del baño (esquivable)
            System.out.println("Caminás hacia el ascensor con la llave en la mano, pero la puerta del baño que está al lado");
            System.out.println("se abre de golpe y una figura inhumana, sale arrastrándose hacia vos...");

            System.out.println("\nOpción 1: Atacar");
            System.out.println("Opción 2: Correr");

            String op = leerOpcionValida(2);
            if (manejarOpcionGlobal(op)) return;

            if ("1".equals(op)) {
                System.out.println("Te preparás y lo atacás...");
                Enemigo cloaca2 = obtenerEnemigo("Monstruo de la cloaca 2");
                ejecutarCombate(cloaca2, true);
                if (salirAlMenu || !jugador.estaVivo()) return;

                System.out.println("El monstruo cae al piso. Del baño sigue saliendo un olor nauseabundo...");
                jugador.setCloaca2Muerto(true);

                System.out.println("\nOpción 1: Subir en el ascensor");
                System.out.println("Opción 2: Revisar el baño");

                String op2 = leerOpcionValida(2);
                if (manejarOpcionGlobal(op2)) return;

                jugador.setEscenaActual("1".equals(op2) ? "Segundo piso" : "BañoUTN");

            } else {
                // Corrió: la garra lo alcanza antes de que cierren las puertas (-10)
                System.out.println("El monstruo alcanza a herirte justo antes de que el ascensor se cierre y corte su cabeza... (-10 de salud)");
                jugador.recibirDanio(10);
                jugador.setCloaca2Muerto(true);
                if (!jugador.estaVivo()) return;

                System.out.println("\nOpción 1: Subir en el ascensor");

                String op2 = leerOpcionValida(1);
                if (manejarOpcionGlobal(op2)) return;

                jugador.setEscenaActual("Segundo piso");
            }

        } else {
            // Cloaca 2 ya muerto: ascensor libre
            System.out.println("El ascensor espera con las puertas abiertas.");
            System.out.println("\nOpción 1: Subir en el ascensor");
            System.out.println("Opción 2: Revisar el baño");

            String op = leerOpcionValida(2);
            if (manejarOpcionGlobal(op)) return;

            jugador.setEscenaActual("1".equals(op) ? "Segundo piso" : "BañoUTN");
        }
    }

    // -------------------------------------------------------------------------

    private void escenaBanioUTN() {
        separarPantalla();
        if (!jugador.getInventario().tieneItem("Botiquín de primeros auxilios 2")) {
            System.out.println("Entrás al baño con cuidado. Hay sangre en las paredes y un espejo roto.");
            System.out.println("Sobre el lavatorio, colgado de la pared, hay un botiquín de primeros auxilios.");

            jugador.registrarNuevoItem(obtenerItem("Botiquín de primeros auxilios 2"));
            System.out.println("Nuevo ítem: Botiquín de primeros auxilios.");
        } else {
            System.out.println("Ya revisaste el baño. No queda nada más.");
        }

        System.out.println("\nOpción 1: Volver al ascensor");

        String op = leerOpcionValida(1);
        if (manejarOpcionGlobal(op)) return;

        jugador.setEscenaActual("Ascensor");
    }

    // -------------------------------------------------------------------------

    private void escenaSegundoPiso() {
        separarPantalla();

        if (!jugador.isLassieMuerto()) {
            // Primera vez: los tres perros atacan
            if (!jugador.isSegundoPisoVisitado()) {
                System.out.println("Subís al ascensor. Las puertas se cierran y empieza a subir solo, con un chirrido metálico.");
                System.out.println("Un ruido seco... y las puertas se abren.");
                System.out.println("No bien salís, unos gruñidos te rodean. Los tres perros de la facultad, Peluche, Timba y Lassie,");
                System.out.println("los que siempre andaban por los pasillos pidiendo comida, ahora tienen los ojos rojos y la piel pelada.");
                System.out.println("Se abalanzan sobre vos...");
                jugador.setSegundoPisoVisitado(true);
            } else {
                System.out.println("Los perros siguen rodeándote, gruñendo...");
            }
                System.out.println("\nOpción 1: Atacar");
                System.out.println("Opción 2: Correr");

                String op = leerOpcionValida(2);
                if (manejarOpcionGlobal(op)) return;

                if ("1".equals(op)) {
                    System.out.println("Te defendés de los tres perros uno por uno...");

                    Enemigo peluche = obtenerEnemigo("Peluche");
                    boolean loMato1 = ejecutarCombate(peluche, false);
                    if (salirAlMenu || !jugador.estaVivo()) return;
                    if (loMato1) {
                        System.out.println("Mataste a Peluche...");
                        jugador.setPelucheMuerto(true);
                    }

                    Enemigo timba = obtenerEnemigo("Timba");
                    boolean loMato2 = ejecutarCombate(timba, false);
                    if (salirAlMenu || !jugador.estaVivo()) return;
                    if (loMato2) {
                        System.out.println("Mataste a Timba...");
                        jugador.setTimbaMuerto(true);
                    }

                    Enemigo lassie = obtenerEnemigo("Lassie");
                    boolean loMato3 = ejecutarCombate(lassie, false);
                    if (salirAlMenu || !jugador.estaVivo()) return;
                    if (loMato3) {
                        System.out.println("Mataste a Lassie...");
                        jugador.setLassieMuerto(true);
                    }

                    System.out.println("El último perro cae al piso y todo queda en silencio.");
                    System.out.println("*Perdón, chicos...* 😢");

                    System.out.println("\nOpción 1: Avanzar por el pasillo");

                    String op2 = leerOpcionValida(1);
                    if (manejarOpcionGlobal(op2)) return;

                    // Vuelve a esta misma escena para mostrar el menú del pasillo
                    jugador.setEscenaActual("Segundo piso");

                } else {
                    // Intentó correr: los perros son más rápidos, -10 por cada uno (-30 total)
                    System.out.println("Corrés por el pasillo pero los perros son más rápidos y te muerden las piernas");
                    System.out.println("No te dejan escapar... (-10 de salud por cada perro)");
                    jugador.recibirDanio(30);
                    if (!jugador.estaVivo()) return;

                }

            } else {
                // Perros ya muertos: menú normal del pasillo
                System.out.println("El pasillo del segundo piso está en silencio.");
                System.out.println("A un lado está la puerta de la SAE (Secretaría de Asuntos Estudiantiles)");
                System.out.println("y, en el otro extremo, unas escaleras que bajan hacia el patio interno de la facultad.");

                System.out.println("\nOpción 1: Ir a la puerta de la SAE");
                System.out.println("Opción 2: Bajar por las escaleras");

                String op = leerOpcionValida(2);
                if (manejarOpcionGlobal(op)) return;

                jugador.setEscenaActual("1".equals(op) ? "SAE" : "Escaleras");
            }
    }

    // -------------------------------------------------------------------------

    private void escenaSAE() {
        separarPantalla();

        if (!jugador.getInventario().tieneItem("Dibujo del patio")) {
            System.out.println("Pegado en la puerta de la SAE con cinta hay un dibujo hecho con crayones.");
            System.out.println("Muestra un patio rodeado por las paredes de una facultad, un único banco de madera en el medio");
            System.out.println("y, al lado del banco, una figura enorme y negra.");
            System.out.println("Abajo, con letra infantil temblorosa, se lee: \"Acá termina todo\".");
            jugador.registrarNuevoItem(obtenerItem("Dibujo del patio"));
            System.out.println("Nuevo ítem: Dibujo del patio.");
        }

        if (jugador.isManiquiMuerto()) {
            // Maniquí ya muerto: revisamos cajones
            if (!jugador.isCajaSaeAbierta()) {
                System.out.println("Revisás los cajones de los escritorios. En uno encontrás una caja de balas.");
                jugador.registrarNuevoItem(obtenerItem("Caja de balas"));
                jugador.setCajaSaeAbierta(true);
                System.out.println("Nuevo ítem: Caja de balas.");
            } else {
                System.out.println("Ya no hay nada más para revisar en la oficina.");
            }

            System.out.println("\nOpción 1: Volver al pasillo");
            String op = leerOpcionValida(1);
            if (manejarOpcionGlobal(op)) return;
            jugador.setEscenaActual("Segundo piso");

        } else if (jugador.isSaePuertaAbierta()) {
            // Puerta abierta con la llave: combate con el maniquí
            System.out.println("Adentro hay escritorios, fichas y un maniquí de exhibición parado en el centro de la sala.");
            System.out.println("Cuando das el primer paso, el maniquí gira lentamente la cabeza hacia vos");
            System.out.println("y empieza a caminar con las piernas rígidas...");

            System.out.println("\nOpción 1: Atacar");
            System.out.println("Opción 2: Correr");

            String op = leerOpcionValida(2);
            if (manejarOpcionGlobal(op)) return;

            if ("1".equals(op)) {
                Enemigo maniqi = obtenerEnemigo("Maniquí");
                maniqi.reiniciarSalud();
                ejecutarCombate(maniqi, false);
                if (salirAlMenu || !jugador.estaVivo()) return;
                System.out.println("El maniquí se parte en pedazos contra el piso.");
                jugador.setManiquiMuerto(true);
                jugador.setEscenaActual("SAE");
            } else {
                System.out.println("Intentás salir pero el maniquí te cierra el paso y te golpea... (-25 de salud)");
                jugador.recibirDanio(25);
                if (!jugador.estaVivo()) return;
                jugador.setEscenaActual("SAE");
            }

        } else {
            // Puerta cerrada: sin llave o sin haberla usado todavía
            System.out.println("La puerta de la SAE está cerrada con llave.");
            if (!jugador.getInventario().tieneItem("Llave de la SAE")) {
                System.out.println("*Necesito la llave de esta oficina...*");
            }

            System.out.println("\nOpción 1: Volver al pasillo");
            String op = leerOpcionValida(1);
            if (manejarOpcionGlobal(op)) return;
            jugador.setEscenaActual("Segundo piso");
        }
    }

    // -------------------------------------------------------------------------

    private void escenaEscaleras() {
        separarPantalla();
        boolean tieneLlaveSAE     = jugador.getInventario().tieneItem("Llave de la SAE");
        boolean estudianteMuerto  = jugador.isEstudianteMuerto();

        // El Estudiante aparece SOLO si el jugador NO tiene la llave SAE y aún no fue derrotado
        if (!tieneLlaveSAE && !estudianteMuerto) {
            System.out.println("Caminás hasta el otro extremo del pasillo y empezás a bajar por las escaleras.");
            System.out.println("A mitad de camino, una figura con una mochila vieja gira lentamente hacia vos.");
            System.out.println("No tiene rostro, sólo piel lisa donde deberían estar los ojos y la boca.");
            System.out.println("Empieza a arrastrarse hacia vos...");

            System.out.println("\nOpción 1: Atacar");
            System.out.println("Opción 2: Correr");

            String op = leerOpcionValida(2);
            if (manejarOpcionGlobal(op)) return;

            if ("1".equals(op)) {
                System.out.println("Esperás a que se acerque y lo atacás...");
                Enemigo estudiante = obtenerEnemigo("Estudiante sin rostro");
                ejecutarCombate(estudiante, false);
                if (salirAlMenu || !jugador.estaVivo()) return;

                System.out.println("El estudiante cae por las escaleras y se pierde en la oscuridad. La música sigue sonando.");
                jugador.setEstudianteMuerto(true);

                // Vuelve a esta escena para mostrar la puerta del patio
                jugador.setEscenaActual("Escaleras");

            } else {
                // Intentó correr: lo agarra del brazo (-15)
                System.out.println("Intentás pasar a su lado pero te agarra del brazo antes de que puedas alejarte... No podés escapar(-15 de salud)");
                jugador.recibirDanio(15);
                if (!jugador.estaVivo()) return;

                // Fuerza el combate desde "Defenderte" (misma escena)
                jugador.setEscenaActual("Escaleras");
            }

        } else {
            // Tiene llave SAE (Maniquí en su lugar) o el Estudiante ya cayó: pasillo libre
            System.out.println("Bajás por las escaleras hasta el final del pasillo.");
            System.out.println("Una puerta de vidrio da al patio interno de la facultad.");
            System.out.println("\nOpción 1: Abrir la puerta del patio");

            String op = leerOpcionValida(1);
            if (manejarOpcionGlobal(op)) return;

            jugador.setEscenaActual("Patio");
        }
    }

    // -------------------------------------------------------------------------

    private void escenaPatio() {
        separarPantalla();

        JefeFinal rector = (JefeFinal) obtenerEnemigo("El Rector");

        if (!jugador.isRectorMuerto()) {
            // Primera vez: presentación
            if (rector.getSalud() == rector.getSaludMaxima()) {
                System.out.println("Abrís la puerta de vidrio y salís al patio interno de la facultad.");
                System.out.println("La niebla es tan espesa que apenas ves las paredes que lo rodean.");
                System.out.println("Hay un único banco de madera en el medio y nada más.");
                System.out.println("La música se detiene de golpe.");
                System.out.println("Detrás tuyo la puerta se cierra con un golpe seco y desaparece de la pared.");
                System.out.println("Junto al banco, una figura enorme con un traje viejo de rector se levanta lentamente del piso.");
                System.out.println("Donde debería tener un brazo, tiene una bobina de alta tensión con cables de cobre incrustados en la carne,");
                System.out.println("que chisporrotean y se iluminan de a poco.");
                System.out.println("*-Esto es lo que querían que encuentre...*");

                // Pausa para leer el texto antes de que empiece la descarga
                System.out.println("\nOpción 1: Continuar");
                String opLeer = leerOpcionValida(1);
                if (manejarOpcionGlobal(opLeer)) return;

            } else {
                System.out.println("El Rector sigue en pie. La bobina vuelve a cargarse con un zumbido agudo...");
                System.out.println("Salud del Rector: " + rector.getSalud() + "/" + rector.getSaludMaxima());
            }

            // Descarga eléctrica antes de cada ataque del jugador
            System.out.println("\nLos cables se tensan y la bobina apunta directo hacia vos... ¡DESCARGA!");
            boolean esquivo = rector.lanzarDescarga(jugador, scanner);
            if (!jugador.estaVivo()) return;

            if (esquivo) {
                System.out.println("Te tirás al piso y la descarga pasa sobre tu cabeza...");
            }else{
                System.out.println("Tardaste demasiado, la descarga eléctrica te da de lleno...");
                System.out.println("Tu salud queda en " + jugador.getSalud() + "/100");
            }

            // Ataque del jugador
            System.out.println("\nOpción 1: Atacar");
            String op = leerOpcionValida(1);
            if (manejarOpcionGlobal(op)) return;

            jugador.atacar(rector);

            if (rector.estaVivo()) {
                System.out.println("Lográs herirlo, pero el Rector no cae...");
                System.out.println("\nOpción 1: Seguir peleando");
                String opSeguir = leerOpcionValida(1);
                if (manejarOpcionGlobal(opSeguir)) return;
                // La salud del Rector persiste en la instancia del catálogo
                jugador.setEscenaActual("Patio");
            } else {
                System.out.println("El Rector cae de rodillas y se desploma sobre el piso del patio.");
                jugador.setRectorMuerto(true);
                jugador.registrarEnemigoMuerto();

                System.out.println("\nOpción 1: Continuar");
                String opFinal = leerOpcionValida(1);
                if (manejarOpcionGlobal(opFinal)) return;

                jugador.setEscenaActual("Final");
            }
        }
    }

    // -------------------------------------------------------------------------

    private void escenaFinal() {
        separarPantalla();
        String tipoFinal = jugador.calcularFinal();

        switch (tipoFinal) {
            case "BUENO" -> {
                System.out.println("Con la caída del Rector, la niebla empieza a levantarse. Un rayo de sol entra al patio por primera vez.");
                System.out.println("Tu celular, que no prendía desde que despertaste, vibra en tu bolsillo. En la pantalla hay un solo mensaje:");
                System.out.println("*\"Gracias por venir a buscarme.\"*");
                System.out.println("Mirás hacia arriba. Sobre el techo del patio, una niña con un barco de juguete te saluda con la mano... y desaparece.");
                System.out.println("*¿Todo esto terminó... o recién empieza?*");
            }
            case "MEDIO" -> {
                System.out.println("La niebla se levanta muy lentamente. Tu celular, que no prendía desde que despertaste, vibra en tu bolsillo.");
                System.out.println("La pantalla sólo muestra estática, pero en su reflejo podés ver una sombra sobre el techo del patio...");
                System.out.println("Mirás hacia arriba, pero el techo está vacío, y de él cae un pequeño barco de juguete.");
                System.out.println("*La niña... me pregunto si estará bien... tengo que seguir buscándola...*");
                System.out.println("De repente tu visión se nubla y todo se vuelve negro... volvés a despertar en el mismo departamento.");
            }
            case "MALO" -> {
                System.out.println("Tu celular sigue sin prender. La niebla sigue igual de espesa.");
                System.out.println("Mirás hacia arriba, pero el techo del patio está vacío.");
                System.out.println("Lo único que se escucha es el eco de una melodía de piano muy lejana.");
                System.out.println("*No pude encontrar a la niña...*");
                System.out.println("Ahora estás atrapado en la universidad, no hay forma de salir...");
            }
        }

        System.out.println("\n--- FIN ---");
        System.out.println("Monstruos derrotados: " + jugador.getMonstruosMuertos());
        System.out.println("Ítems obtenidos: "      + jugador.getItemsObtenidos());
        System.out.println("\nOpción 1: Volver al menú principal");

        String op = leerOpcionValida(1);
        if (manejarOpcionGlobal(op)) return;

        salirAlMenu = true;
    }

}
