package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.DAO.EmpleadoDAO;
import com.miempresa.agroventas.DAO.UsuarioDAO;
import com.miempresa.agroventas.model.Empleado;
import com.miempresa.agroventas.model.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.stream.Collectors;

/**
 * Controlador para el formulario de creación y edición de Empleados.
 * Gestiona la selección de un Usuario y la edición de sus datos de departamento,
 * cargo y salario. Se utiliza como diálogo modal desde EmpleadoController.
 */
public class EmpleadoFormController {

    @FXML private ComboBox<Usuario> cbUsuario;
    @FXML private TextField       tfDepartamento, tfCargo, tfSalario;

    private Stage dialogStage;
    private Empleado empleado;
    private boolean okClicked = false;

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final UsuarioDAO usuarioDAO   = new UsuarioDAO();

    /**
     * Inicializa el formulario con el Stage modal y el Empleado a crear o editar.
     *1) Carga y filtra los usuarios disponibles (no asignados a otro Empleado, o el propio si estamos editando).
     *2) Configura el ComboBox para que muestre usuario con ID, nombre, apellidos y correo.
     *3) Si el Empleado ya existía (ID diferente de 0), precarga sus datos y bloquea la selección de usuario.
     * @param stage ventana modal que contiene este formulario
     * @param e     instancia de Empleado a editar, o nueva (ID=0) para creación
     */
    public void initForm(Stage stage, Empleado e) {
        this.dialogStage = stage;
        this.empleado    = e;

        // 1) Carga y filtra usuarios
        try {
            var todos = usuarioDAO.findAll();
            var pendientes = todos.stream()
                    // Solo los usuarios sin empleado o el mismo si editamos
                    .filter(u -> {
                        try {
                            boolean yaAsociado = empleadoDAO.findById(u.getIdUsuario()) != null;
                            return !yaAsociado || u.getIdUsuario() == e.getIdUsuario();
                        } catch (Exception ex) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
            cbUsuario.setItems(FXCollections.observableArrayList(pendientes));
        } catch (Exception ex) {
            throw new RuntimeException("No pude cargar usuarios para EmpleadoForm", ex);
        }

        // 2) Configura cómo mostrar cada Usuario en el ComboBox
        cbUsuario.setConverter(new StringConverter<>() {
            @Override
            public String toString(Usuario u) {
                if (u == null) return "";
                return String.format("[%d] %s %s — %s",
                        u.getIdUsuario(), u.getNombre(), u.getApellidos(), u.getCorreo());
            }
            @Override
            public Usuario fromString(String s) {
                return null; // no se permite edición manual
            }
        });
        cbUsuario.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Usuario u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null
                        ? ""
                        : String.format("[%d] %s %s — %s",
                        u.getIdUsuario(), u.getNombre(), u.getApellidos(), u.getCorreo())
                );
            }
        });

        // 3) Si es edición, preselecciona Usuario y rellena campos del Empleado
        if (e.getIdUsuario() != 0) {
            try {
                var seleccionado = usuarioDAO.findById(e.getIdUsuario());
                cbUsuario.getSelectionModel().select(seleccionado);
            } catch (Exception ex) {
                throw new RuntimeException("No pude precargar el usuario seleccionado", ex);
            }
            cbUsuario.setDisable(true);
            tfDepartamento.setText(e.getDepartamento());
            tfCargo.setText(e.getCargo());
            tfSalario.setText(Double.toString(e.getSalario()));
        }
    }

    /**
     * Indica si el usuario pulsó "Guardar" correctamente y los datos se insertaron/actualizaron.
     * @return true si el formulario validó y guardó los datos
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Maneja el clic en "Guardar":
     * Valida selección de Usuario y que todos los campos estén completos.
     * Parsea y asigna los valores al modelo Empleado.
     * Decide entre crear o actualizar via DAO según exista o no en BD.
     * En caso de éxito, marca okClicked=true y cierra el diálogo.
     * Gestiona errores de formato de salario y errores de BD, mostrando alertas.
     *
     */
    @FXML
    private void onGuardar() {
        Usuario sel = cbUsuario.getValue();
        if (sel == null || tfDepartamento.getText().isBlank() || tfCargo.getText().isBlank() || tfSalario.getText().isBlank()) {
            showError("Datos inválidos", "Debe seleccionar un usuario y cubrir todos los campos.");
            return;
        }

        try {
            empleado.setIdUsuario(sel.getIdUsuario());
            empleado.setDepartamento(tfDepartamento.getText());
            empleado.setCargo(tfCargo.getText());
            empleado.setSalario(Double.parseDouble(tfSalario.getText()));

            // Si no existe, crear; si existe, actualizar
            if (empleadoDAO.findById(empleado.getIdUsuario()) == null) {
                empleadoDAO.create(empleado);
            } else {
                empleadoDAO.update(empleado);
            }

            okClicked = true;
            dialogStage.close();

        } catch (NumberFormatException ex) {
            showError("Formato inválido", "El salario debe ser un número válido.");
        } catch (Exception ex) {
            showError("Error al guardar empleado", ex.getMessage());
        }
    }

    /**
     * Maneja el clic en "Cancelar": cierra el diálogo sin guardar cambios.
     */
    @FXML
    private void onCancelar() {
        dialogStage.close();
    }

    /**
     * Muestra una alerta de error modal asociada a este formulario.
     * @param header  texto principal de la alerta
     * @param content mensaje detallado del error
     */
    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initOwner(dialogStage);
        alert.showAndWait();
    }
}

