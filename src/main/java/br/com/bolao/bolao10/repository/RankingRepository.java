
package br.com.bolao.bolao10.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.Ranking;
import br.com.bolao.bolao10.domain.Usuario;

@Repository
public class RankingRepository extends GenericRepository {

	@Autowired
	private EntityManager em;
	
	public Ranking save(Ranking ranking) {
		return super.persist(ranking);
	}

	public Ranking findById(Long idUsuario) {
		
		Ranking rankingRetorno = null;
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select r from Ranking r		");
		sql.append(" where r.usuario.id = :idUsuario	");

		TypedQuery<Ranking> query = em.createQuery(sql.toString(), Ranking.class);
		query.setParameter("idUsuario", idUsuario);

		try {
			rankingRetorno = query.getSingleResult();
		}
		catch (Exception e) {}

		// se achar, retorna o objeto
		if (rankingRetorno != null) {
			return rankingRetorno;
			
		} else { // se não achar cria um novo registro
			Ranking ranking = new Ranking();
			ranking.setPosicaoAnterior(null);
			return ranking;
		}
	}

	public List<Ranking> carregarRanking() {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select r from Ranking r	");
		sql.append(" order by r.pontuacao desc, r.usuario.nome ");

		TypedQuery<Ranking> query = em.createQuery(sql.toString(), Ranking.class);
		try {
			return query.getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

	public Long obterPontuacaoPorUsuario(Long idUsuario) {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select r.pontuacao as pontos from Ranking r	");
		sql.append(" where r.usuario.id = :idUsuario			 	");

		TypedQuery<Integer> query = em.createQuery(sql.toString(), Integer.class);
		query.setParameter("idUsuario", idUsuario);
		try {
			if (query.getSingleResult()==null) {
				return 0L;
			}
			return query.getSingleResult().longValue();
		}
		catch (Exception e) {
			return 0L;
		}
	}

	public Long obterPosicaoPorUsuario(Long pontuacao) {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select count(r) as total from Ranking r	");
		sql.append(" where r.pontuacao > :pontuacao			 	");

		TypedQuery<Long> query = em.createQuery(sql.toString(), Long.class);
		query.setParameter("pontuacao", pontuacao.intValue());
		try {
			if (query.getSingleResult() != null)
				return (query.getSingleResult() + 1);
			return 0L;
		}
		catch (Exception e) {
			return 0L;
		}
	}

	public Usuario obterLiderRanking() {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select r.usuario from Ranking r	");
		sql.append(" order by r.pontuacao desc			");

		try {
			TypedQuery<Usuario> query = em.createQuery(sql.toString(), Usuario.class).setMaxResults(1);
			return query.getSingleResult();
		}
		catch (Exception e) {
			return null;
		}
	}

}
