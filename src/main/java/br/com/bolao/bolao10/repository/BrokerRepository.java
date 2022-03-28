package br.com.segmedic.clubflex.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.segmedic.clubflex.domain.Broker;

@Repository
public class BrokerRepository extends GenericRepository{

	@Autowired
	private EntityManager em;
	
	public List<Broker> listAll() {
		return em.createQuery("select b from Broker b order by b.name", Broker.class).getResultList();
	}
}
