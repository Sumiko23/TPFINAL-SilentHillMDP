package ClasesDAO;

import ClasesPOJO.Items.ArmaDeFuego;
import ClasesPOJO.Items.Item;
import ClasesPOJO.Items.TipoItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    private static final String SELECT =
            "SELECT id, nombre, descripcion, tipo, efecto_curacion, capacidad_cargador, danio, activo FROM items";

    // insertar item en la BD
    public int insertar(Item item) throws SQLException {
        String sqlInsert = "INSERT INTO items (nombre, descripcion, tipo, efecto_curacion, danio, capacidad_cargador, activo) "
                + "VALUES (?,?,?,?,?,?,?)";
        String sqlGetId = "SELECT LAST_INSERT_ID()";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement psInsert = con.prepareStatement(sqlInsert);
             PreparedStatement psId = con.prepareStatement(sqlGetId)) {

            psInsert.setString(1, item.getNombre());
            psInsert.setString(2, item.getDescripcion());
            psInsert.setString(3, item.getTipo().name());
            psInsert.setInt(4, item.getEfectoCuracion());
            psInsert.setInt(5, item.getDanio());
            psInsert.setInt(6, balasDe(item));
            psInsert.setBoolean(7, item.isActivo());

            psInsert.executeUpdate();

            try (ResultSet rs = psId.executeQuery()) {
                if (rs.next()) {
                    int ultimoId = rs.getInt(1);
                    item.setId(ultimoId);
                    return ultimoId;
                }
            }
        }
        return -1;
    }

    // listar items activos
    public List<Item> listar() throws SQLException {
        return listarPorEstado(true);
    }

    // listar items dados de baja
    public List<Item> listarBajas() throws SQLException {
        return listarPorEstado(false);
    }

    private List<Item> listarPorEstado(boolean activo) throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = SELECT + " WHERE activo = ? ORDER BY id";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapear(rs));
                }
            }
        }
        return items;
    }

    // listar items por tipo (ARMA, ARMA_FUEGO, CONSUMIBLE, NOTA, LLAVE_MISION)
    public List<Item> listarPorTipo(TipoItem tipo) throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = SELECT + " WHERE tipo = ? AND activo = TRUE ORDER BY id";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tipo.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapear(rs));
                }
            }
        }
        return items;
    }

    // buscar item por ID
    public Item buscarPorId(int id) throws SQLException {
        String sql = SELECT + " WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    // buscar item por nombre (es UNIQUE en la BD)
    public Item buscarPorNombre(String nombre) throws SQLException {
        String sql = SELECT + " WHERE nombre = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    // modificar item
    public boolean modificar(Item item) throws SQLException {
        String sql = "UPDATE items SET nombre = ?, descripcion = ?, tipo = ?, efecto_curacion = ?, "
                + "danio = ?, capacidad_cargador = ?, activo = ? WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, item.getNombre());
            ps.setString(2, item.getDescripcion());
            ps.setString(3, item.getTipo().name());
            ps.setInt(4, item.getEfectoCuracion());
            ps.setInt(5, item.getDanio());
            ps.setInt(6, balasDe(item));
            ps.setBoolean(7, item.isActivo());
            ps.setInt(8, item.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // baja lógica
    public boolean eliminar(int id) throws SQLException {
        return cambiarEstado(id, false);
    }

    // reactivar item dado de baja
    public boolean reactivar(int id) throws SQLException {
        return cambiarEstado(id, true);
    }

    private boolean cambiarEstado(int id, boolean activo) throws SQLException {
        String sql = "UPDATE items SET activo = ? WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        }
    }

    // valor que se guarda en capacidad_cargador: la capacidad si es arma de fuego, si no cantidadBalas
    private int balasDe(Item item) {
        if (item instanceof ArmaDeFuego af) {
            return af.getCapacidadCargador();
        }
        return item.getCantidadBalas();
    }

    private Item mapear(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        String descripcion = rs.getString("descripcion");
        TipoItem tipo = TipoItem.valueOf(rs.getString("tipo"));
        int curacion = rs.getInt("efecto_curacion");
        int balas = rs.getInt("capacidad_cargador");
        int danio = rs.getInt("danio");
        boolean activo = rs.getBoolean("activo");

        if (tipo == TipoItem.ARMA_FUEGO) {
            // Cargador lleno y sin cajas; JugadorDAO restaura después la munición guardada del jugador
            return new ArmaDeFuego(id, nombre, descripcion, danio, balas, balas, activo);
        }
        return new Item(id, nombre, descripcion, tipo, curacion, balas, danio, activo);
    }
}
