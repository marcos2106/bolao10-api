
package br.com.bolao.bolao10.jms;

import javax.jms.JMSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import br.com.segmedic.clubflex.domain.enums.SubscriptionLogAction;
import br.com.segmedic.clubflex.model.AddDependentRequest;
import br.com.segmedic.clubflex.model.DependentType;
import br.com.segmedic.clubflex.rest.BaseRest;
import br.com.segmedic.clubflex.service.SubscriptionService;
import br.com.segmedic.clubflex.support.Constants;

@Component
public class RequestDependentListener extends BaseRest {

   @Autowired
   private SubscriptionService subscriptionService;

   @JmsListener(destination = Constants.QUEUE_REQUEST_DEPENDENT)
   public void receiveMessage(Message<AddDependentRequest> dependentsRequest) throws JMSException {
      AddDependentRequest dependenteRequest = dependentsRequest.getPayload();

      for (int i = 0; i < dependenteRequest.getDependents().size(); i++) {
         if (dependenteRequest.getDependents().get(i).getCpfHolder() == null) {
            dependenteRequest.getDependents().get(i).setType(DependentType.HOLDER);
         }
         else {
            dependenteRequest.getDependents().get(i).setType(DependentType.DEPENDENT);
         }
      }

      try {
         subscriptionService.addDependent(dependenteRequest.getUser(), dependenteRequest);
         for (int i = 0; i < dependenteRequest.getDependents().size(); i++) {
            super.generateLog(dependenteRequest.getUser().getId(), dependenteRequest.getSubscriptionId(),
               "Beneficiário " + dependenteRequest.getDependents().get(i).getName() + " incluído com sucesso.",
               SubscriptionLogAction.ADICIONADO_DEPENDENTES);
         }
      }
      catch (Exception exc) {

         super.generateLog(dependenteRequest.getUser().getId(), dependenteRequest.getSubscriptionId(),
            "ERRO: " + exc.getMessage(), SubscriptionLogAction.DEPENDENTE_NAO_CADASTRADO);
      }
   }
}
