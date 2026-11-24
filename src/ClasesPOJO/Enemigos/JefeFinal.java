package ClasesPOJO.Enemigos;

import ClasesPOJO.Jugador;

import java.util.Scanner;

public class JefeFinal extends Enemigo {
    private long tiempoLimiteMs;

    public JefeFinal(int id, String nombre, int saludMaxima, int danioAtaque,
                     boolean esquivable, boolean activo, long tiempoLimiteMs) {
        super(id, nombre, saludMaxima, danioAtaque, esquivable, activo);
        this.tiempoLimiteMs = tiempoLimiteMs > 0 ? tiempoLimiteMs : 3000L;
    }


     //Mide en milisegundos cuánto tarda el jugador en reaccionar (sin hilos).
     //Si tarda más de 3 segundos (3000 ms), recibe 30 de daño.
    public boolean lanzarDescarga(Jugador jugador, Scanner scanner) {
        System.out.print("¡Escribí '1' rápido para ESQUIVAR!: ");
        long inicio = System.currentTimeMillis();
        String respuesta = scanner.nextLine().trim();
        long duracion = System.currentTimeMillis() - inicio;

        if ("1".equals(respuesta) && duracion <= tiempoLimiteMs) {
            return true;
        } else {
            jugador.recibirDanio(30);
            return false;
        }
    }

    public long getTiempoLimiteMs() { return tiempoLimiteMs; }
    public void setTiempoLimiteMs(long tiempoLimiteMs) { this.tiempoLimiteMs = tiempoLimiteMs; }
}

