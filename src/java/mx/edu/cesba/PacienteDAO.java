
package mx.edu.cesba;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;





public class PacienteDAO {

    private static final String COLUMNAS =
            "id, nombre, apellido_paterno, apellido_materno, " +
            "fecha_nacimiento, sexo, telefono, correo, domicilio, " +
            "contacto_emergencia, telefono_emergencia, activo";

    // =========================================================
    // LISTAR (con búsqueda opcional)
    // =========================================================
    public List<Paciente> listar(String filtro, boolean activos) {

        List<Paciente> lista = new ArrayList<>();

        String sql =
                "SELECT " + COLUMNAS + " FROM pacientes " +
                "WHERE activo = ? AND (" +
                "nombre LIKE ? OR apellido_paterno LIKE ? OR " +
                "apellido_materno LIKE ? OR telefono LIKE ?) " +
                "ORDER BY apellido_paterno, apellido_materno, nombre";

        String patron = "%" + (filtro == null ? "" : filtro.trim()) + "%";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setBoolean(1, activos);
            ps.setString(2, patron);
            ps.setString(3, patron);
            ps.setString(4, patron);
            ps.setString(5, patron);

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

    // =========================================================
    // BUSCAR POR ID
    // =========================================================
    public Paciente buscarPorId(int id) {

        String sql = "SELECT " + COLUMNAS + " FROM pacientes WHERE id = ?";

        try (Connection conexion = Conexion.conectar();
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

    // =========================================================
    // INSERTAR
    // =========================================================
    public boolean insertar(Paciente p) {

        String sql =
                "INSERT INTO pacientes " +
                "(nombre, apellido_paterno, apellido_materno, " +
                "fecha_nacimiento, sexo, telefono, correo, domicilio, " +
                "contacto_emergencia, telefono_emergencia) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            asignarCampos(ps, p);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================
    // ACTUALIZAR
    // =========================================================
    public boolean actualizar(Paciente p) {

        String sql =
                "UPDATE pacientes SET " +
                "nombre = ?, apellido_paterno = ?, apellido_materno = ?, " +
                "fecha_nacimiento = ?, sexo = ?, telefono = ?, correo = ?, " +
                "domicilio = ?, contacto_emergencia = ?, " +
                "telefono_emergencia = ? " +
                "WHERE id = ?";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            asignarCampos(ps, p);
            ps.setInt(11, p.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================
    // DESACTIVAR / REACTIVAR (no se borra al paciente)
    // =========================================================
    public boolean cambiarEstado(int id, boolean activo) {

        String sql = "UPDATE pacientes SET activo = ? WHERE id = ?";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================
    // MÉTODOS AUXILIARES
    // =========================================================
    private void asignarCampos(PreparedStatement ps, Paciente p)
            throws SQLException {

        ps.setString(1, p.getNombre());
        ps.setString(2, p.getApellidoPaterno());
        ps.setString(3, vacioANull(p.getApellidoMaterno()));

        if (p.getFechaNacimiento() == null) {
            ps.setNull(4, Types.DATE);
        }else {
            ps.setDate(4, Date.valueOf(p.getFechaNacimiento()));
        }

        ps.setString(5, vacioANull(p.getSexo()));
        ps.setString(6, vacioANull(p.getTelefono()));
        ps.setString(7, vacioANull(p.getCorreo()));
        ps.setString(8, vacioANull(p.getDomicilio()));
        ps.setString(9, vacioANull(p.getContactoEmergencia()));
        ps.setString(10, vacioANull(p.getTelefonoEmergencia()));
    }
    
    private String vacioANull(String texto) {
        if (texto == null || texto.trim().isEmpty()){
            return null;
        }
        return texto.trim();
    }

    private Paciente mapear(ResultSet rs) throws SQLException {

        Paciente p = new Paciente();

        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setApellidoPaterno(rs.getString("apellido_paterno"));
        p.setApellidoMaterno(rs.getString("apellido_materno"));

        Date fecha = rs.getDate("fecha_nacimiento");
        if (fecha != null) {
            p.setFechaNacimiento(fecha.toLocalDate());
        }

        p.setSexo(rs.getString("sexo"));
        p.setTelefono(rs.getString("telefono"));
        p.setCorreo(rs.getString("correo"));
        p.setDomicilio(rs.getString("domicilio"));
        p.setContactoEmergencia(rs.getString("contacto_emergencia"));
        p.setTelefonoEmergencia(rs.getString("telefono_emergencia"));
        p.setActivo(rs.getBoolean("activo"));

        return p;
    }

    
}
