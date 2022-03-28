
package br.com.segmedic.clubflex.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import br.com.segmedic.clubflex.domain.Reason;
import br.com.segmedic.clubflex.domain.SubscriptionLog;
import br.com.segmedic.clubflex.domain.enums.SubscriptionLogAction;
import br.com.segmedic.clubflex.repository.ReasonRepository;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class SubscriptionLogService {

   @Autowired
   private JmsTemplate queueSubscriptionLog;

   @Autowired
   private ReasonRepository reasonRepository;

   public void generateLog(Long userId, Long subscriptionId, SubscriptionLogAction action) {
      SubscriptionLog log = new SubscriptionLog();
      log.setUserId(userId);
      log.setSubscriptionId(subscriptionId);
      log.setAction(action);
      queueSubscriptionLog.convertAndSend(log);
   }

   public void generateLog(Long userId, Long subscriptionId, String obs, SubscriptionLogAction action) {
      SubscriptionLog log = new SubscriptionLog();
      log.setUserId(userId);
      log.setSubscriptionId(subscriptionId);
      log.setAction(action);
      log.setObs(obs);
      queueSubscriptionLog.convertAndSend(log);
   }

   public void generateLog(Long userId, Long subscriptionId, String obs, SubscriptionLogAction action, Long idReason) {
      SubscriptionLog log = new SubscriptionLog();
      log.setUserId(userId);
      log.setSubscriptionId(subscriptionId);
      log.setAction(action);
      log.setObs(obs);

      Reason reason = reasonRepository.findById(idReason);
      log.setReason(reason);

      queueSubscriptionLog.convertAndSend(log);
   }

   public void saveLog() {

   }
}
