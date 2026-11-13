package mx.edu.cesba;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {

    private static final String SELECT_BASE =
            "SELECT c.id, c.paciente_id, c.medico_id, c.fecha, c.hora, " +
            "c.motivo, c.estado, c.observaciones, " +
            "CONCAT_WS(' ', p.apellido_paterno, p.apellido_materno, p.nombre) AS paciente, " +
            "CONCAT_WS(' ', m.apellido_paterno, m.apellido_materno, m.nombre) AS medico " +
            "FROM citas c " +
            "JOIN pacientes p ON p.id = c.paciente_id " +
            "JOIN medicos m ON m.id = c.medico_id ";

    // =========================================================================
    // LISTAR las citas de un día (con filtros opcionales)
    // =========================================================================
    public List<Cita> listar(LocalDate fecha, Integer medicoId, String estado) {

        List<Cita> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder(SELECT_BASE);
        sql.append("WHERE c.fecha = ? ");

        if (medicoId != null) {
            sql.append("AND c.medico_id = ? ");
        }
        if (estado != null && !estado.isEmpty()) {
            sql.append("AND c.estado = ? ");
        }

        sql.append("ORDER BY c.hora, m.apellido_paterno, p.apellido_paterno");

        try (Connection conexion = abrir();
                PreparedStatement ps = conexion.prepareStatement(sql.toString())) {

            int i = 1;
            ps.setDate(i++, Date.valueOf(fecha));

            if (medicoId != null) {
                ps.setInt(i++, medicoId);
            }
            if (estado != null && !estado.isEmpty()) {
                ps.setString(i++, estado);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // =========================================================================
    // BUSCAR POR ID
    // =========================================================================
    public Cita buscarPorId(int id) {

        String sql = SELECT_BASE + "WHERE c.id = ?";

        try (Connection conexion = abrir();
                PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // =========================================================================
    // INSERTAR
    // usuarioLogin es el usuario que agenda la cita (queda en creado_por).
    // Lanza SQLException para poder detectar un horario ya ocupado.
    // =========================================================================
    public boolean insertar(Cita c, String usuarioLogin) throws SQLException {

        String sql =
                "INSERT INTO citas " +
                "(paciente_id, medico_id, fecha, hora, motivo, estado, " +
                "observaciones, creado_por) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, " +
                "(SELECT id FROM usuarios WHERE usuario = ?))";

        try (Connection conexion = abrir();
                PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, c.getPacienteId());
            ps.setInt(2, c.getMedicoId());
            ps.setDate(3, Date.valueOf(c.getFecha()));
            ps.setTime(4, Time.valueOf(c.getHora()));
            ps.setString(5, vacioANull(c.getMotivo()));
            ps.setString(6, c.getEstado());
            ps.setString(7, vacioANull(c.getObservaciones()));
            ps.setString(8, usuarioLogin);

            return ps.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // ACTUALIZAR
    // =========================================================================
    public boolean actualizar(Cita c) throws SQLException {

        String sql =
                "UPDATE citas SET " +
                "paciente_id = ?, medico_id = ?, fecha = ?, hora = ?, " +
                "motivo = ?, estado = ?, observaciones = ? " +
                "WHERE id = ?";

        try (Connection conexion = abrir();
                PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, c.getPacienteId());
            ps.setInt(2, c.getMedicoId());
            ps.setDate(3, Date.valueOf(c.getFecha()));
            ps.setTime(4, Time.valueOf(c.getHora()));
            ps.setString(5, vacioANull(c.getMotivo()));
            ps.setString(6, c.getEstado());
            ps.setString(7, vacioANull(c.getObservaciones()));
            ps.setInt(8, c.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // CANCELAR (solo citas programadas o confirmadas)
    // Al quedar en CANCELADA, el horario del médico se libera.
    // =========================================================================
    public boolean cancelar(int id) {

        String sql =
                "UPDATE citas SET estado = 'CANCELADA' " +
                "WHERE id = ? AND estado IN ('PROGRAMADA', 'CONFIRMADA')";

        try (Connection conexion = abrir();
                PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================================
    // CONFIRMAR (solo citas programadas)
    // =========================================================================
    public boolean confirmar(int id) {

        String sql =
                "UPDATE citas SET estado = 'CONFIRMADA' " +
                "WHERE id = ? AND estado = 'PROGRAMADA'";

        try (Connection conexion = abrir();
                PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================================
    // ¿El paciente ya tiene otra cita activa a esa misma hora?
    // (excluirId es la cita que se está editando, o 0 si es nueva)
    // =========================================================================
    public boolean pacienteOcupado(int pacienteId, LocalDate fecha,
            LocalTime hora, int excluirId) {

        String sql =
                "SELECT COUNT(*) FROM citas " +
                "WHERE paciente_id = ? AND fecha = ? AND hora = ? " +
                "AND estado <> 'CANCELADA' AND id <> ?";

        try (Connection conexion = abrir();
                PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, pacienteId);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setTime(3, Time.valueOf(hora));
            ps.setInt(4, excluirId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================================
    // ID del médico vinculado a la cuenta de acceso (o null)
    // Sirve para mostrarle primero su propia agenda.
    // =========================================================================
    public Integer medicoIdDeUsuario(String usuarioLogin) {

        String sql =
                "SELECT m.id FROM medicos m " +
                "JOIN usuarios u ON u.id = m.usuario_id " +
                "WHERE u.usuario = ? AND m.activo = 1";

        try (Connection conexion = abrir();
                PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, usuarioLogin);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // =========================================================================
    // MÉTODOS AUXILIARES
    // =========================================================================
    private Connection abrir() throws SQLException {
        Connection conexion = Conexion.conectar();
        if (conexion == null) {
            throw new SQLException("No se pudo conectar a la base de datos.");
        }
        return conexion;
    }

    private String vacioANull(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        return texto.trim();
    }

    private Cita mapear(ResultSet rs) throws SQLException {

        Cita c = new Cita();

        c.setId(rs.getInt("id"));
        c.setPacienteId(rs.getInt("paciente_id"));
        c.setMedicoId(rs.getInt("medico_id"));

        Date fecha = rs.getDate("fecha");
        if (fecha != null) {
            c.setFecha(fecha.toLocalDate());
        }

        Time hora = rs.getTime("hora");
        if (hora != null) {
            c.setHora(hora.toLocalTime());
        }

        c.setMotivo(rs.getString("motivo"));
        c.setEstado(rs.getString("estado"));
        c.setObservaciones(rs.getString("observaciones"));
        c.setPaciente(rs.getString("paciente"));
        c.setMedico(rs.getString("medico"));

        return c;
    }

}
    
