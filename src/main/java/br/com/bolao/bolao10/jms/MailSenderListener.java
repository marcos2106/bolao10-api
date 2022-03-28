
package br.com.segmedic.clubflex.jms;

import javax.jms.JMSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import br.com.segmedic.clubflex.domain.enums.EmailType;
import br.com.segmedic.clubflex.service.MailService;
import br.com.segmedic.clubflex.support.Constants;
import br.com.segmedic.clubflex.support.email.MailTemplate;

@Component
public class MailSenderListener {

   @Autowired
   private MailService mailService;

   @JmsListener(destination = Constants.QUEUE_MAIL)
   public void receiveMessage(Message<MailTemplate> message) throws JMSException {
      MailTemplate mail = message.getPayload();
      String setor = mail.getParams().get(Constants.MAIL_SECTOR);
      if (setor == null)
         setor = EmailType.EMAIL_GERAL.getDescribe();
      mailService.send(mail);
   }

}
