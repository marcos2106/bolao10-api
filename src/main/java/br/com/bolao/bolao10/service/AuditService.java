package br.com.segmedic.clubflex.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.segmedic.clubflex.domain.Audit;
import br.com.segmedic.clubflex.repository.AuditRepository;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class AuditService {
	
	@Autowired
	private AuditRepository auditRepository;
	
	@Autowired
	private JmsTemplate queueAudit;
	
	@Transactional
	public void save(Audit audit) {
		auditRepository.save(audit);
	}

	public void audit(Audit audit) {
		queueAudit.convertAndSend(audit);
	}
		
}
