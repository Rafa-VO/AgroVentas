package com.miempresa.agroventas.DAO;

import com.miempresa.agroventas.baseDatos.ConnectionBD;
import com.miempresa.agroventas.model.Empleado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Empleado. Proporciona métodos para buscar,
 * crear, actualizar y eliminar registros en la tabla empleado,
 * así como convertir las filas de ResultSet en objetos Empleado.
 */
public class EmpleadoDAO {

    private static final String SQL_FIND_BY_ID = "SELECT u.ID_Usuario, u.Nombre, u.Apellidos, u.Correo, " + "       e.Departamento, e.Cargo, e.Salario " + "FROM usuario u " + "JOIN empleado e ON u.ID_Usuario = e.ID_Usuario " + "WHERE u.ID_Usuario = ?";
    private static final String SQL_FIND_ALL = "SELECT u.ID_Usuario, u.Nombre, u.Apellidos, u.Correo, " + "       e.Departamento, e.Cargo, e.Salario " + "FROM usuario u " + "JOIN empleado e ON u.ID_Usuario = e.ID_Usuario";
    private static final String SQL_INSERT = "INSERT INTO empleado (ID_Usuario, Departamento, Cargo, Salario) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE empleado SET Departamento = ?, Cargo = ?, Salario = ? WHERE ID_Usuario = ?";
    private static final String SQL_DELETE = "DELETE FROM empleado WHERE ID_Usuario = ?";

    /**
     * Busca un Empleado por su identificador de usuario.
     * @param id identificador del usuario
     * @return objeto Empleado si existe, o null si no se encuentra
     * @throws Exception si ocurre un error de acceso a la base de datos
     */
    public Empleado findById(int id) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /**
     * Recupera todos los empleados registrados en la base de datos.
     * @return lista de Empleado, vacía si no hay registros
     * @throws Exception si ocurre un error de acceso a la base de datos
     */
    public List<Empleado> findAll() throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            List<Empleado> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
            return lista;
        }
    }

    /**
     * Inserta un nuevo empleado en la base de datos.
     * @param e objeto Empleado con ID de usuario, departamento, cargo y salario
     * @throws Exception si ocurre un error durante la inserción
     */
    public void create(Empleado e) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setInt(1, e.getIdUsuario());
            ps.setString(2, e.getDepartamento());
            ps.setString(3, e.getCargo());
            ps.setDouble(4, e.getSalario());
            ps.executeUpdate();
        }
    }

    /**
     * Actualiza los datos de un empleado existente.
     * @param e objeto Empleado que contiene los nuevos valores y el ID de usuario
     * @throws Exception si ocurre un error durante la actualización
     */
    public void update(Empleado e) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, e.getDepartamento());
            ps.setString(2, e.getCargo());
            ps.setDouble(3, e.getSalario());
            ps.setInt(4, e.getIdUsuario());
            ps.executeUpdate();
        }
    }

    /**
     * Elimina un empleado de la base de datos por su ID de usuario.
     * @param id identificador del usuario a eliminar
     * @throws Exception si ocurre un error durante la eliminación
     */
    public void delete(int id) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Convierte la fila actual del ResultSet en una instancia de Empleado.
     * @param rs ResultSet posicionado en la fila a mapear
     * @return Empleado con los datos de la fila
     * @throws SQLException si ocurre un error al leer del ResultSet
     */
    private Empleado mapRow(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();e.setIdUsuario   (rs.getInt   ("ID_Usuario"));
        e.setNombre      (rs.getString("Nombre"));
        e.setApellidos   (rs.getString("Apellidos"));
        e.setCorreo      (rs.getString("Correo"));
        e.setDepartamento(rs.getString("Departamento"));
        e.setCargo       (rs.getString("Cargo"));
        e.setSalario     (rs.getDouble("Salario"));
        return e;
    }
}


