
package br.com.bolao.bolao10.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.Jogador;

@Repository
public class JogadorRepository extends GenericRepository {

	@Autowired
	private EntityManager em;

	public Jogador save(Jogador jogador) {
		if (jogador.getId() == null) {
			super.persist(jogador);
		}
		else {
			super.update(jogador);
		}
		return jogador;
	}

	public Jogador findById(Long id) {
		return super.find(Jogador.class, id);
	}

	public List<Jogador> carregarJogadores() {

		StringBuilder sql = new StringBuilder();
		sql.append(" select j		            		");
		sql.append(" from Jogador j	 					");
		sql.append(" where j.ativo = true				");
		sql.append(" order by j.nome, j.selecao.nome	");

		TypedQuery<Jogador> query = em.createQuery(sql.toString(), Jogador.class);

		try {
			return query.getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

	public List<Jogador> carregarJogadoresPorSelecao(Long id) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select j		            		");
		sql.append(" from Jogador j	 					");
		sql.append(" where j.ativo = true				");
		sql.append(" and j.selecao.id = :id				");
		sql.append(" order by j.numero, j.nome			");

		TypedQuery<Jogador> query = em.createQuery(sql.toString(), Jogador.class);
		query.setParameter("id", id);

		try {
			return query.getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

	public List<Jogador> carregarArtilharia() {

		StringBuilder sql = new StringBuilder();
		sql.append(" select j		        ");
		sql.append(" from Jogador j	 		");
		sql.append(" where j.ativo = true	");
		sql.append(" order by j.gols desc	");

		TypedQuery<Jogador> query = em.createQuery(sql.toString(), Jogador.class);
		try {
			return query.getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

}
