<%--
    Vista: formulario de cita (nueva y edición)
    Se muestra a través de CitaServlet.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.util.List"%>
<%@page import="mx.edu.cesba.Cita"%>
<%@page import="mx.edu.cesba.Html"%>
<%@page import="mx.edu.cesba.Medico"%>
<%@page import="mx.edu.cesba.Paciente"%>
<%
    String ctx = request.getContextPath();
    
    String usuarioSesion = (String) session.getAttribute("usuario");
    String nombreSesion = (String) session.getAttribute("nombre");
    String rolSesion = (String) session.getAttribute("rol");
    
    if (nombreSesion == null) {
        nombreSesion = usuarioSesion;
    }
    
    Cita c = (Cita) request.getAttribute("cita");
    if (c == null) {
        c = new Cita();
    }
    
    String error = (String) request.getAttribute("error");
    List<Paciente> pacientes = (List<Paciente>) request.getAttribute("pacientes");
    List<Medico> medicos = (List<Medico>) request.getAttribute("medicos");
    List<String> horarios = (List<String>) request.getAttribute("horarios");
    
    boolean esNueva = c.getId() == 0;
    
    String fechaValor = c.getFecha() != null ? c.getFecha().toString() : "";
    String horaValor = c.getHora() != null ? c.getHora().toString() : "";
    
    String fechaVolver = c.getFecha() != null ? c.getFecha().toString()
            : LocalDate.now().toString();
    
    boolean pacienteEnLista = false;
    boolean medicoEnLista = false;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agenda Médica | <%= esNueva ? "Nueva cita" : "Editar cita" %></title>
    <link rel="stylesheet" href="<%= ctx %>/css/estilos.css">
    <link rel="stylesheet" href="<%= ctx %>/css/citas.css">
</head>

<body>

<header>
    <div class="marca">
        <a href="<%= ctx %>/index.jsp">&#9764; Agenda Médica</a>
    </div>
    
    <div class="usuario">
        <strong><%= Html.esc(nombreSesion) %></strong><br>
        Usuario: <%= Html.esc(usuarioSesion) %> | Rol: <%= Html.esc(rolSesion) %>
        <br><br>
        <a class="boton" href="<%= ctx %>/LogoutServlet">Cerrar sesión</a>
    </div>
</header>

<main class="contenedor">

    <div class="encabezado-seccion">
        <h1><%= esNueva ? "Nueva cita" : "Editar cita" %></h1>
    </div>

    <% if (error != null) { %>
    <div class="mensaje error" role="alert">
        <%= Html.esc(error) %>
    </div>
    <% } %>

    <section class="panel">

        <form class="formulario" method="post" 
              action="<%=ctx%>/CitaServlet">

            <input type="hidden" name="accion" value="guardar">
            <input type="hidden" name="id" value="<%= c.getId() %>">

            <div class="campo completo">
                <label for="pacienteId">Paciente <span class="requerido">*</span></label>
                <select id="pacienteId" name="pacienteId" required>
                    <option value="">Selecciona un paciente</option>
                    <% if (pacientes != null) {
                        for (Paciente p : pacientes) {
                            boolean elegido = p.getId() == c.getPacienteId();
                            if (elegido) {
                                pacienteEnLista = true;
                            } %>
                    <option value="<%= p.getId() %>" <%= elegido ? "selected" : "" %>>
                        <%= Html.esc(p.getNombreCompleto()) %>
                    </option>
                    <%  }
                       } %>
                    <% if (!pacienteEnLista && c.getPacienteId() > 0) { %>
                    <option value="<%= c.getPacienteId() %>" selected>
                        <%= Html.esc(c.getPaciente()) %> (desactivado)
                    </option>
                    <% } %>
                </select>
            </div>

            <div class="campo completo">
                <label for="medicoId">Médico <span class="requerido">*</span></label>
                <select id="medicoId" name="medicoId" required>
                    <option value="">Selecciona un médico</option>
                    <% if (medicos != null) {
                        for (Medico m : medicos) {
                            boolean elegido = m.getId() == c.getMedicoId();
                            if (elegido) {
                                medicoEnLista = true;
                            }
                    %>
                    <option value="<%= m.getId() %>" <%= elegido ? "selected" : "" %>>
                        <%= Html.esc(m.getNombreCompleto()) %> -- <%= Html.esc(m.getEspecialidad()) %>
                    </option>
                    <%  }
                       } %>
                    <% if (!medicoEnLista && c.getMedicoId() > 0) { %>
                    <option value="<%= c.getMedicoId() %>" selected>
                        <%= Html.esc(c.getMedico()) %> (desactivado)
                    </option>
                    <% } %>
                </select>
            </div>

            <div class="campo">
                <label for="fecha">Fecha <span class="requerido">*</span></label>
                <input type="date" id="fecha" name="fecha" required
                       <%= esNueva ? "min=\"" + LocalDate.now() + "\"" : "" %>
                       value="<%= fechaValor %>">
            </div>

            <div class="campo">
                <label for="hora">Hora <span class="requerido">*</span></label>
                <select id="hora" name="hora" required>
                    <option value="">Selecciona una hora</option>
                    <% if (horarios != null) {
                        for (String h : horarios) { %>
                        <option value="<%= h %>" <%= h.equals(horaValor) ? "selected" : "" %>><%= h %></option>
                    <%  }
                       } %>
                </select>
            </div>

            <% if (!esNueva) { %>
            <div class="campo completo">
                <label for="estado">Estado</label>
                <select id="estado" name="estado">
                <% for (String e : Cita.ESTADOS) { %>
                <option value="<%= e %>" <%= e.equals(c.getEstado()) ? "selected" : "" %>>
                    <%= Cita.etiquetaEstado(e) %>
                </option>
                <% } %>
            </select>
            </div>
            <% } %>
            
            <div class="campo">
                <label for="motivo">Motivo de la consulta</label>
                <input type="text" id="motivo" name="motivo" maxlength="255" 
                       value="<%= c.getMotivo() != null ? Html.esc(c.getMotivo()) : "" %>">
            </div>

            <div class="campo completo">
                <label for="observaciones">Observaciones</label>
                <textarea id="observaciones" name="observaciones" rows="4" 
                          maxlength="500"><%= c.getObservaciones() != null ? Html.esc(c.getObservaciones()) : "" %></textarea>
            </div>

            <div class="botones-form">
                <button type="submit" class="boton">Guardar cita</button>
                <a class="boton secundario" 
                   href="<%= ctx %>/CitaServlet?fecha=<%= fechaVolver %>&amp;medicoId=">Cancelar</a>
            </div>

        </form>

    </section>

</main>

<footer>
    Agenda Médica &copy; 2026 | Sistema de gestión del consultorio
</footer>

</body>
</html>
