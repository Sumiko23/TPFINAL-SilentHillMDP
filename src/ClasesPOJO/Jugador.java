package ClasesPOJO;

import ClasesPOJO.Items.ArmaDeFuego;
import ClasesPOJO.Items.Inventario;
import ClasesPOJO.Items.Item;
import Interfaces.IAtacable;

/**
 * POJO principal del jugador. Almacena todo el progreso de la partida en memoria
 * y se persiste en MySQL únicamente al crear partida o guardar/salir ('0').
 */
public class Jugador implements IAtacable {
    private static final int SALUD_MAXIMA = 100;
    private static final int DANIO_SIN_ARMA = 10;

    private int id;
    private String nombre;
    private boolean activo;
    private int salud;
    private int monstruosMuertos;
    private int itemsObtenidos;
    private String escenaActual;
    private Inventario inventario;
    private Item armaEquipada;
    private int balasCargador;
    private int cajasBalas;

    // Estado de escenas en memoria
    private boolean deptoVisitado;
    private boolean prefecturaVisitada;
    private boolean barcoVisitado;
    private boolean policia2Aparecio;
    private boolean cajaSaeAbierta;
    private int rondaRector;

    // Estado de enemigos derrotados en memoria
    private boolean cloacaMuerto;
    private boolean policia1Muerto;
    private boolean policia2Muerto;
    private boolean acuaticoMuerto;
    private boolean lyingFigure1Muerto;
    private boolean policia3Muerto;
    private boolean policia4Muerto;
    private boolean lyingFigure2Muerto;
    private boolean cloaca2Muerto;
    private boolean pelucheMuerto;
    private boolean timbaMuerto;
    private boolean lassieMuerto;
    private boolean maniquiMuerto;
    private boolean estudianteMuerto;
    private boolean rectorMuerto;

    public Jugador(String nombre) {
        this.id = 0;
        this.nombre = nombre;
        this.activo = true;
        this.salud = SALUD_MAXIMA;
        this.escenaActual = "Departamento";
        this.inventario = new Inventario();
    }

    public Jugador(int id, String nombre, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.activo = activo;
        this.salud = SALUD_MAXIMA;
        this.escenaActual = "Departamento";
        this.inventario = new Inventario();
    }

    public void mostrarMenu() {
        System.out.println("=== SESIÓN DE JUGADOR: " + nombre + " ===");
        System.out.println("1 - Continuar partida guardada");
        System.out.println("2 - Iniciar partida nueva");
    }

    @Override
    public void atacar(IAtacable objetivo) {
        int danioAInfligir = DANIO_SIN_ARMA;

        if (armaEquipada != null) {
            if (armaEquipada instanceof ArmaDeFuego armaFuego) {
                if (armaFuego.disparar()) {
                    danioAInfligir = armaFuego.getDanio();
                    this.balasCargador = armaFuego.getBalasCargador();
                } else {
                    System.out.println("Sin munición, golpeás con la culata (-10 de daño).");
                    danioAInfligir = DANIO_SIN_ARMA;
                }
            } else {
                danioAInfligir = armaEquipada.getDanio();
            }
        }
        objetivo.recibirDanio(danioAInfligir);
    }

    @Override
    public void recibirDanio(int danio) {
        this.salud = Math.max(0, this.salud - danio);
    }

    @Override
    public boolean estaVivo() {
        return this.salud > 0;
    }

    public void curar(int puntos) {
        this.salud = Math.min(SALUD_MAXIMA, this.salud + puntos);
    }

    public void registrarNuevoItem(Item item) {
        this.inventario.agregarItem(item);
        this.itemsObtenidos++;
    }

    public void registrarEnemigoMuerto() {
        this.monstruosMuertos++;
    }

