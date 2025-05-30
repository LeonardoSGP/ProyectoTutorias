
package control;

import control.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Cita;
import modelo.Tutorado;
import modelo.Tutoria;

public class TutoriaJpaController implements Serializable {

    public TutoriaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tutoria tutoria) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cita cita = tutoria.getCita();
            if (cita != null) {
                cita = em.getReference(cita.getClass(), cita.getIdcita());
                tutoria.setCita(cita);
            }
            Tutorado tutorado = tutoria.getTutorado();
            if (tutorado != null) {
                tutorado = em.getReference(tutorado.getClass(), tutorado.getIdtuto());
                tutoria.setTutorado(tutorado);
            }
            em.persist(tutoria);
            if (cita != null) {
                cita.getTutoriaList().add(tutoria);
                cita = em.merge(cita);
            }
            if (tutorado != null) {
                tutorado.getTutoriaList().add(tutoria);
                tutorado = em.merge(tutorado);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tutoria tutoria) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tutoria persistentTutoria = em.find(Tutoria.class, tutoria.getIdTutoria());
            Cita citaOld = persistentTutoria.getCita();
            Cita citaNew = tutoria.getCita();
            Tutorado tutoradoOld = persistentTutoria.getTutorado();
            Tutorado tutoradoNew = tutoria.getTutorado();
            if (citaNew != null) {
                citaNew = em.getReference(citaNew.getClass(), citaNew.getIdcita());
                tutoria.setCita(citaNew);
            }
            if (tutoradoNew != null) {
                tutoradoNew = em.getReference(tutoradoNew.getClass(), tutoradoNew.getIdtuto());
                tutoria.setTutorado(tutoradoNew);
            }
            tutoria = em.merge(tutoria);
            if (citaOld != null && !citaOld.equals(citaNew)) {
                citaOld.getTutoriaList().remove(tutoria);
                citaOld = em.merge(citaOld);
            }
            if (citaNew != null && !citaNew.equals(citaOld)) {
                citaNew.getTutoriaList().add(tutoria);
                citaNew = em.merge(citaNew);
            }
            if (tutoradoOld != null && !tutoradoOld.equals(tutoradoNew)) {
                tutoradoOld.getTutoriaList().remove(tutoria);
                tutoradoOld = em.merge(tutoradoOld);
            }
            if (tutoradoNew != null && !tutoradoNew.equals(tutoradoOld)) {
                tutoradoNew.getTutoriaList().add(tutoria);
                tutoradoNew = em.merge(tutoradoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tutoria.getIdTutoria();
                if (findTutoria(id) == null) {
                    throw new NonexistentEntityException("The tutoria with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tutoria tutoria;
            try {
                tutoria = em.getReference(Tutoria.class, id);
                tutoria.getIdTutoria();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tutoria with id " + id + " no longer exists.", enfe);
            }
            Cita cita = tutoria.getCita();
            if (cita != null) {
                cita.getTutoriaList().remove(tutoria);
                cita = em.merge(cita);
            }
            Tutorado tutorado = tutoria.getTutorado();
            if (tutorado != null) {
                tutorado.getTutoriaList().remove(tutoria);
                tutorado = em.merge(tutorado);
            }
            em.remove(tutoria);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tutoria> findTutoriaEntities() {
        return findTutoriaEntities(true, -1, -1);
    }

    public List<Tutoria> findTutoriaEntities(int maxResults, int firstResult) {
        return findTutoriaEntities(false, maxResults, firstResult);
    }

    private List<Tutoria> findTutoriaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tutoria.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Tutoria findTutoria(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tutoria.class, id);
        } finally {
            em.close();
        }
    }

    public int getTutoriaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tutoria> rt = cq.from(Tutoria.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
