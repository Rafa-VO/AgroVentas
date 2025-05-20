package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.DAO.ClienteDAO;
import com.miempresa.agroventas.DAO.UsuarioDAO;
import com.miempresa.agroventas.model.Cliente;
import com.miempresa.agroventas.model.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controlador para el formulario de creación/edición de clientes.
 * Gestiona la vinculación de un Usuario a un Cliente, así como la introducción
 * de dirección y teléfono. Se usa desde ClienteController.
 */
public class ClienteFormController {

    @FXML private ComboBox<Usuario> cbUsuario;
    @FXML private TextField       tfDireccion, tfTelefono;

    private Stage dialogStage;
    private Cliente cliente;
    private boolean okClicked = false;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Establece el Stage del diálogo, necesario para posicionar las alertas
     * de error como hijos de esta ventana modal.
     * @param stage la ventana (Stage) que contiene este formulario
     */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    /**
     * Configura el objeto Cliente que se va a crear o editar.
     * Si el ID es distinto de cero, asume modo edición: precarga los datos
     * y bloquea el ComboBox de usuario.
     * @param c instancia de Cliente a editar o nueva (ID=0) para creación
     */
    public void setCliente(Cliente c) {
        this.cliente = c;
        if (c.getIdUsuario() != 0) {
            // Modo edición: precarga usuario, dirección y teléfono
            try {
                Usuario u = usuarioDAO.findById(c.getIdUsuario());
                cbUsuario.getSelectionModel().select(u);
            } catch (Exception e) {
                throw new RuntimeException("No se pudo cargar el usuario para edición", e);
            }
            tfDireccion.setText(c.getDireccion());
            tfTelefono.setText(c.getTelefono());
            cbUsuario.setDisable(true);  // no permitir cambio de usuario en edición
        }
    }

    /**
     * Inicializa el formulario:
     * 1) Carga todos los usuarios.
     * 2) Elimina del listado los que ya están asociados a un Cliente.
     * 3) Configura cómo se muestran en el ComboBox (converter y cell factory).
     * 4) Rellena el ComboBox con la lista filtrada.
     * Se ejecuta automáticamente tras cargar el FXML.
     */
    @FXML
    public void initialize() {
        try {
            // 1) Lee todos los usuarios
            var usuarios = usuarioDAO.findAll();
            // 2) IDs de los usuarios que ya tienen cliente
            Set<Integer> idsClientes = clienteDAO.findAll().stream()
                    .map(Cliente::getIdUsuario)
                    .collect(Collectors.toSet());
            // 3) Filtrar usuarios ya asignados
            usuarios.removeIf(u -> idsClientes.contains(u.getIdUsuario()));

            // 4) Configurar visualización en el ComboBox
            cbUsuario.setConverter(new StringConverter<>() {
                @Override
                public String toString(Usuario u) {
                    if (u == null) return "";
                    return String.format("[%d] %s %s",
                            u.getIdUsuario(), u.getNombre(), u.getApellidos());
                }
                @Override
                public Usuario fromString(String string) {
                    return null; // no usado en este contexto
                }
            });
            cbUsuario.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Usuario u, boolean empty) {
                    super.updateItem(u, empty);
                    if (empty || u == null) {
                        setText(null);
                    } else {
                        setText(String.format(
                                "[%d] %s %s — %s",
                                u.getIdUsuario(), u.getNombre(), u.getApellidos(), u.getCorreo()
                        ));
                    }
                }
            });

            // 5) Rellenar ComboBox con usuarios disponibles
            cbUsuario.setItems(FXCollections.observableArrayList(usuarios));

        } catch (Exception e) {
            showError("Error al cargar usuarios", e.getMessage());
        }
    }

    /**
     * Indica si el usuario ha pulsado "Guardar" con datos válidos.
     * @return true si el formulario se validó y guardó correctamente
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Gestiona el evento de guardar el cliente:
     * - Valida que haya usuario seleccionado y campos no vacíos.
     * - Asigna valores al modelo Cliente.
     * - Decide entre crear o actualizar en la base de datos.
     * - Cierra el diálogo si todo salió bien, marcando okClicked = true.
     */
    @FXML
    private void onGuardar() {
        Usuario sel = cbUsuario.getValue();
        if (sel == null
                || tfDireccion.getText().isBlank()
                || tfTelefono.getText().isBlank()) {
            showError(
                    "Datos inválidos",
                    "Debe seleccionar un usuario y completar dirección y teléfono."
            );
            return;
        }
        cliente.setIdUsuario(sel.getIdUsuario());
        cliente.setDireccion(tfDireccion.getText());
        cliente.setTelefono(tfTelefono.getText());

        try {
            // Si no existe, crea; si existe, actualiza
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

    /**
     * Gestiona el evento de cancelación:
     * Cierra el diálogo sin setear okClicked, por lo que no se guardan cambios.
     */
    @FXML
    private void onCancelar() {
        dialogStage.close();
    }

    /**
     * Muestra una alerta de error modal, asociada al diálogo actual.
     * @param header  texto de cabecera de la alerta
     * @param content texto con los detalles del error
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




