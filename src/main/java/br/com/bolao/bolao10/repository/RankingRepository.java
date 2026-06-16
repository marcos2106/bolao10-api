
package br.com.bolao.bolao10.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.Ranking;
import br.com.bolao.bolao10.domain.Usuario;
import br.com.bolao.bolao10.domain.enums.UserProfile;

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

	/**
	 * Carrega ranking com EAGER FETCH usando LEFT JOIN FETCH + DISTINCT.
	 * LEFT JOIN garante todos os rankings, DISTINCT evita duplicatas.
	 */
	public List<Ranking> carregarRanking() {
		long inicio = System.currentTimeMillis();
		
		// DISTINCT obrigatório com JOIN FETCH para evitar duplicatas
		StringBuilder sql = new StringBuilder();
		sql.append(" select distinct r from Ranking r ");
		sql.append(" left join fetch r.usuario u ");  // LEFT JOIN FETCH garante eager loading
		sql.append(" order by r.pontuacao desc, u.nome ");

		TypedQuery<Ranking> query = em.createQuery(sql.toString(), Ranking.class);
		
		long t1 = System.currentTimeMillis();
		System.out.println(">>> [REPOSITORY] Query preparada em: " + (t1-inicio) + "ms");
		
		try {
			List<Ranking> resultado = query.getResultList();
			long fim = System.currentTimeMillis();
			System.out.println(">>> [REPOSITORY] Query executada em: " + (fim-t1) + "ms - Retornou " + resultado.size() + " registros");
			System.out.println(">>> [REPOSITORY] TOTAL carregarRanking(): " + (fim-inicio) + "ms");
			return resultado;
		}
		catch (Exception e) {
			long fim = System.currentTimeMillis();
			System.out.println(">>> [REPOSITORY] Query FALHOU em: " + (fim-inicio) + "ms - " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public List<Ranking> carregarRankingUsuariosAtivosOrdenadosPorNome() {

		StringBuilder sql = new StringBuilder();
		sql.append(" select r from Ranking r ");
		sql.append(" join fetch r.usuario u ");
		sql.append(" where u.perfil = :perfil ");
		sql.append(" and u.ativo = true ");
		sql.append(" order by u.nome ");

		TypedQuery<Ranking> query = em.createQuery(sql.toString(), Ranking.class);
		query.setParameter("perfil", UserProfile.USER);

		return query.getResultList();
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
