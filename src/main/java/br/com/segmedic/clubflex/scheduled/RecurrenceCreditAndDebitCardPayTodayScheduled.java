
package br.com.segmedic.clubflex.scheduled;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import br.com.segmedic.clubflex.service.InvoiceService;
import br.com.segmedic.clubflex.service.RedeItauGatewayService;

@Component
public class RecurrenceCreditAndDebitCardPayTodayScheduled {

   private static final Logger LOGGER = LoggerFactory.getLogger(RecurrenceCreditAndDebitCardPayTodayScheduled.class);
   private static final String TIME_ZONE = "America/Sao_Paulo";

   @Autowired
   private InvoiceService invoiceService;

   @Autowired
   private RedeItauGatewayService redeItauService;

   /**
    * Faz os pagamentos das faturas de cartao de credito/debito que estão vencendo hoje.
    */
   // Todo dia as 05:00 da manha
   @Scheduled(cron = "0 0 5 * * * ", zone = TIME_ZONE)
   public void execute() {
      try {
         LocalDate begin = LocalDate.now();
         LocalDate end = LocalDate.now();
         List<BigInteger> invoices = invoiceService.listInvoicesToRecurrence(begin, end);
         invoices.forEach(invoiceId -> {
            try {
               redeItauService.pay(invoiceId.longValue());
               TimeUnit.SECONDS.sleep(2);
            }
            catch (Exception e) {
               e.printStackTrace();
            }
         });
      }
      catch (Exception e) {
         LOGGER.error("Erro na execução da schedule de pagamento (credit, debit) do dia", e);
      }
   }

}
