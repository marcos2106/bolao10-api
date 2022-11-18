
package br.com.bolao.bolao10.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.RankingCustomizado;

@Repository
public class RankingCustomizadoRepository extends GenericRepository {

	@Autowired
	private EntityManager em;
	
	public RankingCustomizado save(RankingCustomizado rankingCustomizado) {
		return super.persist(rankingCustomizado);
	}

	public RankingCustomizado findById(Long id) {
		return super.find(RankingCustomizado.class, id);
	}

	public List<RankingCustomizado> carregarRankingCustomizado(Long idUsuario) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select rc from RankingCustomizado rc	");
		sql.append(" where rc.usuario.id = :idUsuario ");
		sql.append(" order by rc.id ");

		TypedQuery<RankingCustomizado> query = em.createQuery(sql.toString(), RankingCustomizado.class);
		query.setParameter("idUsuario", idUsuario);
		try {
			return query.getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

}
