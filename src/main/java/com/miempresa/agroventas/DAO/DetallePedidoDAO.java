package com.miempresa.agroventas.DAO;

import com.miempresa.agroventas.baseDatos.ConnectionBD;
import com.miempresa.agroventas.model.DetallePedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad DetallePedido. Proporciona operaciones CRUD y consultas
 * específicas sobre la tabla detallePedido.
 */
public class DetallePedidoDAO {

    private static final String SQL_FIND_BY_ID = "SELECT * FROM detallePedido WHERE ID_Pedido = ? AND ID_Maquinaria = ?";
    private static final String SQL_FIND_ALL = "SELECT * FROM detallePedido";
    private static final String SQL_FIND_BY_PEDIDO_ID = "SELECT ID_Pedido, ID_Maquinaria, Cantidad, PrecioUnitario " + "FROM detallePedido WHERE ID_Pedido = ?";
    private static final String SQL_INSERT = "INSERT INTO detallePedido (ID_Pedido, ID_Maquinaria, Cantidad, PrecioUnitario) " + "VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE detallePedido SET Cantidad = ?, PrecioUnitario = ? " + "WHERE ID_Pedido = ? AND ID_Maquinaria = ?";
    private static final String SQL_DELETE = "DELETE FROM detallePedido WHERE ID_Pedido = ? AND ID_Maquinaria = ?";

    /**
     * Busca un DetallePedido por la clave compuesta (pedido, maquinaria).
     * @param idPedido     identificador del pedido
     * @param idMaquinaria identificador de la maquinaria
     * @return objeto DetallePedido si existe, o null si no se encuentra
     * @throws Exception si ocurre un error de acceso a la base de datos
     */
    public DetallePedido findById(int idPedido, int idMaquinaria) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, idPedido);
            ps.setInt(2, idMaquinaria);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /**
     * Recupera todos los DetallePedido de la base de datos.
     * @return lista de DetallePedido (vacía si no hay registros)
     * @throws Exception si ocurre un error de acceso a la base de datos
     */
    public List<DetallePedido> findAll() throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            List<DetallePedido> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        }
    }

    /**
     * Obtiene las líneas de detalle de un pedido específico.
     * @param idPedido identificador del pedido
     * @return lista de DetallePedido asociada al pedido
     * @throws Exception si ocurre un error de acceso a la base de datos
     */
    public List<DetallePedido> findByPedidoId(int idPedido) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_PEDIDO_ID)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                List<DetallePedido> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new DetallePedido(
                            rs.getInt("ID_Pedido"),
                            rs.getInt("ID_Maquinaria"),
                            rs.getInt("Cantidad"),
                            rs.getDouble("PrecioUnitario")
                    ));
                }
                return list;
            }
        }
    }

    /**
     * Inserta un nuevo DetallePedido en la base de datos.
     * @param dp objeto DetallePedido con pedido, maquinaria, cantidad y precio unitario
     * @throws Exception si ocurre un error de inserción
     */
    public void create(DetallePedido dp) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setInt(1, dp.getIdPedido());
            ps.setInt(2, dp.getIdMaquinaria());
            ps.setInt(3, dp.getCantidad());
            ps.setDouble(4, dp.getPrecioUnitario());
            ps.executeUpdate();
        }
    }

    /**
     * Actualiza la cantidad y el precio unitario de una línea de pedido.
     * @param dp objeto DetallePedido con datos actualizados
     * @throws Exception si ocurre un error de actualización
     */
    public void update(DetallePedido dp) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setInt(1, dp.getCantidad());
            ps.setDouble(2, dp.getPrecioUnitario());
            ps.setInt(3, dp.getIdPedido());
            ps.setInt(4, dp.getIdMaquinaria());
            ps.executeUpdate();
        }
    }

    /**
     * Elimina una línea de pedido identificada por pedido y maquinaria.
     * @param idPedido     identificador del pedido
     * @param idMaquinaria identificador de la maquinaria
     * @throws Exception si ocurre un error de eliminación
     */
    public void delete(int idPedido, int idMaquinaria) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idPedido);
            ps.setInt(2, idMaquinaria);
            ps.executeUpdate();
        }
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto DetallePedido.
     * @param rs ResultSet posicionado en la fila a mapear
     * @return DetallePedido con los valores de la fila
     * @throws SQLException si ocurre un error al leer del ResultSet
     */
    private DetallePedido mapRow(ResultSet rs) throws SQLException {
        return new DetallePedido(rs.getInt("ID_Pedido"), rs.getInt("ID_Maquinaria"),
                rs.getInt("Cantidad"), rs.getDouble("PrecioUnitario")
        );
    }
}





