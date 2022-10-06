
package br.com.bolao.bolao10.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.RankingHistorico;

@Repository
public class RankingHistoricoRepository extends GenericRepository {

	@Autowired
	private EntityManager em;

	public RankingHistorico save(RankingHistorico ranking) {
		return super.persist(ranking);
	}

	public RankingHistorico findById(Long idRanking) {
		try {
			return super.find(RankingHistorico.class, idRanking);
		}
		catch (Exception e) {
			return null;
		}
	}

	public List<RankingHistorico> carregarRankingHistorico() {

		StringBuilder sql = new StringBuilder();
		sql.append(" select r from RankingHistorico r	");
		sql.append(" order by r.pontuacao desc 	");

		TypedQuery<RankingHistorico> query = em.createQuery(sql.toString(), RankingHistorico.class);
		try {
			return query.getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

	public RankingHistorico obterMelhorPosicaoPorUsuario(Long idUsuario) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select r from RankingHistorico r	");
		sql.append(" where r.usuario.id = :idUsuario 	");
		sql.append(" order by r.posicao, r.id desc 	");

		TypedQuery<RankingHistorico> query = em.createQuery(sql.toString(), RankingHistorico.class);
		query.setParameter("idUsuario", idUsuario);
		
		try {
			List<RankingHistorico> listaRanking = query.getResultList();
			if (!listaRanking.isEmpty()) {
				return listaRanking.get(0);
			}
			return null;
		}
		catch (Exception e) {
			return null;
		}
	}

	public List<RankingHistorico> carregarRankingHistoricoPorUsuario(Long idUsuario) {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select r from RankingHistorico r	");
		sql.append(" where r.usuario.id = :idUsuario 	");
		sql.append(" order by r.id					 	");

		TypedQuery<RankingHistorico> query = em.createQuery(sql.toString(), RankingHistorico.class);
		query.setParameter("idUsuario", idUsuario);
		
		try {
			return query.getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}
}
