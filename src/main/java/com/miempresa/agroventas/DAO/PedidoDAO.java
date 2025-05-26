package com.miempresa.agroventas.DAO;

import com.miempresa.agroventas.baseDatos.ConnectionBD;
import com.miempresa.agroventas.interfaces.EstadosPedido;
import com.miempresa.agroventas.model.Pedido;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class  PedidoDAO {

    // Consultas SQL definidas como constantes
    private static final String SQL_FIND_BY_ID = "SELECT * FROM pedido WHERE ID_Pedido = ?";
    private static final String SQL_FIND_BY_CLIENTE_ID = "SELECT * FROM pedido WHERE ID_Cliente = ?";
    private static final String SQL_FIND_ALL = "SELECT * FROM pedido";
    private static final String SQL_INSERT = "INSERT INTO pedido (ID_Cliente, FechaInicio, Estado, Comentario) VALUES (?,?,?,?)";
    private static final String SQL_UPDATE = "UPDATE pedido SET FechaInicio = ?, Estado = ?, Comentario = ? WHERE ID_Pedido = ?";
    private static final String SQL_DELETE = "DELETE FROM pedido WHERE ID_Pedido = ?";

    /**
     * Convierte la representación en texto del estado almacenado en la base de datos
     * a la constante correspondiente de nuestro enum EstadosPedido. Trata valores nulos
     * o no reconocidos devolviendo PENDIENTE.
     * @param estadoStr cadena recuperada de la columna Estado
     * @return valor de EstadosPedido que corresponde al texto proporcionado
     */
    private static EstadosPedido mapEstado(String estadoStr) {
        if (estadoStr == null) return EstadosPedido.PENDIENTE;
        try {
            return EstadosPedido.valueOf(estadoStr.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return EstadosPedido.PENDIENTE;
        }
    }

    /**
     * Busca un pedido por su clave primaria.
     * Abre una conexión, prepara la consulta con el ID, ejecuta el ResultSet,
     * mapea la primera fila a un objeto Pedido y devuelve ese objeto o null si no existe.
     * @param id identificador del pedido
     * @return instancia de Pedido con los datos encontrados o null si no hay registro
     * @throws Exception si ocurre cualquier error de acceso a la base de datos
     */
    public static Pedido findById(int id) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDate fecha = rs.getDate("FechaInicio").toLocalDate();
                    EstadosPedido estado = mapEstado(rs.getString("Estado"));
                    return new Pedido(
                            rs.getInt("ID_Pedido"),
                            fecha,
                            estado,
                            rs.getString("Comentario")
                    );
                }
                return null;
            }
        }
    }

    /**
     * Recupera todos los pedidos asociados a un cliente determinado.
     * Prepara la consulta con el ID de cliente, recorre todas las filas del ResultSet,
     * crea objetos Pedido para cada fila y los acumula en una lista que devuelve.
     * @param idCliente identificador del cliente cuyos pedidos se desean listar
     * @return lista de objetos Pedido para ese cliente (vacía si no hay registros)
     * @throws Exception si ocurre un error al acceder a la base de datos
     */
    public static List<Pedido> findByClienteId(int idCliente) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_CLIENTE_ID)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                List<Pedido> list = new ArrayList<>();
                while (rs.next()) {
                    LocalDate fecha = rs.getDate("FechaInicio").toLocalDate();
                    EstadosPedido estado = mapEstado(rs.getString("Estado"));
                    list.add(new Pedido(
                            rs.getInt("ID_Pedido"),
                            fecha,
                            estado,
                            rs.getString("Comentario")
                    ));
                }
                return list;
            }
        }
    }

    /**
     * Recupera todos los pedidos de la tabla. Ejecuta la consulta SQL_FIND_ALL,
     * itera sobre el ResultSet y mapea cada fila a un objeto Pedido, que añade
     * a la lista de resultados.
     * @return lista completa de pedidos en la base de datos
     * @throws Exception si hay fallo al ejecutar la consulta
     */
    public static ArrayList<Pedido> findAll() throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            ArrayList<Pedido> list = new ArrayList<>();
            while (rs.next()) {
                LocalDate fecha = rs.getDate("FechaInicio").toLocalDate();
                EstadosPedido estado = mapEstado(rs.getString("Estado"));
                list.add(new Pedido(
                        rs.getInt("ID_Pedido"),
                        fecha,
                        estado,
                        rs.getString("Comentario")
                ));
            }
            return list;
        }
    }

    /**
     * Inserta un nuevo pedido en la base de datos. Usa RETURN_GENERATED_KEYS
     * para recuperar la clave primaria asignada y la establece en el objeto Pedido.
     * @param p objeto Pedido con idCliente, fecha, estado y comentario definidos
     * @throws Exception si falla la inserción o la recuperación de la clave generada
     */
    public static void create(Pedido p) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getCliente().getIdUsuario());
            ps.setDate(2, Date.valueOf(p.getFechaPedido()));
            ps.setString(3, p.getEstado().name());
            ps.setString(4, p.getComentario());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    p.setIdPedido(gk.getInt(1));
                }
            }
        }
    }

    /**
     * Actualiza un pedido existente en la base de datos. Prepara la consulta
     * con todos los campos válidos y ejecuta la actualización sobre la fila
     * correspondiente al ID de pedido.
     * @param p objeto Pedido con los nuevos valores y su idPedido establecido
     * @throws Exception si ocurre un error durante la actualización
     */
    public static void update(Pedido p) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setDate(1, Date.valueOf(p.getFechaPedido()));
            ps.setString(2, p.getEstado().name());
            ps.setString(3, p.getComentario());
            ps.setInt(4, p.getIdPedido());
            ps.executeUpdate();
        }
    }

    /**
     * Elimina un pedido por su identificador. Ejecuta la sentencia DELETE
     * usando el ID de pedido proporcionado.
     * @param id identificador del pedido que se desea borrar
     * @throws Exception si falla la eliminación en la base de datos
     */
    public static void delete(int id) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}






