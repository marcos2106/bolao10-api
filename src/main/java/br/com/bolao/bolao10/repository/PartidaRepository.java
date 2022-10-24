
package br.com.bolao.bolao10.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.Partida;

@Repository
public class PartidaRepository extends GenericRepository {

	@Autowired
	private EntityManager em;

	public Partida save(Partida partida) {
		if (partida.getId() == null) {
			super.persist(partida);
		}
		else {
			super.update(partida);
		}
		return partida;
	}

	public Partida findById(Long id) {
		return super.find(Partida.class, id);
	}
	
	public List<Partida> carregarPartidasConfiguracao() {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select p		            				");
		sql.append(" from Partida p	 							");
		sql.append(" order by p.finalizada, p.iniciada DESC, p.dataHora, p.fase 	");
		
		TypedQuery<Partida> query = em.createQuery(sql.toString(), Partida.class);
		
		try {
			return query.getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public List<Partida> carregarPartidasTabela() {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select p from Partida p order by p.dataHora ");
		
		TypedQuery<Partida> query = em.createQuery(sql.toString(), Partida.class);
		try {
			return query.getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public List<Partida> carregarPartidasAposta() {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select p from Partida p					");
		sql.append(" order by p.fase, p.selecaoA.grupo, p.dataHora	");
		
		TypedQuery<Partida> query = em.createQuery(sql.toString(), Partida.class);
		try {
			return query.getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

	public Partida carregarPartidaEstreia() {

		StringBuilder sql = new StringBuilder();
		sql.append(" select p from Partida p  where p.id = 3 ");
		
		TypedQuery<Partida> query = em.createQuery(sql.toString(), Partida.class);
		try {
			return query.getSingleResult();
		}
		catch (Exception e) {
			return null;
		}
	}

	public List<Partida> carregarProximasPartidas() {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select p from Partida p	");
		sql.append(" where p.finalizada = false	");
		sql.append(" order by p.dataHora	");
		
		TypedQuery<Partida> query = em.createQuery(sql.toString(), Partida.class);
		try {
			return query.setMaxResults(3).getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public List<Partida> carregarPartidasAnteriores() {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select p from Partida p	");
		sql.append(" where p.finalizada = true order by p.dataHora desc ");
		
		TypedQuery<Partida> query = em.createQuery(sql.toString(), Partida.class);
		try {
			return query.setMaxResults(3).getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

	public List<Partida> carregarPartidasPorSelecao(Long id) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select p from Partida p		");
		sql.append(" where p.selecaoA.id = :idIda	");
		sql.append(" or p.selecaoB.id = :idVolta	");
		sql.append(" order by p.dataHora	");
		
		TypedQuery<Partida> query = em.createQuery(sql.toString(), Partida.class);
		query.setParameter("idIda", id);
		query.setParameter("idVolta", id);
		try {
			return query.getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

	public Long quantidadeJogoAovivo() {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select count(p) as total from Partida p			");
		sql.append(" where p.iniciada = true and p.finalizada = false	");
		
		try {
			TypedQuery<Long> query = em.createQuery(sql.toString(), Long.class);
			return query.getSingleResult();
		}
		catch (Exception e) {
			return 0L;
		}
	}

	public Long obterQntdPartidasRealizadas() {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select count(p) as total from Partida p	");
		sql.append(" where p.finalizada = true					");
		
		try {
			TypedQuery<Long> query = em.createQuery(sql.toString(), Long.class);
			return query.getSingleResult();
		}
		catch (Exception e) {
			return 0L;
		}
	}

}
