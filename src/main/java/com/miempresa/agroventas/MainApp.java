package com.miempresa.agroventas;

import com.miempresa.agroventas.controller.ClienteFormController;
import com.miempresa.agroventas.controller.EmpleadoFormController;
import com.miempresa.agroventas.interfaces.Role;
import com.miempresa.agroventas.model.Cliente;
import com.miempresa.agroventas.model.Empleado;
import com.miempresa.agroventas.util.Session;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {

    // Estos valores deben coincidir con los prefWidth/prefHeight que tienes en login.fxml
    private static final double LOGIN_WIDTH  = 836;
    private static final double LOGIN_HEIGHT = 528;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1) Cargamos el FXML de login
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/agrobdgui/login.fxml")
        );
        // 2) Creamos la escena con tamaño fijo
        Scene scene = new Scene(loader.load(), LOGIN_WIDTH, LOGIN_HEIGHT);
        // 3) Añadimos la hoja de estilos CSS
        scene.getStylesheets().add(
                getClass().getResource("/css/login.css").toExternalForm()
        );

        primaryStage.setScene(scene);
        primaryStage.setTitle("AgroVentas – Acceso");

        // 4) Fijamos tamaño y deshabilitamos redimensionar/maximizar
        primaryStage.setResizable(false);
        // Si quisieras quitar por completo la barra de título:
        // primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Muestra la ventana principal con el menú (root.fxml).
     */
    /**
     * Muestra la ventana principal tras un login exitoso.
     * Antes cargabas root.fxml; ahora elegimos la vista según el rol.
     */
    public static void showMainView(Stage stage) throws Exception {
        // Elegimos el FXML correcto:
        String fxml = Session.getRole() == Role.CLIENTE
                ? "/agrobdgui/clienteView.fxml"
                : "/agrobdgui/empleadoView.fxml";

        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxml));
        // Usamos el mismo tamaño que tu login:
        Scene scene = new Scene(loader.load(), LOGIN_WIDTH, LOGIN_HEIGHT);

        stage.setScene(scene);
        stage.setTitle(
                Session.getRole() == Role.CLIENTE
                        ? "AgroVentas – Cliente"
                        : "AgroVentas – Empleado"
        );
        stage.setResizable(false);
        stage.show();
    }


    /**
     * Helper para abrir el diálogo de creación/edición de Cliente.
     */
    public static void showClienteForm(Stage ownerStage, Cliente cliente) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/agrobdgui/clienteform.fxml")
        );
        Stage dialog = new Stage();
        dialog.setTitle(cliente == null ? "Nuevo Cliente" : "Editar Cliente");
        dialog.initOwner(ownerStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setScene(new Scene(loader.load()));
        dialog.setResizable(false);
        ClienteFormController controller = loader.getController();
        controller.setDialogStage(dialog);
        controller.setCliente(cliente == null ? new Cliente() : cliente);
        dialog.showAndWait();
    }

    /**
     * Helper para abrir el diálogo de creación/edición de Empleado.
     */
    public static void showEmpleadoForm(Stage ownerStage, Empleado empleado) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/agrobdgui/empleadoform.fxml")
        );
        Stage dialog = new Stage();
        dialog.setTitle(empleado == null ? "Nuevo Empleado" : "Editar Empleado");
        dialog.initOwner(ownerStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setScene(new Scene(loader.load()));
        dialog.setResizable(false);
        EmpleadoFormController controller = loader.getController();
        controller.initForm(dialog, empleado == null ? new Empleado() : empleado);
        dialog.showAndWait();
    }
}






