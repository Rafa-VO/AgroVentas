package com.miempresa.agroventas.DAO;

import com.miempresa.agroventas.baseDatos.ConnectionBD;
import com.miempresa.agroventas.model.Proveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    public Proveedor findById(int id) throws Exception {
        String sql = "SELECT * FROM proveedor WHERE ID_Proveedor = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<Proveedor> findAll() throws Exception {
        String sql = "SELECT * FROM proveedor";
        try (Connection c = ConnectionBD.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            List<Proveedor> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        }
    }

    public void create(Proveedor p) throws Exception {
        String sql = "INSERT INTO proveedor (Nombre, Telefono, Email) VALUES (?,?,?)";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getTelefono());
            ps.setString(3, p.getEmail());
            ps.executeUpdate();
            try (ResultSet g = ps.getGeneratedKeys()) {
                if (g.next()) {
                    p.setIdProveedor(g.getInt(1));
                }
            }
        }
    }

    public void update(Proveedor p) throws Exception {
        String sql = "UPDATE proveedor SET Nombre = ?, Telefono = ?, Email = ? WHERE ID_Proveedor = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getTelefono());
            ps.setString(3, p.getEmail());
            ps.setInt(4, p.getIdProveedor());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws Exception {
        String sql = "DELETE FROM proveedor WHERE ID_Proveedor = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Mapea un ResultSet a un objeto Proveedor */
    private Proveedor mapRow(ResultSet rs) throws SQLException {
        return new Proveedor(
                rs.getInt   ("ID_Proveedor"),
                rs.getString("Nombre"),
                rs.getString("Telefono"),
                rs.getString("Email")
        );
    }
}


