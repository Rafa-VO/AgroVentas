package com.miempresa.agroventas.baseDatos;

/**
 * Simple POJO que almacena los parámetros de conexión.
 */
public class ConnectionProperties {
    private final String server;
    private final int port;
    private final String dataBase;
    private final String user;
    private final String password;

    public ConnectionProperties(String server, int port, String dataBase, String user, String password) {
        this.server = server;
        this.port = port;
        this.dataBase = dataBase;
        this.user = user;
        this.password = password;
    }

    public String getServer()     { return server; }
    public int    getPort()       { return port; }
    public String getDataBase()   { return dataBase; }
    public String getUser()       { return user; }
    public String getPassword()   { return password; }
}

