package com.tareasCliente.views.ML;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    
    private int idUsuario;
    private String username;
    private String email;
    private String nombre;
    private String password;
    private boolean enabled;
    private List<Tarea> tareas;
    
    public Usuario() {
        this.tareas = new ArrayList<>();
        this.enabled = false;
    }
    
    public Usuario(String username, String email, String nombre) {
        this.username = username;
        this.email = email;
        this.nombre = nombre;
        this.tareas = new ArrayList<>();
        this.enabled = false;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public List<Tarea> getTareas() {
        return tareas;
    }
    
    public void setTareas(List<Tarea> tareas) {
        this.tareas = tareas;
    }
    
}
    