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
import modelo.Tutorado;

public class TutoradoJpaController implements Serializable {

    public TutoradoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tutorado tutorado) {

        if (tutorado.getTutoriaList() == null) {
            tutorado.setTutoriaList(new ArrayList<Tutoria>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tutor idtutor = tutorado.getIdtutor();
            if (idtutor != null) {
                idtutor = em.getReference(idtutor.getClass(), idtutor.getIdpersona());
                tutorado.setIdtutor(idtutor);
            }
            List<Tutoria> attachedTutoriaList = new ArrayList<Tutoria>();
            for (Tutoria tutoriaListTutoriaToAttach : tutorado.getTutoriaList()) {
                tutoriaListTutoriaToAttach = em.getReference(tutoriaListTutoriaToAttach.getClass(), tutoriaListTutoriaToAttach.getIdTutoria());
                attachedTutoriaList.add(tutoriaListTutoriaToAttach);
            }
            tutorado.setTutoriaList(attachedTutoriaList);
            em.persist(tutorado);
            if (idtutor != null) {
                idtutor.getTutoradoList().add(tutorado);
                idtutor = em.merge(idtutor);
            }
            for (Tutoria tutoriaListTutoria : tutorado.getTutoriaList()) {
                Tutorado oldTutoradoOfTutoriaListTutoria = tutoriaListTutoria.getTutorado();
                tutoriaListTutoria.setTutorado(tutorado);
                tutoriaListTutoria = em.merge(tutoriaListTutoria);
                if (oldTutoradoOfTutoriaListTutoria != null) {
                    oldTutoradoOfTutoriaListTutoria.getTutoriaList().remove(tutoriaListTutoria);
                    oldTutoradoOfTutoriaListTutoria = em.merge(oldTutoradoOfTutoriaListTutoria);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tutorado tutorado) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tutorado persistentTutorado = em.find(Tutorado.class, tutorado.getIdtuto());
            Tutor idtutorOld = persistentTutorado.getIdtutor();
            Tutor idtutorNew = tutorado.getIdtutor();
            List<Tutoria> tutoriaListOld = persistentTutorado.getTutoriaList();
            List<Tutoria> tutoriaListNew = tutorado.getTutoriaList();
            if (idtutorNew != null) {
                idtutorNew = em.getReference(idtutorNew.getClass(), idtutorNew.getIdpersona());
                tutorado.setIdtutor(idtutorNew);
            }
            List<Tutoria> attachedTutoriaListNew = new ArrayList<Tutoria>();
            for (Tutoria tutoriaListNewTutoriaToAttach : tutoriaListNew) {
                tutoriaListNewTutoriaToAttach = em.getReference(tutoriaListNewTutoriaToAttach.getClass(), tutoriaListNewTutoriaToAttach.getIdTutoria());
                attachedTutoriaListNew.add(tutoriaListNewTutoriaToAttach);
            }
            tutoriaListNew = attachedTutoriaListNew;
            tutorado.setTutoriaList(tutoriaListNew);
            tutorado = em.merge(tutorado);
            if (idtutorOld != null && !idtutorOld.equals(idtutorNew)) {
                idtutorOld.getTutoradoList().remove(tutorado);
                idtutorOld = em.merge(idtutorOld);
            }
            if (idtutorNew != null && !idtutorNew.equals(idtutorOld)) {
                idtutorNew.getTutoradoList().add(tutorado);
                idtutorNew = em.merge(idtutorNew);
            }
            for (Tutoria tutoriaListOldTutoria : tutoriaListOld) {
                if (!tutoriaListNew.contains(tutoriaListOldTutoria)) {
                    tutoriaListOldTutoria.setTutorado(null);
                    tutoriaListOldTutoria = em.merge(tutoriaListOldTutoria);
                }
            }
            for (Tutoria tutoriaListNewTutoria : tutoriaListNew) {
                if (!tutoriaListOld.contains(tutoriaListNewTutoria)) {
                    Tutorado oldTutoradoOfTutoriaListNewTutoria = tutoriaListNewTutoria.getTutorado();
                    tutoriaListNewTutoria.setTutorado(tutorado);
                    tutoriaListNewTutoria = em.merge(tutoriaListNewTutoria);
                    if (oldTutoradoOfTutoriaListNewTutoria != null && !oldTutoradoOfTutoriaListNewTutoria.equals(tutorado)) {
                        oldTutoradoOfTutoriaListNewTutoria.getTutoriaList().remove(tutoriaListNewTutoria);
                        oldTutoradoOfTutoriaListNewTutoria = em.merge(oldTutoradoOfTutoriaListNewTutoria);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tutorado.getIdtuto();
                if (findTutorado(id) == null) {
                    throw new NonexistentEntityException("The tutorado with id " + id + " no longer exists.");
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
            Tutorado tutorado;
            try {
                tutorado = em.getReference(Tutorado.class, id);
                tutorado.getIdtuto();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tutorado with id " + id + " no longer exists.", enfe);
            }
            Tutor idtutor = tutorado.getIdtutor();
            if (idtutor != null) {
                idtutor.getTutoradoList().remove(tutorado);
                idtutor = em.merge(idtutor);
            }
            List<Tutoria> tutoriaList = tutorado.getTutoriaList();
            for (Tutoria tutoriaListTutoria : tutoriaList) {
                tutoriaListTutoria.setTutorado(null);
                tutoriaListTutoria = em.merge(tutoriaListTutoria);
            }
            em.remove(tutorado);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tutorado> findTutoradoEntities() {
        return findTutoradoEntities(true, -1, -1);
    }

    public List<Tutorado> findTutoradoEntities(int maxResults, int firstResult) {
        return findTutoradoEntities(false, maxResults, firstResult);
    }

    private List<Tutorado> findTutoradoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tutorado.class));
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

    public Tutorado findTutorado(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tutorado.class, id);
        } finally {
            em.close();
        }
    }

    public int getTutoradoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tutorado> rt = cq.from(Tutorado.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public List<Tutorado> findTutoradosByTutorId(Integer idTutor) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("SELECT t FROM Tutorado t WHERE t.idtutor.idpersona = :idTutor");
            q.setParameter("idTutor", idTutor);
            return q.getResultList();
        } finally {
            em.close();
        }
    }


}
