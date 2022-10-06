
package br.com.bolao.bolao10.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.Selecao;

@Repository
public class SelecaoRepository extends GenericRepository {

	@Autowired
	private EntityManager em;


	public Selecao save(Selecao selecao) {
		if (selecao.getId() == null) {
			super.persist(selecao);
		}
		else {
			super.update(selecao);
		}
		return selecao;
	}

	public Selecao findById(Long id) {
		return (id == null ) ? null : super.find(Selecao.class, id);
	}

	public List<Selecao> carregarSelecoes() {

		StringBuilder sql = new StringBuilder();
		sql.append(" select sel               ");
		sql.append(" from Selecao sel 		");
		sql.append(" where sel.ativo = true	");
		sql.append(" order by sel.nome     	");

		TypedQuery<Selecao> query = em.createQuery(sql.toString(), Selecao.class);

		try {
			return query.getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

	public List<Selecao> carregarSelecoesOrderGrupo() {

		StringBuilder sql = new StringBuilder();
		sql.append(" select sel               ");
		sql.append(" from Selecao sel 		");
		sql.append(" where sel.ativo = true	");
		sql.append(" order by sel.grupo     	");

		TypedQuery<Selecao> query = em.createQuery(sql.toString(), Selecao.class);

		try {
			return query.getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

	public Selecao findByName(String nome) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select sel from Selecao sel 	");
		sql.append(" where sel.nome = :nome			");

		TypedQuery<Selecao> query = em.createQuery(sql.toString(), Selecao.class);
		query.setParameter("nome", nome);
		try {
			return query.getSingleResult();
		}
		catch (Exception e) {
			return null;
		}
	}
}
