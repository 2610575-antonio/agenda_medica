package mx.edu.cesba;




public class Usuario {
      private int id;
    private String usuario;
    private String password;
    private String nombre;
    private String rol;
    private boolean activo;
    
    
    //constructor vacio
    public Usuario(){ 
    }
    
    //constructor para crear usuario
    public Usuario(
            String usuario,
            String password,
            String nombre,
            String rol){
        
        this.usuario = usuario;
        this.password = password;
        this.nombre = nombre;
        this.rol = rol;
        this.activo = true;
    }
    
    // constructor completo
    public Usuario(
            int id,
            String usuario,
            String password,
            String nombre,
            String rol,
            boolean activo) {
        
        this.id = id;
        this.usuario = usuario;
        this.password = password;
        this.nombre = nombre;
        this.rol = rol;
        this.activo = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
}
