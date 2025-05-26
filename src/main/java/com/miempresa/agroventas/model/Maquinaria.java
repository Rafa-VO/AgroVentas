package com.miempresa.agroventas.model;


import com.miempresa.agroventas.DAO.ProveedorDAO;

import java.util.ArrayList;
import java.util.List;

public class Maquinaria {
    private int idMaquinaria;
    private String nombre;
    private String descripcion;
    private String tipo;
    private double precio;
    private int stock;
    private List<Pedido> pedidos;
    private Proveedor proveedor;
    private Empleado empleado;

    public Maquinaria() {
        try {
            this.proveedor = ProveedorDAO.findById(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Maquinaria(Empleado empleado ,int idMaquinaria, Proveedor proveedor, String nombre, String descripcion, String tipo, double precio, int stock) {
        this.idMaquinaria = idMaquinaria;
        this.nombre       = nombre;
        this.descripcion  = descripcion;
        this.tipo         = tipo;
        this.precio       = precio;
        this.stock        = stock;
        this.proveedor = proveedor;
        this.pedidos = new ArrayList<>();
        this.empleado = empleado;
    }

    public int getIdMaquinaria() {
        return idMaquinaria;
    }
    public void setIdMaquinaria(int id) {
        this.idMaquinaria = id;
    }

    public Proveedor getProveedor() {
        if (proveedor == null) {
            try {
                this.proveedor = ProveedorDAO.findById(1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
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

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    @Override
    public String toString() {
        // Para depuración o combobox por defecto
        return "[" + idMaquinaria + "] " + nombre;
    }
}



