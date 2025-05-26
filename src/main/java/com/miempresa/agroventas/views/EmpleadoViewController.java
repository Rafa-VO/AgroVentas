package com.miempresa.agroventas.views;

import com.miempresa.agroventas.controller.ClienteController;
import com.miempresa.agroventas.controller.MaquinariaController;
import com.miempresa.agroventas.controller.PedidoController;
import com.miempresa.agroventas.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EmpleadoViewController {

    @FXML private Button btnGestionarClientes;
    @FXML private Button btnGestionarMaquinaria;
    @FXML private Button btnGestionarPedidos;
    @FXML private Button btnCerrarSesion;

    @FXML
    private void onGestionarClientes() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/agrobdgui/cliente.fxml")
            );
            Parent root = loader.load();
            Stage dlg = new Stage();
            dlg.initOwner(btnGestionarClientes.getScene().getWindow());
            dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.setTitle("Gestión de Clientes");
            dlg.setScene(new Scene(root));
            dlg.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onGestionarMaquinaria() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/agrobdgui/maquinaria.fxml")
            );
            Parent root = loader.load();
            Stage dlg = new Stage();
            dlg.initOwner(btnGestionarMaquinaria.getScene().getWindow());
            dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.setTitle("Gestión de Maquinaria");
            dlg.setScene(new Scene(root));
            dlg.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onGestionarPedidos() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/agrobdgui/pedidoListEmpleado.fxml")
            );
            Parent root = loader.load();
            PedidoController ctrl = loader.getController();
            ctrl.setOnlyMine(false);
            Stage dlg = new Stage();
            dlg.initOwner(btnGestionarPedidos.getScene().getWindow());
            dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.setTitle("Validar/Cambiar estado de pedidos");
            dlg.setScene(new Scene(root));
            dlg.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCerrarSesion() {
        try {
            Session.logout();
            Stage st = (Stage) btnCerrarSesion.getScene().getWindow();
            st.close();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/agrobdgui/login.fxml")
            );
            Parent login = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("AgroVentas – Acceso");
            loginStage.setScene(new Scene(login));
            loginStage.setResizable(false);
            loginStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

