
package modelo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "tutor")
@NamedQueries({
    @NamedQuery(name = "Tutor.findAll", query = "SELECT t FROM Tutor t"),
    @NamedQuery(name = "Tutor.findByIdpersona", query = "SELECT t FROM Tutor t WHERE t.idpersona = :idpersona"),
    @NamedQuery(name = "Tutor.findByNumtar", query = "SELECT t FROM Tutor t WHERE t.numtar = :numtar"),
    @NamedQuery(name = "Tutor.findByNombre", query = "SELECT t FROM Tutor t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "Tutor.findByDias", query = "SELECT t FROM Tutor t WHERE t.dias = :dias"),
    @NamedQuery(name = "Tutor.findByCarrera", query = "SELECT t FROM Tutor t WHERE t.carrera = :carrera")})
public class Tutor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idpersona")
    private Integer idpersona;
    @Column(name = "numtar")
    private Integer numtar;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "dias")
    private String dias;
    @Column(name = "carrera")
    private String carrera;
    @OneToMany(mappedBy = "idtutor")
    private List<Tutorado> tutoradoList;
    @OneToMany(mappedBy = "idtutor")
    private List<Cita> citaList;

    public Tutor() {
    }

    public Tutor(Integer idpersona) {
        this.idpersona = idpersona;
    }

    public Integer getIdpersona() {
        return idpersona;
    }

    public void setIdpersona(Integer idpersona) {
        this.idpersona = idpersona;
    }

    public Integer getNumtar() {
        return numtar;
    }

    public void setNumtar(Integer numtar) {
        this.numtar = numtar;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDias() {
        return dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public List<Tutorado> getTutoradoList() {
        return tutoradoList;
    }

    public void setTutoradoList(List<Tutorado> tutoradoList) {
        this.tutoradoList = tutoradoList;
    }

    public List<Cita> getCitaList() {
        return citaList;
    }

    public void setCitaList(List<Cita> citaList) {
        this.citaList = citaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idpersona != null ? idpersona.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tutor)) {
            return false;
        }
        Tutor other = (Tutor) object;
        if ((this.idpersona == null && other.idpersona != null) || (this.idpersona != null && !this.idpersona.equals(other.idpersona))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.Tutor[ idpersona=" + idpersona + " ]";
    }
    
}
