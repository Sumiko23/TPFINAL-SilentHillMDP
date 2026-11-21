package ClasesDAO;

import ClasesPOJO.Escena;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class EscenaDAO {

    // insertar escena en la BD
    public int insertar(Escena escena) throws SQLException {
        String sqlInsert = "INSERT INTO escenas (nombre, descripcion, activo) VALUES (?,?,?)";
        String sqlGetId = "SELECT LAST_INSERT_ID()";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement psInsert = con.prepareStatement(sqlInsert);
             PreparedStatement psId = con.prepareStatement(sqlGetId)) {

            psInsert.setString(1, escena.getNombre());
            psInsert.setString(2, escena.getDescripcion());
            psInsert.setBoolean(3, escena.isActiva());

            psInsert.executeUpdate();

            try (ResultSet rs = psId.executeQuery()) {
                if (rs.next()) {
                    int ultimoId = rs.getInt(1);
                    escena.setId(ultimoId);
                    return ultimoId;
                }
            }
        }
        return -1;
    }

    // listar escenas activas
    public List<Escena> listar() throws SQLException {
        return listarPorEstado(true);
    }

    // listar escenas dadas de baja
    public List<Escena> listarBajas() throws SQLException {
        return listarPorEstado(false);
    }

    private List<Escena> listarPorEstado(boolean activa) throws SQLException {
        List<Escena> escenas = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, activo FROM escenas WHERE activo = ? ORDER BY id";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activa);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    escenas.add(mapear(rs));
                }
            }
        }
        return escenas;
    }

    // buscar escena por ID
    public Escena buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, descripcion, activo FROM escenas WHERE id = ?";

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

    // buscar escena por nombre (es UNIQUE en la BD)
    public Escena buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT id, nombre, descripcion, activo FROM escenas WHERE nombre = ?";

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

    // modificar escena
    public boolean modificar(Escena escena) throws SQLException {
        String sql = "UPDATE escenas SET nombre = ?, descripcion = ?, activo = ? WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, escena.getNombre());
            ps.setString(2, escena.getDescripcion());
            ps.setBoolean(3, escena.isActiva());
            ps.setInt(4, escena.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // baja lógica
    public boolean eliminar(int id) throws SQLException {
        return cambiarEstado(id, false);
    }

    // reactivar escena dada de baja
    public boolean reactivar(int id) throws SQLException {
        return cambiarEstado(id, true);
    }

    private boolean cambiarEstado(int id, boolean activa) throws SQLException {
        String sql = "UPDATE escenas SET activo = ? WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activa);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        }
    }

    private Escena mapear(ResultSet rs) throws SQLException {
        return new Escena(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getBoolean("activo")
        );
    }
}
