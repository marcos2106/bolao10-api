package br.com.segmedic.clubflex.jms;

import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import br.com.segmedic.clubflex.service.TicketGatewayService;
import br.com.segmedic.clubflex.support.Constants;

@Component
public class TicketProcessorListener {

	@Autowired
	private TicketGatewayService ticketGatewayService;

	@JmsListener(destination = Constants.QUEUE_INVOICE, concurrency="1-1")
	public void receiveMessage(Message<Long> msg) throws JMSException, InterruptedException {
		ticketGatewayService.registerTicket(msg.getPayload(), 1);
	}
	
}
