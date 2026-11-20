package Interfaces;

public interface IAtacable {

    void atacar(IAtacable objetivo);
    void recibirDanio(int danio);
    boolean estaVivo();
}
