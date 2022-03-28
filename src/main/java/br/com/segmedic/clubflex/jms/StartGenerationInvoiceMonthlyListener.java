package br.com.segmedic.clubflex.jms;

import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import br.com.segmedic.clubflex.service.InvoiceService;
import br.com.segmedic.clubflex.support.Constants;

@Component
public class StartGenerationInvoiceMonthlyListener {
	
	@Autowired
	private InvoiceService invoiceService;
	
	@JmsListener(destination = Constants.QUEUE_MONTHLY_INVOICE)
	public void receiveMessage(Message<Long> message) throws JMSException {
		Long subscriptionId = message.getPayload();
		invoiceService.generateMonthlyInvoice(subscriptionId);
	}
}
