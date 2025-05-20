package com.miempresa.agroventas.DAO;

import com.miempresa.agroventas.baseDatos.ConnectionBD;
import com.miempresa.agroventas.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario findById(int id) throws Exception {
        String sql = "SELECT * FROM usuario WHERE ID_Usuario = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public Usuario findByEmail(String correo) throws Exception {
        String sql = "SELECT * FROM usuario WHERE Correo = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<Usuario> findAll() throws Exception {
        String sql = "SELECT * FROM usuario";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Usuario> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
            return lista;
        }
    }

    public void create(Usuario u) throws Exception {
        String sql = "INSERT INTO usuario (Nombre, Apellidos, Correo, Contrasena) VALUES (?,?,?,?)";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellidos());
            ps.setString(3, u.getCorreo());
            ps.setString(4, u.getContrasena());
            ps.executeUpdate();
            try (ResultSet gen = ps.getGeneratedKeys()) {
                if (gen.next()) {
                    u.setIdUsuario(gen.getInt(1));
                }
            }
        }
    }

    public void update(Usuario u) throws Exception {
        String sql = "UPDATE usuario SET Nombre=?, Apellidos=?, Correo=?, Contrasena=? WHERE ID_Usuario=?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellidos());
            ps.setString(3, u.getCorreo());
            ps.setString(4, u.getContrasena());
            ps.setInt(5, u.getIdUsuario());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws Exception {
        String sql = "DELETE FROM usuario WHERE ID_Usuario = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Busca un Usuario por correo y contraseña.
     * @return el Usuario si existe y coincide la contraseña, o null en caso contrario.
     */
    public Usuario findByEmailAndPassword(String correo, String contrasena) throws Exception {
        String sql = "SELECT * FROM usuario WHERE Correo = ? AND Contrasena = ?";
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setString(2, contrasena);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    private Usuario mapRow(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario  (rs.getInt   ("ID_Usuario"));
        u.setNombre     (rs.getString("Nombre"));
        u.setApellidos  (rs.getString("Apellidos"));
        u.setCorreo     (rs.getString("Correo"));
        u.setContrasena (rs.getString("Contrasena"));
        return u;
    }
}

