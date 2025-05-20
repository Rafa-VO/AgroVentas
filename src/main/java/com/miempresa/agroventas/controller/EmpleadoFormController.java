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

public class EmpleadoFormController {

    @FXML private ComboBox<Usuario> cbUsuario;
    @FXML private TextField       tfDepartamento, tfCargo, tfSalario;

    private Stage dialogStage;
    private Empleado empleado;
    private boolean okClicked = false;

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final UsuarioDAO usuarioDAO   = new UsuarioDAO();

    /**
     * Inicializa el diálogo con un Stage modal y el objeto a crear/editar.
     * Se encarga también de configurar el ComboBox (filtrado y formato).
     */
    public void initForm(Stage stage, Empleado e) {
        this.dialogStage = stage;
        this.empleado    = e;

        // 1) carga y filtra usuarios
        try {
            var todos = usuarioDAO.findAll();
            var pendientes = todos.stream()
                    // sólo los que NO tienen ya empleado, o bien el mismo si editamos
                    .filter(u -> {
                        try {
                            boolean yaEs = empleadoDAO.findById(u.getIdUsuario()) != null;
                            return !yaEs || u.getIdUsuario() == e.getIdUsuario();
                        } catch (Exception ex) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
            cbUsuario.setItems(FXCollections.observableArrayList(pendientes));
        } catch (Exception ex) {
            throw new RuntimeException("No pude cargar usuarios para EmpleadoForm", ex);
        }

        // 2) configura el StringConverter + CellFactory para que se vea bonito
        cbUsuario.setConverter(new StringConverter<>() {
            @Override
            public String toString(Usuario u) {
                if (u == null) return "";
                return "[" + u.getIdUsuario() + "] "
                        + u.getNombre() + " " + u.getApellidos()
                        + " — " + u.getCorreo();
            }
            @Override
            public Usuario fromString(String s) {
                return null; // no editable text
            }
        });
        cbUsuario.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Usuario u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null
                        ? ""
                        : "[" + u.getIdUsuario() + "] "
                        + u.getNombre() + " " + u.getApellidos()
                        + " — " + u.getCorreo()
                );
            }
        });

        // 3) si venimos de editar, preselecciona y rellena campos
        if (e.getIdUsuario() != 0) {
            try {
                var seleccionado = usuarioDAO.findById(e.getIdUsuario());
                cbUsuario.getSelectionModel().select(seleccionado);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            cbUsuario.setDisable(true);

            tfDepartamento.setText(e.getDepartamento());
            tfCargo.setText(e.getCargo());
            tfSalario.setText(Double.toString(e.getSalario()));
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void onGuardar() {
        Usuario sel = cbUsuario.getValue();
        if (sel == null
                || tfDepartamento.getText().isBlank()
                || tfCargo.getText().isBlank()
                || tfSalario.getText().isBlank())
        {
            showError("Datos inválidos",
                    "Debe seleccionar un usuario y cubrir todos los campos.");
            return;
        }

        try {
            empleado.setIdUsuario(sel.getIdUsuario());
            empleado.setDepartamento(tfDepartamento.getText());
            empleado.setCargo(tfCargo.getText());
            empleado.setSalario(Double.parseDouble(tfSalario.getText()));

            // decide entre alta y modificación
            if (empleadoDAO.findById(empleado.getIdUsuario()) == null) {
                empleadoDAO.create(empleado);
            } else {
                empleadoDAO.update(empleado);
            }

            okClicked = true;
            dialogStage.close();

        } catch (NumberFormatException ex) {
            showError("Formato inválido", "El salario debe ser un número.");
        } catch (Exception ex) {
            showError("Error al guardar empleado", ex.getMessage());
        }
    }

    @FXML
    private void onCancelar() {
        dialogStage.close();
    }

    private void showError(String header, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(header);
        a.setContentText(content);
        a.initOwner(dialogStage);
        a.showAndWait();
    }
}
