<%-- 
    Document   : login
    Created on : 13 oct 2026, 07:43:54
    Author     : Agustin Martinez
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="es">
    
    <head>
        
        <meta charset="UTF-8">
        
        <meta name="viewport"
              content="width=device-width. initial-scale=1.0">
        
        <title>Agenda Médica | Iniciar sesión</title>
        
        <style>
        
            * {
                box-sizing: border-box;
                margin: 0;
                padding: 0;
            }
            
            body {
                
                font-family: arial, sans-serif;
                
                background: linear-gradient(
                    135deg,
                    #0d6efd,
                    #084298
                );
                
                min-height: 100vh;
                
                display: flex;
                
                justify-content: center;
                
                align-items: center;
            }
            
            .login-container {
                
                width: 400px;
                
                background: white;
                
                padding: 40px;
                
                border-radius: 15px;
                
                box-shadow: 
                    0 10px 30px rgba(0,0,0,0.25);
            }
            
            .logo {
                
                text-align: center;
                
                margin-bottom: 20px;
            }
            
            .logo-icon {
                
                font-size: 60px;
            }
            
            h1 {
                
                text-align: center;
                
                color: #222;
                
                margin-bottom: 8px;
            }
            
            .subtitulo {
                
                text-align: center;
                
                color: #666;
                
                margin-bottom: 30px;
            }
            
            label {
                
                display: block;
                
                margin-bottom: 8px;
                
                color: #333;
                
                font-weight: bold;
            }
            
            input {
                
                width: 100%;
                
                padding: 12px;
                
                margin-bottom: 20px;
                
                border: 1px solid #ccc;
                
                border-radius: 8px;
                
                font-size: 16px;
            }
            
            input:focus {
                
                outline: none;
                
                border-color: #0d6efd;
                
                box-shadow: 
                    0 0 5px rgba(13,110,253,0.3);
            }
            
            button {
                
                width: 100%;
                
                padding: 13px;
                
                border: none;
                
                border-radius: 8px;
                
                background: #0d6efd;
                
                color: white;
                
                font-size: 17px;
                
                cursor: pointer;
            }
            
            button:hover {
                
                background: #084298;
            }
            
            .mensaje-error {
                
                background: #f8d7da;
                
                color: #842029;
                
                border: 1px solid #f5c2c7;
                
                padding: 12px;
                
                border-radius: 8px;
                
                margin-bottom: 20px;
                
                text-align: center;
            }
            
            .pie{
                
                text-align: center;
                
                margin-top: 25px;
                
                color: #777;
                
                font-size: 13px;
            }
            
        </style>
        
    </head>
    
    <body>
        
        <div class="login-container">
            
            <div class="logo">
                
                <div class="logo-icon"></div>
                
            </div>
            
            <h1>Agenda Médica</h1>
            
            <div class="subtitulo">
                Sistema de gestión para consultorio
            </div>
            
            <%
                String error = request.getParameter("error");
                
                if ("1".equals(error)) {
            %>
            
            <div class="mensaje-error">
                Usuario o contraseña incorrecta.
            </div>
            
            <%
                }
            %>
            
            <form action="LoginServlet" method="post">
                
                <label for="usuario">
                    Usuario
                </label>
                
                <input
                    type="text"
                    id="usuario"
                    name="usuario"
                    placeholder="ingresa tu usuario"
                    required
                    autocomplete="username"
                >                    
                
                <label for="password">
                    contraseña
                </label>
                
                <input
                    type="password"
                    id="password"
                    name="password"
                    placeholder="ingresa tu contraseña"
                    required
                    autocomplete="current-password"
                >
                
                <button type="submit">
                    Ingresar
                </button>
                
            </form>
            
            <div class="pie">
                 Agenda Médica © 2026
            </div>
            
        </div>
            
</body>
    
</html>
