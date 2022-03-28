/*
* Rankme Informática S.A.
* Criação : 13 de jul. de 2021
*/

package br.com.segmedic.clubflex.scheduled;

import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.domain.enums.EmailType;
import br.com.segmedic.clubflex.service.MailService;
import br.com.segmedic.clubflex.service.SubscriptionService;
import br.com.segmedic.clubflex.support.Constants;
import br.com.segmedic.clubflex.support.email.MailTemplate;
import br.com.segmedic.clubflex.support.email.MailTemplateBuilder;

/**
 * Rotina para envio de boleto que pertencem a um carne
 * 
 * @author Julho/2021: Roberto de Souza Rodrigues
 */
@Component
public class AdviseTicketsMailScheduled {

   @Autowired
   private SubscriptionService sservice;

   @Autowired
   private MailService mailService;

   private static final Logger LOGGER = LoggerFactory.getLogger(AdviseTicketsMailScheduled.class);
   private static final String TIME_ZONE = "America/Sao_Paulo";

   // Todo dia 1 a 03:00 da madruga
   @Scheduled(cron = "0 0 3 1 * *", zone = TIME_ZONE)
   public void advise() {
      List<Invoice> invoices = sservice.listAdviseTickets();
      if (!invoices.isEmpty()) {

         for (Invoice invoice : invoices) {
            // envia boleto para pagamento
            try {
               MailTemplate mail = new MailTemplateBuilder()
                        .subject("Boleto para pagamento clubFlex")
                        .template("new-ticket-generated.html")
                        .addParam("nome", invoice.getSubscription().getHolder().getName())
                        .addParam("vencimento", invoice.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        .addParam("valor", invoice.getAmountFmt())
                        .addParam("link", invoice.getUrlTicket())
                        .addParam("linkdescricao", invoice.getUrlTicket())
                        .addParam("linhadigitavel", invoice.getBarcodeTicket())
                        .addParam(Constants.MAIL_SECTOR, EmailType.EMAIL_FINANCE.getDescribe())
                        .to(invoice.getSubscription().getHolder().getEmail())
                        .build();
               mailService.scheduleSend(mail);
            }
            catch (Exception e) {
               LOGGER.error("Erro ao enviar e-mail com boleto para pagamento", e);
            }
         }
      }
   }

   // Todo dia a 04:00 da madruga
   @Scheduled(cron = "0 0 4 * * *", zone = TIME_ZONE)
   public void adviseTodue() {
      List<Invoice> invoices = sservice.listAdviseTickets2DaysToDue();
      if (!invoices.isEmpty()) {
         for (Invoice invoice : invoices) {
            // envia boleto para pagamento
            try {
               MailTemplate mail = new MailTemplateBuilder()
                        .subject("Boleto vencendo clubFlex")
                        .template("overdue-ticket.html")
                        .addParam("nome", invoice.getSubscription().getHolder().getName())
                        .addParam("vencimento", invoice.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        .addParam("valor", invoice.getAmountFmt())
                        .addParam("link", invoice.getUrlTicket())
                        .addParam("linkdescricao", invoice.getUrlTicket())
                        .addParam("linhadigitavel", invoice.getBarcodeTicket())
                        .addParam(Constants.MAIL_SECTOR, EmailType.EMAIL_FINANCE.getDescribe())
                        .to(invoice.getSubscription().getHolder().getEmail())
                        .build();
               mailService.scheduleSend(mail);
            }
            catch (Exception e) {
               LOGGER.error("Erro ao enviar e-mail com boleto para pagamento", e);
            }
         }
      }
   }
}
