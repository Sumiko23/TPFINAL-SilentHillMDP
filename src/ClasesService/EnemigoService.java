package ClasesService;

import ClasesDAO.EnemigoDAO;
import ClasesPOJO.Enemigos.Enemigo;

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



        /** Alta. Devuelve false si ya existe un enemigo con ese nombre o si no se pudo insertar. */
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

        /** Baja lógica: queda en la base con activo = false y sale de la lista en memoria. */
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

        /** Reactiva un enemigo dado de baja y lo vuelve a cargar en memoria. */
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

        /** Modificación. Devuelve false si el nombre nuevo ya lo usa otro enemigo o si no existe el id. */
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

        public Enemigo buscarPorIdMemoria(int id) {
            for (Enemigo aux : lista) {
                if (aux.getId() == id) {
                    return aux;
                }
            }
            return null;
        }

        public Enemigo buscarPorNombreMemoria(String nombre) {
            for (Enemigo aux : lista) {
                if (aux.getNombre().equalsIgnoreCase(nombre.trim())) {
                    return aux;
                }
            }
            return null;
        }

        /** Texto con los enemigos activos (lista en memoria). */
        public String mostrarListado() {
            StringBuilder res = new StringBuilder();
            for (Enemigo aux : lista) {
                res.append(mostrar(aux));
            }
            return res.toString();
        }

        /** Texto con los enemigos dados de baja (se consultan en la base, no están en memoria). */
        public String mostrarDadosDeBaja() throws SQLException {
            StringBuilder res = new StringBuilder();
            for (Enemigo aux : dao.listarBajas()) {
                res.append(mostrar(aux));
            }
            return res.toString();
        }

        public String mostrar(Enemigo e) {
            return "[" + e.getId() + "] " + e.getNombre()
                    + " | Tipo: " + e.getClass().getSimpleName()
                    + " | Salud máx: " + e.getSaludMaxima()
                    + " | Daño: " + e.getDanioAtaque()
                    + " | Esquivable: " + (e.isEsquivable() ? "sí" : "no") + "\n";
        }

}
