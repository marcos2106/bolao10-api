
package br.com.bolao.bolao10.repository;

import java.util.List;

import javax.persistence.EntityManager;
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

		apSel.setCampeaoA(calcularQuantidadeAposta("campeao", idSelecaoA));
		apSel.setViceA(calcularQuantidadeAposta("vice", idSelecaoA));
		apSel.setTerceiroA(calcularQuantidadeAposta("terceiro", idSelecaoA));
		apSel.setQuartoA(calcularQuantidadeAposta("quarto", idSelecaoA));
		apSel.setArtilhariaA(calcularQuantidadeAposta("artilharia", idSelecaoA));

		apSel.setCampeaoB(calcularQuantidadeAposta("campeao", idSelecaoB));
		apSel.setViceB(calcularQuantidadeAposta("vice", idSelecaoB));
		apSel.setTerceiroB(calcularQuantidadeAposta("terceiro", idSelecaoB));
		apSel.setQuartoB(calcularQuantidadeAposta("quarto", idSelecaoB));
		apSel.setArtilhariaB(calcularQuantidadeAposta("artilharia", idSelecaoB));

		return apSel;
	}

	private Long calcularQuantidadeAposta(String colocacao, Long idSelecao) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select count(*) as total from ApostaColocacao c where "+ colocacao +".id=:idSelecao ");

		TypedQuery<Long> query = em.createQuery(sql.toString(), Long.class);
		query.setParameter("idSelecao", idSelecao);
		try {
			return query.getSingleResult();
		}
		catch (Exception e) {
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
