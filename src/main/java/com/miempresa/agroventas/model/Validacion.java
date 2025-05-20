package com.miempresa.agroventas.model;

import java.time.LocalDateTime;

public class Validacion {
    private int idPedido;
    private int idEmpleado;
    private LocalDateTime fechaValidacion;
    private String comentarioValidacion;

    public Validacion() {}

    public Validacion(int idPedido, int idEmpleado,
                      LocalDateTime fechaValidacion,
                      String comentarioValidacion) {
        this.idPedido            = idPedido;
        this.idEmpleado          = idEmpleado;
        this.fechaValidacion     = fechaValidacion;
        this.comentarioValidacion = comentarioValidacion;
    }

    public int getIdPedido() {
        return idPedido;
    }
    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }
    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
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

    @Override
    public String toString() {
        return "Validacion{" +
                "idPedido=" + idPedido +
                ", idEmpleado=" + idEmpleado +
                ", fechaValidacion=" + fechaValidacion +
                ", comentarioValidacion='" + comentarioValidacion + '\'' +
                '}';
    }
}

