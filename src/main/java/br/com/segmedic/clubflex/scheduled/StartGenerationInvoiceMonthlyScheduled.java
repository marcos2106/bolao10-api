
package br.com.segmedic.clubflex.scheduled;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import br.com.segmedic.clubflex.domain.enums.TypeSub;
import br.com.segmedic.clubflex.service.InvoiceService;
import br.com.segmedic.clubflex.service.SubscriptionService;

@Component
public class StartGenerationInvoiceMonthlyScheduled {

   private static final Logger LOGGER = LoggerFactory.getLogger(StartGenerationInvoiceMonthlyScheduled.class);
   private static final String TIME_ZONE = "America/Sao_Paulo";

   @Autowired
   private SubscriptionService subscriptionService;

   @Autowired
   private InvoiceService invoiceService;

   /**
    * Inicia a geracao das invoices mensais de cada assinatura ativa (Pessoa Fisica)
    */
   // Todo dia 1 as 02:00 da madruga
   @Scheduled(cron = "0 0 2 1 * *", zone = TIME_ZONE)
   public void execute() {
      try {
         Integer actualCompetence = Integer.valueOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")));
         List<BigInteger> subs = subscriptionService.listActiveAndBlocksSubscriptions(actualCompetence, TypeSub.PF);
         subs.forEach(subId -> {
            try {
               invoiceService.sendToGenerateMonthlyInvoice(subId);
            }
            catch (Exception exc) {
               LOGGER.error("Erro na execução da schedule de geração de invoices - ID: " + subId, exc);
            }
         });
      }
      catch (Exception e) {
         LOGGER.error("Erro na execução da schedule de geração de invoices", e);
      }
   }

   /**
    * Inicia a geracao das invoices mensais de cada assinatura ativa (Pessoa Juridica)
    */
   // Todo dia do mes as 02:00 da madruga
   @Scheduled(cron = "0 0 3 * * *", zone = TIME_ZONE)
   public void execute2() {
      try {
         Integer actualDayOfMounth = LocalDate.now().getDayOfMonth();
         Integer actualCompetence = Integer.valueOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")));
         List<BigInteger> subs = subscriptionService.listActiveSubscriptions(actualCompetence, actualDayOfMounth, TypeSub.PJ);
         subs.forEach(subId -> {
            invoiceService.sendToGenerateMonthlyInvoice(subId);
         });
      }
      catch (Exception e) {
         LOGGER.error("Erro na execução da schedule de geração de invoices", e);
      }
   }

}
