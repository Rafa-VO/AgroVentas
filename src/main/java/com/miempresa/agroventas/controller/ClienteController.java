package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.DAO.ClienteDAO;
import com.miempresa.agroventas.model.Cliente;
import com.miempresa.agroventas.model.Usuario;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ClienteController {

    @FXML private TableView<Cliente>tablaClientes;
    @FXML private TableColumn<Cliente,Integer> colId;
    @FXML private TableColumn<Cliente,String>  colNombre;
    @FXML private TableColumn<Cliente,String>  colCorreo;
    @FXML private TableColumn<Cliente,String>  colDireccion;
    @FXML private TableColumn<Cliente,String>  colTelefono;

    private final ClienteDAO dao = new ClienteDAO();

    @FXML
    public void initialize() {
        colId       .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getIdUsuario()));
        colNombre   .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getNombre()));
        colCorreo   .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCorreo()));
        colDireccion.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getDireccion()));
        colTelefono .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTelefono()));

        loadClientes();
    }

    private void loadClientes() {
        try {
            var list = dao.findAll();
            tablaClientes.setItems(FXCollections.observableArrayList(list));
        } catch(Exception e) {
            showError("No se pudieron cargar clientes", e.getMessage());
        }
    }

    @FXML private TableView<Usuario> tablaUsuarios;  // tu lista de usuarios
    @FXML private Button      btnNuevoCliente;

    @FXML
    private void onNuevoCliente() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/agrobdgui/clienteform.fxml")
            );
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Nuevo Cliente");
            dialog.initOwner(tablaClientes.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));

            ClienteFormController fc = loader.getController();
            // Nuevo Cliente vacío
            fc.setDialogStage(dialog);
            fc.setCliente(new Cliente());

            dialog.showAndWait();
            if (fc.isOkClicked()) {
                loadClientes();
            }
        } catch (Exception e) {
            showError("Error al abrir formulario", e.getMessage());
        }
    }


    @FXML
    private void onEditarCliente() {
        Cliente sel = tablaClientes.getSelectionModel().getSelectedItem();
        if (sel == null) { showError("Selecciona un cliente",""); return; }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/agrobdgui/clienteform.fxml")
            );
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Editar Cliente");
            dialog.initOwner(tablaClientes.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));

            ClienteFormController fc = loader.getController();
            fc.setDialogStage(dialog);
            fc.setCliente(sel);   // pasamos el Cliente existente

            dialog.showAndWait();
            if (fc.isOkClicked()) loadClientes();
        } catch (Exception e) {
            showError("Error al abrir formulario de edición", e.getMessage());
        }
    }


    private void abrirFormulario(Cliente temp, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/agrobdgui/clienteform.fxml")
            );
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle(titulo);
            dialog.initOwner(tablaClientes.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));

            ClienteFormController fc = loader.getController();
            fc.setDialogStage(dialog);
            fc.setCliente(temp);

            dialog.showAndWait();
            if (fc.isOkClicked()) loadClientes();
        } catch(Exception e) {
            showError("Error al abrir formulario", e.getMessage());
        }
    }

    @FXML
    private void onEliminarCliente() {
        Cliente sel = tablaClientes.getSelectionModel().getSelectedItem();
        if (sel==null) { showError("Selecciona un cliente",""); return; }
        try {
            dao.delete(sel.getIdUsuario());
            loadClientes();
        } catch(Exception e) {
            showError("No se pudo eliminar", e.getMessage());
        }
    }

    private void showError(String h, String c) {
        Alert a = new Alert(Alert.AlertType.ERROR, c);
        a.setHeaderText(h);
        a.showAndWait();
    }
}

