package com.miempresa.agroventas.model;

public class Cliente extends Usuario {
    private String direccion;
    private String telefono;

    public Cliente() {
        super();  // llama al constructor vacío de Usuario
    }

    public Cliente(int idUsuario, String nombre, String apellidos, String correo, String contrasena,
                   String direccion, String telefono) {
        super(idUsuario, nombre, apellidos, correo, contrasena);
        this.direccion = direccion;
        this.telefono  = telefono;
    }

    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idUsuario=" + getIdUsuario() +
                ", nombre='" + getNombre() + '\'' +
                ", apellidos='" + getApellidos() + '\'' +
                ", correo='" + getCorreo() + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}

