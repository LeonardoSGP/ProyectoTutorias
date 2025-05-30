
package modelo;

import java.util.Date;

public class DatosTablaTutorado {
     private String fecha;
    private int hora;
    private String estado;
    private String acciones;

    public DatosTablaTutorado(String fecha, int hora, String estado, String acciones) {
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.acciones = acciones;
    }

    public String getFecha() { return fecha; }
    public int getHora() { return hora; }
    public String getEstado() { return estado; }
    public String getAcciones() { return acciones; }
}
