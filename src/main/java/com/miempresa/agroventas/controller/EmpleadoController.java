package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.DAO.EmpleadoDAO;
import com.miempresa.agroventas.model.Empleado;
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

public class EmpleadoController {

    @FXML private TableView<Empleado>           tablaEmpleados;
    @FXML private TableColumn<Empleado,Integer> colId;
    @FXML private TableColumn<Empleado,String>  colNombre;
    @FXML private TableColumn<Empleado,String>  colCorreo;
    @FXML private TableColumn<Empleado,String>  colDepartamento;
    @FXML private TableColumn<Empleado,String>  colCargo;
    @FXML private TableColumn<Empleado,Double>  colSalario;

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();

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

    private void loadEmpleados() {
        try {
            tablaEmpleados.setItems(
                    FXCollections.observableArrayList(empleadoDAO.findAll())
            );
        } catch (Exception e) {
            showError("Error al cargar empleados",
                    "No se pudieron leer empleados de la base de datos",
                    e);
        }
    }

    @FXML
    private void onNuevoEmpleado() {
        abrirFormularioEmpleado("Nuevo Empleado", new Empleado());
    }

    @FXML
    private void onEditarEmpleado() {
        Empleado sel = tablaEmpleados.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Selecciona un empleado",
                    "Debes elegir un empleado de la lista antes de editar.");
            return;
        }
        abrirFormularioEmpleado("Editar Empleado", sel);
    }

    private void abrirFormularioEmpleado(String titulo, Empleado objeto) {
        try {
            // 1) Localizar el FXML en el classpath
            URL fxmlUrl = getClass().getResource("/agrobdgui/empleadoform.fxml");
            if (fxmlUrl == null) {
                throw new IllegalStateException(
                        "No encuentro /agrobdgui/empleadoform.fxml en el JAR");
            }

            // 2) Cargarlo
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // 3) Preparar el Stage modal
            Stage dialog = new Stage();
            dialog.setTitle(titulo);
            dialog.initOwner(tablaEmpleados.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));

            // 4) Pasar el Empleado al formulario
            EmpleadoFormController fc = loader.getController();
            fc.initForm(dialog, objeto);

            // 5) Mostrar y, si pulsa OK, recargar tabla
            dialog.showAndWait();
            if (fc.isOkClicked()) {
                loadEmpleados();
            }

        } catch (Exception e) {
            showError("Error al abrir formulario",
                    "Comprueba la ruta y sintaxis de empleadoform.fxml",
                    e);
        }
    }

    @FXML
    private void onEliminarEmpleado() {
        Empleado sel = tablaEmpleados.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Selecciona un empleado",
                    "Debes elegir un empleado de la lista antes de eliminar.");
            return;
        }
        try {
            empleadoDAO.delete(sel.getIdUsuario());
            loadEmpleados();
        } catch (Exception e) {
            showError("Error al eliminar empleado",
                    "No se pudo borrar al empleado de la base de datos",
                    e);
        }
    }

    // Versión mejorada de showError que imprime el stack trace y muestra e.toString()
    private void showError(String header, String msg, Exception e) {
        // para ver todo en la consola
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(msg + "\n\n" + e.toString());
        alert.showAndWait();
    }
    // Sobrecarga para casos simples sin exception
    private void showError(String header, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}






