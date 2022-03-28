
package br.com.segmedic.clubflex.jms;

import javax.jms.JMSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import br.com.segmedic.clubflex.domain.CreditCard;
import br.com.segmedic.clubflex.service.CreditCardService;
import br.com.segmedic.clubflex.support.Constants;

@Component
public class CreditCardListener {

   @Autowired
   private CreditCardService ccService;

   @JmsListener(destination = Constants.QUEUE_CREDIT_CARD_UPDATE)
   public void receiveMessage(Message<CreditCard> message) throws JMSException {
      CreditCard creditCard = message.getPayload();
      ccService.update(creditCard);
   }
}
