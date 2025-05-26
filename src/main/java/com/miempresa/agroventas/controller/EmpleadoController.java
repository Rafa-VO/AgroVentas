package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.DAO.EmpleadoDAO;
import com.miempresa.agroventas.model.Empleado;
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

import java.net.URL;

/**
 * Controlador de la vista de gestión de Empleados.
 * Permite listar, crear, editar y eliminar empleados de la base de datos
 * usando un TableView y formularios modales.
 */
public class EmpleadoController {

    @FXML private TableView<Empleado>           tablaEmpleados;
    @FXML private TableColumn<Empleado,Integer> colId;
    @FXML private TableColumn<Empleado,String>  colNombre;
    @FXML private TableColumn<Empleado,String>  colCorreo;
    @FXML private TableColumn<Empleado,String>  colDepartamento;
    @FXML private TableColumn<Empleado,String>  colCargo;
    @FXML private TableColumn<Empleado,Double>  colSalario;


    /**
     * Inicializa las columnas de la tabla vinculándolas a las propiedades
     * del modelo Empleado y carga la lista de empleados.
     * Ejecutado automáticamente tras el cargado del FXML.
     */
    @FXML
    public void initialize() {
        colId           .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getIdUsuario()));
        colNombre       .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getNombre()));
        colCorreo       .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCorreo()));
        colDepartamento .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getDepartamento()));
        colCargo        .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCargo()));
        colSalario      .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getSalario()));

        loadEmpleados();
    }

    /**
     * Recupera todos los empleados desde la capa DAO y los inserta
     * en el TableView. En caso de error, muestra un diálogo con detalles.
     */
    private void loadEmpleados() {
        try {
            tablaEmpleados.setItems(
                    FXCollections.observableArrayList(EmpleadoDAO.findAll())
            );
        } catch (Exception e) {
            showError(
                    "Error al cargar empleados",
                    "No se pudieron leer empleados de la base de datos",
                    e
            );
        }
    }

    /**
     * Acción asociada al botón "Nuevo Empleado".
     * Abre el formulario en modo creación, pasando un objeto Empleado vacío.
     */
    @FXML
    private void onNuevoEmpleado() {
        abrirFormularioEmpleado("Nuevo Empleado", new Empleado());
    }

    /**
     * Acción asociada al botón "Editar Empleado".
     * Si hay selección, abre el formulario en modo edición con el empleado seleccionado.
     * Si no, muestra un mensaje de error.
     */
    @FXML
    private void onEditarEmpleado() {
        Empleado sel = tablaEmpleados.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError(
                    "Selecciona un empleado",
                    "Debes elegir un empleado de la lista antes de editar."
            );
            return;
        }
        abrirFormularioEmpleado("Editar Empleado", sel);
    }

    /**
     * Método auxiliar que abre un diálogo modal con el formulario de Empleado.
     * Recibe el título de la ventana y el objeto Empleado (nuevo o existente).
     * Tras cerrar, si el formulario confirmó cambios, recarga la tabla.
     * @param titulo  título de la ventana modal
     * @param objeto  instancia de Empleado a crear o editar
     */
    private void abrirFormularioEmpleado(String titulo, Empleado objeto) {
        try {
            // 1) Localiza el FXML
            URL fxmlUrl = getClass().getResource("/agrobdgui/empleadoform.fxml");
            if (fxmlUrl == null) {
                throw new IllegalStateException(
                        "No encuentro /agrobdgui/empleadoform.fxml en el classpath"
                );
            }

            // 2) Carga el layout
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // 3) Prepara la ventana modal
            Stage dialog = new Stage();
            dialog.setTitle(titulo);
            dialog.initOwner(tablaEmpleados.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));

            // 4) Inicializa el formulario con el Empleado
            EmpleadoFormController fc = loader.getController();
            fc.initForm(dialog, objeto);

            // 5) Muestra y, si confirma, recarga la tabla
            dialog.showAndWait();
            if (fc.isOkClicked()) {
                loadEmpleados();
            }

        } catch (Exception e) {
            showError(
                    "Error al abrir formulario",
                    "Comprueba la ruta y sintaxis de empleadoform.fxml",
                    e
            );
        }
    }

    /**
     * Acción asociada al botón "Eliminar Empleado".
     * Elimina de la BD al empleado seleccionado, o muestra un error si no hay selección
     * o si falla la operación.
     */
    @FXML
    private void onEliminarEmpleado() {
        Empleado sel = tablaEmpleados.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError(
                    "Selecciona un empleado",
                    "Debes elegir un empleado de la lista antes de eliminar."
            );
            return;
        }
        try {
            EmpleadoDAO.delete(sel.getIdUsuario());

            loadEmpleados();
        } catch (Exception e) {
            showError(
                    "Error al eliminar empleado",
                    "No se pudo borrar al empleado de la base de datos",
                    e
            );
        }
    }

    /**
     * Muestra un diálogo de alerta de tipo ERROR con detalles de excepción.
     * @param header texto de cabecera de la alerta
     * @param msg    mensaje descriptivo del error
     * @param e      excepción capturada para imprimir stack trace y toString()
     */
    private void showError(String header, String msg, Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(msg + "\n\n" + e.toString());
        alert.showAndWait();
    }

    /**
     * Sobrecarga de showError para errores sin excepción asociada.
     * @param header texto de cabecera de la alerta
     * @param msg    mensaje descriptivo del error
     */
    private void showError(String header, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}







