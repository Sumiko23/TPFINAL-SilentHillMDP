package ClasesDAO;

import ClasesPOJO.Jugador;
import ClasesPOJO.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // insertar usuario nuevo (el resto de columnas de jugadores queda con sus DEFAULT)
    public int insertar(Usuario usuario) throws SQLException {
        String sqlInsert = "INSERT INTO jugadores (nombre, activo) VALUES (?,?)";
        String sqlGetId = "SELECT LAST_INSERT_ID()";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement psInsert = con.prepareStatement(sqlInsert);
             PreparedStatement psId = con.prepareStatement(sqlGetId)) {

            psInsert.setString(1, usuario.getNombre());
            psInsert.setBoolean(2, usuario.isActivo());

            psInsert.executeUpdate();

            try (ResultSet rs = psId.executeQuery()) {
                if (rs.next()) {
                    int ultimoId = rs.getInt(1);
                    usuario.setId(ultimoId);
                    return ultimoId;
                }
            }
        }
        return -1;
    }

    // listar usuarios activos
    public List<Usuario> listar() throws SQLException {
        return listarPorEstado(true);
    }

    // listar usuarios dados de baja
    public List<Usuario> listarBajas() throws SQLException {
        return listarPorEstado(false);
    }

    private List<Usuario> listarPorEstado(boolean activo) throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nombre, activo FROM jugadores WHERE activo = ? ORDER BY id";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapear(rs));
                }
            }
        }
        return usuarios;
    }

    // buscar usuario por ID
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, activo FROM jugadores WHERE id = ?";

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

    // buscar usuario por nombre (es UNIQUE en la BD)
    public Usuario buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT id, nombre, activo FROM jugadores WHERE nombre = ?";

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

    // para validar nombre duplicado al crear partida nueva
    public boolean existeNombre(String nombre) throws SQLException {
        String sql = "SELECT 1 FROM jugadores WHERE nombre = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // modificar datos de cuenta (no toca el progreso de la partida)
    public boolean modificar(Usuario usuario) throws SQLException {
        String sql = "UPDATE jugadores SET nombre = ?, activo = ? WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setBoolean(2, usuario.isActivo());
            ps.setInt(3, usuario.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // baja lógica
    public boolean eliminar(int id) throws SQLException {
        return cambiarEstado(id, false);
    }

    // reactivar usuario dado de baja
    public boolean reactivar(int id) throws SQLException {
        return cambiarEstado(id, true);
    }

    private boolean cambiarEstado(int id, boolean activo) throws SQLException {
        String sql = "UPDATE jugadores SET activo = ? WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        }
    }

    // Usuario es abstracta: se devuelve Jugador (solo con id, nombre y activo)
    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Jugador(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getBoolean("activo")
        );
    }
}
