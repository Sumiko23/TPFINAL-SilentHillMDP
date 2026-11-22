package ClasesService;

import ClasesDAO.JugadorDAO;
import ClasesPOJO.Jugador;
import Excepciones.UsuarioDuplicadoException;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class JugadorService {


    private Set<Jugador> lista;
    private final JugadorDAO dao;

    public JugadorService() throws SQLException {
        this.dao = new JugadorDAO();
        this.lista = new LinkedHashSet<>(dao.listar());
    }

    /// Devuelve los jugadores activos que están en memoria (para "Continuar partida")
    public Set<Jugador> listarActivos() {
        return lista;
    }


    /// Alta de un jugador nuevo (sirve para "Iniciar partida nueva" y para el alta del administrador).
    ///  Se valida el nombre contra la base de datos, que es la que tiene todos los jugadores (activos y de baja).

    public boolean agregar(Jugador aux) throws SQLException, UsuarioDuplicadoException {
        if (dao.buscarPorNombre(aux.getNombre()) != null) {
            throw new UsuarioDuplicadoException();
        }
        int id = dao.insertar(aux);
        if (id > 0) {
            lista.add(aux);
            return true;
        }
        return false;
    }

    /** Baja lógica: el registro queda en la base con activo = false y sale de la lista en memoria. */
    public boolean eliminar(int id) throws SQLException {
        boolean seElimino = dao.eliminar(id);
        if (seElimino) {
            Iterator<Jugador> it = lista.iterator();
            while (it.hasNext()) {
                Jugador aux = it.next();
                if (aux.getId() == id) {
                    it.remove();
                    break;
                }
            }
        }
        return seElimino;
    }


    /** Modificación hecha por el administrador. No permite dejarle a un jugador el nombre de otro. */
    public boolean modificar(int id, Jugador nuevoJugador) throws SQLException, UsuarioDuplicadoException {
        Jugador existente = dao.buscarPorNombre(nuevoJugador.getNombre());
        if (existente != null && existente.getId() != id) {
            throw new UsuarioDuplicadoException();
        }
        nuevoJugador.setId(id);
        boolean seModifico = dao.modificar(nuevoJugador);

        if (seModifico) {

            this.lista = new LinkedHashSet<>(dao.listar());
        }
        return seModifico;
    }

    /** Mostrar ugadores activos (lista en memoria). */
    public String mostrarListado() {
        if (lista.isEmpty()) {
            return "La lista de jugadores activos esta vacia .";
        }
        StringBuilder res = new StringBuilder();
        for (Jugador aux : lista) {
            res.append(mostrar(aux));
        }
        return res.toString();
    }


    /** Reactiva un jugador dado de baja y lo vuelve a cargar en la lista en memoria. */
    public boolean reactivar(int id) throws SQLException {
        boolean seReactivo = dao.reactivar(id);
        if (seReactivo) {
            Jugador aux = dao.buscarPorId(id);
            if (aux != null) {
                lista.add(aux);
            }
        }
        return seReactivo;
    }



    /**
     * Guarda el progreso de la partida (se usa cuando el jugador toca '0' y vuelve al menú principal).
     * Devuelve false si el jugador no existe en la base.
     */
    public boolean guardarPartida(Jugador jugador) throws SQLException {
        return dao.modificar(jugador);
    }

    public Jugador buscarPorIdBD(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public Jugador buscarPorNombreBD(String nombre) throws SQLException {
        return dao.buscarPorNombre(nombre);
    }

    public Jugador buscarPorIdMemoria(int id) {
        for (Jugador aux : lista) {
            if (aux.getId() == id) {
                return aux;
            }
        }
        return null;
    }



    public String mostrarDadosDeBaja() throws SQLException {
        List<Jugador> bajas = dao.listarBajas();
        if (bajas.isEmpty()) {
            return "No hay jugadores dados de baja.";
        }

        StringBuilder res = new StringBuilder();
        for (Jugador aux : bajas) {
            res.append(mostrar(aux));
        }
        return res.toString();
    }

    public String mostrar(Jugador j) {
        return "[" + j.getId() + "] " + j.getNombre()
                + " | Escena: " + j.getEscenaActual()
                + " | Salud: " + j.getSalud()
                + " | Monstruos muertos: " + j.getMonstruosMuertos()
                + " | Ítems: " + j.getItemsObtenidos() + "\n";
    }






}
