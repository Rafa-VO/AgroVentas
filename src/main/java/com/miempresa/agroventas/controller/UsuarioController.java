package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.DAO.UsuarioDAO;
import com.miempresa.agroventas.model.Usuario;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controlador de la vista de gestión de usuarios. Permite listar todos los
 * usuarios, crear nuevos, editar existentes y eliminar los seleccionados.
 */
public class UsuarioController {

    @FXML private TableView<Usuario>           tablaUsuarios;
    @FXML private TableColumn<Usuario,Integer> colId;
    @FXML private TableColumn<Usuario,String>  colNombre;
    @FXML private TableColumn<Usuario,String>  colCorreo;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Inicializa las columnas de la tabla vinculando cada columna
     * con la propiedad correspondiente del modelo Usuario. A continuación
     * carga la lista inicial de usuarios.
     */
    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getIdUsuario()));
        colNombre.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getNombre()));
        colCorreo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCorreo()));
        loadUsuarios();
    }

    /**
     * Obtiene todos los usuarios desde la capa DAO y los asigna a la tabla.
     * Si ocurre un error de acceso a datos, muestra un cuadro de diálogo de error.
     */
    private void loadUsuarios() {
        try {
            var list = usuarioDAO.findAll();
            tablaUsuarios.setItems(FXCollections.observableArrayList(list));
        } catch (Exception e) {
            showError("Error al cargar usuarios", e.getMessage());
        }
    }

    /**
     * Abre un formulario modal para dar de alta un nuevo usuario. Se crea
     * un objeto Usuario vacío y se pasa al UsuarioFormController. Tras cerrar
     * el diálogo, si el usuario confirmó la operación, se recarga la tabla.
     */
    @FXML
    private void onNuevoUsuario() {
        try {
            Usuario temp = new Usuario();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/agrobdgui/usuarioform.fxml"));
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Nuevo Usuario");
            dialog.initOwner(tablaUsuarios.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));

            UsuarioFormController fc = loader.getController();
            fc.setDialogStage(dialog);
            fc.setUsuario(temp);

            dialog.showAndWait();
            if (fc.isOkClicked()) {
                loadUsuarios();
            }
        } catch (Exception e) {
            showError("Error al abrir formulario", e.getMessage());
        }
    }

    /**
     * Elimina el usuario seleccionado en la tabla. Si no hay ninguno seleccionado
     * muestra un error. Tras eliminar con éxito, recarga la lista de usuarios.
     */
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

    /**
     * Abre un formulario modal para editar el usuario seleccionado. Si no hay
     * selección, muestra un mensaje de error. Al confirmar los cambios, recarga
     * la tabla de usuarios.
     */
    @FXML
    private void onEditarUsuario() {
        Usuario sel = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Selecciona un usuario", "");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/agrobdgui/usuarioform.fxml"));
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Editar Usuario");
            dialog.initOwner(tablaUsuarios.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));

            UsuarioFormController fc = loader.getController();
            fc.setDialogStage(dialog);
            fc.setUsuario(sel);

            dialog.showAndWait();
            if (fc.isOkClicked()) {
                loadUsuarios();
            }
        } catch (Exception e) {
            showError("Error al abrir formulario de edición", e.getMessage());
        }
    }

    /**
     * Muestra un cuadro de diálogo de alerta de tipo ERROR con el título y
     * mensaje especificados.
     * @param title texto que aparece en la cabecera del diálogo
     * @param msg   mensaje de contenido con detalles del error
     */
    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}


