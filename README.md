# AgroVentas

AgroVentas es una aplicación desarrollada en Java que ofrece a clientes y empresas interesadas en el sector agrícola una plataforma para la compra de maquinaria y productos agrícolas. Basada en el patrón MVC y conectada a una base de datos MySQL, la aplicación permite a los empleados gestionar clientes, validar pedidos, y dar de alta, modificar o eliminar maquinaria y nuevos productos. Por otra parte, los clientes podrán hacer pedidos, consultar el estado de los estos y actualizar su propio perfil.

## Estructura del proyecto

src/
├─ main/
│  ├─ java/com/miempresa/agroventas/
│  │  ├─ baseDatos/
│  │  │  ├─ ConnectionBD.java
│  │  │  ├─ ConnectionProperties.java
│  │  │  └─ XMLManager.java
│  │  ├─ controller/
│  │  │  ├─ MainApp.java
│  │  │  ├─ LoginController.java
│  │  │  ├─ ClienteController.java
│  │  │  ├─ ClienteFormController.java
│  │  │  ├─ EmpleadoController.java
│  │  │  ├─ EmpleadoFormController.java
│  │  │  ├─ MaquinariaController.java
│  │  │  ├─ MaquinariaFormController.java
│  │  │  ├─ PedidoController.java
│  │  │  ├─ PedidoFormController.java
│  │  │  ├─ PedidoStateController.java
│  │  │  └─ UsuarioFormController.java
│  │  ├─ dao/
│  │  │  ├─ ClienteDAO.java
│  │  │  ├─ EmpleadoDAO.java
│  │  │  ├─ MaquinariaDAO.java
│  │  │  ├─ PedidoDAO.java
│  │  │  ├─ DetallePedidoDAO.java
│  │  │  ├─ ProveedorDAO.java
│  │  │  ├─ UsuarioDAO.java
│  │  │  └─ ValidacionDAO.java
│  │  ├─ interfaces/
│  │  │  ├─ Role.java
│  │  │  └─ EstadosPedido.java
│  │  ├─ model/
│  │  │  ├─ Usuario.java
│  │  │  ├─ Cliente.java
│  │  │  ├─ Empleado.java
│  │  │  ├─ Proveedor.java
│  │  │  ├─ Maquinaria.java
│  │  │  ├─ Pedido.java
│  │  │  ├─ DetallePedido.java
│  │  │  └─ Validacion.java
│  │  └─ util/
│  │     └─ Session.java
│  └─ resources/
│     ├─ agrobdgui/
│     │  ├─ root.fxml
│     │  ├─ login.fxml
│     │  ├─ cliente.fxml
│     │  ├─ clienteform.fxml
│     │  ├─ clienteView.fxml
│     │  ├─ empleado.fxml
│     │  ├─ empleadoform.fxml
│     │  ├─ empleadoView.fxml
│     │  ├─ maquinaria.fxml
│     │  ├─ maquinariaform.fxml
│     │  ├─ pedidoList.fxml
│     │  ├─ pedidoListEmpleado.fxml
│     │  ├─ pedidoform.fxml
│     │  ├─ pedidoStateForm.fxml
│     │  ├─ usuario.fxml
│     │  └─ usuarioform.fxml
│     ├─ connection.xml
│     ├─ css/login.css
│     └─ imagenes/
│        ├─ FondoLogin.png
│        └─ ImagenCliente1.png


## Descripcion de las clases

- Clases de arranque

    MainApp: Clase principal que arranca la aplicación JavaFX, carga el FXML inicial y configura la escena.
    
    Main: Contiene el método main(String[]) que invoca a MainApp.launch().


1. BaseDatos
      ConnectionBD: Gestiona la conexión JDBC con la base de datos MySQL.
      
      ConnectionProperties: Carga desde un fichero XML los parámetros de conexión (URL, usuario, contraseña).
      
      XMLManager: Utilidad para leer y escribir el fichero de configuración XML de la aplicación.

