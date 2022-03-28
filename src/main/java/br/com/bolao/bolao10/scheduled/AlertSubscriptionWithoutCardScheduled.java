package br.com.segmedic.clubflex.scheduled;

import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.service.HolderService;
import br.com.segmedic.clubflex.service.SubscriptionService;

@Component
public class AlertSubscriptionWithoutCardScheduled {

	private static final Logger LOGGER = LoggerFactory.getLogger(AlertSubscriptionWithoutCardScheduled.class);
	private static final String TIME_ZONE = "America/Sao_Paulo";

	@Autowired
	private SubscriptionService subscriptionService;
	
	@Autowired
	private HolderService holderService;
	
	/**
	 * Sinaliza as assinaturas que tem tipo de pagamento igual a cartao, porem nao o tem informado. 
	 */
	//roda Todos os dias as 10 da manhã
	@Scheduled(cron = "0 0 10 * * *", zone = TIME_ZONE)
	public void execute() {
		try {
			List<BigInteger> subscriptions = subscriptionService.ListSubscriptionIdWithoutCard();
			subscriptions.forEach(subId->{
				Subscription sub = new Subscription();
				sub = subscriptionService.findById(subId.longValue());
				if(sub != null) {
					if (sub.getHolder().getCellPhone() != null && !sub.getHolder().getCellPhone().equals("")) {
						holderService.sendRequestCardSMS(sub.getHolder().getName(), sub.getHolder().getCellPhone());
						holderService.sendRequestCardMail(sub.getHolder().getName(), sub.getHolder().getEmail());
					}
				}
			});
		} catch (Exception e) {
			LOGGER.error("Erro na execução da Scheduled de desbloqueio", e);
		}
	}
}
