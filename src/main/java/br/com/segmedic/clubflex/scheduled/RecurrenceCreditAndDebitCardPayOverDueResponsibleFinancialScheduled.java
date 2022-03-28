
package br.com.segmedic.clubflex.scheduled;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.beust.jcommander.internal.Lists;
import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.repository.InvoiceRepository;
import br.com.segmedic.clubflex.service.InvoiceService;
import br.com.segmedic.clubflex.service.RedeItauGatewayService;

@Component
public class RecurrenceCreditAndDebitCardPayOverDueResponsibleFinancialScheduled {

   private static final Logger LOGGER = LoggerFactory.getLogger(RecurrenceCreditAndDebitCardPayOverDueResponsibleFinancialScheduled.class);

   @Autowired
   private InvoiceService invoiceService;

   @Autowired
   private RedeItauGatewayService redeItauService;

   @Value("${erede.gateway.api.sincronizar}")
   private Boolean sincronizar;

   private static final String TIME_ZONE = "America/Sao_Paulo";

   @Autowired
   private InvoiceRepository invoiceRepository;

   /**
    * Faz os pagamentos das faturas (Responsavel financeiro) de cartao de credito/debito que estão com até 90 dias de vencida.
    */

   @Scheduled(cron = "0 0 7,19 * * * ", zone = TIME_ZONE)
   public void execute10() {
      run();
   }

   public void run() {
      if (sincronizar) {
         try {
            LocalDate begin = LocalDate.now().minusDays(91);
            LocalDate end = LocalDate.now().minusDays(1);
            List<BigInteger> invoices = invoiceService.listInvoicesToRecurrenceReponsibleFinancial(begin, end, true);

            List<Long> processed = Lists.newArrayList();
            List<Long> errorTransactionprocessed = Lists.newArrayList();

            invoices.stream().sorted().forEach(invoiceId -> {
               try {
                  if (!processed.contains(invoiceId.longValue())) {

                     Invoice invoice = invoiceRepository.findById(invoiceId.longValue());

                     if (!errorTransactionprocessed.contains(invoice.getSubscription().getId())) {
                        if (redeItauService.payRecurrency(invoice))
                           errorTransactionprocessed.add(invoice.getSubscription().getId());
                        TimeUnit.SECONDS.sleep(10);
                     }
                     processed.add(invoiceId.longValue());
                  }
               }
               catch (Exception e) {
                  processed.add(invoiceId.longValue());
                  e.printStackTrace();
               }
            });
         }
         catch (Exception e) {
            LOGGER.error("Erro na execução da schedule de pagamento overdue (credit, debit) do dia", e);
         }
      }
   }
}
