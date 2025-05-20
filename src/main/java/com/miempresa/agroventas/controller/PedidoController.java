package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.DAO.PedidoDAO;
import com.miempresa.agroventas.model.Pedido;
import com.miempresa.agroventas.util.Session;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador para la vista de pedidos. Permite listar pedidos,
 * ver detalles, eliminar pedidos y cambiar su estado según el rol.
 */
public class PedidoController {

    @FXML private TableView<Pedido> tablePedidos;
    @FXML private TableColumn<Pedido,Integer> colId;
    @FXML private TableColumn<Pedido,String> colFecha;
    @FXML private TableColumn<Pedido,String> colEstado;
    @FXML private TableColumn<Pedido,String> colComentario;

    private final PedidoDAO dao = new PedidoDAO();
    private boolean onlyMine = false;
    private Stage dialogStage;

    /**
     * Configura si se deben cargar solo los pedidos del usuario en sesión.
     * Llama a cargarPedidos para actualizar la tabla.
     * @param onlyMine true para mostrar solo los pedidos del cliente actual
     */
    public void setOnlyMine(boolean onlyMine) {
        this.onlyMine = onlyMine;
        cargarPedidos();
    }

    /**
     * Establece el Stage padre para los diálogos modales.
     * @param stage ventana principal desde la que se abren los modales
     */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    /**
     * Inicializa las columnas de la tabla vinculándolas a las propiedades
     * del modelo Pedido. El formato de la fecha se ajusta a dd/MM/yyyy.
     * Se invoca automáticamente tras cargar el FXML.
     */
    @FXML
    public void initialize() {
        colId.setCellValueFactory(p ->
                new ReadOnlyObjectWrapper<>(p.getValue().getIdPedido())
        );
        colFecha.setCellValueFactory(p ->
                new SimpleStringProperty(
                        p.getValue().getFechaPedido()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                )
        );
        colEstado.setCellValueFactory(p ->
                new SimpleStringProperty(p.getValue().getEstado().name())
        );
        colComentario.setCellValueFactory(p ->
                new SimpleStringProperty(p.getValue().getComentario())
        );
    }

    /**
     * Carga los pedidos en la tabla. Si onlyMine es true, filtra por el cliente
     * logueado. En caso de error muestra una alerta de tipo ERROR.
     */
    private void cargarPedidos() {
        try {
            List<Pedido> lista = onlyMine
                    ? dao.findByClienteId(Session.getUsuario().getIdUsuario())
                    : dao.findAll();
            tablePedidos.getItems().setAll(lista);
        } catch (Exception e) {
            new Alert(
                    Alert.AlertType.ERROR,
                    "No se pudieron cargar pedidos:\n" + e.getMessage()
            ).showAndWait();
        }
    }

    /**
     * Abre un diálogo modal con los detalles de un pedido seleccionado en modo solo lectura.
     * Si no hay selección, muestra una alerta de tipo WARNING.
     */
    @FXML
    private void onVerDetalles() {
        Pedido sel = tablePedidos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Selecciona un pedido primero."
            ).showAndWait();
            return;
        }
        try {
            URL url = getClass().getResource("/agrobdgui/pedidoform.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            PedidoFormController formCtrl = loader.getController();
            formCtrl.setDialogStage(dialogStage);
            formCtrl.setReadOnly(sel);

            Stage dlg = new Stage();
            dlg.initOwner(dialogStage);
            dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.setTitle("Detalles Pedido #" + sel.getIdPedido());
            dlg.setScene(new Scene(root));
            dlg.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(
                    Alert.AlertType.ERROR,
                    "No se pudo abrir detalles:\n" + ex.getMessage()
            ).showAndWait();
        }
    }

    /**
     * Elimina el pedido seleccionado tras confirmar la acción.
     * Si no hay selección, muestra una alerta de tipo WARNING.
     * Tras eliminar, recarga la lista o muestra un ERROR si falla.
     */
    @FXML
    private void onEliminarPedido() {
        Pedido sel = tablePedidos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Selecciona un pedido para eliminar."
            ).showAndWait();
            return;
        }
        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Eliminar pedido #" + sel.getIdPedido() + "?",
                ButtonType.YES, ButtonType.NO
        );
        confirm.initOwner(dialogStage);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    dao.delete(sel.getIdPedido());
                    cargarPedidos();
                } catch (Exception e) {
                    new Alert(
                            Alert.AlertType.ERROR,
                            "No se pudo eliminar:\n" + e.getMessage()
                    ).showAndWait();
                }
            }
        });
    }

    /**
     * Abre un formulario modal para validar o cambiar el estado de un pedido.
     * Si no hay selección, muestra una alerta de tipo WARNING.
     * Después de cerrar el modal recarga la tabla de pedidos.
     */
    @FXML
    private void onCambiarEstado() {
        Pedido sel = tablePedidos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Selecciona un pedido primero."
            ).showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/agrobdgui/pedidoStateForm.fxml")
            );
            Parent root = loader.load();

            Stage dlg = new Stage();
            dlg.initOwner(dialogStage);
            dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.setTitle("Validar/Cambiar estado Pedido #" + sel.getIdPedido());
            dlg.setScene(new Scene(root));

            PedidoStateController ctrl = loader.getController();
            ctrl.setDialogStage(dlg);
            ctrl.setPedido(sel);

            dlg.showAndWait();
            cargarPedidos();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "No se pudo abrir el formulario de estado:\n" + e.getMessage()
            ).showAndWait();
        }
    }
}














