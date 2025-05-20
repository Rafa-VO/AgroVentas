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
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PedidoController {

    @FXML private TableView<Pedido> tablePedidos;
    @FXML private TableColumn<Pedido,Integer> colId;
    @FXML private TableColumn<Pedido,String>  colFecha;
    @FXML private TableColumn<Pedido,String>  colEstado;
    @FXML private TableColumn<Pedido,String>  colComentario;

    // ** YA NO inyectamos btnCambiarEstado **
    // @FXML private Button btnCambiarEstado;

    private final PedidoDAO dao = new PedidoDAO();
    private boolean onlyMine = false;
    private Stage dialogStage;

    /** ClienteViewController o EmpleadoViewController llaman esto antes de mostrar */
    public void setOnlyMine(boolean onlyMine) {
        this.onlyMine = onlyMine;
        cargarPedidos();
    }

    /** Inyecta el Stage padre para modales */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

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

    @FXML
    private void onCambiarEstado() {
        Pedido sel = tablePedidos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un pedido primero.").showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/agrobdgui/pedidoStateForm.fxml")
            );
            Parent root = loader.load();

            // 1) Creamos el Stage
            Stage dlg = new Stage();
            dlg.initOwner(dialogStage); // dialogStage es el Stage padre de este controller
            dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.setTitle("Validar/Cambiar estado Pedido #" + sel.getIdPedido());
            dlg.setScene(new Scene(root));

            // 2) Inyectamos el Stage en el controller
            PedidoStateController ctrl = loader.getController();
            ctrl.setDialogStage(dlg);

            // 3) Ahora le pasamos el pedido
            ctrl.setPedido(sel);

            // 4) Y finalmente lo mostramos
            dlg.showAndWait();

            // 5) Re-cargamos la lista tras cerrar
            cargarPedidos();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "No se pudo abrir el formulario de estado:\n" + e.getMessage()
            ).showAndWait();
        }
    }
}













