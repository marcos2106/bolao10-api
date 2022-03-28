
package br.com.segmedic.clubflex.repository;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import br.com.segmedic.clubflex.domain.CreditCard;

@Repository
public class CreditCardRepository extends GenericRepository {

   @Autowired
   private EntityManager em;

   public CreditCard save(CreditCard card) {
      if (card.getId() == null) {
         super.persist(card);
      }
      else {
         super.update(card);
      }
      return card;
   }

   public CreditCard findByHolderId(Long id) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select c ");
      sql.append(" from CreditCard c");
      sql.append(" where c.holder.id = :holderId");
      sql.append(" order by c.id DESC ");

      TypedQuery<CreditCard> query = em.createQuery(sql.toString(), CreditCard.class);
      query.setParameter("holderId", id);
      query.setMaxResults(1);

      try {
         return query.getSingleResult();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<CreditCard> listByHolderId(Long id) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select c ");
      sql.append(" from CreditCard c");
      sql.append(" where c.holder.id = :holderId");
      sql.append(" order by c.id DESC ");

      TypedQuery<CreditCard> query = em.createQuery(sql.toString(), CreditCard.class);
      query.setParameter("holderId", id);

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }
}
