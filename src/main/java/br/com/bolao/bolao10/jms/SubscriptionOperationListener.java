package br.com.segmedic.clubflex.jms;

import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import br.com.segmedic.clubflex.model.Operation;
import br.com.segmedic.clubflex.model.SubscriptionOperation;
import br.com.segmedic.clubflex.service.SubscriptionService;
import br.com.segmedic.clubflex.support.Constants;

@Component
public class SubscriptionOperationListener {

	@Autowired
	private SubscriptionService subscriptionService;

	@JmsListener(destination = Constants.QUEUE_SUB_OPERATION)
	public void receiveMessage(Message<SubscriptionOperation> message) throws JMSException {
		SubscriptionOperation subop = message.getPayload();
		
		if(Operation.BLOCK.equals(subop.getOperation())) {
			subscriptionService.block(subop.getSubscriptionId());
			
		}else if(Operation.UNBLOCK.equals(subop.getOperation())) {
			subscriptionService.unBlock(subop.getSubscriptionId());
		}
	}
}
