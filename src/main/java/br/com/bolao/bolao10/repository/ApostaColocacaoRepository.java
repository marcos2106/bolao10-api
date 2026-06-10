
package br.com.bolao.bolao10.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.ApostaColocacao;
import br.com.bolao.bolao10.model.ApostaColocacaoSelecao;
import br.com.bolao.bolao10.support.Constants;

@Repository
public class ApostaColocacaoRepository extends GenericRepository {

	@Autowired
	private EntityManager em;

	@Autowired
	private UserRepository usuarioRepository;

	public ApostaColocacao save(ApostaColocacao colocacao) {
		return super.persist(colocacao);		
	}

	public ApostaColocacao findByUsuario(Long idUsuario) {

		ApostaColocacao colocacaoRetorno = null;
		try {
			String sql = " select c from ApostaColocacao c where c.usuario.id = :idUsuario ";
			TypedQuery<ApostaColocacao> query = em.createQuery(sql, ApostaColocacao.class);
			query.setParameter("idUsuario", idUsuario);
			try {
				colocacaoRetorno = query.getSingleResult();
			}
			catch (Exception e) {
				colocacaoRetorno = null;
			}
		}
		catch (Exception e) {}

		// se achar, retorna o objeto
		if (colocacaoRetorno != null) {
			return colocacaoRetorno;

		} else { // se não achar cria um novo registro
			ApostaColocacao colocacao = new ApostaColocacao();
			colocacao.setUsuario(usuarioRepository.findById(idUsuario));
			return colocacao;
		}
	}

	public List<ApostaColocacao> carregarApostaColocacao() {

		TypedQuery<ApostaColocacao> query = em.createQuery(
				" select c from ApostaColocacao c ", ApostaColocacao.class);
		try {
			return query.getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

	public ApostaColocacaoSelecao carregarApostaColocacaoPorSelecao(Long idSelecaoA, Long idSelecaoB) {

		ApostaColocacaoSelecao apSel = new ApostaColocacaoSelecao();

		StringBuilder sql = new StringBuilder();
		sql.append(" select             ");
		sql.append("   sum(case when c.campeao.id = :idA then 1 else 0 end), ");
		sql.append("   sum(case when c.vice.id = :idA then 1 else 0 end),  ");
		sql.append("   sum(case when c.terceiro.id = :idA then 1 else 0 end), ");
		sql.append("   sum(case when c.quarto.id = :idA then 1 else 0 end),  ");
		sql.append("   sum(case when c.artilharia.id = :idA then 1 else 0 end), ");
		sql.append("   sum(case when c.campeao.id = :idB then 1 else 0 end), ");
		sql.append("   sum(case when c.vice.id = :idB then 1 else 0 end),  ");
		sql.append("   sum(case when c.terceiro.id = :idB then 1 else 0 end), ");
		sql.append("   sum(case when c.quarto.id = :idB then 1 else 0 end),  ");
		sql.append("   sum(case when c.artilharia.id = :idB then 1 else 0 end) ");
		sql.append(" from ApostaColocacao c         ");

		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter("idA", idSelecaoA);
			query.setParameter("idB", idSelecaoB);
			Object[] result = (Object[]) query.getSingleResult();

			apSel.setCampeaoA(toLong(result[0]));
			apSel.setViceA(toLong(result[1]));
			apSel.setTerceiroA(toLong(result[2]));
			apSel.setQuartoA(toLong(result[3]));
			apSel.setArtilhariaA(toLong(result[4]));
			apSel.setCampeaoB(toLong(result[5]));
			apSel.setViceB(toLong(result[6]));
			apSel.setTerceiroB(toLong(result[7]));
			apSel.setQuartoB(toLong(result[8]));
			apSel.setArtilhariaB(toLong(result[9]));
		}
		catch (Exception e) {
			apSel.setCampeaoA(0L);
			apSel.setViceA(0L);
			apSel.setTerceiroA(0L);
			apSel.setQuartoA(0L);
			apSel.setArtilhariaA(0L);
			apSel.setCampeaoB(0L);
			apSel.setViceB(0L);
			apSel.setTerceiroB(0L);
			apSel.setQuartoB(0L);
			apSel.setArtilhariaB(0L);
		}

		return apSel;
	}

	private Long toLong(Object value) {
		if (value == null) {
			return 0L;
		}
		if (value instanceof Number) {
			return ((Number) value).longValue();
		}
		try {
			return Long.valueOf(value.toString());
		} catch (Exception e) {
			return 0L;
		}
	}

	public void zerarPontuacaoColocacao() {

		String sqlBasico = " UPDATE aposta_colocacao ac SET ";
		
		String sql = sqlBasico +" ac.pontoscampeao = "+ Constants.APOSTA_ERRADA +" where ac.pontoscampeao = null ";
		em.createNativeQuery(sql).executeUpdate();
		sql = sqlBasico +" ac.pontosvice = "+ Constants.APOSTA_ERRADA +" where ac.pontosvice = null ";
		em.createNativeQuery(sql).executeUpdate();
		sql = sqlBasico +" ac.pontosterceiro = "+ Constants.APOSTA_ERRADA +" where ac.pontosterceiro = null ";
		em.createNativeQuery(sql).executeUpdate();
		sql = sqlBasico +" ac.pontosquarto = "+ Constants.APOSTA_ERRADA +" where ac.pontosquarto = null ";
		em.createNativeQuery(sql).executeUpdate();
	}

	/** Retorna o ID do primeiro usuário que apostou no artilheiro provisório — Badge Goleador */
	public Long carregarIdUsuarioAcertouArtilheiro(Long idSelecaoArtilheiro) {
		try {
			String sql = "select c.usuario.id from ApostaColocacao c "
					+ "where c.artilharia.id = :idSelecao";
			List<Long> result = em.createQuery(sql, Long.class)
					.setParameter("idSelecao", idSelecaoArtilheiro)
					.setMaxResults(1)
					.getResultList();
			return result.isEmpty() ? null : result.get(0);
		} catch (Exception e) { return null; }
	}

}
