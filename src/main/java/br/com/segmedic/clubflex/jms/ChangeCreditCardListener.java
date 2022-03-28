
package br.com.segmedic.clubflex.jms;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import br.com.segmedic.clubflex.domain.CreditCard;
import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.domain.enums.InvoiceStatus;
import br.com.segmedic.clubflex.repository.InvoiceRepository;
import br.com.segmedic.clubflex.service.CreditCardService;
import br.com.segmedic.clubflex.service.RedeItauGatewayService;
import br.com.segmedic.clubflex.service.SubscriptionService;
import br.com.segmedic.clubflex.support.Constants;

/**
 * Listener responsavel por processar os pagamentos pendentes caso haja troca de cartão.
 */
@Component
public class ChangeCreditCardListener {

   private static final Logger LOGGER = LoggerFactory.getLogger(ChangeCreditCardListener.class);

   @Autowired
   private InvoiceRepository invoiceRepository;

   @Autowired
   private SubscriptionService subscriptionService;

   @Autowired
   private CreditCardService creditCardService;

   @Autowired
   private RedeItauGatewayService redeGatewayService;

   @JmsListener(destination = Constants.QUEUE_CHANGE_CREDCARD)
   public void receiveMessage(Message<Long> msg) throws JMSException {
      Subscription sub = subscriptionService.findById(msg.getPayload());

      // Efetua o pagamento de faturas pendentes e vencidas, após troca do cartão.
      List<Invoice> invoices = invoiceRepository.listInvoiceBySubscriptionIdAndStatus(msg.getPayload(),
         InvoiceStatus.OPENED);
      AtomicLong lastInvoice = new AtomicLong(0L);
      if (invoices != null && !invoices.isEmpty()) {
         for (Invoice invoice : invoices) {
            LOGGER.info(String.format("LOG CHANGE_CREDCARD / Fatura %s / SizeArray %s / Payload %s",
               invoice.getId(), invoices.size(), msg.getPayload()));
            if (invoice.isOutDate() && lastInvoice.get() != invoice.getId()) {
               try {
                  CreditCard creditCard = creditCardService.findByHolderId(sub.getHolder().getId());
                  creditCard.setHolder(sub.getHolder());
                  invoice.setCreditCard(creditCard);
                  invoice.setSubscription(sub);
                  invoice.setPaymentType(sub.getPaymentType());
                  redeGatewayService.pay(invoice, true);
                  TimeUnit.MILLISECONDS.sleep(200);
                  lastInvoice.set(invoice.getId());
               }
               catch (Exception e) {
                  LOGGER.error(e.getMessage());
               }
            }
         }
      }
   }
}
