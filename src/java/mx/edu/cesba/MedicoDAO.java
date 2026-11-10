package mx.edu.cesba;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class MedicoDAO {

    private static final String SELECT_BASE =
            "SELECT m.id, m.usuario_id, m.cedula_profesional, m.nombre, " +
            "m.apellido_paterno, m.apellido_materno, m.especialidad, " +
            "m.telefono, m.correo, m.activo, u.usuario AS cuenta " +
            "FROM medicos m " +
            "LEFT JOIN usuarios u ON u.id = m.usuario_id ";

    // =========================================================================
    // LISTAR (con búsqueda opcional)
    // =========================================================================
    public List<Medico> listar(String filtro, boolean activos) {

        List<Medico> lista = new ArrayList<>();

        String sql = SELECT_BASE +
                "WHERE m.activo = ? AND (" +
                "m.nombre LIKE ? OR m.apellido_paterno LIKE ? OR " +
                "m.apellido_materno LIKE ? OR m.cedula_profesional LIKE ? OR " +
                "m.especialidad LIKE ?) " +
                "ORDER BY m.apellido_paterno, m.apellido_materno, m.nombre";

        String patron = "%" + (filtro == null ? "" : filtro.trim()) + "%";

        try (Connection conexion = abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setBoolean(1, activos);
            ps.setString(2, patron);
            ps.setString(3, patron);
            ps.setString(4, patron);
            ps.setString(5, patron);
            ps.setString(6, patron);

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
    public Medico buscarPorId(int id) {

        String sql = SELECT_BASE + "WHERE m.id = ?";

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
    // Lanza SQLException para que el servlet pueda distinguir,
    // por ejemplo, una cédula repetida (código 1062).
    // =========================================================================
    public boolean insertar(Medico m) throws SQLException {

        String sql =
                "INSERT INTO medicos " +
                "(usuario_id, cedula_profesional, nombre, apellido_paterno, " +
                "apellido_materno, especialidad, telefono, correo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            asignarCampos(ps, m);

            return ps.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // ACTUALIZAR
    // =========================================================================
    public boolean actualizar(Medico m) throws SQLException {

        String sql =
                "UPDATE medicos SET " +
                "usuario_id = ?, cedula_profesional = ?, nombre = ?, " +
                "apellido_paterno = ?, apellido_materno = ?, " +
                "especialidad = ?, telefono = ?, correo = ? " +
                "WHERE id = ?";

        try (Connection conexion = abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            asignarCampos(ps, m);
            ps.setInt(9, m.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // DESACTIVAR / REACTIVAR (no se borra al médico)
    // =========================================================================
    public boolean cambiarEstado(int id, boolean activo) {

        String sql = "UPDATE medicos SET activo = ? WHERE id = ?";

        try (Connection conexion = abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================================
    // CUENTAS DE ACCESO DISPONIBLES PARA VINCULAR
    // Usuarios activos con rol MEDICO que no estén vinculados a otro
    // médico, más la cuenta que este médico ya tiene (si la tiene).
    // =========================================================================
    public List<Usuario> listarCuentasDisponibles(int medicoId) {

        List<Usuario> lista = new ArrayList<>();

        String sql =
                "SELECT u.id, u.usuario, u.nombre FROM usuarios u " +
                "WHERE (u.activo = 1 AND u.rol = 'MEDICO' " +
                "AND NOT EXISTS (SELECT 1 FROM medicos m " +
                "WHERE m.usuario_id = u.id AND m.id <> ?)) " +
                "OR u.id = (SELECT usuario_id FROM medicos WHERE id = ?) " +
                "ORDER BY u.usuario";

        try (Connection conexion = abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, medicoId);
            ps.setInt(2, medicoId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setUsuario(rs.getString("usuario"));
                    u.setNombre(rs.getString("nombre"));
                    lista.add(u);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
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

    private void asignarCampos(PreparedStatement ps, Medico m)
            throws SQLException {

        if (m.getUsuarioId() == null) {
            ps.setNull(1, Types.INTEGER);
        } else {
            ps.setInt(1, m.getUsuarioId());
        }

        ps.setString(2, m.getCedulaProfesional());
        ps.setString(3, m.getNombre());
        ps.setString(4, m.getApellidoPaterno());
        ps.setString(5, vacioANull(m.getApellidoMaterno()));
        ps.setString(6, m.getEspecialidad());
        ps.setString(7, vacioANull(m.getTelefono()));
        ps.setString(8, vacioANull(m.getCorreo()));
    }

    private String vacioANull(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        return texto.trim();
    }

    private Medico mapear(ResultSet rs) throws SQLException {

        Medico m = new Medico();

        m.setId(rs.getInt("id"));

        int usuarioId = rs.getInt("usuario_id");
        m.setUsuarioId(rs.wasNull() ? null : usuarioId);

        m.setCedulaProfesional(rs.getString("cedula_profesional"));
        m.setNombre(rs.getString("nombre"));
        m.setApellidoPaterno(rs.getString("apellido_paterno"));
        m.setApellidoMaterno(rs.getString("apellido_materno"));
        m.setEspecialidad(rs.getString("especialidad"));
        m.setTelefono(rs.getString("telefono"));
        m.setCorreo(rs.getString("correo"));
        m.setActivo(rs.getBoolean("activo"));
        m.setCuenta(rs.getString("cuenta"));

        return m;
    }

}
