package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.DAO.PedidoDAO;
import com.miempresa.agroventas.DAO.ValidacionDAO;
import com.miempresa.agroventas.interfaces.EstadosPedido;
import com.miempresa.agroventas.model.Pedido;
import com.miempresa.agroventas.model.Validacion;
import com.miempresa.agroventas.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.time.LocalDateTime;

/**
 * Controlador del formulario para visualizar y modificar el estado de un pedido
 * y para registrar o actualizar la validación realizada por un empleado.
 */
public class PedidoStateController {

    @FXML private Label lblId;
    @FXML private Label lblFecha;
    @FXML private ComboBox<String> cbEstado;
    @FXML private TextArea taComentario;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final ValidacionDAO validacionDAO = new ValidacionDAO();
    private Pedido pedido;
    private Validacion validacion;
    private Stage dialogStage;

    /**
     * Inicializa el ComboBox de estados con los valores definidos en el enum EstadosPedido.
     * Este método se invoca automáticamente después de cargar el FXML.
     */
    @FXML
    public void initialize() {
        cbEstado.getItems().setAll(
                EstadosPedido.PENDIENTE.name(),
                EstadosPedido.ENVIADO.name(),
                EstadosPedido.ENTREGADO.name(),
                EstadosPedido.CANCELADO.name()
        );
    }

    /**
     * Asigna el Stage que contiene este formulario. Es necesario para mostrar
     * alertas modales asociadas a este diálogo.
     * @param stage el Stage modal padre de este formulario
     */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    /**
     * Carga los datos del pedido en los controles de la vista. Muestra el identificador
     * y la fecha formateada del pedido. Selecciona en el ComboBox el estado actual
     * y, si existe una validación previa hecha por el empleado en sesión, carga su comentario.
     * @param p objeto Pedido cuya información se desea mostrar y editar
     */
    public void setPedido(Pedido p) {
        this.pedido = p;
        lblId.setText(String.valueOf(p.getIdPedido()));
        lblFecha.setText(p.getFechaPedido().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
        ));
        cbEstado.setValue(p.getEstado().name());

        try {
            validacion = validacionDAO.findById(
                    Session.getUsuario().getIdUsuario(),
                    p.getIdPedido()
            );
            if (validacion != null) {
                taComentario.setText(validacion.getComentarioValidacion());
            }
        } catch (Exception e) {
            // Si no hubo validación previa, no se realiza ninguna acción.
        }
    }

    /**
     * Al pulsar Guardar, actualiza primero el estado del pedido en la tabla Pedido.
     * A continuación crea una nueva validación o actualiza la existente en la tabla Validacion
     * con la fecha actual y el comentario introducido. Finalmente cierra el diálogo.
     * En caso de error muestra una alerta de tipo ERROR con el mensaje de la excepción.
     */
    @FXML
    private void onGuardar() {
        try {
            EstadosPedido nuevoEstado = EstadosPedido.valueOf(cbEstado.getValue());
            pedido.setEstado(nuevoEstado);
            pedidoDAO.update(pedido);

            LocalDateTime ahora = LocalDateTime.now();
            if (validacion == null) {
                validacion = new Validacion();
                validacion.setIdPedido(pedido.getIdPedido());
                validacion.setIdEmpleado(Session.getUsuario().getIdUsuario());
                validacion.setFechaValidacion(ahora);
                validacion.setComentarioValidacion(taComentario.getText());
                validacionDAO.create(validacion);
            } else {
                validacion.setFechaValidacion(ahora);
                validacion.setComentarioValidacion(taComentario.getText());
                validacionDAO.update(validacion);
            }

            dialogStage.close();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR,
                    "Error al guardar:\n" + ex.getMessage()
            ).showAndWait();
        }
    }

    /**
     * Cierra el diálogo sin guardar cambios cuando el usuario pulsa el botón Cancelar.
     */
    @FXML
    private void onCancelar() {
        dialogStage.close();
    }
}




