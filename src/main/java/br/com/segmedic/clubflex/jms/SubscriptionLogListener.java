package br.com.segmedic.clubflex.jms;

import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import br.com.segmedic.clubflex.domain.SubscriptionLog;
import br.com.segmedic.clubflex.service.SubscriptionService;
import br.com.segmedic.clubflex.support.Constants;

@Component
public class SubscriptionLogListener {

	@Autowired
	private SubscriptionService subscriptionService;

	@JmsListener(destination = Constants.QUEUE_SUB_LOG)
	public void receiveMessage(Message<SubscriptionLog> message) throws JMSException {
		SubscriptionLog log = message.getPayload();
		subscriptionService.saveLog(log);
	}
}
