package com.miempresa.agroventas.model;

public class DetallePedido {
    private int idPedido;
    private int idMaquinaria;
    private int cantidad;
    private double precioUnitario;

    public DetallePedido() {}

    public DetallePedido(int idPedido, int idMaquinaria,
                         int cantidad, double precioUnitario) {
        this.idPedido = idPedido;
        this.idMaquinaria = idMaquinaria;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public int getIdMaquinaria() { return idMaquinaria; }
    public void setIdMaquinaria(int idMaquinaria) { this.idMaquinaria = idMaquinaria; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    @Override
    public String toString() {
        return "DetallePedido{" +
                "idPedido=" + idPedido +
                ", idMaquinaria=" + idMaquinaria +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                '}';
    }
}
