
package br.com.segmedic.clubflex.repository;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import br.com.segmedic.clubflex.domain.ClubCard;
import br.com.segmedic.clubflex.domain.Holder;
import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.domain.enums.ClubCardStatus;

@Repository
public class ClubCardRepository extends GenericRepository {

   @Autowired
   private EntityManager em;

   public ClubCard save(ClubCard card) {
      if (card.getId() == null) {
         em.persist(card);
      }
      else {
         em.merge(card);
      }
      return card;
   }

   public ClubCard findByToken(String token) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select c ");
      sql.append(" from ClubCard c");
      sql.append(" where c.token = :token");

      TypedQuery<ClubCard> query = em.createQuery(sql.toString(), ClubCard.class);
      query.setParameter("token", token);

      try {
         return query.getSingleResult();
      }
      catch (Exception e) {
         return null;
      }
   }

   public ClubCard findByDependentId(Long dependentId, boolean apenasAtivos) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select c ");
      sql.append(" from ClubCard c");
      sql.append(" where c.dependent.id = :dependentId");
      if (apenasAtivos)
         sql.append("   and c.status = :status ");

      TypedQuery<ClubCard> query = em.createQuery(sql.toString(), ClubCard.class);
      query.setParameter("dependentId", dependentId);

      if (apenasAtivos)
         query.setParameter("status", ClubCardStatus.OK);

      try {
         return query.getSingleResult();
      }
      catch (Exception e) {
         return null;
      }
   }

   public ClubCard findByDependentId(Long dependentId) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select c from ClubCard c");
      sql.append(" where c.dependent.id = :dependentId");

      TypedQuery<ClubCard> query = em.createQuery(sql.toString(), ClubCard.class);
      query.setParameter("dependentId", dependentId);
      try {
         return query.getSingleResult();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<ClubCard> findByHolderId(Long holderId) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select c ");
      sql.append(" from ClubCard c");
      sql.append(" where c.holder.id = :holderId ");

      TypedQuery<ClubCard> query = em.createQuery(sql.toString(), ClubCard.class);
      query.setParameter("holderId", holderId);

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public ClubCard findByHolderSubIsHolder(Holder holder, Subscription sub) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select c ");
      sql.append(" from ClubCard c");
      sql.append(" where c.holder.id = :holderId ");
      sql.append(" and c.subscription.id = :subId ");
      sql.append(" and c.isCardOfHolder = true ");
      sql.append(" and c.status = 'OK' ");

      TypedQuery<ClubCard> query = em.createQuery(sql.toString(), ClubCard.class);
      query.setParameter("holderId", holder.getId());
      query.setParameter("subId", sub.getId());

      try {
         return query.getSingleResult();
      }
      catch (Exception e) {
         return null;
      }
   }

   @Transactional
   public void deleteCardById(Long id) {
      try {
         em.createNativeQuery("DELETE FROM club_card WHERE idclubcard= :id")
                  .setParameter("id", id)
                  .executeUpdate();
      }
      catch (Exception exc) {
         System.out.println(exc);
      }
   }
}