    public String calcularFinal() {
        if (monstruosMuertos >= 12 && itemsObtenidos >= 18) {
            return "BUENO";
        } else if (monstruosMuertos <= 9 && itemsObtenidos <= 14) {
            return "MALO";
        }
        return "MEDIO";
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public int getSalud() { return salud; }
    public void setSalud(int salud) { this.salud = Math.max(0, Math.min(SALUD_MAXIMA, salud)); }

    public int getMonstruosMuertos() { return monstruosMuertos; }
    public void setMonstruosMuertos(int monstruosMuertos) { this.monstruosMuertos = monstruosMuertos; }

    public int getItemsObtenidos() { return itemsObtenidos; }
    public void setItemsObtenidos(int itemsObtenidos) { this.itemsObtenidos = itemsObtenidos; }

    public String getEscenaActual() { return escenaActual; }
    public void setEscenaActual(String escenaActual) { this.escenaActual = escenaActual; }

    public Inventario getInventario() { return inventario; }
    public void setInventario(Inventario inventario) { this.inventario = inventario; }

    public Item getArmaEquipada() { return armaEquipada; }
    public void setArmaEquipada(Item armaEquipada) { this.armaEquipada = armaEquipada; }

    public int getBalasCargador() { return balasCargador; }
    public void setBalasCargador(int balasCargador) { this.balasCargador = balasCargador; }

    public int getCajasBalas() { return cajasBalas; }
    public void setCajasBalas(int cajasBalas) { this.cajasBalas = cajasBalas; }

    public boolean isDeptoVisitado() { return deptoVisitado; }
    public void setDeptoVisitado(boolean deptoVisitado) { this.deptoVisitado = deptoVisitado; }

    public boolean isPrefecturaVisitada() { return prefecturaVisitada; }
    public void setPrefecturaVisitada(boolean prefecturaVisitada) { this.prefecturaVisitada = prefecturaVisitada; }

    public boolean isBarcoVisitado() { return barcoVisitado; }
    public void setBarcoVisitado(boolean barcoVisitado) { this.barcoVisitado = barcoVisitado; }

    public boolean isPolicia2Aparecio() { return policia2Aparecio; }
    public void setPolicia2Aparecio(boolean policia2Aparecio) { this.policia2Aparecio = policia2Aparecio; }

    public boolean isCajaSaeAbierta() { return cajaSaeAbierta; }
    public void setCajaSaeAbierta(boolean cajaSaeAbierta) { this.cajaSaeAbierta = cajaSaeAbierta; }

    public int getRondaRector() { return rondaRector; }
    public void setRondaRector(int rondaRector) { this.rondaRector = rondaRector; }

    public boolean isCloacaMuerto() { return cloacaMuerto; }
    public void setCloacaMuerto(boolean cloacaMuerto) { this.cloacaMuerto = cloacaMuerto; }

    public boolean isPolicia1Muerto() { return policia1Muerto; }
    public void setPolicia1Muerto(boolean policia1Muerto) { this.policia1Muerto = policia1Muerto; }

    public boolean isPolicia2Muerto() { return policia2Muerto; }
    public void setPolicia2Muerto(boolean policia2Muerto) { this.policia2Muerto = policia2Muerto; }

    public boolean isAcuaticoMuerto() { return acuaticoMuerto; }
    public void setAcuaticoMuerto(boolean acuaticoMuerto) { this.acuaticoMuerto = acuaticoMuerto; }

    public boolean isLyingFigure1Muerto() { return lyingFigure1Muerto; }
    public void setLyingFigure1Muerto(boolean lyingFigure1Muerto) { this.lyingFigure1Muerto = lyingFigure1Muerto; }

    public boolean isPolicia3Muerto() { return policia3Muerto; }
    public void setPolicia3Muerto(boolean policia3Muerto) { this.policia3Muerto = policia3Muerto; }

    public boolean isPolicia4Muerto() { return policia4Muerto; }
    public void setPolicia4Muerto(boolean policia4Muerto) { this.policia4Muerto = policia4Muerto; }

    public boolean isLyingFigure2Muerto() { return lyingFigure2Muerto; }
    public void setLyingFigure2Muerto(boolean lyingFigure2Muerto) { this.lyingFigure2Muerto = lyingFigure2Muerto; }

    public boolean isCloaca2Muerto() { return cloaca2Muerto; }
    public void setCloaca2Muerto(boolean cloaca2Muerto) { this.cloaca2Muerto = cloaca2Muerto; }

    public boolean isPelucheMuerto() { return pelucheMuerto; }
    public void setPelucheMuerto(boolean pelucheMuerto) { this.pelucheMuerto = pelucheMuerto; }

    public boolean isTimbaMuerto() { return timbaMuerto; }
    public void setTimbaMuerto(boolean timbaMuerto) { this.timbaMuerto = timbaMuerto; }

    public boolean isLassieMuerto() { return lassieMuerto; }
    public void setLassieMuerto(boolean lassieMuerto) { this.lassieMuerto = lassieMuerto; }

    public boolean isManiquiMuerto() { return maniquiMuerto; }
    public void setManiquiMuerto(boolean maniquiMuerto) { this.maniquiMuerto = maniquiMuerto; }

    public boolean isEstudianteMuerto() { return estudianteMuerto; }
    public void setEstudianteMuerto(boolean estudianteMuerto) { this.estudianteMuerto = estudianteMuerto; }

    public boolean isRectorMuerto() { return rectorMuerto; }
    public void setRectorMuerto(boolean rectorMuerto) { this.rectorMuerto = rectorMuerto; }
}