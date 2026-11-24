package ClasesPOJO.Items;

public class ArmaDeFuego extends Item {
    private int balasCargador;
    private int capacidadCargador;

    public ArmaDeFuego(int id, String nombre, String descripcion, int danio,
                       int balasCargador, int capacidadCargador, boolean activo) {
        super(id, nombre, descripcion, TipoItem.ARMA_FUEGO, 0 ,0,danio, activo);
        this.balasCargador = balasCargador;
        this.capacidadCargador = capacidadCargador;
    }


     //Descuenta una bala por disparo.

    public boolean disparar() {
        if (balasCargador > 0) {
            balasCargador--;
            System.out.println("Disparaste. Balas restantes en cargador: " + balasCargador + "/" + capacidadCargador);
            return true;
        } else {
            System.out.println("¡Click! Sin balas en el cargador.");
            return false;
        }
    }

    public int cargarDesdeCantidad(int balasDisponibles) {
        int espacioLibre = capacidadCargador - balasCargador;
        if (espacioLibre <= 0 || balasDisponibles <= 0) {
            return 0;
        }
        int usadas = Math.min(espacioLibre, balasDisponibles);
        balasCargador += usadas;
        return usadas;
    }

    public int getBalasCargador() { return balasCargador; }
    public void setBalasCargador(int balasCargador) { this.balasCargador = balasCargador; }

    public int getCapacidadCargador() { return capacidadCargador; }
    public void setCapacidadCargador(int capacidadCargador) { this.capacidadCargador = capacidadCargador; }

}
