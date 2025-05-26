package com.miempresa.agroventas.DAO;

import com.miempresa.agroventas.baseDatos.ConnectionBD;
import com.miempresa.agroventas.model.Proveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    private static final String SQL_FIND_BY_ID = "SELECT * FROM proveedor WHERE ID_Proveedor = ?";
    private static final String SQL_FIND_ALL = "SELECT * FROM proveedor";
    private static final String SQL_INSERT = "INSERT INTO proveedor (Nombre, Telefono, Email) VALUES (?,?,?)";
    private static final String SQL_UPDATE = "UPDATE proveedor SET Nombre = ?, Telefono = ?, Email = ? WHERE ID_Proveedor = ?";
    private static final String SQL_DELETE = "DELETE FROM proveedor WHERE ID_Proveedor = ?";

    /**
     * Busca un proveedor por su identificador.
     * @param id identificador del proveedor
     * @return instancia de Proveedor si existe, o null si no se encuentra
     * @throws Exception en caso de error de acceso a la base de datos
     */
    public static Proveedor findById(int id) throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /**
     * Recupera todos los proveedores registrados.
     * @return lista de Proveedor, vacía si no hay registros
     * @throws Exception en caso de error de acceso a la base de datos
     */
    public static List<Proveedor> findAll() throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            List<Proveedor> list = new ArrayList<>();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        }
    }

    /**
     * Inserta un nuevo proveedor en la base de datos.
     * Al usar RETURN_GENERATED_KEYS, asigna el ID generado al objeto Proveedor.
     * @param p objeto Proveedor con nombre, teléfono y email definidos
     * @throws Exception en caso de error durante la inserción
     */
    public static void create(Proveedor p) throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getTelefono());
            ps.setString(3, p.getEmail());
            ps.executeUpdate();
            try (ResultSet g = ps.getGeneratedKeys()) {
                if (g.next()) p.setIdProveedor(g.getInt(1));
            }
        }
    }

    /**
     * Actualiza los datos de un proveedor existente.
     * @param p objeto Proveedor con ID y nuevos valores de nombre, teléfono y email
     * @throws Exception en caso de error durante la actualización
     */
    public static void update(Proveedor p) throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getTelefono());
            ps.setString(3, p.getEmail());
            ps.setInt(4, p.getIdProveedor());
            ps.executeUpdate();
        }
    }

    /**
     * Elimina un proveedor por su identificador.
     * @param id identificador del proveedor a eliminar
     * @throws Exception en caso de error durante la eliminación
     */
    public static void delete(int id) throws Exception {
        try (Connection c = ConnectionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Mapea la fila actual del ResultSet a un objeto Proveedor.
     * @param rs ResultSet posicionado en la fila a convertir
     * @return instancia de Proveedor con los campos cargados
     * @throws SQLException en caso de error al leer datos del ResultSet
     */
    private static Proveedor mapRow(ResultSet rs) throws SQLException {
        return new Proveedor(
                rs.getInt("ID_Proveedor"),
                rs.getString("Nombre"),
                rs.getString("Telefono"),
                rs.getString("Email")
        );
    }
}




