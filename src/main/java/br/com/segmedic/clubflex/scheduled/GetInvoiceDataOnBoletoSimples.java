
package br.com.segmedic.clubflex.scheduled;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.domain.enums.InvoiceStatus;
import br.com.segmedic.clubflex.service.InvoiceService;
import br.com.segmedic.clubflex.service.TicketGatewayService;

@Component
public class GetInvoiceDataOnBoletoSimples {

   private static final String TIME_ZONE = "America/Sao_Paulo";

   @Value("${ticket.gateway.api.sincronizar}")
   private Boolean sincronizar;

   @Autowired
   private InvoiceService invoiceService;

   @Autowired
   private TicketGatewayService ticketGatewayService;

   @Scheduled(fixedRate = 60000, zone = TIME_ZONE)
   public void execute() {
      if (sincronizar) {
         List<Invoice> invoiceGenerating = invoiceService.listInvoiceByStatus(InvoiceStatus.GENERATING);
         invoiceGenerating.forEach(invoice -> {
            ticketGatewayService.sincronizeTicketInfo(invoice);
         });
      }
   }
}
