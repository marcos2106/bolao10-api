/*
* Rankme Informática S.A.
* Criação : 13 de jul. de 2021
*/

package br.com.segmedic.clubflex.scheduled;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.service.InvoiceService;
import br.com.segmedic.clubflex.service.SubscriptionService;

/**
 * Rotina para geracao de boletos
 * 
 * @author Julho/2021: Roberto de Souza Rodrigues
 */
@Component
public class RenewTicketsScheduled {

   @Autowired
   private SubscriptionService sservice;

   @Autowired
   private InvoiceService iservice;

   private static final Logger LOGGER = LoggerFactory.getLogger(RenewTicketsScheduled.class);
   private static final String TIME_ZONE = "America/Sao_Paulo";

   // Todo dia 1 a 01:00 da madruga
   @Scheduled(cron = "0 0 1 1 * *", zone = TIME_ZONE)
   public void execute() {
      List<BigInteger> invoices = sservice.listRenewTickets();
      if (!invoices.isEmpty()) {
         for (BigInteger id : invoices) {
            Invoice invoice = iservice.findById(id.longValue());
            try {
               if (invoice.getSubscription().getPlan().getAutomaticRenovation()) {
                  LocalDate baseDate = invoice.getDueDate().plusMonths(1);
                  LocalDate baseDateBegin = baseDate.withDayOfMonth(1);
                  iservice.generateTickets12MonthsService(invoice.getSubscription(), baseDateBegin, baseDateBegin);
               }
            }
            catch (Exception exc) {
               LOGGER.error("Erro ao gerar boletos para a assinatura " + invoice.getSubscription().getId(), exc);
            }

         }
      }
   }
}
