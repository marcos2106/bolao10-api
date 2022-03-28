package br.com.segmedic.clubflex.scheduled;

import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.service.InvoiceService;
import br.com.segmedic.clubflex.service.TicketGatewayService;

@Component
public class ReviewStatusBoletoSimplesScheduled {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReviewStatusBoletoSimplesScheduled.class);
	private static final String TIME_ZONE = "America/Sao_Paulo";
	
	@Autowired
	private TicketGatewayService ticketGatewayService;
	
	@Autowired
	private InvoiceService invoiceService;
	
	//Toda segunda e quinta as 22:30 da noite.
	@Scheduled(cron = "0 30 22 * * MON-THU", zone = TIME_ZONE)
	public void execute() {
		try {
			List<BigInteger> invoices = invoiceService.listInvoiceWithoutPaymentAndDue();
			invoices.forEach(invoice->{
				Invoice invoiceObj = invoiceService.findById(invoice.longValue());
				ticketGatewayService.sincronizeTicketInfo(invoiceObj);
			});
		} catch (Exception e) {
			LOGGER.error("Erro na execução da schedule de verificacao de boletos em aberno no boleto simples", e);
		}
	}
	
}
