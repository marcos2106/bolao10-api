
package br.com.segmedic.clubflex.repository;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import br.com.segmedic.clubflex.domain.Pet;

@Repository
public class PetRepository extends GenericRepository {

   @Autowired
   private EntityManager em;

   public Pet findById(Long id) {
      return super.find(Pet.class, id);
   }

   public List<Pet> findPetsBySub(Long subscriptionId) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select p ");
      sql.append(" from Pet p ");
      sql.append(" where p.subscription.id = :subscriptionId ");

      TypedQuery<Pet> query = em.createQuery(sql.toString(), Pet.class);
      query.setParameter("subscriptionId", subscriptionId);

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

}
