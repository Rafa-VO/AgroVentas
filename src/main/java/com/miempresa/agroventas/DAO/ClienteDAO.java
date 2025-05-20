package com.miempresa.agroventas.DAO;

import com.miempresa.agroventas.baseDatos.ConnectionBD;
import com.miempresa.agroventas.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Cliente. Proporciona operaciones CRUD sobre la tabla
 * cliente y realiza el mapeo entre filas de ResultSet y objetos Cliente.
 */
public class ClienteDAO {

    private static final String SQL_FIND_BY_ID =
            "SELECT u.ID_Usuario, u.Nombre, u.Apellidos, u.Correo, " +
                    "       c.Direccion, c.Telefono " +
                    "FROM usuario u " +
                    "JOIN cliente c ON u.ID_Usuario = c.ID_Usuario " +
                    "WHERE u.ID_Usuario = ?";

    private static final String SQL_FIND_ALL =
            "SELECT u.ID_Usuario, u.Nombre, u.Apellidos, u.Correo, " +
                    "       c.Direccion, c.Telefono " +
                    "FROM usuario u " +
                    "JOIN cliente c ON u.ID_Usuario = c.ID_Usuario";

    private static final String SQL_INSERT =
            "INSERT INTO cliente (ID_Usuario, Direccion, Telefono) VALUES (?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE cliente SET Direccion = ?, Telefono = ? WHERE ID_Usuario = ?";

    private static final String SQL_DELETE =
            "DELETE FROM cliente WHERE ID_Usuario = ?";

    /**
     * Busca un Cliente por su identificador de usuario.
     * @param id identificador del usuario que actúa como cliente
     * @return objeto Cliente con los datos si existe, o null si no se encuentra
     * @throws Exception si ocurre un error de acceso a la base de datos
     */
    public Cliente findById(int id) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /**
     * Recupera todos los clientes existentes en la base de datos.
     * @return lista de objetos Cliente (vacía si no hay registros)
     * @throws Exception si ocurre un error de acceso a la base de datos
     */
    public List<Cliente> findAll() throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            List<Cliente> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
            return lista;
        }
    }

    /**
     * Inserta un nuevo registro de cliente en la base de datos.
     * @param cl objeto Cliente que contiene el ID de usuario, dirección y teléfono
     * @throws Exception si ocurre un error de inserción en la base de datos
     */
    public void create(Cliente cl) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setInt(1, cl.getIdUsuario());
            ps.setString(2, cl.getDireccion());
            ps.setString(3, cl.getTelefono());
            ps.executeUpdate();
        }
    }

    /**
     * Actualiza los datos de dirección y teléfono de un cliente existente.
     * @param cl objeto Cliente que contiene el ID de usuario y los nuevos valores
     * @throws Exception si ocurre un error de actualización en la base de datos
     */
    public void update(Cliente cl) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, cl.getDireccion());
            ps.setString(2, cl.getTelefono());
            ps.setInt(3, cl.getIdUsuario());
            ps.executeUpdate();
        }
    }

    /**
     * Elimina el registro de cliente asociado a un ID de usuario.
     * @param id identificador del usuario a eliminar como cliente
     * @throws Exception si ocurre un error de eliminación en la base de datos
     */
    public void delete(int id) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Mapea la fila actual del ResultSet a un objeto Cliente.
     * @param rs ResultSet posicionado en la fila a convertir
     * @return instancia de Cliente con todos los campos cargados
     * @throws SQLException si ocurre un error al leer columnas del ResultSet
     */
    private Cliente mapRow(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdUsuario (rs.getInt   ("ID_Usuario"));
        c.setNombre    (rs.getString("Nombre"));
        c.setApellidos (rs.getString("Apellidos"));
        c.setCorreo    (rs.getString("Correo"));
        c.setDireccion (rs.getString("Direccion"));
        c.setTelefono  (rs.getString("Telefono"));
        return c;
    }
}



