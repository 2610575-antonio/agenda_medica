<%-- 
    Vista: lista de médicos
    Se muestra a través de MedicoServlet (está dentro de WEB-INF,
    por eso no se puede abrir escribiendo la URL).
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="mx.edu.cesba.Html"%>
<%@page import="mx.edu.cesba.Medico"%>
<%
    String ctx = request.getContextPath();

    String usuarioSesion = (String) session.getAttribute("usuario");
    String nombreSesion = (String) session.getAttribute("nombre");
    String rolSesion = (String) session.getAttribute("rol");

    if (nombreSesion == null) {
        nombreSesion = usuarioSesion;
    }

    List<Medico> medicos = (List<Medico>) request.getAttribute("medicos");
    String filtro = (String) request.getAttribute("filtro");
    boolean verInactivos = Boolean.TRUE.equals(request.getAttribute("verInactivos"));
    String mensaje = (String) request.getAttribute("mensaje");
    boolean mensajeError = Boolean.TRUE.equals(request.getAttribute("mensajeError"));
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agenda Médica | Médicos</title>
    <link rel="stylesheet" href="<%= ctx %>/css/estilos.css">
</head>

<body>

    <header>
        <div class="marca">
            <a href="<%= ctx %>/index.jsp">&#9764; Agenda Médica</a>
        </div>

        <div class="usuario">
            <strong><%= Html.esc(nombreSesion) %></strong><br>
            Usuario: <%= Html.esc(usuarioSesion) %> | Rol: <%= Html.esc(rolSesion)%>
            <br><br>
            <a class="boton" href="<%= ctx %>/LogoutServlet">Cerrar sesión</a>
        </div>
    </header>

    <main class="contenedor">

        <div class="encabezado-seccion">
            <h1>Médicos</h1>

            <div>
                <a class="boton secundario" href="<%= ctx %>/index.jsp">Volver al panel</a>
                <a class="boton" href="<%= ctx %>/MedicoServlet?accion=nuevo">Nuevo médico</a>
            </div>
        </div>

        <% if (mensaje != null) { %>
        <div class="mensaje <%= mensajeError ? "error" : "ok" %>" role="status">
            <%= Html.esc(mensaje) %>
        </div>
        <% } %>

        <section class="panel">

            <form class="busqueda" method="get" action="<%= ctx %>/MedicoServlet">
                <input type="text" name="q" value="<%= Html.esc(filtro) %>"
                       placeholder="Buscar por nombre, cédula o especialidad"
                       aria-label="Buscar médico">

                <label>
                    <input type="checkbox" name="inactivos" value="1"
                           <%= verInactivos ? "checked" : "" %>>
                    Ver desactivados
                </label>

                <button type="submit" class="boton">Buscar</button>
            </form>

            <div class="tabla-contenedor">
                <table>
                    <thead>
                        <tr>
                            <th>Médico</th>
                            <th>Cédula</th>
                            <th>Especialidad</th>
                            <th>Teléfono</th>
                            <th>Cuenta de acceso</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (medicos == null || medicos.isEmpty()) { %>
                        <tr>
                            <td colspan="6" class="vacio">
                                <% if (filtro != null && !filtro.isEmpty()) { %>
                                Ningún médico coincide con la búsqueda.
                                <% } else if (verInactivos) { %>
                                No hay médicos desactivados.
                                <% } else { %>
                                No hay médicos activos. Agrega uno con «Nuevo médico» o marca «Ver desactivados».
                                <% } %>
                            </td>
                        </tr>
                        <% } else {
                        for (Medico m : medicos) { %>
                         <tr>
                            <td class="<%= m.isActivo() ? "" : "inactivo" %>">
                                <%= Html.esc(m.getNombreCompleto()) %>
                            </td>
                            <td><%= Html.esc(m.getCedulaProfesional()) %></td>
                            <td><%= Html.esc(m.getEspecialidad()) %></td>
                            <td><%= m.getTelefono() != null ? Html.esc(m.getTelefono()) : "—" %></td>
                            <td><%= m.getCuenta() != null ? Html.esc(m.getCuenta()) : "—" %></td>
                            <td class="acciones">
                                <a class="boton chico"
                                   href="<%= ctx %>/MedicoServlet?accion=editar&amp;id=<%= m.getId() %>">Editar</a>

                                <form class="en-linea" method="post"
                                      action="<%= ctx %>/MedicoServlet"
                                      onsubmit="return confirm('<%= m.isActivo()
                                          ? "¿Desactivar a este médico?"
                                          : "¿Reactivar a este médico?" %>');">
                                    <input type="hidden" name="accion"
                                           value="<%= m.isActivo() ? "desactivar" : "reactivar" %>">
                                    <input type="hidden" name="id" value="<%= m.getId() %>">
                                    <button type="submit"
                                            class="boton chico <%= m.isActivo() ? "peligro" : "secundario" %>">
                                        <%= m.isActivo() ? "Desactivar" : "Reactivar" %>
                                    </button>
                                </form>
                            </td>
                        </tr>
                        <%  }
                        } %>
                    </tbody>
                </table>
            </div>

        </section>
    </main>

    <footer>
        Agenda Médica &copy; 2026 | Sistema de gestión del consultorio
    </footer>

</body>
</html>
