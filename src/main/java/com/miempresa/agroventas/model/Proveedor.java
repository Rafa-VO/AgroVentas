package com.miempresa.agroventas.model;

import java.util.List;

public class Proveedor {
    private int idProveedor;
    private String nombre;
    private String telefono;
    private String email;
    private List <Maquinaria> maquinaria;

    public Proveedor() { }

    public Proveedor(int idProveedor, String nombre, String telefono, String email) {
        this.idProveedor = idProveedor;
        this.nombre      = nombre;
        this.telefono    = telefono;
        this.email       = email;
    }

    public int getIdProveedor() {
        return idProveedor;
    }
    public void setIdProveedor(int id) {
        this.idProveedor = id;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String n) {
        this.nombre = n;
    }

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String t) {
        this.telefono = t;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String e) {
        this.email = e;
    }

    public List<Maquinaria> getMaquinaria() {
        return maquinaria;
    }

    public void setMaquinaria(List<Maquinaria> maquinaria) {
        this.maquinaria = maquinaria;
    }

    @Override
    public String toString() {
        // Esto sale en el ComboBox:
        return "[" + idProveedor + "] " + nombre;
    }
}


