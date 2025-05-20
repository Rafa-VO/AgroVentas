package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.DAO.PedidoDAO;
import com.miempresa.agroventas.DAO.ValidacionDAO;
import com.miempresa.agroventas.interfaces.EstadosPedido;
import com.miempresa.agroventas.model.Pedido;
import com.miempresa.agroventas.model.Validacion;
import com.miempresa.agroventas.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;

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

    @FXML
    public void initialize() {
        // Cargamos los valores del enum
        cbEstado.getItems().setAll(
                EstadosPedido.PENDIENTE.name(),
                EstadosPedido.ENVIADO.name(),
                EstadosPedido.ENTREGADO.name(),
                EstadosPedido.CANCELADO.name()
        );
    }

    /** Debe invocarse desde quien abra este diálogo */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    /** Rellena los datos del pedido y de la validación previa si existe */
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
            // si no había validación previa, ignoramos
        }
    }

    @FXML
    private void onGuardar() {
        try {
            // 1) Actualizamos el estado en la tabla pedido
            EstadosPedido nuevoEstado = EstadosPedido.valueOf(cbEstado.getValue());
            pedido.setEstado(nuevoEstado);
            pedidoDAO.update(pedido);

            // 2) Creamos o actualizamos la validación
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
                    "Error al guardar:\n" + ex.getMessage())
                    .showAndWait();
        }
    }

    @FXML
    private void onCancelar() {
        dialogStage.close();
    }
}



