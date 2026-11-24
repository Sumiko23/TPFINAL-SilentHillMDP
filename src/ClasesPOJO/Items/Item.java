package ClasesPOJO.Items;

import java.util.Objects;

public class Item {
    private int id;
    private String nombre;
    private String descripcion;
    private TipoItem tipo;
    private int efectoCuracion;
    private int cantidadBalas;
    private int danio;
    private boolean activo;

    public Item() {
        this.activo = true;
    }

    public Item(int id, String nombre, String descripcion, TipoItem tipo, int efectoCuracion, int cantidadBalas, int danio, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.efectoCuracion = efectoCuracion;
        this.cantidadBalas = cantidadBalas;
        this.danio = danio;
        this.activo = activo;
    }

    public int getDanioEfectivo() {
        return this.danio;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public TipoItem getTipo() { return tipo; }
    public void setTipo(TipoItem tipo) { this.tipo = tipo; }

    public int getEfectoCuracion() { return efectoCuracion; }
    public void setEfectoCuracion(int efectoCuracion) { this.efectoCuracion = efectoCuracion; }

    public int getCantidadBalas() {
        return cantidadBalas;
    }

    public Item setCantidadBalas(int cantidadBalas) {
        this.cantidadBalas = cantidadBalas;
        return this;
    }

    public int getDanio() { return danio; }
    public void setDanio(int danio) { this.danio = danio; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return Objects.equals(nombre, item.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }

    @Override
    public String toString() {
        return nombre + " [" + tipo + "] - " + descripcion;}
}
