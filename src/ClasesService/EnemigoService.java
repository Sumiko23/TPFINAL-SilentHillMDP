package ClasesService;

import ClasesDAO.EnemigoDAO;
import ClasesPOJO.Enemigos.Enemigo;
import ClasesPOJO.Enemigos.JefeFinal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnemigoService {
// List: los enemigos se recorren en el orden en que se cargan y no hace falta buscarlos por clave.
private List<Enemigo> lista;
private final EnemigoDAO dao;



public EnemigoService() throws SQLException {
  this.dao = new EnemigoDAO();
 this.lista = new ArrayList<>(dao.listar());
        }



        public boolean agregar(Enemigo aux) throws SQLException {
            if (dao.buscarPorNombre(aux.getNombre()) != null) {
                return false;
            }
            int id = dao.insertar(aux);
            if (id > 0) {
                lista.add(aux);
                return true;
            }
            return false;
        }

        public boolean eliminar(int id) throws SQLException {
            boolean seElimino = dao.eliminar(id);
            if (seElimino) {
                Iterator<Enemigo> it = lista.iterator();
                while (it.hasNext()) {
                    Enemigo aux = it.next();
                    if (aux.getId() == id) {
                        it.remove();
                        break;
                    }
                }
            }
            return seElimino;
        }

        public boolean reactivar(int id) throws SQLException {
            boolean seReactivo = dao.reactivar(id);
            if (seReactivo) {
                Enemigo aux = dao.buscarPorId(id);
                if (aux != null) {
                    lista.add(aux);
                }
            }
            return seReactivo;
        }

        public boolean modificar(int id, Enemigo nuevoEnemigo) throws SQLException {
            Enemigo existente = dao.buscarPorNombre(nuevoEnemigo.getNombre());
            if (existente != null && existente.getId() != id) {
                return false;
            }
            nuevoEnemigo.setId(id);
            boolean seModifico = dao.modificar(nuevoEnemigo);

            if (seModifico) {
                // Se vuelve a cargar la lista desde la base para que memoria y base queden idénticas.
                this.lista = new ArrayList<>(dao.listar());
            }
            return seModifico;
        }

        public Enemigo buscarPorIdBD(int id) throws SQLException {
            return dao.buscarPorId(id);
        }

        public Enemigo buscarPorNombreBD(String nombre) throws SQLException {
            return dao.buscarPorNombre(nombre);
        }



        public Enemigo buscarPorNombreMemoria(String nombre) {
            for (Enemigo aux : lista) {
                if (aux.getNombre().equalsIgnoreCase(nombre.trim())) {
                    return aux;
                }
            }
            return null;
        }




    public String mostrarListado() {
        if (lista.isEmpty()) {
            return "No hay enemigos activos.";
        }
        StringBuilder res = new StringBuilder();
        for (Enemigo aux : lista) {
            res.append(mostrar(aux));
        }
        return res.toString();
    }

    public String mostrarDadosDeBaja() throws SQLException {
        List<Enemigo> bajas = dao.listarBajas();
        if (bajas.isEmpty()) {
            return "No hay enemigos dados de baja.";
        }
        StringBuilder res = new StringBuilder();
        for (Enemigo aux : bajas) {
            res.append(mostrar(aux));
        }
        return res.toString();
    }


    public String mostrar(Enemigo e) {
        String res = "[" + e.getId() + "] " + e.getNombre()
                + " | Tipo: " + e.getClass().getSimpleName()
                + " | Salud máx: " + e.getSaludMaxima()
                + " | Daño: " + e.getDanioAtaque()
                + " | Esquivable: " + (e.isEsquivable() ? "sí" : "no");
        if (e instanceof JefeFinal jefe) {
            res += " | Tiempo límite (ms): " + jefe.getTiempoLimiteMs();
        }
        return res + "\n";
    }

}
