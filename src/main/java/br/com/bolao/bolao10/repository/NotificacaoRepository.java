package br.com.bolao.bolao10.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.Notificacao;

@Repository
public class NotificacaoRepository extends GenericRepository {

	@Autowired
	private EntityManager em;
	
	public Notificacao save(Notificacao notificacao) {
		return super.persist(notificacao);
	}

	public List<Notificacao> obterUltimasCinco() {
		StringBuilder sql = new StringBuilder();
		sql.append(" select n from Notificacao n ");
		sql.append(" order by n.dataCriacao desc ");

		try {
			// Trazemos as ultimas 5 ordenadas pelas mais recentes
			TypedQuery<Notificacao> query = em.createQuery(sql.toString(), Notificacao.class).setMaxResults(5);
			return query.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public List<Notificacao> obterTodasPaginado(int offset, int pageSize) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select n from Notificacao n ");
		sql.append(" order by n.dataCriacao desc ");

		try {
			TypedQuery<Notificacao> query = em.createQuery(sql.toString(), Notificacao.class);
			query.setFirstResult(offset);
			query.setMaxResults(pageSize);
			return query.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public Long obterContagemTotal() {
		StringBuilder sql = new StringBuilder();
		sql.append(" select count(n) from Notificacao n ");

		try {
			TypedQuery<Long> query = em.createQuery(sql.toString(), Long.class);
			return query.getSingleResult();
		} catch (Exception e) {
			return 0L;
		}
	}

}
