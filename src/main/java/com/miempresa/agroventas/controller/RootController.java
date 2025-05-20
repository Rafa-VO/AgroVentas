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

public class RootController {

    @FXML private BorderPane contenidoPane;
    @FXML private Menu     menuGestion;
    @FXML private MenuItem menuUsuarios;
    @FXML private MenuItem menuClientes;
    @FXML private MenuItem menuEmpleados;

    @FXML
    public void initialize() {
        Role role = Session.getRole();

        if (role == Role.CLIENTE) {
            // los clientes no ven el menú de gestión
            menuGestion.setVisible(false);
            // carga ClienteView en el centro
            cargar("views/clienteView.fxml");
        } else {
            // EMPLEADO
            menuUsuarios.setVisible(false);
            menuClientes .setOnAction(e -> cargar("agrobdgui/cliente.fxml"));
            menuEmpleados.setOnAction(e -> cargar("views/empleadoView.fxml"));
            // vista inicial: EmpleadoView
            cargar("views/empleadoView.fxml");
        }
    }

    /**
     * Carga un FXML desde el classpath y lo inyecta en el centro.
     * @param ruta ejemplo "views/empleadoView.fxml" o "agrobdgui/cliente.fxml"
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







