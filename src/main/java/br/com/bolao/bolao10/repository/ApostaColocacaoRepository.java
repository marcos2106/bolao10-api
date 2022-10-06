
package br.com.bolao.bolao10.repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.ApostaColocacao;
import br.com.bolao.bolao10.model.ApostaColocacaoSelecao;

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

	public ApostaColocacao carregarApostaColocacao() {

		StringBuilder sql = new StringBuilder();
		sql.append(" select c from ApostaColocacao c ");

		TypedQuery<ApostaColocacao> query = em.createQuery(sql.toString(), ApostaColocacao.class);
		try {
			return query.getSingleResult();
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

}
