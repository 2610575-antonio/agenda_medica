
package mx.edu.cesba;

import java.sql.Connection;





public class PruebaConexion {
    
    public static void main(String[] args) {
     
        Connection conexion = Conexion.conectar();
        
        if (conexion!= null) {
            
            System.out.println(
                    "================================="
            );
            
            System.out.println(
                    "CONEXION EXITOSA"
            );
            
            System.out.println(
                    "Base de datos: agenda_medica"
            );
            
            System.out.println(
                    "================================="
            );
            
            try {
                
                conexion.close();
                
            }catch (Exception e) {
                
                e.printStackTrace();
            }
            
        } else {
            
            System.out.println(
                    "================================="
            );
            
            System.out.println(
                    "ERROR DE CONEXION"
            );
            
            System.out.println(
                    "================================="
            );
        }   
    }
}
