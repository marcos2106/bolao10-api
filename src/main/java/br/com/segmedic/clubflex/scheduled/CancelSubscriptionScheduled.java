package br.com.segmedic.clubflex.scheduled;

import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.segmedic.clubflex.service.SubscriptionService;

@Component
public class CancelSubscriptionScheduled {

	private static final Logger LOGGER = LoggerFactory.getLogger(CancelSubscriptionScheduled.class);
	private static final String TIME_ZONE = "America/Sao_Paulo";

	@Autowired
	private SubscriptionService subscriptionService;
	
	/**
	 * Cancela todas as assinaturas que estão OK, mas com data de cancelamento pre informada (pre canceladas)
	 */
	//roda 0:30 da madruga
	@Scheduled(cron = "0 30 0 * * *", zone = TIME_ZONE)
	public void execute() {
		try {
			List<BigInteger> subscriptions = subscriptionService.listPreCancelled();
			subscriptions.forEach(subscriptionId->{
				subscriptionService.cancelPreCancelled(subscriptionId.longValue());
			});
		} catch (Exception e) {
			LOGGER.error("Erro na execução da Scheduled de cancelamento de assinatura pre canceladas", e);
		}
	}
}
