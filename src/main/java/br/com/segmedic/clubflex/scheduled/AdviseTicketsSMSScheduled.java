/*
* Rankme Informática S.A.
* Criação : 13 de jul. de 2021
*/

package br.com.segmedic.clubflex.scheduled;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.service.SubscriptionService;
import br.com.segmedic.clubflex.support.sms.SMSSendBuilder;
import br.com.zenvia.client.request.MessageSmsElement;
import br.com.zenvia.client.request.MultipleMessageSms;

/**
 * Rotina para envio de boleto que pertencem a um carne
 * 
 * @author Julho/2021: Roberto de Souza Rodrigues
 */
@Component
public class AdviseTicketsSMSScheduled {

   @Autowired
   private SubscriptionService sservice;

   @Value("${zenvia.api.remetente}")
   private String remetente;

   @Value("${zenvia.api.username}")
   private String username;

   @Value("${zenvia.api.password}")
   private String password;

   private static final Logger LOGGER = LoggerFactory.getLogger(AdviseTicketsSMSScheduled.class);
   private static final String TIME_ZONE = "America/Sao_Paulo";

   // Todo dia 1 a 03:00 da madruga
   @Scheduled(cron = "0 0 9 1 * *", zone = TIME_ZONE)
   public void advise() {
      SMSSendBuilder smsBuilder = new SMSSendBuilder(username, password, 1000);
      List<Invoice> invoices = sservice.listAdviseTickets();

      String message =
         "Ola {primeiro_nome}, segue o boleto referente a sua assinatura clubflex - {link_boleto_pagamento}. Caso ja tenha pago, favor desconsiderar!";

      if (!invoices.isEmpty()) {
         executeSmsAdvise(smsBuilder, invoices, message, 1L, true);
      }
   }

   // Todo dia a 04:00 da madruga
   @Scheduled(cron = "0 0 7 * * *", zone = TIME_ZONE)
   public void adviseTodue() {
      SMSSendBuilder smsBuilder = new SMSSendBuilder(username, password, 1000);
      String message =
         "Ola {primeiro_nome}, Verificamos que existe um boleto vencendo - {link_boleto_pagamento}. Nao fique sem cobertura!";

      List<Invoice> invoices = sservice.listAdviseTickets2DaysToDue();
      if (!invoices.isEmpty()) {
         executeSmsAdvise(smsBuilder, invoices, message, 1L, true);
      }
   }

   public String parametrizaMensagem(String mensagem, Invoice invoice) {

      String mensagemParm = mensagem;

      mensagemParm = mensagemParm.replaceAll("\\{primeiro_nome\\}",
         invoice.getSubscription().getHolder().getName().split(" ")[0]);
      mensagemParm = mensagemParm.replaceAll("\\{link_boleto_pagamento\\}", invoice.getUrlTicket());

      return mensagemParm;
   }

   public void executeSmsAdvise(SMSSendBuilder smsBuilder, List<Invoice> invoices, String mensagem,
      Long quantidadeMensagens, boolean mensagemIlimitada) {

      MultipleMessageSms multipleMessageSms = new MultipleMessageSms();
      int contador = 0;
      int travaMensagens = 1;
      int qtdMensagens = (mensagemIlimitada ? 999999999 : quantidadeMensagens.intValue());
      if (invoices != null && !invoices.isEmpty()) {
         for (Invoice invoice : invoices) {

            if (invoice.getSubscription().getHolder().getCellPhone() != null) {
               if (travaMensagens <= qtdMensagens) {

                  travaMensagens++;
                  contador++;
                  MessageSmsElement messageSms = new MessageSmsElement();
                  messageSms.setId(ThreadLocalRandom.current().nextInt(1, 999999 + 1) + "");
                  messageSms.setFrom(remetente);
                  messageSms.setMsg(parametrizaMensagem(mensagem, invoice));
                  messageSms.setTo("55"
                           .concat(invoice.getSubscription().getHolder().getCellPhone().replaceAll("[^0-9]", "")));

                  multipleMessageSms.addMessageSms(messageSms);
                  if (contador > 99) {
                     try {
                        multipleMessageSms.setAggregateId(ThreadLocalRandom.current().nextInt(1, 999999 + 1));
                        smsBuilder.sendMultipleSms(multipleMessageSms);
                        multipleMessageSms = new MultipleMessageSms();
                        contador = 0;
                     }
                     catch (Exception e) {
                        LOGGER.error("Erro no envio de mensagem", e);
                     }
                  }
               }
               else {
                  break;
               }
            }
         }
         if (contador != 0) {
            try {
               multipleMessageSms.setAggregateId(ThreadLocalRandom.current().nextInt(1, 999999 + 1));
               smsBuilder.sendMultipleSms(multipleMessageSms);
            }
            catch (Exception e) {
               LOGGER.error("Erro no envio de mensagem", e);
            }
         }
      }
   }
}
