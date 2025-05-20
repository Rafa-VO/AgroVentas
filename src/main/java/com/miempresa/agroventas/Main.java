package com.miempresa.agroventas;

import com.miempresa.agroventas.DAO.UsuarioDAO;
import com.miempresa.agroventas.DAO.ClienteDAO;
import com.miempresa.agroventas.DAO.EmpleadoDAO;
import com.miempresa.agroventas.model.Usuario;
import com.miempresa.agroventas.model.Cliente;
import com.miempresa.agroventas.model.Empleado;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        UsuarioDAO usuarioDAO   = new UsuarioDAO();
        ClienteDAO clienteDAO   = new ClienteDAO();
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();

        try {
            // --- 1) Listar usuarios existentes ---
            System.out.println("Usuarios existentes:");
            List<Usuario> usuarios = usuarioDAO.findAll();
            usuarios.forEach(System.out::println);

            // --- 2) Crear un nuevo usuario ---
            Usuario u = new Usuario();
            u.setNombre("Ana");
            u.setApellidos("López");
            u.setCorreo("ana.lopez@example.com");
            u.setContrasena("pass123");
            usuarioDAO.create(u);
            System.out.println("\nCreado usuario: " + u);

            // --- 3) Volver a listar usuarios ---
            System.out.println("\nUsuarios tras inserción:");
            usuarioDAO.findAll().forEach(System.out::println);

            // --- 4) Listar clientes existentes ---
            System.out.println("\nClientes existentes:");
            clienteDAO.findAll().forEach(System.out::println);

            // --- 5) Crear un cliente para el usuario creado ---
            Cliente cl = new Cliente();
            cl.setIdUsuario(u.getIdUsuario());
            cl.setDireccion("Calle Falsa 123");
            cl.setTelefono("600123456");
            clienteDAO.create(cl);
            System.out.println("\nCreado cliente: " + cl);

            // --- 6) Volver a listar clientes ---
            System.out.println("\nClientes tras inserción:");
            clienteDAO.findAll().forEach(System.out::println);

            // --- 7) Listar empleados existentes ---
            System.out.println("\nEmpleados existentes:");
            empleadoDAO.findAll().forEach(System.out::println);

            // --- 8) Crear un empleado para el mismo usuario ---
            Empleado e = new Empleado();
            e.setIdUsuario(u.getIdUsuario());
            e.setDepartamento("Ventas");
            e.setCargo("Representante");
            e.setSalario(1800.00);
            empleadoDAO.create(e);
            System.out.println("\nCreado empleado: " + e);

            // --- 9) Volver a listar empleados ---
            System.out.println("\nEmpleados tras inserción:");
            empleadoDAO.findAll().forEach(System.out::println);

        } catch (Exception ex) {
            System.err.println("Error durante las pruebas CRUD:");
            ex.printStackTrace();
        }
    }
}




