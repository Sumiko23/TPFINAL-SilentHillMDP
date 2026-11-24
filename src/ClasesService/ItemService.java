package ClasesService;

import ClasesDAO.ItemDAO;
import ClasesPOJO.Items.Item;
import ClasesPOJO.Items.TipoItem;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemService {



    private List<Item> lista;
    private final ItemDAO dao;

    public ItemService() throws SQLException {
        this.dao = new ItemDAO();
        this.lista = new ArrayList<>(dao.listar());
    }

    public boolean agregar(Item aux) throws SQLException {
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
            Iterator<Item> it = lista.iterator();
            while (it.hasNext()) {
                Item aux = it.next();
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
            Item aux = dao.buscarPorId(id);
            if (aux != null) {
                lista.add(aux);
            }
        }
        return seReactivo;
    }

    /** Modificación. Devuelve false si el nombre nuevo ya lo usa otro ítem o si no existe el id. */
    public boolean modificar(int id, Item nuevoItem) throws SQLException {
        Item existente = dao.buscarPorNombre(nuevoItem.getNombre());
        if (existente != null && existente.getId() != id) {
            return false;
        }
        nuevoItem.setId(id);
        boolean seModifico = dao.modificar(nuevoItem);

        if (seModifico) {
            // Se vuelve a cargar la lista desde la base para que memoria y base queden idénticas.
            this.lista = new ArrayList<>(dao.listar());
        }
        return seModifico;
    }

    public Item buscarPorIdBD(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public Item buscarPorNombreBD(String nombre) throws SQLException {
        return dao.buscarPorNombre(nombre);
    }


    public Item buscarPorNombreMemoria(String nombre) {
        for (Item aux : lista) {
            if (aux.getNombre().equalsIgnoreCase(nombre.trim())) {
                return aux;
            }
        }
        return null;
    }

    public List<Item> listarPorTipo(TipoItem tipo) throws SQLException {
        return dao.listarPorTipo(tipo);
    }


    public String mostrarListado() {
        if (lista.isEmpty()) {
            return "La lista de Items activos esta vacia .";
        }
        StringBuilder res = new StringBuilder();
        for (Item aux : lista) {
            res.append(mostrar(aux));
        }
        return res.toString();
    }


    public String mostrarDadosDeBaja() throws SQLException {
        List<Item> bajas = dao.listarBajas();
        if (bajas.isEmpty()) {
            return "No hay Items dados de baja.";
        }

        StringBuilder res = new StringBuilder();
        for (Item aux : bajas) {
            res.append(mostrar(aux));
        }
        return res.toString();
    }

    public String mostrar(Item i) {
        return "[" + i.getId() + "] " + i.getNombre()
                + " | Tipo: " + i.getTipo()
                + " | Curación: " + i.getEfectoCuracion()
                + " | Daño: " + i.getDanio()
                + " | Cantidad de Balas " + i.getCantidadBalas()
                + " | " + i.getDescripcion()  +  "\n";
    }





}
