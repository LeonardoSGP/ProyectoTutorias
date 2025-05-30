package modelo;

public class DatosTablaCitas {

    private Tutorado t;
    private Boolean asistencia;
    private String accion;

    public DatosTablaCitas(Tutorado tutorado) {
        t = tutorado;
        asistencia = false;
        accion = new String();
    }

    public Tutorado getT() {
        return t;
    }

    public void setT(Tutorado t) {
        this.t = t;
    }

    public Boolean getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(Boolean asistencia) {
        this.asistencia = asistencia;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

}
