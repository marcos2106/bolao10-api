
package br.com.segmedic.clubflex.jms;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.jms.JMSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import br.com.segmedic.clubflex.domain.Dependent;
import br.com.segmedic.clubflex.model.AddDependentRequest;
import br.com.segmedic.clubflex.support.Constants;

@Component
public class RequestDependentsListener {

   @Autowired
   private JmsTemplate queueRequestDependent;

   @JmsListener(destination = Constants.QUEUE_REQUEST_DEPENDENTS)
   public void receiveMessage(Message<AddDependentRequest> dependentsRequest) throws JMSException, InterruptedException {
      AddDependentRequest dependenteRequest = dependentsRequest.getPayload();

      TimeUnit.SECONDS.sleep(3);

      List<Dependent> holder = new ArrayList<Dependent>();
      List<Dependent> dependents = new ArrayList<Dependent>();

      holder = dependenteRequest.getDependents().stream()
               .filter(p -> p.getCpfHolder() == null)
               .collect(Collectors.toList());

      dependents = dependenteRequest.getDependents().stream()
               .filter(p -> p.getCpfHolder() != null)
               .collect(Collectors.toList());

      for (Dependent dependent : holder) {
         List<Dependent> listDependents = new ArrayList<Dependent>();
         listDependents.add(dependent);
         dependenteRequest.setDependents(listDependents);
         queueRequestDependent.convertAndSend(dependenteRequest);
      }

      for (Dependent dependent : dependents) {
         List<Dependent> listDependents = new ArrayList<Dependent>();
         listDependents.add(dependent);
         dependenteRequest.setDependents(listDependents);
         queueRequestDependent.convertAndSend(dependenteRequest);
      }
   }
}
