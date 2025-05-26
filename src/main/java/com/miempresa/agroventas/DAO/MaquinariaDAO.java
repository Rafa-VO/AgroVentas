package com.miempresa.agroventas.DAO;

import com.miempresa.agroventas.baseDatos.ConnectionBD;
import com.miempresa.agroventas.model.Maquinaria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Maquinaria. Proporciona métodos para buscar una máquina por su
 * identificador, listar todas las máquinas, crear nuevas, actualizar existentes
 * y eliminar según su identificador. Gestiona la obtención de la conexión y el mapeo
 * de los resultados SQL a objetos Maquinaria.
 */
public class MaquinariaDAO {

    private static final String SQL_FIND_BY_ID = "SELECT * FROM maquinaria WHERE ID_Maquinaria = ?";
    private static final String SQL_FIND_ALL = "SELECT * FROM maquinaria";
    private static final String SQL_INSERT = "INSERT INTO maquinaria (ID_Proveedor, Nombre, Descripcion, Tipo, Precio, Stock) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE maquinaria SET Nombre = ?, Descripcion = ?, Tipo = ?, Precio = ?, Stock = ? WHERE ID_Maquinaria = ?";
    private static final String SQL_DELETE = "DELETE FROM maquinaria WHERE ID_Maquinaria = ?";

    /**
     * Busca una instancia de Maquinaria por su identificador.
     * @param id identificador de la maquinaria
     * @return objeto Maquinaria con los datos de la fila encontrada, o null si no existe
     * @throws Exception si ocurre un error de acceso a la base de datos
     */
    public static Maquinaria findById(int id) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /**
     * Recupera la lista de todas las máquinas disponibles en la base de datos.
     * @return lista de objetos Maquinaria (vacía en caso de no haber registros)
     * @throws Exception si se produce un error durante la consulta
     */
    public static List<Maquinaria> findAll() throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            List<Maquinaria> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        }
    }

    /**
     * Inserta un nuevo registro de maquinaria. Al usar RETURN_GENERATED_KEYS, asigna
     * el identificador generado al objeto Maquinaria proporcionado.
     * @param m objeto Maquinaria con los campos ID_Proveedor, Nombre, Descripcion,
     * Tipo, Precio y Stock definidos
     * @throws Exception si falla la inserción o la obtención de la clave generada
     */
    public static void create(Maquinaria m) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getProveedor().getIdProveedor());
            ps.setString(2, m.getNombre());
            ps.setString(3, m.getDescripcion());
            ps.setString(4, m.getTipo());
            ps.setDouble(5, m.getPrecio());
            ps.setInt(6, m.getStock());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    m.setIdMaquinaria(gk.getInt(1));
                }
            }
        }
    }

    /**
     * Actualiza los datos de una máquina existente. Todos los campos salvo el
     * identificador pueden modificarse.
     * @param m objeto Maquinaria con el ID_Maquinaria y los nuevos valores de
     * ID_Proveedor, Nombre, Descripcion, Tipo, Precio y Stock
     * @throws Exception si ocurre un error al ejecutar la actualización
     */
    public static void update(Maquinaria m) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, m.getNombre());
            ps.setString(2, m.getDescripcion());
            ps.setString(3, m.getTipo());
            ps.setDouble(4, m.getPrecio());
            ps.setInt(5, m.getStock());
            ps.setInt(6, m.getIdMaquinaria());
            ps.executeUpdate();
        }
    }

    /**
     * Elimina el registro de maquinaria con el identificador especificado.
     * @param id identificador de la máquina a eliminar
     * @throws Exception si ocurre un error al ejecutar la eliminación
     */
    public static void delete(int id) throws Exception {
        try (Connection conn = ConnectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Convierte la fila actual de un ResultSet en una instancia de Maquinaria,
     * leyendo todas sus columnas.
     * @param rs ResultSet posicionado en la fila a mapear
     * @return objeto Maquinaria con datos cargados
     * @throws SQLException si falla la lectura de columnas
     */
    private static Maquinaria mapRow(ResultSet rs) throws SQLException {
        Maquinaria m = new Maquinaria();
        m.setIdMaquinaria(rs.getInt("ID_Maquinaria"));
        m.getProveedor().setIdProveedor(rs.getInt("ID_Proveedor"));
        m.setNombre(rs.getString("Nombre"));
        m.setDescripcion(rs.getString("Descripcion"));
        m.setTipo(rs.getString("Tipo"));
        m.setPrecio(rs.getDouble("Precio"));
        m.setStock(rs.getInt("Stock"));
        return m;
    }
}





