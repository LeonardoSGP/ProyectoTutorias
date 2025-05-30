
package modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "tutorado")
@NamedQueries({
    @NamedQuery(name = "Tutorado.findAll", query = "SELECT t FROM Tutorado t"),
    @NamedQuery(name = "Tutorado.findByIdtuto", query = "SELECT t FROM Tutorado t WHERE t.idtuto = :idtuto"),
    @NamedQuery(name = "Tutorado.findByNc", query = "SELECT t FROM Tutorado t WHERE t.nc = :nc"),
    @NamedQuery(name = "Tutorado.findByNombre", query = "SELECT t FROM Tutorado t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "Tutorado.findByGenero", query = "SELECT t FROM Tutorado t WHERE t.genero = :genero"),
    @NamedQuery(name = "Tutorado.findByFechanac", query = "SELECT t FROM Tutorado t WHERE t.fechanac = :fechanac")})
public class Tutorado implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idtuto")
    private Integer idtuto;
    @Column(name = "nc")
    private String nc;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "genero")
    private Character genero;
    @Column(name = "fechanac")
    @Temporal(TemporalType.DATE)
    private Date fechanac;
    @JoinColumn(name = "idtutor", referencedColumnName = "idpersona")
    @ManyToOne
    private Tutor idtutor;
    @OneToMany(mappedBy = "tutorado")
    private List<Tutoria> tutoriaList;

    public Tutorado() {
    }

    public Tutorado(Integer idtuto) {
        this.idtuto = idtuto;
    }

    public Integer getIdtuto() {
        return idtuto;
    }

    public void setIdtuto(Integer idtuto) {
        this.idtuto = idtuto;
    }

    public String getNc() {
        return nc;
    }

    public void setNc(String nc) {
        this.nc = nc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Character getGenero() {
        return genero;
    }

    public void setGenero(Character genero) {
        this.genero = genero;
    }

    public Date getFechanac() {
        return fechanac;
    }

    public void setFechanac(Date fechanac) {
        this.fechanac = fechanac;
    }

    public Tutor getIdtutor() {
        return idtutor;
    }

    public void setIdtutor(Tutor idtutor) {
        this.idtutor = idtutor;
    }

    public List<Tutoria> getTutoriaList() {
        return tutoriaList;
    }

    public void setTutoriaList(List<Tutoria> tutoriaList) {
        this.tutoriaList = tutoriaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idtuto != null ? idtuto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tutorado)) {
            return false;
        }
        Tutorado other = (Tutorado) object;
        if ((this.idtuto == null && other.idtuto != null) || (this.idtuto != null && !this.idtuto.equals(other.idtuto))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.Tutorado[ idtuto=" + idtuto + " ]";
    }
    
}
