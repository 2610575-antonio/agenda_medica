
package mx.edu.cesba;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Permisos {

    // Roles (deben coincidir con la columna "rol" de la tabla usuarios)
    public static final String ADMIN = "ADMIN";
    public static final String MEDICO = "MEDICO";
    public static final String RECEPCION = "RECEPCION";

    // Módulos del sistema
    public static final String PACIENTES = "PACIENTES";
    public static final String MEDICOS = "MEDICOS";
    public static final String CITAS = "CITAS";
    public static final String CONSULTAS = "CONSULTAS";
    public static final String EXPEDIENTES = "EXPEDIENTES";
    public static final String USUARIOS = "USUARIOS";

    private static final Map<String, Set<String>> PERMISOS = new HashMap<>();

    static {
        Set<String> todo = new HashSet<>(Arrays.asList(
                PACIENTES, MEDICOS, CITAS, CONSULTAS, EXPEDIENTES, USUARIOS
        ));

        PERMISOS.put(ADMIN, todo);
        PERMISOS.put(MEDICO, todo);
        PERMISOS.put(RECEPCION, new HashSet<>(Arrays.asList(
                PACIENTES, CITAS
        )));
    }

    public static boolean puede(String rol, String modulo) {
        if (rol == null) {
            return false;
        }
        Set<String> modulos = PERMISOS.get(rol.toUpperCase());
        return modulos != null && modulos.contains(modulo);
    }
    
    public static boolean veTodasLasCitas(String rol) {
        return "ADMIN".equals(rol) || "RECEPCION".equals(rol);
    }
}
    

