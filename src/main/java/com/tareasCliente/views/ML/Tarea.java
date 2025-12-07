package com.tareasCliente.views.ML;

import java.time.LocalDate;

public class Tarea {
    
    private int idTarea;
    private String titulo;
    private String descripcion;
    private LocalDate fechafin;
    private EstadoTarea estado;
    private Usuario usuario;
    
    public enum EstadoTarea {
        INICIADA, PENDIENTE, COMPLETADA
    }
    
    public Tarea() {
        this.estado = EstadoTarea.INICIADA; 
    }
    
    public Tarea(String titulo, String descripcion, LocalDate fechafin) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechafin = fechafin;
        this.estado = EstadoTarea.INICIADA;
    }
    
    public int getIdTarea() {
        return idTarea;
    }
    
    public void setIdTarea(int idTarea) {
        this.idTarea = idTarea;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public LocalDate getFechafin() {
        return fechafin;
    }
    
    public void setFechafin(LocalDate fechafin) {
        this.fechafin = fechafin;
    }
    
    public EstadoTarea getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoTarea estado) {
        this.estado = estado;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public boolean isCompletada() {
        return this.estado == EstadoTarea.COMPLETADA;
    }
    
    public boolean isPendiente() {
        return this.estado == EstadoTarea.PENDIENTE;
    }
    
    public boolean isIniciada() {
        return this.estado == EstadoTarea.INICIADA;
    }
    
   
    
    
}