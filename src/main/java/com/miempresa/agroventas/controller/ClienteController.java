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

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente,Integer> colId;
    @FXML private TableColumn<Cliente,String>  colNombre;
    @FXML private TableColumn<Cliente,String>  colCorreo;
    @FXML private TableColumn<Cliente,String>  colDireccion;
    @FXML private TableColumn<Cliente,String>  colTelefono;

    private final ClienteDAO dao = new ClienteDAO();

    /**
     * Inicializa las columnas de la tabla y carga la lista de clientes.
     * Este método se ejecuta automáticamente tras cargar el FXML.
     */
    @FXML
    public void initialize() {
        colId       .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getIdUsuario()));
        colNombre   .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getNombre()));
        colCorreo   .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCorreo()));
        colDireccion.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getDireccion()));
        colTelefono .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTelefono()));

        loadClientes();
    }

    /**
     * Recupera todos los clientes de la base de datos y los muestra en la tabla.
     * Si ocurre un error, muestra un diálogo de error.
     */
    private void loadClientes() {
        try {
            var list = dao.findAll();
            tablaClientes.setItems(FXCollections.observableArrayList(list));
        } catch(Exception e) {
            showError("No se pudieron cargar clientes", e.getMessage());
        }
    }

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private Button      btnNuevoCliente;

    /**
     * Abre un diálogo para dar de alta un nuevo cliente.
     * Al cerrar el formulario, si el usuario confirma, recarga la tabla.
     */
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
            fc.setDialogStage(dialog);
            fc.setCliente(new Cliente());  // cliente vacío para crear uno nuevo

            dialog.showAndWait();
            if (fc.isOkClicked()) {
                loadClientes();
            }
        } catch (Exception e) {
            showError("Error al abrir formulario", e.getMessage());
        }
    }

    /**
     * Abre un diálogo para editar el cliente seleccionado.
     * Si no hay ninguno seleccionado, muestra un error.
     * Al confirmar cambios, recarga la tabla.
     */
    @FXML
    private void onEditarCliente() {
        Cliente sel = tablaClientes.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Selecciona un cliente", "");
            return;
        }
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
            fc.setCliente(sel);   // pasamos el cliente existente para editar

            dialog.showAndWait();
            if (fc.isOkClicked()) {
                loadClientes();
            }
        } catch (Exception e) {
            showError("Error al abrir formulario de edición", e.getMessage());
        }
    }

    /**
     * Método auxiliar que factoriza la lógica de abrir formularios de cliente.
     * @param temp   instancia de Cliente (nueva o existente)
     * @param titulo título de la ventana de diálogo
     */
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
            if (fc.isOkClicked()) {
                loadClientes();
            }
        } catch(Exception e) {
            showError("Error al abrir formulario", e.getMessage());
        }
    }

    /**
     * Elimina el cliente seleccionado de la base de datos.
     * Si no hay selección, muestra un error; ante cualquier excepción, informa al usuario.
     */
    @FXML
    private void onEliminarCliente() {
        Cliente sel = tablaClientes.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Selecciona un cliente", "");
            return;
        }
        try {
            dao.delete(sel.getIdUsuario());
            loadClientes();
        } catch(Exception e) {
            showError("No se pudo eliminar", e.getMessage());
        }
    }

    /**
     * Muestra un diálogo de alerta de tipo ERROR.
     * @param header texto de cabecera de la alerta
     * @param content texto de contenido (detalle del error)
     */
    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content);
        alert.setHeaderText(header);
        alert.showAndWait();
    }
}


