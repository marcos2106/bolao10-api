package br.com.segmedic.clubflex.scheduled;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.segmedic.clubflex.repository.DashboardRepository;

@Component
public class UpdateDebtStatisticsScheduled {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UnblockSubscriptionScheduled.class);
	private static final String TIME_ZONE = "America/Sao_Paulo";

	@Autowired
	private DashboardRepository dashboardRepository;
	
	/**
	 * Atualiza a quantidade de invoices inadimplentes para fins de estatistica e graficos
	 */
	//roda todos os dias as 23:55
	@Scheduled(cron = "* 55 23 * * *", zone = TIME_ZONE)
	@Transactional
	public void execute() {
		try {
			LocalDate begin = LocalDate.of(1900, 1, 1);
			LocalDate end = LocalDate.now();
			BigDecimal debitQuantity = dashboardRepository.getDebitQuantity(begin, end);
			if(debitQuantity != null) {
				dashboardRepository.saveDebtStatistics(end, debitQuantity.intValue());
			}
		} catch (Exception e) {
			LOGGER.error("Erro na execução da Scheduled ", e);
		}
	}
}
