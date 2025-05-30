package control;

import control.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Tutor;
import modelo.Tutoria;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import modelo.Cita;

public class CitaJpaController implements Serializable {

    public CitaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cita cita) {
        if (cita.getTutoriaList() == null) {
            cita.setTutoriaList(new ArrayList<Tutoria>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tutor idtutor = cita.getIdtutor();
            if (idtutor != null) {
                idtutor = em.getReference(idtutor.getClass(), idtutor.getIdpersona());
                cita.setIdtutor(idtutor);
            }
            List<Tutoria> attachedTutoriaList = new ArrayList<Tutoria>();
            for (Tutoria tutoriaListTutoriaToAttach : cita.getTutoriaList()) {
                tutoriaListTutoriaToAttach = em.getReference(tutoriaListTutoriaToAttach.getClass(), tutoriaListTutoriaToAttach.getIdTutoria());
                attachedTutoriaList.add(tutoriaListTutoriaToAttach);
            }
            cita.setTutoriaList(attachedTutoriaList);
            em.persist(cita);
            if (idtutor != null) {
                idtutor.getCitaList().add(cita);
                idtutor = em.merge(idtutor);
            }
            for (Tutoria tutoriaListTutoria : cita.getTutoriaList()) {
                Cita oldCitaOfTutoriaListTutoria = tutoriaListTutoria.getCita();
                tutoriaListTutoria.setCita(cita);
                tutoriaListTutoria = em.merge(tutoriaListTutoria);
                if (oldCitaOfTutoriaListTutoria != null) {
                    oldCitaOfTutoriaListTutoria.getTutoriaList().remove(tutoriaListTutoria);
                    oldCitaOfTutoriaListTutoria = em.merge(oldCitaOfTutoriaListTutoria);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cita cita) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cita persistentCita = em.find(Cita.class, cita.getIdcita());
            Tutor idtutorOld = persistentCita.getIdtutor();
            Tutor idtutorNew = cita.getIdtutor();
            List<Tutoria> tutoriaListOld = persistentCita.getTutoriaList();
            List<Tutoria> tutoriaListNew = cita.getTutoriaList();
            if (idtutorNew != null) {
                idtutorNew = em.getReference(idtutorNew.getClass(), idtutorNew.getIdpersona());
                cita.setIdtutor(idtutorNew);
            }
            List<Tutoria> attachedTutoriaListNew = new ArrayList<Tutoria>();
            for (Tutoria tutoriaListNewTutoriaToAttach : tutoriaListNew) {
                tutoriaListNewTutoriaToAttach = em.getReference(tutoriaListNewTutoriaToAttach.getClass(), tutoriaListNewTutoriaToAttach.getIdTutoria());
                attachedTutoriaListNew.add(tutoriaListNewTutoriaToAttach);
            }
            tutoriaListNew = attachedTutoriaListNew;
            cita.setTutoriaList(tutoriaListNew);
            cita = em.merge(cita);
            if (idtutorOld != null && !idtutorOld.equals(idtutorNew)) {
                idtutorOld.getCitaList().remove(cita);
                idtutorOld = em.merge(idtutorOld);
            }
            if (idtutorNew != null && !idtutorNew.equals(idtutorOld)) {
                idtutorNew.getCitaList().add(cita);
                idtutorNew = em.merge(idtutorNew);
            }
            for (Tutoria tutoriaListOldTutoria : tutoriaListOld) {
                if (!tutoriaListNew.contains(tutoriaListOldTutoria)) {
                    tutoriaListOldTutoria.setCita(null);
                    tutoriaListOldTutoria = em.merge(tutoriaListOldTutoria);
                }
            }
            for (Tutoria tutoriaListNewTutoria : tutoriaListNew) {
                if (!tutoriaListOld.contains(tutoriaListNewTutoria)) {
                    Cita oldCitaOfTutoriaListNewTutoria = tutoriaListNewTutoria.getCita();
                    tutoriaListNewTutoria.setCita(cita);
                    tutoriaListNewTutoria = em.merge(tutoriaListNewTutoria);
                    if (oldCitaOfTutoriaListNewTutoria != null && !oldCitaOfTutoriaListNewTutoria.equals(cita)) {
                        oldCitaOfTutoriaListNewTutoria.getTutoriaList().remove(tutoriaListNewTutoria);
                        oldCitaOfTutoriaListNewTutoria = em.merge(oldCitaOfTutoriaListNewTutoria);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cita.getIdcita();
                if (findCita(id) == null) {
                    throw new NonexistentEntityException("The cita with id " + id + " no longer exists.");
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
            Cita cita;
            try {
                cita = em.getReference(Cita.class, id);
                cita.getIdcita();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cita with id " + id + " no longer exists.", enfe);
            }
            Tutor idtutor = cita.getIdtutor();
            if (idtutor != null) {
                idtutor.getCitaList().remove(cita);
                idtutor = em.merge(idtutor);
            }
            List<Tutoria> tutoriaList = cita.getTutoriaList();
            for (Tutoria tutoriaListTutoria : tutoriaList) {
                tutoriaListTutoria.setCita(null);
                tutoriaListTutoria = em.merge(tutoriaListTutoria);
            }
            em.remove(cita);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cita> findCitaEntities() {
        return findCitaEntities(true, -1, -1);
    }

    public List<Cita> findCitaEntities(int maxResults, int firstResult) {
        return findCitaEntities(false, maxResults, firstResult);
    }

    private List<Cita> findCitaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cita.class));
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

    public Cita findCita(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cita.class, id);
        } finally {
            em.close();
        }
    }

    public int getCitaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cita> rt = cq.from(Cita.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
