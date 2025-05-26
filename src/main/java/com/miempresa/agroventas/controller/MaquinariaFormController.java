package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.DAO.MaquinariaDAO;
import com.miempresa.agroventas.DAO.ProveedorDAO;
import com.miempresa.agroventas.model.Maquinaria;
import com.miempresa.agroventas.model.Proveedor;
import com.miempresa.agroventas.util.Session;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

/**
 * Controlador del formulario de creación y edición de objetos Maquinaria.
 * Gestiona la selección de proveedor, la entrada de datos de la máquina
 * y la validación antes de guardar o actualizar en la base de datos.
 */
public class MaquinariaFormController {

    @FXML private ComboBox<Proveedor> cbProveedor;
    @FXML private TextField tfNombre;
    @FXML private TextArea taDescripcion;
    @FXML private TextField tfTipo;
    @FXML private TextField tfPrecio;
    @FXML private Spinner<Integer> spStock;

    private Stage dialogStage;
    private boolean okClicked = false;
    private Maquinaria maquinaria;

    /**
     * Inicializa el spinner de stock con rango válido y cero por defecto.
     * Carga todos los proveedores desde la base de datos y los asigna
     * al ComboBox. Configura el formato de visualización de cada proveedor.
     * Este método se invoca automáticamente tras cargar el FXML.
     */
    @FXML
    public void initialize() {
        spStock.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0)
        );

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

        try {
            List<Proveedor> lista = new ProveedorDAO().findAll();
            cbProveedor.getItems().setAll(lista);
        } catch (Exception e) {
            e.printStackTrace();
            // o muestra alerta
        }
    }

    /**
     * Almacena la referencia al Stage modal que contiene este formulario.
     * Es necesario para asociar las alertas de validación o error.
     * @param stage la ventana modal que contiene el formulario
     */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    /**
     * Configura los campos del formulario con los datos de la maquinaria
     * si el objeto ya existe (identificador distinto de cero).
     * Preselecciona el proveedor, rellena nombre, descripción, tipo,
     * precio y stock. Deshabilita el selector de proveedor al editar.
     * @param m instancia de Maquinaria a editar o nueva con id 0 para creación
     */
    public void setMaquinaria(Maquinaria m) {
        this.maquinaria = m;
        if (m.getIdMaquinaria() != 0) {
            try {
                Proveedor p = m.getProveedor();
                if (p != null) {
                    cbProveedor.getSelectionModel().select(p);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            tfNombre.setText(m.getNombre());
            taDescripcion.setText(m.getDescripcion());
            tfTipo.setText(m.getTipo());
            tfPrecio.setText(Double.toString(m.getPrecio()));
            spStock.getValueFactory().setValue(m.getStock());
            cbProveedor.setDisable(true);
        }
    }

    /**
     * Indica si el usuario pulsó el botón Guardar y los cambios
     * se han persistido correctamente en la base de datos.
     * @return true si se confirmó y guardó el formulario
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Valida los campos del formulario. Muestra una alerta si hay
     * errores de validación. Si todos los datos son correctos,
     * asigna los valores al objeto Maquinaria y lo crea o actualiza
     * en la base de datos. Marca okClicked en true y cierra el diálogo.
     */
    @FXML
    private void onGuardar() {
        StringBuilder err = new StringBuilder();
        Proveedor sel = cbProveedor.getValue();
        if (sel == null) err.append("Debe seleccionar un proveedor.\n");
        if (tfNombre.getText().isBlank()) err.append("Nombre vacío.\n");
        if (tfTipo.getText().isBlank()) err.append("Tipo vacío.\n");
        if (tfPrecio.getText().isBlank()) err.append("Precio vacío.\n");
        else {
            try {
                Double.parseDouble(tfPrecio.getText());
            } catch (NumberFormatException ex) {
                err.append("Precio no numérico.\n");
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

        maquinaria.setProveedor(cbProveedor.getValue());
        maquinaria.setNombre(tfNombre.getText());
        maquinaria.setDescripcion(taDescripcion.getText());
        maquinaria.setTipo(tfTipo.getText());
        maquinaria.setPrecio(Double.parseDouble(tfPrecio.getText()));
        maquinaria.setStock(spStock.getValue());

        try {
            if (maquinaria.getIdMaquinaria() == 0) {
                Session.getCurrentEmpleado().anadirMaquinaria(maquinaria);
            } else {
                Session.getCurrentEmpleado().actualizarMaquinaria(maquinaria);
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

    /**
     * Cierra el diálogo sin guardar cambios cuando el usuario
     * pulsa el botón Cancelar.
     */
    @FXML
    private void onCancelar() {
        dialogStage.close();
    }
}





