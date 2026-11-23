package ClasesDAO;

import ClasesPOJO.Items.Item;
import ClasesPOJO.Jugador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JugadorDAO {

    private static final String[] COLS = {
            "nombre", "salud", "monstruos_muertos", "items_obtenidos", "escena_actual", "arma_equipada_id",
            "balas_cargador", "cajas_balas", "depto_visitado", "prefectura_visitada", "barco_visitado",
            "policia2_aparecio", "caja_sae_abierta",
            "banio_visitado", "abrir_cofre_ocurrido", "seguirla_ocurrida", "calle_ocurrida",
            "caja_fuerte_abierta", "sae_puerta_abierta", "segundo_piso_visitado",
            "cloaca_muerto", "policia1_muerto",
            "policia2_muerto", "acuatico_muerto", "lying_figure1_muerto", "policia3_muerto", "policia4_muerto",
            "lying_figure2_muerto", "cloaca2_muerto", "peluche_muerto", "timba_muerto", "lassie_muerto",
            "maniqui_muerto", "estudiante_muerto", "rector_muerto", "activo"
    };

    private static final String SQL_INSERT =
            "INSERT INTO jugadores (" + String.join(", ", COLS) + ") VALUES ("
                    + "?, ".repeat(COLS.length - 1) + "?)";

    private static final String SQL_UPDATE =
            "UPDATE jugadores SET " + String.join(" = ?, ", COLS) + " = ? WHERE id = ?";

    private final ItemDAO itemDAO = new ItemDAO();

    // insertar jugador (con su inventario) en la BD
    public int insertar(Jugador jugador) throws SQLException {
        String sqlGetId = "SELECT LAST_INSERT_ID()";

        try (Connection con = ConexionBD.conectar()) {
            con.setAutoCommit(false);

            try (PreparedStatement psInsert = con.prepareStatement(SQL_INSERT);
                 PreparedStatement psId = con.prepareStatement(sqlGetId)) {

                cargarParametros(psInsert, jugador);
                psInsert.executeUpdate();

                int ultimoId = -1;
                try (ResultSet rs = psId.executeQuery()) {
                    if (rs.next()) {
                        ultimoId = rs.getInt(1);
                        jugador.setId(ultimoId);
                    }
                }

                guardarInventario(con, jugador);
                con.commit();
                return ultimoId;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    // listar jugadores activos
    public List<Jugador> listar() throws SQLException {
        return listarPorEstado(true);
    }

    // listar jugadores dados de baja
    public List<Jugador> listarBajas() throws SQLException {
        return listarPorEstado(false);
    }

    //lista jugadores de la BD segun su estado(true o false):
    private List<Jugador> listarPorEstado(boolean activo) throws SQLException {
        List<Jugador> jugadores = new ArrayList<>();
        String sql = "SELECT * FROM jugadores WHERE activo = ? ORDER BY id";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    jugadores.add(mapear(con, rs));
                }
            }
        }
        return jugadores;
    }

    // buscar jugador por ID
    public Jugador buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM jugadores WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(con, rs);
                }
            }
        }
        return null;
    }

    // buscar jugador por nombre (para "Continuar partida")
    public Jugador buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM jugadores WHERE nombre = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(con, rs);
                }
            }
        }
        return null;
    }

    // modificar jugador (guardar partida) con su inventario
    public boolean modificar(Jugador jugador) throws SQLException {
        try (Connection con = ConexionBD.conectar()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {
                cargarParametros(ps, jugador);
                ps.setInt(COLS.length + 1, jugador.getId());

                if (ps.executeUpdate() == 0) {
                    con.rollback();
                    return false;
                }

                guardarInventario(con, jugador);
                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    // baja lógica
    public boolean eliminar(int id) throws SQLException {
        return cambiarEstado(id, false);
    }

    // reactivar jugador dado de baja
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

    // ---------- inventario ----------

    // reemplaza el inventario guardado por el que tiene el jugador en memoria
    private void guardarInventario(Connection con, Jugador jugador) throws SQLException {
        try (PreparedStatement del = con.prepareStatement(
                "DELETE FROM jugador_inventario WHERE jugador_id = ?")) {
            del.setInt(1, jugador.getId());
            del.executeUpdate();
        }

        // cuenta cuántas veces aparece cada item (cantidad)
        Map<Integer, Integer> cantidades = new LinkedHashMap<>();
        for (Item item : jugador.getInventario().getItems()) {
            cantidades.merge(item.getId(), 1, Integer::sum);
        }
        if (cantidades.isEmpty()) return;

        String sql = "INSERT INTO jugador_inventario (jugador_id, item_id, cantidad) VALUES (?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Map.Entry<Integer, Integer> e : cantidades.entrySet()) {
                ps.setInt(1, jugador.getId());
                ps.setInt(2, e.getKey());
                ps.setInt(3, e.getValue());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void cargarInventario(Connection con, Jugador jugador) throws SQLException {
        Map<Integer, Integer> cantidades = new LinkedHashMap<>();
        String sql = "SELECT item_id, cantidad FROM jugador_inventario WHERE jugador_id = ? ORDER BY item_id";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, jugador.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cantidades.put(rs.getInt("item_id"), rs.getInt("cantidad"));
                }
            }
        }

        // se agrega directo al inventario (no con registrarNuevoItem, para no sumar itemsObtenidos)
        for (Map.Entry<Integer, Integer> e : cantidades.entrySet()) {
            Item item = itemDAO.buscarPorId(e.getKey());
            if (item == null) continue;
            for (int i = 0; i < e.getValue(); i++) {
                jugador.getInventario().agregarItem(item);
            }
        }
    }

    // ---------- mapeo y parámetros ----------

    private Jugador mapear(Connection con, ResultSet rs) throws SQLException {
        Jugador j = new Jugador(rs.getInt("id"), rs.getString("nombre"), rs.getBoolean("activo"));

        j.setSalud(rs.getInt("salud"));
        j.setMonstruosMuertos(rs.getInt("monstruos_muertos"));
        j.setItemsObtenidos(rs.getInt("items_obtenidos"));
        j.setEscenaActual(rs.getString("escena_actual"));
        j.setBalasCargador(rs.getInt("balas_cargador"));
        j.setCajasBalas(rs.getInt("cajas_balas"));

        j.setDeptoVisitado(rs.getBoolean("depto_visitado"));
        j.setPrefecturaVisitada(rs.getBoolean("prefectura_visitada"));
        j.setBarcoVisitado(rs.getBoolean("barco_visitado"));
        j.setPolicia2Aparecio(rs.getBoolean("policia2_aparecio"));
        j.setCajaSaeAbierta(rs.getBoolean("caja_sae_abierta"));

        j.setBanioVisitado(rs.getBoolean("banio_visitado"));
        j.setAbrirCofreOcurrido(rs.getBoolean("abrir_cofre_ocurrido"));
        j.setSeguirlaOcurrida(rs.getBoolean("seguirla_ocurrida"));
        j.setCalleOcurrida(rs.getBoolean("calle_ocurrida"));
        j.setCajaFuerteAbierta(rs.getBoolean("caja_fuerte_abierta"));
        j.setSaePuertaAbierta(rs.getBoolean("sae_puerta_abierta"));
        j.setSegundoPisoVisitado(rs.getBoolean("segundo_piso_visitado"));

        j.setCloacaMuerto(rs.getBoolean("cloaca_muerto"));
        j.setPolicia1Muerto(rs.getBoolean("policia1_muerto"));
        j.setPolicia2Muerto(rs.getBoolean("policia2_muerto"));
        j.setAcuaticoMuerto(rs.getBoolean("acuatico_muerto"));
        j.setLyingFigure1Muerto(rs.getBoolean("lying_figure1_muerto"));
        j.setPolicia3Muerto(rs.getBoolean("policia3_muerto"));
        j.setPolicia4Muerto(rs.getBoolean("policia4_muerto"));
        j.setLyingFigure2Muerto(rs.getBoolean("lying_figure2_muerto"));
        j.setCloaca2Muerto(rs.getBoolean("cloaca2_muerto"));
        j.setPelucheMuerto(rs.getBoolean("peluche_muerto"));
        j.setTimbaMuerto(rs.getBoolean("timba_muerto"));
        j.setLassieMuerto(rs.getBoolean("lassie_muerto"));
        j.setManiquiMuerto(rs.getBoolean("maniqui_muerto"));
        j.setEstudianteMuerto(rs.getBoolean("estudiante_muerto"));
        j.setRectorMuerto(rs.getBoolean("rector_muerto"));

        int armaId = rs.getInt("arma_equipada_id");
        if (!rs.wasNull()) {
            j.setArmaEquipada(itemDAO.buscarPorId(armaId));
            // Si tu ArmaDeFuego tiene setBalasCargador, descomentá:
            // if (j.getArmaEquipada() instanceof ArmaDeFuego af) af.setBalasCargador(j.getBalasCargador());
        }

        cargarInventario(con, j);
        return j;
    }

    // mismo orden que COLS
    private void cargarParametros(PreparedStatement ps, Jugador j) throws SQLException {
        int i = 1;
        ps.setString(i++, j.getNombre());
        ps.setInt(i++, j.getSalud());
        ps.setInt(i++, j.getMonstruosMuertos());
        ps.setInt(i++, j.getItemsObtenidos());
        ps.setString(i++, j.getEscenaActual());
        if (j.getArmaEquipada() != null) ps.setInt(i++, j.getArmaEquipada().getId());
        else ps.setNull(i++, Types.INTEGER);
        ps.setInt(i++, j.getBalasCargador());
        ps.setInt(i++, j.getCajasBalas());
        ps.setBoolean(i++, j.isDeptoVisitado());
        ps.setBoolean(i++, j.isPrefecturaVisitada());
        ps.setBoolean(i++, j.isBarcoVisitado());
        ps.setBoolean(i++, j.isPolicia2Aparecio());
        ps.setBoolean(i++, j.isCajaSaeAbierta());
        ps.setBoolean(i++, j.isBanioVisitado());
        ps.setBoolean(i++, j.isAbrirCofreOcurrido());
        ps.setBoolean(i++, j.isSeguirlaOcurrida());
        ps.setBoolean(i++, j.isCalleOcurrida());
        ps.setBoolean(i++, j.isCajaFuerteAbierta());
        ps.setBoolean(i++, j.isSaePuertaAbierta());
        ps.setBoolean(i++, j.isSegundoPisoVisitado());
        ps.setBoolean(i++, j.isCloacaMuerto());
        ps.setBoolean(i++, j.isPolicia1Muerto());
        ps.setBoolean(i++, j.isPolicia2Muerto());
        ps.setBoolean(i++, j.isAcuaticoMuerto());
        ps.setBoolean(i++, j.isLyingFigure1Muerto());
        ps.setBoolean(i++, j.isPolicia3Muerto());
        ps.setBoolean(i++, j.isPolicia4Muerto());
        ps.setBoolean(i++, j.isLyingFigure2Muerto());
        ps.setBoolean(i++, j.isCloaca2Muerto());
        ps.setBoolean(i++, j.isPelucheMuerto());
        ps.setBoolean(i++, j.isTimbaMuerto());
        ps.setBoolean(i++, j.isLassieMuerto());
        ps.setBoolean(i++, j.isManiquiMuerto());
        ps.setBoolean(i++, j.isEstudianteMuerto());
        ps.setBoolean(i++, j.isRectorMuerto());
        ps.setBoolean(i++, j.isActivo());
    }
}
