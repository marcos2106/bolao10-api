
package br.com.bolao.bolao10.repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.Colocacao;

@Repository
public class ColocacaoRepository extends GenericRepository {

	@Autowired
	private EntityManager em;

	public Colocacao save(Colocacao colocacao) {
		return super.persist(colocacao);
	}

	public Colocacao findById(Long idcolocacao) {
		try {
			return super.find(Colocacao.class, idcolocacao);
		}
		catch (Exception e) {
			return null;
		}
	}

	public Colocacao carregarColocacao() {

		StringBuilder sql = new StringBuilder();
		sql.append(" select c from Colocacao c ");

		TypedQuery<Colocacao> query = em.createQuery(sql.toString(), Colocacao.class);
		try {
			return query.getSingleResult();
		}
		catch (Exception e) {
			return null;
		}
	}

}
