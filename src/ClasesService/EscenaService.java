package ClasesService;

import ClasesDAO.EscenaDAO;
import ClasesPOJO.Escena;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EscenaService {



    private Map<String, Escena> lista;
    private final EscenaDAO dao;

    public EscenaService() throws SQLException {
        this.dao = new EscenaDAO();
        this.lista = new LinkedHashMap<>();
        cargarLista();
    }


    private void cargarLista() throws SQLException {
        this.lista = new LinkedHashMap<>();
        for (Escena aux : dao.listar()) {
            lista.put(aux.getNombre(), aux);
        }
    }


    public boolean agregar(Escena aux) throws SQLException {
        if (dao.buscarPorNombre(aux.getNombre()) != null) {
            return false;
        }
        int id = dao.insertar(aux);
        if (id > 0) {
            lista.put(aux.getNombre(), aux);
            return true;
        }
        return false;
    }


    public boolean eliminar(int id) throws SQLException {
        boolean seElimino = dao.eliminar(id);
        if (seElimino) {
            Iterator<Escena> it = lista.values().iterator();
            while (it.hasNext()) {
                Escena aux = it.next();
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
            Escena aux = dao.buscarPorId(id);
            if (aux != null) {
                lista.put(aux.getNombre(), aux);
            }
        }
        return seReactivo;
    }


    public boolean modificar(int id, Escena nuevaEscena) throws SQLException {
        Escena existente = dao.buscarPorNombre(nuevaEscena.getNombre());
        if (existente != null && existente.getId() != id) {
            return false;
        }
        nuevaEscena.setId(id);
        boolean seModifico = dao.modificar(nuevaEscena);

        if (seModifico) {
            cargarLista();
        }
        return seModifico;
    }

    public Escena buscarPorIdBD(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public Escena buscarPorNombreBD(String nombre) throws SQLException {
        return dao.buscarPorNombre(nombre);
    }

    public Escena buscarPorIdMemoria(int id) {
        for (Escena aux : lista.values()) {
            if (aux.getId() == id) {
                return aux;
            }
        }
        return null;
    }


    public Escena buscarPorNombreMemoria(String nombre) {
        return lista.get(nombre);
    }


    public String mostrarListado() {
        if (lista.isEmpty()) {
            return "No hay escenas activas.";
        }
        StringBuilder res = new StringBuilder();
        for (Escena aux : lista.values()) {
            res.append(mostrar(aux));
        }
        return res.toString();
    }


    public String mostrarDadosDeBaja() throws SQLException {
        List<Escena> bajas = dao.listarBajas();
        if (bajas.isEmpty()) {
            return "No hay escenas dadas de baja.";
        }
        StringBuilder res = new StringBuilder();
        for (Escena aux : bajas) {
            res.append(mostrar(aux));
        }
        return res.toString();
    }

    public String mostrar(Escena e) {
        return "[" + e.getId() + "] " + e.getNombre()
                + " | Descripción: " + e.getDescripcion() + "\n";
    }





}
