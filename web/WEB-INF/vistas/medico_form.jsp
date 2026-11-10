<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import=" mx.edu.cesba.Html"%>
<%@page import=" mx.edu.cesba.Medico"%>
<%@page import=" mx.edu.cesba.Usuario"%>

<%
    String ctx = request.getContextPath();

    String usuarioSesion = (String) session.getAttribute("usuario");
    String nombreSesion = (String) session.getAttribute("nombre");
    String rolSesion = (String) session.getAttribute("rol");

    if (nombreSesion == null) {
        nombreSesion = usuarioSesion;
    }

    Medico m = (Medico) request.getAttribute("medico");
    String error = (String) request.getAttribute("error");
    List<Usuario> cuentas = (List<Usuario>) request.getAttribute("cuentas");

    boolean esNuevo = m.getId() == 0;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agenda Médica | <%= esNuevo ? "Nuevo médico" : "Editar médico" %></title>
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
        <h1><%= esNuevo ? "Nuevo médico" : "Editar médico" %></h1>
    </div>

    <% if (error != null) { %>
    <div class="mensaje error" role="alert">
        <%= Html.esc(error) %>
    </div>
    <% } %>

    <section class="panel">

        <form class="formulario" method="post"
              action="<%= ctx %>/MedicoServlet">

            <input type="hidden" name="accion" value="guardar">
            <input type="hidden" name="id" value="<%= m.getId() %>">

            <div class="campo">
                <label for="nombre">Nombre <span class="requerido">*</span></label>
                <input type="text" id="nombre" name="nombre" maxlength="100"
                       required value="<%= Html.esc(m.getNombre()) %>">
            </div>

            <div class="campo">
                <label for="apellidoPaterno">Apellido paterno <span class="requerido">*</span></label>
                <input type="text" id="apellidoPaterno" name="apellidoPaterno"
                       maxlength="100" required
                       value="<%= Html.esc(m.getApellidoPaterno()) %>">
            </div>

            <div class="campo">
                <label for="apellidoMaterno">Apellido materno</label>
                <input type="text" id="apellidoMaterno" name="apellidoMaterno"
                       maxlength="100"
                       value="<%= Html.esc(m.getApellidoMaterno()) %>">
            </div>

            <div class="campo">
                <label for="cedulaProfesional">Cédula profesional <span class="requerido">*</span></label>
                <input type="text" id="cedulaProfesional" name="CedulaProfesional"
                       maxlength="30" required
                       value="<%= Html.esc(m.getCedulaProfesional()) %>">
            </div>

            <div class="campo">
                <label for="especialidad">Especialidad <span class="requerido">*</span></label>
                <input type="text" id="especialidad" name="especialidad"
                       maxlength="100" required list="especialidades"
                       value="<%= Html.esc(m.getEspecialidad()) %>">
                <datalist id="especialidades">
                    <option value="Medicina general">
                    <option value="Medicina familiar">
                    <option value="Pediatría">
                    <option value="Ginecología y obstetricia">
                    <option value="Medicina interna">
                    <option value="Cardiología">
                    <option value="Dermatología">
                    <option value="Psiquiatría">
                    <option value="Traumatología y ortopedia">
                </datalist>
                <small style="color:#64748b">Puedes elegir una sugerencia o escribir otra.</small>
            </div>

            <div class="campo">
                <label for="telefono">Teléfono</label>
                <input type="tel" id="telefono" name="telefono" maxlength="20"
                       value="<%= Html.esc(m.getTelefono()) %>">
            </div>

            <div class="campo completo">
                <label for="correo">Correo electrónico</label>
                <input type="email" id="correo" name="correo" maxlength="150"
                       value="<%= Html.esc(m.getCorreo()) %>">
            </div>

            <div class="campo completo">
                <label for="usuarioId">Cuenta de acceso al sistema</label>
                <select id="usuarioId" name="usuarioid">
                    <option value="">Sin cuenta (no inicia sesión)</option>
                    <% if (cuentas != null) {
                        for (Usuario u : cuentas) { %>
                    <option value="<%= u.getId() %>"
                            <%= (m.getUsuarioId() != null && m.getUsuarioId() == u.getId())
                                ? "selected" : "" %>>
                        <%= Html.esc(u.getUsuario()) %> — <%= Html.esc(u.getNombre()) %>
                    </option>
                    <%  }
                    } %>
                </select>
                <small style="color:#64748b">
                    Solo aparecen cuentas activas con rol MEDICO que no estén
                    vinculadas a otro médico.
                </small>
            </div>

            <div class="botones-form">
                <button type="submit" class="boton">Guardar médico</button>
                <a class="boton secundario" href="<%= ctx %>/MedicoServlet">Cancelar</a>
            </div>

        </form>

    </section>
</main>

<footer>
    Agenda Médica &copy; 2026 | Sistema de gestión del consultorio
</footer>

</body>
</html>
