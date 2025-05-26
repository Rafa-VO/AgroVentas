package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.DAO.MaquinariaDAO;
import com.miempresa.agroventas.model.Empleado;
import com.miempresa.agroventas.model.Maquinaria;
import com.miempresa.agroventas.util.Session;
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
 * Controlador de la vista de gestión de Maquinaria. Permite listar todas las máquinas,
 * crear una nueva, editar la seleccionada y eliminarla de la base de datos.
 */
public class MaquinariaController {

    @FXML private TableView<Maquinaria> tablaMaquinaria;
    @FXML private TableColumn<Maquinaria,Integer> colId;
    @FXML private TableColumn<Maquinaria,String>  colNombre;
    @FXML private TableColumn<Maquinaria,String>  colDescripcion;
    @FXML private TableColumn<Maquinaria,String>  colTipo;
    @FXML private TableColumn<Maquinaria,Double>  colPrecio;
    @FXML private TableColumn<Maquinaria,Integer> colStock;

    /**
     * Inicializa las columnas de la tabla vinculándolas a las propiedades
     * del modelo Maquinaria y carga los datos llamando a loadMaquinaria.
     * Este método se invoca automáticamente después de cargar el FXML.
     */
    @FXML
    public void initialize() {
        colId.setCellValueFactory(m -> new ReadOnlyObjectWrapper<>(m.getValue().getIdMaquinaria()));
        colNombre.setCellValueFactory(m -> new ReadOnlyObjectWrapper<>(m.getValue().getNombre()));
        colDescripcion.setCellValueFactory(m -> new ReadOnlyObjectWrapper<>(m.getValue().getDescripcion()));
        colTipo.setCellValueFactory(m -> new ReadOnlyObjectWrapper<>(m.getValue().getTipo()));
        colPrecio.setCellValueFactory(m -> new ReadOnlyObjectWrapper<>(m.getValue().getPrecio()));
        colStock.setCellValueFactory(m -> new ReadOnlyObjectWrapper<>(m.getValue().getStock()));
        loadMaquinaria();
    }

    /**
     * Obtiene la lista completa de maquinaria desde el DAO y la asigna
     * a la tabla. Si ocurre un error en la consulta, muestra un diálogo de error.
     */
    private void loadMaquinaria() {
        try {
            var list = Session.getCurrentEmpleado().getMaquinarias();
            tablaMaquinaria.setItems(FXCollections.observableArrayList(list));
        } catch (Exception e) {
            showError("Error al cargar maquinaria", e.getMessage());
        }
    }

    /**
     * Abre el formulario en modo creación pasando una nueva instancia de Maquinaria.
     * Tras cerrar, si el usuario confirma la operación, recarga la tabla.
     */
    @FXML
    private void onNuevoMaquinaria() {
        abrirFormulario(new Maquinaria(), "Nueva Maquinaria");
    }

    /**
     * Toma la maquinaria seleccionada en la tabla y abre el formulario de edición.
     * Si no hay ninguna seleccionada, muestra un error y retorna sin acción.
     */
    @FXML
    private void onEditarMaquinaria() {
        Maquinaria sel = tablaMaquinaria.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Selecciona una máquina", "");
            return;
        }
        abrirFormulario(sel, "Editar Maquinaria");
    }

    /**
     * Elimina la maquinaria seleccionada tras confirmación implícita.
     * Si no hay selección, muestra un error. Después de eliminar recarga la tabla.
     */
    @FXML
    private void onEliminarMaquinaria() {
        Maquinaria sel = tablaMaquinaria.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Selecciona una máquina", "");
            return;
        }
        try {
            Session.getCurrentEmpleado().eliminarMaquinaria(sel);
            loadMaquinaria();
        } catch (Exception e) {
            showError("No se pudo eliminar maquinaria", e.getMessage());
        }
    }

    /**
     * Método interno que abre un diálogo modal con el formulario de Maquinaria.
     * Recibe la instancia a editar o crear, el título de la ventana y tras cerrar
     * recarga la tabla si se confirmó la operación.
     * @param m       objeto Maquinaria a crear o editar
     * @param titulo  texto que se mostrará en la cabecera de la ventana
     */
    private void abrirFormulario(Maquinaria m, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/agrobdgui/maquinariaForm.fxml")
            );
            Stage dialog = new Stage();
            dialog.initOwner(tablaMaquinaria.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(titulo);
            dialog.setScene(new Scene(loader.load()));

            MaquinariaFormController ctrl = loader.getController();
            ctrl.setDialogStage(dialog);
            ctrl.setMaquinaria(m);

            dialog.showAndWait();
            if (ctrl.isOkClicked()) {
                loadMaquinaria();
            }
        } catch (Exception e) {
            showError("No se pudo abrir formulario", e.getMessage());
        }
    }

    /**
     * Muestra un diálogo de alerta de tipo ERROR con el encabezado y mensaje especificados.
     * @param header   texto principal de la alerta
     * @param content  detalle o descripción del error
     */
    private void showError(String header, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR, content);
        a.setHeaderText(header);
        a.showAndWait();
    }
}


