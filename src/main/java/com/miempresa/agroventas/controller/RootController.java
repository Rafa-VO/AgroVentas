package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.interfaces.Role;
import com.miempresa.agroventas.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;

/**
 * Controlador de la ventana raíz de la aplicación. Gestiona la visibilidad
 * de los menús y carga las vistas correspondientes según el rol del usuario.
 */
public class RootController {

    @FXML private BorderPane contenidoPane;
    @FXML private Menu     menuGestion;
    @FXML private MenuItem menuUsuarios;
    @FXML private MenuItem menuClientes;
    @FXML private MenuItem menuEmpleados;

    /**
     * Inicializa los elementos de la interfaz tras cargar el FXML.
     * Según el rol recuperado de la sesión, oculta o muestra los menús
     * y carga la vista inicial apropiada en el panel central.
     */
    @FXML
    public void initialize() {
        Role role = Session.getRole();

        if (role == Role.CLIENTE) {
            // Oculta el menú de gestión para clientes
            menuGestion.setVisible(false);
            // Carga la vista de cliente en el panel central
            cargar("views/clienteView.fxml");
        } else {
            // Oculta la opción de usuarios (solo empleados con permisos)
            menuUsuarios.setVisible(false);
            // Configura la acción para cargar la vista de clientes
            menuClientes.setOnAction(e -> cargar("agrobdgui/cliente.fxml"));
            // Configura la acción para cargar la vista de empleados
            menuEmpleados.setOnAction(e -> cargar("views/empleadoView.fxml"));
            // Carga la vista de empleado como inicial
            cargar("views/empleadoView.fxml");
        }
    }

    /**
     * Carga un archivo FXML desde el classpath y lo coloca en el centro
     * del BorderPane. Si no se encuentra el recurso, escribe un error en stderr.
     * @param ruta ruta relativa del recurso FXML, por ejemplo "views/empleadoView.fxml"
     */
    private void cargar(String ruta) {
        URL url = getClass().getClassLoader().getResource(ruta);
        if (url == null) {
            System.err.println("No se encontró FXML: " + ruta);
            return;
        }
        try {
            Node vista = new FXMLLoader(url).load();
            contenidoPane.setCenter(vista);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}








