package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.DAO.UsuarioDAO;
import com.miempresa.agroventas.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UsuarioFormController {

    @FXML private TextField tfNombre;
    @FXML private TextField tfApellidos;
    @FXML private TextField tfCorreo;
    @FXML private PasswordField tfContrasena;

    private Stage dialogStage;
    private Usuario usuario;            // el usuario que editamos/creamos
    private boolean okClicked = false;  // para saber si guardó o canceló

    private final UsuarioDAO dao = new UsuarioDAO();

    /** Llamado por el MainApp después de cargar el FXML. */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    /** Inyecta el Usuario a editar. */
    public void setUsuario(Usuario u) {
        this.usuario = u;
        // inicializa los campos
        tfNombre.setText(u.getNombre());
        tfApellidos.setText(u.getApellidos());
        tfCorreo.setText(u.getCorreo());
        tfContrasena.setText(u.getContrasena());
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    /** ¿Pulsó Guardar? */
    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void onGuardar() {
        if (!validateInput()) return;

        // vuelca los campos al objeto
        usuario.setNombre(tfNombre.getText());
        usuario.setApellidos(tfApellidos.getText());
        usuario.setCorreo(tfCorreo.getText());
        usuario.setContrasena(tfContrasena.getText());

        try {
            if (usuario.getIdUsuario() == 0) {
                dao.create(usuario);
            } else {
                dao.update(usuario);
            }
            okClicked = true;
            dialogStage.close();
        } catch (Exception e) {
            showError("Error al guardar usuario", e.getMessage());
        }
    }

    @FXML
    private void onCancelar() {
        dialogStage.close();
    }

    private boolean validateInput() {
        StringBuilder err = new StringBuilder();
        if (tfNombre.getText().isBlank())     err.append("– El nombre no puede quedar vacío\n");
        if (tfApellidos.getText().isBlank())  err.append("– Los apellidos no pueden quedar vacíos\n");
        if (tfCorreo.getText().isBlank())     err.append("– El correo no puede quedar vacío\n");
        if (tfContrasena.getText().isBlank()) err.append("– La contraseña no puede quedar vacía\n");

        if (err.length() > 0) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Datos inválidos");
            a.setHeaderText("Corrige los siguientes errores:");
            a.setContentText(err.toString());
            a.initOwner(dialogStage);
            a.showAndWait();
            return false;
        }
        return true;
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(title);
        a.setContentText(msg);
        a.initOwner(dialogStage);
        a.showAndWait();
    }
}

