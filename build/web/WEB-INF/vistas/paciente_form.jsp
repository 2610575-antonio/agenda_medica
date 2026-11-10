<%-- 
    Vista: formulario de paciente (nuevo y edición)
    Se muestra a través de PacienteServlet.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.time.LocalDate"%>
<%@page import="mx.edu.cesba.Html"%>
<%@page import="mx.edu.cesba.Paciente"%>
<%
    String ctx = request.getContextPath();

    String usuarioSesion = (String) session.getAttribute("usuario");
    String nombreSesion = (String) session.getAttribute("nombre");
    String rolSesion = (String) session.getAttribute("rol");

    if (nombreSesion == null) {
        nombreSesion = usuarioSesion;
    }

    Paciente p = (Paciente) request.getAttribute("paciente");
    String error = (String) request.getAttribute("error");

    boolean esNuevo = p.getId() == 0;

    String fechaValor = p.getFechaNacimiento() != null 
            ? p.getFechaNacimiento().toString() : "";

    String sexo = p.getSexo() == null ? "" : p.getSexo();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agenda Médica - <%= esNuevo ? "Nuevo paciente" : "Editar paciente" %></title>
    <link rel="stylesheet" href="<%= ctx %>/css/estilos.css">
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
        <h1><%= esNuevo ? "Nuevo paciente" : "Editar paciente" %></h1>
    </div>

    <% if (error != null) { %>
    <div class="mensaje error" role="alert">
        <%= Html.esc(error) %>
        </div>
    <% } %>

    <div class="panel">

        <form class="formulario" method="post" 
              action="<%= ctx %>/PacienteServlet">

            <input type="hidden" name="accion" value="guardar">
            <input type="hidden" name="id" value="<%= p.getId() %>">

            <div class="campo">
                <label for="nombre">Nombre <span class="requerido">*</span></label>
                <input type="text" id="nombre" name="nombre" maxlength="100"
                       required value="<%= Html.esc(p.getNombre()) %>">
            </div>

            <div class="campo">
                <label for="apellidoPaterno">Apellido paterno <span class="requerido">*</span></label>
                <input type="text" id="apellidoPaterno" name="apellidoPaterno"
                       maxlength="100" required
                       value="<%= Html.esc(p.getApellidoPaterno()) %>">
            </div>

            <div class="campo">
                <label for="apellidoMaterno">Apellido materno</label>
                <input type="text" id="apellidoMaterno" name="apellidoMaterno"
                       maxlength="100"
                       value="<%= Html.esc(p.getApellidoMaterno()) %>">
            </div>

            <div class="campo">
                <label for="fechaNacimiento">Fecha de nacimiento</label>
                <input type="date" id="fechaNacimiento" name="fechaNacimiento" 
                       max="<%= LocalDate.now() %>" value="<%= fechaValor %>">
            </div>

            <div class="campo">
                <label for="sexo">Sexo</label>
                <select id="sexo" name="sexo">
                    <option value="">--Seleccionar--</option>
                    <option value="Femenino" <%= "Femenino".equalsIgnoreCase(sexo) ? "selected" : "" %>>Femenino</option>
                    <option value="Masculino" <%= "Masculino".equalsIgnoreCase(sexo) ? "selected" : "" %>>Masculino</option>
                    <option value="Otro" <%= "Otro".equalsIgnoreCase(sexo) ? "selected" : "" %>>Otro</option>
                </select>
            </div>

            <div class="campo">
                <label for="telefono">Teléfono</label>
                <input type="text" id="telefono" name="telefono" maxlength="20" 
                       value="<%= Html.esc(p.getTelefono()) %>">
            </div>

            <div class="campo completo">
                <label for="correo">Correo electrónico</label>
                <input type="email" id="correo" name="correo" maxlength="150" 
                       value="<%= Html.esc(p.getCorreo()) %>">
            </div>

            <div class="campo completo">
                <label for="domicilio">Domicilio</label>
                <input type="text" id="domicilio" name="domicilio" maxlength="250" 
                       value="<%= Html.esc(p.getDomicilio()) %>">
            </div>

            <div class="campo">
                <label for="contactoEmergencia">Contacto de emergencia</label>
                <input type="text" id="contactoEmergencia" name="contactoEmergencia" 
                       maxlength="150" 
                       value="<%= Html.esc(p.getContactoEmergencia()) %>">
            </div>

            <div class="campo">
                <label for="telefonoEmergencia">Teléfono de emergencia</label>
                <input type="text" id="telefonoEmergencia" name="telefonoEmergencia" 
                       maxlength="20" 
                       value="<%= Html.esc(p.getTelefonoEmergencia()) %>">
            </div>

            <div class="botones-form">
                <button type="submit" class="boton">Guardar paciente</button>
                <a class="boton secundario" href="<%= ctx %>/PacienteServlet?accion=listar">Cancelar</a>
            </div>

        </form>

    </div>

</main>

<footer>
    Agenda Médica &copy; 2026 | Sistema de gestión del consultorio</p>
</footer>

</body>
</html>


