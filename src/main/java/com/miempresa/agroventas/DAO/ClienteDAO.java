package com.miempresa.agroventas.DAO;

import com.miempresa.agroventas.baseDatos.ConnectionBD;
import com.miempresa.agroventas.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public Cliente findById(int id) throws Exception {
        String sql =
                "SELECT u.ID_Usuario, u.Nombre, u.Apellidos, u.Correo, " +
                        "       c.Direccion, c.Telefono " +
                        "FROM usuario u " +
                        "JOIN cliente c ON u.ID_Usuario = c.ID_Usuario " +
                        "WHERE u.ID_Usuario = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<Cliente> findAll() throws Exception {
        String sql =
                "SELECT u.ID_Usuario, u.Nombre, u.Apellidos, u.Correo, " +
                        "       c.Direccion, c.Telefono " +
                        "FROM usuario u " +
                        "JOIN cliente c ON u.ID_Usuario = c.ID_Usuario";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Cliente> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
            return lista;
        }
    }

    public void create(Cliente cl) throws Exception {
        String sql = "INSERT INTO cliente (ID_Usuario, Direccion, Telefono) VALUES (?, ?, ?)";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, cl.getIdUsuario());
            ps.setString(2, cl.getDireccion());
            ps.setString(3, cl.getTelefono());
            ps.executeUpdate();
        }
    }

    public void update(Cliente cl) throws Exception {
        String sql = "UPDATE cliente SET Direccion=?, Telefono=? WHERE ID_Usuario=?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cl.getDireccion());
            ps.setString(2, cl.getTelefono());
            ps.setInt(3, cl.getIdUsuario());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws Exception {
        String sql = "DELETE FROM cliente WHERE ID_Usuario = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Cliente mapRow(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdUsuario(rs.getInt("ID_Usuario"));
        c.setNombre    (rs.getString("Nombre"));
        c.setApellidos (rs.getString("Apellidos"));
        c.setCorreo    (rs.getString("Correo"));
        c.setDireccion (rs.getString("Direccion"));
        c.setTelefono  (rs.getString("Telefono"));
        return c;
    }
}

