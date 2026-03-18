
package br.com.bolao.bolao10.repository;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.Aposta;
import br.com.bolao.bolao10.domain.Usuario;
import br.com.bolao.bolao10.model.ApostaPartida;
import br.com.bolao.bolao10.support.Constants;

@Repository
public class ApostaRepository extends GenericRepository {

	@Autowired
	private EntityManager em;

	@Autowired
	private PartidaRepository partidaRepository;

	@Autowired
	private UserRepository usuarioRepository;

	public Aposta save(Aposta aposta) {
		return super.persist(aposta);
	}

	public Aposta findById(Long idPartida, Long idUsuario) {

		Aposta apostaRetorno = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" select a from Aposta a	 			");
		sql.append(" where a.partida.id = :idPartida	");
		sql.append(" and a.usuario.id = :idUsuario		");

		TypedQuery<Aposta> query = em.createQuery(sql.toString(), Aposta.class);
		query.setParameter("idPartida", idPartida);
		query.setParameter("idUsuario", idUsuario);

		try {
			apostaRetorno = query.getSingleResult();
		}
		catch (Exception e) {}

		// se achar, retorna o objeto
		if (apostaRetorno != null) {
			return apostaRetorno;

		} else { // se não achar cria um novo registro
			Aposta aposta = new Aposta();
			aposta.setPartida(partidaRepository.findById(idPartida));
			aposta.setUsuario(usuarioRepository.findById(idUsuario));
			aposta.setPlacarA(null);
			aposta.setPlacarB(null);
			return aposta;
		}
	}

	public ApostaPartida calcularApostasPorPartida(Long idPartida) {

		ApostaPartida ap = new ApostaPartida();
		String sqlBasico = " select a from Aposta a where a.partida.id = :idPartida	";

		// QNTS VITORIAS SELECAO A
		TypedQuery<Aposta> queryA = em.createQuery(sqlBasico + " and a.placarA > a.placarB ", Aposta.class);
		queryA.setParameter("idPartida", idPartida);
		try {
			ap.setNumSelecaoA(queryA.getResultList().size());
		}
		catch (Exception e) {
			ap.setNumSelecaoA(0);			
		}

		// QNTS EMPATE
		TypedQuery<Aposta> queryE = em.createQuery(sqlBasico +" and a.placarA = a.placarB	", Aposta.class);
		queryE.setParameter("idPartida", idPartida);
		try {
			ap.setNumEmpate(queryE.getResultList().size());
		}
		catch (Exception e) {
			ap.setNumEmpate(0);			
		}

		// QNTS VITORIAS SELECAO B
		TypedQuery<Aposta> queryB = em.createQuery(sqlBasico +" and a.placarA < a.placarB	", Aposta.class);
		queryB.setParameter("idPartida", idPartida);
		try {
			ap.setNumSelecaoB(queryB.getResultList().size());
		}
		catch (Exception e) {
			ap.setNumSelecaoB(0);			
		}
		return ap;
	}

	public List<Aposta> carregarApostaPorPartida(Long idPartida) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select a from Aposta a	 			");
		sql.append(" where a.partida.id = :idPartida		");

		TypedQuery<Aposta> query = em.createQuery(sql.toString(), Aposta.class);
		query.setParameter("idPartida", idPartida);

		try {
			return query.getResultList();
		}
		catch (Exception e) {}
		return null;
	}

	public List<Aposta> carregarApostaPorUsuario(Long idUsuario) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select a from Aposta a	 			");
		sql.append(" where a.usuario.id = :idUsuario	");
		sql.append(" order by a.partida.dataHora 		");

		TypedQuery<Aposta> query = em.createQuery(sql.toString(), Aposta.class);
		query.setParameter("idUsuario", idUsuario);

		try {
			return query.getResultList();
		}
		catch (Exception e) {}
		return null;
	}

	public Long obterPlacarExatoPorUsuario(Long idUsuario) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select count(a) as total from Aposta a	");
		sql.append(" where a.usuario.id = :idUsuario		");
		sql.append(" and a.pontuacao = :pontuacao			");

		TypedQuery<Long> query = em.createQuery(sql.toString(), Long.class);
		query.setParameter("idUsuario", idUsuario);
		query.setParameter("pontuacao", Constants.APOSTA_CORRETA);

		try {
			return query.getSingleResult();
		}
		catch (Exception e) {
			return 0L;
		}
	}

	public Integer calcularPontuacaoProvisoria(Usuario usuario) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select sum(a.pontuacao_provisoria) as somaPontos ");
		sql.append(" from bolao10.aposta a where a.idusuario = :idUsuario ");
		sql.append(" and a.pontuacao_provisoria is not NULL ");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("idUsuario", usuario.getId());

		BigDecimal pontuacaoBD = (BigDecimal) query.getSingleResult();

		return (pontuacaoBD == null) ? 0 : pontuacaoBD.intValue();		
	}

	/** Retorna o ID do usuário com mais placares exatos (pontuacao = 5) — Badge Beteiro */
	public Long carregarIdUsuarioMaisPlacarExato() {
		try {
			String sql = "select a.usuario.id from Aposta a where a.pontuacao = 5 "
					+ "group by a.usuario.id order by count(a) desc";
			List<Long> result = em.createQuery(sql, Long.class).setMaxResults(1).getResultList();
			return result.isEmpty() ? null : result.get(0);
		} catch (Exception e) { return null; }
	}

	/** Retorna o ID do usuário com mais placares zerados (pontuacao = 0) — Badge Gato Preto */
	public Long carregarIdUsuarioMaisZerou() {
		try {
			String sql = "select a.usuario.id from Aposta a where a.pontuacao = 0 "
					+ "group by a.usuario.id order by count(a) desc";
			List<Long> result = em.createQuery(sql, Long.class).setMaxResults(1).getResultList();
			return result.isEmpty() ? null : result.get(0);
		} catch (Exception e) { return null; }
	}

	/** Retorna o ID do usuário com mais empates acertados — Badge Meia Boca */
	public Long carregarIdUsuarioMaisEmpate() {
		try {
			// Empate acertado: apostou empate E a partida empatou (placarA = placarB e pontuacao > 0)
			String sql = "select a.usuario.id from Aposta a "
					+ "where a.placarA = a.placarB and a.pontuacao is not null and a.pontuacao > 0 "
					+ "group by a.usuario.id order by count(a) desc";
			List<Long> result = em.createQuery(sql, Long.class).setMaxResults(1).getResultList();
			return result.isEmpty() ? null : result.get(0);
		} catch (Exception e) { return null; }
	}

	/**
	 * Retorna IDs dos usuários que zeraram pontos na ÚLTIMA partida calculada — Badge Empacado.
	 * Considera a última partida que teve pontuação calculada (pontuacao not null).
	 */
	public java.util.List<Long> carregarIdsUsuariosEmpacados() {
		try {
			// Encontra o ID da última partida que teve pontuação calculada
			String sqlUltimaPartida = "select a.partida.id from Aposta a "
					+ "where a.pontuacao is not null "
					+ "order by a.partida.dataHora desc";
			List<Long> idsUltimaPartida = em.createQuery(sqlUltimaPartida, Long.class).setMaxResults(1).getResultList();
			if (idsUltimaPartida.isEmpty()) return new java.util.ArrayList<>();
			Long idUltimaPartida = idsUltimaPartida.get(0);

			// Busca usuários que zeraram nessa partida
			String sql = "select a.usuario.id from Aposta a "
					+ "where a.partida.id = :idPartida and a.pontuacao = 0";
			return em.createQuery(sql, Long.class)
					.setParameter("idPartida", idUltimaPartida)
					.getResultList();
		} catch (Exception e) { return new java.util.ArrayList<>(); }
	}

}
