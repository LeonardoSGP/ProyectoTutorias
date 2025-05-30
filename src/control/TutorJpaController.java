
package control;

import control.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Tutorado;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import modelo.Cita;
import modelo.Tutor;


public class TutorJpaController implements Serializable {

    public TutorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tutor tutor) {
        if (tutor.getTutoradoList() == null) {
            tutor.setTutoradoList(new ArrayList<Tutorado>());
        }
        if (tutor.getCitaList() == null) {
            tutor.setCitaList(new ArrayList<Cita>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Tutorado> attachedTutoradoList = new ArrayList<Tutorado>();
            for (Tutorado tutoradoListTutoradoToAttach : tutor.getTutoradoList()) {
                tutoradoListTutoradoToAttach = em.getReference(tutoradoListTutoradoToAttach.getClass(), tutoradoListTutoradoToAttach.getIdtuto());
                attachedTutoradoList.add(tutoradoListTutoradoToAttach);
            }
            tutor.setTutoradoList(attachedTutoradoList);
            List<Cita> attachedCitaList = new ArrayList<Cita>();
            for (Cita citaListCitaToAttach : tutor.getCitaList()) {
                citaListCitaToAttach = em.getReference(citaListCitaToAttach.getClass(), citaListCitaToAttach.getIdcita());
                attachedCitaList.add(citaListCitaToAttach);
            }
            tutor.setCitaList(attachedCitaList);
            em.persist(tutor);
            for (Tutorado tutoradoListTutorado : tutor.getTutoradoList()) {
                Tutor oldIdtutorOfTutoradoListTutorado = tutoradoListTutorado.getIdtutor();
                tutoradoListTutorado.setIdtutor(tutor);
                tutoradoListTutorado = em.merge(tutoradoListTutorado);
                if (oldIdtutorOfTutoradoListTutorado != null) {
                    oldIdtutorOfTutoradoListTutorado.getTutoradoList().remove(tutoradoListTutorado);
                    oldIdtutorOfTutoradoListTutorado = em.merge(oldIdtutorOfTutoradoListTutorado);
                }
            }
            for (Cita citaListCita : tutor.getCitaList()) {
                Tutor oldIdtutorOfCitaListCita = citaListCita.getIdtutor();
                citaListCita.setIdtutor(tutor);
                citaListCita = em.merge(citaListCita);
                if (oldIdtutorOfCitaListCita != null) {
                    oldIdtutorOfCitaListCita.getCitaList().remove(citaListCita);
                    oldIdtutorOfCitaListCita = em.merge(oldIdtutorOfCitaListCita);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tutor tutor) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tutor persistentTutor = em.find(Tutor.class, tutor.getIdpersona());
            List<Tutorado> tutoradoListOld = persistentTutor.getTutoradoList();
            List<Tutorado> tutoradoListNew = tutor.getTutoradoList();
            List<Cita> citaListOld = persistentTutor.getCitaList();
            List<Cita> citaListNew = tutor.getCitaList();
            List<Tutorado> attachedTutoradoListNew = new ArrayList<Tutorado>();
            for (Tutorado tutoradoListNewTutoradoToAttach : tutoradoListNew) {
                tutoradoListNewTutoradoToAttach = em.getReference(tutoradoListNewTutoradoToAttach.getClass(), tutoradoListNewTutoradoToAttach.getIdtuto());
                attachedTutoradoListNew.add(tutoradoListNewTutoradoToAttach);
            }
            tutoradoListNew = attachedTutoradoListNew;
            tutor.setTutoradoList(tutoradoListNew);
            List<Cita> attachedCitaListNew = new ArrayList<Cita>();
            for (Cita citaListNewCitaToAttach : citaListNew) {
                citaListNewCitaToAttach = em.getReference(citaListNewCitaToAttach.getClass(), citaListNewCitaToAttach.getIdcita());
                attachedCitaListNew.add(citaListNewCitaToAttach);
            }
            citaListNew = attachedCitaListNew;
            tutor.setCitaList(citaListNew);
            tutor = em.merge(tutor);
            for (Tutorado tutoradoListOldTutorado : tutoradoListOld) {
                if (!tutoradoListNew.contains(tutoradoListOldTutorado)) {
                    tutoradoListOldTutorado.setIdtutor(null);
                    tutoradoListOldTutorado = em.merge(tutoradoListOldTutorado);
                }
            }
            for (Tutorado tutoradoListNewTutorado : tutoradoListNew) {
                if (!tutoradoListOld.contains(tutoradoListNewTutorado)) {
                    Tutor oldIdtutorOfTutoradoListNewTutorado = tutoradoListNewTutorado.getIdtutor();
                    tutoradoListNewTutorado.setIdtutor(tutor);
                    tutoradoListNewTutorado = em.merge(tutoradoListNewTutorado);
                    if (oldIdtutorOfTutoradoListNewTutorado != null && !oldIdtutorOfTutoradoListNewTutorado.equals(tutor)) {
                        oldIdtutorOfTutoradoListNewTutorado.getTutoradoList().remove(tutoradoListNewTutorado);
                        oldIdtutorOfTutoradoListNewTutorado = em.merge(oldIdtutorOfTutoradoListNewTutorado);
                    }
                }
            }
            for (Cita citaListOldCita : citaListOld) {
                if (!citaListNew.contains(citaListOldCita)) {
                    citaListOldCita.setIdtutor(null);
                    citaListOldCita = em.merge(citaListOldCita);
                }
            }
            for (Cita citaListNewCita : citaListNew) {
                if (!citaListOld.contains(citaListNewCita)) {
                    Tutor oldIdtutorOfCitaListNewCita = citaListNewCita.getIdtutor();
                    citaListNewCita.setIdtutor(tutor);
                    citaListNewCita = em.merge(citaListNewCita);
                    if (oldIdtutorOfCitaListNewCita != null && !oldIdtutorOfCitaListNewCita.equals(tutor)) {
                        oldIdtutorOfCitaListNewCita.getCitaList().remove(citaListNewCita);
                        oldIdtutorOfCitaListNewCita = em.merge(oldIdtutorOfCitaListNewCita);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tutor.getIdpersona();
                if (findTutor(id) == null) {
                    throw new NonexistentEntityException("The tutor with id " + id + " no longer exists.");
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
            Tutor tutor;
            try {
                tutor = em.getReference(Tutor.class, id);
                tutor.getIdpersona();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tutor with id " + id + " no longer exists.", enfe);
            }
            List<Tutorado> tutoradoList = tutor.getTutoradoList();
            for (Tutorado tutoradoListTutorado : tutoradoList) {
                tutoradoListTutorado.setIdtutor(null);
                tutoradoListTutorado = em.merge(tutoradoListTutorado);
            }
            List<Cita> citaList = tutor.getCitaList();
            for (Cita citaListCita : citaList) {
                citaListCita.setIdtutor(null);
                citaListCita = em.merge(citaListCita);
            }
            em.remove(tutor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tutor> findTutorEntities() {
        return findTutorEntities(true, -1, -1);
    }

    public List<Tutor> findTutorEntities(int maxResults, int firstResult) {
        return findTutorEntities(false, maxResults, firstResult);
    }

    private List<Tutor> findTutorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tutor.class));
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

    public Tutor findTutor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tutor.class, id);
        } finally {
            em.close();
        }
    }

    public int getTutorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tutor> rt = cq.from(Tutor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
