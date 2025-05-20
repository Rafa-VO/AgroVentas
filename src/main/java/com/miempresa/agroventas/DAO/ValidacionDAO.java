package com.miempresa.agroventas.DAO;

import com.miempresa.agroventas.baseDatos.ConnectionBD;
import com.miempresa.agroventas.model.Validacion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ValidacionDAO {

    /**
     * Busca la validación de un pedido hecha por un empleado concreto.
     */
    public Validacion findById(int idEmpleado, int idPedido) throws Exception {
        String sql = "SELECT * FROM validacion WHERE ID_Empleado = ? AND ID_Pedido = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.setInt(2, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /** Todas las validaciones */
    public List<Validacion> findAll() throws Exception {
        String sql = "SELECT * FROM validacion";
        try (Connection c = ConnectionBD.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            List<Validacion> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        }
    }

    /** Inserta una nueva validación */
    public void create(Validacion v) throws Exception {
        String sql = "INSERT INTO validacion (ID_Pedido, ID_Empleado, FechaValidacion, ComentarioValidacion) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, v.getIdPedido());
            ps.setInt(2, v.getIdEmpleado());
            ps.setTimestamp(3, Timestamp.valueOf(v.getFechaValidacion()));
            ps.setString(4, v.getComentarioValidacion());
            ps.executeUpdate();
        }
    }

    /** Actualiza la fecha/comentario de una validación existente */
    public void update(Validacion v) throws Exception {
        String sql = "UPDATE validacion "
                + "SET FechaValidacion = ?, ComentarioValidacion = ? "
                + "WHERE ID_Empleado = ? AND ID_Pedido = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(v.getFechaValidacion()));
            ps.setString(2, v.getComentarioValidacion());
            ps.setInt(3, v.getIdEmpleado());
            ps.setInt(4, v.getIdPedido());
            ps.executeUpdate();
        }
    }

    /** Borra una validación */
    public void delete(int idEmpleado, int idPedido) throws Exception {
        String sql = "DELETE FROM validacion WHERE ID_Empleado = ? AND ID_Pedido = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        }
    }

    /** Mapea la fila del ResultSet a un objeto Validacion */
    private Validacion mapRow(ResultSet rs) throws SQLException {
        return new Validacion(
                rs.getInt("ID_Pedido"),
                rs.getInt("ID_Empleado"),
                rs.getTimestamp("FechaValidacion").toLocalDateTime(),
                rs.getString("ComentarioValidacion")
        );
    }
}

