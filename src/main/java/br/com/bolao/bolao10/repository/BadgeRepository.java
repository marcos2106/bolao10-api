package br.com.bolao.bolao10.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.Badge;

@Repository
public class BadgeRepository extends GenericRepository {

	@Autowired
	private EntityManager em;

	public List<Badge> findAll() {
		try {
			return em.createQuery("select b from Badge b order by b.id", Badge.class).getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public Badge findById(Long id) {
		try {
			return super.find(Badge.class, id);
		} catch (Exception e) {
			return null;
		}
	}
}
