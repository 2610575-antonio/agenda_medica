package mx.edu.cesba.servlet;




import java.io.IOException;
import mx.edu.cesba.UsuarioDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mx.edu.cesba.Usuario;

import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet{
    
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        
        
        request.setCharacterEncoding("UTF-8");
        
        
        String usuario
                =request.getParameter("usuario");
        
        String password
                =request.getParameter("password");
        
        
        if (usuario == null || usuario.trim().isEmpty()
                || password == null || password.isEmpty()){
            
            response.sendRedirect("login.jsp?error=1");
            return;
        }
        
        
      UsuarioDAO dao
              =new UsuarioDAO();
      
      Usuario usuarioEncontrado
              =dao.buscarPorUsuario(usuario);
      
      
      if (usuarioEncontrado != null){
          
          
          if (!usuarioEncontrado.isActivo()){
              
              response.sendRedirect(
                      "login.jsp?error=1"
              );
              
              return;
          }
          
          
          boolean passwordCorrecta = false;
          
          try{
              passwordCorrecta = BCrypt.checkpw(
                      password,
                      usuarioEncontrado.getPassword()
              );
          }catch (IllegalArgumentException e){
              
             passwordCorrecta = false; 
          }
          
          if (passwordCorrecta){
              
              
              HttpSession sesion
                      =request.getSession();
              
              sesion.setAttribute(
                      "usuario", 
                      usuarioEncontrado.getUsuario()
              );
              
              sesion.setAttribute(
                      "nombre", 
                      usuarioEncontrado.getNombre()
              );
              
              sesion.setAttribute(
                      "rol", 
                      usuarioEncontrado.getRol()
              );
              
              
              request.changeSessionId();
              
              
              response.sendRedirect(
                      "index.jsp"
              );
              
          }else {
              
              
             response.sendRedirect(
                      "login.jsp?error=1"
              );     
          }
            
      }else {
          
          
         response.sendRedirect(
                      "login.jsp?error=1"
         );   
       }  
    }                 
}
