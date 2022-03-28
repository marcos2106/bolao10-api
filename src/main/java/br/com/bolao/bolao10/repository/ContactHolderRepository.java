package br.com.segmedic.clubflex.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.segmedic.clubflex.domain.ContactHolder;

@Repository
public class ContactHolderRepository extends GenericRepository{
	
	@Autowired
	private EntityManager em;

	public List<ContactHolder> listByHolderId(Long holderId) {
		String sql = "SELECT c FROM ContactHolder c WHERE c.holder.id = :holderId ORDER BY c.dateTimeContact DESC";
		return em.createQuery(sql, ContactHolder.class).setParameter("holderId", holderId).getResultList();
	}
	
}
