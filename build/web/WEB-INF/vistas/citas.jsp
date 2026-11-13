<%--
    Vista: agenda de citas del día
    Se muestra a través de CitaServlet (está dentro de WEB-INF,
    por eso no se puede abrir escribiendo la URL).
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Locale"%>
<%@page import="mx.edu.cesba.Cita"%>
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

    List<Cita> citas = (List<Cita>) request.getAttribute("citas");
    List<Medico> medicos = (List<Medico>) request.getAttribute("medicos");
    LocalDate fecha = (LocalDate) request.getAttribute("fecha");
    Integer medicoId = (Integer) request.getAttribute("medicoId");
    String estadoFiltro = (String) request.getAttribute("estadoFiltro");
    boolean soloPropia = Boolean.TRUE.equals(request.getAttribute("soloPropia"));
    String mensaje = (String) request.getAttribute("mensaje");
    boolean mensajeError = Boolean.TRUE.equals(request.getAttribute("mensajeError"));

    String medicoTexto = medicoId == null ? "" : String.valueOf(medicoId);

    // Parte común de los enlaces de navegación (conserva los filtros)
    String filtros = "&medicoId=" + medicoTexto + "&estado=" + estadoFiltro;

    DateTimeFormatter fechaLarga = DateTimeFormatter.ofPattern(
            "EEEE d 'de' MMMM 'de' yyyy", new Locale("es", "MX"));
    DateTimeFormatter horaCorta = DateTimeFormatter.ofPattern("HH:mm");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agenda Médica | Agenda de citas</title>
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
        <div>
            <h1>Agenda de citas</h1>
            <p class="subtitulo-fecha"><%= fecha.format(fechaLarga) %></p>
        </div>

        <div>
            <a class="boton secundario" href="<%= ctx %>/index.jsp">Volver al panel</a>
            <a class="boton"
               href="<%= ctx %>/CitaServlet?accion=nuevo&amp;fecha=<%= fecha %>&amp;medicoId=<%= medicoTexto %>">
                Nueva cita</a>
        </div>
    </div>

    <% if (mensaje != null) { %>
    <div class="mensaje <%= mensajeError ? "error" : "ok" %>" role="status">
        <%= Html.esc(mensaje) %>
    </div>
    <% } %>

    <section class="panel">

        <nav class="navegacion-dias" aria-label="Cambiar de día">
            <a class="boton chico secundario"
               href="<%= ctx %>/CitaServlet?fecha=<%= fecha.minusDays(1) %><%= filtros %>">
                &lsaquo; Día anterior</a>
            <a class="boton chico"
               href="<%= ctx %>/CitaServlet?fecha=<%= LocalDate.now() %><%= filtros %>">Hoy</a>
            <a class="boton chico secundario"
               href="<%= ctx %>/CitaServlet?fecha=<%= fecha.plusDays(1) %><%= filtros %>">
                Día siguiente &rsaquo;</a>
        </nav>

        <form class="filtros" method="get" action="<%= ctx %>/CitaServlet">

            <div class="campo">
                <label for="fecha">Fecha</label>
                <input type="date" id="fecha" name="fecha" value="<%= fecha %>">
            </div>

            <div class="campo">
                <% if (soloPropia) { %>
                <label for="medicoNombre">Médico</label>
                <input type="text" id="medicoNombre" readonly
                       value="<%= (medicos != null && !medicos.isEmpty())
                               ? Html.esc(medicos.get(0).getNombreCompleto()) : "" %>">
                <input type="hidden" name="medicoId" value="<%= medicoTexto %>">
                <% } else { %>
                <label for="medicoId">Médico</label>
                <select id="medicoId" name="medicoId">
                    <option value="">Todos los médicos</option>
                    <% if (medicos != null) {
                        for (Medico m : medicos) { %>
                    <option value="<%= m.getId() %>"
                            <%= (medicoId != null && medicoId == m.getId()) ? "selected" : "" %>>
                        <%= Html.esc(m.getNombreCompleto()) %>
                    </option>
                    <%  }
                       } %>
                </select>
                <% } %>
            </div>

            <div class="campo">
                <label for="estado">Estado</label>
                <select id="estado" name="estado">
                    <option value="">Todos los estados</option>
                    <% for (String e : Cita.ESTADOS) { %>
                    <option value="<%= e %>" <%= e.equals(estadoFiltro) ? "selected" : "" %>>
                        <%= Cita.etiquetaEstado(e) %>
                    </option>
                    <% } %>
                </select>
            </div>

            <button type="submit" class="boton">Filtrar</button>
        </form>

        <div class="tabla-contenedor">
            <table>
                <thead>
                    <tr>
                        <th>Hora</th>
                        <th>Paciente</th>
                        <th>Médico</th>
                        <th>Motivo</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (citas == null || citas.isEmpty()) { %>
                    <tr>
                        <td colspan="6" class="vacio">
                            No hay citas para este día con los filtros elegidos.
                            Agenda una con «Nueva cita».
                        </td>
                    </tr>
                    <% } else {
                        for (Cita c : citas) {
                            boolean cancelable = "PROGRAMADA".equals(c.getEstado())
                                    || "CONFIRMADA".equals(c.getEstado()); %>
                    <tr>
                        <td><strong><%= c.getHora().format(horaCorta) %></strong></td>
                        <td><%= Html.esc(c.getPaciente()) %></td>
                        <td><%= Html.esc(c.getMedico()) %></td>
                        <td><%= c.getMotivo() != null ? Html.esc(c.getMotivo()) : "—" %></td>
                        <td>
                            <span class="estado <%= c.getEstado().toLowerCase() %>">
                                <%= Cita.etiquetaEstado(c.getEstado()) %>
                            </span>
                        </td>
                        <td class="acciones">
                            <% if ("PROGRAMADA".equals(c.getEstado())) { %>
                            <form class="en-linea" method="post"
                                  action="<%= ctx %>/CitaServlet">
                                <input type="hidden" name="accion" value="confirmar">
                                <input type="hidden" name="id" value="<%= c.getId() %>">
                                <input type="hidden" name="fecha" value="<%= fecha %>">
                                <input type="hidden" name="medicoId" value="<%= medicoTexto %>">
                                <input type="hidden" name="estado" value="<%= estadoFiltro %>">
                                <button type="submit" class="boton chico exito">Confirmar</button>
                            </form>
                            <% } %>

                            <a class="boton chico"
                               href="<%=ctx%>/CitaServlet?accion=editar&amp;id=<%=c.getId()%>">Editar</a>

                            <% if (cancelable) { %>
                            <form class="en-linea" method="post"
                                  action="<%= ctx %>/CitaServlet"
                                  onsubmit="return confirm('¿Cancelar esta cita? El horario quedará libre.');">
                                <input type="hidden" name="accion" value="cancelar">
                                <input type="hidden" name="id" value="<%= c.getId() %>">
                                <input type="hidden" name="fecha" value="<%= fecha %>">
                                <input type="hidden" name="medicoId" value="<%= medicoTexto %>">
                                <input type="hidden" name="estado" value="<%= estadoFiltro %>">
                                <button type="submit" class="boton chico peligro">Cancelar</button>
                            </form>
                            <% } %>
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
