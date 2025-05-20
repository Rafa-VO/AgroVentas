package com.miempresa.agroventas.DAO;

import com.miempresa.agroventas.baseDatos.ConnectionBD;
import com.miempresa.agroventas.interfaces.EstadosPedido;
import com.miempresa.agroventas.model.Pedido;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    // Helper para mapear la cadena de BD a nuestro enum, con trim() y uppercase()
    private EstadosPedido mapEstado(String estadoStr) {
        if (estadoStr == null) return EstadosPedido.PENDIENTE;
        try {
            return EstadosPedido.valueOf(estadoStr.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            // Si viene algo inesperado, devolvemos un valor por defecto
            return EstadosPedido.PENDIENTE;
        }
    }

    public Pedido findById(int id) throws Exception {
        String sql = "SELECT * FROM pedido WHERE ID_Pedido = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDate fecha = rs.getDate("FechaInicio").toLocalDate();
                    EstadosPedido estado = mapEstado(rs.getString("Estado"));
                    return new Pedido(
                            rs.getInt("ID_Pedido"),
                            rs.getInt("ID_Cliente"),
                            fecha,
                            estado,
                            rs.getString("Comentario")
                    );
                }
                return null;
            }
        }
    }

    public List<Pedido> findByClienteId(int idCliente) throws Exception {
        String sql = "SELECT * FROM pedido WHERE ID_Cliente = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                List<Pedido> list = new ArrayList<>();
                while (rs.next()) {
                    LocalDate fecha = rs.getDate("FechaInicio").toLocalDate();
                    EstadosPedido estado = mapEstado(rs.getString("Estado"));
                    list.add(new Pedido(
                            rs.getInt("ID_Pedido"),
                            rs.getInt("ID_Cliente"),
                            fecha,
                            estado,
                            rs.getString("Comentario")
                    ));
                }
                return list;
            }
        }
    }

    public List<Pedido> findAll() throws Exception {
        String sql = "SELECT * FROM pedido";
        try (Connection c = ConnectionBD.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            List<Pedido> list = new ArrayList<>();
            while (rs.next()) {
                LocalDate fecha = rs.getDate("FechaInicio").toLocalDate();
                EstadosPedido estado = mapEstado(rs.getString("Estado"));
                list.add(new Pedido(
                        rs.getInt("ID_Pedido"),
                        rs.getInt("ID_Cliente"),
                        fecha,
                        estado,
                        rs.getString("Comentario")
                ));
            }
            return list;
        }
    }

    public void create(Pedido p) throws Exception {
        String sql = "INSERT INTO pedido (ID_Cliente, FechaInicio, Estado, Comentario) VALUES (?,?,?,?)";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getIdCliente());
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

    public void update(Pedido p) throws Exception {
        String sql = "UPDATE pedido SET ID_Cliente=?, FechaInicio=?, Estado=?, Comentario=? WHERE ID_Pedido=?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, p.getIdCliente());
            ps.setDate(2, Date.valueOf(p.getFechaPedido()));
            ps.setString(3, p.getEstado().name());
            ps.setString(4, p.getComentario());
            ps.setInt(5, p.getIdPedido());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws Exception {
        String sql = "DELETE FROM pedido WHERE ID_Pedido=?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}




