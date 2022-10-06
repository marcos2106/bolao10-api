
package br.com.bolao.bolao10.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.Pontuacao;

@Repository
public class PontuacaoRepository extends GenericRepository {

   @Autowired
   private EntityManager em;


   public Pontuacao save(Pontuacao pontuacao) {
      if (pontuacao.getId() == null) {
         super.persist(pontuacao);
      }
      else {
         super.update(pontuacao);
      }
      return pontuacao;
   }

   public Pontuacao findById(Long id) {
      return super.find(Pontuacao.class, id);
   }

   public List<Pontuacao> carregarRanking() {
	   
      StringBuilder sql = new StringBuilder();
      sql.append(" select p               	");
      sql.append(" from Pontuacao p 		");
      sql.append(" order by p.pontos DESC	");

      TypedQuery<Pontuacao> query = em.createQuery(sql.toString(), Pontuacao.class);

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }
}
