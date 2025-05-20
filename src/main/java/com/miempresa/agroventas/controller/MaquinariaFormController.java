package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.DAO.MaquinariaDAO;
import com.miempresa.agroventas.DAO.ProveedorDAO;
import com.miempresa.agroventas.model.Maquinaria;
import com.miempresa.agroventas.model.Proveedor;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

public class MaquinariaFormController {

    @FXML private ComboBox<Proveedor>       cbProveedor;
    @FXML private TextField                 tfNombre;
    @FXML private TextArea                  taDescripcion;
    @FXML private TextField                 tfTipo;
    @FXML private TextField                 tfPrecio;
    @FXML private Spinner<Integer>          spStock;
    @FXML private Button                    btnGuardar;
    @FXML private Button                    btnCancelar;

    private Stage dialogStage;
    private boolean okClicked = false;
    private final MaquinariaDAO dao = new MaquinariaDAO();
    private Maquinaria maquinaria;

    @FXML
    public void initialize() {
        // Spinner de stock
        spStock.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0)
        );

        // Cargamos proveedores desde la BD
        try {
            List<Proveedor> provs = new ProveedorDAO().findAll();
            cbProveedor.setItems(FXCollections.observableArrayList(provs));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Formateo de cómo se muestra cada Proveedor
        cbProveedor.setConverter(new StringConverter<>() {
            @Override
            public String toString(Proveedor p) {
                return p == null
                        ? ""
                        : "[" + p.getIdProveedor() + "] " + p.getNombre();
            }
            @Override
            public Proveedor fromString(String s) {
                return null;
            }
        });
        cbProveedor.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Proveedor p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null
                        ? null
                        : "[" + p.getIdProveedor() + "] " + p.getNombre()
                );
            }
        });
    }

    /** Debe ser llamado desde quien abra este diálogo */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    /** Rellena el form si estamos editando (ID != 0) */
    public void setMaquinaria(Maquinaria m) {
        this.maquinaria = m;
        if (m.getIdMaquinaria() != 0) {
            // preseleccionar proveedor
            try {
                Proveedor p = new ProveedorDAO().findById(m.getIdProveedor());
                if (p != null) cbProveedor.getSelectionModel().select(p);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // resto de campos
            tfNombre     .setText(m.getNombre());
            taDescripcion.setText(m.getDescripcion());
            tfTipo       .setText(m.getTipo());
            tfPrecio     .setText(Double.toString(m.getPrecio()));
            spStock.getValueFactory().setValue(m.getStock());

            // si no debe cambiar proveedor al editar:
            cbProveedor.setDisable(true);
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void onGuardar() {
        StringBuilder err = new StringBuilder();
        Proveedor sel = cbProveedor.getValue();
        if (sel == null) err.append("• Debe seleccionar un proveedor.\n");
        if (tfNombre.getText().isBlank())     err.append("• Nombre vacío.\n");
        if (tfTipo.getText().isBlank())       err.append("• Tipo vacío.\n");
        if (tfPrecio.getText().isBlank())     err.append("• Precio vacío.\n");
        else {
            try {
                Double.parseDouble(tfPrecio.getText());
            } catch (NumberFormatException ex) {
                err.append("• Precio no numérico.\n");
            }
        }

        if (err.length() > 0) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Campos inválidos");
            alerta.setHeaderText(null);
            alerta.setContentText(err.toString());
            alerta.initOwner(dialogStage);
            alerta.showAndWait();
            return;
        }

        // Asignar al modelo
        maquinaria.setIdProveedor(sel.getIdProveedor());
        maquinaria.setNombre(tfNombre.getText());
        maquinaria.setDescripcion(taDescripcion.getText());
        maquinaria.setTipo(tfTipo.getText());
        maquinaria.setPrecio(Double.parseDouble(tfPrecio.getText()));
        maquinaria.setStock(spStock.getValue());

        // Persistir
        try {
            if (maquinaria.getIdMaquinaria() == 0) {
                dao.create(maquinaria);
            } else {
                dao.update(maquinaria);
            }
            okClicked = true;
            dialogStage.close();
        } catch (Exception e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error al guardar");
            alerta.setHeaderText(null);
            alerta.setContentText(e.getMessage());
            alerta.initOwner(dialogStage);
            alerta.showAndWait();
        }
    }

    @FXML
    private void onCancelar() {
        dialogStage.close();
    }
}




