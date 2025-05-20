package com.miempresa.agroventas.DAO;

import com.miempresa.agroventas.baseDatos.ConnectionBD;
import com.miempresa.agroventas.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private static final String SQL_FIND_BY_ID               = "SELECT * FROM usuario WHERE ID_Usuario = ?";
    private static final String SQL_FIND_BY_EMAIL            = "SELECT * FROM usuario WHERE Correo = ?";
    private static final String SQL_FIND_ALL                 = "SELECT * FROM usuario";
    private static final String SQL_INSERT                   = "INSERT INTO usuario (Nombre, Apellidos, Correo, Contrasena) VALUES (?,?,?,?)";
    private static final String SQL_UPDATE                   = "UPDATE usuario SET Nombre = ?, Apellidos = ?, Correo = ?, Contrasena = ? WHERE ID_Usuario = ?";
    private static final String SQL_DELETE                   = "DELETE FROM usuario WHERE ID_Usuario = ?";
    private static final String SQL_FIND_BY_EMAIL_AND_PWD    = "SELECT * FROM usuario WHERE Correo = ? AND Contrasena = ?";

    /**
     * Busca un usuario por su identificador.
     * @param id el ID del usuario a buscar
     * @return objeto Usuario si existe, o null en caso contrario
     * @throws Exception si ocurre un error de acceso a la base de datos
     */
    public Usuario findById(int id) throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /**
     * Busca un usuario por su correo electrónico.
     * @param correo la dirección de correo del usuario
     * @return objeto Usuario si se encuentra uno con ese correo, o null si no existe
     * @throws Exception si ocurre un error de acceso a la base de datos
     */
    public Usuario findByEmail(String correo) throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_EMAIL)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /**
     * Recupera todos los usuarios existentes.
     * @return lista de todos los objetos Usuario en la base de datos
     * @throws Exception si ocurre un error de acceso a la base de datos
     */
    public List<Usuario> findAll() throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            List<Usuario> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
            return lista;
        }
    }

    /**
     * Inserta un nuevo usuario en la base de datos.
     * Asigna al objeto Usuario el ID generado tras la inserción.
     * @param u objeto Usuario a crear (sin ID)
     * @throws Exception si ocurre un error durante la inserción
     */
    public void create(Usuario u) throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
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

    /**
     * Actualiza los datos de un usuario existente.
     * @param u objeto Usuario con ID y nuevos valores de nombre, apellidos, correo y contraseña
     * @throws Exception si ocurre un error durante la actualización
     */
    public void update(Usuario u) throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellidos());
            ps.setString(3, u.getCorreo());
            ps.setString(4, u.getContrasena());
            ps.setInt(5, u.getIdUsuario());
            ps.executeUpdate();
        }
    }

    /**
     * Elimina un usuario por su identificador.
     * @param id el ID del usuario a eliminar
     * @throws Exception si ocurre un error durante la eliminación
     */
    public void delete(int id) throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Busca un usuario por correo y contraseña, utilizado para autenticación.
     * @param correo      la dirección de correo
     * @param contrasena  la contraseña en texto plano
     * @return objeto Usuario si las credenciales coinciden, o null si no
     * @throws Exception si ocurre un error de acceso a la base de datos
     */
    public Usuario findByEmailAndPassword(String correo, String contrasena) throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_EMAIL_AND_PWD)) {
            ps.setString(1, correo);
            ps.setString(2, contrasena);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /**
     * Mapea la fila actual del ResultSet a un objeto Usuario.
     * @param rs ResultSet posicionado en la fila a convertir
     * @return instancia de Usuario con los campos cargados
     * @throws SQLException si ocurre un error al obtener los valores del ResultSet
     */
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



