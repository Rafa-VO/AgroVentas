package com.miempresa.agroventas.DAO;

import com.miempresa.agroventas.baseDatos.ConnectionBD;
import com.miempresa.agroventas.model.Validacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ValidacionDAO {

    private static final String SQL_FIND_BY_ID    = "SELECT * FROM validacion WHERE ID_Empleado = ? AND ID_Pedido = ?";
    private static final String SQL_FIND_ALL      = "SELECT * FROM validacion";
    private static final String SQL_INSERT        = "INSERT INTO validacion (ID_Pedido, ID_Empleado, FechaValidacion, ComentarioValidacion) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE        = "UPDATE validacion SET FechaValidacion = ?, ComentarioValidacion = ? WHERE ID_Empleado = ? AND ID_Pedido = ?";
    private static final String SQL_DELETE        = "DELETE FROM validacion WHERE ID_Empleado = ? AND ID_Pedido = ?";

    /**
     * Busca una validación concreta asociada a un empleado y un pedido.
     * @param idEmpleado identificador del empleado que hizo la validación
     * @param idPedido   identificador del pedido validado
     * @return instancia de Validacion si existe, o null si no se encontró
     * @throws Exception en caso de error de acceso a la base de datos
     */
    public Validacion findById(int idEmpleado, int idPedido) throws Exception {
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
    public List<Validacion> findAll() throws Exception {
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
    public void create(Validacion v) throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT)) {
            ps.setInt(1, v.getIdPedido());
            ps.setInt(2, v.getIdEmpleado());
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
    public void update(Validacion v) throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_UPDATE)) {
            ps.setTimestamp(1, Timestamp.valueOf(v.getFechaValidacion()));
            ps.setString(2, v.getComentarioValidacion());
            ps.setInt(3, v.getIdEmpleado());
            ps.setInt(4, v.getIdPedido());
            ps.executeUpdate();
        }
    }

    /**
     * Elimina una validación identificada por empleado y pedido.
     * @param idEmpleado identificador del empleado
     * @param idPedido   identificador del pedido
     * @throws Exception en caso de error durante la eliminación
     */
    public void delete(int idEmpleado, int idPedido) throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idEmpleado);
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        }
    }

    /**
     * Mapea la fila actual del ResultSet a un objeto Validacion.
     * @param rs ResultSet posicionado en la fila a convertir
     * @return instancia de Validacion con los datos de la fila
     * @throws SQLException en caso de error al leer del ResultSet
     */
    private Validacion mapRow(ResultSet rs) throws SQLException {
        return new Validacion(
                rs.getInt("ID_Pedido"),
                rs.getInt("ID_Empleado"),
                rs.getTimestamp("FechaValidacion").toLocalDateTime(),
                rs.getString("ComentarioValidacion")
        );
    }
}

