package com.miempresa.agroventas.model;

public class DetallePedido {
    private int cantidad;
    private double precioUnitario;
    private Pedido pedido;
    private Maquinaria maquinaria;

    public DetallePedido() {
    }

    public DetallePedido(Maquinaria maquinaria, Pedido pedido, int cantidad, double precioUnitario) {
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.pedido = pedido;
        this.maquinaria = maquinaria;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Maquinaria getMaquinaria() {
        return maquinaria;
    }

    public void setMaquinaria(Maquinaria maquinaria) {
        this.maquinaria = maquinaria;
    }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    @Override
    public String toString() {
        return "DetallePedido{" +
                "idPedido = " + getPedido().getIdPedido() +
                ", idMaquinaria = " + getMaquinaria().getIdMaquinaria() +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                '}';
    }
}
