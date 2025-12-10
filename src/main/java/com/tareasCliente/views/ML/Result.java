package com.tareasCliente.views.ML;
import java.util.List;

public class Result<T> {
    public boolean correct;
    public int status;
    public String errorMessage;
    public T object;
    public Exception ex;
    public List objects;  
}
