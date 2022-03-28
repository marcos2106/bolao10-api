package br.com.segmedic.clubflex.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.segmedic.clubflex.domain.Company;

@Repository
public class CompanyRepository extends GenericRepository{

	@Autowired
	private EntityManager em;
	
	public List<Company> listAll() {
		return em.createQuery("select c from Company c order by c.name", Company.class).getResultList();
	}
}
