package br.com.segmedic.clubflex.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.segmedic.clubflex.domain.Ratification;
import br.com.segmedic.clubflex.domain.User;

@Repository
public class RatificationRepository extends GenericRepository {
	
	@Autowired
	private EntityManager em;
	
	public Ratification findById(Long id) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select r ");
		sql.append(" from Ratification r ");
		sql.append(" where r.id = :id");
		
		TypedQuery<Ratification> query = em.createQuery(sql.toString(), Ratification.class);
		query.setParameter("id", id);
		
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
		
		
	}
	
	public List<Ratification> listRatification() {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select r ");
		sql.append(" from Ratification r ");
		sql.append(" where r.isPending = :pending");
		sql.append(" order by r.updatedAt ");
		
		TypedQuery<Ratification> query = em.createQuery(sql.toString(), Ratification.class);
		query.setParameter("pending", Boolean.TRUE);
		
		try {
			return query.getResultList();
		} catch (Exception e) {
			return null;
		}
	}
	
	public List<Ratification> listRatificationByUser(User user) {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select r ");
		sql.append(" from Ratification r ");
		sql.append(" where r.user.id = :user");
		sql.append(" order by r.isPending DESC, r.updatedAt ");
		
		TypedQuery<Ratification> query = em.createQuery(sql.toString(), Ratification.class);
		query.setParameter("user", user.getId());
		
		try {
			return query.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public Ratification save(Ratification ratification) {
		if(ratification.getId() == null) {
			super.persist(ratification);
		}else {
			super.update(ratification);
		}
		return ratification;
	}

	public String getJustificationRatificaion(Long idInvoice) {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select r ");
		sql.append(" from Ratification r ");
		sql.append(" where r.invoice.id = :idInvoice ");
		sql.append(" and r.isPending = false ");
		sql.append(" and r.isApproved = false ");
		
		TypedQuery<Ratification> query = em.createQuery(sql.toString(), Ratification.class);
		query.setParameter("idInvoice", idInvoice);
		
		try {
			return query.getSingleResult().getJustification();
		} catch (Exception e) {
			return null;
		}
	}
	
}
