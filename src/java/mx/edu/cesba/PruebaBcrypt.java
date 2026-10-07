package mx.edu.cesba;


import org.mindrot.jbcrypt.BCrypt;




public class PruebaBcrypt {
     public static void main(String[] args) {
        
        String password = "admin123";
        
        String hash = BCrypt.hashpw(
                password, 
                BCrypt.gensalt(12)
        );
         
         System.out.println("HASH GENERADO"); 
         System.out.println(hash);
         
         boolean correcta = BCrypt.checkpw(
                 password, 
                 hash
         );
         
         System.out.println(
                 "CONTRASENA CORRECTA" + correcta
         );   
    }
     
}
