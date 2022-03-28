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
import br.com.segmedic.clubflex.service.SubscriptionService;

@Component
public class GeneratePreviewInvoiceMailScheduled {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GeneratePreviewInvoiceMailScheduled.class);
	private static final String TIME_ZONE = "America/Sao_Paulo";

	@Autowired
	private SubscriptionService subscriptionService;
	
	/**
	 * Dispara o e-mail de preview de fatura das assinaturas para todas (Pessoa Juridicas)
	 */
	//Todo dia 1 as 04:30 da madruga
	@Scheduled(cron = "0 30 4 1 * *", zone = TIME_ZONE)
	public void execute() {
		try {
			Integer monthCompetence = LocalDate.now().getMonthValue();
			Integer yearCompetence = LocalDate.now().getYear();
			Integer actualCompetence = Integer.valueOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")));
			//Pessoa Juridica
			List<BigInteger> subs = subscriptionService.listActiveSubscriptions(actualCompetence, TypeSub.PJ);
			subs.forEach(subId->{
				subscriptionService.sendPreviewInvoiceWithCC(subId, monthCompetence, yearCompetence);
			});

			//Pessoa Fisica
			subs = subscriptionService.listActiveSubscriptions(actualCompetence, TypeSub.PF);
			subs.forEach(subId->{
				subscriptionService.sendPreviewInvoiceWithoutCC(subId, monthCompetence, yearCompetence);
			});
			
		} catch (Exception e) {
			LOGGER.error("Erro na execução da schedule de geração de invoices", e);
		}
	}
	
}
