package br.com.segmedic.clubflex.jms;

import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import br.com.segmedic.clubflex.domain.Audit;
import br.com.segmedic.clubflex.service.AuditService;
import br.com.segmedic.clubflex.support.Constants;

@Component
public class AuditListener {
	
	@Autowired
	private AuditService auditService;
	
	@JmsListener(destination = Constants.QUEUE_AUDIT)
	public void receiveMessage(Message<Audit> msg) throws JMSException {
		auditService.save(msg.getPayload());
	}
}
