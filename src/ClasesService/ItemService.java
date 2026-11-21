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

    /** Alta. Devuelve false si ya existe un ítem con ese nombre o si no se pudo insertar. */
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

    /** Baja lógica: queda en la base con activo = false y sale de la lista en memoria. */
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

    /** Reactiva un ítem dado de baja y lo vuelve a cargar en memoria. */
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

    public Item buscarPorIdMemoria(int id) {
        for (Item aux : lista) {
            if (aux.getId() == id) {
                return aux;
            }
        }
        return null;
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
        StringBuilder res = new StringBuilder();
        for (Item aux : lista) {
            res.append(mostrar(aux));
        }
        return res.toString();
    }


    public String mostrarDadosDeBaja() throws SQLException {
        StringBuilder res = new StringBuilder();
        for (Item aux : dao.listarBajas()) {
            res.append(mostrar(aux));
        }
        return res.toString();
    }

    public String mostrar(Item i) {
        return "[" + i.getId() + "] " + i.getNombre()
                + " | Tipo: " + i.getTipo()
                + " | Curación: " + i.getEfectoCuracion()
                + " | Daño: " + i.getDanio()
                + " | " + i.getDescripcion() + "\n";
    }





}
