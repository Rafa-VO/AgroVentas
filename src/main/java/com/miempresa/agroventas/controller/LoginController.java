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

import java.sql.SQLException;

/**
 * Controlador de la ventana de Login / Registro.
 * Gestiona:
 *
 *   Validación de credenciales y rol (Cliente o Empleado).
 *   Registro de nuevos Usuarios y asignación de roles.
 *   Apertura de formularios modales para Usuario, Cliente o Empleado
 *   nicio de sesión y lanzamiento de la vista principal tras éxito.
 *
 */
public class LoginController {

    @FXML private TextField     tfCorreo;
    @FXML private PasswordField tfContrasena;
    @FXML private RadioButton   rbCliente, rbEmpleado;
    @FXML private Label         lblError;
    @FXML private Button        btnLogin, btnRegister;

    private ToggleGroup roleGroup;


    /**
     * Inicializa el ToggleGroup de roles y selecciona Cliente por defecto.
     * Se ejecuta automáticamente tras cargar el FXML.
     */
    @FXML
    public void initialize() {
        roleGroup = new ToggleGroup();
        rbCliente .setToggleGroup(roleGroup);
        rbEmpleado.setToggleGroup(roleGroup);
        rbCliente.setSelected(true);
    }

    /**
     * Maneja el evento de inicio de sesión:
     *
     *   Valida que correo y contraseña no estén vacíos.
     *   Comprueba credenciales contra la tabla Usuario.
     *   Verifica si el Usuario tiene asociado el rol seleccionado (Cliente/Empleado).
     *   Si es correcto, guarda la sesión y abre la vista principal.
     *   En caso de error o coincidencia fallida, muestra mensaje en lblError.
     *
     */
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
            u = UsuarioDAO.findByEmailAndPassword(correo, pwd);
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
            isCliente  = ClienteDAO.findById(u.getIdUsuario())  != null;
            isEmpleado = EmpleadoDAO.findById(u.getIdUsuario()) != null;
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

        // Login exitoso: guardar sesión y abrir MainView
        Role role = wantCliente ? Role.CLIENTE : Role.EMPLEADO;
        Session.login(u, role);

        Stage st = (Stage) tfCorreo.getScene().getWindow(); //Aqui
        st.close();
        try {
            MainApp.showMainView(new Stage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Maneja el evento de registro de usuario:
     *
     *   Valida que correo y contraseña no estén vacíos.
     *   Busca o crea un registro en Usuario (ignorando duplicados).
     *   Verifica si ya tiene el rol elegido.
     *   Abre el formulario de Cliente o Empleado para completar datos.
     *   Guarda el nuevo Cliente/Empleado (ignorando duplicados) y hace login.
     *   Muestra mensajes de error en lblError en caso de fallo.
     *
     */
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
            // 1) Buscar usuario existente o preparar uno nuevo
            Usuario u = UsuarioDAO.findByEmail(correo);
            if (u == null) {
                // Formulario modal para crear Usuario (correo + contraseña)
                FXMLLoader ufLoader = new FXMLLoader(
                        getClass().getResource("/agrobdgui/usuarioform.fxml")
                );
                Stage ux = new Stage();
                ux.initOwner(tfCorreo.getScene().getWindow());
                ux.initModality(Modality.APPLICATION_MODAL);
                Parent ufRoot = ufLoader.load();
                UsuarioFormController ufc = ufLoader.getController();
                ufc.setDialogStage(ux);

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
                u = ufc.getUsuario();
            }

            // 2) Guardar o actualizar Usuario (ignorar duplicados)
            try {
                if (u.getIdUsuario() == 0) {
                    UsuarioDAO.create(u);
                } else {
                    UsuarioDAO.update(u);
                }
            } catch (SQLException sqlEx) {
                if (!sqlEx.getMessage().toLowerCase().contains("duplicate")) {
                    throw sqlEx;
                }
            }

            // 3) Verificar rol ya existente
            boolean isCliente  = ClienteDAO.findById(u.getIdUsuario())  != null;
            boolean isEmpleado = EmpleadoDAO.findById(u.getIdUsuario()) != null;
            if (wantCliente && isCliente) {
                lblError.setText("Este usuario ya está registrado como Cliente.");
                return;
            }
            if (!wantCliente && isEmpleado) {
                lblError.setText("Este usuario ya está registrado como Empleado.");
                return;
            }

            // 4) Abrir formulario de rol faltante y guardar (ignorar duplicates)
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
                    ClienteDAO.create(c);
                } catch (SQLException sqlEx) {
                    if (!sqlEx.getMessage().toLowerCase().contains("duplicate")) {
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
                    EmpleadoDAO.create(e);
                } catch (SQLException sqlEx) {
                    if (!sqlEx.getMessage().toLowerCase().contains("duplicate")) {
                        throw sqlEx;
                    }
                }
            }

            // 5) Login automático tras registro completo
            Role role = wantCliente ? Role.CLIENTE : Role.EMPLEADO;
            Session.login(u, role);
            Stage st = (Stage) tfCorreo.getScene().getWindow();
            st.close();
            MainApp.showMainView(new Stage());

        } catch (Exception ex) {
            lblError.setText("Error al registrar: " + ex.getMessage());
        }
    }

    /**
     * Abre el formulario de Cliente, lo guarda y hace login como CLIENTE.
     * Usado por flujos alternativos (no directamente desde FXML).
     * @param u Usuario ya autenticado
     */
    private void showClienteFormAndLogin(Usuario u) throws Exception {
        if (ClienteDAO.findById(u.getIdUsuario()) != null) {
            lblError.setText("Ya tienes cuenta de cliente.");
            return;
        }
        Cliente c = new Cliente();
        c.setIdUsuario(u.getIdUsuario());

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/agrobdgui/clienteform.fxml")
        );
        Stage dialog = new Stage();
        dialog.initOwner(tfCorreo.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
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

        ClienteDAO.create(c);
        Session.login(u, Role.CLIENTE);
        closeAndLaunchMain();
    }

    /**
     * Abre el formulario de Empleado, lo guarda y hace login como EMPLEADO.
     * Usado por flujos alternativos (no directamente desde FXML).
     * @param u Usuario ya autenticado
     */
    private void showEmpleadoFormAndLogin(Usuario u) throws Exception {
        if (EmpleadoDAO.findById(u.getIdUsuario()) != null) {
            lblError.setText("Ya tienes cuenta de empleado.");
            return;
        }
        Empleado e = new Empleado();
        e.setIdUsuario(u.getIdUsuario());

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/agrobdgui/empleadoform.fxml")
        );
        Stage dialog = new Stage();
        dialog.initOwner(tfCorreo.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setScene(new Scene(loader.load()));
        dialog.setTitle("Registrar Empleado");

        EmpleadoFormController ctrl = loader.getController();
        ctrl.initForm(dialog, e);
        dialog.showAndWait();

        if (!ctrl.isOkClicked()) {
            lblError.setText("Registro de empleado cancelado.");
            return;
        }

        EmpleadoDAO.create(e);
        Session.login(u, Role.EMPLEADO);
        closeAndLaunchMain();
    }

    /**
     * Cierra la ventana de login y abre la vista principal de la aplicación.
     * @throws Exception si falla la carga de la vista principal
     */
    private void closeAndLaunchMain() throws Exception {
        Stage st = (Stage) tfCorreo.getScene().getWindow();
        st.close();
        MainApp.showMainView(new Stage());
    }
}









