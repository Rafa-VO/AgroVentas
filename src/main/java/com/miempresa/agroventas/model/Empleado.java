package com.miempresa.agroventas.model;

import com.miempresa.agroventas.DAO.ClienteDAO;
import com.miempresa.agroventas.DAO.MaquinariaDAO;
import com.miempresa.agroventas.DAO.PedidoDAO;

import java.util.ArrayList;
import java.util.List;

public class Empleado extends Usuario {
    private String departamento;
    private String cargo;
    private double salario;
    private List<Pedido> pedidos;
    private List<Cliente> clientes;
    private List<Maquinaria> maquinarias;

    public Empleado() {
        super();
        this.clientes = new ArrayList<>();
        this.pedidos = new ArrayList<>();
        this.maquinarias = new ArrayList<>();
    }


    public Empleado(int idUsuario, String nombre, String apellidos, String correo, String contrasena,
                    String departamento, String cargo, double salario) {
        super(idUsuario, nombre, apellidos, correo, contrasena);
        this.departamento = departamento;
        this.cargo        = cargo;
        this.salario      = salario;

        try {
            this.maquinarias      = MaquinariaDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            this.clientes      = ClienteDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            this.pedidos      = PedidoDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String getDepartamento() {
        return departamento;
    }
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getCargo() {
        return cargo;
    }
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public double getSalario() {
        return salario;
    }
    public void setSalario(double salario) {
        this.salario = salario;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public List<Pedido> getPedidos() {

            try {
                pedidos = PedidoDAO.findAll();
            } catch (Exception e) {
                throw new RuntimeException("No se pudieron cargar los pedidos", e);
            }

        return pedidos;
    }

    public void eliminarPedido(Pedido pedido) {

        if (pedidos == null) {
            try {
                pedidos = PedidoDAO.findAll();
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

    public void actualizarPedido(Pedido pedido) {
        if (pedidos == null) {
            try {
                pedidos = PedidoDAO.findByClienteId(getIdUsuario());
            } catch (Exception e) {
                throw new RuntimeException("No se pudieron cargar los pedidos", e);
            }
        }

        pedidos.removeIf(p -> p.getIdPedido() == pedido.getIdPedido());

        try {
            PedidoDAO.update(pedido); // actualiza en BD
            pedidos.add(pedido);      // agrega el pedido actualizado a la lista local
        } catch (Exception e) {
            throw new RuntimeException("No se pudo actualizar el pedido", e);
        }
    }

    public List<Cliente> getClientes() {
        if (clientes == null || clientes.isEmpty()) {
            try {
                clientes = ClienteDAO.findAll();
            } catch (Exception e) {
                throw new RuntimeException("No se pudieron cargar los clientes", e);
            }
        }
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    public void eliminarCliente(Cliente cliente) {

        if (clientes == null) {
            try {
                clientes = ClienteDAO.findAll();
            } catch (Exception e) {
                throw new RuntimeException("No se pudieron cargar los pedidos", e);
            }
        }

        clientes.remove(cliente);
        try {
            ClienteDAO.delete(cliente.getIdUsuario());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void actualizarCliente(Cliente cliente) {

            clientes.remove(cliente);
            clientes.add(cliente);

            try {
                ClienteDAO.update(cliente);
            } catch (Exception e) {
                throw new RuntimeException("No se pudieron cargar los pedidos", e);
            }
    }

    public List<Maquinaria> getMaquinarias() {
        if (maquinarias == null || maquinarias.isEmpty()) {
            try {
                maquinarias = MaquinariaDAO.findAll();
            } catch (Exception e) {
                throw new RuntimeException("No se pudieron cargar las maquinarias", e);
            }
        }
        return maquinarias;
    }


    public void setMaquinarias(List<Maquinaria> maquinarias) {
        this.maquinarias = maquinarias;
    }

    public void eliminarMaquinaria(Maquinaria maquinaria) {

        if (maquinaria == null) {
            try {
                maquinarias = MaquinariaDAO.findAll();
            } catch (Exception e) {
                throw new RuntimeException("No se pudieron cargar los pedidos", e);
            }
        }

        maquinarias.remove(maquinaria);
        try {
            MaquinariaDAO.delete(maquinaria.getIdMaquinaria());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void actualizarMaquinaria(Maquinaria maquinaria) {

        maquinarias.remove(maquinaria);
        maquinarias.add(maquinaria);

        try {
            MaquinariaDAO.update(maquinaria);
        } catch (Exception e) {
            throw new RuntimeException("No se pudieron cargar los pedidos", e);
        }
    }

    public void anadirMaquinaria(Maquinaria maquinaria) {

        if (maquinarias == null) {
            try {
                maquinarias = MaquinariaDAO.findAll();
            } catch (Exception e) {
                throw new RuntimeException("No se pudieron cargar los pedidos", e);
            }
        }

        maquinarias.add(maquinaria);
        try {
            MaquinariaDAO.create(maquinaria);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "idUsuario=" + getIdUsuario() +
                ", nombre='" + getNombre() + '\'' +
                ", apellidos='" + getApellidos() + '\'' +
                ", correo='" + getCorreo() + '\'' +
                ", departamento='" + departamento + '\'' +
                ", cargo='" + cargo + '\'' +
                ", salario=" + salario +
                '}';
    }
}

