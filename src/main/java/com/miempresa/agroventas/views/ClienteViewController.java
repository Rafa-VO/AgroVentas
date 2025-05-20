package com.miempresa.agroventas.views;

import com.miempresa.agroventas.controller.PedidoController;
import com.miempresa.agroventas.controller.PedidoFormController;
import com.miempresa.agroventas.controller.UsuarioFormController;
import com.miempresa.agroventas.controller.ClienteFormController;
import com.miempresa.agroventas.DAO.ClienteDAO;
import com.miempresa.agroventas.model.Cliente;
import com.miempresa.agroventas.model.Usuario;
import com.miempresa.agroventas.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ClienteViewController {

    @FXML private Button btnNuevoPedido;
    @FXML private Button btnVerPedidos;
    @FXML private Button btnEditarPerfil;
    @FXML private Button btnCerrarSesion;    // <-- Nuevo

    @FXML
    private void onNuevoPedido() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getClassLoader()
                            .getResource("agrobdgui/pedidoform.fxml")
            );
            Parent root = loader.load();
            PedidoFormController ctrl = loader.getController();

            Stage dialog = new Stage();
            dialog.initOwner(btnNuevoPedido.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            ctrl.setDialogStage(dialog);

            dialog.setScene(new Scene(root));
            dialog.setTitle("Nuevo Pedido");
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onVerPedidos() {
        try {
            URL fxml = getClass().getClassLoader()
                    .getResource("agrobdgui/pedidoList.fxml");
            FXMLLoader loader = new FXMLLoader(fxml);
            Parent root = loader.load();

            PedidoController pc = loader.getController();
            pc.setOnlyMine(true);

            Stage dialog = new Stage();
            dialog.initOwner(btnVerPedidos.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            pc.setDialogStage(dialog);

            dialog.setScene(new Scene(root));
            dialog.setTitle("Mis Pedidos");
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEditarPerfil() {
        try {
            Usuario u = Session.getUsuario();
            FXMLLoader uf = new FXMLLoader(
                    getClass().getClassLoader()
                            .getResource("agrobdgui/usuarioform.fxml")
            );
            Parent uRoot = uf.load();
            UsuarioFormController uCtrl = uf.getController();
            Stage ux = new Stage();
            ux.initOwner(btnEditarPerfil.getScene().getWindow());
            ux.initModality(Modality.APPLICATION_MODAL);
            uCtrl.setDialogStage(ux);
            uCtrl.setUsuario(u);
            ux.setScene(new Scene(uRoot));
            ux.setTitle("Editar Usuario");
            ux.showAndWait();

            if (!uCtrl.isOkClicked()) return;

            ClienteDAO cDAO = new ClienteDAO();
            Cliente cli = cDAO.findById(u.getIdUsuario());
            FXMLLoader cf = new FXMLLoader(
                    getClass().getClassLoader()
                            .getResource("agrobdgui/clienteform.fxml")
            );
            Parent cRoot = cf.load();
            ClienteFormController cCtrl = cf.getController();
            Stage cx = new Stage();
            cx.initOwner(btnEditarPerfil.getScene().getWindow());
            cx.initModality(Modality.APPLICATION_MODAL);
            cCtrl.setDialogStage(cx);
            cCtrl.setCliente(cli);
            cx.setScene(new Scene(cRoot));
            cx.setTitle("Editar Cliente");
            cx.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCerrarSesion() {
        try {
            // 1) Limpiar la sesión
            Session.clear();
            // 2) Cerrar ventana actual
            Stage ventana = (Stage) btnCerrarSesion.getScene().getWindow();
            ventana.close();
            // 3) Volver al login
            FXMLLoader loader = new FXMLLoader(
                    getClass().getClassLoader()
                            .getResource("agrobdgui/login.fxml")
            );
            Parent loginRoot = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("AgroVentas – Acceso");
            loginStage.setScene(new Scene(loginRoot));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}






