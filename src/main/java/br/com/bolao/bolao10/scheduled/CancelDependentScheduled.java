
package br.com.segmedic.clubflex.scheduled;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import br.com.segmedic.clubflex.domain.ClubCard;
import br.com.segmedic.clubflex.domain.Dependent;
import br.com.segmedic.clubflex.domain.enums.ClubCardStatus;
import br.com.segmedic.clubflex.domain.enums.DependentStatus;
import br.com.segmedic.clubflex.service.ClubFlexCardService;
import br.com.segmedic.clubflex.service.DependentService;

@Component
public class CancelDependentScheduled {

   private static final Logger LOGGER = LoggerFactory.getLogger(CancelDependentScheduled.class);
   private static final String TIME_ZONE = "America/Sao_Paulo";

   @Autowired
   private DependentService dependentService;

   @Autowired
   private ClubFlexCardService clubFlexCardService;

   /**
    * Cancela todas os dependentes que estão OK, mas com data de cancelamento pre informado
    */
   // roda 06:15 (6:15 da manha)
   @Scheduled(cron = "0 15 6 * * *", zone = TIME_ZONE)
   public void execute() {
      try {
         List<BigInteger> dependents = dependentService.listPreCancelled();
         dependents.forEach(depId -> {
            // cancelando dependente
            Dependent dependent = dependentService.getById(depId.longValue());
            dependent.setStatus(DependentStatus.REMOVED);
            dependentService.save(dependent);

            // cancelando cartao do dependente
            ClubCard clubcard = clubFlexCardService.getByDependentId(depId.longValue());
            clubcard.setStatus(ClubCardStatus.CANCELED);
            clubcard.setDateCancel(LocalDateTime.now());
            clubFlexCardService.save(clubcard);
         });
      }
      catch (Exception e) {
         LOGGER.error("Erro na execução da Scheduled de cancelamento de dependent pre canceladas", e);
      }
   }
}
