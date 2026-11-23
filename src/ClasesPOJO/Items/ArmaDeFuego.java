package ClasesPOJO.Items;

public class ArmaDeFuego extends Item {
    private int balasCargador;
    private int capacidadCargador;
    private int cajasBalas;

    public ArmaDeFuego(int id, String nombre, String descripcion, int danio,
                       int balasCargador, int capacidadCargador, int cajasBalas, boolean activo) {
        super(id, nombre, descripcion, TipoItem.ARMA_FUEGO, 0, danio, activo);
        this.balasCargador = balasCargador;
        this.capacidadCargador = capacidadCargador;
        this.cajasBalas = cajasBalas;
    }


     //Descuenta una bala por disparo.

    public boolean disparar() {
        if (balasCargador > 0) {
            balasCargador--;
            System.out.println("Disparaste. Balas restantes en cargador: " + balasCargador + "/" + capacidadCargador);
            return true;
        } else {
            if (cajasBalas > 0) {
                System.out.println("¡Click! Sin balas. Tenés cajas disponibles para recargar.");
            } else {
                System.out.println("¡Click! No hay más balas disponibles para recargar.");
            }
            return false;
        }
    }

    public boolean recargar() {
        if (cajasBalas > 0) {
            cajasBalas--;
            balasCargador = capacidadCargador;
            System.out.println("Recargaste "+ balasCargador + " balas. Cajas restantes: " + cajasBalas);
            return true;
        }
        System.out.println("No tenés cajas de balas disponibles para recargar.");
        return false;
    }

    public int getBalasCargador() { return balasCargador; }
    public void setBalasCargador(int balasCargador) { this.balasCargador = balasCargador; }

    public int getCapacidadCargador() { return capacidadCargador; }
    public void setCapacidadCargador(int capacidadCargador) { this.capacidadCargador = capacidadCargador; }

    public int getCajasBalas() { return cajasBalas; }
    public void setCajasBalas(int cajasBalas) { this.cajasBalas = cajasBalas; }
}
