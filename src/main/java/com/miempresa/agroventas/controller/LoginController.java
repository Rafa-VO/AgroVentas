package com.miempresa.agroventas.controller;

import com.miempresa.agroventas.MainApp;
import com.miempresa.agroventas.DAO.ClienteDAO;
import com.miempresa.agroventas.DAO.EmpleadoDAO;
import com.miempresa.agroventas.DAO.UsuarioDAO;
import com.miempresa.agroventas.interfaces.Role;
import com.miempresa.agroventas.model.Cliente;
import com.miempresa.agroventas.model.Empleado;
import com.miempresa.agroventas.model.Usuario;
import com.miempresa.agroventas.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

// IMPORTS de tus form-controllers
import com.miempresa.agroventas.controller.UsuarioFormController;
import com.miempresa.agroventas.controller.ClienteFormController;
import com.miempresa.agroventas.controller.EmpleadoFormController;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class LoginController {

    @FXML private TextField     tfCorreo;
    @FXML private PasswordField tfContrasena;
    @FXML private RadioButton   rbCliente, rbEmpleado;
    @FXML private Label         lblError;
    @FXML private Button        btnLogin, btnRegister;

    private ToggleGroup roleGroup;

    private final UsuarioDAO  usuarioDAO   = new UsuarioDAO();
    private final ClienteDAO  clienteDAO   = new ClienteDAO();
    private final EmpleadoDAO empleadoDAO  = new EmpleadoDAO();

    @FXML
    public void initialize() {
        roleGroup = new ToggleGroup();
        rbCliente .setToggleGroup(roleGroup);
        rbEmpleado.setToggleGroup(roleGroup);
        rbCliente.setSelected(true);
    }

    @FXML
    private void onLogin() {
        lblError.setText("");
        String correo = tfCorreo.getText().trim();
        String pwd    = tfContrasena.getText();
        if (correo.isEmpty() || pwd.isEmpty()) {
            lblError.setText("Debes indicar correo y contraseña.");
            return;
        }

        Usuario u;
        try {
            u = usuarioDAO.findByEmailAndPassword(correo, pwd);
        } catch (Exception ex) {
            lblError.setText("Error al validar usuario: " + ex.getMessage());
            return;
        }
        if (u == null) {
            lblError.setText("Usuario o contraseña incorrectos.");
            return;
        }

        boolean wantCliente = rbCliente.isSelected();
        boolean isCliente, isEmpleado;
        try {
            isCliente  = clienteDAO.findById(u.getIdUsuario())  != null;
            isEmpleado = empleadoDAO.findById(u.getIdUsuario()) != null;
        } catch (Exception ex) {
            lblError.setText("Error al comprobar rol: " + ex.getMessage());
            return;
        }

        if (wantCliente && !isCliente) {
            lblError.setText("No tienes cuenta de cliente.");
            return;
        }
        if (!wantCliente && !isEmpleado) {
            lblError.setText("No tienes cuenta de empleado.");
            return;
        }

        // Login OK
        Role role = wantCliente ? Role.CLIENTE : Role.EMPLEADO;
        Session.login(u, role);

        // Cerrar login y abrir vista principal
        Stage st = (Stage) tfCorreo.getScene().getWindow();
        st.close();
        try {
            MainApp.showMainView(new Stage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onRegister() {
        lblError.setText("");
        String correo = tfCorreo.getText().trim();
        String pwd    = tfContrasena.getText();
        boolean wantCliente = rbCliente.isSelected();

        if (correo.isEmpty() || pwd.isEmpty()) {
            lblError.setText("Indica correo y contraseña para registrarte.");
            return;
        }

        try {
            // 1) Buscamos al usuario por correo (o null si no existe)
            Usuario u = usuarioDAO.findByEmail(correo);

            if (u == null) {
                // ——— Usuario NUEVO ———
                FXMLLoader ufLoader = new FXMLLoader(
                        getClass().getResource("/agrobdgui/usuarioform.fxml")
                );
                Stage ux = new Stage();
                ux.initOwner(tfCorreo.getScene().getWindow());
                ux.initModality(Modality.APPLICATION_MODAL);
                Parent ufRoot = ufLoader.load();
                UsuarioFormController ufc = ufLoader.getController();
                ufc.setDialogStage(ux);

                // Le pasamos correo/contra para que los muestre
                Usuario nuevo = new Usuario();
                nuevo.setCorreo(correo);
                nuevo.setContrasena(pwd);
                ufc.setUsuario(nuevo);

                ux.setScene(new Scene(ufRoot));
                ux.setTitle("Nuevo Usuario");
                ux.showAndWait();
                if (!ufc.isOkClicked()) {
                    lblError.setText("Registro de usuario cancelado.");
                    return;
                }
                // Recuperamos el usuario ya rellenado en el form
                u = ufc.getUsuario();
            }

            // 2) Guardamos o actualizamos el usuario en BD, ignorando duplicados
            try {
                if (u.getIdUsuario() == 0) {
                    usuarioDAO.create(u);
                } else {
                    usuarioDAO.update(u);
                }
            } catch (SQLException sqlEx) {
                // Si es un duplicado de clave única, lo ignoramos
                String msg = sqlEx.getSQLState() + " " + sqlEx.getMessage();
                if (!msg.toLowerCase().contains("duplicate")) {
                    throw sqlEx;
                }
            }

            // 3) Comprobamos si ya tiene el rol
            boolean isCliente  = clienteDAO.findById(u.getIdUsuario())  != null;
            boolean isEmpleado = empleadoDAO.findById(u.getIdUsuario()) != null;
            if (wantCliente && isCliente) {
                lblError.setText("Este usuario ya está registrado como Cliente.");
                return;
            }
            if (!wantCliente && isEmpleado) {
                lblError.setText("Este usuario ya está registrado como Empleado.");
                return;
            }

            // 4) Abrimos sólo el form del rol faltante, y creamos ese registro ignorando duplicados
            if (wantCliente) {
                Cliente c = new Cliente();
                c.setIdUsuario(u.getIdUsuario());
                FXMLLoader cfLoader = new FXMLLoader(
                        getClass().getResource("/agrobdgui/clienteform.fxml")
                );
                Stage cx = new Stage();
                cx.initOwner(tfCorreo.getScene().getWindow());
                cx.initModality(Modality.APPLICATION_MODAL);
                Parent cfRoot = cfLoader.load();
                ClienteFormController cfc = cfLoader.getController();
                cfc.setDialogStage(cx);
                cfc.setCliente(c);
                cx.setScene(new Scene(cfRoot));
                cx.setTitle("Registrar Cliente");
                cx.showAndWait();
                if (!cfc.isOkClicked()) {
                    lblError.setText("Registro de cliente cancelado.");
                    return;
                }

                try {
                    clienteDAO.create(c);
                } catch (SQLException sqlEx) {
                    String msg = sqlEx.getSQLState() + " " + sqlEx.getMessage();
                    if (!msg.toLowerCase().contains("duplicate")) {
                        throw sqlEx;
                    }
                }
            } else {
                Empleado e = new Empleado();
                e.setIdUsuario(u.getIdUsuario());
                FXMLLoader efLoader = new FXMLLoader(
                        getClass().getResource("/agrobdgui/empleadoform.fxml")
                );
                Stage ex = new Stage();
                ex.initOwner(tfCorreo.getScene().getWindow());
                ex.initModality(Modality.APPLICATION_MODAL);
                Parent efRoot = efLoader.load();
                EmpleadoFormController efc = efLoader.getController();
                efc.initForm(ex, e);
                ex.setScene(new Scene(efRoot));
                ex.setTitle("Registrar Empleado");
                ex.showAndWait();
                if (!efc.isOkClicked()) {
                    lblError.setText("Registro de empleado cancelado.");
                    return;
                }

                try {
                    empleadoDAO.create(e);
                } catch (SQLException sqlEx) {
                    String msg = sqlEx.getSQLState() + " " + sqlEx.getMessage();
                    if (!msg.toLowerCase().contains("duplicate")) {
                        throw sqlEx;
                    }
                }
            }

            // 5) ¡Login automático y cierro!
            Role role = wantCliente ? Role.CLIENTE : Role.EMPLEADO;
            Session.login(u, role);
            Stage st = (Stage) tfCorreo.getScene().getWindow();
            st.close();
            MainApp.showMainView(new Stage());

        } catch (Exception ex) {
            lblError.setText("Error al registrar: " + ex.getMessage());
        }
    }



    /** Abre el form de Cliente, lo guarda y hace login como CLIENTE */
    private void showClienteFormAndLogin(Usuario u) throws Exception {
        // si ya tiene cliente registrado, salimos
        if (clienteDAO.findById(u.getIdUsuario()) != null) {
            lblError.setText("Ya tienes cuenta de cliente.");
            return;
        }
        Cliente c = new Cliente();
        c.setIdUsuario(u.getIdUsuario());

        Stage dialog = new Stage();
        dialog.initOwner(tfCorreo.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/agrobdgui/clienteform.fxml")
        );
        dialog.setScene(new Scene(loader.load()));
        dialog.setTitle("Registrar Cliente");

        ClienteFormController ctrl = loader.getController();
        ctrl.setDialogStage(dialog);
        ctrl.setCliente(c);
        dialog.showAndWait();

        if (!ctrl.isOkClicked()) {
            lblError.setText("Registro de cliente cancelado.");
            return;
        }

        // guardamos y hacemos login
        clienteDAO.create(c);
        Session.login(u, Role.CLIENTE);
        closeAndLaunchMain();
    }

    /** Abre el form de Empleado, lo guarda y hace login como EMPLEADO */
    private void showEmpleadoFormAndLogin(Usuario u) throws Exception {
        if (empleadoDAO.findById(u.getIdUsuario()) != null) {
            lblError.setText("Ya tienes cuenta de empleado.");
            return;
        }
        Empleado e = new Empleado();
        e.setIdUsuario(u.getIdUsuario());

        Stage dialog = new Stage();
        dialog.initOwner(tfCorreo.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/agrobdgui/empleadoform.fxml")
        );
        dialog.setScene(new Scene(loader.load()));
        dialog.setTitle("Registrar Empleado");

        EmpleadoFormController ctrl = loader.getController();
        ctrl.initForm(dialog, e);
        dialog.showAndWait();

        if (!ctrl.isOkClicked()) {
            lblError.setText("Registro de empleado cancelado.");
            return;
        }

        empleadoDAO.create(e);
        Session.login(u, Role.EMPLEADO);
        closeAndLaunchMain();
    }

    /** Cierra el login y muestra la vista principal */
    private void closeAndLaunchMain() throws Exception {
        Stage st = (Stage) tfCorreo.getScene().getWindow();
        st.close();
        MainApp.showMainView(new Stage());
    }
}








