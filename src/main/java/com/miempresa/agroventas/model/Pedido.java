package com.miempresa.agroventas.model;

import com.miempresa.agroventas.interfaces.EstadosPedido;
import java.time.LocalDate;

public class Pedido {
    private int idPedido;
    private int idCliente;
    private LocalDate fechaPedido;
    private EstadosPedido estado;
    private String comentario;

    public Pedido() {}

    public Pedido(int idPedido, int idCliente, LocalDate fechaPedido, EstadosPedido estado, String comentario) {
        this.idPedido     = idPedido;
        this.idCliente    = idCliente;
        this.fechaPedido  = fechaPedido;
        this.estado       = estado;
        this.comentario   = comentario;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDate getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDate fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public EstadosPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadosPedido estado) {
        this.estado = estado;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "idPedido=" + idPedido +
                ", idCliente=" + idCliente +
                ", fechaPedido=" + fechaPedido +
                ", estado=" + estado +
                ", comentario='" + comentario + '\'' +
                '}';
    }
}