2. Controller
   
      RootController: Controlador del layout principal (barra de navegación, side menu).
      
      LoginController: Valida credenciales y redirige al área de cliente o empleado.
      
      ClienteController / ClienteFormController: Gestionan la vista de listado de clientes y el formulario de alta y edición.
      
      EmpleadoController / EmpleadoFormController: Igual que cliente, pero para empleados.
      
      MaquinariaController / MaquinariaFormController: Listado y formulario de alta/edición de maquinaria.
      
      PedidoController / PedidoFormController: Listado de pedidos y formulario para crear o editar pedidos.
      
      PedidoStateController: Controla la actualización de estados de pedido (pendiente, en curso, entregado).
      
      UsuarioController / UsuarioFormController: Gestión genérica de usuarios (editar perfil, cambiar contraseña).


3. DAO
      ClienteDAO, EmpleadoDAO, MaquinariaDAO, PedidoDAO, DetallePedidoDAO, ProveedorDAO, UsuarioDAO: Cada uno implementa las operaciones CRUD (create, read, update, delete) sobre su tabla correspondiente.
      
      ValidacionDAO: Gestiona el registro de validaciones que hacen los empleados sobre los pedidos.


4. Interfaces
      Role: Enumeración de roles de usuario (CLIENTE y EMPLEADO).
      
      EstadosPedido: Enumeración de los posibles estados de un pedido (PENDIENTE, ENVIADO, ENTREGADO, CANCELADO).

5. Model
      Usuario, Cliente, Empleado: Clases simples que reflejan la jerarquía de usuario (Usuario es superclase; Cliente y Empleado la extienden).
      
      Maquinaria, Proveedor, Pedido, DetallePedido, Validacion: Clases que representan las entidades de la base de datos con sus atributos y métodos simples de acceso.


6. Util
      Session: Almacena en memoria el usuario logueado y su rol para controlar permisos en toda la aplicación.

7. Views
      ClienteViewController, EmpleadoViewController: Controladores de vistas específicas (por ejemplo cuadros de diálogo o componentes personalizados) para clientes y empleados.

8. FXML
      root.fxml: Define el layout principal de la aplicación (menú lateral, cabecera y zona de contenido).
      
      login.fxml: Pantalla de login: campos de usuario/contraseña y botón de acceso.
      
      cliente.fxml: Vista de listado de clientes: tabla con datos básicos y botones para acciones.
      
      clienteform.fxml: Formulario de alta/edición de cliente (nombre, dirección, teléfono…).
      
      clienteView.fxml: Detalle extendido de un cliente concreto (para consulta rápida).
      
      empleado.fxml: Vista de listado de empleados con acciones CRUD.
      
      empleadoform.fxml: Formulario de alta/edición de empleado (departamento, cargo, salario).
      
      empleadoView.fxml: Detalle extendido de un empleado (consulta de perfil completo).
      
      maquinaria.fxml: Listado de maquinaria y productos agrícolas: imágenes, stock y precios.
      
      maquinariaform.fxml: Formulario de alta/edición de maquinaria (tipo, descripción, imagen, precio…).
      
      pedidoList.fxml: Tabla de pedidos desde la perspectiva del cliente (sólo sus propios pedidos).
      
      pedidoListEmpleado.fxml: Tabla de pedidos desde la perspectiva del empleado (todos los pedidos).
      
      pedidoform.fxml: Formulario para crear o editar un pedido: selección de cliente, líneas de detalle.
      
      pedidoStateForm.fxml: Diálogo específico para cambiar el estado de un pedido (pendiente → en curso → entregado → cancelado).
      
      usuario.fxml: Vista de detalles del usuario logueado (cliente o empleado).
      
      usuarioform.fxml: Formulario para que el propio usuario edite su perfil o cambie contraseña.


9. Otros Recursos
      connection.xm: Fichero de configuración XML con los parámetros de conexión a la base de datos (URL, usuario, contraseña) que lee XMLManager.
      
      css/login.css: Hoja de estilo que da formato específico a la pantalla de login (colores, fuentes, márgenes…).
      
      imágenes:
      
      FondoLogin.png: imagen de fondo para la vista de login.
      
      ImagenCliente(1).png: icono o ilustración usada en la sección de cliente(Imagen No utilizada).
