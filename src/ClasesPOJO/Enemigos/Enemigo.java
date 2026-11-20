package ClasesPOJO.Enemigos;

import Interfaces.IAtacable;

public abstract class Enemigo implements IAtacable{
    protected int id;
    protected String nombre;
    protected int salud;
    protected int saludMaxima;
    protected int danioAtaque;
    protected boolean esquivable;
    protected boolean activo;

    public Enemigo(int id, String nombre, int saludMaxima, int danioAtaque, boolean esquivable, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.saludMaxima = saludMaxima;
        this.salud = saludMaxima;
        this.danioAtaque = danioAtaque;
        this.esquivable = esquivable;
        this.activo = activo;
    }

    @Override
    public void atacar(IAtacable objetivo) {
        if (estaVivo()) {
            objetivo.recibirDanio(this.danioAtaque);
        }
    }

    @Override
    public void recibirDanio(int danio) {
        this.salud = Math.max(0, this.salud - danio);
    }

    @Override
    public boolean estaVivo() {
        return this.salud > 0;
    }

    public void reiniciarSalud() {
        this.salud = this.saludMaxima;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getSalud() { return salud; }
    public void setSalud(int salud) { this.salud = salud; }

    public int getSaludMaxima() { return saludMaxima; }
    public void setSaludMaxima(int saludMaxima) { this.saludMaxima = saludMaxima; }

    public int getDanioAtaque() { return danioAtaque; }
    public void setDanioAtaque(int danioAtaque) { this.danioAtaque = danioAtaque; }

    public boolean isEsquivable() { return esquivable; }
    public void setEsquivable(boolean esquivable) { this.esquivable = esquivable; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
