package com.miempresa.agroventas.DAO;

import com.miempresa.agroventas.baseDatos.ConnectionBD;
import com.miempresa.agroventas.model.Empleado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {

    public Empleado findById(int id) throws Exception {
        String sql =
                "SELECT u.ID_Usuario, u.Nombre, u.Apellidos, u.Correo, " +
                        "       e.Departamento, e.Cargo, e.Salario " +
                        "FROM usuario u " +
                        "JOIN empleado e ON u.ID_Usuario = e.ID_Usuario " +
                        "WHERE u.ID_Usuario = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<Empleado> findAll() throws Exception {
        String sql =
                "SELECT u.ID_Usuario, u.Nombre, u.Apellidos, u.Correo, " +
                        "       e.Departamento, e.Cargo, e.Salario " +
                        "FROM usuario u " +
                        "JOIN empleado e ON u.ID_Usuario = e.ID_Usuario";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Empleado> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
            return lista;
        }
    }

    public void create(Empleado e) throws Exception {
        String sql = "INSERT INTO empleado (ID_Usuario, Departamento, Cargo, Salario) VALUES (?, ?, ?, ?)";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, e.getIdUsuario());
            ps.setString(2, e.getDepartamento());
            ps.setString(3, e.getCargo());
            ps.setDouble(4, e.getSalario());
            ps.executeUpdate();
        }
    }

    public void update(Empleado e) throws Exception {
        String sql = "UPDATE empleado SET Departamento=?, Cargo=?, Salario=? WHERE ID_Usuario=?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getDepartamento());
            ps.setString(2, e.getCargo());
            ps.setDouble(3, e.getSalario());
            ps.setInt(4, e.getIdUsuario());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws Exception {
        String sql = "DELETE FROM empleado WHERE ID_Usuario = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Empleado mapRow(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();
        e.setIdUsuario   (rs.getInt   ("ID_Usuario"));
        e.setNombre      (rs.getString("Nombre"));
        e.setApellidos   (rs.getString("Apellidos"));
        e.setCorreo      (rs.getString("Correo"));
        e.setDepartamento(rs.getString("Departamento"));
        e.setCargo       (rs.getString("Cargo"));
        e.setSalario     (rs.getDouble("Salario"));
        return e;
    }
}

