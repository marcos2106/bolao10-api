
package br.com.segmedic.clubflex.scheduled;

import java.math.BigInteger;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import br.com.segmedic.clubflex.service.SubscriptionService;

@Component
public class UnblockSubscriptionScheduled {

   private static final Logger LOGGER = LoggerFactory.getLogger(UnblockSubscriptionScheduled.class);
   // private static final String TIME_ZONE = "America/Sao_Paulo";

   @Autowired
   private SubscriptionService subscriptionService;

   /**
    * Desbloqueia todas as assinaturas bloqueadas, que estão com pagamento em dia (menos de 3 em aberto)
    */
   // roda a cada 30 minutos
   // @Scheduled(cron = "* 30 * * * *", zone = TIME_ZONE)
   public void execute() {
      try {
         List<BigInteger> subscriptions = subscriptionService.listNoShouldBeBlocked();
         subscriptions.forEach(subId -> {
            subscriptionService.unBlock(subId.longValue());
         });
      }
      catch (Exception e) {
         LOGGER.error("Erro na execução da Scheduled de desbloqueio", e);
      }
   }
}
