package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.DAO.ClienteDAO;
import com.miempresa.agroventas.DAO.UsuarioDAO;
import com.miempresa.agroventas.model.Cliente;
import com.miempresa.agroventas.model.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.Set;
import java.util.stream.Collectors;

public class ClienteFormController {

    @FXML private ComboBox<Usuario> cbUsuario;
    @FXML private TextField       tfDireccion, tfTelefono;

    private Stage dialogStage;
    private Cliente cliente;
    private boolean okClicked = false;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /** Inyectado desde el ClienteController antes de mostrar el diálogo */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    /** Carga el Cliente (para edición) o deja fields en blanco (nuevo) */
    public void setCliente(Cliente c) {
        this.cliente = c;
        if (c.getIdUsuario() != 0) {
            // Estamos en modo edición: preselecciona y bloquea cambio de usuario
            try {
                Usuario u = usuarioDAO.findById(c.getIdUsuario());
                cbUsuario.getSelectionModel().select(u);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            tfDireccion.setText(c.getDireccion());
            tfTelefono .setText(c.getTelefono());
            cbUsuario.setDisable(true);
        }
    }

    @FXML
    public void initialize() {
        try {
            // 1) Lee todos los usuarios
            var usuarios = usuarioDAO.findAll();
            // 2) Saca los IDs que ya tienen cliente
            Set<Integer> idsClientes = clienteDAO.findAll().stream()
                    .map(Cliente::getIdUsuario)
                    .collect(Collectors.toSet());
            // 3) Filtra los que ya existen
            usuarios.removeIf(u -> idsClientes.contains(u.getIdUsuario()));

            // 4) Configura cómo se ve el combo (StringConverter + CellFactory)
            cbUsuario.setConverter(new StringConverter<>() {
                @Override
                public String toString(Usuario u) {
                    if (u == null) return "";
                    return String.format("[%d] %s %s", u.getIdUsuario(), u.getNombre(), u.getApellidos());
                }
                @Override
                public Usuario fromString(String string) {
                    return null; // no usado
                }
            });
            cbUsuario.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Usuario u, boolean empty) {
                    super.updateItem(u, empty);
                    if (empty || u == null) {
                        setText(null);
                    } else {
                        setText(String.format("[%d] %s %s \u2014 %s",
                                u.getIdUsuario(), u.getNombre(), u.getApellidos(), u.getCorreo()));
                    }
                }
            });

            // 5) Carga al ComboBox la lista filtrada
            cbUsuario.setItems(FXCollections.observableArrayList(usuarios));

        } catch (Exception e) {
            showError("Error al cargar usuarios", e.getMessage());
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void onGuardar() {
        Usuario sel = cbUsuario.getValue();
        if (sel == null || tfDireccion.getText().isBlank() || tfTelefono.getText().isBlank()) {
            showError("Datos inválidos", "Debe seleccionar un usuario y completar dirección y teléfono.");
            return;
        }
        cliente.setIdUsuario(sel.getIdUsuario());
        cliente.setDireccion(tfDireccion.getText());
        cliente.setTelefono(tfTelefono.getText());

        try {
            // Decide entre INSERT / UPDATE según exista o no en BD
            if (clienteDAO.findById(cliente.getIdUsuario()) == null) {
                clienteDAO.create(cliente);
            } else {
                clienteDAO.update(cliente);
            }
            okClicked = true;
            dialogStage.close();
        } catch (Exception e) {
            showError("Error al guardar cliente", e.getMessage());
        }
    }

    @FXML
    private void onCancelar() {
        dialogStage.close();
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initOwner(dialogStage);
        alert.showAndWait();
    }
}



