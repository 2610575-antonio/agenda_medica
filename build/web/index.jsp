<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
   
      //Verificar que exista una sesión iniciada 
    String usuario = (String) session.getAttribute("usuario");

    if (usuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    String nombre = (String) session.getAttribute("nombre");
    String rol = (String) session.getAttribute("rol");

    if (nombre == null) {
        nombre = usuario;
    }

    if (rol == null) {
        rol = "Usuario";
    }

%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sistema Médico - Panel Principal</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f1f5f9;
            color: #1e293b;
        }

        header {
            background: #ffffff;
            padding: 20px 40px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }

        .logo {
            color: #2563eb;
            font-size: 22px;
            font-weight: bold;
        }

        .usuario {
            font-size: 14px;
        }

        .contenedor {
            max-width: 1200px;
            margin: 30px auto;
            padding: 0 20px;
        }

        .bienvenida {
            margin-bottom: 25px;
        }

        .bienvenida h1 {
            margin-bottom: 5px;
            font-size: 26px;
        }

        .bienvenida p {
            color: #64748b;
        }

        .tarjetas {
            display: grid;
            grid-template-columns: repeat(
                auto-fit, minmax(250px, 1fr)
            );
            gap: 20px;
        }

        .tarjeta {
            background: #ffffff;
            padding: 22px;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            border-top: 4px solid #2563eb;
        }

        .tarjeta h3 {
            margin-top: 0;
            margin-bottom: 8px;
        }

        .tarjeta p {
            color: #64748b;
            font-size: 14px;
            line-height: 1.4;
        }

        .boton {
            display: inline-block;
            background: #2563eb;
            color: white;
            padding: 10px 16px;
            border-radius: 7px;
            text-decoration: none;
            font-size: 14px;
            margin-top: 12px;
        }

        .boton:hover {
            background: #1d4ed8;
        }

        .especial {
            border-top-color: #059669;
        }

        .especial .boton {
            background: #059669;
        }

        footer {
            text-align: center;
            padding: 25px;
            color: #64748b;
            font-size: 13px;
        }

        @media (max-width: 600px) {
            header {
                padding: 18px;
                gap: 15px;
                flex-wrap: wrap;
            }

            .usuario {
                text-align: left;
            }
        }
    </style>
</head>

<body>

<header>
    <div class="logo">
        Medical System
    </div>

    <div class="usuario">
        Usuario: <strong><%= nombre %></strong><br>
        Rol: <%= rol %><br>
        <a href="logout" class="cerrar-sesion">Cerrar sesión</a>
    </div>
</header>

<main class="contenedor">

    <section class="bienvenida">
        <h1>Bienvenido al sistema de gestión de consultorio médico.</h1>
    </section>

    <section class="tarjetas">

        <article class="tarjeta">
            <h3>Pacientes</h3>
            <p>Registro y administración de los datos de los pacientes.</p>
            <a class="boton" href="paciente.jsp">Ingresar</a>
        </article>

        <article class="tarjeta">
            <h3>Médicos</h3>
            <p>Administración de los médicos y sus datos profesionales.</p>
            <a class="boton" href="medico.jsp">Ingresar</a>
        </article>

        <article class="tarjeta">
            <h3>Agenda de citas</h3>
            <p>Programación de horarios, citas y diagnóstico del médico.</p>
            <a class="boton" href="agendar.jsp">Ingresar</a>
        </article>

        <article class="tarjeta">
            <h3>Consultas</h3>
            <p>Registros de consultas y citas médicas.</p>
            <a class="boton" href="consulta.jsp">Ingresar</a>
        </article>

        <article class="tarjeta especial">
            <h3>Expediente médico</h3>
            <p>Acceso restringido a la información médica confidencial.</p>
            <a class="boton" href="historial.jsp">Ingresar</a>
        </article>

        <article class="tarjeta">
            <h3>Usuarios</h3>
            <p>Administración de las cuentas y permisos del sistema.</p>
            <a class="boton" href="usuario.jsp">Ingresar</a>
        </article>

    </section>

</main>

<footer>
    &copy; 2024 | Sistema de gestión del consultorio médico
</footer>

</body>
</html>
