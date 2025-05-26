package com.miempresa.agroventas.DAO;

import com.miempresa.agroventas.baseDatos.ConnectionBD;
import com.miempresa.agroventas.model.Empleado;
import com.miempresa.agroventas.model.Pedido;
import com.miempresa.agroventas.model.Validacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ValidacionDAO {

    private static final String SQL_FIND_BY_ID    = "SELECT * FROM validacion WHERE ID_Empleado = ? AND ID_Pedido = ?";
    private static final String SQL_FIND_ALL      = "SELECT * FROM validacion";
    private static final String SQL_INSERT        = "INSERT INTO validacion (ID_Pedido, ID_Empleado, FechaValidacion, ComentarioValidacion) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE        = "UPDATE validacion SET FechaValidacion = ?, ComentarioValidacion = ? WHERE ID_Pedido = ?";
    private static final String SQL_DELETE        = "DELETE FROM validacion WHERE ID_Empleado = ? AND ID_Pedido = ?";

    /**
     * Busca una validación concreta asociada a un empleado y un pedido.
     * @param idEmpleado identificador del empleado que hizo la validación
     * @param idPedido   identificador del pedido validado
     * @return instancia de Validacion si existe, o null si no se encontró
     * @throws Exception en caso de error de acceso a la base de datos
     */
    public static Validacion findById(int idEmpleado, int idPedido) throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, idEmpleado);
            ps.setInt(2, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /**
     * Recupera todas las validaciones almacenadas en la base de datos.
     * @return lista de todas las instancias de Validacion
     * @throws Exception en caso de error de acceso a la base de datos
     */
    public static List<Validacion> findAll() throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            List<Validacion> list = new ArrayList<>();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        }
    }

    /**
     * Inserta una nueva validación en la tabla, con fecha y comentario.
     * @param v objeto Validacion con idPedido, idEmpleado, fecha y comentario
     * @throws Exception en caso de error de inserción en la base de datos
     */
    public static void create(Validacion v) throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT)) {
            ps.setInt(1, v.getPedido().getIdPedido());
            ps.setInt(2, v.getEmpleado().getIdUsuario());
            ps.setTimestamp(3, Timestamp.valueOf(v.getFechaValidacion()));
            ps.setString(4, v.getComentarioValidacion());
            ps.executeUpdate();
        }
    }

    /**
     * Actualiza una validación existente, cambiando la fecha y el comentario.
     * @param v objeto Validacion con los nuevos valores y las claves idEmpleado e idPedido
     * @throws Exception en caso de error al ejecutar la actualización
     */
    public static void update(Validacion v) throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_UPDATE)) {
            ps.setTimestamp(1, Timestamp.valueOf(v.getFechaValidacion()));
            ps.setString(2, v.getComentarioValidacion());
//            ps.setInt(3, v.getIdEmpleado());
            ps.setInt(3, v.getPedido().getIdPedido());
            ps.executeUpdate();
        }
    }

    /**
     * Elimina una validación identificada por empleado y pedido.
     * @param idEmpleado identificador del empleado
     * @param idPedido   identificador del pedido
     * @throws Exception en caso de error durante la eliminación
     */
    public static void delete(int idEmpleado, int idPedido) throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idEmpleado);
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        }
    }

    public static Validacion findByPedidoId(int idPedido) throws Exception {
        String sql = "SELECT * FROM validacion WHERE ID_Pedido = ?";
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                else return null;
            }
        }
    }


    /**
     * Mapea la fila actual del ResultSet a un objeto Validacion.
     * @param rs ResultSet posicionado en la fila a convertir
     * @return instancia de Validacion con los datos de la fila
     * @throws SQLException en caso de error al leer del ResultSet
     */
    private static Validacion mapRow(ResultSet rs) throws SQLException {
        try {
            Pedido pedido = PedidoDAO.findById(rs.getInt("ID_Pedido"));
            Empleado empleado = EmpleadoDAO.findById(rs.getInt("ID_Empleado"));

            // Crear instancia y asignar datos
            Validacion v = new Validacion();
            v.setPedido(pedido);
            v.setEmpleado(empleado);
            v.setFechaValidacion(rs.getTimestamp("FechaValidacion").toLocalDateTime());
            v.setComentarioValidacion(rs.getString("ComentarioValidacion"));

            return v;

        } catch (Exception e) {
            throw new RuntimeException("Error al mapear Validacion", e);
        }
    }

}

