
package br.com.segmedic.clubflex.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.security.RequireAuthentication;
import br.com.segmedic.clubflex.service.BrokerService;

@RestController
public class BrokerRest extends BaseRest {

   @Autowired
   private BrokerService brokerService;

   @GetMapping(value = "/broker", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> listBrokers() {
      return createObjectReturn(brokerService.listAll());
   }

}
