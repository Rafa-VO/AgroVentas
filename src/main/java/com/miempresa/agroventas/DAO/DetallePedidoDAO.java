package com.miempresa.agroventas.DAO;

import com.miempresa.agroventas.baseDatos.ConnectionBD;
import com.miempresa.agroventas.model.DetallePedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetallePedidoDAO {

    public DetallePedido findById(int idPedido, int idMaquinaria) throws Exception {
        String sql = "SELECT * FROM detallePedido WHERE ID_Pedido = ? AND ID_Maquinaria = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ps.setInt(2, idMaquinaria);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<DetallePedido> findAll() throws Exception {
        String sql = "SELECT * FROM detallePedido";
        try (Connection c = ConnectionBD.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            List<DetallePedido> list = new ArrayList<>();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        }
    }

    public List<DetallePedido> findByPedidoId(int idPedido) throws Exception {
        String sql = "SELECT ID_Pedido, ID_Maquinaria, Cantidad, PrecioUnitario "
                + "FROM detallePedido WHERE ID_Pedido = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
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

    public void create(DetallePedido dp) throws Exception {
        String sql = "INSERT INTO detallePedido (ID_Pedido, ID_Maquinaria, Cantidad, PrecioUnitario) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, dp.getIdPedido());
            ps.setInt(2, dp.getIdMaquinaria());
            ps.setInt(3, dp.getCantidad());
            ps.setDouble(4, dp.getPrecioUnitario());
            ps.executeUpdate();
        }
    }

    public void update(DetallePedido dp) throws Exception {
        String sql = "UPDATE detallePedido SET Cantidad = ?, PrecioUnitario = ? "
                + "WHERE ID_Pedido = ? AND ID_Maquinaria = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, dp.getCantidad());
            ps.setDouble(2, dp.getPrecioUnitario());
            ps.setInt(3, dp.getIdPedido());
            ps.setInt(4, dp.getIdMaquinaria());
            ps.executeUpdate();
        }
    }

    public void delete(int idPedido, int idMaquinaria) throws Exception {
        String sql = "DELETE FROM detallePedido WHERE ID_Pedido = ? AND ID_Maquinaria = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ps.setInt(2, idMaquinaria);
            ps.executeUpdate();
        }
    }

    private DetallePedido mapRow(ResultSet rs) throws SQLException {
        return new DetallePedido(
                rs.getInt("ID_Pedido"),
                rs.getInt("ID_Maquinaria"),
                rs.getInt("Cantidad"),
                rs.getDouble("PrecioUnitario")
        );
    }
}



