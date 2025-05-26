package com.miempresa.agroventas.model;

import com.miempresa.agroventas.interfaces.EstadosPedido;
import com.miempresa.agroventas.util.Session;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private int idPedido;
    private LocalDate fechaPedido;
    private EstadosPedido estado;
    private String comentario;
    private Cliente cliente;
    private Empleado empleado;
    private List<Maquinaria> maquinarias;


    public Pedido() {
        try {
            if(Session.getCurrentCliente() != null) {
            this.cliente = Session.getCurrentCliente();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Pedido(int idPedido, LocalDate fechaPedido, EstadosPedido estado, String comentario) {
        this.idPedido     = idPedido;
        this.fechaPedido  = fechaPedido;
        this.estado       = estado;
        this.comentario   = comentario;
        try {
            if(Session.getCurrentCliente() != null) {
                this.cliente = Session.getCurrentCliente();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Maquinaria> getmaquinarias() {
        return maquinarias;
    }

    public void setMaquinarias(List<Maquinaria> maquinarias) {
        this.maquinarias = maquinarias;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "idPedido=" + idPedido +
                ", idCliente=" + getCliente().getIdUsuario() +
                ", fechaPedido=" + fechaPedido +
                ", estado=" + estado +
                ", comentario='" + comentario + '\'' +
                '}';
    }
}

