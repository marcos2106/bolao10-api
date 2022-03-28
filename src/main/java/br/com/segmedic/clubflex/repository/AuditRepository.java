package br.com.segmedic.clubflex.repository;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.segmedic.clubflex.domain.Audit;

@Repository
public class AuditRepository {
	
	@Autowired
	private EntityManager em;
	
	public void save(Audit audit) {
		em.persist(audit);
	}

}
