package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.DAO.DetallePedidoDAO;
import com.miempresa.agroventas.DAO.MaquinariaDAO;
import com.miempresa.agroventas.DAO.PedidoDAO;
import com.miempresa.agroventas.interfaces.EstadosPedido;
import com.miempresa.agroventas.model.Cliente;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador del formulario de creación, edición y visualización de pedidos.
 * Gestiona la selección de fecha, comentario y líneas de detalle de maquinaria,
 * así como la persistencia del pedido y sus detalles en la base de datos.
 */
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

    private Stage dialogStage;
    private boolean okClicked = false;
    private final ObservableList<DetallePedido> lineas = FXCollections.observableArrayList();

    /**
     * Inicializa los controles del formulario tras cargar el FXML.
     * Se establece la fecha por defecto, el valor inicial del spinner,
     * la configuración de las columnas de la tabla de líneas y la carga
     * del catálogo de maquinaria en el ComboBox.
     */
    @FXML
    private void initialize() {
        dpFecha.setValue(LocalDate.now());
        spCantidad.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1)
        );

        colNombre.setCellValueFactory(cd -> {
            try {
                Maquinaria m = cd.getValue().getMaquinaria();
                return new SimpleStringProperty(m.getNombre());
            } catch (Exception e) {
                return new SimpleStringProperty("");
            }
        });
        colTipo.setCellValueFactory(cd -> {
            try {
                Maquinaria m = cd.getValue().getMaquinaria();
                return new SimpleStringProperty(m.getTipo());
            } catch (Exception e) {
                return new SimpleStringProperty("");
            }
        });
        colDescripcion.setCellValueFactory(cd -> {
            try {
                Maquinaria m = cd.getValue().getMaquinaria();
                return new SimpleStringProperty(m.getDescripcion());
            } catch (Exception e) {
                return new SimpleStringProperty("");
            }
        });
        colCantidad.setCellValueFactory(cd ->
                new SimpleIntegerProperty(cd.getValue().getCantidad()).asObject()
        );

        colPrecio.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                setText(empty || precio == null ? null : String.format("%.2f €", precio));
            }
        });
        colPrecio.setCellValueFactory(cd ->
                new SimpleDoubleProperty(cd.getValue().getPrecioUnitario()).asObject()
        );

        colSubtotal.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double sub, boolean empty) {
                super.updateItem(sub, empty);
                setText(empty || sub == null ? null : String.format("%.2f €", sub));
            }
        });
        colSubtotal.setCellValueFactory(cd -> {
            double sub = cd.getValue().getCantidad() * cd.getValue().getPrecioUnitario();
            return new SimpleDoubleProperty(sub).asObject();
        });

        tvLineas.setItems(lineas);

        try {
            cbMaquinaria.setItems(
                    FXCollections.observableArrayList(MaquinariaDAO.findAll())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        actualizarTotal();
    }

    /**
     * Asigna el Stage modal que contiene este formulario,
     * necesario para asociar alertas de error o confirmación.
     * @param stage ventana modal padre
     */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    /**
     * Indica si el usuario ha confirmado el formulario pulsando "Guardar"
     * y los datos se han persistido correctamente.
     * @return true si el formulario se guardó con éxito
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Añade o incrementa una línea de pedido según la maquinaria
     * y cantidad seleccionadas. Si la misma maquinaria ya existe
     * en la lista, suma la cantidad; de lo contrario crea una nueva línea.
     * Actualiza el total tras la operación.
     */
    @FXML
    private void onAgregarLinea() {
        Maquinaria m = cbMaquinaria.getValue();
        int qty = spCantidad.getValue();
        if (m == null || qty <= 0) return;

        DetallePedido existente = lineas.stream()
                .filter(dp -> dp.getMaquinaria().getIdMaquinaria() == m.getIdMaquinaria())
                .findFirst()
                .orElse(null);

        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + qty);
            tvLineas.refresh();
        } else {
            DetallePedido dp = new DetallePedido();
            dp.setMaquinaria(m);
            dp.setCantidad(qty);
            dp.setPrecioUnitario(m.getPrecio());
            lineas.add(dp);
        }

        actualizarTotal();
    }

    /**
     * Elimina la línea de detalle de pedido seleccionada en la tabla
     * y recalcula el total. Si no hay selección no realiza ninguna acción.
     */
    @FXML
    private void onEliminarLinea() {
        DetallePedido sel = tvLineas.getSelectionModel().getSelectedItem();
        if (sel != null) {
            lineas.remove(sel);
            actualizarTotal();
        }
    }

    /**
     * Persiste el pedido con sus detalles en la base de datos.
     * Se crea un nuevo objeto Pedido con el cliente en sesión, fecha,
     * estado PENDIENTE y comentario, y a continuación cada DetallePedido.
     * Si la operación es correcta marca okClicked y cierra el diálogo;
     * en caso contrario muestra una alerta de error.
     */
    @FXML
    private void onGuardar() {
        try {
            Cliente cliente = Session.getCurrentCliente();

            Pedido p = new Pedido();

            p.setFechaPedido(dpFecha.getValue());
            p.setEstado(EstadosPedido.PENDIENTE);
            p.setComentario(taComentario.getText());

            p.setCliente(cliente);

            cliente.anadirPedido(p);

            for (DetallePedido dp : lineas) {
                dp.setPedido(p);
                DetallePedidoDAO.create(dp);
            }

            okClicked = true;
            dialogStage.close();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR,
                    "No se pudo guardar el pedido:\n" + ex.getMessage()
            ).showAndWait();
        }
    }

    /**
     * Cancela la operación y cierra el diálogo sin persistir cambios.
     */
    @FXML
    private void onCancelar() {
        dialogStage.close();
    }

    /**
     * Configura el formulario en modo sólo lectura para mostrar
     * un pedido existente. Rellena fecha, comentario y líneas,
     * deshabilita todos los controles de edición y recalcula el total.
     * @param pedido pedido a visualizar en modo sólo lectura
     */
    public void setReadOnly(Pedido pedido) {
        dpFecha.setValue(pedido.getFechaPedido());
        taComentario.setText(pedido.getComentario());

        try {
            // Cargar todas las maquinarias una vez
            List<Maquinaria> todasLasMaquinarias = MaquinariaDAO.findAll();

            // Pasarlas al método que ahora necesita esa lista
            List<DetallePedido> detalles = DetallePedidoDAO.findByPedidoId(pedido.getIdPedido(), todasLasMaquinarias);

            lineas.clear();
            lineas.addAll(detalles);
        } catch (Exception e) {
            e.printStackTrace();
        }

        actualizarTotal();

        dpFecha.setDisable(true);
        taComentario.setDisable(true);
        cbMaquinaria.setDisable(true);
        spCantidad.setDisable(true);
        btnAgregarLinea.setDisable(true);
        btnEliminarLinea.setDisable(true);
        btnGuardar.setDisable(true);
        btnCancelar.setDisable(true);
    }


    /**
     * Recalcula la suma de todos los subtotales de las líneas
     * y actualiza la etiqueta lblTotal con el valor formateado en euros.
     */
    private void actualizarTotal() {
        double suma = 0;
        for (DetallePedido dp : lineas) {
            suma += dp.getCantidad() * dp.getPrecioUnitario();
        }
        lblTotal.setText(String.format("Total: %.2f €", suma));
    }
}













