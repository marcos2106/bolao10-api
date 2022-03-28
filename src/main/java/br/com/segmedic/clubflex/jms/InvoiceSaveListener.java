
package br.com.segmedic.clubflex.jms;

import javax.jms.JMSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.service.InvoiceService;
import br.com.segmedic.clubflex.support.Constants;

@Component
public class InvoiceSaveListener {

   @Autowired
   InvoiceService invoiceService;

   @JmsListener(destination = Constants.QUEUE_PAY_SINGLE_INVOICE)
   @Transactional
   public void receiveMessage(Message<Long> message) throws JMSException {
      Long idInvoice = message.getPayload();
      Invoice invoice = invoiceService.findById(idInvoice);
      invoiceService.paySingleInvoiceCCard(invoice);
   }
}
