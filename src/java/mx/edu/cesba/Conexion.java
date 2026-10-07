package mx.edu.cesba;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    
    private static final String URL = 
            "jdbc:mysql://localhost:3306/agenda_medica"
            + "?useSSL=false"
            + "&serverTimezone=UTC"
            + "&allowPublicKeyRetrieval=true";
    
    private static final String USUARIO = "root";
    
    private static final  String PASSWORD = "1wd3wa2qse";
    
    public static Connection conectar(){
        
      Connection conexion = null;
      
      try{
          
          Class.forName("com.mysql.cj.jdbc.Driver");
                  
          conexion = DriverManager.getConnection(
                  URL,
                  USUARIO,
                  PASSWORD
          );
                  
           System.out.println(
                   "conexion a agenda_medica exitosa"
           );       
                            
      }catch (ClassNotFoundException e){
          
          System.out.println(
                  "no se encontro el driver de MySQL"
          );
          
        e.printStackTrace();
               
      }catch (SQLException e){
          
          System.out.println(
                  "error al conectar con agencia_medica."
          );
          
          e.printStackTrace(); 
      }
       
      return conexion;
    } 
}
