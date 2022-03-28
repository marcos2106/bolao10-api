package br.com.segmedic.clubflex.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.segmedic.clubflex.domain.Broker;
import br.com.segmedic.clubflex.repository.BrokerRepository;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class BrokerService {
	
	@Autowired
	private BrokerRepository brokerRepository;

	public List<Broker> listAll() {
		return brokerRepository.listAll();
	}
	
	public Broker findById(Long id) {
		return brokerRepository.find(Broker.class, id);
	}
	
}
