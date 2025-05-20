package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.DAO.UsuarioDAO;
import com.miempresa.agroventas.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controlador del formulario de creación y edición de usuarios. Se encarga
 * de inicializar los campos con los datos del Usuario, validar la entrada
 * y persistir los cambios mediante UsuarioDAO.
 */
public class UsuarioFormController {

    @FXML private TextField tfNombre;
    @FXML private TextField tfApellidos;
    @FXML private TextField tfCorreo;
    @FXML private PasswordField tfContrasena;

    private Stage dialogStage;
    private Usuario usuario;
    private boolean okClicked = false;

    private final UsuarioDAO dao = new UsuarioDAO();

    /**
     * Guarda la referencia al Stage modal que contiene este formulario.
     * Es utilizado al mostrar alertas de validación o errores.
     * @param stage ventana modal padre de este formulario
     */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    /**
     * Inyecta el objeto Usuario a editar o inicializa para uno nuevo.
     * Rellena los campos de texto con los valores actuales del Usuario.
     * @param u instancia de Usuario a editar o con id 0 para creación
     */
    public void setUsuario(Usuario u) {
        this.usuario = u;
        tfNombre.setText(u.getNombre());
        tfApellidos.setText(u.getApellidos());
        tfCorreo.setText(u.getCorreo());
        tfContrasena.setText(u.getContrasena());
    }

    /**
     * Devuelve el Usuario asociado a este formulario, con los posibles
     * cambios volcados tras pulsar Guardar.
     * @return objeto Usuario editado
     */
    public Usuario getUsuario() {
        return this.usuario;
    }

    /**
     * Indica si el usuario confirmó la operación pulsando Guardar
     * y los cambios se guardaron correctamente.
     * @return true si se guardó con éxito
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Método que maneja el evento de pulsar Guardar. Valida los campos
     * obligatorios y, si son correctos, actualiza el objeto Usuario con
     * los valores de los campos de texto y llama al DAO para crear o actualizar.
     * En caso de éxito, marca okClicked como true y cierra el diálogo.
     * Si ocurre un error en la persistencia, muestra una alerta con el mensaje.
     */
    @FXML
    private void onGuardar() {
        if (!validateInput()) {
            return;
        }
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

    /**
     * Cierra el diálogo sin guardar cambios cuando el usuario pulsa Cancelar.
     */
    @FXML
    private void onCancelar() {
        dialogStage.close();
    }

    /**
     * Valida que los campos Nombre, Apellidos, Correo y Contraseña no estén vacíos.
     * Si existen errores, muestra una alerta indicando qué campos corregir.
     * @return true si todos los campos son válidos, false en caso contrario
     */
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

    /**
     * Muestra una alerta de tipo ERROR con un título y mensaje especificados.
     * Se asocia al Stage de este formulario.
     * @param title encabezado de la alerta
     * @param msg   contenido detallado del error
     */
    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(title);
        a.setContentText(msg);
        a.initOwner(dialogStage);
        a.showAndWait();
    }
}


