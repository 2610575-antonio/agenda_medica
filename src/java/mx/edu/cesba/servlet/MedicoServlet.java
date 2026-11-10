package mx.edu.cesba.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import mx.edu.cesba.Medico;
import mx.edu.cesba.MedicoDAO;
import mx.edu.cesba.Permisos;
import mx.edu.cesba.Usuario;

@WebServlet("/MedicoServlet")
public class MedicoServlet extends HttpServlet {
    // Las vistas están dentro de WEB-INF: solo se muestran a través de este servlet
    private static final String VISTA_LISTA = "/WEB-INF/vistas/medicos.jsp";
    private static final String VISTA_FORM = "/WEB-INF/vistas/medico_form.jsp";

    private static final Pattern CEDULA =
            Pattern.compile("^[0-9A-Za-z\\-]{5,30}$");
    
    private static final Pattern CORREO =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private static final Pattern TELEFONO =
            Pattern.compile("^[0-9+()\\-\\s]{7,20}$");

    private final MedicoDAO dao = new MedicoDAO();

    // =========================================================================
    // GET: Listar, formulario nuevo y formulario de edición
    // =========================================================================
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!autorizado(request, response)) {
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        switch (accion) {
            case "nuevo":
                mostrarFormulario(request, response, new Medico(), null);
                break;

            case "editar":
                Medico existente = dao.buscarPorId(leerId(request));
                if (existente == null) {
                    response.sendRedirect(
                            request.getContextPath() + "/MedicoServlet");
                } else {
                    mostrarFormulario(request, response, existente, null);
                }
                break;

            default:
                listar(request, response);
        }
    }

    // =========================================================================
    // POST: Guardar, desactivar y reactivar
    // =========================================================================
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        if (!autorizado(request, response)) {
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "";
        }

        switch (accion) {
            case "guardar":
                guardar(request, response);
                break;

            case "desactivar":
                cambiarEstado(request, response, false);
                break;

            case "reactivar":
                cambiarEstado(request, response, true);
                break;

            default:
                response.sendRedirect(
                        request.getContextPath() + "/MedicoServlet");
        }
    }

    // =========================================================================
    // SESIÓN Y PERMISOS
    // =========================================================================
    private boolean autorizado(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        
        response.setHeader("Cache-Control",
                "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        HttpSession sesion = request.getSession(false);
        
        if (sesion == null || sesion.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return false;
        }
        
        String rol = (String) sesion.getAttribute("rol");
        
        if (!Permisos.puede(rol, Permisos.MEDICOS)){
           response.sendError(HttpServletResponse.SC_FORBIDDEN);
           return false;
        }
        
        return true;
    }
    
    // =========================================================================
    // LISTAR
    // =========================================================================
    private void listar(HttpServletRequest request, 
            HttpServletResponse response) 
            throws ServletException, IOException {

        String filtro = request.getParameter("q");
        if (filtro == null){
            filtro= "";
        }
        
        boolean verInactivos = "1".equals(request.getParameter("inactivos"));
        
        List<Medico> medicos = dao.listar(filtro, !verInactivos);
        
        String msg = request.getParameter("mensaje");
        if ("guardado".equals(msg)) {
            request.setAttribute("mensaje", "Médico guardado");
        }else if ("desactivado".equals(msg)){
            request.setAttribute("mensaje", "Médico desactivado");
        }else if ("reactivado".equals(msg)){
            request.setAttribute("mensaje", "Médico reactivado");
        }else if ("error".equals(msg)){
            request.setAttribute("mensaje",
                    "No se pudo completar la acción. Intenta de nuevo.");
            request.setAttribute("mensajeError", true);
        }

        request.setAttribute("medicos", medicos);
        request.setAttribute("filtro", filtro);
        request.setAttribute("verInactivos", verInactivos);

        request.getRequestDispatcher(VISTA_LISTA).forward(request, response);
    }
    
    // =========================================================================
    // FORMULARIO
    // =========================================================================
    private void mostrarFormulario(HttpServletRequest request,
            HttpServletResponse response, Medico medico, String error)
            throws ServletException, IOException {

        request.setAttribute("medico", medico);
        request.setAttribute("error", error);
        request.setAttribute("cuentas",
                dao.listarCuentasDisponibles(medico.getId()));

        request.getRequestDispatcher(VISTA_FORM).forward(request, response);
    }

    // =========================================================================
    // GUARDAR (nuevo o edición)
    // =========================================================================
    private void guardar(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Medico m = new Medico();
        
        m.setId(leerId(request));
        m.setCedulaProfesional(texto(request, "CedulaProfesional").toUpperCase());
        m.setNombre(texto(request, "nombre"));
        m.setApellidoPaterno(texto(request, "apellidoPaterno"));
        m.setApellidoMaterno(texto(request, "apellidoMaterno"));
        m.setEspecialidad(texto(request, "especialidad"));
        m.setTelefono(texto(request, "telefono"));
        m.setCorreo(texto(request, "correo"));

        String error = null;
        
        String usuarioTexto = texto(request, "usuarioid");
        if (!usuarioTexto.isEmpty()){
            try{
                m.setUsuarioId(Integer.valueOf(usuarioTexto));
            }catch (NumberFormatException e){
                error = "La cuenta seleccionada no es valida.";
            }
        }

        if (error == null) {
            error = validar(m);
        }

        if (error != null) {
            mostrarFormulario(request, response, m, error);
            return;
        }
        
        try {
        boolean ok = (m.getId() == 0) ? dao.insertar(m) : dao.actualizar(m);

        if (ok) {
            response.sendRedirect(request.getContextPath()
                    + "/MedicoServlet?msg=guardado");
            return;
        }
        
        error = "No se pudo guardar el médico. Intenta de nuevo.";
        
        }catch (SQLException e){
            error = traducirError(e);
        }
        
        mostrarFormulario(request, response, m, error);
    }
    
    // =========================================================================
    // DESACTIVAR / REACTIVAR
    // =========================================================================
    private void cambiarEstado(HttpServletRequest request,
            HttpServletResponse response,boolean activo)
            throws IOException {

        int id = leerId(request);
        
        boolean ok = id > 0 && dao.cambiarEstado(id, activo);
        
        String msg;
        if (!ok) {
            msg = "error"; 
        }else{
            msg = activo ? "reactivado" : "desactivado";
        }
        
        response.sendRedirect(
                request.getContextPath() + "/MedicoServlet?mensaje=" + msg);
    }
    
    // =========================================================================
    // UTILIDADES
    // =========================================================================
    private String validar(Medico m) {
        
        if (m.getNombre().isEmpty()) {
            return "El nombre es obligatorio.";
        }
        if (m.getApellidoPaterno().isEmpty()) {
            return "El apellido paterno es obligatorio.";
        }
        if (m.getCedulaProfesional().isEmpty()) {
            return "La cédula profesional es obligatoria.";
        }
        if (m.getEspecialidad().isEmpty()) {
            return "La especialidad es obligatoria.";
        }

        String e;
        if ((e = revisarLongitud(m.getNombre(), 100, "nombre")) !=null){
            return e;
        }
        if ((e = revisarLongitud(m.getApellidoPaterno(), 100,
                "apellido paterno")) !=null){
            return e;
        }
        if ((e = revisarLongitud(m.getApellidoMaterno(), 100,
                "apellido materno")) !=null){
            return e;
        }
        if ((e = revisarLongitud(m.getEspecialidad(), 100,
                "especialidad")) !=null){
            return e;
        }
        if ((e = revisarLongitud(m.getCorreo(), 150, "correo")) !=null){
            return e;
        }
        
        if (!CEDULA.matcher(m.getCedulaProfesional()).matches()) {
            return "La cédula profesional no es válida. Usa entre 5 y 30 "
                    + "caracteres: letras, numeros o guiones.";
        }
        
        if (!m.getCorreo().isEmpty()
                && !CORREO.matcher(m.getCorreo()).matches()) {
            return "El correo electrónico no es válido.";
        }
        
        if (!m.getTelefono().isEmpty()
                && !TELEFONO.matcher(m.getTelefono()).matches()) {
            return "El teléfono no es válido. Usa solo números, espacios, "
                    + "+, - y parentesis (entre 7 y 20 carcateres).";
        }

        // Si la cédula cambio, validar que no este duplicada
        // (unicamente cuando se crea o se modifica)
        if (m.getUsuarioId() != null) {
            boolean permitida = false;
            for (Usuario u : dao.listarCuentasDisponibles(m.getId())){
                if(u.getId() == m.getUsuarioId()){
                    permitida = true;
                    break;
                }
            }
            if (!permitida){
                return "La cuenta seleccionada no esta disponible.";
            }
        } 
        
        return null;
    }

    private String revisarLongitud(String valor, int max, String campo) {
        if (valor != null && valor.length() > max) {
            return "El campo " + campo + " no puede tener más de " 
                    + max + " caracteres.";
        }
        return null;
    }
    
    // covierte un error de MYSQL en un mensaje entendible
    private String traducirError(SQLException e){
        
        if (e.getErrorCode() == 1062){
            String detalle = String.valueOf(e.getMessage()).toLowerCase();
            
            if (detalle.contains("cedula")){
                return "Ya existe un médico con esa cédula profesional";
            }
            if (detalle.contains("usuario")){
                return "Esa cuenta de acceso ya está vinculada a otro médico";
            }
        }
        
        e.printStackTrace();
        return "No se pudo guardar el médico. Intentalo d nuevo";
    }
    
    // =========================================================================
    // UTILIDADES
    // =========================================================================
    private int leerId(HttpServletRequest request) {
        try {
            return Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String texto(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        return valor == null ? "" : valor.trim();
    }
}
