package com.miempresa.agroventas.model;

public class Empleado extends Usuario {
    private String departamento;
    private String cargo;
    private double salario;

    public Empleado() {
        super();
    }

    public Empleado(int idUsuario, String nombre, String apellidos, String correo, String contrasena,
                    String departamento, String cargo, double salario) {
        super(idUsuario, nombre, apellidos, correo, contrasena);
        this.departamento = departamento;
        this.cargo        = cargo;
        this.salario      = salario;
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

