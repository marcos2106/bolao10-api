
package br.com.bolao.bolao10.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.Situacao;

@Repository
public class SituacaoRepository extends GenericRepository {

	@Autowired
	private EntityManager em;

	public Situacao save(Situacao situacao) {
		return super.persist(situacao);
	}

	public Situacao findById(Long idSituacao) {
		return super.find(Situacao.class, idSituacao);
	}

	public Situacao stiuacaoAtiva() {

		StringBuilder sql = new StringBuilder();
		sql.append(" select c from Situacao c where c.ativo = true ");

		TypedQuery<Situacao> query = em.createQuery(sql.toString(), Situacao.class);
		try {
			return query.getSingleResult();
		}
		catch (Exception e) {
			return null;
		}
	}

	public List<Situacao> carregarSituacoes() {

		StringBuilder sql = new StringBuilder();
		sql.append(" select c from Situacao c order by 1 ");

		TypedQuery<Situacao> query = em.createQuery(sql.toString(), Situacao.class);
		try {
			return query.getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

	public void ativarSituacao(Long situacao) {

		StringBuilder sql1 = new StringBuilder();
		sql1.append(" UPDATE situacao SET ativo = false ");
		em.createNativeQuery(sql1.toString()).executeUpdate();

		StringBuilder sql2 = new StringBuilder();
		sql2.append(" UPDATE situacao SET ativo = true where idsituacao = :situacao ");
		em.createNativeQuery(sql2.toString())
				.setParameter("situacao", situacao)
				.executeUpdate();
	}

}
