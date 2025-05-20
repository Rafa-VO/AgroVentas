package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.DAO.MaquinariaDAO;
import com.miempresa.agroventas.model.Maquinaria;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MaquinariaController {

    @FXML private TableView<Maquinaria> tablaMaquinaria;
    @FXML private TableColumn<Maquinaria,Integer> colId;
    @FXML private TableColumn<Maquinaria,String>  colNombre;
    @FXML private TableColumn<Maquinaria,String>  colDescripcion;
    @FXML private TableColumn<Maquinaria,String>  colTipo;
    @FXML private TableColumn<Maquinaria,Double>  colPrecio;
    @FXML private TableColumn<Maquinaria,Integer> colStock;

    private final MaquinariaDAO dao = new MaquinariaDAO();

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

    private void loadMaquinaria() {
        try {
            var list = dao.findAll();
            tablaMaquinaria.setItems(FXCollections.observableArrayList(list));
        } catch (Exception e) {
            showError("Error al cargar maquinaria", e.getMessage());
        }
    }

    @FXML
    private void onNuevoMaquinaria() {
        abrirFormulario(new Maquinaria(), "Nueva Maquinaria");
    }

    @FXML
    private void onEditarMaquinaria() {
        Maquinaria sel = tablaMaquinaria.getSelectionModel().getSelectedItem();
        if (sel == null) { showError("Selecciona una máquina", ""); return; }
        abrirFormulario(sel, "Editar Maquinaria");
    }

    @FXML
    private void onEliminarMaquinaria() {
        Maquinaria sel = tablaMaquinaria.getSelectionModel().getSelectedItem();
        if (sel == null) { showError("Selecciona una máquina", ""); return; }
        try {
            dao.delete(sel.getIdMaquinaria());
            loadMaquinaria();
        } catch (Exception e) {
            showError("No se pudo eliminar maquinaria", e.getMessage());
        }
    }

    private void abrirFormulario(Maquinaria m, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/agrobdgui/maquinariaForm.fxml")
            );
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.initOwner(tablaMaquinaria.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(titulo);
            dialog.setScene(new Scene(root));

            MaquinariaFormController ctrl = loader.getController();
            ctrl.setDialogStage(dialog);
            ctrl.setMaquinaria(m);

            dialog.showAndWait();
            if (ctrl.isOkClicked()) loadMaquinaria();
        } catch (Exception e) {
            showError("No se pudo abrir formulario", e.getMessage());
        }
    }

    private void showError(String header, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR, content);
        a.setHeaderText(header);
        a.showAndWait();
    }
}

