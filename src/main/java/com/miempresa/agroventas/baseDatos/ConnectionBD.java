package com.miempresa.agroventas.baseDatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Proporciona conexiones JDBC usando los datos leídos desde XMLManager.
 */
public class ConnectionBD {
    private static final ConnectionProperties props;

    static {
        try {
            // Carga una sola vez la configuración
            props = new XMLManager().loadProperties();
            // Aseguramos que el driver de MySQL esté registrado
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            throw new ExceptionInInitializerError("No se pudo cargar configuración de BD: " + e);
        }
    }

    /** Abre una nueva conexión JDBC. */
    public static Connection getConnection() throws SQLException {
        String url = String.format(
                "jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC",
                props.getServer(),
                props.getPort(),
                props.getDataBase()
        );
        return DriverManager.getConnection(url, props.getUser(), props.getPassword());
    }
}

