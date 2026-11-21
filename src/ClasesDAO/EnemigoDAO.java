package ClasesDAO;
import ClasesPOJO.Enemigos.Enemigo;
import ClasesPOJO.Enemigos.EnemigoComun;
import ClasesPOJO.Enemigos.JefeFinal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnemigoDAO {

    // insertar enemigo en la BD
    public int insertar(Enemigo enemigo) throws SQLException {
        String sqlInsert = "INSERT INTO enemigos (nombre, salud_maxima, danio_ataque, esquivable, "
                + "es_jefe_final, tiempo_limite_ms, activo) VALUES (?,?,?,?,?,?,?)";
        String sqlGetId = "SELECT LAST_INSERT_ID()";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement psInsert = con.prepareStatement(sqlInsert);
             PreparedStatement psId = con.prepareStatement(sqlGetId)) {

            cargarParametros(psInsert, enemigo);
            psInsert.executeUpdate();

            try (ResultSet rs = psId.executeQuery()) {
                if (rs.next()) {
                    int ultimoId = rs.getInt(1);
                    enemigo.setId(ultimoId);
                    return ultimoId;
                }
            }
        }
        return -1;
    }

    // listar enemigos activos
    public List<Enemigo> listar() throws SQLException {
        return listarPorEstado(true);
    }

    // listar enemigos dados de baja
    public List<Enemigo> listarBajas() throws SQLException {
        return listarPorEstado(false);
    }

    private List<Enemigo> listarPorEstado(boolean activo) throws SQLException {
        List<Enemigo> enemigos = new ArrayList<>();
        String sql = "SELECT id, nombre, salud_maxima, danio_ataque, esquivable, es_jefe_final, "
                + "tiempo_limite_ms, activo FROM enemigos WHERE activo = ? ORDER BY id";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    enemigos.add(mapear(rs));
                }
            }
        }
        return enemigos;
    }

    // buscar enemigo por ID
    public Enemigo buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, salud_maxima, danio_ataque, esquivable, es_jefe_final, "
                + "tiempo_limite_ms, activo FROM enemigos WHERE id = ?";

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

    // buscar enemigo por nombre (es UNIQUE en la BD)
    public Enemigo buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT id, nombre, salud_maxima, danio_ataque, esquivable, es_jefe_final, "
                + "tiempo_limite_ms, activo FROM enemigos WHERE nombre = ?";

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

    // modificar enemigo
    public boolean modificar(Enemigo enemigo) throws SQLException {
        String sql = "UPDATE enemigos SET nombre = ?, salud_maxima = ?, danio_ataque = ?, esquivable = ?, "
                + "es_jefe_final = ?, tiempo_limite_ms = ?, activo = ? WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            cargarParametros(ps, enemigo);
            ps.setInt(8, enemigo.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // baja lógica
    public boolean eliminar(int id) throws SQLException {
        return cambiarEstado(id, false);
    }

    // reactivar enemigo dado de baja
    public boolean reactivar(int id) throws SQLException {
        return cambiarEstado(id, true);
    }

    private boolean cambiarEstado(int id, boolean activo) throws SQLException {
        String sql = "UPDATE enemigos SET activo = ? WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        }
    }

    // fila -> EnemigoComun o JefeFinal según es_jefe_final
    private Enemigo mapear(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        int saludMaxima = rs.getInt("salud_maxima");
        int danio = rs.getInt("danio_ataque");
        boolean esquivable = rs.getBoolean("esquivable");
        boolean activo = rs.getBoolean("activo");

        if (rs.getBoolean("es_jefe_final")) {
            return new JefeFinal(id, nombre, saludMaxima, danio, esquivable, activo,
                    rs.getLong("tiempo_limite_ms"));
        }
        return new EnemigoComun(id, nombre, saludMaxima, danio, esquivable, activo);
    }

    // orden: nombre, salud_maxima, danio_ataque, esquivable, es_jefe_final, tiempo_limite_ms, activo
    private void cargarParametros(PreparedStatement ps, Enemigo e) throws SQLException {
        boolean esJefe = e instanceof JefeFinal;

        ps.setString(1, e.getNombre());
        ps.setInt(2, e.getSaludMaxima());
        ps.setInt(3, e.getDanioAtaque());
        ps.setBoolean(4, e.isEsquivable());
        ps.setBoolean(5, esJefe);
        ps.setLong(6, esJefe ? ((JefeFinal) e).getTiempoLimiteMs() : 0L);
        ps.setBoolean(7, e.isActivo());
    }
}