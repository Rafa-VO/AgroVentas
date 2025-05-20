package com.miempresa.agroventas.DAO;

import com.miempresa.agroventas.baseDatos.ConnectionBD;
import com.miempresa.agroventas.model.Maquinaria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaquinariaDAO {

    public Maquinaria findById(int id) throws Exception {
        String sql = "SELECT * FROM maquinaria WHERE ID_Maquinaria = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<Maquinaria> findAll() throws Exception {
        String sql = "SELECT * FROM maquinaria";
        try (Connection c = ConnectionBD.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            List<Maquinaria> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        }
    }

    public void create(Maquinaria m) throws Exception {
        String sql = "INSERT INTO maquinaria " +
                "(ID_Proveedor, Nombre, Descripcion, Tipo, Precio, Stock) " +
                "VALUES (?,?,?,?,?,?)";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, m.getIdProveedor());
            ps.setString(2, m.getNombre());
            ps.setString(3, m.getDescripcion());
            ps.setString(4, m.getTipo());
            ps.setDouble(5, m.getPrecio());
            ps.setInt   (6, m.getStock());
            ps.executeUpdate();
            try (ResultSet g = ps.getGeneratedKeys()) {
                if (g.next()) {
                    m.setIdMaquinaria(g.getInt(1));
                }
            }
        }
    }

    public void update(Maquinaria m) throws Exception {
        String sql = "UPDATE maquinaria SET " +
                "ID_Proveedor=?, Nombre=?, Descripcion=?, Tipo=?, Precio=?, Stock=? " +
                "WHERE ID_Maquinaria=?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt   (1, m.getIdProveedor());
            ps.setString(2, m.getNombre());
            ps.setString(3, m.getDescripcion());
            ps.setString(4, m.getTipo());
            ps.setDouble(5, m.getPrecio());
            ps.setInt   (6, m.getStock());
            ps.setInt   (7, m.getIdMaquinaria());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws Exception {
        String sql = "DELETE FROM maquinaria WHERE ID_Maquinaria=?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Maquinaria mapRow(ResultSet rs) throws SQLException {
        return new Maquinaria(
                rs.getInt   ("ID_Maquinaria"),
                rs.getInt   ("ID_Proveedor"),
                rs.getString("Nombre"),
                rs.getString("Descripcion"),
                rs.getString("Tipo"),
                rs.getDouble("Precio"),
                rs.getInt   ("Stock")
        );
    }
}



