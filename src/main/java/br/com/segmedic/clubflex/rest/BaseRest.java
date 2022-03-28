
package br.com.segmedic.clubflex.rest;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import br.com.segmedic.clubflex.domain.User;
import br.com.segmedic.clubflex.domain.enums.SubscriptionLogAction;
import br.com.segmedic.clubflex.model.StandardResponse;
import br.com.segmedic.clubflex.service.JWTService;
import br.com.segmedic.clubflex.service.SubscriptionLogService;

@Component
public class BaseRest {

   @Autowired
   private JWTService jwtService;

   @Autowired
   private SubscriptionLogService subscriptionLogService;

   protected ResponseEntity<?> createObjectReturn(Object object) {
      return ResponseEntity.ok().body(new StandardResponse(HttpStatus.OK.value(), null, null, object));
   }

   protected User getUserToken(HttpServletRequest request) {
      return jwtService.readJwtToken(request);
   }

   protected void generateLog(Long userId, Long subscriptionId, SubscriptionLogAction action) {
      subscriptionLogService.generateLog(userId, subscriptionId, action);
   }

   protected void generateLog(Long userId, Long subscriptionId, String obs, SubscriptionLogAction action) {
      subscriptionLogService.generateLog(userId, subscriptionId, obs, action);
   }

   protected void generateLog(Long userId, Long subscriptionId, String obs, SubscriptionLogAction action, Long idreason) {
      subscriptionLogService.generateLog(userId, subscriptionId, obs, action, idreason);
   }

}
