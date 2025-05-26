package com.miempresa.agroventas.util;

import com.miempresa.agroventas.DAO.ClienteDAO;
import com.miempresa.agroventas.DAO.EmpleadoDAO;
import com.miempresa.agroventas.interfaces.Role;
import com.miempresa.agroventas.model.Cliente;
import com.miempresa.agroventas.model.Empleado;
import com.miempresa.agroventas.model.Usuario;

public class Session {
    private static Usuario usuario;
    private static Role role;

    /** Inicia sesión, guardando usuario y rol */
    public static void login(Usuario u, Role r) {
        usuario = u;
        role    = r;
    }

    /** Recupera el usuario en sesión */
    public static Usuario getUsuario() {
        return usuario;
    }

    /** Recupera el rol en sesión */
    public static Role getRole() {
        return role;
    }

    /** Cierra sesión, borrando usuario y rol */
    public static void logout() {
        usuario = null;
        role    = null;
    }

    /** Alias de logout para mantener compatibilidad con Session.clear() */
    public static void clear() {
        logout();
    }

    /**
     * Si el rol es CLIENTE, devuelve el Cliente asociado,
     * o null si no hay usuario o no existe el cliente.
     */
    public static Cliente getCurrentCliente() throws Exception {
        if (usuario == null) return null;
        return new ClienteDAO().findById(usuario.getIdUsuario());
    }

    public static Empleado getCurrentEmpleado() throws Exception {
        if (usuario == null) return null;
        return new EmpleadoDAO().findById(usuario.getIdUsuario());
    }
}




