package com.miempresa.agroventas.model;

import com.miempresa.agroventas.DAO.PedidoDAO;

import java.util.List;

public class Cliente extends Usuario {
    private String direccion;
    private String telefono;
    private List<Pedido> pedidos;

    public Cliente() {
        super();
    }

    public Cliente(int idUsuario, String nombre, String apellidos, String correo, String contrasena, String direccion, String telefono) {
        super(idUsuario, nombre, apellidos, correo, contrasena);
        this.direccion = direccion;
        this.telefono  = telefono;
        try {
            this.pedidos = PedidoDAO.findByClienteId(idUsuario);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public List<Pedido> getPedidos() {
        if (pedidos == null) {
            try {
                pedidos = PedidoDAO.findByClienteId(getIdUsuario());
            } catch (Exception e) {
                throw new RuntimeException("No se pudieron cargar los pedidos", e);
            }
        }
        return pedidos;
    }

    public void anadirPedido(Pedido pedido) {

        if (pedidos == null) {
            try {
                pedidos = PedidoDAO.findByClienteId(getIdUsuario());
            } catch (Exception e) {
                throw new RuntimeException("No se pudieron cargar los pedidos", e);
            }
        }

        pedidos.add(pedido);
        try {
            PedidoDAO.create(pedido);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void eliminarPedido(Pedido pedido) {

        if (pedidos == null) {
            try {
                pedidos = PedidoDAO.findByClienteId(getIdUsuario());
            } catch (Exception e) {
                throw new RuntimeException("No se pudieron cargar los pedidos", e);
            }
        }

        pedidos.remove(pedido);
        try {
            PedidoDAO.delete(pedido.getIdPedido());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

