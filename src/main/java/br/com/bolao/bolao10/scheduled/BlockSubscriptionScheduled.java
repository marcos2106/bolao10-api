package br.com.bolao.bolao10.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.bolao.bolao10.service.SubscriptionService;

@Component
public class BlockSubscriptionScheduled {

	private static final Logger LOGGER = LoggerFactory.getLogger(BlockSubscriptionScheduled.class);
	private static final String TIME_ZONE = "America/Sao_Paulo";

	@Autowired
	private SubscriptionService subscriptionService;
	
	/**
	 * Bloqueia todas as assinaturas ok, que estão com 3 ou mais pagamentos em atraso
	 */
	//roda 1:15 da madruga
	@Scheduled(cron = "0 15 1 * * *", zone = TIME_ZONE)
	public void execute() {
		try {
//			List<BigInteger> subscriptions = subscriptionService.listShouldBeBlocked();
//			subscriptions.forEach(subId->{
//				//subscriptionService.block(subId.longValue());
//			});
		} catch (Exception e) {
			LOGGER.error("Erro na execução da Scheduled de bloqueio", e);
		}
	}
}
