package com.miempresa.agroventas.model;

import java.time.LocalDateTime;

public class Validacion {

    private LocalDateTime fechaValidacion;
    private String comentarioValidacion;
    private Pedido pedido;
    private Empleado empleado;

    public Validacion() {}

    public Validacion(LocalDateTime fechaValidacion, String comentarioValidacion, Pedido pedido, Empleado empleado) {
        this.fechaValidacion = fechaValidacion;
        this.comentarioValidacion = comentarioValidacion;
        this.pedido = pedido;
        this.empleado = empleado;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public LocalDateTime getFechaValidacion() {
        return fechaValidacion;
    }
    public void setFechaValidacion(LocalDateTime fechaValidacion) {
        this.fechaValidacion = fechaValidacion;
    }

    public String getComentarioValidacion() {
        return comentarioValidacion;
    }
    public void setComentarioValidacion(String comentarioValidacion) {
        this.comentarioValidacion = comentarioValidacion;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public String toString() {
        return "Validacion{" +
                "idPedido=" + pedido.getIdPedido() +
                ", idEmpleado=" + empleado.getIdUsuario() +
                ", fechaValidacion=" + fechaValidacion +
                ", comentarioValidacion='" + comentarioValidacion + '\'' +
                '}';
    }
}

