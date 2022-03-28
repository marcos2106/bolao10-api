
package br.com.segmedic.clubflex.repository;

import java.util.Comparator;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import br.com.segmedic.clubflex.domain.Reason;

@Repository
public class ReasonRepository extends GenericRepository {

   @Autowired
   private EntityManager em;

   public List<Reason> findByDescription(String descricao) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select r                            ");
      sql.append(" from Reason r                       ");
      sql.append(" where r.description like :descricao ");
      sql.append(" order by r.description              ");

      try {
         return em.createQuery(sql.toString(), Reason.class)
                  .setParameter("descricao", "%".concat(descricao).concat("%")).getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Reason> listAll() {
      List<Reason> listaRetorno = list(Reason.class);
      listaRetorno.sort(Comparator.comparing(Reason::getDescription));
      return listaRetorno;
   }

   public List<Reason> listAllActive() {

      StringBuilder sql = new StringBuilder();
      sql.append(" select r                  ");
      sql.append(" from Reason r             ");
      sql.append(" where r.isActive = true   ");
      sql.append(" order by r.description    ");

      try {
         return em.createQuery(sql.toString(), Reason.class).getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public Reason findById(Long id) {
      return super.find(Reason.class, id);
   }

   public Reason save(Reason reason) {
      if (reason.getId() == null) {
         super.persist(reason);
      }
      else {
         super.update(reason);
      }
      return reason;
   }

   public void deleteReason(Reason reasonObj) {
      delete(reasonObj);
   }
}
