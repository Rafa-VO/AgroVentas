package com.miempresa.agroventas.model;

public class Maquinaria {
    private int idMaquinaria;
    private int idProveedor;
    private String nombre;
    private String descripcion;
    private String tipo;
    private double precio;
    private int stock;

    public Maquinaria() {}

    public Maquinaria(int idMaquinaria,
                      int idProveedor,
                      String nombre,
                      String descripcion,
                      String tipo,
                      double precio,
                      int stock) {
        this.idMaquinaria = idMaquinaria;
        this.idProveedor  = idProveedor;
        this.nombre       = nombre;
        this.descripcion  = descripcion;
        this.tipo         = tipo;
        this.precio       = precio;
        this.stock        = stock;
    }

    public int getIdMaquinaria() {
        return idMaquinaria;
    }
    public void setIdMaquinaria(int id) {
        this.idMaquinaria = id;
    }

    public int getIdProveedor() {
        return idProveedor;
    }
    public void setIdProveedor(int idProv) {
        this.idProveedor = idProv;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String n) {
        this.nombre = n;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String d) {
        this.descripcion = d;
    }

    public String getTipo() {
        return tipo;
    }
    public void setTipo(String t) {
        this.tipo = t;
    }

    public double getPrecio() {
        return precio;
    }
    public void setPrecio(double p) {
        this.precio = p;
    }

    public int getStock() {
        return stock;
    }
    public void setStock(int s) {
        this.stock = s;
    }

    @Override
    public String toString() {
        // Para depuración o combobox por defecto
        return "[" + idMaquinaria + "] " + nombre;
    }
}



