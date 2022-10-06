
package br.com.bolao.bolao10.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.Gol;

@Repository
public class GolRepository extends GenericRepository {

	@Autowired
	private EntityManager em;

	public Gol save(Gol gol) {
		if (gol.getId() == null) {
			super.persist(gol);
		}
		else {
			super.update(gol);
		}
		return gol;
	}

	public Gol findById(Long id) {
		return super.find(Gol.class, id);
	}

	public List<Gol> carregarGolsPorPartida(Long idPartida) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select g		            		");
		sql.append(" from Gol g	 						");
		sql.append(" where g.partida.id = :idPartida	");
		sql.append(" order by g.minuto					");

		TypedQuery<Gol> query = em.createQuery(sql.toString(), Gol.class);
		query.setParameter("idPartida", idPartida);

		try {
			return query.getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

}
