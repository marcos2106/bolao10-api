
package br.com.segmedic.clubflex.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import br.com.segmedic.clubflex.exception.ClubFlexException;
import br.com.segmedic.clubflex.support.Constants;
import br.com.segmedic.clubflex.support.email.MailTemplate;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class MailService {

   private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

   @Autowired
   private JmsTemplate queueMail;

   @Value("${smtp.host}")
   private String smtpHost;

   @Value("${smtp.user}")
   private String smtpUser;

   @Value("${smtp.passwd}")
   private String smtpPasswd;

   public void scheduleSend(MailTemplate mail) {
      queueMail.convertAndSend(mail);
   }

   public void send(MailTemplate mail) {
      try {
         if (mail.getTo() != null && !mail.getTo().equalsIgnoreCase(Constants.SEGMEDIC_MAIL)) {
            HtmlEmail email = new HtmlEmail();
            email.setHostName(smtpHost);
            email.setAuthenticator(new DefaultAuthenticator(smtpUser, smtpPasswd));
            email.setFrom("comunicacao@clubflex.com.br", "ClubFlex");
            email.addTo(mail.getTo());
            email.setDebug(false);
            email.setStartTLSEnabled(true);
            email.setSSLOnConnect(true);
            email.setSocketTimeout(4000);
            email.setCharset("UTF-8");
            email.setSubject(mail.getSubject());
            email.setHtmlMsg(mail.getContent());
            email.send();
         }
      }
      catch (Exception e) {
         LOGGER.error("Erro ao enviar e-mail. ", e);
         throw new ClubFlexException("Ocorreu um problema ao enviar e-mail. Tente novamente, se o problema persistir nos avise.", e);
      }
   }

}
