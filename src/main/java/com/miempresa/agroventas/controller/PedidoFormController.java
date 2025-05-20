package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.DAO.DetallePedidoDAO;
import com.miempresa.agroventas.DAO.MaquinariaDAO;
import com.miempresa.agroventas.DAO.PedidoDAO;
import com.miempresa.agroventas.interfaces.EstadosPedido;
import com.miempresa.agroventas.model.DetallePedido;
import com.miempresa.agroventas.model.Maquinaria;
import com.miempresa.agroventas.model.Pedido;
import com.miempresa.agroventas.util.Session;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class PedidoFormController {

    @FXML private DatePicker dpFecha;
    @FXML private TextArea  taComentario;

    @FXML private ComboBox<Maquinaria> cbMaquinaria;
    @FXML private Spinner<Integer>     spCantidad;

    @FXML private Button btnAgregarLinea;
    @FXML private Button btnEliminarLinea;

    @FXML private TableView<DetallePedido> tvLineas;
    @FXML private TableColumn<DetallePedido,String> colNombre;
    @FXML private TableColumn<DetallePedido,String> colTipo;
    @FXML private TableColumn<DetallePedido,String> colDescripcion;
    @FXML private TableColumn<DetallePedido,Integer> colCantidad;
    @FXML private TableColumn<DetallePedido,Double>  colPrecio;
    @FXML private TableColumn<DetallePedido,Double>  colSubtotal;

    @FXML private Label lblTotal;

    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private final PedidoDAO         pedidoDAO     = new PedidoDAO();
    private final DetallePedidoDAO  detalleDAO    = new DetallePedidoDAO();
    private final MaquinariaDAO     maquinariaDAO = new MaquinariaDAO();

    private Stage dialogStage;
    private boolean okClicked = false;
    private final ObservableList<DetallePedido> lineas = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // 1) Fecha por defecto
        dpFecha.setValue(LocalDate.now());

        // 2) Spinner cantidad
        spCantidad.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1)
        );

        // 3) Configuración columnas
        colNombre.setCellValueFactory(cd -> {
            try {
                Maquinaria m = maquinariaDAO.findById(cd.getValue().getIdMaquinaria());
                return new SimpleStringProperty(m.getNombre());
            } catch (Exception e) {
                return new SimpleStringProperty("");
            }
        });

        colTipo.setCellValueFactory(cd -> {
            try {
                Maquinaria m = maquinariaDAO.findById(cd.getValue().getIdMaquinaria());
                return new SimpleStringProperty(m.getTipo());
            } catch (Exception e) {
                return new SimpleStringProperty("");
            }
        });

        colDescripcion.setCellValueFactory(cd -> {
            try {
                Maquinaria m = maquinariaDAO.findById(cd.getValue().getIdMaquinaria());
                return new SimpleStringProperty(m.getDescripcion());
            } catch (Exception e) {
                return new SimpleStringProperty("");
            }
        });

        colCantidad.setCellValueFactory(cd ->
                new SimpleIntegerProperty(cd.getValue().getCantidad()).asObject()
        );

        // P.Unitario (€)
        colPrecio.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                setText(empty || precio == null
                        ? null
                        : String.format("%.2f €", precio));
            }
        });
        colPrecio.setCellValueFactory(cd ->
                new SimpleDoubleProperty(cd.getValue().getPrecioUnitario()).asObject()
        );

        // Subtotal (€)
        colSubtotal.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double sub, boolean empty) {
                super.updateItem(sub, empty);
                setText(empty || sub == null
                        ? null
                        : String.format("%.2f €", sub));
            }
        });
        colSubtotal.setCellValueFactory(cd -> {
            double sub = cd.getValue().getCantidad() * cd.getValue().getPrecioUnitario();
            return new SimpleDoubleProperty(sub).asObject();
        });

        tvLineas.setItems(lineas);

        // 4) Carga catálogo de maquinaria
        try {
            cbMaquinaria.setItems(
                    FXCollections.observableArrayList(maquinariaDAO.findAll())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 5) Total inicial
        actualizarTotal();
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }
    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void onAgregarLinea() {
        Maquinaria m = cbMaquinaria.getValue();
        int qty = spCantidad.getValue();
        if (m == null || qty <= 0) return;

        // Si ya hay línea, acumular
        DetallePedido existente = null;
        for (DetallePedido dp : lineas) {
            if (dp.getIdMaquinaria() == m.getIdMaquinaria()) {
                existente = dp;
                break;
            }
        }
        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + qty);
            tvLineas.refresh();
        } else {
            DetallePedido dp = new DetallePedido();
            dp.setIdMaquinaria(m.getIdMaquinaria());
            dp.setCantidad(qty);
            dp.setPrecioUnitario(m.getPrecio());
            lineas.add(dp);
        }

        actualizarTotal();
    }

    @FXML
    private void onEliminarLinea() {
        DetallePedido sel = tvLineas.getSelectionModel().getSelectedItem();
        if (sel != null) {
            lineas.remove(sel);
            actualizarTotal();
        }
    }

    @FXML
    private void onGuardar() {
        try {
            Pedido p = new Pedido();
            p.setIdCliente(Session.getUsuario().getIdUsuario());
            p.setFechaPedido(dpFecha.getValue());
            p.setEstado(EstadosPedido.PENDIENTE);
            p.setComentario(taComentario.getText());
            pedidoDAO.create(p);

            for (DetallePedido dp : lineas) {
                dp.setIdPedido(p.getIdPedido());
                detalleDAO.create(dp);
            }

            okClicked = true;
            dialogStage.close();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR,
                    "No se pudo guardar el pedido:\n" + ex.getMessage()
            ).showAndWait();
        }
    }

    @FXML
    private void onCancelar() {
        dialogStage.close();
    }

    /**
     * Carga un pedido en modo SOLO LECTURA:
     * - rellena fecha/comentario/líneas
     * - deshabilita **todos** los controles de edición
     */
    public void setReadOnly(Pedido pedido) {
        // 1) Rellenar datos
        dpFecha.setValue(pedido.getFechaPedido());
        taComentario.setText(pedido.getComentario());

        try {
            List<DetallePedido> detalles =
                    detalleDAO.findByPedidoId(pedido.getIdPedido());
            lineas.clear();
            lineas.addAll(detalles);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2) recalcular total
        actualizarTotal();

        // 3) deshabilitar todo
        dpFecha.setDisable(true);
        taComentario.setDisable(true);
        cbMaquinaria.setDisable(true);
        spCantidad.setDisable(true);
        btnAgregarLinea.setDisable(true);
        btnEliminarLinea.setDisable(true);
        btnGuardar.setDisable(true);
        btnCancelar.setDisable(true);
    }

    /** Recalcula y muestra el total con “€” */
    private void actualizarTotal() {
        double suma = 0;
        for (DetallePedido dp : lineas) {
            suma += dp.getCantidad() * dp.getPrecioUnitario();
        }
        lblTotal.setText(String.format("Total: %.2f €", suma));
    }
}












