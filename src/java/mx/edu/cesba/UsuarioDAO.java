package mx.edu.cesba;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import mx.edu.cesba.Usuario;
//import mx.edu.cesba.Conexion;

import org.mindrot.jbcrypt.BCrypt;

public class UsuarioDAO {
    
    
    
    
    public boolean insertar(Usuario u){
      
        String sql =
                "INSERT INTO usuarios" + 
                "(usuario, password, nombre, rol, activo) " + 
                "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conexion = Conexion.conectar();
             PreparedStatement ps = 
                     conexion.prepareStatement(sql)) {
            
            
           String passwordHash = 
                   BCrypt.hashpw(
                           u.getPassword(),
                           BCrypt.gensalt(12)
                   );
            
           ps.setString(1, u.getUsuario());
           ps.setString(2, passwordHash);
           ps.setString(3, u.getNombre());
           ps.setString(4, u.getRol());
           ps.setBoolean(5, u.isActivo());
            
           int filas =
                   ps.executeUpdate();
           
           return filas > 0;
            
        }catch (Exception e){
            
            e.printStackTrace();
            
            return false;
        }  
    }
    
    
    
    
    public Usuario buscarPorUsuario(String usuario){
    
        String sql =
                "SELECT id, usuario, password, nombre, rol, activo " + 
                "FROM usuarios " + 
                "WHERE usuario = ?";
        
        try (Connection conexion = Conexion.conectar();
             PreparedStatement ps = 
                     conexion.prepareStatement(sql)) {
            
            ps.setString(1, usuario);
            
            try (ResultSet rs =
                    ps.executeQuery()) {
                
                if (rs.next()){
                    
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("usuario"),
                            rs.getString("password"),
                            rs.getString("nombre"),
                            rs.getString("rol"),
                            rs.getBoolean("activo")
                    );
                }
                
            }           
        }catch (Exception e){
            
            e.printStackTrace();
        }
        
        return null;
    } 
}
