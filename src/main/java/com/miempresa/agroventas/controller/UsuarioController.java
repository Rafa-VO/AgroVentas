package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.DAO.UsuarioDAO;
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

public class UsuarioController {

    @FXML private TableView<Usuario>           tablaUsuarios;
    @FXML private TableColumn<Usuario,Integer> colId;
    @FXML private TableColumn<Usuario,String>  colNombre;
    @FXML private TableColumn<Usuario,String>  colCorreo;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    public void initialize() {
        // Configura los cellValueFactory
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getIdUsuario()));
        colNombre.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getNombre()));
        colCorreo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCorreo()));

        // Carga datos iniciales
        loadUsuarios();
    }

    private void loadUsuarios() {
        try {
            var list = usuarioDAO.findAll();
            tablaUsuarios.setItems(FXCollections.observableArrayList(list));
        } catch (Exception e) {
            showError("Error al cargar usuarios", e.getMessage());
        }
    }


    @FXML
    private void onNuevoUsuario() {
        try {
            // 1) preparamos un nuevo Usuario vacío
            Usuario temp = new Usuario();

            // 2) cargamos el FXML del formulario
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/agrobdgui/usuarioform.fxml")
            );
            Parent root = loader.load();

            // 3) creamos la ventana modal
            Stage dialog = new Stage();
            dialog.setTitle("Nuevo Usuario");
            dialog.initOwner(tablaUsuarios.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));

            // 4) pasamos el Stage y el Usuario al formulario
            UsuarioFormController fc = loader.getController();
            fc.setDialogStage(dialog);
            fc.setUsuario(temp);

            // 5) mostramos y esperamos
            dialog.showAndWait();

            // 6) si guardó (okClicked), recargamos tabla
            if (fc.isOkClicked()) {
                loadUsuarios();
            }
        } catch (Exception e) {
            showError("Error al abrir formulario", e.getMessage());
        }
    }



    @FXML
    private void onEliminarUsuario() {
        Usuario sel = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Debe seleccionar un usuario", "");
            return;
        }
        try {
            usuarioDAO.delete(sel.getIdUsuario());
            loadUsuarios();
        } catch (Exception e) {
            showError("Error al eliminar usuario", e.getMessage());
        }
    }

    @FXML
    private void onEditarUsuario() {
        Usuario sel = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Selecciona un usuario", "");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/agrobdgui/usuarioform.fxml")
            );
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Editar Usuario");
            dialog.initOwner(tablaUsuarios.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));

            UsuarioFormController fc = loader.getController();
            fc.setDialogStage(dialog);
            // PASAMOS EL USUARIO EXISTENTE
            fc.setUsuario(sel);

            dialog.showAndWait();
            if (fc.isOkClicked()) {
                loadUsuarios();
            }
        } catch (Exception e) {
            showError("Error al abrir formulario de edición", e.getMessage());
        }
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

